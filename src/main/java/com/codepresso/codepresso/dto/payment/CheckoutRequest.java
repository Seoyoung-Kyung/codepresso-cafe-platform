package com.codepresso.codepresso.dto.payment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 결제하기 요청 DTO
 * */
@Data
public class CheckoutRequest {

    @NotNull
    private Long memberId;

    @NotNull
    private Long branchId;

    @NotNull
    private Boolean isTakeout;
    private LocalDateTime pickupTime;
    private String requestNote;
    private Boolean isFromCart;     // 장바구니에서 온 주문인지 구분
    private Long usedCouponId;      // 사용할 couponId
    private Integer couponDiscountAmount;   // 쿠폰 할인 금액

    @NotEmpty
    @Valid
    private List<OrderItem> orderItems;     // 장바구니든 단일상품이든 여기에 담김

    @Data
    @Builder
    public static class OrderItem {
        @NotNull
        private Long productId;

        @NotNull
        @Positive
        private Integer quantity;

        @NotNull
        @Positive
        private Integer price;

        private List<Long> optionIds; //선택된 옵션들
    }

    /**
     * 전체 주문 금액 계산
     */
    public int getTotalAmount() {
        if (orderItems == null) return 0;
        return orderItems.stream()
                .mapToInt(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    public int getTotalDiscountAmount() {
        int totalDiscountAmount = 0;

        // 쿠폰 할인
        if(couponDiscountAmount != null && couponDiscountAmount>0) {
            totalDiscountAmount = couponDiscountAmount;
        }

        return Math.min(totalDiscountAmount, getTotalAmount());
    }

}
