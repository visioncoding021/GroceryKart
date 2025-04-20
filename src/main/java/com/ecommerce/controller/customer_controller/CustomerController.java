package com.ecommerce.controller.customer_controller;

import com.ecommerce.dto.response_dto.category_dto.CategoryNameResponseDto;
import com.ecommerce.dto.response_dto.category_dto.LeafCategoryResponseDto;
import com.ecommerce.dto.response_dto.message_dto.ApiResponseDto;
import com.ecommerce.service.category_service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public ApiResponseDto<List<CategoryNameResponseDto>> getAllCategories(
            @RequestParam(value = "categoryId", defaultValue = "") String id
    ){
        UUID categoryId = (id.isBlank())?null:UUID.fromString(id);
        return new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "All Categories are fetched Successfully",
                categoryService.getSameLevelCategories(categoryId)
        );
    }
}
