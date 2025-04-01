package com.ecommerce.repository.order_repos;

import com.ecommerce.models.order.OrderProduct;
import com.ecommerce.models.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
}
