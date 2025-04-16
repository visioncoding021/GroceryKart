package com.ecommerce.repository.category_repos;

import com.ecommerce.models.category.CategoryMetadataField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryMetadataFieldRepository extends JpaRepository<CategoryMetadataField, UUID> {
    boolean existsByName(String name);
    Page<CategoryMetadataField> findAll(PageRequest pageRequest);
}
