package com.codepresso.codepresso.cart.repository;

import com.codepresso.codepresso.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long > {

    Optional<Cart> findByMemberId(Long memberId);

    /**
     * 장바구니 조회
     * */
    @Query("SELECT DISTINCT c FROM Cart c " +
            "LEFT JOIN FETCH c.items ci " +
            "LEFT JOIN FETCH ci.product " +
            "WHERE c.member.id = :memberId")
    Optional<Cart> findByMemberIdWithItems(@Param("memberId") Long memberId);
}
