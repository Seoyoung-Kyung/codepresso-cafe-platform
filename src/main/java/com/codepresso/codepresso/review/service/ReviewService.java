package com.codepresso.codepresso.review.service;

import com.codepresso.codepresso.review.repository.ReviewRepository;
import com.codepresso.codepresso.review.converter.ReviewConverter;
import com.codepresso.codepresso.member.entity.Member;
import com.codepresso.codepresso.order.entity.OrdersDetail;
import com.codepresso.codepresso.product.entity.Review;
import com.codepresso.codepresso.common.exception.ReviewNotFoundException;
import com.codepresso.codepresso.common.exception.UnauthorizedAccessException;
import com.codepresso.codepresso.member.repository.MemberRepository;
import com.codepresso.codepresso.order.repository.OrdersDetailRepository;
import com.codepresso.codepresso.review.dto.MyReviewProjection;
import com.codepresso.codepresso.review.dto.ReviewCreateRequest;
import com.codepresso.codepresso.review.dto.ReviewResponse;
import com.codepresso.codepresso.review.dto.ReviewUpdateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepo;
    private final MemberRepository memberRepo;
    private final OrdersDetailRepository ordersDetailRepo;
    private final ReviewConverter reviewConverter;

    @Transactional
    public ReviewResponse createReview(Long memberId, ReviewCreateRequest request, String photoUrl) {
        Long orderDetailId = request.getOrderDetailId();

        // 1. 회원 조회 및 검증
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다: " + memberId));

        // 2. 주문상세 조회 및 검증
        OrdersDetail ordersDetail = ordersDetailRepo.findById(orderDetailId)
                .orElseThrow(() -> new IllegalArgumentException("주문 상세를 찾을 수 없습니다: " + orderDetailId));

        // 3. 권한 검증 - 본인 주문인지 확인
        if (!ordersDetail.getOrders().getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인 주문에만 리뷰 작성 가능합니다");
        }

        if (reviewRepo.existsByOrdersDetail(ordersDetail)) {
            throw new IllegalArgumentException("이미 해당 주문에 대한 리뷰가 존재합니다");
        }

        Review review = Review.builder()
                .rating(request.getRating())
                .content(request.getContent())
                .photoUrl(photoUrl)
                .member(member)
                .ordersDetail(ordersDetail)
                .createdAt(LocalDateTime.now())
                .build();

        Review savedReview = saveReview(review);

        return reviewConverter.toDto(savedReview);
    }

    @Transactional
    public ReviewResponse editReview(Long memberId, Long reviewId, ReviewUpdateRequest request) {
        Review review = validateReviewOwnership(memberId, reviewId);

        review = Review.builder()
                .id(review.getId())
                .ordersDetail(review.getOrdersDetail())
                .member(review.getMember())
                .content(request.getContent())
                .rating(request.getRating())
                .photoUrl(request.getPhotoUrl())
                .createdAt(review.getCreatedAt())
                .build();

        Review savedReview = saveReview(review);

        return reviewConverter.toDto(savedReview);
    }

    @Transactional
    public void deleteReview(Long memberId, Long reviewId) {
        validateReviewOwnership(memberId, reviewId);

        reviewRepo.deleteById(reviewId);
    }

    @Transactional
    public List<MyReviewProjection> getReviewsByMember(Long memberId) {
        return reviewRepo.findByMemberId(memberId);
    }

    @Transactional
    public ReviewResponse getReview(Long memberId, Long reviewId) {
        Review review = validateReviewOwnership(memberId, reviewId);
        return reviewConverter.toDto(review);
    }

    private Review saveReview(Review review) {
        return reviewRepo.save(review);
    }

    private Review validateReviewOwnership(Long memberId, Long reviewId) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다"));

        // 권한 확인
        if (!review.getMember().getId().equals(memberId)) {
            throw new UnauthorizedAccessException("본인의 리뷰만 수정/삭제할 수 있습니다");
        }

        return review;
    }

}
