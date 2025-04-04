package com.ecommerce.dto.response_dto;

import java.util.List;

public class ErrorResponse {
    private String message;
    private List<String> errors;

    public ErrorResponse(String message, List<String> errors) {
        this.message = message;
        this.errors = errors;
    }

    // Getters and Setters
}