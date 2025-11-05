package com.codepresso.codepresso.coupon.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CouponCountResponse {
    private Integer validCouponCount;
}
