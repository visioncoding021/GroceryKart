package com.ecommerce.dto.request_dto.auth_dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthRequestDto {
    @NotBlank(message = "{auth.email}")
    @Email(message = "{auth.email.invalid}")
    private String email;

    @NotBlank(message = "{auth.password}")
    private String password;
}
