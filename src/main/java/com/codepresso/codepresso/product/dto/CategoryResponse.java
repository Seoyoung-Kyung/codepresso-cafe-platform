package com.codepresso.codepresso.product.dto;

import com.codepresso.codepresso.product.entity.Category;

public record CategoryResponse(
        Long id,
        String categoryName,
        String categoryCode,
        Byte level,
        Byte displayOrder
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getCategoryName(),
                category.getCategoryCode(),
                category.getLevel(),
                category.getDisplayOrder()
        );
    }
}
