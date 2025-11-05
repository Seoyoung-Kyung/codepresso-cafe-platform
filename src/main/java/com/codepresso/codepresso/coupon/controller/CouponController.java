package com.codepresso.codepresso.coupon.controller;

import com.codepresso.codepresso.coupon.dto.CouponCountResponse;
import com.codepresso.codepresso.coupon.dto.CouponResponse;
import com.codepresso.codepresso.common.security.LoginUser;
import com.codepresso.codepresso.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    // 사용가능한 쿠폰 개수
    @GetMapping("/me/count")
    public ResponseEntity<CouponCountResponse> getMyCouponCount(@AuthenticationPrincipal LoginUser loginUser) {
        Long memberId = loginUser.getMemberId();

        int count = couponService.getMemberValidCouponCount(memberId);

        CouponCountResponse response = CouponCountResponse.builder()
                .validCouponCount(count)
                .build();

        return ResponseEntity.ok(response);
    }

    // 내 쿠폰 목록
    @GetMapping("/me")
    public ResponseEntity<List<CouponResponse>> getMyCoupons(@AuthenticationPrincipal LoginUser loginUser) {
        Long memberId = loginUser.getMemberId();

        List<CouponResponse> coupons = couponService.getMemberValidCoupons(memberId);

        return ResponseEntity.ok(coupons);
    }
}
