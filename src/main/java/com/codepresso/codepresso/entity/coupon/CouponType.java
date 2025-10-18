package com.codepresso.codepresso.entity.coupon;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;


@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
@Table(name = "coupon_type")
@Entity
public class CouponType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long id;

    @Column(name = "coupon_type", length = 30)
    private String couponType;

    @Column(name = "valid_period")
    private LocalDateTime validPeriod;

    @Column(name = "available_menu", length = 255)
    private String availableMenu;

    // 쿠폰타입 ↔ 회원보유쿠폰 (1:N)
    @OneToMany(mappedBy = "couponType", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MemberCoupon> memberCoupons = new java.util.ArrayList<>();
}
