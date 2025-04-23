package com.ecommerce.utils.service_utils;

import com.ecommerce.dto.response_dto.category_dto.CategoryMetadataFieldResponseDto;
import com.ecommerce.dto.response_dto.message_dto.PaginatedResponseDto;
import com.ecommerce.dto.response_dto.product_dto.ProductResponseDto;
import com.ecommerce.models.category.CategoryMetadataField;
import com.ecommerce.models.product.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProductUtils {
    public static Map<String, String> parseMetadata(String metadataJson) {
        Map<String, String> metadataMap = new HashMap<>();

        // Remove curly braces and split by comma
        String cleanedJson = metadataJson.replaceAll("[{}\"]", "").trim();
        String[] keyValuePairs = cleanedJson.split(",");

        for (String pair : keyValuePairs) {
            // Split each pair by colon to separate key and value
            String[] keyValue = pair.split(":");

            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();

                // Put key-value pair in map
                metadataMap.put(key, value);
            }
        }

        return metadataMap;
    }


    public static Map<String, String> parseQuery(String query) {
        Map<String, String> filters = new HashMap<>();

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

    public static PaginatedResponseDto<List<?>> getProductPaginatedResponse(List<ProductResponseDto> responseDtos, Page<Product> products) {

        return new PaginatedResponseDto<>(
                HttpStatus.OK.value(),
                "All metadata Fields are fetched",
                responseDtos,
                products.getTotalElements(),
                products.getTotalPages(),
                products.getSize(),
                products.getNumber()
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

    public static Specification<Product> getProductFilters(Map<String, String> filters,UUID sellerId){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> listOfPredicate = new ArrayList<>();

            if (sellerId != null) {
                listOfPredicate.add(criteriaBuilder.equal(root.get("seller").get("id"), sellerId));
            }

            if(filters.containsKey("name") && !((String) filters.get("name")).isBlank()){
                listOfPredicate.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),"%" + ((String) filters.get("name")).toLowerCase() + "%"));
            }

            if(filters.containsKey("id") && !((String) filters.get("id")).isBlank()){
                UUID id = UUID.fromString((String) filters.get("id"));
                listOfPredicate.add(criteriaBuilder.equal(root.get("id"), id));
            }

            if (filters.containsKey("brand") && !((String) filters.get("brand")).isBlank()){
                UUID brand = UUID.fromString((String) filters.get("brand"));
                listOfPredicate.add(criteriaBuilder.equal(root.get("parent").get("id"),brand));
            }

            return criteriaBuilder.and(listOfPredicate.toArray(new Predicate[0]));
        };
    }
}
