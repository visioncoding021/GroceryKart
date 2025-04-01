package com.ecommerce.models.order;

import com.testpurpose.model.user.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount_paid", nullable = false)
    private Double amountPaid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "date_created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "customer_address_city", nullable = false)
    private String customerAddressCity;

    @Column(name = "customer_address_state", nullable = false)
    private String customerAddressState;

    @Column(name = "customer_address_country", nullable = false)
    private String customerAddressCountry;

    @Column(name = "customer_address_address_line", nullable = false)
    private String customerAddressAddressLine;

    @Column(name = "customer_address_zip_code", nullable = false)
    private String customerAddressZipCode;

    @Column(name = "customer_address_label", nullable = false)
    private String customerAddressLabel;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderProduct> orderProducts;

}
