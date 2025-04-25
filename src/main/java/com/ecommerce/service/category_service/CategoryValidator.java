package com.ecommerce.service.category_service;

import com.ecommerce.dto.request_dto.category_dto.MetaDataValuesRequestDto;
import com.ecommerce.models.category.Category;
import com.ecommerce.models.category.CategoryMetadataFieldValues;
import com.ecommerce.repository.category_repos.CategoryMetadataFieldValuesRepository;
import com.ecommerce.repository.category_repos.CategoryRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Component
public class CategoryValidator {

    @Autowired
    private CategoryMetadataFieldValuesRepository metadataFieldValuesRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public boolean existsInParentCategories(Category parent, String name){
        while(parent!=null){
            if(parent.getName().equalsIgnoreCase(name)){
                return true;
            }
            parent=parent.getParent();
        }
        return false;
    }

    public boolean existsInChildCategories(Category child,String name){
        if(child.getName().equalsIgnoreCase(name)){
            return true;
        }
        for (Category children : child.getSubCategories()){
            existsInChildCategories(children,name);
        }
        return false;
    }

    public boolean isInBreadth(Category parent, String name) {
        if (parent != null && parent.getSubCategories() != null) {
            for (Category subCategory : parent.getSubCategories()) {
                if (subCategory.getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void validateCategoryName(String name, UUID parentId) {
        boolean nameExists = categoryRepository.existsByNameIgnoreCase(name);

        if (parentId == null) {
            if (nameExists) {
                throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Category already exists at root level");
            }
            return;
        }

        if (nameExists) {
            Category parentCategory = categoryRepository.findById(parentId).get();
            if (isNameConflictInParentOrSiblings(parentCategory, name)) {
                throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Either Parent or Sibling exists with same name category");
            }
        }
    }

    public boolean isNameConflictInParentOrSiblings(Category parent, String name) {
        boolean isBreadth = isInBreadth(parent, name);
        boolean isDepth = !isBreadth && existsInParentCategories(parent, name);
        return isBreadth || isDepth;
    }

    public void validateMetadataNotAssignedInHierarchy(Category category, List<MetaDataValuesRequestDto> metaDataValuesRequestDtos) throws BadRequestException {
        Category current = category;

        while (current != null) {
            UUID currentCategoryId = current.getId();
            for (MetaDataValuesRequestDto dto : metaDataValuesRequestDtos) {
                UUID metadataFieldId = dto.getMetadataFieldId();

                if (metadataFieldValuesRepository.existsByCategoryMetadataField_IdAndCategory_Id(metadataFieldId, currentCategoryId)) {
                    throw new BadRequestException("Relation already exists between metadata and category (or its parent) for metadata field id: " + metadataFieldId);
                }
            }
            current = current.getParent();
        }
        checkMetadataInChildNodesOfCategory(category, metaDataValuesRequestDtos);
    }

    private void checkMetadataInChildNodesOfCategory(Category category,List<MetaDataValuesRequestDto> metaDataValuesRequestDtos) throws BadRequestException {
        if(!category.getCategoryMetadataFieldValues().isEmpty()){
            for (MetaDataValuesRequestDto dto : metaDataValuesRequestDtos) {
                UUID metadataFieldId = dto.getMetadataFieldId();
                if (metadataFieldValuesRepository.existsByCategoryMetadataField_IdAndCategory_Id(metadataFieldId, category.getId())) {
                    throw new BadRequestException("Relation already exists between metadata and category (or in its Child ) for metadata field id: " + metadataFieldId);
                }
            }
        }
        for(Category child : category.getSubCategories()){
            checkMetadataInChildNodesOfCategory(child,metaDataValuesRequestDtos);
        }
    }


}
