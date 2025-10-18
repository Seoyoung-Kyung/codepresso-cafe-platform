package com.codepresso.codepresso.service.coupon;

import com.codepresso.codepresso.entity.coupon.CouponType;
import com.codepresso.codepresso.entity.coupon.MemberCoupon;
import com.codepresso.codepresso.entity.coupon.Stamp;
import com.codepresso.codepresso.entity.member.Member;
import com.codepresso.codepresso.entity.order.OrdersDetail;
import com.codepresso.codepresso.repository.coupon.MemberCouponRepository;
import com.codepresso.codepresso.repository.coupon.StampRepository;
import com.codepresso.codepresso.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StampService {
    private final MemberRepository memberRepository;
    private final StampRepository stampRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final CouponService couponService;

    /**
     * 주문으로부터 스탬프 적립 ( quantity update )
     * */
    @Transactional
    public void earnStampsFromOrder(Long memberId, List<OrdersDetail> ordersDetails) {
        // 1.회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 2. 적립할 스탬프 수 계산
        int totalEarnedStamps = 0;
        for (OrdersDetail ordersDetail : ordersDetails) {
            totalEarnedStamps += ordersDetail.getQuantity();
        }

        // 기존 스탬프 찾기
        Optional<Stamp> existingStamp = stampRepository.findByMember(member);

        if (existingStamp.isPresent()) {
            // 기존 스탬프가 있다면 quantity 증가
            Stamp stamp = existingStamp.get();
            int updateStamp = stamp.getQuantity() +  totalEarnedStamps;
            stamp.setQuantity(updateStamp);

            stampRepository.save(stamp);
        }else{
            // 없다면 새로 생성
            Stamp stamp = Stamp.builder()
                    .member(member)
                    .quantity(totalEarnedStamps)
                    .earnedDate(LocalDateTime.now())
                    .build();

            stampRepository.save(stamp);
        }

        issueCoupon(memberId);
    }

    /**
     * 회원의 현재 스탬프 수 조회
     * */
    public int getMemberStamp(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        
        Optional<Stamp> stampOpt = stampRepository.findByMember(member);
        
        return stampOpt.map(Stamp::getQuantity).orElse(0);
    }

    private void issueCoupon(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Optional<Stamp> stampOpt = stampRepository.findByMember(member);

        if (stampOpt.isPresent() && stampOpt.get().getQuantity()>=10) {
            CouponType defaulCouponType = couponService.getDefaultStampCoupon();

            MemberCoupon coupon = MemberCoupon.builder()
                    .member(member)
                    .couponType(defaulCouponType)
                    .issuedDate(LocalDateTime.now())
                    .build();
            memberCouponRepository.save(coupon);

            Stamp stamp = stampOpt.get();
            stamp.setQuantity(stamp.getQuantity() - 10);
            stampRepository.save(stamp);
        }
    }
}
