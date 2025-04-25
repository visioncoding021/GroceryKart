package com.ecommerce.dto.response_dto.category_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryFiltersResponseDto {
    List<CategoryMetadataFieldValueResponseDto> metadata;
    Set<String> brands;
    Double minPrice;
    Double maxPrice;

    public List<CategoryMetadataFieldValueResponseDto> getMetadata() {
        return metadata.stream()
                .sorted(Comparator.comparing(CategoryMetadataFieldValueResponseDto::getName))
                .collect(Collectors.toList());
    }

}
