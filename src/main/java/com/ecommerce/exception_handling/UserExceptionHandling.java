package com.ecommerce.exception_handling;

import com.ecommerce.exception.UserAlreadyRegistered;
import com.ecommerce.exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;
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

}
