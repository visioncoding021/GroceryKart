package com.ecommerce.models.cart;

import com.ecommerce.models.product.ProductVariation;
import com.ecommerce.models.user.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "cart",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"customer_id", "product_variation_id"})
        }
)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variation_id", nullable = false)
    private ProductVariation productVariation;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private boolean isWishlist = false;
}
