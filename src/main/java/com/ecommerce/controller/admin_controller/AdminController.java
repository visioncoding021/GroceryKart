package com.ecommerce.controller.admin_controller;

import com.ecommerce.dto.request_dto.pagination_dto.UserListRequestDto;
import com.ecommerce.dto.response_dto.user_dto.AllCustomersResponseDto;
import com.ecommerce.dto.response_dto.message_dto.MessageResponseDto;
import com.ecommerce.dto.response_dto.message_dto.PaginatedResponseDto;
import com.ecommerce.dto.response_dto.user_dto.AllSellersResponseDto;
import com.ecommerce.models.user.Token;
import com.ecommerce.repository.user_repos.TokenRepository;
import com.ecommerce.service.admin_service.AdminService;
import com.ecommerce.service.product_service.ProductService;
import jakarta.mail.MessagingException;
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

    @Autowired
    private ProductService productService;

    @Autowired
    private TokenRepository tokenRepository;

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

    @PutMapping("/unlock/{userId}")
    public MessageResponseDto lockUser(@PathVariable UUID userId) throws BadRequestException, MessagingException {
        return new MessageResponseDto(200, adminService.unlockUser(userId));
    }

    @PutMapping("/product-activate/{productId}")
    public MessageResponseDto activateProduct(@PathVariable UUID productId) throws BadRequestException {
        return new MessageResponseDto(200, productService.activateProduct(productId));
    }

    @PutMapping("/product-deactivate/{productId}")
    public MessageResponseDto deactivateProduct(@PathVariable UUID productId) throws BadRequestException {
        return new MessageResponseDto(200, productService.deactivateProduct(productId));
    }

}
