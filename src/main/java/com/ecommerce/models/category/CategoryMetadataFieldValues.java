package com.ecommerce.models.category;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category_metadata_field_values")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMetadataFieldValues {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_metadata_field_id")
    private String categoryMetadataField;

    @Column(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private String category;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "category_metadata_field_values", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "value")
    private List<String> value = new ArrayList<>();
}
