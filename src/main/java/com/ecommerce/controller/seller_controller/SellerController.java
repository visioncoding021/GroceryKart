package com.ecommerce.controller.seller_controller;

import com.ecommerce.dto.response_dto.category_dto.LeafCategoryResponseDto;
import com.ecommerce.dto.response_dto.message_dto.ApiResponseDto;
import com.ecommerce.service.category_service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/seller")
public class SellerController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public ApiResponseDto<List<LeafCategoryResponseDto>> getAllLeafCategories(){
        return new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "All Leaf Categories are fetched",
                categoryService.getAllLeafCategories()
        );
    }



}
