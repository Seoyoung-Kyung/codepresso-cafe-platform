package com.codepresso.codepresso.order.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 주문 상세 응답 DTO
 * */
@Data
@Builder
public class OrderDetailResponse {

    // 주문 정보
    private Long orderId;
    private String orderNumber;
    private LocalDateTime orderDate;
    private String productionStatus;
    private LocalDateTime pickupTime;
    private Boolean isTakeout;
    private String requestNote;

    // 지점 정보
    private BranchInfo branch;

    // 주문 상품 목록
    private List<OrderItem> orderItems;

    // 결제정보
    private PaymentInfo payment;

    @Data
    @Builder
    public static class BranchInfo {
        private Long branchId;
        private String branchName;
        private String address;
        private String branchNumber;
    }

    @Data
    @Builder
    public static class OrderItem {
        private Long orderDetailId;
        private String productName;
        private Integer quantity;
        private Integer price;
        private Integer totalPrice;
        private List<OrderOption> options;
    }

    @Data
    @Builder
    public static class OrderOption {
        private String optionStyle;
        private Integer extraPrice;
    }

    @Data
    @Builder
    public static class PaymentInfo {
        private String paymentMethod;
        private Integer totalAmount;        // 주문금액 (할인 전)
        private Integer discount;           // 할인 금액
        private Integer finalAmount;        // 총 결제 금액 (totalAmount - discountAmount)
        private LocalDateTime paymentDate;
    }
}
