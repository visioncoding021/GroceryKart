package com.ecommerce.dto.response_dto.user_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerProfileResponseDto {
    private UUID id;
    private String firstName;
    private String middleName;
    private String lastName;
    private boolean isActive;
    private String companyName;
    private String companyContact;
    private String gstNumber;
    private String imageUrl;
    private AddressResponseDto address;
}
