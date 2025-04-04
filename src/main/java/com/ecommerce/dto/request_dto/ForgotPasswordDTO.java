package com.ecommerce.dto.request_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@NoArgsConstructor
public class ForgotPasswordDTO {

    @NotBlank(message = "New password is mandatory")
    @Size(min = 8, max = 15, message = "New password must be between 8 and 15 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "Confirm Password must contain at least one lowercase letter, one uppercase letter, one number, and one special character")
    private String password;

    @NotBlank(message = "New password is mandatory")
    @Size(min = 8, max = 15, message = "New password must be between 8 and 15 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "Confirm Password must contain at least one lowercase letter, one uppercase letter, one number, and one special character")
    private String confirmPassword;
}
