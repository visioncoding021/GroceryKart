package com.ecommerce.controller.category_controller;

import com.ecommerce.dto.request_dto.category_dto.CategoryRequestDto;
import com.ecommerce.dto.response_dto.category_dto.CategoryResponseDto;
import com.ecommerce.dto.response_dto.message_dto.ApiResponseDto;
import com.ecommerce.models.category.Category;
import com.ecommerce.service.category_service.CategoryService;
import com.ecommerce.utils.service_utils.CategoryUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ApiResponseDto<String> addCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto){
        return new ApiResponseDto<>(
                HttpStatus.CREATED.value(),
                "Category Added SuccessFully",
                categoryService.addCategory(categoryRequestDto.getName(),categoryRequestDto.getParentId())
        );
    }

    @GetMapping("/{categoryId}")
    public ApiResponseDto<CategoryResponseDto> getCategoryById(@PathVariable UUID categoryId){
        return new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "",
                categoryService.getCategoryById(categoryId)
        );
    }

    @GetMapping
    public ApiResponseDto<List<CategoryResponseDto>> getAllCategories(
            @RequestParam(value = "max", defaultValue = "10") int max,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "order", defaultValue = "id") String order,
            @RequestParam(value = "query", defaultValue = "") String query
    ){
        Map<String, Object> filters = CategoryUtils.parseQuery(query);
        return new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "Categories List",
                categoryService.getAllParentCategory(max,offset,sort,order,filters)
        );
    }

}
