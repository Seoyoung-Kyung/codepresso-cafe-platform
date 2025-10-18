package com.codepresso.codepresso.controller.cart;

import com.codepresso.codepresso.dto.cart.*;
import com.codepresso.codepresso.entity.cart.CartItem;
import com.codepresso.codepresso.security.LoginUser;
import com.codepresso.codepresso.service.cart.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/cart")
public class CartController {

    private final CartService cartService;

    //장바구니 조회
    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal LoginUser loginUser) {
        return ResponseEntity.ok(cartService.getCartByMemberId(loginUser.getMemberId()));
    }

    //장바구니 아이템 개수 조회
    @GetMapping("/count")
    public ResponseEntity<Integer> getCartItemCount(@AuthenticationPrincipal LoginUser loginUser) {
        try {
            CartResponse cart = cartService.getCartByMemberId(loginUser.getMemberId());
            int totalCount = (int) cart.getItems().stream().count();
            return ResponseEntity.ok(totalCount);
        } catch (IllegalArgumentException e) {
            // 장바구니가 없으면 0 반환
            return ResponseEntity.ok(0);
        }
    }

    //장바구니 상품 추가
    @PostMapping
    public ResponseEntity<CartItemResponse> addItem(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam Long productId,
            @RequestParam int quantity,
            @RequestParam(required = false) List<Long> optionIds
    ){
        CartItem savedItem = cartService.addItemWithOptions(loginUser.getMemberId(), productId, quantity, optionIds);

        //cartItem -> DTO 변환
        CartItemResponse response = CartItemResponse.builder()
                .cartItemId(savedItem.getId())
                .productId(savedItem.getProduct().getId())
                .productName(savedItem.getProduct().getProductName())
                .quantity(savedItem.getQuantity())
                .price(savedItem.getPrice())
                .options(savedItem.getOptions().stream().map(opt ->
                        new CartOptionResponse(
                                opt.getProductOption().getId(),
                                opt.getProductOption().getOptionStyle().getExtraPrice(),
                                opt.getProductOption().getOptionStyle().getOptionStyle()
                        )
                ).toList())
                .build();

        return ResponseEntity.ok(response);
    }

    //장바구니 상품 수정
    @PatchMapping("/{cartItemId}")
    public ResponseEntity<String> updateQuantity(
            @PathVariable Long cartItemId,
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody CartItemUpdateRequest updateRequest
    ) {
        cartService.changeItemQuantity(cartItemId, loginUser.getMemberId(), updateRequest.getQuantity());
        return ResponseEntity.ok("수량이 변경되었습니다.");
    }

    //장바구니 상품 삭제
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<String> deleteItem(
            @PathVariable Long cartItemId,
           @AuthenticationPrincipal LoginUser loginUser
    ){
        cartService.deleteItem(cartItemId, loginUser.getMemberId());
        return ResponseEntity.ok("아이템이 삭제되었습니다.");
    }


    //장바구니 비우기
    @PostMapping("/clear")
    public ResponseEntity<String> clearCart(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam Long cartId
    ) {
        cartService.clearCart(loginUser.getMemberId(), cartId);
        return ResponseEntity.ok("장바구니가 비워졌습니다.");
    }
}
