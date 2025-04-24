package com.ecommerce.controller.category_controller;

import com.ecommerce.dto.request_dto.category_dto.CategoryMetadataFieldRequestDto;
import com.ecommerce.dto.response_dto.category_dto.CategoryMetadataFieldResponseDto;
import com.ecommerce.dto.response_dto.message_dto.ApiResponseDto;
import com.ecommerce.dto.response_dto.message_dto.PaginatedResponseDto;
import com.ecommerce.models.category.CategoryMetadataField;
import com.ecommerce.service.category_metadata_field_service.CategoryMetadataService;
import com.ecommerce.utils.service_utils.CategoryUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
                categoryMetadataService.addMetadataField(categoryMetadataFieldRequestDto)
        );
        return ResponseEntity.ok(apiResponseDto);
    }

    @GetMapping
    public PaginatedResponseDto<List<?>> getAllFields(
            @RequestParam(value = "max", defaultValue = "10") int pageSize,
            @RequestParam(value = "offset", defaultValue = "0") int pageOffset,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "order", defaultValue = "id") String order,
            @RequestParam(value = "query", defaultValue = "") String query
    ){
        Map<String, Object> filters = CategoryUtils.parseQuery(query);
        Page<CategoryMetadataField> listOfMetadata = categoryMetadataService.getAllMetadataFields(pageSize,pageOffset,sort,order,filters);

        return CategoryUtils.getCategoryMetaDataPaginatedResponse(listOfMetadata);
    }


}
