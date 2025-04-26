package com.ecommerce.service.auth_service;

import com.ecommerce.exception.user.UserNotFoundException;
import com.ecommerce.models.user.Token;
import com.ecommerce.models.user.User;
import com.ecommerce.repository.user_repos.TokenRepository;
import com.ecommerce.repository.user_repos.UserRepository;
import com.ecommerce.utils.jwt_utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AccessTokenServiceImpl implements AccessTokenService{

    private static final Logger logger = LoggerFactory.getLogger(AccessTokenServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public String getAccessToken(HttpServletRequest request) throws BadRequestException {
        String refreshToken = request.getHeader("Cookie");
        if(refreshToken == null || !refreshToken.contains("refresh=")) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(401),"No Login session found");
        }
        refreshToken = refreshToken.replace("refresh=", "");
        refreshToken = refreshToken.replace(";", "");
        if(refreshToken.isEmpty()) {
            logger.warn("No refresh token found in cookies");
            throw new ResponseStatusException(HttpStatusCode.valueOf(401),"No refresh token found in cookies");
        }
        if(!JwtUtil.isTokenValid(refreshToken) && !JwtUtil.extractType(refreshToken).equals("refresh")){
            logger.warn("No refresh token found in cookies");
            throw new ResponseStatusException(HttpStatusCode.valueOf(401),"Refresh token is not valid");
        }

        String userEmail = JwtUtil.extractEmail(refreshToken);
        logger.debug("Extracted user email from refresh token: {}", userEmail);

        User user = userRepository.findByEmail(JwtUtil.extractEmail(refreshToken)).orElseThrow(UserNotFoundException::new);
        Token userToken = user.getToken();
        Long refreshIssuedAt = userToken.getRefresh();
        if (refreshIssuedAt == null) {
            logger.warn("No refresh issue time found for user: {}", userEmail);
            throw new ResponseStatusException(HttpStatusCode.valueOf(401),"No Login session found");
        }
        if(!refreshIssuedAt.equals(JwtUtil.extractIssuedAt(refreshToken))) {
            logger.warn("Refresh token issue time mismatch for user: {}", userEmail);
            throw new ResponseStatusException(HttpStatusCode.valueOf(401),"Refresh token is not valid");
        }

        String accessToken = JwtUtil.generateToken(user,"access",900000);
        userToken.setAccess(JwtUtil.extractIssuedAt(accessToken));
        tokenRepository.save(userToken);
        return accessToken;
    }
}
