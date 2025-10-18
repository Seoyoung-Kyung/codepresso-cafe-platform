package com.codepresso.codepresso.repository.order;

import com.codepresso.codepresso.entity.order.OrdersDetail;
import com.codepresso.codepresso.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersDetailRepository extends JpaRepository<OrdersDetail, Long> {
    Optional<OrdersDetail> findById(Long orderDetailId);

    @Query("SELECT d FROM OrdersDetail d " +
           "LEFT JOIN FETCH d.orders o " +
           "LEFT JOIN FETCH o.member")
    List<OrdersDetail> findAllWithOrdersAndMember();

}