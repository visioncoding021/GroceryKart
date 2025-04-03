package com.ecommerce.controller.user_controller;

import com.ecommerce.dto.request_dto.CustomerRequestDTO;
import com.ecommerce.dto.request_dto.SellerRequestDTO;
import com.ecommerce.models.user.Customer;
import com.ecommerce.models.user.Seller;
import com.ecommerce.service.user_service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/test")
    public String test() {
        return "User Controller is working";
    }

    @PostMapping(value = {"/register","/register/customer"})
    public ResponseEntity<Customer> registerUser(@Valid @RequestBody CustomerRequestDTO customerRequestDTO) throws MessagingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerCustomer(customerRequestDTO));
    }

    @PostMapping("/register/seller")
    public ResponseEntity<Seller> registerSeller(@Valid @RequestBody SellerRequestDTO sellerRequestDTO) throws MessagingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerSeller(sellerRequestDTO));
    }

    @PutMapping("/activate")
    public ResponseEntity<String> activateUser(@RequestParam String token) throws MessagingException {
        if(!userService.activateUser(token))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        return ResponseEntity.status(HttpStatus.OK).body("User activated successfully");
    }

}
