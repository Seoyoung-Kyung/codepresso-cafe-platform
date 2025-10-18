package com.codepresso.codepresso.service.payment;

import com.codepresso.codepresso.dto.cart.CartItemResponse;
import com.codepresso.codepresso.dto.cart.CartOptionResponse;
import com.codepresso.codepresso.dto.cart.CartResponse;
import com.codepresso.codepresso.dto.payment.CheckoutRequest;
import com.codepresso.codepresso.dto.payment.CheckoutResponse;
import com.codepresso.codepresso.dto.payment.TossPaymentSuccessRequest;
import com.codepresso.codepresso.dto.product.ProductDetailResponse;
import com.codepresso.codepresso.dto.product.ProductOptionDTO;
import com.codepresso.codepresso.entity.branch.Branch;
import com.codepresso.codepresso.entity.member.Member;
import com.codepresso.codepresso.entity.order.Orders;
import com.codepresso.codepresso.entity.order.OrdersDetail;
import com.codepresso.codepresso.entity.order.OrdersItemOptions;
import com.codepresso.codepresso.repository.branch.BranchRepository;
import com.codepresso.codepresso.repository.member.MemberRepository;
import com.codepresso.codepresso.repository.order.OrdersRepository;
import com.codepresso.codepresso.service.cart.CartService;
import com.codepresso.codepresso.service.coupon.CouponService;
import com.codepresso.codepresso.service.coupon.StampService;
import com.codepresso.codepresso.service.product.ProductService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 결제 서비스
 */

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final MemberRepository memberRepository;
    private final OrdersRepository ordersRepository;
    private final BranchRepository branchRepository;
    private final CartService cartService;
    private final ProductService productService;
    private final CouponService couponService;
    private final StampService stampService;
    private final OrderCreationService orderCreationService;

    /**
     * 장바구니 결제페이지 데이터 준비
     */
    public CheckoutResponse prepareCartCheckout(Long memberId) {
        CartResponse cartData = cartService.getCartByMemberId(memberId);

        int totalAmount = cartData.getItems().stream()
                .mapToInt(CartItemResponse::getPrice)
                .sum();

        int totalQuantity = cartData.getItems().stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        List<CheckoutResponse.OrderItem> orderItems = cartData.getItems().stream()
                .map(this::convertCartItemToOrderItem)
                .collect(Collectors.toList());

        return buildCheckoutResponse(totalAmount, totalQuantity, true, orderItems);
    }

    /**
     * 직접 결제페이지 데이터 준비
     */
    public CheckoutResponse prepareDirectCheckout(Long productId, Integer quantity, List<Long> optionIds) {
        validateQuantity(quantity);

        ProductDetailResponse productDetail = productService.findByProductId(productId);
        List<ProductOptionDTO> selectedOptions = new ArrayList<>();
        int totalAmount = calculateTotalAmount(productDetail, optionIds, quantity, selectedOptions);

        CheckoutResponse.OrderItem orderItem = convertDirectItemToOrderItem(
                productDetail, selectedOptions, quantity, totalAmount, optionIds);

        return buildCheckoutResponse(totalAmount, quantity, false, Collections.singletonList(orderItem));
    }

    // ========== Private Helper Methods ==========

    /**
     * CheckoutResponse 빌더 공통 로직
     */
    private CheckoutResponse buildCheckoutResponse(
            Integer totalAmount,
            Integer totalQuantity,
            Boolean isFromCart,
            List<CheckoutResponse.OrderItem> orderItems) {

        return CheckoutResponse.builder()
                .totalAmount(totalAmount)
                .totalQuantity(totalQuantity)
                .isFromCart(isFromCart)
                .orderItems(orderItems)
                .build();
    }

    /**
     * 수량 검증
     */
    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
    }

    /**
     * CartItemResponse를 CheckoutResponse.OrderItem으로 변환
     */
    private CheckoutResponse.OrderItem convertCartItemToOrderItem(CartItemResponse cartItem) {
        int unitPrice = cartItem.getPrice() / cartItem.getQuantity();

        return CheckoutResponse.OrderItem.builder()
                .productId(cartItem.getProductId())
                .productName(cartItem.getProductName())
                .productPhoto(cartItem.getProductPhoto())
                .quantity(cartItem.getQuantity())
                .unitPrice(unitPrice)
                .price(cartItem.getPrice())
                .lineTotal(cartItem.getPrice())
                .optionIds(extractOptionIdsFromCartItem(cartItem))
                .optionNames(extractOptionNamesFromCartItem(cartItem))
                .build();
    }

    /**
     * 직접 결제 정보를 CheckoutResponse.OrderItem으로 변환
     */
    private CheckoutResponse.OrderItem convertDirectItemToOrderItem(
            ProductDetailResponse productDetail,
            List<ProductOptionDTO> selectedOptions,
            Integer quantity,
            Integer totalAmount,
            List<Long> optionIds) {

        int unitPrice = totalAmount / quantity;

        return CheckoutResponse.OrderItem.builder()
                .productId(productDetail.getProductId())
                .productName(productDetail.getProductName())
                .productPhoto(productDetail.getProductPhoto())
                .quantity(quantity)
                .unitPrice(unitPrice)
                .price(unitPrice)
                .lineTotal(totalAmount)
                .optionIds(optionIds != null ? optionIds : Collections.emptyList())
                .optionNames(extractOptionNamesFromProductOptions(selectedOptions))
                .build();
    }

    /**
     * CartItem 옵션 ID 추출
     */
    private List<Long> extractOptionIdsFromCartItem(CartItemResponse cartItem) {
        if (cartItem.getOptions() == null || cartItem.getOptions().isEmpty()) {
            return Collections.emptyList();
        }

        return cartItem.getOptions().stream()
                .map(CartOptionResponse::getOptionId)
                .collect(Collectors.toList());
    }

    /**
     * CartItem 옵션 이름 추출
     */
    private List<String> extractOptionNamesFromCartItem(CartItemResponse cartItem) {
        if (cartItem.getOptions() == null || cartItem.getOptions().isEmpty()) {
            return Collections.emptyList();
        }

        return cartItem.getOptions().stream()
                .map(CartOptionResponse::getOptionStyle)
                .collect(Collectors.toList());
    }

    /**
     * ProductOptionDTO 옵션 이름 추출
     */
    private List<String> extractOptionNamesFromProductOptions(List<ProductOptionDTO> selectedOptions) {
        if (selectedOptions == null || selectedOptions.isEmpty()) {
            return Collections.emptyList();
        }

        return selectedOptions.stream()
                .map(ProductOptionDTO::getOptionStyleName)
                .collect(Collectors.toList());
    }


    /**
     * 총 가격 계산 및 선택된 옵션 수집
     */
    private int calculateTotalAmount(ProductDetailResponse productDetail, List<Long> optionIds,
                                     Integer quantity, List<ProductOptionDTO> selectedOptions) {

        int basePrice = (productDetail.getPrice() != null) ? productDetail.getPrice() : 0;
        int optionPrice = 0;

        // 옵션이 선택된 경우
        if (optionIds != null && !optionIds.isEmpty()) {
            // 선택된 옵션들 찾기 및 가격 계산
            for (Long optionId : optionIds) {
                ProductOptionDTO foundOption = productDetail.getProductOptions().stream()
                        .filter(option -> option.getOptionId().equals(optionId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 옵션입니다: " + optionId));

                selectedOptions.add(foundOption);
                optionPrice += (foundOption.getExtraPrice() != null) ? foundOption.getExtraPrice() : 0;
            }
        }
        return (basePrice + optionPrice) * quantity;
    }

    /**
     * 토스페이먼츠 결제 성공 시 주문 생성
     */
    @Transactional
    public CheckoutResponse processTossPaymentSuccess(TossPaymentSuccessRequest request) {
        // 1. 회원 및 지점 정보 조회
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지점입니다."));

        // 2. 주문 생성
        Orders orders = createTossOrder(request, member, branch);

        // 3. 주문 상세 생성
        List<CheckoutRequest.OrderItem> checkoutItems = request.getOrderItems().stream()
                .map(this::convertToCheckoutOrderItem)
                .collect(Collectors.toList());

        // 4. 주문 저장
        List<OrdersDetail> ordersDetails = orderCreationService.createOrderDetails(checkoutItems,orders);
        orders.setOrdersDetails(ordersDetails);

        Orders savedOrder = ordersRepository.save(orders);

        // 5. 장바구니 비우기 로직 추가
        if (Boolean.TRUE.equals(request.getIsFromCart())) {
            try {
                CartResponse cartData = cartService.getCartByMemberId(member.getId());
                cartService.clearCart(member.getId(), cartData.getCartId());
                System.out.println("✅ 토스 결제 후 장바구니 비우기 성공");
            } catch (Exception e) {
                System.err.println("❌ 토스 결제 후 장바구니 비우기 실패: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // 6. 쿠폰 사용 처리
        if (Boolean.TRUE.equals(request.getUseCoupon()) && request.getDiscountAmount() != null && request.getDiscountAmount() > 0) {
            try {
                // 회원의 사용 가능한 쿠폰 중 첫 번째 쿠폰 사용
                var validCoupons = couponService.getMemberValidCoupons(member.getId());
                if (!validCoupons.isEmpty()) {
                    Long couponId = validCoupons.get(0).getCouponId();
                    couponService.useCoupon(couponId);
                }
            } catch (Exception e) {
                System.err.println("❌ 쿠폰 사용 처리 실패: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // stamp 적립
        try{
            stampService.earnStampsFromOrder(member.getId(), ordersDetails);
        }catch(Exception e){
            e.printStackTrace();
        }

        // 5. 응답 데이터 생성
        return buildCheckoutResponse(savedOrder);
    }

    private Orders createTossOrder(TossPaymentSuccessRequest request, Member member, Branch branch) {
        // String을 LocalDateTime으로 변환
        LocalDateTime pickupTime = null;
        if (request.getPickupTime() != null && !request.getPickupTime().isEmpty()) {
            try {
                pickupTime = LocalDateTime.parse(request.getPickupTime());
            } catch (Exception e) {
                // 파싱 실패 시 현재 시간 + 5분으로 설정
                pickupTime = LocalDateTime.now().plusMinutes(5);
            }
        } else {
            // pickupTime이 없으면 현재 시간 + 5분으로 설정
            pickupTime = LocalDateTime.now().plusMinutes(5);
        }

        int discountAmount = (request.getUseCoupon() != null && request.getUseCoupon() && request.getDiscountAmount() != null)
                ? request.getDiscountAmount() : 0;
        int finalAmount = request.getAmount() != null ? request.getAmount() : 0;
        int totalAmount = finalAmount + discountAmount;

        return Orders.builder()
                .member(member)
                .branch(branch)
                .productionStatus("픽업완료")
                .isTakeout(request.getIsTakeout())
                .pickupTime(pickupTime)
                .orderDate(LocalDateTime.now())
                .requestNote(request.getRequestNote())
                .isPickup(true)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .build();
    }

    /**
     * TossPaymentSuccessRequest.OrderItem을 CheckoutRequest.OrderItem으로 변환
     */
    private CheckoutRequest.OrderItem convertToCheckoutOrderItem(TossPaymentSuccessRequest.OrderItem tossItem) {
        return CheckoutRequest.OrderItem.builder()
                .productId(tossItem.getProductId())
                .quantity(tossItem.getQuantity())
                .price(tossItem.getPrice())
                .optionIds(tossItem.getOptionIds())
                .build();
    }

    private CheckoutResponse buildCheckoutResponse(Orders orders) {
        Orders fetchedOrders = ordersRepository.findByIdWithDetails(orders.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        // 주문 상세 정보 리스트 생성
        List<CheckoutResponse.OrderItem> orderItems = new ArrayList<>();

        // 각 주문 상세를 OrderItem으로 변환
        for (OrdersDetail detail : fetchedOrders.getOrdersDetails()) {
            // 옵션 이름들 수집
            List<String> optionNames = new ArrayList<>();
            if (detail.getOptions() != null) {
                for (OrdersItemOptions option : detail.getOptions()) {
                    optionNames.add(option.getOption().getOptionStyle().getOptionName().getOptionName());
                }
            }

            // OrderItem 생성
            CheckoutResponse.OrderItem orderItem = CheckoutResponse.OrderItem.builder()
                    .orderDetailId(detail.getId())
                    .productName(detail.getProduct().getProductName())
                    .quantity(detail.getQuantity() != null ? detail.getQuantity() : 1)
                    .price(detail.getPrice())
                    .optionNames(optionNames)
                    .build();

            orderItems.add(orderItem);
        }

        // 총 주문 금액 계산
        int totalAmount = 0;
        for (OrdersDetail detail : fetchedOrders.getOrdersDetails()) {
            totalAmount += detail.getPrice();
        }

        // 최종 응답 객체 생성
        return CheckoutResponse.builder()
                .orderId(fetchedOrders.getId())
                .productionStatus(fetchedOrders.getProductionStatus())
                .orderDate(fetchedOrders.getOrderDate())
                .pickupTime(fetchedOrders.getPickupTime())
                .isTakeout(fetchedOrders.getIsTakeout())
                .requestNote(fetchedOrders.getRequestNote())
                .totalAmount(totalAmount)
                .orderItems(orderItems)
                .build();
    }
}
