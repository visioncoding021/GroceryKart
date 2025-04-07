package com.ecommerce.service.auth_service;

import com.ecommerce.exception.user.UserNotFoundException;
import com.ecommerce.models.user.Token;
import com.ecommerce.models.user.User;
import com.ecommerce.repository.user_repos.TokenRepository;
import com.ecommerce.repository.user_repos.UserRepository;
import com.ecommerce.utils.jwt_utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccessTokenService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    public String getAccessToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("Cookie");
        if(refreshToken==null) {
            return "No refresh token found";
        }
        refreshToken = refreshToken.replace("refresh=", "");
        refreshToken = refreshToken.replace(";", "");

        if(!JwtUtil.isTokenValid(refreshToken) && !JwtUtil.extractType(refreshToken).equals("refresh")) return "Invalid refresh Token";

        User user = userRepository.findByEmail(JwtUtil.extractEmail(refreshToken)).orElseThrow(UserNotFoundException::new);
        Token userToken = user.getToken();
        Long refreshIssuedAt = userToken.getRefresh();
        if (refreshIssuedAt == null) {
            return "No Login Session Exist";
        }
        if(!refreshIssuedAt.equals(JwtUtil.extractIssuedAt(refreshToken))) {
            return "Refresh token is not valid";
        }
        String accessToken = JwtUtil.generateToken(user,"access",900000);
        userToken.setAccess(JwtUtil.extractIssuedAt(accessToken));
        tokenRepository.save(userToken);
        return accessToken;
    }
}
