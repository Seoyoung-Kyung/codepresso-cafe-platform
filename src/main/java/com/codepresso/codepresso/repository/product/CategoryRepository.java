package com.codepresso.codepresso.repository.product;

import com.codepresso.codepresso.entity.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c.displayOrder FROM Category c WHERE c.categoryCode = :categoryCode")
    int findByCategoryCode(String categoryCode);

}
