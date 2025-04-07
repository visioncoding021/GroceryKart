package com.ecommerce.service.user_service;

import com.ecommerce.models.user.Token;
import com.ecommerce.models.user.User;
import com.ecommerce.repository.user_repos.CustomerRepository;
import com.ecommerce.repository.user_repos.TokenRepository;
import com.ecommerce.repository.user_repos.UserRepository;
import com.ecommerce.service.email_service.EmailService;
import com.ecommerce.utils.jwt_utils.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public void saveActivationToken(User user, String subject) throws MessagingException {
        String activationToken = JwtUtil.generateToken(user,"activation",10800000);
        Long activationIssuedAt = JwtUtil.extractIssuedAt(activationToken);
        Token userToken = user.getToken();
        System.out.println(userToken);

        if (userToken == null) {
            userToken = new Token();
            userToken.setUser(user);
            user.setToken(userToken);
        }
        userToken.setActivation(activationIssuedAt);
        tokenRepository.save(userToken);

        System.out.println(activationIssuedAt+" "+JwtUtil.extractIssuedAt(activationToken));
        emailService.sendActivationEmail("ininsde15@gmail.com", subject,
                "Please activate your account by clicking the link below.", activationToken);
    }

}
