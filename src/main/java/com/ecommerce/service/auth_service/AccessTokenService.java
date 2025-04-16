package com.ecommerce.service.auth_service;


import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;

public interface AccessTokenService {

    public String getAccessToken(HttpServletRequest request) throws BadRequestException;

}
