package com.ecommerce.dto.response_dto.message_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponseDto {
    private Integer status;
    private String message;
    private List<String> errors;
}