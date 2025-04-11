package com.ecommerce.service.profile_service;

import com.ecommerce.dto.request_dto.AddressRequestDto;
import com.ecommerce.dto.response_dto.user_dto.AddressResponseDto;
import com.ecommerce.dto.response_dto.user_dto.CustomerProfileResponseDto;
import com.ecommerce.dto.response_dto.user_dto.SellerProfileResponseDto;
import com.ecommerce.exception.user.UserNotFoundException;
import com.ecommerce.models.user.User;
import com.ecommerce.repository.user_repos.AddressRepository;
import com.ecommerce.repository.user_repos.UserRepository;
import com.ecommerce.service.image_service.ImageService;
import com.ecommerce.utils.jwt_utils.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class ProfileService {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ProfileImageService profileImageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public CustomerProfileResponseDto getCustomerProfile(String email) throws FileNotFoundException {
        return userProfileService.getCustomerProfile(email);
    }

    public SellerProfileResponseDto getSellerProfile(String email) throws FileNotFoundException {
        return userProfileService.getSellerProfile(email);
    }

    public String uploadProfileImage(String email, MultipartFile file) throws IOException {
        return profileImageService.uploadProfileImage(email, file);
    }

    public String deleteProfileImage(String email) throws FileNotFoundException {
        return profileImageService.deleteProfileImage(email);
    }

    public String updateAddress(UUID addressId, AddressRequestDto addressRequestDto, HttpServletResponse response) throws BadRequestException, MessagingException {
        return addressService.updateAddress(addressId,addressRequestDto,response);
    }

    public List<AddressResponseDto> getAllAddresses(HttpServletRequest request) {
        String email = JwtUtil.extractEmail(request.getHeader("Authorization").substring(7));

        return addressService.getAllAddresses(customerId);
    }


    public String updatePassword(String email, String newPassword, String confirmPassword) throws BadRequestException {
        if (!newPassword.equals(confirmPassword)) throw new BadRequestException("Passwords do not match");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "Password updated successfully";
    }


}
