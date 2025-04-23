package com.ecommerce.service.image_service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public interface ImageService {

    public String uploadImage(String path, UUID id, MultipartFile file) throws IOException ;

    public void deleteImage(String path, UUID id) throws FileNotFoundException ;

    public FileInputStream getResource(String path, UUID id) throws FileNotFoundException ;

    public String uploadMultipleImages(String path, UUID id, List<MultipartFile> files) throws IOException ;

    public List<String> getAllImages(String path, UUID id) throws FileNotFoundException ;

}
