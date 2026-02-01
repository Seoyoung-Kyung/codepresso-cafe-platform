package com.codepresso.codepresso.order.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderSummaryDto {

    private Long orderId;
    private LocalDateTime orderDate;
    private String productionStatus;
    private String branchName;
    private Boolean isTakeout;
    private LocalDateTime pickupTime;
    private Integer totalAmount;
    private String representativeProductName;
    private Long representativeProductId;
}

