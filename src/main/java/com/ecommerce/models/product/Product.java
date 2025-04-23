package com.ecommerce.models.product;

import com.ecommerce.models.category.Category;
import com.ecommerce.models.user.Seller;
import com.ecommerce.utils.audit.AuditDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted = false")
public class Product extends AuditDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private Boolean isCancellable = false;

    @Column(nullable = false)
    private Boolean isReturnable = false;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private Boolean isActive = false;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<ProductVariation> productVariations;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<ProductReview> productReviews;

}
