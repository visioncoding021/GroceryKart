package com.ecommerce.exception_handling;

import com.ecommerce.dto.response_dto.message_dto.ErrorResponseDto;
import com.ecommerce.exception.seller.SellerValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;

@RestControllerAdvice
public class SellerExceptionHandler {

    @ExceptionHandler(SellerValidationException.class)
    public ResponseEntity<?> handleSellerValidationException(SellerValidationException ex) {
        ErrorResponseDto response = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                ex.getErrors()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorResponseDto> buildErrorResponse(String message, String errorDetail, HttpStatus status) {
        ErrorResponseDto response = new ErrorResponseDto(status.value(),message, Collections.singletonList(errorDetail));
        return ResponseEntity.status(status).body(response);
    }
}
