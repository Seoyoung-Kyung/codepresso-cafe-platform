package com.codepresso.codepresso.coupon.entity;

import com.codepresso.codepresso.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
@Table(name = "member_coupon")
@Entity
public class MemberCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_coupon_id")
    private Long id;

    // FK → CouponType
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private CouponType couponType;

    // FK → Member
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "issued_date")
    private LocalDateTime issuedDate;

    // 만료일
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private CouponStatus status;

    public enum CouponStatus {
        UNUSED, // 사용전
        USED    // 사용후
    }

    // staus가 null일 경우 UNUSED로 자동 세팅 & 만료일 자동계산
    @PrePersist
    void onCreate() {
        if (status == null) status = CouponStatus.UNUSED;
        if (issuedDate != null && expiryDate == null) {
            expiryDate = issuedDate.plusMonths(6); // 발급일로부터 6개월
        }
    }
}

