package com.codepresso.codepresso.controller.review;

import com.codepresso.codepresso.dto.review.MyReviewProjection;
import com.codepresso.codepresso.dto.review.OrdersDetailResponse;
import com.codepresso.codepresso.dto.review.ReviewResponse;
import com.codepresso.codepresso.security.LoginUser;
import com.codepresso.codepresso.service.order.OrderService;
import com.codepresso.codepresso.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.codepresso.codepresso.dto.review.ReviewCreateRequest;
import com.codepresso.codepresso.dto.review.ReviewUpdateRequest;
import com.codepresso.codepresso.service.review.ReviewFileUploadService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class ReviewViewController {

    private final ReviewService reviewService;
    private final OrderService orderService;
    private final ReviewFileUploadService fileUploadService;

    /**
     * 리뷰 작성
     */
    @PostMapping("/reviews/create")
    public String createReview(@AuthenticationPrincipal LoginUser loginUser,
                               @ModelAttribute ReviewCreateRequest request) {
        Long memberId = loginUser.getMemberId();

        // 파일 업로드 처리
        String photoUrl = null;
        if (request.getPhotos() != null && !request.getPhotos().isEmpty()) {
            try {
                photoUrl = fileUploadService.saveFile(request.getPhotos());
            } catch (Exception e) {
                // 파일 업로드 실패 시 로그 처리
            }
        }

        reviewService.createReview(memberId, request, photoUrl);
        return "redirect:/users/myReviews";
    }

    /**
     * 리뷰 작성 폼으로 이동
     */
    @PostMapping("/reviews")
    public String writeReviewForm(@AuthenticationPrincipal LoginUser loginUser,
                               @RequestParam Long orderDetailId,
                               Model model) {
        OrdersDetailResponse orderDetail = orderService.getOrdersDetail(orderDetailId);
        model.addAttribute("orderDetail", orderDetail);

        return "review/writeReviewForm";
    }

    /**
     * 리뷰 수정 폼으로 이동
     */
    @GetMapping("/reviews/{reviewId}/edit")
    public String editReviewForm(@AuthenticationPrincipal LoginUser loginUser,
                                 @PathVariable Long reviewId,
                                 Model model) {
        Long memberId = loginUser.getMemberId();

        ReviewResponse review = reviewService.getReview(memberId, reviewId);

        model.addAttribute("review", review);
        model.addAttribute("reviewId", reviewId);
        model.addAttribute("isEdit", true);

        return "review/writeReviewForm";
    }

    /**
     * 리뷰 수정 처리
     */
    @PostMapping("/reviews/update")
    public String updateReview(@AuthenticationPrincipal LoginUser loginUser,
                               @RequestParam Long reviewId,
                               @RequestParam(required = false) String removePhoto,
                               @ModelAttribute ReviewUpdateRequest request,
                               RedirectAttributes redirectAttributes) {
        Long memberId = loginUser.getMemberId();

        try {
            // 파일 업로드 처리
            String photoUrl = request.getPhotoUrl(); // 기존 사진 URL 유지

            // 사진 제거 요청이 있는 경우
            if ("true".equals(removePhoto)) {
                photoUrl = null;
            } else if (request.getPhotos() != null && !request.getPhotos().isEmpty()) {
                try {
                    photoUrl = fileUploadService.saveFile(request.getPhotos());
                } catch (Exception e) {
                    // 파일 업로드 실패 시 기존 URL 유지
                }
            }

            // 사진 URL 설정
            request.setPhotoUrl(photoUrl);

            reviewService.editReview(memberId, reviewId, request);
            redirectAttributes.addFlashAttribute("message", "리뷰가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "리뷰 수정 중 오류가 발생했습니다.");
        }

        return "redirect:/users/myReviews";
    }

    /**
     * 내가 작성한 리뷰 조회
     */
    @GetMapping("/myReviews")
    public String getMyReviews(@AuthenticationPrincipal LoginUser loginUser, Model model) {
        Long memberId = loginUser.getMemberId();

        List<MyReviewProjection> userReviews = reviewService.getReviewsByMember(memberId);

        model.addAttribute("userReviews", userReviews);
        return "member/myReview";
    }

}