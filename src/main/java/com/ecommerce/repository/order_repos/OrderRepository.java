package com.ecommerce.repository.order_repos;

import com.ecommerce.models.order.Order;
import com.ecommerce.models.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
