package com.codepresso.codepresso.cart.repository;

import com.codepresso.codepresso.cart.entity.CartOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartOptionRepository extends JpaRepository<CartOption, Long> {

    @Modifying
    @Query("DELETE FROM CartOption co WHERE co.cartItem.id = :cartItemId")
    void deleteByCartItemId(@Param("cartItemId") Long cartItemId);

    @Query("SELECT co FROM CartOption co WHERE co.cartItem.cart.id = :cartId")
    List<CartOption> findByCartId(@Param("cartId") Long cartId);

    @Modifying
    @Query("DELETE FROM CartOption co WHERE co.cartItem.cart.id = :cartId")
    void deleteByCartId(@Param("cartId") Long cartId);

}