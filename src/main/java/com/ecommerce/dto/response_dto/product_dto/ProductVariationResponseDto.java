package com.ecommerce.dto.response_dto.product_dto;

import com.ecommerce.dto.response_dto.category_dto.LeafCategoryResponseDto;
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
    private UUID productId;
    private Map<String,String> metadata;
    private String quantityAvailable;
    private Double price;
    private String primaryImageUrl;
    private List<String> secondaryImages;
}
