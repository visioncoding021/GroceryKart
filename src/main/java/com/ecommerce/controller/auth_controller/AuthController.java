package com.ecommerce.controller.auth_controller;

import com.ecommerce.dto.request_dto.AuthRequestDTO;
import com.ecommerce.dto.response_dto.LoginTokenDTO;
import com.ecommerce.models.user.User;
import com.ecommerce.repository.user_repos.UserRepository;
import com.ecommerce.utils.jwt_utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.ecommerce.dto.response_dto.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoginTokenDTO loginTokenDTO;

    private Map<String, Object> claims ;



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {
        List<String> errors = new ArrayList<>();

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401).body(new ErrorResponse("Login failed", List.of("Email does not exist.")));
        }

        User user = userOptional.get();

        if (user.isLocked()) {
            return ResponseEntity.status(403).body(new ErrorResponse("Account locked", List.of("Your account is locked due to multiple failed login attempts.")));
        }

        // Check if account is activated
        if (!user.isActive()) {
            return ResponseEntity.status(403).body(new ErrorResponse("Account inactive", List.of("Your account has not been activated.")));
        }

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            user.setInvalidAttemptCount(user.getInvalidAttemptCount() + 1);

            if (user.getInvalidAttemptCount() >= 3) {
                user.setLocked(true);
            }

            userRepository.save(user);
            return ResponseEntity.status(401).body(new ErrorResponse("Login failed", List.of("Invalid credentials.")));
        }

        // Reset failed attempts on successful login
        user.setInvalidAttemptCount(0);
        userRepository.save(user);

        String accessToken = JwtUtil.generateToken(user, "login",900);
        String refereshToken = JwtUtil.generateToken(user, "login",86400);

        loginTokenDTO.setAccessToken(accessToken);
        loginTokenDTO.setRefreshToken(refereshToken);

        return ResponseEntity.ok(loginTokenDTO);
    }
}

