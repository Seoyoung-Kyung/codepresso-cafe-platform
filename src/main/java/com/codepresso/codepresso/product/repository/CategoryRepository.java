package com.codepresso.codepresso.product.repository;

import com.codepresso.codepresso.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c.displayOrder FROM Category c WHERE c.categoryCode = :categoryCode")
    int findByCategoryCode(String categoryCode);

}
