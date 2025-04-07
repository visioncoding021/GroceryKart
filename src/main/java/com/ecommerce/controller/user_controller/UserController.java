package com.ecommerce.controller.user_controller;

import com.ecommerce.dto.request_dto.AuthRequestDTO;
import com.ecommerce.dto.request_dto.CustomerRequestDTO;
import com.ecommerce.dto.request_dto.ForgotPasswordDTO;
import com.ecommerce.dto.request_dto.SellerRequestDTO;
import com.ecommerce.models.user.Customer;
import com.ecommerce.models.user.Seller;
import com.ecommerce.service.user_service.TokenService;
import com.ecommerce.service.user_service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/test")
    public String test() {
        return "User Controller is working";
    }

    @PostMapping(value = {"/register","/register/customer"})
    public ResponseEntity<Customer> registerUser(@Valid @RequestBody CustomerRequestDTO customerRequestDTO) throws MessagingException {
        Customer customer = userService.registerCustomer(customerRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    @PostMapping("/register/seller")
    public ResponseEntity<Seller> registerSeller(@Valid @RequestBody SellerRequestDTO sellerRequestDTO) throws MessagingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerSeller(sellerRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO authRequestDTO,HttpServletRequest request, HttpServletResponse response) throws BadRequestException {
        return ResponseEntity.ok().body(userService.loginUser(authRequestDTO.getEmail(), authRequestDTO.getPassword(),request,response));
    }

    @PostMapping("/resend-activation-token")
    public ResponseEntity<String> resendActivationToken(@RequestParam String email) throws MessagingException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.resendActivationLink(email));
    }

    @PutMapping("/activate")
    public ResponseEntity<String> activateUser(@RequestParam String token) throws MessagingException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.activateUser(token));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) throws MessagingException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.forgotPassword(email));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token , @RequestBody ForgotPasswordDTO forgotPasswordDTO) throws MessagingException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.resetPassword(token,forgotPasswordDTO));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token, HttpServletRequest request,HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.logoutUser(token,request,response));
    }

}
