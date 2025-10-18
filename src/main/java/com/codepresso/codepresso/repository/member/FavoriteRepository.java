package com.codepresso.codepresso.repository.member;

import com.codepresso.codepresso.entity.member.Favorite;
import com.codepresso.codepresso.entity.member.FavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 즐겨찾기 데이터 접근 레포지토리
 * JPA를 활용한 즐겨찾기 데이터 CRUD 작업
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {

    /**
     * 회원의 즐겨찾기 목록 조회 (정렬 순서대로)
     * 
     * @param memberId 조회할 회원 ID
     * @return List<Favorite> 즐겨찾기 목록
     */
    List<Favorite> findByMemberIdOrderByOrderbyAsc(Long memberId);

    /**
     * 특정 회원의 특정 상품 즐겨찾기 조회
     * 
     * @param memberId 회원 ID
     * @param productId 상품 ID
     * @return Favorite 즐겨찾기 정보 (없으면 null)
     */
    Favorite findByMemberIdAndProductId(Long memberId, Long productId);

    /**
     * 회원의 즐겨찾기 개수 조회
     * 
     * @param memberId 조회할 회원 ID
     * @return long 즐겨찾기 개수
     */
    long countByMemberId(Long memberId);

    /**
     * 특정 회원의 특정 상품 즐겨찾기 존재 여부 확인
     * 
     * @param memberId 회원 ID
     * @param productId 상품 ID
     * @return boolean 존재 여부
     */
    boolean existsByMemberIdAndProductId(Long memberId, Long productId);

    /**
     * 회원의 즐겨찾기 목록을 상품 정보와 함께 조회
     * 
     * @param memberId 조회할 회원 ID
     * @return List<Object[]> 즐겨찾기와 상품 정보 조합
     */
    @Query("SELECT f, p FROM Favorite f JOIN f.product p WHERE f.memberId = :memberId ORDER BY f.orderby ASC")
    List<Object[]> findFavoritesWithProductByMemberId(@Param("memberId") Long memberId);

    /**
     * 상품별 즐겨찾기 개수 조회
     * @param productId 조회할 상품
     * @return long 즐겨찾기 개수
     */
    long countByProductId(Long productId);
}
