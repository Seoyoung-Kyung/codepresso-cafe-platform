package com.codepresso.codepresso.product.converter;

import com.codepresso.codepresso.product.dto.NutritionInfoDTO;
import com.codepresso.codepresso.product.dto.ProductDetailResponse;
import com.codepresso.codepresso.product.dto.ProductListResponse;
import com.codepresso.codepresso.product.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductConverter {
    private static final String DEFAULT_CATEGORY = "COFFEE";
    private final ProductOptionConverter productOptConverter;

    public ProductDetailResponse toDetailDto(Product product, long favCount, List<ProductOption> options) {

        return ProductDetailResponse.builder()
                .productId(product.getId())
                .productName(product.getProductName())
                .productPhoto(product.getProductPhoto())
                .price(product.getPrice())
                .productContent(product.getProductContent())
                .categoryName(getCategoryName(product))
                .favCount(favCount)
                .hashtags(convertHashtags(product.getHashtags()))
                .nutritionInfo(getNutritionInfo(product.getNutritionInfo()))
                .allergens(convertAllergens(product.getAllergens()))
                .productOptions(productOptConverter.toListDto(options))
                .build();
    }

    private String getCategoryCode(Product product) {
        Category category = product.getCategory();

        return Optional.ofNullable(category)
                .map(Category::getCategoryCode)
                .orElse(DEFAULT_CATEGORY);
    }

    private String getCategoryName(Product product) {
        Category category = product.getCategory();

        return Optional.ofNullable(category)
                .map(Category::getCategoryName)
                .orElse("커피");
    }

    private Set<String> convertHashtags(Set<Hashtag> hashtags) {
        if (hashtags == null) { return Set.of(); }

        return hashtags.stream()
                .map(Hashtag::getHashtagName)
                .collect(Collectors.toSet());
    }

    private NutritionInfoDTO getNutritionInfo(NutritionInfo nutritionInfo) {
        if (nutritionInfo == null) {
            return null;
    }

        return NutritionInfoDTO.builder()
                .calories(nutritionInfo.getCalories())
                .protein(nutritionInfo.getProtein())
                .fat(nutritionInfo.getFat())
                .carbohydrate(nutritionInfo.getCarbohydrate())
                .saturatedFat(nutritionInfo.getSaturatedFat())
                .caffeine(nutritionInfo.getCaffeine())
                .transFat(nutritionInfo.getTransFat())
                .sodium(nutritionInfo.getSodium())
                .sugar(nutritionInfo.getSugar())
                .cholesterol(nutritionInfo.getCholesterol())
                .build();
    }

    private Set<String> convertAllergens(Set<Allergen> allergens) {
        if (allergens == null) { return Set.of(); }

        return allergens.stream()
                .map(Allergen::getAllergenName)
                .collect(Collectors.toSet());
    }

    public ProductListResponse toDto(Product product) {
        return ProductListResponse.builder()
                .productId(product.getId())
                .productName(product.getProductName())
                .productPhoto(product.getProductPhoto())
                .price(product.getPrice())
                .categoryName(getCategoryName(product))
                .categoryCode(getCategoryCode(product))
                .build();
    }

}