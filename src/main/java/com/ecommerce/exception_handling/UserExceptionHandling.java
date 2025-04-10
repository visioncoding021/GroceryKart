package com.ecommerce.exception_handling;

import com.ecommerce.dto.response_dto.ErrorResponseDto;
import com.ecommerce.exception.user.UserAlreadyRegistered;
import com.ecommerce.exception.user.UserIsInactiveException;
import com.ecommerce.exception.user.UserIsLockedException;
import com.ecommerce.exception.user.UserNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;

@RestControllerAdvice
public class UserExceptionHandling {

    @ExceptionHandler(UserAlreadyRegistered.class)
    public ResponseEntity<ErrorResponseDto> handleUserAlreadyRegistered(UserAlreadyRegistered exception) {
        return buildErrorResponse("User already registered", exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFound(UserNotFoundException exception) {
        return buildErrorResponse("User not found", exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserIsInactiveException.class)
    public ResponseEntity<ErrorResponseDto> handleUserIsInactive(UserIsInactiveException exception) {
        return buildErrorResponse("User is inactive", exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserIsLockedException.class)
    public ResponseEntity<ErrorResponseDto> handleUserIsLocked(UserIsLockedException exception) {
        return buildErrorResponse("User is locked", exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleBadCredentials(BadCredentialsException exception) {
        return buildErrorResponse("Invalid credentials", "Invalid username or password", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequest(BadRequestException exception) {
        return buildErrorResponse("Bad request", exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String rootMessage = getRootCauseMessage(ex);
        String userFriendlyMessage = "A record with the same value already exists.";
        return buildErrorResponse(userFriendlyMessage, rootMessage, HttpStatus.CONFLICT);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception exception) {
//        return buildErrorResponse("An unexpected error occurred", exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }


    private ResponseEntity<ErrorResponseDto> buildErrorResponse( String message, String errorDetail, HttpStatus status) {
        ErrorResponseDto response = new ErrorResponseDto(status.value(),message, Collections.singletonList(errorDetail));
        return ResponseEntity.status(status).body(response);
    }

    private String getRootCauseMessage(Throwable throwable) {
        Throwable root = throwable;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        return root.getMessage();
    }

}
