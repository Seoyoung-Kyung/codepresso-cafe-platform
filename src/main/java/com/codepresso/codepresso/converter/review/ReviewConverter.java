package com.codepresso.codepresso.converter.review;

import com.codepresso.codepresso.dto.review.OrdersDetailResponse;
import com.codepresso.codepresso.dto.review.ReviewListResponse;
import com.codepresso.codepresso.dto.review.ReviewResponse;
import com.codepresso.codepresso.entity.branch.Branch;
import com.codepresso.codepresso.entity.member.Member;
import com.codepresso.codepresso.entity.order.Orders;
import com.codepresso.codepresso.entity.order.OrdersDetail;
import com.codepresso.codepresso.entity.product.Product;
import com.codepresso.codepresso.entity.product.Review;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ReviewConverter {

    public ReviewResponse toDto(Review review) {

        OrdersDetail ordersDetail = review.getOrdersDetail();
        Member member = review.getMember();

        return ReviewResponse.builder()
                .reviewId(review.getId())
                .ordersDetailId(getOrdersDetailId(ordersDetail))
                .branchName(getBranchName(ordersDetail))
                .productName(getProductName(ordersDetail))
                .productPhoto(getProductPhoto(ordersDetail))
                .memberId(getMemberId(member))
                .rating(review.getRating())
                .content(review.getContent())
                .photoUrl(review.getPhotoUrl())
                .createdAt(review.getCreatedAt())
                .build();
    }

    public ReviewListResponse toDto(Review review, Double avgRating) {
        Member member = review.getMember();

        return ReviewListResponse.builder()
                .reviewId(review.getId())
                .nickname(member.getNickname())
                .rating(review.getRating())
                .avgRating(avgRating)
                .content(review.getContent())
                .photoUrl(review.getPhotoUrl())
                .createdAt(review.getCreatedAt())
                .build();
        }

    private Long getOrdersDetailId(OrdersDetail ordersDetail) {
        return Optional.ofNullable(ordersDetail)
                .map(OrdersDetail::getId)
                .orElse(null);
    }

    private String getBranchName(OrdersDetail ordersDetail) {
        return Optional.ofNullable(ordersDetail)
                .map(OrdersDetail::getOrders)
                .map(Orders::getBranch)
                .map(Branch::getBranchName)
                .orElse("Unknown");
    }

    private String getProductName(OrdersDetail ordersDetail) {
        return Optional.ofNullable(ordersDetail)
                .map(OrdersDetail::getProduct)
                .map(Product::getProductName)
                .orElse("Unknown");
    }

    private String getProductPhoto(OrdersDetail ordersDetail) {
        return Optional.ofNullable(ordersDetail)
                .map(OrdersDetail::getProduct)
                .map(Product::getProductPhoto)
                .orElse("Unknown");
    }

    private Long getMemberId(Member member) {
        return Optional.ofNullable(member)
                .map(Member::getId)
                .orElse(null);
    }

}
