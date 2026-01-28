package com.codepresso.codepresso.review.repository;

import com.codepresso.codepresso.review.dto.MyReviewProjection;
import com.codepresso.codepresso.order.entity.OrdersDetail;
import com.codepresso.codepresso.product.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.ordersDetail od WHERE od.product.id = :productId")
    List<Review> findByProductReviews(@Param("productId") Long productId);

    boolean existsByOrdersDetail(OrdersDetail ordersDetail);

    @Query("SELECT AVG(r.rating) FROM Review r LEFT JOIN r.ordersDetail od WHERE od.product.id = :productId")
    Double getAverageRatingByProduct(Long productId);

    @Query("SELECT " +
            "r.id AS id, r.photoUrl AS photoUrl, r.content AS content, r.rating AS rating, r.createdAt AS createdAt, " +
            "p.id AS productId, p.productName AS productName, p.productPhoto AS productPhoto, " +
            "b.branchName AS branchName, os.orderDate AS orderDate " +
            "FROM Review r " +
            "JOIN r.ordersDetail od " +
            "JOIN od.orders os " +
            "JOIN os.branch b " +
            "JOIN od.product p " +
            "WHERE r.member.id = :memberId " +
            "ORDER BY r.createdAt desc")
    List<MyReviewProjection> findByMemberId(@Param("memberId") Long memberId);


}