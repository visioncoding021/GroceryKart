package com.ecommerce.service.user_service;

import com.ecommerce.dto.response_dto.LoginTokenDTO;
import com.ecommerce.exception.user.UserIsInactiveException;
import com.ecommerce.exception.user.UserIsLockedException;
import com.ecommerce.exception.user.UserNotFoundException;
import com.ecommerce.models.user.Token;
import com.ecommerce.models.user.User;
import com.ecommerce.repository.user_repos.UserRepository;
import com.ecommerce.utils.jwt_utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class LoginLogoutService {

    @Autowired
    private LoginTokenDTO loginTokenDTO;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public String loginUser(String email, String password, HttpServletResponse response) {

        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

        if (user.getIsLocked()) {
            throw new UserIsLockedException();
        }

        if (!user.getIsActive()) {
            throw new UserIsInactiveException();
        }

        if (user.getInvalidAttemptCount() >= 3) {
            throw new UserIsLockedException();
        }

        if(response.getHeader("Cookie") != null) {
            String cookie = response.getHeader("Cookie");
            if(cookie.contains("refresh")) {
                throw new BadCredentialsException("Invalid credentials");
            }
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            user.setInvalidAttemptCount(user.getInvalidAttemptCount() + 1);

            if (user.getInvalidAttemptCount() >= 3) {
                user.setIsLocked(true);
            }

            userRepository.save(user);
            throw new BadCredentialsException("Invalid credentials");
        }

        user.setInvalidAttemptCount(0);
        userRepository.save(user);

        UsernamePasswordAuthenticationToken authCredential = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        authenticationManager.authenticate(authCredential);

        String accessToken = JwtUtil.generateToken(user, "access",900000);
        String refreshToken = JwtUtil.generateToken(user, "refresh",86400000);

        Token userToken = user.getToken();
        userToken.setAccess(JwtUtil.extractIssuedAt(accessToken));
        userToken.setRefresh(JwtUtil.extractIssuedAt(refreshToken));
        userRepository.save(user);

        ResponseCookie refreshCookie = ResponseCookie.from("refresh", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return "User Login Successful";
    }

    public String logoutUser(HttpServletResponse response) {
        String cookie = response.getHeader("Cookie");
        String accessToken = response.getHeader("Authorization");

        if(isValidAccessToken(accessToken)) response.setHeader(HttpHeaders.AUTHORIZATION, null);
        if(isValidRefreshToken(cookie)) response.addHeader(HttpHeaders.SET_COOKIE, null);

        User user = userRepository.findByEmail(JwtUtil.extractEmail(accessToken)).orElseThrow(UserNotFoundException::new);
        Token userToken = user.getToken();
        userToken.setAccess(null);
        userToken.setRefresh(null);

        return "User Logout Successful";
    }

    private boolean isValidAccessToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authHeader = authHeader.substring(7);
            if (!JwtUtil.isTokenValid(authHeader) && !JwtUtil.extractType(authHeader).equals("access")) {
                throw new BadCredentialsException("Invalid Access TOken");
            }
            User user = userRepository.findByEmail(JwtUtil.extractEmail(authHeader)).orElseThrow(UserNotFoundException::new);
            Token userToken = user.getToken();
            Long accessIssuedAt = userToken.getRefresh();
            if (!accessIssuedAt.equals(JwtUtil.extractIssuedAt(authHeader))) {
                throw new IllegalArgumentException("Invalid Access token");
            }
            return true;
        }else throw new IllegalArgumentException("Invalid Access token");
    }


    private boolean isValidRefreshToken(String cookie) {
        if (cookie != null && cookie.startsWith("refresh=")) {
            cookie = cookie.replace("refresh=", "");
            cookie = cookie.replace(";", "");

            if (!JwtUtil.isTokenValid(cookie) && !JwtUtil.extractType(cookie).equals("refresh")) {
                throw new BadCredentialsException("Invalid Refresh Token");
            }
            User user = userRepository.findByEmail(JwtUtil.extractEmail(cookie)).orElseThrow(UserNotFoundException::new);
            Token userToken = user.getToken();
            Long refreshIssuedAt = userToken.getRefresh();
            if (!refreshIssuedAt.equals(JwtUtil.extractIssuedAt(cookie))) {
                throw new IllegalArgumentException("Invalid refresh token");
            }
            return true;
        }else throw new IllegalArgumentException("Invalid Access token");
    }
}
