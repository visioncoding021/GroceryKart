package com.ecommerce.dto.request_dto.category_dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryMetadataFieldRequestDto {
    @NotBlank(message = "Field name is required")
    private String name;
}
