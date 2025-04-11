package com.ecommerce.service.profile_service;

import com.ecommerce.dto.response_dto.user_dto.ImageResponseDto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ProfileHelper {
    public static ImageResponseDto getImageUrl(FileInputStream imageStream,String id){
        try{
            File file = new File("/images/users/"+id+".png");
            return new ImageResponseDto(
                    file.getName(),
                    Files.probeContentType(file.toPath()),
                    imageStream,
                    file.length()
            );
        }catch (IOException e) {
            throw new RuntimeException("Error while converting image to base64", e);
        }

    }

    public static String getImageUrl(String imageName, String bucketName) {
        return "https://" + bucketName + ".s3.amazonaws.com/" + imageName;
    }
}
