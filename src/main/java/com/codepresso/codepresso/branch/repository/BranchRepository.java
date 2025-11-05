package com.codepresso.codepresso.branch.repository;

import com.codepresso.codepresso.branch.entity.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    // 매장명 검색 (대소문자 무시)
    Page<Branch> findByBranchNameContainingIgnoreCase(String branchName, Pageable pageable);

    // 내 주변 매장: 하버사인 거리 계산으로 반경 내 정렬/필터링
    @Query(value = """
            SELECT *
            FROM branch b
            WHERE b.latitude IS NOT NULL AND b.longitude IS NOT NULL
              AND (6371 * acos(
                    cos(radians(:lat)) * cos(radians(b.latitude)) *
                    cos(radians(b.longitude) - radians(:lng)) +
                    sin(radians(:lat)) * sin(radians(b.latitude))
                  )) <= :radiusKm
            ORDER BY (6371 * acos(
                    cos(radians(:lat)) * cos(radians(b.latitude)) *
                    cos(radians(b.longitude) - radians(:lng)) +
                    sin(radians(:lat)) * sin(radians(b.latitude))
                  )) ASC
            """,
            countQuery = """
            SELECT COUNT(*)
            FROM branch b
            WHERE b.latitude IS NOT NULL AND b.longitude IS NOT NULL
              AND (6371 * acos(
                    cos(radians(:lat)) * cos(radians(b.latitude)) *
                    cos(radians(b.longitude) - radians(:lng)) +
                    sin(radians(:lat)) * sin(radians(b.latitude))
                  )) <= :radiusKm
            """,
            nativeQuery = true)
    Page<Branch> findNearby(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusKm") double radiusKm,
            Pageable pageable);

    Optional<Object> findByBranchName(String branchName);
}
