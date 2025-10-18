package com.codepresso.codepresso.entity.payment;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
@Table(name = "payment_type")
@Entity
public class PaymentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_type_id")
    private Long id;

    @Column(name = "payment_type_name",length = 100)
    private String paymentTypeName;

    @Column(name = "description", length = 500)
    private String description;

    // 결제타입 <-> 결제 슬레이브 (1:N)
    @OneToMany(mappedBy = "paymentType",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentDetail> paymentDetails = new ArrayList<>();
}
