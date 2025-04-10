package com.ecommerce.controller.auth_controller;

import com.ecommerce.dto.request_dto.AuthRequestDto;
import com.ecommerce.dto.request_dto.ForgotPasswordDto;
import com.ecommerce.dto.response_dto.message_dto.ApiResponseDto;
import com.ecommerce.dto.response_dto.message_dto.MessageResponseDto;
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
    public ResponseEntity<ApiResponseDto<String>> login(@Valid @RequestBody AuthRequestDto authRequestDTO, HttpServletRequest request, HttpServletResponse response) throws BadRequestException {
        Object token = userService.loginUser(authRequestDTO.getEmail(), authRequestDTO.getPassword(), request, response);
        ApiResponseDto<String> responseDto = new ApiResponseDto<String>(
                HttpStatus.OK.value(),
                "Login successful with access token provided ",
                token
        );
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/get-access-token")
    public ResponseEntity<ApiResponseDto<String>> getAccessToken(@RequestHeader("Authorization") String refreshToken, HttpServletRequest request) {
        String accessToken = userService.getAccessToken(request);
        ApiResponseDto<String> responseDto = new ApiResponseDto<String>(
                HttpStatus.OK.value(),
                "Access token generated successfully",
                accessToken
        );
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponseDto> logout(@RequestHeader("Authorization") String token, HttpServletRequest request, HttpServletResponse response) {
        String result = userService.logoutUser(token, request, response);
        MessageResponseDto responseDto = new MessageResponseDto(
                HttpStatus.OK.value(),
                result
        );
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponseDto> forgotPassword(@RequestParam String email) throws MessagingException {
        String message = userService.forgotPassword(email);
        MessageResponseDto responseDto = new MessageResponseDto(
                HttpStatus.OK.value(),
                message
        );
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<MessageResponseDto> resetPassword(@RequestParam String token, @RequestBody ForgotPasswordDto forgotPasswordDTO) throws MessagingException {
        String message = userService.resetPassword(token, forgotPasswordDTO);
        MessageResponseDto responseDto = new MessageResponseDto(
                HttpStatus.OK.value(),
                message
        );
        return ResponseEntity.ok(responseDto);
    }
}
