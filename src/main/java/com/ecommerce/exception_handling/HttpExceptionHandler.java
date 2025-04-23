package com.ecommerce.exception_handling;

import com.ecommerce.dto.response_dto.message_dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

@RestControllerAdvice
public class HttpExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleJsonParseException(HttpMessageNotReadableException ex) {
        String errorDetail = "JSON parse error: " + ex.getMessage();
        return buildErrorResponse("Bad Request", errorDetail, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingParams(MissingServletRequestParameterException ex) {
        String errorDetail = "Missing required parameter: " + ex.getParameterName();
        return buildErrorResponse("Bad Request", errorDetail, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        String errorDetail = "Request method " + ex.getMethod() + " not supported for this endpoint";
        return buildErrorResponse("Method Not Allowed", errorDetail, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(ResponseStatusException ex) {
        String errorDetail = ex.getReason() != null ? ex.getReason() : "Resource not found";
        return buildErrorResponse("Not Found", errorDetail, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ErrorResponseDto> buildErrorResponse(String message, String errorDetail, HttpStatus status) {
        ErrorResponseDto response = new ErrorResponseDto(status.value(),message, Collections.singletonList(errorDetail));
        return ResponseEntity.status(status).body(response);
    }
}
