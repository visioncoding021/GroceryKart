package com.ecommerce.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class UserIsInactiveException extends RuntimeException{
    public UserIsInactiveException() {
        super("User is inactive. Please activate your account to access it.");
    }

    public UserIsInactiveException(String message) {
        super(message);
    }
}
