package com.ecommerce.service.product_variation_service;

import com.ecommerce.dto.request_dto.product_dto.ProductVariationRequestDto;
import com.ecommerce.dto.request_dto.product_dto.ProductVariationUpdateRequestDto;
import com.ecommerce.dto.response_dto.message_dto.PaginatedResponseDto;
import com.ecommerce.dto.response_dto.product_variation_dto.ProductVariationResponseDto;
import org.apache.coyote.BadRequestException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProductVariationService {

    public String addProductVariation(UUID sellerId, ProductVariationRequestDto productVariationRequestDto, Map<String, String> metadata) throws IOException;

    ProductVariationResponseDto getProductVariationById(UUID productVariationId, UUID sellerId) throws BadRequestException, FileNotFoundException;

    PaginatedResponseDto<List<ProductVariationResponseDto>> getAllProductVariationByProductId(UUID productId, UUID sellerId, int max, int offset, String sort, String order, Map<String, String> filters) throws BadRequestException, FileNotFoundException;

    String updateProductVariation(UUID productVariationId, UUID sellerId, ProductVariationUpdateRequestDto productVariationRequestDto, Map<String, String> metadata) throws IOException;
}