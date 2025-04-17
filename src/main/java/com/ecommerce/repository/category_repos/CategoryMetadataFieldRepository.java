package com.ecommerce.repository.category_repos;

import com.ecommerce.models.category.CategoryMetadataField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CategoryMetadataFieldRepository extends JpaRepository<CategoryMetadataField, UUID> {
    boolean existsByNameIgnoreCase(String name);

    Page<CategoryMetadataField> findByNameContainingIgnoreCase(String name, PageRequest pageRequest);

    Optional<CategoryMetadataField> findById(UUID id);

    @Query("""
    SELECT c FROM CategoryMetadataField c
    WHERE (:name IS NOT NULL AND LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
       OR (:id IS NOT NULL AND c.id = :id)
    """)
    Page<CategoryMetadataField> findByNameOrId(@Param("name") String name, @Param("id") UUID id, Pageable pageable);


}
