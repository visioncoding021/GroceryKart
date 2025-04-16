package com.ecommerce.service.image_service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ImageServiceImpl implements ImageService{

    @Override
    public String uploadImage(String path, UUID id, MultipartFile file) throws IOException {

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
        String fileName = id.toString().concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
        String filePath = path + File.separator + fileName;

        File folder = new File(path);
        if(!folder.exists()) {
            folder.mkdirs();
        }

        File file1 = new File(filePath);
        if(file1.exists()) {
            file1.delete();
        }

        Files.copy(file.getInputStream(), Paths.get(filePath));

        return fileName;
    }

    @Override
    public void deleteImage(String path, UUID id) throws FileNotFoundException {
        String filePath = path + File.separator + id.toString()+".png";
        File file = new File(filePath);
        System.out.println(filePath);
        if (file.exists()) {
            file.delete();
        }else throw new FileNotFoundException("Image not found");
    }

    @Override
    public FileInputStream getResource(String path, UUID id) throws FileNotFoundException {
        String filePath = path + File.separator + id+".png";
        File file = new File(filePath);

        if (!file.exists()) {
            throw new FileNotFoundException("File not found at path: " + filePath);
        }

        return new FileInputStream(file);
    }
}
