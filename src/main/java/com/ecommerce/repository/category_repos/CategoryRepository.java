package com.ecommerce.repository.category_repos;

import com.ecommerce.models.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsById(UUID id);

    Optional<Category> findById(UUID id);

    Page<Category> findAll(Specification<Category> categoryFilters, Pageable pageable);

    @Modifying
    @Query("UPDATE Category c SET c.name = :name WHERE c.id = :id")
    void updateCategoryName(@Param("name") String name, @Param("id") UUID id);
}
