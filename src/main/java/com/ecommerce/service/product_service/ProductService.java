package com.ecommerce.service.product_service;

import com.ecommerce.dto.request_dto.product_dto.ProductRequestDto;
import com.ecommerce.dto.response_dto.message_dto.PaginatedResponseDto;
import com.ecommerce.dto.response_dto.product_dto.ProductResponseDto;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProductService {
    public String addProduct(ProductRequestDto productRequestDto, UUID sellerId) throws BadRequestException;

    public ProductResponseDto getProductDetailsById(UUID productId, UUID sellerId) throws BadRequestException;

    PaginatedResponseDto<List<ProductResponseDto>> getAllProductsBySellerId(UUID sellerId, int max, int offset, String sort, String order, Map<String,String> filters) throws BadRequestException;

}
