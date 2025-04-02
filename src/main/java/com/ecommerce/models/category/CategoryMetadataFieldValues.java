package com.ecommerce.models.category;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category_metadata_field_values",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"category_metadata_field_id", "category_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMetadataFieldValues {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_metadata_field_id",nullable = false)
    private CategoryMetadataField categoryMetadataField;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",nullable = false)
    private Category category;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "category_metadata_field_value_entries", joinColumns = @JoinColumn(name = "field_value_id"))
    @Column(name = "value",nullable = false)
    private List<String> value = new ArrayList<>();
}
