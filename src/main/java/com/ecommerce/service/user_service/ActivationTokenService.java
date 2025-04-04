package com.ecommerce.service.user_service;

import com.ecommerce.models.user.User;
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

    public String activateUser(String token) throws MessagingException {
        Map<String,Object> claims = JwtUtil.extractAllClaims(token);
        String email = JwtUtil.extractEmail(token);
        String type = (String) claims.get("type");

        System.out.println(email+" "+type);

        if(!type.equals("activationToken")) {
            throw new IllegalArgumentException("Invalid token ");
        }

        System.out.println(email+" "+type);

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            emailService.sendEmail("ininsde15@gmail.com", "Account Activation Failed",
                    "You are not registered user. Please register again.");
            return "You are not registered user with this email. Please register again.";
        }

        if (user.isActive()) {
            emailService.sendEmail("ininsde15@gmail.com", "Account Already Activated",
                    "Your account is already activated. You can login.");
            return "Your account is already activated. You can login.";
        }


        if (!JwtUtil.isTokenValid(token)) {
            String activationToken = JwtUtil.generateToken(user,"activationToken",10800);
            emailService.sendActivationEmail("ininsde15@gmail.com", "Account Activation Failed",
                    "Your account activation link is either expired. Please register again.",activationToken);
            return "Your account activation link is either expired. Please register again.";
        }

        user.setActive(true);
        userRepository.save(user);

        emailService.sendEmail("ininsde15@gmail.com", "Account Activated",
                "Your account has been activated successfully. Now you can login.");

        return "User Activated Succesfully";
    }


}
