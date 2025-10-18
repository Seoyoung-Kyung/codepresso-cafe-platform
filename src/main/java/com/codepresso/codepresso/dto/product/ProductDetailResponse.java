package com.codepresso.codepresso.dto.product;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
@Builder
public class ProductDetailResponse {
    private Long productId;

    private String productName;

    private String productPhoto;

    private Integer price;

    private String productContent;

    private String categoryName;

    private long favCount;

    private Set<String> hashtags;

    private NutritionInfoDTO nutritionInfo;

    private Set<String> allergens;

    private List<ProductOptionDTO> productOptions;

}