package com.codepresso.codepresso.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
    private Long cartItemId;
    private Long productId;
    private Integer quantity;
    private String productName;
    private Integer price;
    private List<CartOptionResponse> options;
    private String productPhoto;
}
