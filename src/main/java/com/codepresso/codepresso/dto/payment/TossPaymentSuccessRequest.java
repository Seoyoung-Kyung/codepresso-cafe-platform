package com.codepresso.codepresso.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

/**
 * 토스페이먼츠 결제 성공 시 주문 생성 요청 DTO
 */
@Data
public class TossPaymentSuccessRequest {
    
    @NotNull(message = "회원 ID는 필수입니다")
    private Long memberId;
    
    @NotNull(message = "지점 ID는 필수입니다")
    private Long branchId;
    
    @NotBlank(message = "결제 키는 필수입니다")
    private String paymentKey;
    
    @NotBlank(message = "주문 ID는 필수입니다")
    private String orderId;
    
    @NotNull(message = "결제 금액은 필수입니다")
    @Positive(message = "결제 금액은 0보다 커야 합니다")
    private Integer amount;
    
    @NotNull(message = "주문 아이템은 필수입니다")
    private List<OrderItem> orderItems;
    
    private Boolean isTakeout = true;
    private String pickupTime;
    private String requestNote;
    private String pickupMethod = "포장안함";
    private Boolean useCoupon = false;
    private Integer discountAmount = 0;
    private Integer finalAmount;
    private Boolean isFromCart =  false;
    
    @Data
    public static class OrderItem {
        @NotNull(message = "상품 ID는 필수입니다")
        private Long productId;
        
        @NotNull(message = "수량은 필수입니다")
        @Positive(message = "수량은 0보다 커야 합니다")
        private Integer quantity;
        
        @NotNull(message = "가격은 필수입니다")
        @Positive(message = "가격은 0보다 커야 합니다")
        private Integer price;
        
        private List<Long> optionIds = List.of();
    }
}
