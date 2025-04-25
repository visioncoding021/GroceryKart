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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

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
                "/api/auth/register"
        );
        return excludedPaths.contains(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        System.out.println("First Goes To filter");
        String jwt = resolveToken(request);

        if (jwt != null) {
            String email = JwtUtil.extractEmail(jwt);
            User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

            if(user.getIsLocked() || user.getIsDeleted() || user.getIsExpired()){
                throw new UserNotFoundException("User is locked or deleted or expired");
            }
            if(!user.getIsActive()){
                throw new UserIsInactiveException("User is inactive");
            }

            Token userToken = user.getToken();
            Long accessIssuedAt = userToken.getAccess();
            if(accessIssuedAt!=null){
                if ( !accessIssuedAt.equals(JwtUtil.extractIssuedAt(jwt)) || !Objects.equals(JwtUtil.extractType(jwt), "access")) {
                    throw new IllegalArgumentException("Invalid token");
                }
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    if (JwtUtil.isTokenValid(jwt)) {
                        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                                userDetails.getUsername(), userDetails.getPassword(),userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authRequest);
                    }
                }

            }

        }
        System.out.println("Filter Ends Here");
        filterChain.doFilter(request, response);
    }


    private String resolveToken(HttpServletRequest request) {
        String cookie = request.getHeader("Cookie");
        if (cookie != null && cookie.startsWith("refresh=")) {
            cookie = cookie.replace("refresh=", "");
            cookie = cookie.replace(";", "");
            User user = userRepository.findByEmail(JwtUtil.extractEmail(cookie)).orElseThrow(UserNotFoundException::new);
            Token userToken = user.getToken();
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
