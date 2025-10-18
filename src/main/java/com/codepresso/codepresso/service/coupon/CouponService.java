package com.codepresso.codepresso.service.coupon;

import com.codepresso.codepresso.dto.coupon.CouponResponse;
import com.codepresso.codepresso.entity.coupon.CouponType;
import com.codepresso.codepresso.entity.coupon.MemberCoupon;
import com.codepresso.codepresso.repository.coupon.CouponTypeRepository;
import com.codepresso.codepresso.repository.coupon.MemberCouponRepository;
import com.codepresso.codepresso.repository.product.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponTypeRepository couponTypeRepository;
    private final MemberCouponRepository memberCouponRepository;

    /**
     * 스탬프 적립용 기본 쿠폰 타입 조회
     * */
    public CouponType getDefaultStampCoupon() {
        return couponTypeRepository.findByCouponType("STAMP_REWARD")
                .orElseThrow(() -> new IllegalArgumentException("Default Stamp Coupon Type is not exist"));
    }

    /**
     * 회원의 사용 가능한 쿠폰 개수 조회
     * */
    public int getMemberValidCouponCount(Long memberId) {
        // status = UNUSED && 만료일이 지나지 않은 것
        return memberCouponRepository.countByMemberIdAndStatusAndExpiryDateAfter(
                memberId, MemberCoupon.CouponStatus.UNUSED, LocalDateTime.now());
    }

    /**
     * 회원의 사용 가능한 쿠폰 목록 조회
     * */
    public List<CouponResponse> getMemberValidCoupons(Long memberId) {
        List<MemberCoupon> validCoupons = memberCouponRepository
                .findByMemberIdAndStatusAndExpiryDateAfter(
                memberId, MemberCoupon.CouponStatus.UNUSED, LocalDateTime.now());

        return validCoupons.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 쿠폰 유효성 검증
     * */
    private void validateCoupon(MemberCoupon coupon) {
        // 이미 사용된 쿠폰인지 확인
        if(coupon.getStatus().equals(MemberCoupon.CouponStatus.USED)) {
            throw new IllegalStateException("Coupon Status is used");
        }

        // 만료된 쿠폰인지 확인
        if(coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Coupon Expiry Date is invalid");
        }
    }

    /**
     * 쿠폰 타입별 할인금액 계산
     * */
    private int calculateCouponDiscountAmount(CouponType couponType,int totalAmount) {
        String type = couponType.getCouponType();

        switch (type) {
            case "STAMP_REWARD":
                return Math.min(2000, totalAmount);
                default:
                    return 0;
        }
    }

    /**
     * 쿠폰 할인 금액 계산 및 검증
     * */
    public int calculateCouponDiscount(Long couponId, int totalAmount) {
        MemberCoupon coupon = memberCouponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("Member coupon not found"));

        // 쿠폰 유효성 검증
        validateCoupon(coupon);

        // 할인금액 계산
        return calculateCouponDiscountAmount(coupon.getCouponType(), totalAmount);
    }

    /**
     * 쿠폰 사용 처리 (UNUSED -> USED)
     * */
    @Transactional
    public void useCoupon(Long couponId) {
        MemberCoupon coupon = memberCouponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("Member coupon not found"));

        validateCoupon(coupon);
        coupon.setStatus(MemberCoupon.CouponStatus.USED);
        memberCouponRepository.save(coupon);
    }

    /**
     * MemberCoupon 엔티티를 CouponResponse로 반환
     * */
    private CouponResponse convertToResponse(MemberCoupon coupon) {
        int discountAmount = calculateCouponDiscountAmount(coupon.getCouponType(),Integer.MAX_VALUE);

        return CouponResponse.builder()
                .couponId(coupon.getId())
                .couponType(coupon.getCouponType().getCouponType())
                .issuedDate(coupon.getIssuedDate())
                .expiryDate(coupon.getExpiryDate())
                .couponStatus(coupon.getStatus())
                .discountAmount(discountAmount)
                .build();
    }

}
