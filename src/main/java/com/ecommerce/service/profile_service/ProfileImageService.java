package com.ecommerce.service.profile_service;

import com.ecommerce.exception.user.UserNotFoundException;
import com.ecommerce.models.user.User;
import com.ecommerce.repository.user_repos.UserRepository;
import com.ecommerce.service.image_service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;

@Service
public class ProfileImageService {
    @Autowired
    private ImageService imageService;

    @Autowired
    private UserRepository userRepository;

    public String uploadProfileImage(String email, MultipartFile file) throws IOException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        String fullPath = "/users";
        imageService.uploadImage(fullPath, user.getId(), file);
        return "Profile image uploaded successfully";
    }

    public String deleteProfileImage(String email) throws FileNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        String fullPath =  "/users";
        imageService.deleteImage(fullPath, user.getId());
        return "Profile image deleted successfully";
    }
}
