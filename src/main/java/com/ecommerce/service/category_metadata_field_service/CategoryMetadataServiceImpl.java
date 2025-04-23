package com.ecommerce.service.category_metadata_field_service;

import com.ecommerce.dto.request_dto.category_dto.CategoryMetadataFieldRequestDto;
import com.ecommerce.dto.response_dto.category_dto.CategoryMetadataFieldResponseDto;
import com.ecommerce.models.category.CategoryMetadataField;
import com.ecommerce.repository.category_repos.CategoryMetadataFieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class CategoryMetadataServiceImpl implements CategoryMetadataService{

    @Autowired
    private CategoryMetadataFieldRepository categoryMetadataFieldRepository;

    @Override
    public CategoryMetadataFieldResponseDto createField(CategoryMetadataFieldRequestDto categoryMetadataFieldRequestDto) {
        if (categoryMetadataFieldRepository.existsByNameIgnoreCase(categoryMetadataFieldRequestDto.getName().trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Metadata field name already exists with name +" + categoryMetadataFieldRequestDto.getName());
        }

        CategoryMetadataField field = new CategoryMetadataField();
        field.setName(categoryMetadataFieldRequestDto.getName().trim());

        CategoryMetadataField categoryMetadataField = categoryMetadataFieldRepository.save(field);
        return new CategoryMetadataFieldResponseDto(categoryMetadataField.getId(), categoryMetadataField.getName());
    }

    @Override
    public Page<CategoryMetadataField> getAllMetadataFields(int max, int offset, String sort, String order, Map<String,Object> filters){

        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, max, Sort.by(direction, sort));

        String name = filters.containsKey("name") ? (String) filters.get("name") : null;
        UUID id = filters.containsKey("id") ? UUID.fromString((String) filters.get("id")) : null;

        if(name==null && id==null) return categoryMetadataFieldRepository.findAll(pageable);

        System.out.println(filters.toString());

        return categoryMetadataFieldRepository.findByNameOrId(name, id, pageable);
    }
}
