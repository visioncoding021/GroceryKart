package com.ecommerce.models.user;

import com.ecommerce.models.product.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "sellers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Seller extends User {

    @Column(nullable = false,unique = true)
    private String gstNumber;

    @Column(nullable = false)
    private String companyContact;

    @Column(nullable = false,unique = true)
    private String companyName;

    @OneToMany(mappedBy = "seller")
    @JsonIgnore
    private List<Product> products;

}
