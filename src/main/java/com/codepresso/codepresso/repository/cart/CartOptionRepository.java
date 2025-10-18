package com.codepresso.codepresso.repository.cart;

import com.codepresso.codepresso.entity.cart.CartOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartOptionRepository extends JpaRepository<CartOption, Long> {

    //특정 CartItem에 속한 옵션 조회
    List<CartOption> findByCartItem_Id(Long cartItemId);

    //삭제
    //@Query("DELETE c FROM CartOption c WHERE c.id= :cartItemId")
    @Modifying
    @Query("delete from CartOption c where c.cartItem.id = :cartItemId")
    void deleteByCartItemId(Long cartItemId);
}
