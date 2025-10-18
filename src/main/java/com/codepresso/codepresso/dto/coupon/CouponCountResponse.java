package com.codepresso.codepresso.dto.coupon;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CouponCountResponse {
    private Integer validCouponCount;
}
