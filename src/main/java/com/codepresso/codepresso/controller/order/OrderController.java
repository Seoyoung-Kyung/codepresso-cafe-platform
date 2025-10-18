package com.codepresso.codepresso.controller.order;

import com.codepresso.codepresso.dto.order.OrderDetailResponse;
import com.codepresso.codepresso.dto.order.OrderListResponse;
import com.codepresso.codepresso.security.LoginUser;
import com.codepresso.codepresso.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 주문 관련 컨트롤러
 * */

@RequiredArgsConstructor
@RequestMapping("/users/orders")
@RestController
public class OrderController {

    private final OrderService orderService;

    /**
     * 사용자별 주문 내역 조회 ( 사용자별 + 기간별 )
     * GET /users/orders?memberId=1&period=1개월
     * period : 1개월, 3개월, 전체 ( 기본값 : 1개월 )
     * */
    @GetMapping
    public ResponseEntity<OrderListResponse> getOrderList(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam(value = "period", defaultValue = "1개월") String period,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){

        Long memberId = loginUser.getMemberId();
        OrderListResponse response = orderService.getOrderList(memberId, period, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * 주문 상세 조회
     * /users/orders/{orderId}
     * */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable Long orderId){
        OrderDetailResponse response = orderService.getOrderDetail(orderId);
        return ResponseEntity.ok(response);
    }
}
