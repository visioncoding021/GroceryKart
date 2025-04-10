package com.ecommerce.dto.response_dto.message_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponseDto<T> {
    private int status;
    private String message;
    private T data;
    private Long totalElements;
    private Integer totalPages;
    private Integer size;
    private Integer number;
}
