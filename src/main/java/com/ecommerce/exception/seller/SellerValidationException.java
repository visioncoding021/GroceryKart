package com.ecommerce.exception.seller;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Getter
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class SellerValidationException extends RuntimeException{
    private List<String> errors;
    public SellerValidationException(List<String> errors) {
        super("Seller validation failed");
        this.errors = errors;
    }
}
