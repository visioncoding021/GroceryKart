package com.ecommerce.controller.register_controller;

import com.ecommerce.dto.request_dto.CustomerRequestDto;
import com.ecommerce.dto.request_dto.SellerRequestDto;
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

    @PostMapping(value = {"/register","/register/customer"})
    public ResponseEntity<Customer> registerUser(@Valid @RequestBody CustomerRequestDto customerRequestDTO) throws MessagingException {
        Customer customer = userService.registerCustomer(customerRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    @PostMapping("/register/seller")
    public ResponseEntity<Seller> registerSeller(@Valid @RequestBody SellerRequestDto sellerRequestDTO) throws MessagingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerSeller(sellerRequestDTO));
    }

    @PostMapping("/resend-activation-token")
    public ResponseEntity<String> resendActivationToken(@RequestParam String email) throws MessagingException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.resendActivationLink(email));
    }

    @PutMapping("/activate")
    public ResponseEntity<String> activateUser(@RequestParam String token) throws MessagingException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.activateUser(token));
    }
}
