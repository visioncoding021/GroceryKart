package com.ecommerce.dto.response_dto.user_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AllCustomersResponseDto extends UserResponseDto{
    private String contact;
}
