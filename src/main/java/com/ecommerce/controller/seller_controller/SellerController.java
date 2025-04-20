package com.ecommerce.controller.seller_controller;

import com.ecommerce.dto.request_dto.product_dto.ProductRequestDto;
import com.ecommerce.dto.response_dto.category_dto.LeafCategoryResponseDto;
import com.ecommerce.dto.response_dto.message_dto.ApiResponseDto;
import com.ecommerce.dto.response_dto.message_dto.MessageResponseDto;
import com.ecommerce.service.category_service.CategoryService;
import com.ecommerce.service.product_service.ProductService;
import com.ecommerce.utils.user_utils.CurrentUserUtils;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.Currency;
import java.util.List;

@RestController
@RequestMapping("/api/seller")
public class SellerController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CurrentUserUtils currentUserUtils;

    @GetMapping("/categories")
    public ApiResponseDto<List<LeafCategoryResponseDto>> getAllLeafCategories(){
        System.out.println(currentUserUtils.getUserId());
        System.out.println(currentUserUtils.getUsername());
        return new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "All Leaf Categories are fetched",
                categoryService.getAllLeafCategories()
        );
    }

    @PostMapping("/add-product")
    public ResponseEntity<String> addProduct(@Valid @RequestBody ProductRequestDto productRequestDto) throws BadRequestException {
        return ResponseEntity.ok().body(
            productService.addProduct(productRequestDto,currentUserUtils.getUserId())
        );
    }



}
