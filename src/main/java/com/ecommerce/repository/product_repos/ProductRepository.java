package com.ecommerce.repository.product_repos;

import com.ecommerce.models.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    boolean existsByNameIgnoreCaseAndBrandAndCategoryIdAndSellerId(String name, String brand, UUID categoryId, UUID sellerId);

}
