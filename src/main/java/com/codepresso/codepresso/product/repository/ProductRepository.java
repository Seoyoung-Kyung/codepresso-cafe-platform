package com.codepresso.codepresso.product.repository;

import com.codepresso.codepresso.product.dto.ProductListResponse;
import com.codepresso.codepresso.product.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    interface ProductSummary {
        Long getProductId();
        String getProductName();
        String getProductPhoto();
        Integer getPrice();
        Long getCategoryId();
    }

    @Query("""
            SELECT p.id           AS productId,
                   p.productName  AS productName,
                   p.productPhoto AS productPhoto,
                   p.price        AS price,
                   p.category.id  AS categoryId
            FROM Product p
            ORDER BY p.category.displayOrder
            """)
    List<ProductSummary> findAllProducts();

    @Query("""
            SELECT new com.codepresso.codepresso.product.dto.ProductListResponse(
                p.id, p.productName, p.productPhoto, p.price, c.categoryName, c.categoryCode
            )
            FROM Product p
            LEFT JOIN p.category c
            ORDER BY c.displayOrder
            """)
    List<ProductListResponse> findAllProductsAsDto();

    @Query("SELECT p " +
            "FROM Product p " +
            "LEFT JOIN p.category c " +
            "ORDER BY c.displayOrder")
    List<Product> findAll();


    @Query("SELECT new com.codepresso.codepresso.product.dto.ProductListResponse(" +
            "p.id, p.productName, p.productPhoto, p.price, c.categoryName, c.categoryCode) " +
            "FROM Product p LEFT JOIN p.category c " +
            "ORDER BY FUNCTION('RAND')")
    List<ProductListResponse> findByProductRandom(Pageable pageable);

    @EntityGraph(attributePaths = {
            "category",
            "hashtags"
    })
    Product findProductById(@Param("id") Long id);

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.category " +
            "WHERE p.productName LIKE %:keyword%")
    List<Product> findByProductNameContaining(@Param("keyword") String keyword);

    @Query("SELECT new com.codepresso.codepresso.product.dto.ProductListResponse(" +
            "p1.id, p1.productName, p1.productPhoto, p1.price, p1.category.categoryName, p1.category.categoryCode) " +
            "FROM Product p1 " +
            "WHERE p1.id " +
            "IN(select distinct(p2.id)" +
            "FROM Product p2 join p2.hashtags h " +
            "WHERE h.hashtagName IN :hashtags " +
            "GROUP BY p2.id " +
            "HAVING COUNT(DISTINCT h.hashtagName) = :size)")
    List<ProductListResponse> findByHashtagsIn(
            @Param("hashtags") List<String> hashtags,
            @Param("size") long size
    );

}