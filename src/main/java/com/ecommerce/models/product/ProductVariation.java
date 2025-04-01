package com.ecommerce.models.product;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.testpurpose.model.order.OrderProduct;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_variation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String quantityAvailable;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String primaryImageUrl;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @OneToMany(mappedBy = "productVariation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private OrderProduct orderProduct;

}
