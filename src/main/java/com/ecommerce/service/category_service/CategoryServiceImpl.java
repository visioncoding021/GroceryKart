package com.ecommerce.service.category_service;

import com.ecommerce.dto.request_dto.category_dto.MetaDataValuesRequestDto;
import com.ecommerce.dto.response_dto.category_dto.*;
import com.ecommerce.models.category.Category;
import com.ecommerce.models.category.CategoryMetadataField;
import com.ecommerce.models.category.CategoryMetadataFieldValues;
import com.ecommerce.repository.category_repos.CategoryMetadataFieldRepository;
import com.ecommerce.repository.category_repos.CategoryMetadataFieldValuesRepository;
import com.ecommerce.repository.category_repos.CategoryRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMetadataFieldValuesRepository metadataFieldValuesRepository;

    @Autowired
    private CategoryMetadataFieldRepository categoryMetadataFieldRepository;

    @Autowired
    private CategoryValidator categoryValidator;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    @Transactional
    public String addCategory(String name, UUID parentId){

        name = name.trim();

        categoryValidator.validateCategoryName(name, parentId);

        Category parentCategory = (parentId!=null)?getParentCategory(parentId):null;

        if(parentCategory!=null && parentCategory.getProducts()!=null && !parentCategory.getProducts().isEmpty())
            throw new ResponseStatusException(HttpStatusCode.valueOf(400),"Products are active under the parent category; cannot add a subcategory ");

        Category newCategory = new Category();
        newCategory.setName(name);
        newCategory.setParent(parentCategory);
        newCategory = categoryRepository.save(newCategory);

        if(parentCategory==null){
            return newCategory.getId().toString();
        }

        if (parentCategory.getSubCategories()==null){
            parentCategory.setSubCategories(new ArrayList<>());
        }

        List<Category> subCategoryList = parentCategory.getSubCategories();
        subCategoryList.add(newCategory);
        parentCategory.setSubCategories(subCategoryList);
        categoryRepository.save(parentCategory);

        return newCategory.getId().toString();
    }

    @Override
    public CategoryResponseDto getCategoryById(UUID id){
        if(!categoryRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatusCode.valueOf(400),"Category Doesn't exist with given Id");
        }

        Category category = categoryRepository.findById(id).get();
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
        BeanUtils.copyProperties(category,categoryResponseDto);
        categoryResponseDto.setParent(categoryMapper.mapParentHierarchyAndMetadataFieldValues(category.getParent(),categoryResponseDto));
        categoryResponseDto.setChildren(categoryMapper.mapChildren(category));
        List<CategoryMetadataFieldValueResponseDto> fieldsResponseDtos = categoryMapper.mapFields(category);
        List<CategoryMetadataFieldValueResponseDto> currentFields = categoryResponseDto.getFields();
         if (!fieldsResponseDtos.isEmpty()) currentFields.addAll(fieldsResponseDtos);
         categoryResponseDto.setFields(currentFields);

        return categoryResponseDto;
    }

    @Override
    public List<CategoryResponseDto> getAllCategories(int max, int offset, String sort, String order, Map<String,Object> filters){
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, max, Sort.by(direction, sort));

        Page<Category> listOfCategory = categoryRepository.findAll(getCategoryFilters(filters),pageable);
        List<CategoryResponseDto> categoryResponseDtoList = new ArrayList<>();

        for(Category category : listOfCategory){
            CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
            BeanUtils.copyProperties(category,categoryResponseDto);
            categoryResponseDto.setParent(categoryMapper.mapParentHierarchyAndMetadataFieldValues(category.getParent(),categoryResponseDto));
            categoryResponseDto.setChildren(categoryMapper.mapChildren(category));
            List<CategoryMetadataFieldValueResponseDto> fieldsResponseDtos = categoryMapper.mapFields(category);
            List<CategoryMetadataFieldValueResponseDto> currentFields = categoryResponseDto.getFields();
            if (!fieldsResponseDtos.isEmpty()) currentFields.addAll(fieldsResponseDtos);
            categoryResponseDto.setFields(currentFields);

            categoryResponseDtoList.add(categoryResponseDto);
        }
        return categoryResponseDtoList;
    }

    @Override
    @Transactional
    public String updateCategory(UUID categoryId,String categoryName) throws BadRequestException {
        if(!categoryRepository.existsById(categoryId)) throw new BadRequestException("Category doesn't Exists");

        categoryName = categoryName.trim();

        Category category = categoryRepository.findById(categoryId).get();

        if(categoryValidator.existsInParentCategories(category.getParent(),categoryName) || categoryValidator.existsInChildCategories(category,categoryName) ){
            throw new BadRequestException("Already Exists in Parent or child Hierarchy");
        }

        category.setName(categoryName);
        System.out.println(categoryName);
        categoryRepository.updateCategoryName(categoryName,categoryId);

        return "Category Updated Successfully";
    }

    @Override
    @Transactional
    public String addMetadataFieldWithValues(UUID categoryId, List<MetaDataValuesRequestDto> metaDataValuesRequestDtos) throws BadRequestException {
        if (!categoryRepository.existsById(categoryId)) throw new BadRequestException("Category Id doesn't Exists");

        for(MetaDataValuesRequestDto metaDataValuesRequestDto : metaDataValuesRequestDtos){
            UUID metadataFieldId = metaDataValuesRequestDto.getMetadataFieldId();
            if(!categoryMetadataFieldRepository.existsById(metadataFieldId)) throw new BadRequestException("Category Metadata Field Id doesn't Exists");
        }

        Category category = categoryRepository.findById(categoryId).get();

        categoryValidator.validateMetadataNotAssignedInHierarchy(category, metaDataValuesRequestDtos);

        for (MetaDataValuesRequestDto metaDataValuesRequestDto : metaDataValuesRequestDtos){

            UUID metadataFieldId = metaDataValuesRequestDto.getMetadataFieldId();
            List<String> value = metaDataValuesRequestDto.getValue();

            CategoryMetadataField categoryMetadataField = categoryMetadataFieldRepository.findById(metadataFieldId).get();

            CategoryMetadataFieldValues categoryMetadataFieldValues = new CategoryMetadataFieldValues();
            categoryMetadataFieldValues.setCategory(category);
            categoryMetadataFieldValues.setCategoryMetadataField(categoryMetadataField);
            categoryMetadataFieldValues.setValue(value);

            if(category.getCategoryMetadataFieldValues()==null){
                category.setCategoryMetadataFieldValues(new ArrayList<>());
            }

            metadataFieldValuesRepository.save(categoryMetadataFieldValues);

            category.getCategoryMetadataFieldValues().add(categoryMetadataFieldValues);
            categoryRepository.save(category);
        }

        return "Category with MetadataField is Successfully added with values";
    }


    @Override
    @Transactional
    public String updateMetadataFieldValues(UUID categoryId, List<MetaDataValuesRequestDto> metaDataValuesRequestDtos) throws BadRequestException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BadRequestException("Category ID doesn't exist"));

        for (MetaDataValuesRequestDto dto : metaDataValuesRequestDtos) {
            UUID metadataFieldId = dto.getMetadataFieldId();

            CategoryMetadataField metadataField = categoryMetadataFieldRepository.findById(metadataFieldId)
                    .orElseThrow(() -> new BadRequestException("Metadata Field ID doesn't exist"));

            CategoryMetadataFieldValues existingValues = metadataFieldValuesRepository
                    .findByCategory_IdAndCategoryMetadataField_Id(categoryId, metadataFieldId)
                    .orElseThrow(() -> new BadRequestException("Mapping doesn't exist. Can't update"));

            List<String> requestValues = dto.getValue();
            List<String> alreadySavedValues = existingValues.getValue();

            for (String savedValue : alreadySavedValues) {
                if (!requestValues.contains(savedValue)) {
                    throw new BadRequestException("Missing previously saved value: " + savedValue + " in "+metadataField.getName()+ " Metadata Field");
                }
            }

            Set<String> merged = new LinkedHashSet<>(alreadySavedValues);
            merged.addAll(requestValues);

            existingValues.setValue(new ArrayList<>(merged));
            metadataFieldValuesRepository.save(existingValues);
        }

        return "Metadata field values updated successfully.";
    }

    @Override
    public List<LeafCategoryResponseDto> getAllLeafCategories(){
        List<Category> categoryList = categoryRepository.findLeafCategories();
        List<LeafCategoryResponseDto> leafCategoryResponseDtoList = new ArrayList<>();

        for(Category category:categoryList){
            LeafCategoryResponseDto leafCategoryResponseDto = new LeafCategoryResponseDto();
            BeanUtils.copyProperties(category,leafCategoryResponseDto);
            leafCategoryResponseDto.setParent(categoryMapper.mapParentHierarchyAndMetadataFieldValuesForLeaf(category.getParent(),leafCategoryResponseDto));
            List<CategoryMetadataFieldValueResponseDto> fieldsResponseDtos = categoryMapper.mapFields(category);
            List<CategoryMetadataFieldValueResponseDto> currentFields = leafCategoryResponseDto.getFields();
            if (!fieldsResponseDtos.isEmpty()) currentFields.addAll(fieldsResponseDtos);
            leafCategoryResponseDto.setFields(currentFields);
            leafCategoryResponseDtoList.add(leafCategoryResponseDto);
        }
        return leafCategoryResponseDtoList;
    }

    @Override
    public List<CategoryNameResponseDto> getSameLevelCategories(UUID categoryId){
        if(categoryId!=null && !categoryRepository.existsById(categoryId)) throw new ResponseStatusException(HttpStatusCode.valueOf(400),"Category doesn't exists with given Id");
        List<Category> categoryList = categoryRepository.findByParent_Id(categoryId);
        List<CategoryNameResponseDto> categoryNameResponseDtoList = new ArrayList<>();

        for (Category category : categoryList){
            CategoryNameResponseDto categoryNameResponseDto = new CategoryNameResponseDto();
            BeanUtils.copyProperties(category,categoryNameResponseDto);
            categoryNameResponseDtoList.add(categoryNameResponseDto);
        }
        return categoryNameResponseDtoList;
    }

    @Override
    public CategoryFiltersResponseDto getCategoryFilters(UUID categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(400), "Category doesn't exist with given Id"));
        CategoryFiltersResponseDto categoryFiltersResponseDto = new CategoryFiltersResponseDto();
        categoryFiltersResponseDto.setMetadata(new ArrayList<>());
        categoryMapper.mapParentFields(category, categoryFiltersResponseDto);
        categoryFiltersResponseDto.setMetadata(categoryMapper.mapFields(category));

        categoryFiltersResponseDto.setBrands(new HashSet<>());
        categoryFiltersResponseDto.setBrands(categoryMapper.mapBrands(category));

        categoryFiltersResponseDto.setMinPrice(categoryMapper.getMinimumPriceFromCategory(category));
        categoryFiltersResponseDto.setMaxPrice(categoryMapper.getMaximumPriceFromCategory(category));

        return categoryFiltersResponseDto;
    }


    private Specification<Category> getCategoryFilters(Map<String, Object> filters){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> listOfPredicate = new ArrayList<>();
            if(filters.containsKey("name") && !((String) filters.get("name")).isBlank()){
                listOfPredicate.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),"%" + ((String) filters.get("name")).toLowerCase() + "%"));
            }

            if(filters.containsKey("id") && !((String) filters.get("id")).isBlank()){
                UUID id = UUID.fromString((String) filters.get("id"));
                listOfPredicate.add(criteriaBuilder.equal(root.get("id"), id));
            }

            if (filters.containsKey("parentId") && !((String) filters.get("parentId")).isBlank()){
                UUID parentId = UUID.fromString((String) filters.get("parentId"));
                listOfPredicate.add(criteriaBuilder.equal(root.get("parent").get("id"),parentId));
            }

            return criteriaBuilder.and(listOfPredicate.toArray(new Predicate[0]));
        };
    }

    private Category getParentCategory(UUID parentId) {
        if (!categoryRepository.existsById(parentId)) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Parent Id doesn't Exist");
        }
        return categoryRepository.findById(parentId).get();
    }


}
