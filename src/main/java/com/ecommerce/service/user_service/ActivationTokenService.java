package com.ecommerce.service.user_service;

import com.ecommerce.exception.user.UserNotFoundException;
import com.ecommerce.models.user.Token;
import com.ecommerce.models.user.User;
import com.ecommerce.repository.user_repos.TokenRepository;
import com.ecommerce.repository.user_repos.UserRepository;
import com.ecommerce.service.email_service.EmailService;
import com.ecommerce.utils.jwt_utils.JwtUtil;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ActivationTokenService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TokenService tokenService;

    public String activateUser(String token) throws MessagingException {
        Map<String,Object> claims = JwtUtil.extractAllClaims(token);
        String email = JwtUtil.extractEmail(token);
        String type = (String) claims.get("type");

        System.out.println(email+" "+type);

        if(!type.equals("activation")) {
            throw new IllegalArgumentException("Invalid token ");
        }

        System.out.println(email+" "+type);

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            emailService.sendEmail("ininsde15@gmail.com", "Account Activation Failed",
                    "You are not registered user. Please register again.");
            return "You are not registered user with this email. Please register again.";
        }

        if (user.getIsActive()) {
            emailService.sendEmail("ininsde15@gmail.com", "Account Already Activated",
                    "Your account is already activated. You can login.");
            return "Your account is already activated. You can login.";
        }

        Token userToken = user.getToken();
        Long activationIssuedAt = userToken.getActivation();
        System.out.println(activationIssuedAt+" "+JwtUtil.extractIssuedAt(token));

        if (!activationIssuedAt.equals(JwtUtil.extractIssuedAt(token))){
            throw new IllegalArgumentException("Invalid Expired token ");
        }

        if (!JwtUtil.isTokenValid(token)) {
            tokenService.saveActivationToken(user, "Account Activation Failed");
            return "Your account activation link is either expired. Please register again.";
        }

        user.setIsActive(true);
        userToken.setActivation(null);
        tokenRepository.save(userToken);
        userRepository.save(user);

        emailService.sendEmail("ininsde15@gmail.com", "Account Activated",
                "Your account has been activated successfully. Now you can login.");

        return "User Activated Successfully";
    }

    public String resendActivationLink(String email) throws MessagingException {
        System.out.println(email);
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        if (user.getIsActive()) {
            return "Your account is already activated. You can login.";
        }
        tokenService.saveActivationToken(user, "Account Activation Re-Sent ");
        return "Activation link sent to your email";
    }


}
