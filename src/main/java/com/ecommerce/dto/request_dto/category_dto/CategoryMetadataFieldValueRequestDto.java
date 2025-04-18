package com.ecommerce.dto.request_dto.category_dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CategoryMetadataFieldValueRequestDto {

    @NotNull(message = "Category Id is required")
    private UUID categoryId;

    @NotNull(message = "Metadata Field Id is required")
    private List<MetaDataValuesRequestDto> metadataFields;
}
