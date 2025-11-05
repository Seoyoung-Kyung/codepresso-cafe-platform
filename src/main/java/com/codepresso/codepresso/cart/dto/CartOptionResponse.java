package com.codepresso.codepresso.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartOptionResponse {
    private Long optionId;
    private Integer extraPrice;
    private String optionStyle;
}