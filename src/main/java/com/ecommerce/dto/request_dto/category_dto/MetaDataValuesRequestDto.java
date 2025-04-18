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
public class MetaDataValuesRequestDto {

    @NotNull(message = "Metadata Field Id Id is required")
    private UUID metadataFieldId;

    @NotNull(message = "List cant be empty")
    private List<String> value;
}
