package com.ecommerce.controller.admin_controller;

import com.ecommerce.dto.request_dto.UserListRequestDto;
import com.ecommerce.dto.response_dto.user_dto.AllCustomersResponseDto;
import com.ecommerce.dto.response_dto.message_dto.MessageResponseDto;
import com.ecommerce.dto.response_dto.message_dto.PaginatedResponseDto;
import com.ecommerce.dto.response_dto.user_dto.AllSellersResponseDto;
import com.ecommerce.service.admin_service.AdminService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/customers")
    public ResponseEntity<PaginatedResponseDto<List<AllCustomersResponseDto>>> getCustomers(
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
    public ResponseEntity<PaginatedResponseDto<List<AllSellersResponseDto>>> getSellers(
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

    @PatchMapping("/activate")
    public MessageResponseDto activateUser(@RequestParam UUID id) throws BadRequestException {
        return new MessageResponseDto(200,adminService.activateUser(id));
    }

    @PatchMapping("/deactivate")
    public MessageResponseDto deactivateUser(@RequestParam UUID id) throws BadRequestException {
        return new MessageResponseDto(200, adminService.deactivateUser(id));
    }
}
