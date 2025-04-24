package com.ecommerce.controller.common_controller;

import com.ecommerce.dto.response_dto.message_dto.ApiResponseDto;
import com.ecommerce.dto.response_dto.product_dto.ProductResponseDto;
import com.ecommerce.service.product_service.ProductService;
import com.ecommerce.utils.user_utils.CurrentUserUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private CurrentUserUtils currentUserUtils;

    @Autowired
    private ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponseDto<ProductResponseDto>> getProductsById(@PathVariable UUID productId) throws FileNotFoundException, BadRequestException {

        ApiResponseDto<ProductResponseDto> response = new ApiResponseDto<>(200,
                "Product Retrieved Successfully",
                productService.getProductByIdForUser(productId,currentUserUtils.getRole())
        );

        return ResponseEntity.ok().body(response);
    }

//    @GetMapping
//    public ResponseEntity<ApiResponseDto<List<ProductResponseDto>>> getProducts(@PathVariable UUID productId) throws FileNotFoundException, BadRequestException {
//
//        ApiResponseDto<List<ProductResponseDto>> response = new ApiResponseDto<>(200,
//                "Product Retrieved Successfully",
//                productService.getProductByIdForUser(productId,currentUserUtils.getRole())
//        );
//
//        return ResponseEntity.ok().body(response);
//    }
}
