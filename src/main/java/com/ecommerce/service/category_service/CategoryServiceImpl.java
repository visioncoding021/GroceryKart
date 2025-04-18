package com.ecommerce.service.category_service;

import com.ecommerce.dto.response_dto.category_dto.*;
import com.ecommerce.models.category.Category;
import com.ecommerce.models.category.CategoryMetadataFieldValues;
import com.ecommerce.repository.category_repos.CategoryRepository;
import jakarta.persistence.criteria.Predicate;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public String addCategory(String name, UUID parentId){

        name = name.trim();
        boolean nameExists = categoryRepository.existsByNameIgnoreCase(name);

        if (parentId == null) {
            if (nameExists) {
                throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Category already exists at root level");
            }

            Category newCategory = new Category();
            newCategory.setName(name);
            newCategory.setParent(null);
            return categoryRepository.save(newCategory).getId().toString();
        }

        if(!categoryRepository.existsById(parentId)){
            throw new ResponseStatusException(HttpStatusCode.valueOf(400),"Parent Id doesn't Exist");
        }

        Category parentCategory = categoryRepository.findById(parentId).get();

        if(parentCategory.getProducts()!=null && !parentCategory.getProducts().isEmpty())
            throw new ResponseStatusException(HttpStatusCode.valueOf(400),"Products are active under the parent category; cannot add a subcategory ");

        if(parentCategory.getCategoryMetadataFieldValues()!=null){
            throw new ResponseStatusException(HttpStatusCode.valueOf(400),"Metadata Available on the Parent Category so considered as leaf Category, cannot add a subcategory ");
        }

        if(nameExists){
            boolean isDepth = false;
            boolean isBreadth = false;

            for(Category subCategory : parentCategory.getSubCategories()){
                if(subCategory.getName().equalsIgnoreCase(name)){
                    isBreadth=true;
                    break;
                }
            }

            Category parent = parentCategory;
            while(parent!=null && !isBreadth){
                if(parent.getName().equalsIgnoreCase(name)){
                    isDepth = true;
                    break;
                }
                parent=parent.getParent();
            }

            if(isBreadth || isDepth){
                throw new ResponseStatusException(HttpStatusCode.valueOf(400),"Either Parent or Sibling exists with same name category");
            }

        }

        Category newCategory = new Category();
        newCategory.setName(name);
        newCategory.setParent(parentCategory);
        newCategory = categoryRepository.save(newCategory);

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

        categoryResponseDto.setParent(mapParentHierarchy(category.getParent()));

        categoryResponseDto.setChildren(mapChildren(category));

        categoryResponseDto.setFields(mapFields(category));

        return categoryResponseDto;
    }

    @Override
    public List<CategoryResponseDto> getAllParentCategory(int max, int offset, String sort, String order, Map<String,Object> filters){
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, max, Sort.by(direction, sort));

        Page<Category> listOfCategory = categoryRepository.findAll(getCategoryFilters(filters),pageable);

        List<CategoryResponseDto> categoryResponseDtoList = new ArrayList<>();

        for(Category category : listOfCategory){
            CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
            BeanUtils.copyProperties(category,categoryResponseDto);

            categoryResponseDto.setParent(mapParentHierarchy(category.getParent()));

            categoryResponseDto.setChildren(mapChildren(category));

            categoryResponseDto.setFields(mapFields(category));

            categoryResponseDtoList.add(categoryResponseDto);
        }
        return categoryResponseDtoList;
    }


    private ParentCategoryDto mapParentHierarchy(Category parent) {
        ParentCategoryDto root = null;
        ParentCategoryDto currentDto = null;

        while (parent != null) {
            ParentCategoryDto temp = new ParentCategoryDto();
            BeanUtils.copyProperties(parent, temp);

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

    private List<ChildrenCategoryDto> mapChildren(Category category) {
        List<ChildrenCategoryDto> children = new ArrayList<>();

        if (category.getSubCategories() != null) {
            for (Category subCategory : category.getSubCategories()) {
                ChildrenCategoryDto dto = new ChildrenCategoryDto();
                BeanUtils.copyProperties(subCategory, dto);
                children.add(dto);
            }
        }

        return children;
    }

    private List<CategoryMetadataFieldValueResponseDto> mapFields(Category category){
        List<CategoryMetadataFieldValueResponseDto> fields = new ArrayList<>();
        if(category.getCategoryMetadataFieldValues()!=null){
            for (CategoryMetadataFieldValues categoryMetadataFieldValues : category.getCategoryMetadataFieldValues()){
                CategoryMetadataFieldValueResponseDto categoryMetadataFieldValueResponseDto = new CategoryMetadataFieldValueResponseDto();
                BeanUtils.copyProperties(categoryMetadataFieldValues,categoryMetadataFieldValueResponseDto);
                fields.add(categoryMetadataFieldValueResponseDto);
            }
        }
        return fields;
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

}
