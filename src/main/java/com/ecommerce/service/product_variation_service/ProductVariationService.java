package com.ecommerce.service.product_variation_service;

import com.ecommerce.dto.request_dto.product_dto.ProductVariationRequestDto;
import org.apache.coyote.BadRequestException;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public interface ProductVariationService {

    public String addProductVariation(UUID sellerId, ProductVariationRequestDto productVariationRequestDto, Map<String,String> metadata) throws IOException;

}
