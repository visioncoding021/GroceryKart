package com.ecommerce.utils.service_utils;

import com.ecommerce.dto.response_dto.category_dto.CategoryMetadataFieldResponseDto;
import com.ecommerce.dto.response_dto.category_dto.CategoryResponseDto;
import com.ecommerce.dto.response_dto.message_dto.PaginatedResponseDto;
import com.ecommerce.models.category.Category;
import com.ecommerce.models.category.CategoryMetadataField;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.util.*;

public final class CategoryUtils {

    public static PaginatedResponseDto<List<CategoryResponseDto>> getCategoryPaginatedResponse(List<CategoryResponseDto> data, Page<Category> page) {
        return new PaginatedResponseDto<>(
                HttpStatus.OK.value(),
                "All Categories are fetched successfully",
                data,
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber()
        );
    }

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

    public static Map<String, String> validateCategoryRequestParams(
            String order,
            int max,
            int offset
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (order != null && !(order.equalsIgnoreCase("asc") || order.equalsIgnoreCase("desc"))) {
            errors.put("order", "Invalid order value: " + order + " (must be 'asc' or 'desc')");
        }

        if (max <= 0) {
            errors.put("max", "Max must be greater than 0");
        }
        if (offset < 0) {
            errors.put("offset", "Offset must be 0 or greater");
        }

        return errors;
    }

}
