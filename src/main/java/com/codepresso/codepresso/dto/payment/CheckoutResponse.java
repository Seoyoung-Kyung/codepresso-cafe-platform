package com.codepresso.codepresso.dto.payment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 통합 결제 응답 DTO (결제 준비 + 주문 완료 응답 통합)
 * */
@Data
@Builder
public class CheckoutResponse {

    // 주문 완료 후 필드들 (결제 준비 단계에서는 null)
    private Long orderId;
    private String productionStatus;
    private LocalDateTime orderDate;
    private LocalDateTime pickupTime;
    private Boolean isTakeout;
    private String requestNote;

    // 공통 필드
    private Integer totalAmount;
    private Integer totalQuantity;        // 총 수량
    private Boolean isFromCart;           // 장바구니/직접구매 구분

    private List<CheckoutResponse.OrderItem> orderItems;

    @Data
    @Builder
    public static class OrderItem {
        // 기존 필드
        private Long orderDetailId;    // 주문 완료 후에만 사용
        private String productName;
        private Integer quantity;
        private Integer price;
        private List<String> optionNames;

        // 결제 준비용 추가 필드
        private String productPhoto;   // 추가: 상품 이미지
        private Integer unitPrice;     // 추가: 단가
        private Integer lineTotal;     // 추가: 총액
        private Long productId;        // 추가: 상품 ID
        private List<Long> optionIds;  // 추가: 옵션 ID 리스트
    }

    /**
     * 토스 결제용 주문 아이템 리스트로 변환
     * toss-checkout.jsp에서 사용
     */
    public List<Map<String, Object>> toTossOrderItems() {
        if (orderItems == null || orderItems.isEmpty()) {
            return Collections.emptyList();
        }

        return orderItems.stream()
                .map(item -> {
                    Map<String, Object> orderItem = new HashMap<>();
                    orderItem.put("productId", item.getProductId());
                    orderItem.put("quantity", item.getQuantity());
                    orderItem.put("price", item.getUnitPrice()); // 단가 사용
                    orderItem.put("optionIds", item.getOptionIds() != null ?
                            item.getOptionIds() : Collections.emptyList());
                    return orderItem;
                })
                .collect(Collectors.toList());
    }

    /**
     * JSP checkout.jsp에서 사용하는 directItems 형식으로 변환
     * 기존 checkout.jsp 호환성 유지용
     */
    public List<Map<String, Object>> toDirectItemsMap() {
        if (orderItems == null || orderItems.isEmpty()) {
            return Collections.emptyList();
        }

        return orderItems.stream()
                .map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("productId", item.getProductId());
                    map.put("productName", item.getProductName());
                    map.put("productPhoto", item.getProductPhoto());
                    map.put("quantity", item.getQuantity());
                    map.put("unitPrice", item.getUnitPrice());
                    map.put("price", item.getPrice());
                    map.put("lineTotal", item.getLineTotal());
                    map.put("optionIds", item.getOptionIds() != null ?
                            item.getOptionIds() : Collections.emptyList());
                    map.put("optionNames", item.getOptionNames() != null ?
                            item.getOptionNames() : Collections.emptyList());
                    return map;
                })
                .collect(Collectors.toList());
    }

}
