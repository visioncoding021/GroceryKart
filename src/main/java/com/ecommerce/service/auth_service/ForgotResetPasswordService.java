package com.ecommerce.service.auth_service;

import com.ecommerce.exception.user.UserIsLockedException;
import com.ecommerce.exception.user.UserNotFoundException;
import com.ecommerce.models.user.Token;
import com.ecommerce.models.user.User;
import com.ecommerce.repository.user_repos.TokenRepository;
import com.ecommerce.repository.user_repos.UserRepository;
import com.ecommerce.service.email_service.EmailService;
import com.ecommerce.utils.jwt_utils.JwtUtil;
import com.ecommerce.utils.service_utils.UserUtils;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Service
public class ForgotResetPasswordService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenRepository tokenRepository;

    public String sendResetPasswordEmail(String email) throws MessagingException {

        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

        if (user.getIsLocked()){
            throw new UserIsLockedException("You can't change password");
        }

        String resetToken = JwtUtil.generateToken(user,"resetPassword",900000);
        Token userToken = user.getToken();
        userToken.setResetPassword(JwtUtil.extractIssuedAt(resetToken));
        tokenRepository.save(userToken);
        emailService.sendResetPasswordEmail("ininsde15@gmail.com", "Reset Password Request Sent",
                    "Please reset your password by clicking the link below.", resetToken);

        return "Reset password request sent successfully . Please check your mail for token ";
    }

    public String resetPassword(String token, String password , String confirmPassword) throws UserNotFoundException {

        if(!JwtUtil.isTokenValid(token))
            throw new IllegalArgumentException("Invalid token");

        Map<String,Object> claims = JwtUtil.extractAllClaims(token);
        String email = JwtUtil.extractEmail(token);
        String type = (String) claims.get("type");
        Long tokenIssuedAt = JwtUtil.extractIssuedAt(token);

        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        Token userToken = user.getToken();
        if(!Objects.equals(type, "resetPassword") && !userToken.getResetPassword().equals(tokenIssuedAt))
            throw new IllegalArgumentException("Invalid token type");

        if (!UserUtils.isPasswordMatching(password, confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        UserUtils.setPasswordEncoder(user);
        user.setPasswordUpdateDate(LocalDateTime.now());
        userRepository.save(user);
        userToken.setResetPassword(null);
        tokenRepository.save(userToken);
        return "Password reset successfully";
    }
}
