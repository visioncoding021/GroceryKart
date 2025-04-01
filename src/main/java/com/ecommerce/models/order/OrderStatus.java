package com.ecommerce.models.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_status")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_product_id", nullable = false)
    private OrderProduct orderProduct;

    @Column(nullable = false)
    private String fromStatus;

    @Column(nullable = false)
    private String toStatus;

    @Column(nullable = false)
    private LocalDateTime statusChangeDate;

    @Column(nullable = false)
    private String transitionNotes;

    @Column(nullable = false)
    private LocalDateTime transitionDate;
}
