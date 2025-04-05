package com.ecommerce.service.user_service;

import com.ecommerce.dto.request_dto.CustomerRequestDTO;
import com.ecommerce.dto.request_dto.ForgotPasswordDTO;
import com.ecommerce.dto.request_dto.SellerRequestDTO;
import com.ecommerce.dto.response_dto.LoginTokenDTO;
import com.ecommerce.models.user.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private ActivationTokenService activationTokenService;

    @Autowired
    private ForgotResetPasswordService forgotPasswordService;

    @Autowired
    private LoginLogoutService loginLogoutService;


    public Customer registerCustomer(CustomerRequestDTO customerRequestDTO) throws MessagingException {
        return registerService.registerCustomer(customerRequestDTO);
    }

    public Seller registerSeller(SellerRequestDTO sellerRequestDTO) throws MessagingException {
        return registerService.registerSeller(sellerRequestDTO);
    }

    public String activateUser(String token) throws MessagingException {
        return activationTokenService.activateUser(token);
    }

    public String forgotPassword(String email) throws MessagingException {
        return forgotPasswordService.sendResetPasswordEmail(email);
    }

    public String resetPassword(String token,ForgotPasswordDTO forgotPasswordDTO) throws MessagingException {
        return forgotPasswordService.resetPassword(token,forgotPasswordDTO.getPassword(),forgotPasswordDTO.getConfirmPassword());
    }

    public String loginUser(String email, String password, HttpServletResponse response) {
        return loginLogoutService.loginUser(email,password,response);
    }

}
