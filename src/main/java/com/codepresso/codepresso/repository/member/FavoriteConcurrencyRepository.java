package com.codepresso.codepresso.repository.member;

import com.codepresso.codepresso.entity.member.Favorite;
import com.codepresso.codepresso.entity.product.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteConcurrencyRepository extends JpaRepository<Favorite, Long> {

    /**
     * 비관적 락으로 즐겨찾기 조회
     * SELECT ... FOR UPDATE 사용
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM Favorite f WHERE f.memberId = :memberId AND f.productId = :productId")
    Optional<Favorite> findByMemberIdAndProductIdWithPessimisticLock(
            @Param("memberId") Long memberId,
            @Param("productId") Long productId);

    /**
     * 낙관적 락으로 즐겨찾기 조회
     * 충돌 감지용 (실제로는 Favorite 엔티티에 @Version이 없어서 제한적)
     */
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT f FROM Favorite f WHERE f.memberId = :memberId AND f.productId = :productId")
    Optional<Favorite> findByMemberIdAndProductIdWithOptimisticLock(
            @Param("memberId") Long memberId,
            @Param("productId") Long productId);

    /**
     * 특정 상품에 대한 즐겨찾기 존재 여부 확인
     */
    boolean existsByMemberIdAndProductId(Long memberId, Long productId);

    /**
     * 특정 상품의 즐겨찾기 개수 조회
     */
    long countByProductId(Long productId);

    /**
     * 특정 회원의 즐겨찾기 개수 조회
     */
    long countByMemberId(Long memberId);

    /**
     * 특정 회원의 특정 상품 즐겨찾기 조회
     */
    Favorite findByMemberIdAndProductId(Long memberId, Long productId);
}
