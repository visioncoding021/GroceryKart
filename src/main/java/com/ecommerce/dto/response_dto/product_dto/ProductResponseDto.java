package com.ecommerce.dto.response_dto.product_dto;

import com.ecommerce.dto.response_dto.category_dto.LeafCategoryResponseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
    private UUID id;
    private String name;
    private String description;
    private String brand;
    private Boolean isCancellable = false;
    private Boolean isReturnable = false;
    private List<ProductVariationResponseDto> productVariations;
    private LeafCategoryResponseDto category;
}
