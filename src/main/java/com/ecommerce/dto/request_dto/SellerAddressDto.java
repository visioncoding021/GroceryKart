package com.ecommerce.dto.request_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SellerAddressDto {
    @NotBlank(message = "City is mandatory")
    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "City must contain only letters, spaces, or hyphens")
    private String city;

    @NotBlank(message = "State is mandatory")
    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "State must contain only letters, spaces, or hyphens")
    private String state;

    @NotBlank(message = "Country is mandatory")
    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "Country must contain only letters, spaces, or hyphens")
    private String country;

    @NotBlank(message = "Address line is mandatory")
    private String addressLine;

    @NotBlank(message = "Zip code is mandatory")
    @Pattern(regexp = "^[0-9]{5}(-[0-9]{4})?$", message = "Invalid ZIP code format")
    private String zipCode;

    @NotBlank(message = "Label is mandatory")
    private String label;
}
