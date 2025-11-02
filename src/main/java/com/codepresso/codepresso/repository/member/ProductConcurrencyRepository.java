package com.codepresso.codepresso.repository.member;

import com.codepresso.codepresso.entity.member.Favorite;
import com.codepresso.codepresso.entity.product.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductConcurrencyRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Optional<Product> findByWithOutLock(@Param("productId") Long productId);

    /**
     * 비관적 락으로 즐겨찾기 조회
     * SELECT ... FOR UPDATE 사용
     */

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Optional<Product> findByWithPessimisticLock(@Param("productId") Long productId);

    /**
     * 낙관적 락으로 즐겨찾기 조회
     * 충돌 감지용 (실제로는 Favorite 엔티티에 @Version이 없어서 제한적)
     */

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Optional<Product> findByWithOptimisticLock(@Param("productId") Long productId);

    @Modifying
    @Query("UPDATE Product p SET p.favoriteCount = p.favoriteCount + 1 WHERE p.id = :productId")
    int incrementFavoriteCountAtomic(@Param("productId") Long productId);

    @Modifying
    @Query("UPDATE Product p SET p.favoriteCount = p.favoriteCount - 1 WHERE p.id = :productId AND p.favoriteCount > 0")
    int decrementFavoriteCountAtomic(@Param("productId") Long productId);

}
