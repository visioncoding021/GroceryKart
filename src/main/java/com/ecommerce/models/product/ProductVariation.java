package com.ecommerce.models.product;


import com.ecommerce.models.order.OrderProduct;
import com.ecommerce.utils.audit.AuditDetails;
import com.ecommerce.utils.model_utils.JsonConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "product_variations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariation extends AuditDetails {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String quantityAvailable;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String primaryImageUrl;

    @Column(columnDefinition = "JSON",nullable = false)
    @Convert(converter = JsonConverter.class)
    private Map<String, String> metadata;

    @OneToMany(mappedBy = "productVariation")
    @JsonIgnore
    private Set<OrderProduct> orderProduct;
}
