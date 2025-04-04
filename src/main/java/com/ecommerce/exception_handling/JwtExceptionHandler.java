package com.ecommerce.exception_handling;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import io.jsonwebtoken.security.SignatureException;

@RestControllerAdvice
public class JwtExceptionHandler {

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<String> handleInvalidSignature(SignatureException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT signature");
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<String> handleExpiredToken(ExpiredJwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT token has expired");
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<String> handleInvalidToken(MalformedJwtException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JWT token format");
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<String> handleUnsupportedToken(UnsupportedJwtException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsupported JWT token");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleEmptyClaims(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}

