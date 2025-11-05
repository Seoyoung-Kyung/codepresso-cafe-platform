package com.codepresso.codepresso.product.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductListResponse {
    private Long productId;

    private String productName;

    private String productPhoto;

    private Integer price;

    private String categoryName;

    private String categoryCode;
}