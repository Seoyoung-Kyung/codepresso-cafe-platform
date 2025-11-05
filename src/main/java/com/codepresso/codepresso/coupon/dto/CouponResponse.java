package com.codepresso.codepresso.coupon.dto;

import com.codepresso.codepresso.coupon.entity.MemberCoupon;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CouponResponse {
    private Long couponId;
    private String couponType;
    private LocalDateTime issuedDate;
    private LocalDateTime expiryDate;
    private MemberCoupon.CouponStatus couponStatus;
    private Integer discountAmount;
}
