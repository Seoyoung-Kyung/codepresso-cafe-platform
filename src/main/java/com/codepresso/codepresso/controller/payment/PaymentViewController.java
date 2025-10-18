package com.codepresso.codepresso.controller.payment;

import com.codepresso.codepresso.dto.payment.CheckoutResponse;
import com.codepresso.codepresso.security.LoginUser;
import com.codepresso.codepresso.service.coupon.CouponService;
import com.codepresso.codepresso.service.coupon.StampService;
import com.codepresso.codepresso.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * 결제 페이지 뷰 컨트롤러
 */
@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentViewController {

    private final PaymentService paymentService;
    private final CouponService couponService;
    private final StampService stampService;

    /**
     * 장바구니에서 결제페이지로
     * GET /payments/cart
     */
    @GetMapping("/cart")
    public String cartCheckoutPage(
            @AuthenticationPrincipal LoginUser loginUser,
            Model model) {
        try {
            if (loginUser == null) {
                return "redirect:/auth/login?redirect=/payments/cart";
            }

            CheckoutResponse checkoutData = paymentService.prepareCartCheckout(loginUser.getMemberId());

            Map<String, Object> cartData = new HashMap<>();
            cartData.put("items", checkoutData.getOrderItems());

            model.addAttribute("cartData", cartData);
            model.addAttribute("orderItems", checkoutData.getOrderItems());
            model.addAttribute("totalAmount", checkoutData.getTotalAmount());
            model.addAttribute("totalQuantity", checkoutData.getTotalQuantity());
            model.addAttribute("isFromCart", true);
            model.addAttribute("validCouponCount", couponService.getMemberValidCouponCount(loginUser.getMemberId()));
            model.addAttribute("stampCount",stampService.getMemberStamp(loginUser.getMemberId()));

        } catch (Exception e) {
            model.addAttribute("errorMessage", "장바구니 정보를 불러올 수 없습니다: " + e.getMessage());
            return "redirect:/cart?error=" + e.getMessage();
        }

        return "payment/checkout";
    }

    /**
     * 상품상세에서 바로 결제페이지로 (POST)
     * POST /payments/direct
     */
    @PostMapping("/direct")
    public String directCheckoutPost(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            @RequestParam(required = false) List<Long> optionIds,
            Model model
    ) {
        try {
            // 직접 주문 데이터 준비
            CheckoutResponse checkoutData = paymentService.prepareDirectCheckout(productId, quantity, optionIds);

            model.addAttribute("directItems", checkoutData.toDirectItemsMap());
            model.addAttribute("directItemsCount", checkoutData.getOrderItems().size());
            model.addAttribute("totalAmount", checkoutData.getTotalAmount());
            model.addAttribute("totalQuantity", checkoutData.getTotalQuantity());
            model.addAttribute("isFromCart", false);

            // 쿠폰 개수 추가 (로그인된 경우에만)
            if (loginUser != null) {
                model.addAttribute("validCouponCount", couponService.getMemberValidCouponCount(loginUser.getMemberId()));
            }
            model.addAttribute("stampCount",stampService.getMemberStamp(loginUser.getMemberId()));

            return "payment/checkout";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "결제 정보를 준비하는 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/products/" + productId + "?error=" + e.getMessage();
        }
    }

    /**
     * 토스페이먼츠 결제 페이지
     * POST /payments/toss-checkout
     */
    @PostMapping("/toss-checkout")
    public String tossCheckoutPage(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam Integer amount,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) List<Long> optionIds,
            Model model) {
        try {
            if (loginUser == null) {
                return "redirect:/auth/login?redirect=/payments/cart";
            }

            // 사용자 정보 전달
            model.addAttribute("memberId", loginUser.getMemberId());
            model.addAttribute("memberName", loginUser.getName());
            model.addAttribute("memberEmail", loginUser.getEmail());


            // 금액 정보 전달
            model.addAttribute("totalAmount", amount);

            // 직접결제인 경우 (productId가 있으면)
            if (productId != null && quantity != null) {
                CheckoutResponse checkoutData = paymentService.prepareDirectCheckout(productId, quantity, optionIds);
                model.addAttribute("directItems", checkoutData.toDirectItemsMap());
                model.addAttribute("directItemsCount", checkoutData.getOrderItems().size());
                model.addAttribute("totalQuantity", checkoutData.getTotalQuantity());
                model.addAttribute("isFromCart", false);
            }
            // 장바구니 결제인 경우
            else {
                CheckoutResponse checkoutData = paymentService.prepareCartCheckout(loginUser.getMemberId());
                model.addAttribute("orderItems", checkoutData.getOrderItems());
                model.addAttribute("totalQuantity", checkoutData.getTotalQuantity());
                model.addAttribute("isFromCart", true);
            }

            return "payment/toss-checkout";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "결제 페이지를 불러올 수 없습니다: " + e.getMessage());
            return "redirect:/payments/cart?error=" + e.getMessage();
        }
    }

    /**
     * 토스페이먼츠 결제 성공 페이지
     * GET /payments/success
     */
    @GetMapping("/success")
    public String paymentSuccessPage(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String paymentKey,
            @RequestParam(required = false) String amount,
            @RequestParam(required = false) String orderName,
            Model model) {
        
        try {
            model.addAttribute("orderId", orderId);
            model.addAttribute("paymentKey", paymentKey);
            model.addAttribute("amount", amount);
            model.addAttribute("orderName", orderName);

            return "payment/success";
        } catch (Exception e) {
            // 로그 출력
            System.err.println("Payment success page error: " + e.getMessage());
            e.printStackTrace();
            
            // 실패 페이지로 리다이렉트
            try {
                return "redirect:/payments/fail?message=" + 
                       java.net.URLEncoder.encode("결제 성공 페이지를 불러올 수 없습니다: " + e.getMessage(), "UTF-8") + 
                       "&code=SERVER_ERROR";
            } catch (java.io.UnsupportedEncodingException ex) {
                return "redirect:/payments/fail?message=Server%20Error&code=SERVER_ERROR";
            }
        }
    }

    /**
     * 토스페이먼츠 결제 실패 페이지
     * GET /payments/fail
     */
    @GetMapping("/fail")
    public String paymentFailPage(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String orderId,
            Model model) {
        
        try {
            model.addAttribute("code", code);
            model.addAttribute("message", message);
            model.addAttribute("orderId", orderId);
            
            return "payment/fail";
        } catch (Exception e) {
            // 로그 출력
            System.err.println("Payment fail page error: " + e.getMessage());
            e.printStackTrace();
            
            // 기본 실패 페이지로 리다이렉트
            try {
                return "redirect:/payments/fail?message=" + 
                       java.net.URLEncoder.encode("결제 실패 페이지를 불러올 수 없습니다: " + e.getMessage(), "UTF-8") + 
                       "&code=PAGE_ERROR";
            } catch (java.io.UnsupportedEncodingException ex) {
                return "redirect:/payments/fail?message=Page%20Error&code=PAGE_ERROR";
            }
        }
    }

}
