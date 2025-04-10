package com.ecommerce.dto.response_dto.message_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MessageResponseDto {
    int status;
    String message;
}
