package com.codepresso.codepresso.product.repository;

import com.codepresso.codepresso.product.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    // 옵션 가져오기
    @Query("SELECT p FROM ProductOption p " +
            "LEFT JOIN FETCH p.optionStyle os " +
            "LEFT JOIN FETCH os.optionName " +
            "WHERE p.product.id = :productId")
    List<ProductOption> findOptionByProductId(@Param("productId") Long productId);
}