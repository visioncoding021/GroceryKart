package com.ecommerce.service.user_service;

import com.ecommerce.dto.response_dto.ErrorResponse;
import com.ecommerce.dto.response_dto.LoginTokenDTO;
import com.ecommerce.exception.user.UserIsInactiveException;
import com.ecommerce.exception.user.UserIsLockedException;
import com.ecommerce.exception.user.UserNotFoundException;
import com.ecommerce.models.user.User;
import com.ecommerce.repository.user_repos.UserRepository;
import com.ecommerce.utils.jwt_utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
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

    public String loginUser(String email, String password, HttpServletResponse response) {

        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

        if (user.isLocked()) {
            throw new UserIsLockedException();
        }

        if (!user.isActive()) {
            throw new UserIsInactiveException();
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            user.setInvalidAttemptCount(user.getInvalidAttemptCount() + 1);

            if (user.getInvalidAttemptCount() >= 3) {
                user.setLocked(true);
            }

            userRepository.save(user);
            throw new BadCredentialsException("Invalid credentials");
        }

        user.setInvalidAttemptCount(0);
        userRepository.save(user);

        String accessToken = JwtUtil.generateToken(user, "login",900000);
        String refreshToken = JwtUtil.generateToken(user, "login",86400000);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
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
}
