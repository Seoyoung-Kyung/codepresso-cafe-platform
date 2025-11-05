package com.codepresso.codepresso.product.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductOptionDTO {
    private Long optionStyleId;

    private Long optionId;

    private String optionName;

    private String optionStyleName;

    private Integer extraPrice;

}