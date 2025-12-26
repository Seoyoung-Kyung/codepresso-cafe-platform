package com.codepresso.codepresso.product.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NutritionInfoDto {
    private double calories;

    private double protein;

    private double fat;

    private double carbohydrate;

    private double saturatedFat;

    private double caffeine;

    private double transFat;

    private double sodium;

    private double sugar;

    private int cholesterol;

}
