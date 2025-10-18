package com.codepresso.codepresso.dto.review;

import java.time.LocalDateTime;

public interface MyReviewProjection {
    Long getId();
    String getContent();
    String getPhotoUrl();
    int getRating();
    LocalDateTime getCreatedAt();
    Long getProductId();
    String getProductName();
    String getProductPhoto();
    String getBranchName();
    LocalDateTime getOrderDate();

    default String getFormattedDate() {
        LocalDateTime createdAt = getCreatedAt();
        if (createdAt == null) return "";
        return String.format("%04d.%02d.%02d",
            createdAt.getYear(),
            createdAt.getMonthValue(),
            createdAt.getDayOfMonth());
    }
}
