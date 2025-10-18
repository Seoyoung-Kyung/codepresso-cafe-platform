package com.codepresso.codepresso.dto.review;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewListResponse {
    private Long reviewId;
    private String nickname;
    private BigDecimal rating;
    private Double avgRating;
    private String content;
    private String photoUrl;
    private LocalDateTime createdAt;

}