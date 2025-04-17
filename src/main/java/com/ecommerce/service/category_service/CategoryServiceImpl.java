package com.ecommerce.service.category_service;

import com.ecommerce.models.category.Category;
import com.ecommerce.repository.category_repos.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
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
            while(parent!=null){
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

}
