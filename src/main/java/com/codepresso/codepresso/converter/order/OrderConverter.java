package com.codepresso.codepresso.converter.order;

import com.codepresso.codepresso.dto.review.OrdersDetailResponse;
import com.codepresso.codepresso.entity.branch.Branch;
import com.codepresso.codepresso.entity.order.Orders;
import com.codepresso.codepresso.entity.order.OrdersDetail;
import com.codepresso.codepresso.entity.product.Product;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderConverter {

    public OrdersDetailResponse toDto(OrdersDetail ordersDetail) {
        return OrdersDetailResponse.builder()
                .orderDetailId(ordersDetail.getId())
                .productName(getProductName(ordersDetail))
                .productPhoto(getProductPhoto(ordersDetail))
                .branchName(getBranchName(ordersDetail))
                .build();
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
}
