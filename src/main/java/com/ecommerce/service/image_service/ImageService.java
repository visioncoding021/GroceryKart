package com.ecommerce.service.image_service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
public class ImageService {

    public String uploadImage(String path, String id, MultipartFile file) throws IOException {

        if(file.isEmpty()) {
            throw new FileNotFoundException("Image You are trying to upload is empty");
        }
        Set<String> validImageTypes = new HashSet<>(Arrays.asList("image/jpeg", "image/png", "image/jpg", "image/bmp"));
        if (!validImageTypes.contains(file.getContentType())) {
            throw new FileNotFoundException("Image you are trying to upload is not a valid image");
        }

        String originalFileName = Objects.requireNonNull(file.getOriginalFilename()).toLowerCase();
        if (originalFileName.isEmpty()) {
            throw new FileNotFoundException("Uploaded file does not have a valid filename");
        }
        String fileName = id.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
        String filePath = path + File.separator + fileName;

        File folder = new File(path);
        if(!folder.exists()) {
            folder.mkdir();
        }

        Files.copy(file.getInputStream(), Paths.get(filePath));

        return fileName;
    }

    public InputStream getResource(String path, String id) throws FileNotFoundException {
        String filePath = path + File.separator + id;
        return new FileInputStream(filePath);
    }
}
