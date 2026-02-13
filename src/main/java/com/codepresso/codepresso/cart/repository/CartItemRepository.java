package com.codepresso.codepresso.cart.repository;

import com.codepresso.codepresso.cart.entity.CartItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteByCartId(@Param("cartId") Long cartId);

    @Query("""
            SELECT DISTINCT ci FROM CartItem ci
            LEFT JOIN FETCH ci.product p
            LEFT JOIN FETCH ci.cartOptions co
            LEFT JOIN FETCH co.productOption po
            LEFT JOIN FETCH po.optionStyle
            WHERE ci.cart.id = :cartId
            """)
    List<CartItem> findCartItemsWithOptions(@Param("cartId") Long cartId);

    int countCartItemByCart_Member_Id(Long memberId);

}