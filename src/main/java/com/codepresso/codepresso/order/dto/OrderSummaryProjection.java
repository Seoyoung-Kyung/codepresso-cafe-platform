package com.codepresso.codepresso.order.dto;

import java.time.LocalDateTime;

public interface OrderSummaryProjection {
    Long getOrderId();
    LocalDateTime getOrderDate();
    String getProductionStatus();
    String getBranchName();
    Boolean getIsTakeout();
    LocalDateTime getPickupTime();
    Integer getTotalAmount();
    String getRepresentativeProductName();
    Long getRepresentativeProductId();
}
