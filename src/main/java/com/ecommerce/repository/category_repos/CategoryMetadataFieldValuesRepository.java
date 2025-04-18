package com.ecommerce.repository.category_repos;

import com.ecommerce.models.category.CategoryMetadataFieldValues;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryMetadataFieldValuesRepository extends JpaRepository<CategoryMetadataFieldValues, UUID> {

    boolean existsByCategoryMetadataField_IdAndCategory_Id(UUID metaFieldId, UUID categoryId);

}
