package com.ecommerce.controller.category_controller;

import com.ecommerce.dto.request_dto.category_dto.CategoryMetadataFieldValueRequestDto;
import com.ecommerce.dto.request_dto.category_dto.CategoryRequestDto;
import com.ecommerce.dto.request_dto.category_dto.MetaDataValuesRequestDto;
import com.ecommerce.dto.response_dto.category_dto.CategoryResponseDto;
import com.ecommerce.dto.response_dto.message_dto.ApiResponseDto;
import com.ecommerce.dto.response_dto.message_dto.MessageResponseDto;
import com.ecommerce.dto.response_dto.message_dto.PaginatedResponseDto;
import com.ecommerce.service.category_service.CategoryService;
import com.ecommerce.utils.service_utils.CategoryUtils;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ApiResponseDto<String> addCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto){
        return new ApiResponseDto<>(
                HttpStatus.CREATED.value(),
                "Category Added SuccessFully",
                categoryService.addCategory(categoryRequestDto.getName(),categoryRequestDto.getParentId())
        );
    }

    @GetMapping("/{categoryId}")
    public ApiResponseDto<CategoryResponseDto> getCategoryById(@PathVariable UUID categoryId){
        return new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "",
                categoryService.getCategoryById(categoryId)
        );
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<?>> getAllCategories(
            @RequestParam(value = "max", defaultValue = "10") int max,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "order", defaultValue = "asc") String order,
            @RequestParam(value = "query", defaultValue = "") String query
    ) throws BadRequestException {
        final Set<String> VALID_SORT_FIELDS = Set.of("name", "id", "parentId");
        Map<String,String> errors = CategoryUtils.validateCategoryRequestParams(order,max,offset);
        if (!VALID_SORT_FIELDS.contains(sort)) {
            errors.put("Sort" , "Invalid sort field: " + sort);
        }
        if(!errors.isEmpty()){
            throw new BadRequestException(errors.toString());
        }

        Map<String, Object> filters = CategoryUtils.parseQuery(query);
        return ResponseEntity.ok().body(
                categoryService.getAllCategories(max,offset,sort,order,filters)
        );
    }

    @PutMapping("/{categoryId}")
    public MessageResponseDto updateCategory(@PathVariable UUID categoryId,@RequestBody CategoryRequestDto categoryRequestDto) throws BadRequestException {
        return new MessageResponseDto(
                HttpStatus.OK.value(),
                categoryService.updateCategory(categoryId,categoryRequestDto.getName())
        );
    }

    @PostMapping("/category-metadata")
    public MessageResponseDto addMetadataFieldsToCategory(@Valid @RequestBody CategoryMetadataFieldValueRequestDto categoryMetadataFieldValueRequestDto) throws BadRequestException {
        UUID categoryId = categoryMetadataFieldValueRequestDto.getCategoryId();
        List<MetaDataValuesRequestDto> metaDataValuesRequestDtos = categoryMetadataFieldValueRequestDto.getMetadataFields();

        for(MetaDataValuesRequestDto metaDataValuesRequestDto : metaDataValuesRequestDtos){
            UUID metadataFieldId = metaDataValuesRequestDto.getMetadataFieldId();
            List<String> list = new ArrayList<>();
            for(String el : metaDataValuesRequestDto.getValue()){
                el = el.trim();
                if(list.contains(el)) throw new BadRequestException("Elements in list are not unique in Metadata with id " + metadataFieldId);
                list.add(el);
            }
        }

        return new MessageResponseDto(
            HttpStatus.OK.value(),
            categoryService.addMetadataFieldWithValues(categoryId,metaDataValuesRequestDtos)
        );
    }

    @PutMapping("/category-metadata")
    public MessageResponseDto updateMetadataFieldValues(@Valid @RequestBody CategoryMetadataFieldValueRequestDto categoryMetadataFieldValueRequestDto) throws BadRequestException {
        UUID categoryId = categoryMetadataFieldValueRequestDto.getCategoryId();
        List<MetaDataValuesRequestDto> metaDataValuesRequestDtos = categoryMetadataFieldValueRequestDto.getMetadataFields();

        for(MetaDataValuesRequestDto metaDataValuesRequestDto : metaDataValuesRequestDtos){
            UUID metadataFieldId = metaDataValuesRequestDto.getMetadataFieldId();
            List<String> list = new ArrayList<>();
            for(String el : metaDataValuesRequestDto.getValue()){
                el = el.trim();
                if(list.contains(el)) throw new BadRequestException("Elements in list are not unique in Metadata with id " + metadataFieldId);
                list.add(el);
            }
        }

        return new MessageResponseDto(
                HttpStatus.OK.value(),
                categoryService.updateMetadataFieldValues(categoryId,metaDataValuesRequestDtos)
        );
    }

}
