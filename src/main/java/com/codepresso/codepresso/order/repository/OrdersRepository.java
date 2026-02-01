package com.codepresso.codepresso.order.repository;

import com.codepresso.codepresso.order.dto.OrderSummaryDto;
import com.codepresso.codepresso.order.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    /**
     * 회원별 주문 목록 조회 (최신순 + 페이징)
     */
    @Query(value = "SELECT DISTINCT o FROM Orders o " +
            "LEFT JOIN FETCH o.branch " +
            "LEFT JOIN FETCH o.member " +
            "LEFT JOIN FETCH o.ordersDetails " +
            "WHERE o.member.id = :memberId " +
            "ORDER BY o.orderDate DESC",
            countQuery = "select count(distinct o) from Orders o where o.member.id = :memberId")
    Page<Orders> findByMemberIdWithPaging(@Param("memberId") Long memberId, Pageable pageable);

    @Query("""
               SELECT DISTINCT new com.codepresso.codepresso.order.dto.OrderSummaryDto(
                           o.id,
                           o.orderDate,
                           o.productionStatus,
                           b.branchName,
                           o.isTakeout,
                           o.pickupTime,
                           o.totalAmount,
                           p.productName,
                           p.id
                       )
                FROM Orders o
                LEFT JOIN o.branch b
                JOIN o.ordersDetails od
                JOIN od.product p
                WHERE o.member.id = :memberId AND od.isRepresentative = true
                ORDER BY o.orderDate DESC
            """)
    Page<OrderSummaryDto> findByMemberIdWithPaging2(@Param("memberId") Long memberId, Pageable pageable);

    /**
     * 회원별 + 기간별 주문 목록 조회 (최신순 + 페이징)
     */
    @Query("SELECT DISTINCT o FROM Orders o " +
            "LEFT JOIN FETCH o.branch " +
            "LEFT JOIN FETCH o.member " +
            "WHERE o.member.id = :memberId " +
            "AND o.orderDate >= :startDate " +
            "ORDER BY o.orderDate DESC")
    Page<Orders> findByMemberIdAndDateWithPaging(
            @Param("memberId") Long memberId,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable);

    /**
     * Improve
     */
    @Query("""
               SELECT DISTINCT new com.codepresso.codepresso.order.dto.OrderSummaryDto(
                           o.id,
                           o.orderDate,
                           o.productionStatus,
                           b.branchName,
                           o.isTakeout,
                           o.pickupTime,
                           o.totalAmount,
                           p.productName,
                           p.id
                       )
                FROM Orders o
                LEFT JOIN o.branch b
                JOIN o.ordersDetails od
                LEFT JOIN od.product p
                WHERE o.member.id = :memberId
                AND od.isRepresentative = true
                AND o.orderDate >= :startDate
                ORDER BY o.orderDate DESC
            """)
    Page<OrderSummaryDto> findByMemberIdAndDateWithPaging2(
            @Param("memberId") Long memberId,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable
    );

    /**
     * 해당 일자(startOfDay~endOfDay) 중에서 특정 주문시각(orderDate)까지 생성된 주문 수
     * (일일 순번 계산용)
     */
    @Query("SELECT COUNT(o) FROM Orders o " +
            "WHERE o.orderDate >= :startOfDay " +
            "AND o.orderDate <= :endOfDay " +
            "AND o.orderDate <= :orderDate")
    long countByOrderDateBetweenAndOrderDateLessThanEqual(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("orderDate") LocalDateTime orderDate);

    /**
     * 회원별 주문 개수 조회
     */
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.member.id = :memberId")
    long countByMemberId(Long memberId);

    /**
     * 주문 상세 조회
     * Orders -> Branch, Member, OrdersDetails, Product를 fetch join
     * OrdersDetail.options는 @BatchSize로 최적화 필요
     */
    @Query("SELECT DISTINCT o FROM Orders o " +
            "LEFT JOIN FETCH o.branch " +
            "LEFT JOIN FETCH o.member " +
            "LEFT JOIN FETCH o.ordersDetails od " +
            "LEFT JOIN FETCH od.product " +
            "WHERE o.id = :orderId")
    Optional<Orders> findByIdWithDetails(@Param("orderId") Long orderId);

}
