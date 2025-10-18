package com.codepresso.codepresso.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

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
