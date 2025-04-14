package com.ecommerce.dto.request_dto.user_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddressRequestDto {
    @NotBlank(message = "{address.city}")
    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "{address.city.pattern}")
    private String city;

    @NotBlank(message = "{address.state}")
    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "{address.state.pattern}")
    private String state;

    @NotBlank(message = "{address.country}")
    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "{address.country.pattern}")
    private String country;

    @NotBlank(message = "{address.addressLine}")
    private String addressLine;

    @NotBlank(message = "{address.zipCode}")
    @Pattern(regexp = "^[0-9]{5}(-[0-9]{4})?$", message = "{address.zipCode.pattern}")
    private String zipCode;

    @NotBlank(message = "{address.label}")
    private String label;
}
