package com.ecommerce.service.email_service;

import jakarta.mail.MessagingException;

public interface EmailService {

    public void sendEmail(String to, String subject, String text) throws MessagingException;

    public void sendActivationEmail(String to, String subject, String text, String token) throws MessagingException ;

    public void sendResetPasswordEmail(String to, String subject, String text, String token) throws MessagingException;
}
