package com.ecommerce.dto.response_dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
public class LoginTokenDTO {

    @NotBlank(message = "Access token is mandatory")
    private String accessToken;

    @NotBlank(message = "Refresh token is mandatory")
    private String refreshToken;
}
