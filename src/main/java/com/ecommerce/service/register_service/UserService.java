package com.ecommerce.service.register_service;

import com.ecommerce.dto.request_dto.user_dto.CustomerRequestDto;
import com.ecommerce.dto.request_dto.auth_dto.ForgotPasswordDto;
import com.ecommerce.dto.request_dto.user_dto.SellerRequestDto;
import com.ecommerce.models.user.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.BadRequestException;

import java.util.Locale;

public interface UserService {

    public Customer registerCustomer(CustomerRequestDto customerRequestDTO, Locale locale) throws MessagingException ;

    public Seller registerSeller(SellerRequestDto sellerRequestDTO,Locale locale) throws MessagingException ;

    public String activateUser(String token) throws MessagingException ;

    public String forgotPassword(String email) throws MessagingException ;

    public String resetPassword(String token, ForgotPasswordDto forgotPasswordDTO) throws MessagingException ;

    public String loginUser(String email, String password,HttpServletRequest request, HttpServletResponse response) throws BadRequestException ;

    public String resendActivationLink(String email) throws MessagingException ;

    public String logoutUser(String accessToken, HttpServletRequest request, HttpServletResponse response);

    public String getAccessToken(HttpServletRequest request) throws BadRequestException;

}
