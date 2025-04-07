package com.ecommerce.service.user_service;

import com.ecommerce.dto.request_dto.CustomerRequestDTO;
import com.ecommerce.dto.request_dto.ForgotPasswordDTO;
import com.ecommerce.dto.request_dto.SellerRequestDTO;
import com.ecommerce.dto.response_dto.LoginTokenDTO;
import com.ecommerce.models.user.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private ActivationTokenService activationTokenService;

    @Autowired
    private ForgotResetPasswordService forgotResetPasswordService;

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
        return forgotResetPasswordService.sendResetPasswordEmail(email);
    }

    public String resetPassword(String token,ForgotPasswordDTO forgotPasswordDTO) throws MessagingException {
        return forgotResetPasswordService.resetPassword(token,forgotPasswordDTO.getPassword(),forgotPasswordDTO.getConfirmPassword());
    }

    public String loginUser(String email, String password,HttpServletRequest request, HttpServletResponse response) throws BadRequestException {
        return loginLogoutService.loginUser(email,password,request,response);
    }

    public String resendActivationLink(String email) throws MessagingException {
        return activationTokenService.resendActivationLink(email);
    }

    public String logoutUser(String accessToken, HttpServletRequest request, HttpServletResponse response) {
        return loginLogoutService.logoutUser(accessToken,request, response);
    }

}
