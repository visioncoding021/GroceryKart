package com.ecommerce.repository.product_repos;

import com.ecommerce.models.product.Product;
import com.ecommerce.models.product.ProductVariation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductVariationRepository extends JpaRepository<ProductVariation, UUID> {
}
