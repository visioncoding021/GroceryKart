package com.ecommerce.repository.product_repos;

import com.ecommerce.models.product.Product;
import com.ecommerce.models.product.ProductVariation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductVariationRepository extends JpaRepository<ProductVariation, UUID> {

    Page<ProductVariation> findAll(Specification<ProductVariation> specification, Pageable pageable);
}
