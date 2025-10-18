package com.codepresso.codepresso.entity.payment;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;



@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "payment_detail")
@Entity
public class PaymentDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_detail_id")
    private Long id;

    // FK → PaymentMaster
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    // FK → PaymentType
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_type_id", nullable = false)
    private PaymentType paymentType;

    @Column(name = "payment_amt")
    private Integer paymentAmt;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "PG_id", length = 50)
    private String pgId;
}
