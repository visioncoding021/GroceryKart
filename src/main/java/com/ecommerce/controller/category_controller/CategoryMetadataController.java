package com.ecommerce.controller.category_controller;

import com.ecommerce.dto.request_dto.category_dto.CategoryMetadataFieldRequestDto;
import com.ecommerce.dto.response_dto.category_dto.CategoryMetadataFieldResponseDto;
import com.ecommerce.dto.response_dto.message_dto.ApiResponseDto;
import com.ecommerce.service.category_service.CategoryMetadataService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/metadata-fields")
public class CategoryMetadataController {

    @Autowired
    private CategoryMetadataService categoryMetadataService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<?>> createField(
            @Valid @RequestBody CategoryMetadataFieldRequestDto categoryMetadataFieldRequestDto) {
        ApiResponseDto<CategoryMetadataFieldResponseDto> apiResponseDto = new ApiResponseDto<>(
                HttpStatus.CREATED.value(),
                "New Category Metadata Field Created",
                categoryMetadataService.createField(categoryMetadataFieldRequestDto)
        );
        return ResponseEntity.ok(apiResponseDto);
    }


}
