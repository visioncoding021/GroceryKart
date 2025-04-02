package com.ecommerce.models.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Seller extends User {

    @Column(nullable = false)
    private String gstNumber;

    @Column(nullable = false)
    private String companyContact;

    @Column(nullable = false)
    private String companyName;

    @OneToOne
    @JoinColumn(name = "address_id")
    @Column(nullable = false)
    private Address address;

}
