package com.codepresso.codepresso.dto.review;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponse {
    private Long reviewId;
    private Long ordersDetailId;
    private String branchName;
    private String productName;
    private String productPhoto;
    private Long memberId;
    private BigDecimal rating;
    private String content;
    private String photoUrl;
    private LocalDateTime createdAt;
}
