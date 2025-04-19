package com.ecommerce.service.category_service;

import com.ecommerce.dto.request_dto.category_dto.MetaDataValuesRequestDto;
import com.ecommerce.dto.response_dto.category_dto.CategoryResponseDto;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CategoryService {

    public String addCategory(String name, UUID parentId);

    public CategoryResponseDto getCategoryById(UUID id);

    public List<CategoryResponseDto> getAllParentCategory(int max, int offset, String sort, String order, Map<String,Object> filters);

    public String updateCategory(UUID categoryId,String categoryName) throws BadRequestException;

    public String addMetadataFieldWithValues(UUID categoryId,List<MetaDataValuesRequestDto> metaDataValuesRequestDtos) throws BadRequestException;

    public String updateMetadataFieldValues(UUID categoryId, List<MetaDataValuesRequestDto> metaDataValuesRequestDtos) throws BadRequestException;

}
