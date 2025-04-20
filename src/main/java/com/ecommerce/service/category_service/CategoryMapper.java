package com.ecommerce.service.category_service;

import com.ecommerce.dto.response_dto.category_dto.*;
import com.ecommerce.models.category.Category;
import com.ecommerce.models.category.CategoryMetadataFieldValues;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
