package com.ecommerce.dto.request_dto.pagination_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserListRequestDto {
    private int pageSize = 10;
    private int pageOffset = 0;
    private String sort = "id";
    private String emailFilter;
}
