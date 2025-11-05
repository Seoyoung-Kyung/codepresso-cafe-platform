package com.codepresso.codepresso.coupon.repository;

import com.codepresso.codepresso.coupon.entity.MemberCoupon;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MemberCouponRepository extends CrudRepository<MemberCoupon, Long> {

    // 사용 가능한 쿠폰 개수 조회
    int countByMemberIdAndStatusAndExpiryDateAfter(Long memberId, MemberCoupon.CouponStatus couponStatus, LocalDateTime now);

    // 사용 가능한 쿠폰 목록 조회
    List<MemberCoupon> findByMemberIdAndStatusAndExpiryDateAfter(Long memberId, MemberCoupon.CouponStatus couponStatus, LocalDateTime now);
}
