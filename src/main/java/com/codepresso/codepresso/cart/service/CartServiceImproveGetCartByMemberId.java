package com.codepresso.codepresso.cart.service;

import com.codepresso.codepresso.cart.converter.CartConverter;
import com.codepresso.codepresso.cart.dto.CartResponse;
import com.codepresso.codepresso.cart.entity.CartItem;
import com.codepresso.codepresso.cart.repository.CartItemRepository;
import com.codepresso.codepresso.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImproveGetCartByMemberId {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartConverter cartConverter;

    @Transactional(readOnly = true)
    public CartResponse getCartByMemberId(Long memberId) {

        Long cartId = cartRepository.findCartIdByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 회원의 장바구니가 존재하지 않습니다. memberId: " + memberId));

        List<CartItem> items = cartItemRepository.findCartItemsWithOptions(cartId);

        return cartConverter.toCartResponse(cartId, memberId, items);
    }

    @Transactional(readOnly = true)
    public int getCartItemsCount(Long memberId) {
        return cartItemRepository.countCartItemByCart_Member_Id(memberId);
    }
}
