package com.ecommerce.exception_handling;

import com.ecommerce.exception.user.UserAlreadyRegistered;
import com.ecommerce.exception.user.UserIsInactiveException;
import com.ecommerce.exception.user.UserIsLockedException;
import com.ecommerce.exception.user.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandling {

    @ExceptionHandler(UserAlreadyRegistered.class)
    public ResponseEntity<String> handleUserAlreadyRegistered(UserAlreadyRegistered exception) {
        return ResponseEntity.status(400).body(exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException exception) {
        return ResponseEntity.status(404).body(exception.getMessage());
    }

    @ExceptionHandler(UserIsInactiveException.class)
    public ResponseEntity<String> handleUserIsInactive(UserIsInactiveException exception) {
        return ResponseEntity.status(403).body(exception.getMessage());
    }

    @ExceptionHandler(UserIsLockedException.class)
    public ResponseEntity<String> handleUserIsLocked(UserIsLockedException exception) {
        return ResponseEntity.status(403).body(exception.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(BadCredentialsException exception) {
        return ResponseEntity.status(401).body("Invalid username or password");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception exception) {
        return ResponseEntity.status(500).body("An unexpected error occurred: " + exception.getMessage());
    }

}
