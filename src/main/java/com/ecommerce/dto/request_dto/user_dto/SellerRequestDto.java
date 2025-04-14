package com.ecommerce.dto.request_dto.user_dto;

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
    @NotBlank(message = "{seller.gstNumber.required}")
    private String gstNumber;

    @NotBlank(message = "{seller.companyContact.required}")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}([\\s\\-\\(\\)]\\d{1,4})?$", message = "{seller.companyContact.pattern}")
    private String companyContact;

    @NotNull(message = "{seller.companyAddress.required}")
    private AddressRequestDto companyAddress;

    @NotBlank(message = "{seller.companyName.required}")
    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "{seller.companyName.pattern}")
    private String companyName;

}
