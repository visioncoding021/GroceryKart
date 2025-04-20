package com.ecommerce.repository.product_repos;

import com.ecommerce.models.product.Product;
import com.ecommerce.models.product.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductReviewRepository extends JpaRepository<ProductReview, UUID> {
}
