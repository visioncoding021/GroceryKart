package com.ecommerce.service.category_metadata_field_service;

import com.ecommerce.dto.request_dto.category_dto.CategoryMetadataFieldRequestDto;
import com.ecommerce.dto.response_dto.category_dto.CategoryMetadataFieldResponseDto;
import com.ecommerce.models.category.CategoryMetadataField;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface CategoryMetadataService {

    public CategoryMetadataFieldResponseDto addMetadataField(CategoryMetadataFieldRequestDto categoryMetadataFieldRequestDto);

    public Page<CategoryMetadataField> getAllMetadataFields(int max, int offset, String sort, String order, Map<String,Object> filters);

}
