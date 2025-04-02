package com.ecommerce.models.product;


import com.ecommerce.models.order.OrderProduct;
import com.ecommerce.utils.audit.AuditDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "product_variations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariation extends AuditDetails {

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

    @Column(columnDefinition = "JSON")
    @Convert(converter = JsonConverter.class)
    private Map<String, String> metadata;

    @OneToMany(mappedBy = "productVariation")
    @JsonIgnore
    private Set<OrderProduct> orderProduct;

}
