package com.ecommerce.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAlreadyRegistered extends RuntimeException {
    public UserAlreadyRegistered() {
        super("User already registered with this email: ");
    }
    public UserAlreadyRegistered(String message) {
        super(message);
    }
}
