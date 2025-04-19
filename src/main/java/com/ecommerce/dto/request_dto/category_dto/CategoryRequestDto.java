package com.ecommerce.dto.request_dto.category_dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CategoryRequestDto {
    @NotBlank(message = "Field name is required")
    private String name;

    private UUID parentId;
}
