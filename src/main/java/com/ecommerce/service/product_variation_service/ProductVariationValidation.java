package com.ecommerce.service.product_variation_service;

import com.ecommerce.dto.request_dto.product_dto.ProductVariationRequestDto;
import com.ecommerce.models.category.CategoryMetadataFieldValues;
import com.ecommerce.models.product.Product;
import com.ecommerce.repository.product_repos.ProductRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class ProductVariationValidation {

    @Autowired
    private ProductRepository productRepository;

    public Product getProductById(UUID productId) throws BadRequestException {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product not found with ID: " + productId+" . So variation can't be added"));
    }

    public void isMetadataValidForProductVariation(Map<String,String> metadata, List<CategoryMetadataFieldValues> categoryMetadataFieldValues) throws BadRequestException {
        for(String fieldName : metadata.keySet()){
            boolean isFieldPresent = false;
            for(CategoryMetadataFieldValues categoryMetadataFieldValue : categoryMetadataFieldValues){
                if(categoryMetadataFieldValue.getCategoryMetadataField().getName().equalsIgnoreCase(fieldName)){
                    isFieldPresent = true;
                    boolean isValueValid = false;
                    for (String value : categoryMetadataFieldValue.getValue()) {
                        if (value.equalsIgnoreCase(metadata.get(fieldName))) {
                            isValueValid = true;
                            break;
                        }
                    }
                    if(!isValueValid) throw new BadRequestException("Product variation can't be added as value "+metadata.get(fieldName)+" is not valid for field "+fieldName);
                    break;
                }
            }
            if(!isFieldPresent) throw new BadRequestException("Product variation can't be added as field name "+fieldName+" doesn't exists in category");
        }
    }

    public void isStructureSameForAllVariations(Set<String> previousProductVariationKeySet, Set<String> currentProductVariationKeySet) throws BadRequestException {
        for (String currentKey : currentProductVariationKeySet) {
            boolean isKeyPresent = false;
            for (String previousKey : previousProductVariationKeySet) {
                System.out.println("Previous Key: " + previousKey);
                if (currentKey.equalsIgnoreCase(previousKey)) {
                    isKeyPresent = true;
                    break;
                }
            }
            if (!isKeyPresent) throw new BadRequestException("Product variation can't be added as field name " + currentKey + " doesn't exists in previous product variation");
        }
    }

}
