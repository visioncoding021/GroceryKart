package com.ecommerce.dto.response_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerResponseDto {
    private UUID id;
    private String email;
    private String fullName;
    private String companyName;
    private String companyContact;
    private String gstNumber;
    private boolean isActive;
    private boolean isExpired;
    private boolean isLocked;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
