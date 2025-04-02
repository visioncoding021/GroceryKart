package com.ecommerce.models.order;

import com.ecommerce.models.product.ProductVariation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variation_id", nullable = false)
    private ProductVariation productVariation;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private Double price;

    @OneToOne(mappedBy = "orderProduct", fetch = FetchType.LAZY)
    @JsonIgnore
    private OrderStatus orderStatus;

}
