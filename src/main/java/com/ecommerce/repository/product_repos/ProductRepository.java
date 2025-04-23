package com.ecommerce.repository.product_repos;

import com.ecommerce.models.category.Category;
import com.ecommerce.models.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    boolean existsByNameIgnoreCaseAndBrandAndCategoryIdAndSellerId(String name, String brand, UUID categoryId, UUID sellerId);
    Optional<Product> findByIdAndSellerId(UUID productId, UUID sellerId);
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
}
