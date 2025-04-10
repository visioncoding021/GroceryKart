package com.ecommerce.dto.response_dto.user_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private UUID id;
    private String email;
    private String fullName;
    private boolean isActive;
    private boolean isExpired;
    private boolean isLocked;
    private boolean isDeleted;

    public boolean getIsActive() {
        return isActive;
    }

    public boolean getIsExpired() {
        return isExpired;
    }

    public boolean getIsLocked() {
        return isLocked;
    }
    public boolean getIsDeleted() {
        return isDeleted;
    }
}
