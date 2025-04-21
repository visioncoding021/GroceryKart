package com.ecommerce.dto.request_dto.product_dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ProductVariationRequestDto {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotBlank(message = "Metadata must not be blank")
    private String metadata;

    @NotBlank(message = "Quantity is required")
    @Pattern(regexp = "^[0-9]+$", message = "Quantity must be a number")
    private String quantityAvailable;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotNull(message = "Primary image is required")
    private MultipartFile primaryImage;

    private MultipartFile[] secondaryImages;

    public String toString() {
        return "ProductVariationRequestDto{" +
                "productId=" + productId +
                ", metadata=" + metadata +
                ", quantityAvailable='" + quantityAvailable + '\'' +
                ", price=" + price +
                '}';
    }

}
