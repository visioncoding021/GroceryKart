package com.ecommerce.exception_handling;

import com.ecommerce.dto.response_dto.message_dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.NoSuchFileException;
import java.util.Collections;

@RestControllerAdvice
public class ImageExceptionHandler {

    @ExceptionHandler(NoSuchFileException.class)
    public ResponseEntity<ErrorResponseDto> handleNoSuchFileException(NoSuchFileException ex) {
        return buildErrorResponse("Image Not Found ", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorResponseDto> buildErrorResponse(String message, String errorDetail, HttpStatus status) {
        ErrorResponseDto response = new ErrorResponseDto(status.value(),message, Collections.singletonList(errorDetail));
        return ResponseEntity.status(status).body(response);
    }
}
