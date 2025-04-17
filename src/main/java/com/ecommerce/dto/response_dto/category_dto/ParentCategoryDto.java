package com.ecommerce.dto.response_dto.category_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParentCategoryDto {
    private UUID id;
    private String name;
    private ParentCategoryDto parent;
}
