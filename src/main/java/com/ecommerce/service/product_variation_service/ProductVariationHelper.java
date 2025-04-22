package com.ecommerce.service.product_variation_service;

import com.ecommerce.models.category.Category;
import com.ecommerce.models.category.CategoryMetadataFieldValues;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductVariationHelper {
    public static List<CategoryMetadataFieldValues> getMetadataFieldValues(Category category){
        List<CategoryMetadataFieldValues> metadataFieldValues = new ArrayList<>();
        while (category!=null){
            List<CategoryMetadataFieldValues> values = category.getCategoryMetadataFieldValues();
            if(values!=null){
                metadataFieldValues.addAll(values);
            }
            category = category.getParent();
        }
        return metadataFieldValues;
    }
}
