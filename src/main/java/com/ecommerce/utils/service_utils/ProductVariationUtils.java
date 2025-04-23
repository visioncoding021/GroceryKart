package com.ecommerce.utils.service_utils;

import com.ecommerce.dto.response_dto.message_dto.PaginatedResponseDto;
import com.ecommerce.dto.response_dto.product_dto.ProductResponseDto;
import com.ecommerce.dto.response_dto.product_variation_dto.ProductVariationResponseDto;
import com.ecommerce.models.product.Product;
import com.ecommerce.models.product.ProductVariation;
import jakarta.persistence.criteria.Predicate;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProductVariationUtils {
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

    public static PaginatedResponseDto<List<ProductVariationResponseDto>> getProductVariationPaginatedResponse(List<ProductVariationResponseDto> responseDtos, Page<ProductVariation> products) {

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

    public static Map<String, String> validateProductRequestParams(
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

    public static Specification<ProductVariation> getProductVariationFilters(Map<String, String> filters,UUID productId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (productId.toString() != null && !productId.toString().isBlank()) {
                predicates.add(cb.equal(root.get("product").get("id"), productId));
            }

            if (filters.containsKey("id") && !filters.get("id").isBlank()) {
                UUID id = UUID.fromString(filters.get("id"));
                predicates.add(cb.equal(root.get("id"), id));
            }

            if (filters.containsKey("quantity") && !filters.get("quantity").isBlank()) {
                predicates.add(cb.equal(root.get("quantityAvailable"), filters.get("quantity")));
            }

            if (filters.containsKey("price") && !filters.get("price").isBlank()) {
                try {
                    Double price = Double.parseDouble(filters.get("price"));
                    predicates.add(cb.equal(root.get("price"), price));
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Invalid price value in query string");
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
