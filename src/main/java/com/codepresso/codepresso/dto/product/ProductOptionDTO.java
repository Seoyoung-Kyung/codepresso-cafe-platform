package com.codepresso.codepresso.dto.product;

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