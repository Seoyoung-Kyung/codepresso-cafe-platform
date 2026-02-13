package com.codepresso.codepresso.product.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutritionInfoRepository extends JpaRepository<NutritionInfo, Long> {

    NutritionInfo findNutritionInfoByProductId(Long productId);
}
