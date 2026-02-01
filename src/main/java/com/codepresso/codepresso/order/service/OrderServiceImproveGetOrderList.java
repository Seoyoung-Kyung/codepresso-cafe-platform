package com.codepresso.codepresso.order.service;

import com.codepresso.codepresso.order.converter.OrderConverter;
import com.codepresso.codepresso.order.dto.OrderDetailResponse;
import com.codepresso.codepresso.order.dto.OrderListResponse;
import com.codepresso.codepresso.order.dto.OrderSummaryDto;
import com.codepresso.codepresso.order.repository.OrdersDetailRepository;
import com.codepresso.codepresso.order.repository.OrdersRepository;
import com.codepresso.codepresso.review.dto.OrdersDetailResponse;
import com.codepresso.codepresso.order.entity.Orders;
import com.codepresso.codepresso.order.entity.OrdersDetail;
import com.codepresso.codepresso.order.entity.OrdersItemOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderServiceImproveGetOrderList {

    private final OrdersRepository ordersRepository;
    private final OrdersDetailRepository ordersDetailRepository;
    private final OrderConverter orderConverter;

    /**
     * 주문 목록 조회
     */
    public OrderListResponse getOrderList(Long memberId, String period, int page, int size) {
        // 기간 계산
        LocalDateTime startDate = calculateStartDate(period);

        // Pageable 객체 생성 (페이지 번호, 사이즈, 정렬 조건)
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());

        // 전체 건수 (기간 무관)
        long totalCount = ordersRepository.countByMemberId(memberId);

        List<OrderListResponse.OrderSummary> orderSummaries = new ArrayList<>();
        long totalElements;
        int totalPages;
        boolean hasNext;
        boolean hasPrevious;

        if ("전체".equals(period)) {
            Page<OrderSummaryDto> projectionsPage = ordersRepository.findByMemberIdWithPaging2(memberId, pageable);

            for (OrderSummaryDto projection : projectionsPage.getContent()) {
                OrderListResponse.OrderSummary orderSummary = convertProjectionToOrderSummary(projection);
                orderSummaries.add(orderSummary);
            }

            totalElements = projectionsPage.getTotalElements();
            totalPages = projectionsPage.getTotalPages();
            hasNext = projectionsPage.hasNext();
            hasPrevious = projectionsPage.hasPrevious();
        } else {
            // 기존 방식 (기간 필터가 있는 경우)
            Page<OrderSummaryDto> ordersPage = ordersRepository.findByMemberIdAndDateWithPaging2(memberId, startDate, pageable);

            for (OrderSummaryDto orderSummaryDto : ordersPage.getContent()) {
                OrderListResponse.OrderSummary orderSummary = convertToOrderSummary(orderSummaryDto);
                orderSummaries.add(orderSummary);
            }

            totalElements = ordersPage.getTotalElements();
            totalPages = ordersPage.getTotalPages();
            hasNext = ordersPage.hasNext();
            hasPrevious = ordersPage.hasPrevious();
        }

        // 페이징 정보 포함하여 반환
        return OrderListResponse.builder()
                .orders(orderSummaries)
                .totalCount(totalCount)
                .filteredCount((int) totalElements) // 기간 필터 적용된 전체 수
                .currentPage(page)
                .totalPages(totalPages)
                .pageSize(size)
                .hasNext(hasNext)
                .hasPrevious(hasPrevious)
                .build();
    }

    /**
     * 주문 상세 조회
     */
    public OrderDetailResponse getOrderDetail(Long orderId) {
        // 1차 쿼리: Orders + Branch + Member + OrdersDetails + Product
        Orders orders = ordersRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        return convertToOrderDetail(orders);
    }

    /**
     * OrderSummaryProjection을 OrderListResponse.OrderSummary로 변환
     * isRepresentative를 활용한 최적화된 쿼리 결과를 변환
     */
    private OrderListResponse.OrderSummary convertProjectionToOrderSummary(OrderSummaryDto projection) {
        // TODO: orderNumber 생성을 위해서는 Orders 엔티티가 필요하므로 임시로 간단한 형식 사용
        // 실제로는 주문번호 생성 로직을 별도로 처리하거나 Projection에 포함시켜야 함
        String orderNumber = projection.getOrderId().toString();

        return OrderListResponse.OrderSummary.builder()
                .orderId(projection.getOrderId())
                .orderNumber(orderNumber)
                .orderDate(projection.getOrderDate())
                .productionStatus(projection.getProductionStatus())
                .branchName(projection.getBranchName())
                .isTakeout(projection.getIsTakeout())
                .pickupTime(projection.getPickupTime())
                .totalAmount(projection.getTotalAmount())
                .representativeName(projection.getRepresentativeProductName())
                .build();
    }

    private OrderListResponse.OrderSummary convertToOrderSummary(OrderSummaryDto projection) {
        // 대표 상품명 계산
        String orderNumber = projection.getOrderId().toString();

        return OrderListResponse.OrderSummary.builder()
                .orderId(projection.getOrderId())
                .orderNumber(orderNumber)
                .orderDate(projection.getOrderDate())
                .productionStatus(projection.getProductionStatus())
                .branchName(projection.getBranchName())
                .isTakeout(projection.getIsTakeout())
                .pickupTime(projection.getPickupTime())
                .totalAmount(projection.getTotalAmount())
                .representativeName(projection.getRepresentativeProductName())
                .build();
    }

    private OrderDetailResponse convertToOrderDetail(Orders orders) {
        // 주문 상품 목록 변환
        List<OrderDetailResponse.OrderItem> orderItems = new ArrayList<>();
        for (OrdersDetail detail : orders.getOrdersDetails()) {
            OrderDetailResponse.OrderItem orderItem = convertToOrderItem(detail);
            orderItems.add(orderItem);
        }

        // 지점 정보
        OrderDetailResponse.BranchInfo branch = OrderDetailResponse.BranchInfo.builder()
                .branchId(orders.getBranch().getId())
                .branchName(orders.getBranch().getBranchName())
                .address(orders.getBranch().getAddress())
                .branchNumber(orders.getBranch().getBranchNumber())
                .build();

        // 결제 정보 생성
        int totalAmount = orders.getTotalAmount() != null ? orders.getTotalAmount() : 0;
        int discount = orders.getDiscountAmount() != null ? orders.getDiscountAmount() : 0;
        int finalAmount = orders.getFinalAmount() != null ? orders.getFinalAmount() : 0;

        OrderDetailResponse.PaymentInfo payment = OrderDetailResponse.PaymentInfo.builder()
                .paymentMethod("신용카드")
                .totalAmount(totalAmount)
                .discount(discount)
                .finalAmount(finalAmount)
                .paymentDate(orders.getOrderDate())
                .build();

        return OrderDetailResponse.builder()
                .orderId(orders.getId())
                .orderNumber(generateOrderNumber(orders))
                .orderDate(orders.getOrderDate())
                .productionStatus(orders.getProductionStatus())
                .pickupTime(orders.getPickupTime())
                .isTakeout(orders.getIsTakeout())
                .requestNote(orders.getRequestNote())
                .branch(branch)
                .orderItems(orderItems)
                .payment(payment)
                .build();
    }

    private OrderDetailResponse.OrderItem convertToOrderItem(OrdersDetail detail) {
        // 옵션들 수집 (기본 옵션 제외)
        List<OrderDetailResponse.OrderOption> options = new ArrayList<>();
        int optionExtraPrice = 0;

        if (detail.getOptions() != null) {
            for (OrdersItemOptions option : detail.getOptions()) {
                String optionStyle = option.getOption().getOptionStyle().getOptionStyle();
                Integer extraPrice = option.getOption().getOptionStyle().getExtraPrice();

                // "기본" 옵션 제외
                if (!"기본".equals(optionStyle)) {
                    options.add(OrderDetailResponse.OrderOption.builder()
                            .optionStyle(optionStyle)
                            .extraPrice(extraPrice)
                            .build());

                    if (extraPrice != null) {
                        optionExtraPrice += extraPrice;
                    }
                }
            }
        }

        int basePrice = detail.getProduct().getPrice();  // 상품 기본 가격
        int unitPrice = basePrice + optionExtraPrice;    // 단가 = 기본가격 + 옵션가격
        int quantity = detail.getQuantity() != null ? detail.getQuantity() : 1;
        int totalPriceBeforeDiscount = unitPrice * quantity;  // 할인 전 총액 = 단가 × 수량

        return OrderDetailResponse.OrderItem.builder()
                .orderDetailId(detail.getId())
                .productName(detail.getProduct().getProductName())
                .quantity(quantity)
                .price(basePrice)
                .totalPrice(totalPriceBeforeDiscount)
                .options(options)
                .build();
    }

    private String calculateRepresentativeItem(List<OrdersDetail> orderDetails) {
        if (orderDetails.isEmpty()) {
            return "주문 상품 없음";
        }

        String firstProductName = orderDetails.get(0).getProduct().getProductName();
        int totalItems = orderDetails.size();

        if (totalItems == 1) {
            return firstProductName;
        } else {
            return firstProductName + " 외 " + (totalItems - 1) + "개";
        }
    }

    private String generateOrderNumber(Orders orders) {
        LocalDateTime orderDate = orders.getOrderDate();
        String phone = orders.getMember() != null ? orders.getMember().getPhone() : null;
        String last4 = "0000";
        if (phone != null) {
            String digits = phone.replaceAll("\\D", "");
            if (digits.length() >= 4) last4 = digits.substring(digits.length() - 4);
        }
        LocalDateTime startOfDay = orderDate.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        long seq = ordersRepository.countByOrderDateBetweenAndOrderDateLessThanEqual(startOfDay, endOfDay, orderDate);
        if (seq < 1) seq = 1; // 안전장치
        return String.format("%s-%d", last4, seq);
    }

    /**
     * 기간별 시작일 계산
     */
    private LocalDateTime calculateStartDate(String period) {
        LocalDateTime now = LocalDateTime.now();

        switch (period) {
            case "1개월":
                return now.minusMonths(1);
            case "3개월":
                return now.minusMonths(3);
            case "전체":
                return LocalDateTime.of(2020, 1, 1, 0, 0);
            default:
                return now.minusMonths(1); // 기본값: 1개월
        }
    }

    /**
     * 특정 주문 상품
     */
    public OrdersDetailResponse getOrdersDetail(Long orderDetailId) {
        OrdersDetail ordersDetail = ordersDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new RuntimeException("OrdersDetail not found: " + orderDetailId));

        return orderConverter.toDto(ordersDetail);
    }

    /**
     * 주문 제품들의 가격 합
     */
    private int calculateTotalAmount(Orders orders) {
        int totalAmount = 0;
        for (OrdersDetail detail : orders.getOrdersDetails()) {
            totalAmount += detail.getPrice();
        }
        return totalAmount;
    }

}
