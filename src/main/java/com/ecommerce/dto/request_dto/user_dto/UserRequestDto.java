package com.ecommerce.dto.request_dto.user_dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRequestDto {
    @Email(message = "{user.email.invalid}")
    @NotBlank(message = "{user.email.required}")
    private String email;

    @NotBlank(message = "{user.firstName.required}")
    @Size(max = 50, message = "{user.firstName.size}")
    private String firstName;

    @Size(max = 50, message = "{user.middleName.size}")
    private String middleName;

    @NotBlank(message = "{user.lastName.required}")
    @Size(max = 50, message = "{user.lastName.size}")
    private String lastName;

    @NotBlank(message = "{user.password.required}")
    @Size(min = 8, max = 15, message = "{user.password.size}")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "{user.password.pattern}"
    )
    private String password;

    @NotBlank(message = "{user.confirmPassword.required}")
    @Size(min = 8, max = 15, message = "{user.confirmPassword.size}")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "{user.confirmPassword.pattern}"
    )
    private String confirmPassword;
}
