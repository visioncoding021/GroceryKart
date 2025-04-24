package com.ecommerce.service.product_service;

import com.ecommerce.dto.request_dto.product_dto.ProductRequestDto;
import com.ecommerce.dto.response_dto.message_dto.PaginatedResponseDto;
import com.ecommerce.dto.response_dto.product_dto.ProductResponseDto;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProductService {
    public String addProduct(ProductRequestDto productRequestDto, UUID sellerId) throws BadRequestException;

    @Transactional
    String deleteProduct(UUID productId, UUID sellerId) throws BadRequestException;

    public ProductResponseDto getProductDetailsById(UUID productId, UUID sellerId) throws BadRequestException, FileNotFoundException;

    PaginatedResponseDto<List<ProductResponseDto>> getAllProductsBySellerId(UUID sellerId, int max, int offset, String sort, String order, Map<String,String> filters) throws BadRequestException, FileNotFoundException;

    public String updateProduct(ProductRequestDto productRequestDto, UUID productId, UUID sellerId) throws BadRequestException;

    String activateProduct(UUID productId) throws BadRequestException;

    String deactivateProduct(UUID productId) throws BadRequestException;

    ProductResponseDto getProductByIdForUser(UUID productId, String role) throws BadRequestException, FileNotFoundException;

    PaginatedResponseDto<List<ProductResponseDto>> getAllProductsForUser(String role,UUID categoryId, int max, int offset, String sort, String order, Map<String, String> filters) throws BadRequestException, FileNotFoundException;

    PaginatedResponseDto<List<ProductResponseDto>> getAllSimilarProductsForCustomer(UUID productId, int max, int offset, String sort, String order, Map<String, String> filters) throws FileNotFoundException;
}
