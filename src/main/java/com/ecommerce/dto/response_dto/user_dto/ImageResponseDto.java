package com.ecommerce.dto.response_dto.user_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Getter
@Setter
public class ImageResponseDto {
    private String imageName;
    private String imageType;
    private String base64Image;
    private long size;

    public ImageResponseDto(String imageName, String imageType, InputStream imageStream,long size) throws IOException {
        this.imageName = imageName;
        this.imageType = imageType;
        this.base64Image = encodeInputStreamToBase64(imageStream);
        this.size=size;
    }

    private String encodeInputStreamToBase64(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;

        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }

        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(byteArray);
    }

}


