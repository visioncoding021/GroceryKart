package com.ecommerce.service.email_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired


    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

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
    public void sendActivationEmail(String to, String subject, String text, String token) throws MessagingException {
        String activationLink = "http://localhost:8080/api/activate?token=" + token;
        sendEmail(to, subject, text + "\n\n" + "Click the link to activate your account: " + activationLink);
    }

    @Async
    public void sendResetPasswordEmail(String to, String subject, String text, String token) throws MessagingException {
        String resetLink = "http://localhost:8080/api/auth/reset-password?token=" + token;
        sendEmail(to, subject, text + "\n\n" + "Click the link to reset your password: " + resetLink);
    }
}
