package com.ecommerce.repository.product_repos;

import com.ecommerce.models.product.Product;
import com.ecommerce.models.product.ProductVariation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariationRepository extends JpaRepository<ProductVariation, Long> {
}
