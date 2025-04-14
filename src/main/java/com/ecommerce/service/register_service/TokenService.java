package com.ecommerce.service.register_service;

import com.ecommerce.models.user.Token;
import com.ecommerce.models.user.User;
import com.ecommerce.repository.user_repos.TokenRepository;
import com.ecommerce.repository.user_repos.UserRepository;
import com.ecommerce.service.email_service.EmailService;
import com.ecommerce.utils.jwt_utils.JwtUtil;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MessageSource messageSource;

    public void saveActivationToken(User user, String subject) throws MessagingException {

        Locale locale = LocaleContextHolder.getLocale();

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

        String subjectLocalized = messageSource.getMessage("email.activation.subject", null, locale);
        String bodyLocalized = messageSource.getMessage("email.activation.body", null, locale);
        String linkText = messageSource.getMessage("email.activation.linkText", null, locale);


        System.out.println(activationIssuedAt+" "+JwtUtil.extractIssuedAt(activationToken));
        emailService.sendActivationEmail("ininsde15@gmail.com", subjectLocalized, bodyLocalized, activationToken);
    }

}
