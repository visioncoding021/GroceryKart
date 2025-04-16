package com.ecommerce.controller.register_controller;

import com.ecommerce.dto.request_dto.user_dto.CustomerRequestDto;
import com.ecommerce.dto.request_dto.user_dto.SellerRequestDto;
import com.ecommerce.dto.response_dto.message_dto.ApiResponseDto;
import com.ecommerce.dto.response_dto.message_dto.MessageResponseDto;
import com.ecommerce.models.user.Customer;
import com.ecommerce.models.user.Seller;
import com.ecommerce.service.register_service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/api/auth")
public class RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    @PostMapping(value = {"/register", "/register/customer"})
    public ResponseEntity<ApiResponseDto<Customer>> registerUser(@Valid @RequestBody CustomerRequestDto customerRequestDTO, HttpServletRequest request) throws MessagingException {
        Locale locale = request.getLocale();
        Customer customer = userService.registerCustomer(customerRequestDTO,locale);
        ApiResponseDto<Customer> response = new ApiResponseDto<Customer>(
                HttpStatus.CREATED.value(),
                messageSource.getMessage("response.register.customer.success", null,locale),
                customer
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/register/seller")
    public ResponseEntity<ApiResponseDto<Seller>> registerSeller(@Valid @RequestBody SellerRequestDto sellerRequestDTO, HttpServletRequest request) throws MessagingException {
        Locale locale = request.getLocale();
        Seller seller = userService.registerSeller(sellerRequestDTO,locale);
        ApiResponseDto<Seller> response = new ApiResponseDto<Seller>(
                HttpStatus.CREATED.value(),
                messageSource.getMessage("response.register.seller.success", null,locale),
                seller
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/resend-activation-token")
    public ResponseEntity<MessageResponseDto> resendActivationToken(@RequestParam String email, HttpServletRequest request) throws MessagingException {
        String message = userService.resendActivationLink(email);
        Locale locale = request.getLocale();
        MessageResponseDto response = new MessageResponseDto(
                HttpStatus.OK.value(),
                message
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/activate")
    public ResponseEntity<MessageResponseDto> activateUser(@RequestParam String token, HttpServletRequest request) throws MessagingException {
        String message = userService.activateUser(token);
        Locale locale = request.getLocale();
        MessageResponseDto response = new MessageResponseDto(
                HttpStatus.OK.value(),
                message
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
