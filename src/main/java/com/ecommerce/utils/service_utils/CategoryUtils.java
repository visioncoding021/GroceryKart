package com.ecommerce.utils.service_utils;

import com.ecommerce.dto.response_dto.category_dto.CategoryMetadataFieldResponseDto;
import com.ecommerce.dto.response_dto.message_dto.PaginatedResponseDto;
import com.ecommerce.models.category.CategoryMetadataField;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CategoryUtils {

    public static Map<String, Object> parseQuery(String query) {
        Map<String, Object> filters = new HashMap<>();

        if (query != null && !query.isBlank()) {
            String[] pairs = query.split(",");

            for (String pair : pairs) {
                String[] parts = pair.split(":", 2);
                if (parts.length == 2) {
                    filters.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
        return filters;
    }

    public static PaginatedResponseDto<List<?>> getCategoryMetaDataPaginatedResponse(Page<CategoryMetadataField> data){
        List<CategoryMetadataFieldResponseDto> responseDtoList = new ArrayList<>();

        for(CategoryMetadataField categoryMetadataField : data.getContent()){
            CategoryMetadataFieldResponseDto categoryMetadataFieldResponseDto = new CategoryMetadataFieldResponseDto();
            BeanUtils.copyProperties(categoryMetadataField,categoryMetadataFieldResponseDto);
            responseDtoList.add(categoryMetadataFieldResponseDto);
        }

        return new PaginatedResponseDto<>(
                HttpStatus.OK.value(),
                "All metadata Fields are fetched",
                responseDtoList,
                data.getTotalElements(),
                data.getTotalPages(),
                data.getSize(),
                data.getNumber()
        );
    }
}
