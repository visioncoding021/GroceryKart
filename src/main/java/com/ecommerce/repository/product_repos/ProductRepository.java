package com.ecommerce.repository.product_repos;

import com.ecommerce.models.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
