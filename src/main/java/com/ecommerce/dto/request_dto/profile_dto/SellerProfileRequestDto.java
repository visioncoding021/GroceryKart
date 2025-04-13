package com.ecommerce.dto.request_dto.profile_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SellerProfileRequestDto {
    @Size(max = 50, message = "First name must be less than 50 characters")
    private String firstName;

    @Size(max = 50, message = "Middle name must be less than 50 characters")
    private String middleName;

    @Size(max = 50, message = "Last name must be less than 50 characters")
    private String lastName;

    private String gstNumber;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}([\\s\\-\\(\\)]\\d{1,4})?$", message = "Company contact must be a valid phone number")
    private String companyContact;

    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "Company name must contain only letters, spaces, or hyphens")
    private String companyName;
}
