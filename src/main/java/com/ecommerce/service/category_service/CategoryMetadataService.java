package com.ecommerce.service.category_service;

import com.ecommerce.dto.request_dto.category_dto.CategoryMetadataFieldRequestDto;
import com.ecommerce.dto.response_dto.category_dto.CategoryMetadataFieldResponseDto;
import com.ecommerce.models.category.CategoryMetadataField;
import com.ecommerce.repository.category_repos.CategoryMetadataFieldRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryMetadataService {

    @Autowired
    private CategoryMetadataFieldRepository categoryMetadataFieldRepository;

    public CategoryMetadataFieldResponseDto createField(CategoryMetadataFieldRequestDto categoryMetadataFieldRequestDto) {
        if (categoryMetadataFieldRepository.existsByName(categoryMetadataFieldRequestDto.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Field name already exists");
        }

        CategoryMetadataField field = new CategoryMetadataField();
        field.setName(categoryMetadataFieldRequestDto.getName());

        CategoryMetadataField categoryMetadataField = categoryMetadataFieldRepository.save(field);
        return new CategoryMetadataFieldResponseDto(categoryMetadataField.getId(), categoryMetadataField.getName());
    }

    public List<CategoryMetadataFieldResponseDto> getAllMetadataFields(){
        List<CategoryMetadataFieldResponseDto> responseDtoList = new ArrayList<>();
        List<CategoryMetadataField> listOfFields =  categoryMetadataFieldRepository.findAll();
        for(CategoryMetadataField categoryMetadataField : listOfFields){
            CategoryMetadataFieldResponseDto categoryMetadataFieldResponseDto = new CategoryMetadataFieldResponseDto();
            BeanUtils.copyProperties(categoryMetadataField,categoryMetadataFieldResponseDto);
            responseDtoList.add(categoryMetadataFieldResponseDto);
        }
        return responseDtoList;
    }
}
