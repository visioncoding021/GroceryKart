package com.ecommerce.models.category;

import com.ecommerce.utils.audit.AuditDetails;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "category_metadata_fields")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMetadataField extends AuditDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false,unique = true)
    private String name;

    @OneToMany(mappedBy = "categoryMetadataField")
    private Set<CategoryMetadataFieldValues> categoryMetadataFieldValues;
}
