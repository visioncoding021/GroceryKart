package com.ecommerce.controller.common_controller;

import com.ecommerce.dto.request_dto.user_dto.AddressRequestDto;
import com.ecommerce.dto.request_dto.auth_dto.ForgotPasswordDto;
import com.ecommerce.dto.request_dto.profile_dto.CustomerProfileRequestDto;
import com.ecommerce.dto.request_dto.profile_dto.SellerProfileRequestDto;
import com.ecommerce.dto.request_dto.profile_dto.UserProfileRequestDto;
import com.ecommerce.dto.response_dto.message_dto.ApiResponseDto;
import com.ecommerce.dto.response_dto.message_dto.MessageResponseDto;
import com.ecommerce.dto.response_dto.user_dto.CustomerProfileResponseDto;
import com.ecommerce.dto.response_dto.user_dto.SellerProfileResponseDto;
import com.ecommerce.service.profile_service.ProfileService;
import com.ecommerce.utils.jwt_utils.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.BeanUtils;
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

    @Autowired
    private Validator validator;

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

    @PatchMapping("/update")
    public ResponseEntity<ApiResponseDto<?>> updateProfile(HttpServletRequest request, @RequestBody UserProfileRequestDto userProfileRequestDto) throws BadRequestException, FileNotFoundException {
        String email = getEmailFromToken(request);
        String role = getRoleFromToken(request);
        ApiResponseDto apiResponseDto;

        if (role.equals("ROLE_CUSTOMER")) {
            CustomerProfileRequestDto customerProfileRequestDto = new CustomerProfileRequestDto();
            BeanUtils.copyProperties(userProfileRequestDto,customerProfileRequestDto);
            if(!validator.validate(customerProfileRequestDto).isEmpty()) throw new BadRequestException(""+validator.validate(customerProfileRequestDto).toString());
            apiResponseDto = new ApiResponseDto(
                    HttpStatus.OK.value(),
                    "Customer profile Updated Successfully",
                    profileService.updateCustomerProfile(email, customerProfileRequestDto)
                    );

        } else {
            SellerProfileRequestDto sellerProfileRequestDto = new SellerProfileRequestDto();
            BeanUtils.copyProperties(userProfileRequestDto,sellerProfileRequestDto);
            if(!validator.validate(sellerProfileRequestDto).isEmpty()) throw new BadRequestException(""+validator.validate(sellerProfileRequestDto).toString());
            apiResponseDto = new ApiResponseDto(
                    HttpStatus.OK.value(),
                    "Seller profile Updated Successfully",
                    profileService.updateSellerProfile(email, sellerProfileRequestDto)
            );
        }
        return ResponseEntity.ok(apiResponseDto);
    }

    @PostMapping("/add-address")
    public ResponseEntity<ApiResponseDto<?>> addAddress(@Valid @RequestBody AddressRequestDto addressRequestDto, HttpServletRequest request) throws BadRequestException {
        String email = getEmailFromToken(request);
        String role = getRoleFromToken(request);
        if(!role.equals("ROLE_CUSTOMER")) {
            throw new BadRequestException("Only customers can add new address");
        }
        ApiResponseDto apiResponseDto = new ApiResponseDto(
                HttpStatus.OK.value(),
                "Address added successfully",
                profileService.addAddress(addressRequestDto, email)
        );
        return ResponseEntity.ok(apiResponseDto);
    }

    @DeleteMapping("/delete-address/{addressId}")
    public ResponseEntity<MessageResponseDto> deleteAddress(@PathVariable UUID addressId, HttpServletRequest request) throws BadRequestException {
        String email = getEmailFromToken(request);
        String role = getRoleFromToken(request);
        if(!role.equals("ROLE_CUSTOMER")) {
            throw new BadRequestException("Only customers can add new address");
        }
        MessageResponseDto messageResponseDto = new MessageResponseDto(
                HttpStatus.OK.value(),
                profileService.deleteAddress(addressId, email)
        );
        return ResponseEntity.ok(messageResponseDto);
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
