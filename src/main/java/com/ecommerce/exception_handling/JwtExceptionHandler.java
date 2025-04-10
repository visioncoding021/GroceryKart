package com.ecommerce.exception_handling;

import com.ecommerce.dto.response_dto.ErrorResponseDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import io.jsonwebtoken.security.SignatureException;

import java.util.Collections;

@RestControllerAdvice
public class JwtExceptionHandler {

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidSignature(SignatureException e) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED,"Invalid JWT signature");
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponseDto> handleExpiredToken(ExpiredJwtException e) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED,"JWT token has expired");
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidToken(MalformedJwtException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST,"Invalid JWT token format");
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ErrorResponseDto> handleUnsupportedToken(UnsupportedJwtException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST,"Unsupported JWT token");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleEmptyClaims(IllegalArgumentException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST,e.getMessage());
    }

    private ResponseEntity<ErrorResponseDto> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponseDto error = new ErrorResponseDto(
                status.value(),
                message,
                Collections.singletonList(message)
        );
        return new ResponseEntity<>(error, status);
    }
}

