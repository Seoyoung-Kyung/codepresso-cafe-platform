package com.codepresso.codepresso.entity.order;

import com.codepresso.codepresso.entity.branch.Branch;
import com.codepresso.codepresso.entity.member.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name="orders")
@Entity
public class Orders {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "production_status", length = 50)
    private String productionStatus;

    @Column(name = "takeout")
    private Boolean isTakeout;

    @Column(name = "pickup_time")
    private LocalDateTime pickupTime;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "request_note",length = 100)
    private String requestNote;

    @Column(name = "pickup")
    private Boolean isPickup;

    // 주문 마스터 <-> 주문상세 (1:N)
    @BatchSize(size = 100)
    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdersDetail> ordersDetails;

    @Column(name = "total_amount")
    private Integer totalAmount;     // 원래 주문 금액( 할인전 )

    @Column(name ="discount_amount")
    private Integer discountAmount;     // 할인금액

    @Column(name = "final_amount")
    private Integer finalAmount;        // 최종 결제 금액

    @Column(name = "used_coupon_id")
    private Long usedCouponId;          // 사용할 쿠폰 ID
}
