package com.codepresso.codepresso.dto.review;

import lombok.*;

@Getter
@Builder
public class OrdersDetailResponse {
    private Long orderDetailId;

    private String productName;

    private String productPhoto;

    private String branchName;
}