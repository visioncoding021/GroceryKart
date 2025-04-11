package com.ecommerce.controller.common_controller;

import com.ecommerce.dto.request_dto.AddressRequestDto;
import com.ecommerce.dto.request_dto.ForgotPasswordDto;
import com.ecommerce.dto.response_dto.message_dto.ApiResponseDto;
import com.ecommerce.dto.response_dto.message_dto.MessageResponseDto;
import com.ecommerce.dto.response_dto.user_dto.AllCustomersResponseDto;
import com.ecommerce.dto.response_dto.user_dto.AllSellersResponseDto;
import com.ecommerce.dto.response_dto.user_dto.CustomerProfileResponseDto;
import com.ecommerce.dto.response_dto.user_dto.SellerProfileResponseDto;
import com.ecommerce.service.profile_service.ProfileService;
import com.ecommerce.utils.jwt_utils.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/get-profile")
    public ResponseEntity<ApiResponseDto<?>> getProfile(HttpServletRequest request) throws FileNotFoundException {
        String role = getRoleFromToken(request);
        String email = getEmailFromToken(request);

        System.out.println("Controller");
        if (role.equals("ROLE_CUSTOMER")) {

            ApiResponseDto<CustomerProfileResponseDto> response = new ApiResponseDto<>(200,
                    "Customer Profile Retrieved Successfully",
                    profileService.getCustomerProfile(email));

            return ResponseEntity.ok(response);
        } else if (role.equals("ROLE_SELLER")) {

            ApiResponseDto<SellerProfileResponseDto> response = new ApiResponseDto<>(200,
                    "Seller Profile Retrieved Successfully",
                    profileService.getSellerProfile(email));

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(new ApiResponseDto<>(400, "Invalid Role", null));
    }

    @PostMapping("/upload-profile-photo")
    public ResponseEntity<MessageResponseDto> uploadProfilePhoto(HttpServletRequest request, @RequestParam("image") MultipartFile file) throws IOException {
        String email = getEmailFromToken(request);

        MessageResponseDto messageResponseDto = new MessageResponseDto(
                HttpStatus.OK.value(),
                profileService.uploadProfileImage(email, file)
        );
        return ResponseEntity.ok(messageResponseDto);
    }

    @DeleteMapping("/delete-profile-photo")
    public ResponseEntity<MessageResponseDto> deleteProfilePhoto(HttpServletRequest request) throws FileNotFoundException {
        String email = getEmailFromToken(request);

        MessageResponseDto messageResponseDto = new MessageResponseDto(
                HttpStatus.OK.value(),
                profileService.deleteProfileImage(email)
        );
        return ResponseEntity.ok(messageResponseDto);
    }

    @PatchMapping("update-password")
    public ResponseEntity<MessageResponseDto> updatePassword(HttpServletRequest request,@Valid @RequestBody ForgotPasswordDto forgotPasswordDto) throws BadRequestException {
        String email = getEmailFromToken(request);

        MessageResponseDto messageResponseDto = new MessageResponseDto(
                HttpStatus.OK.value(),
                profileService.updatePassword(email, forgotPasswordDto.getPassword(), forgotPasswordDto.getConfirmPassword())
        );
        return ResponseEntity.ok(messageResponseDto);
    }

    @PutMapping("/update-address/{addressId}")
    public ResponseEntity<MessageResponseDto> updateAddress(@PathVariable UUID addressId, @Valid @RequestBody AddressRequestDto addressRequestDto, HttpServletResponse response) throws BadRequestException, MessagingException {
        MessageResponseDto messageResponseDto = new MessageResponseDto(
                HttpStatus.OK.value(),
                profileService.updateAddress(addressId, addressRequestDto,response)
        );
        return ResponseEntity.ok(messageResponseDto);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/get-all-addresses")
    public ResponseEntity<ApiResponseDto<?>> getAllAddresses(HttpServletRequest request) {
        ApiResponseDto<?> response = new ApiResponseDto<>(200,
                "All Addresses Retrieved Successfully",
                profileService.getAllAddresses(request));

        return ResponseEntity.ok(response);
    }

    private String getRoleFromToken(HttpServletRequest request) {
        String accessToken = JwtUtil.getAccessTokenFromHeader(request.getHeader("Authorization"));
        return JwtUtil.extractRole(accessToken);
    }

    private String getEmailFromToken(HttpServletRequest request) {
        String accessToken = JwtUtil.getAccessTokenFromHeader(request.getHeader("Authorization"));
        return JwtUtil.extractEmail(accessToken);
    }

}
