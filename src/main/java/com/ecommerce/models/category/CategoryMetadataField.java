package com.ecommerce.models.category;

import com.ecommerce.utils.audit.AuditDetails;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "category_metadata_field")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMetadataField extends AuditDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String name;
}
