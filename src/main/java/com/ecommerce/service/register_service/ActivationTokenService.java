package com.ecommerce.service.register_service;

import com.ecommerce.exception.user.UserNotFoundException;
import com.ecommerce.models.user.Token;
import com.ecommerce.models.user.User;
import com.ecommerce.repository.user_repos.TokenRepository;
import com.ecommerce.repository.user_repos.UserRepository;
import com.ecommerce.service.email_service.EmailService;
import com.ecommerce.utils.jwt_utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;
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

    @Autowired
    private MessageSource messageSource;

    public String activateUser(String token) throws MessagingException {

        Locale locale = LocaleContextHolder.getLocale();

        Map<String,Object> claims = JwtUtil.extractAllClaims(token);
        String email = JwtUtil.extractEmail(token);
        String type = (String) claims.get("type");

        System.out.println(email+" "+type);

        if(!type.equals("activation")) {
            throw new IllegalArgumentException(messageSource.getMessage("error.invalid.token", null, locale));
        }

        System.out.println(email+" "+type);

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            emailService.sendEmail("ininsde15@gmail.com", messageSource.getMessage("email.activation.failed.subject", null, locale),
                    messageSource.getMessage("email.activation.failed.body", null, locale));
            return messageSource.getMessage("response.activation.userNotRegistered", null, locale);
        }

        if (user.getIsActive()) {
            emailService.sendEmail("ininsde15@gmail.com", messageSource.getMessage("email.alreadyActivated.subject", null, locale),
                    messageSource.getMessage("email.alreadyActivated.body", null, locale));
            return messageSource.getMessage("response.activation.alreadyActivated", null, locale);
        }

        Token userToken = user.getToken();
        Long activationIssuedAt = userToken.getActivation();
        System.out.println(activationIssuedAt+" "+JwtUtil.extractIssuedAt(token));

        if (!activationIssuedAt.equals(JwtUtil.extractIssuedAt(token))){
            throw new JwtException(messageSource.getMessage("error.token.expiredOrInvalid", null, locale));
        }

        if (!JwtUtil.isTokenValid(token)) {
            tokenService.saveActivationToken(user, messageSource.getMessage("email.activation.failed.subject", null, locale));
            return messageSource.getMessage("response.activation.expired", null, locale);
        }

        user.setIsActive(true);
        userToken.setActivation(null);
        tokenRepository.save(userToken);
        userRepository.save(user);

        emailService.sendEmail("ininsde15@gmail.com",  messageSource.getMessage("email.activation.success.subject", null, locale),
                messageSource.getMessage("email.activation.success.body", null, locale));

        return messageSource.getMessage("response.activation.success", null, locale);
    }

    public String resendActivationLink(String email) throws MessagingException {
        Locale locale = LocaleContextHolder.getLocale();
        System.out.println(email);
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        if (user.getIsActive()) {
            return messageSource.getMessage("response.activation.alreadyActivated", null, locale);
        }
        tokenService.saveActivationToken(user, messageSource.getMessage("email.resendActivationToken.subject", null, locale));
        return messageSource.getMessage("response.activation.linkResent", null, locale);
    }
}
