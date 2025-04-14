package com.ecommerce.dto.request_dto.user_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerRequestDto extends UserRequestDto {
    @NotBlank(message = "{customer.contactNumber.required}")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "{customer.contactNumber.pattern}")
    private String contact;

    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "{address.city.pattern}")
    private String city;

    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "{address.state.pattern}")
    private String state;

    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "{address.country.pattern}")
    private String country;

    private String addressLine;

    @Pattern(regexp = "^[0-9]{5}(-[0-9]{4})?$", message = "{address.zipCode.pattern}")
    private String zipCode;
    private String label;
}
