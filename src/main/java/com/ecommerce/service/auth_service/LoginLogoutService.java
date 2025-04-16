package com.ecommerce.service.auth_service;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.BadRequestException;


public interface LoginLogoutService {

    public String loginUser(String email, String password,HttpServletRequest request, HttpServletResponse response) throws BadRequestException ;

    public String logoutUser(String accessToken, HttpServletRequest request, HttpServletResponse response) ;

}
