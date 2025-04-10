package com.ecommerce.controller.register_controller;

import com.ecommerce.dto.request_dto.CustomerRequestDto;
import com.ecommerce.dto.request_dto.SellerRequestDto;
import com.ecommerce.dto.response_dto.ApiResponseDto;
import com.ecommerce.models.user.Customer;
import com.ecommerce.models.user.Seller;
import com.ecommerce.service.register_service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class RegisterController {

    @Autowired
    private UserService userService;

    @PostMapping(value = {"/register", "/register/customer"})
    public ResponseEntity<ApiResponseDto> registerUser(@Valid @RequestBody CustomerRequestDto customerRequestDTO) throws MessagingException {
        Customer customer = userService.registerCustomer(customerRequestDTO);
        ApiResponseDto response = new ApiResponseDto(
                HttpStatus.CREATED.value(),
                "Customer registered successfully",
                customer
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/register/seller")
    public ResponseEntity<ApiResponseDto> registerSeller(@Valid @RequestBody SellerRequestDto sellerRequestDTO) throws MessagingException {
        Seller seller = userService.registerSeller(sellerRequestDTO);
        ApiResponseDto response = new ApiResponseDto(
                HttpStatus.CREATED.value(),
                "Seller registered successfully and email has send to the seller",
                seller
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/resend-activation-token")
    public ResponseEntity<ApiResponseDto> resendActivationToken(@RequestParam String email) throws MessagingException {
        String message = userService.resendActivationLink(email);
        ApiResponseDto response = new ApiResponseDto(
                HttpStatus.OK.value(),
                "Activation token resent successfully",
                message
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/activate")
    public ResponseEntity<ApiResponseDto> activateUser(@RequestParam String token) throws MessagingException {
        String message = userService.activateUser(token);
        ApiResponseDto response = new ApiResponseDto(
                HttpStatus.OK.value(),
                "User activated successfully",
                message
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
