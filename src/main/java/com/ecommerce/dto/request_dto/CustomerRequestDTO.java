package com.ecommerce.dto.request_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
public class CustomerRequestDTO extends UserRequestDTO{
    @NotBlank(message = "Contact is mandatory")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Contact must be a valid phone number")
    private String contact;

    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "City must contain only letters, spaces, or hyphens")
    private String city;

    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "State must contain only letters, spaces, or hyphens")
    private String state;

    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "Country must contain only letters, spaces, or hyphens")
    private String country;

    private String addressLine;

    @Pattern(regexp = "^[0-9]{5}(-[0-9]{4})?$", message = "Invalid ZIP code format")
    private String zipCode;
    private String label;
}
