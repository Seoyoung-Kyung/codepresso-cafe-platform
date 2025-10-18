package com.codepresso.codepresso.controller.review;

import com.codepresso.codepresso.dto.review.MyReviewProjection;
import com.codepresso.codepresso.dto.review.ReviewResponse;
import com.codepresso.codepresso.dto.review.ReviewUpdateRequest;
import com.codepresso.codepresso.security.LoginUser;
import com.codepresso.codepresso.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/reviews")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 수정
     */
    @PatchMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> editReview(@AuthenticationPrincipal LoginUser loginUser,
                                                     @PathVariable Long reviewId,
                                                     @RequestBody ReviewUpdateRequest request) {
        Long memberId = loginUser.getMemberId();

        ReviewResponse review = reviewService.editReview(memberId, reviewId, request);
        return ResponseEntity.ok(review);
    }

    /**
     * 리뷰 삭제
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@AuthenticationPrincipal LoginUser loginUser,
                                             @PathVariable Long reviewId) {

        Long memberId = loginUser.getMemberId();
        reviewService.deleteReview(memberId, reviewId);

        return ResponseEntity.noContent().build(); // 상태 204, body 없음
    }

}