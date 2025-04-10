package com.ecommerce.dto.response_dto.user_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponseDto {
    private String city;
    private String state;
    private String country;
    private String addressLine;
    private String zipCode;
    private String label;
}
