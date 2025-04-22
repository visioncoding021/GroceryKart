package com.ecommerce.service.image_service;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${image.upload.path}")
    private String imageUploadPath;
    @Override
    public String uploadImage(String path, UUID id, MultipartFile file) throws IOException {

        fileValidations(file);

        String originalFileName = Objects.requireNonNull(file.getOriginalFilename()).toLowerCase();
        String fileName = id.toString().concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
        String filePath = imageUploadPath + path + File.separator + fileName;

        File folder = new File(Paths.get(imageUploadPath, path).toString());
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
        String filePath = imageUploadPath + path + File.separator + id.toString()+".png";
        File file = new File(filePath);
        System.out.println(filePath);
        if (file.exists()) {
            file.delete();
        }else throw new FileNotFoundException("Image not found");
    }

    @Override
    public FileInputStream getResource(String path, UUID id) throws FileNotFoundException {
        File folder = new File(Paths.get(imageUploadPath, path).toString());

        if (!folder.exists()) {
            throw new FileNotFoundException("Folder does not exist: " + path);
        }

        File[] matchingFiles = folder.listFiles((dir, name) ->
                name.startsWith(id.toString() + ".") || name.startsWith(id.toString() + "_")
        );

        if (matchingFiles == null || matchingFiles.length == 0) {
            throw new FileNotFoundException("No image found for variation ID: " + id);
        }

        return new FileInputStream(matchingFiles[0]);
    }


    @Override
    public String uploadMultipleImages(String path, UUID id, List<MultipartFile> files) throws IOException {
        File folder = new File(Paths.get(imageUploadPath, path).toString());
        if (!folder.exists()) folder.mkdirs();

        for(MultipartFile file : files) {
            fileValidations(file);
        }

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String originalFileName = Objects.requireNonNull(file.getOriginalFilename()).toLowerCase();
            String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));

            String fileName;
            if (i == 0) {
                fileName = id + extension;
            } else {
                fileName = id + "_" + UUID.randomUUID() + extension;
            }

            String filePath = imageUploadPath + path + File.separator + fileName;

            File destination = new File(filePath);
            if (destination.exists()) destination.delete();

            Files.copy(file.getInputStream(), Paths.get(filePath));

        }

        return "Images uploaded successfully";
    }

    @Override
    public List<String> getAllImages(String path, UUID id) throws FileNotFoundException {
        File folder = new File(Paths.get(imageUploadPath, path).toString());
        if (!folder.exists()) {
            throw new FileNotFoundException("Folder does not exist: " + path);
        }

        File[] matchingFiles = folder.listFiles((dir, name) ->
                name.equals(id.toString()) || name.startsWith(id.toString() + ".") || name.startsWith(id.toString() + "_")
        );

        if (matchingFiles == null || matchingFiles.length == 0) {
            throw new FileNotFoundException("No image found for variation ID: " + id);
        }

        List<String> imagePaths = new ArrayList<>();
        for (File file : matchingFiles) {
            imagePaths.add(file.getPath());
        }

        return imagePaths;
    }

    public String getPrimaryImage(String path, UUID id) throws FileNotFoundException {
        List<String> allImages = getAllImages(path, id);
        return allImages.get(0);
    }

    private void fileValidations(MultipartFile file) throws IOException {
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
    }


}
