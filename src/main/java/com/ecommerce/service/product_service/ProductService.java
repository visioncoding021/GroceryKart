package com.ecommerce.service.product_service;

import com.ecommerce.dto.request_dto.product_dto.ProductRequestDto;
import org.apache.coyote.BadRequestException;

import java.util.UUID;

public interface ProductService {
    public String addProduct(ProductRequestDto productRequestDto, UUID sellerId) throws BadRequestException;
}
