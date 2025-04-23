package com.ecommerce.service.product_service;

import com.ecommerce.dto.request_dto.product_dto.ProductRequestDto;
import com.ecommerce.dto.response_dto.product_dto.ProductResponseDto;
import org.apache.coyote.BadRequestException;

import java.util.UUID;

public interface ProductService {
    public String addProduct(ProductRequestDto productRequestDto, UUID sellerId) throws BadRequestException;
    public ProductResponseDto getProductDetailsById(UUID productId) throws BadRequestException;
}
