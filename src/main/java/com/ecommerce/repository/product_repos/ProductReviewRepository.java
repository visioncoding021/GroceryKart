package com.ecommerce.repository.product_repos;

import com.ecommerce.models.product.Product;
import com.ecommerce.models.product.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
}
