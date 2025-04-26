package com.ecommerce.dto.request_dto.profile_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserProfileRequestDto {
    @Size(max = 50, message = "First name must be less than 50 characters")
    private String firstName;

    @Size(max = 50, message = "First name must be less than 50 characters")
    private String middleName;

    @Size(max = 50, message = "First name must be less than 50 characters")
    private String lastName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "{customer.contactNumber.pattern}")
    private String contact;

    private String gstNumber;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "{seller.companyContact.pattern}")
    private String companyContact;

    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "{seller.companyName.pattern}")
    private String companyName;
}
