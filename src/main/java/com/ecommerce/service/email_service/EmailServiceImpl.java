package com.ecommerce.service.email_service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService{

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final MessageSource messageSource;


    public EmailServiceImpl(JavaMailSender mailSender, MessageSource messageSource) {
        this.mailSender = mailSender;
        this.messageSource=messageSource;
    }

    @Async
    @Override
    public void sendEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);
        helper.setFrom(fromEmail);

        mailSender.send(message);
    }

    @Async
    @Override
    public void sendActivationEmail(String to, String subject, String text, String token) throws MessagingException {
        Locale locale = LocaleContextHolder.getLocale();
        String activationLink = "http://localhost:8080/api/auth/activate?token=" + token;
        sendEmail(to, subject, text + "<br><br><a href=\"" + activationLink + "\">" +
                messageSource.getMessage("email.activation.linkText", null, locale) + "</a>");
    }

    @Async
    @Override
    public void sendResetPasswordEmail(String to, String subject, String text, String token) throws MessagingException {
        Locale locale = LocaleContextHolder.getLocale();
        String resetLink = "http://localhost:8080/api/auth/reset-password?token=" + token;
        sendEmail(to, subject, text + "\n\n" + messageSource.getMessage("email.resetPassword.linkText", null, locale) + resetLink);
    }

}
