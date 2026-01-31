package com.codepresso.codepresso.coupon.repository;

import com.codepresso.codepresso.coupon.entity.MemberCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {

    // 사용 가능한 쿠폰 개수 조회
    int countByMemberIdAndStatusAndExpiryDateAfter(Long memberId, MemberCoupon.CouponStatus couponStatus, LocalDateTime now);

    // 사용 가능한 쿠폰 목록 조회 (기존 메서드 유지 - 호환성)
    List<MemberCoupon> findByMemberIdAndStatusAndExpiryDateAfter(Long memberId, MemberCoupon.CouponStatus couponStatus, LocalDateTime now);

    /**
     * 사용 가능한 쿠폰 목록 조회 - CouponType fetch join
     * N+1 해결: MemberCoupon -> CouponType
     */
    @Query("SELECT mc FROM MemberCoupon mc " +
            "JOIN FETCH mc.couponType " +
            "WHERE mc.member.id = :memberId " +
            "AND mc.status = :status " +
            "AND mc.expiryDate > :now")
    List<MemberCoupon> findValidCouponsWithType(
            @Param("memberId") Long memberId,
            @Param("status") MemberCoupon.CouponStatus status,
            @Param("now") LocalDateTime now);
}
