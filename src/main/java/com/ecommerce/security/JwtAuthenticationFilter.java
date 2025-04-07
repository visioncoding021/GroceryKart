package com.ecommerce.security;

import com.ecommerce.exception.user.UserNotFoundException;
import com.ecommerce.models.user.Token;
import com.ecommerce.models.user.User;
import com.ecommerce.repository.user_repos.UserRepository;
import com.ecommerce.utils.jwt_utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImp userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String jwt = resolveToken(request);

        if (jwt != null) {
            String email = JwtUtil.extractEmail(jwt);
            User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
            Token userToken = user.getToken();
            Long activationIssuedAt = userToken.getActivation();
            if (!activationIssuedAt.equals(JwtUtil.extractIssuedAt(jwt)) && !Objects.equals(JwtUtil.extractType(jwt), "access")) {
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

        filterChain.doFilter(request, response);
    }


    private String resolveToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            return authHeader.substring(7);
        }
        return null;
    }


}
