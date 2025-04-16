package com.ecommerce.service.auth_service;

import com.ecommerce.exception.user.UserNotFoundException;
import jakarta.mail.MessagingException;


public interface ForgotResetPasswordService {

    public String sendResetPasswordEmail(String email) throws MessagingException ;

    public String resetPassword(String token, String password , String confirmPassword) throws UserNotFoundException ;

}
