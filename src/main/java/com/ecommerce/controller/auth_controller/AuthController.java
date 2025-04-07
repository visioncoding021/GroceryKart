package com.ecommerce.controller.auth_controller;

import com.ecommerce.dto.request_dto.AuthRequestDTO;
import com.ecommerce.dto.request_dto.ForgotPasswordDTO;
import com.ecommerce.service.register_service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO authRequestDTO, HttpServletRequest request, HttpServletResponse response) throws BadRequestException {
        return ResponseEntity.ok().body(userService.loginUser(authRequestDTO.getEmail(), authRequestDTO.getPassword(),request,response));
    }

    @PostMapping("/get-access-token")
    public ResponseEntity<String> getAccessToken(@RequestHeader("Authorization") String refreshToken, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAccessToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token, HttpServletRequest request,HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.logoutUser(token,request,response));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) throws MessagingException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.forgotPassword(email));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token , @RequestBody ForgotPasswordDTO forgotPasswordDTO) throws MessagingException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.resetPassword(token,forgotPasswordDTO));
    }
}
