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
    private String firstName;
    private String middleName;
    private String lastName;
    private String contact;
    private String gstNumber;
    private String companyContact;
    private String companyName;
}
