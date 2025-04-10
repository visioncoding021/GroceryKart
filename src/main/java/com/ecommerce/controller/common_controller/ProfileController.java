package com.ecommerce.controller.common_controller;

import com.ecommerce.dto.response_dto.message_dto.ApiResponseDto;
import com.ecommerce.dto.response_dto.user_dto.AllCustomersResponseDto;
import com.ecommerce.dto.response_dto.user_dto.AllSellersResponseDto;
import com.ecommerce.dto.response_dto.user_dto.CustomerProfileResponseDto;
import com.ecommerce.dto.response_dto.user_dto.SellerProfileResponseDto;
import com.ecommerce.service.profile_service.ProfileService;
import com.ecommerce.utils.jwt_utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/get-profile")
    public ResponseEntity<ApiResponseDto<?>> getProfile(HttpServletRequest request) {
        String accessToken = JwtUtil.getAccessTokenFromHeader(request.getHeader("Authorization"));
        String role = JwtUtil.extractRole(accessToken);
        String email = JwtUtil.extractEmail(accessToken);

        System.out.println("Controller");
        if (role.equals("ROLE_CUSTOMER")) {

            ApiResponseDto<CustomerProfileResponseDto> response = new ApiResponseDto<>(200,
                    "Customer Profile Retrieved Successfully",
                    profileService.getCustomerProfile(email));

            return ResponseEntity.ok(response);
        } else if (role.equals("ROLE_SELLER")) {

            ApiResponseDto<SellerProfileResponseDto> response = new ApiResponseDto<>(200,
                    "Customer Profile Retrieved Successfully",
                    profileService.getSellerProfile(email));

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(new ApiResponseDto<>(400, "Invalid Role", null));
    }
}
