package com.ecommerce.security;

import com.ecommerce.exception.user.UserIsInactiveException;
import com.ecommerce.exception.user.UserNotFoundException;
import com.ecommerce.models.user.Token;
import com.ecommerce.models.user.User;
import com.ecommerce.repository.user_repos.TokenRepository;
import com.ecommerce.repository.user_repos.UserRepository;
import com.ecommerce.utils.jwt_utils.JwtUtil;
import com.ecommerce.utils.service_utils.UserUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImp userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        List<String> excludedPaths = List.of(
                "/api/auth/get-access-token",
                "/api/auth/login",
                "/api/auth/register",
                "/api/auth/forgot-password",
                "/api/auth/reset-password",
                "/api/auth/activate",
                "/api/auth/resend-activation-token"
        );
        return excludedPaths.contains(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        logger.info("Entering JwtAuthenticationFilter");
        String jwt = resolveToken(request);

        if (jwt != null) {
            String email = JwtUtil.extractEmail(jwt);
            logger.debug("JWT token detected for email: {}", email);
            User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

            if(user.getIsLocked() || user.getIsDeleted() || user.getIsExpired()){
                logger.warn("Blocked user tried to authenticate: {}", email);
                throw new UserNotFoundException("User is locked or deleted or expired");
            }
            if(!user.getIsActive()){
                logger.warn("Inactive user tried to authenticate: {}", email);
                throw new UserIsInactiveException("User is inactive");
            }

            Token userToken = user.getToken();
            Long accessIssuedAt = userToken.getAccess();
            if (accessIssuedAt == null) {
                logger.warn("No access issue time found for user: {}", email);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No Login Session found");
            }
            else {
                if ( !accessIssuedAt.equals(JwtUtil.extractIssuedAt(jwt)) || !Objects.equals(JwtUtil.extractType(jwt), "access")) {
                    logger.error("Token issuedAt or type mismatch for user: {}", email);
                    throw new IllegalArgumentException("Invalid token");
                }
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    if (JwtUtil.isTokenValid(jwt)) {
                        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                                userDetails.getUsername(), userDetails.getPassword(),userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authRequest);
                        logger.info("Authentication successful for user: {}", email);
                    }
                }

            }

        }
        logger.info("Exiting JwtAuthenticationFilter");
        filterChain.doFilter(request, response);
    }


    private String resolveToken(HttpServletRequest request) throws BadRequestException {
        String cookie = request.getHeader("Cookie");
        if (cookie != null && cookie.startsWith("refresh=")) {
            cookie = cookie.replace("refresh=", "");
            cookie = cookie.replace(";", "");
            if(cookie.isEmpty()) {
                logger.warn("No refresh token found in cookies");
                throw new BadRequestException("No refresh token found in cookies");
            }
            User user = userRepository.findByEmail(JwtUtil.extractEmail(cookie)).orElseThrow(UserNotFoundException::new);
            Token userToken = user.getToken();
            if (userToken.getRefresh() == null) {
                logger.warn("No refresh issue time found for user: {}", user.getEmail());
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No Login Session found");
            }
            if(UserUtils.is24HoursExpired(userToken.getRefresh())){
                userToken.setRefresh(null);
                userToken.setAccess(null);
                tokenRepository.save(userToken);
                return null;
            }
            Long refreshIssuedAt = userToken.getRefresh();
            if(!JwtUtil.isTokenValid(cookie) && !JwtUtil.extractType(cookie).equals("refresh") && !refreshIssuedAt.equals(JwtUtil.extractIssuedAt(cookie))) {
                return null;
            }
        }else return null;

        String authHeader = request.getHeader("Authorization");
        return JwtUtil.getAccessTokenFromHeader(authHeader);
    }


}
