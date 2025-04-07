package com.ecommerce.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class UserIsLockedException extends RuntimeException {
    public UserIsLockedException() {
        super("User is locked. So cant access the account");
    }
    public UserIsLockedException(String message) {
        super(message);
    }
}
