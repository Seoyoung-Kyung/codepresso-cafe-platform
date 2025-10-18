package com.codepresso.codepresso.repository.cart;

import com.codepresso.codepresso.entity.cart.CartItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    //특정 장바구니의 모든 아이템 조회
    List<CartItem> findByCartId (Long cartId);

    //단일 아이템 조회
    Optional<CartItem> findById(Long cartItemId);

    //특정 장바구니 + 상품의 아이템들 전체 조회(옵션까지 한번에 로딩)
    @EntityGraph(attributePaths = {"options", "options.productOption",
            "options.productOption.optionStyle",
            "product"})
    List<CartItem> findByCart_IdAndProduct_Id (Long cartId, Long productId);
}
