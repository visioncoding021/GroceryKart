package com.ecommerce.utils.jwt_utils;

import com.ecommerce.models.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public final class JwtUtil {

    private static String secretKey;

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey
    ) {
        this.secretKey = secretKey;
    }

    public static String generateToken(User user,String type,long expirationTime) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRole().getAuthority());
        claims.put("type", type);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static LocalDateTime extractIssuedAt(String token) {
        Date issuedAt = Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getIssuedAt();

        return issuedAt.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8)).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
