package com.ecommerce.service.category_service;

import com.ecommerce.dto.response_dto.category_dto.*;
import com.ecommerce.models.category.Category;
import com.ecommerce.models.category.CategoryMetadataFieldValues;
import com.ecommerce.models.product.Product;
import com.ecommerce.models.product.ProductVariation;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class CategoryMapper {

    public ParentCategoryResponseDto mapParentHierarchyAndMetadataFieldValues(Category parent, CategoryResponseDto categoryResponseDto) {
        ParentCategoryResponseDto root = null;
        ParentCategoryResponseDto currentDto = null;

        if(categoryResponseDto.getFields()==null){
            categoryResponseDto.setFields(new ArrayList<>());
        }

        while (parent != null) {
            ParentCategoryResponseDto temp = new ParentCategoryResponseDto();
            BeanUtils.copyProperties(parent, temp);

            List<CategoryMetadataFieldValueResponseDto> fieldsResponseDtos = mapFields(parent);

            System.out.println(fieldsResponseDtos.toString());

            if(!fieldsResponseDtos.isEmpty()){
                List<CategoryMetadataFieldValueResponseDto> currentList = categoryResponseDto.getFields();
                currentList.addAll(fieldsResponseDtos);
                categoryResponseDto.setFields(currentList);
                System.out.println(categoryResponseDto.getFields());
            }

            if (root == null) {
                root = temp;
            } else {
                currentDto.setParent(temp);
            }

            currentDto = temp;
            parent = parent.getParent();
        }
        return root;
    }

    public List<CategoryMetadataFieldValueResponseDto> mapParentFields(Category category,CategoryFiltersResponseDto currentDto){
        List<CategoryMetadataFieldValueResponseDto> fields = new ArrayList<>();
        while (category != null) {
            List<CategoryMetadataFieldValueResponseDto> fieldsResponseDtos = mapFields(category);

            System.out.println(fieldsResponseDtos.toString());

            if(!fieldsResponseDtos.isEmpty()){
                List<CategoryMetadataFieldValueResponseDto> currentList = currentDto.getMetadata();
                currentList.addAll(fieldsResponseDtos);
                currentDto.setMetadata(currentList);
                System.out.println(currentDto.getMetadata());
            }
            category = category.getParent();
        }
        return fields;
    }

    public Set<String> mapBrands(Category category) {
        return mapBrandFromLeaf(category);
    }

    private Set<String> mapBrandFromLeaf(Category category) {
        List<Category> subCategories = category.getSubCategories();
        if (subCategories==null || subCategories.isEmpty()) {
            List<Product> products = category.getProducts();
            Set<String> brands = new HashSet<>();
            for (Product product : products) {
                if (!brands.contains(product.getBrand())) {
                    brands.add(product.getBrand());
                }
            }
            return brands;
        }
        Set<String> brands = new HashSet<>();
        for (Category subCategory : subCategories) {
            brands.addAll(mapBrandFromLeaf(subCategory));
        }
        return brands;
    }

    public Double getMinimumPriceFromCategory(Category category) {
        double minimumPrice = Double.MAX_VALUE;

        List<Category> subCategories = category.getSubCategories();
        if (subCategories == null || subCategories.isEmpty()) {
            for (Product product : category.getProducts()) {
                for (ProductVariation productVariation : product.getProductVariations()) {
                    minimumPrice = Math.min(minimumPrice, productVariation.getPrice());
                }
            }
            return minimumPrice;
        }

        for (Category subCategory : subCategories) {
            minimumPrice = Math.min(minimumPrice, getMinimumPriceFromCategory(subCategory));
        }

        return minimumPrice;
    }

    public Double getMaximumPriceFromCategory(Category category) {
        double maximumPrice = Double.NEGATIVE_INFINITY;

        List<Category> subCategories = category.getSubCategories();
        if (subCategories == null || subCategories.isEmpty()) {
            for (Product product : category.getProducts()) {
                for (ProductVariation productVariation : product.getProductVariations()) {
                    maximumPrice = Math.max(maximumPrice, productVariation.getPrice());
                }
            }
            return maximumPrice;
        }

        for (Category subCategory : subCategories) {
            maximumPrice = Math.max(maximumPrice, getMaximumPriceFromCategory(subCategory));
        }

        return maximumPrice;
    }


    public List<ChildrenCategoryResponseDto> mapChildren(Category category) {
        List<ChildrenCategoryResponseDto> children = new ArrayList<>();

        if (category.getSubCategories() != null) {
            for (Category subCategory : category.getSubCategories()) {
                ChildrenCategoryResponseDto dto = new ChildrenCategoryResponseDto();
                BeanUtils.copyProperties(subCategory, dto);
                children.add(dto);
            }
        }

        return children;
    }

    public List<CategoryMetadataFieldValueResponseDto> mapFields(Category category){
        List<CategoryMetadataFieldValueResponseDto> fields = new ArrayList<>();
        if(category.getCategoryMetadataFieldValues()!=null){
            for (CategoryMetadataFieldValues categoryMetadataFieldValues : category.getCategoryMetadataFieldValues()){
                CategoryMetadataFieldValueResponseDto categoryMetadataFieldValueResponseDto = new CategoryMetadataFieldValueResponseDto();
                BeanUtils.copyProperties(categoryMetadataFieldValues,categoryMetadataFieldValueResponseDto);
                categoryMetadataFieldValueResponseDto.setName(categoryMetadataFieldValues.getCategoryMetadataField().getName());
                fields.add(categoryMetadataFieldValueResponseDto);
            }
        }
        return fields;
    }



    public ParentCategoryResponseDto mapParentHierarchyAndMetadataFieldValuesForLeaf(Category parent, LeafCategoryResponseDto categoryResponseDto) {
        ParentCategoryResponseDto root = null;
        ParentCategoryResponseDto currentDto = null;

        if(categoryResponseDto.getFields()==null){
            categoryResponseDto.setFields(new ArrayList<>());
        }

        while (parent != null) {
            ParentCategoryResponseDto temp = new ParentCategoryResponseDto();
            BeanUtils.copyProperties(parent, temp);

            List<CategoryMetadataFieldValueResponseDto> fieldsResponseDtos = mapFields(parent);

            if(!fieldsResponseDtos.isEmpty()){
                List<CategoryMetadataFieldValueResponseDto> currentList = categoryResponseDto.getFields();
                currentList.addAll(fieldsResponseDtos);
                categoryResponseDto.setFields(currentList);
                System.out.println(categoryResponseDto.getFields());
            }

            if (root == null) {
                root = temp;
            } else {
                currentDto.setParent(temp);
            }

            currentDto = temp;
            parent = parent.getParent();
        }
        return root;
    }
}
