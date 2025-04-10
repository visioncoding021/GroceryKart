package com.ecommerce.dto.request_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SellerRequestDto extends UserRequestDto {
    @NotBlank(message = "GST number is mandatory")
    private String gstNumber;

    @NotBlank(message = "Company contact is mandatory")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}([\\s\\-\\(\\)]\\d{1,4})?$", message = "Company contact must be a valid phone number")
    private String companyContact;

    @NotNull(message = "Company address is mandatory")
    private SellerAddressDto companyAddress;

    @NotBlank(message = "Company name is mandatory")
    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "Company name must contain only letters, spaces, or hyphens")
    private String companyName;

}
