package com.ecommerce.controller.admin_controller;

import com.ecommerce.dto.request_dto.UserListRequestDto;
import com.ecommerce.dto.response_dto.CustomerResponseDto;
import com.ecommerce.dto.response_dto.PaginatedResponseDto;
import com.ecommerce.dto.response_dto.SellerResponseDto;
import com.ecommerce.service.admin_service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/customers")
    public ResponseEntity<PaginatedResponseDto<List<CustomerResponseDto>>> getCustomers(
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "pageOffset", defaultValue = "0") int pageOffset,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "email", required = false) String email
    ) {
        UserListRequestDto request = new UserListRequestDto();
        request.setPageSize(pageSize);
        request.setPageOffset(pageOffset);
        request.setEmailFilter(email);
        request.setSort(sort);

        return ResponseEntity.ok(adminService.getAllCustomers(request));
    }


    @GetMapping("/sellers")
    public ResponseEntity<PaginatedResponseDto<List<SellerResponseDto>>> getSellers(
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "pageOffset", defaultValue = "0") int pageOffset,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "email", required = false) String email
    ) {
        UserListRequestDto request = new UserListRequestDto();
        request.setPageSize(pageSize);
        request.setPageOffset(pageOffset);
        request.setEmailFilter(email);
        request.setSort(sort);

        return ResponseEntity.ok(adminService.getAllSellers(request));
    }
}
