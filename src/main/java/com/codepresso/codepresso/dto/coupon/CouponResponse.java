package com.codepresso.codepresso.dto.coupon;

import com.codepresso.codepresso.entity.coupon.MemberCoupon;
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
