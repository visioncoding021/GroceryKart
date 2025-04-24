package com.ecommerce.dto.response_dto.product_variation_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariationResponseDto {
    private UUID id;
    private Map<String,String> metadata;
    private String quantityAvailable;
    private Double price;
    private Boolean isActive;
    private String primaryImageUrl;
    private List<String> secondaryImages;
    private ProductResponseDto product;
}
