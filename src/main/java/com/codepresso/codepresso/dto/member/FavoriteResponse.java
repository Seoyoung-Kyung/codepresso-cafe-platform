package com.codepresso.codepresso.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 개별 즐겨찾기 항목 응답 DTO
 * 
 * 특정 상품의 즐겨찾기 정보를 담는 단일 객체입니다.
 * 즐겨찾기 목록에서 각 항목을 나타내거나, 
 * 개별 즐겨찾기 정보를 반환할 때 사용됩니다.
 * 
 * 포함 정보:
 * - 회원 ID, 상품 ID
 * - 상품 상세 정보 (이름, 설명, 사진, 가격)
 * - 즐겨찾기 정렬 순서 및 추가 시간
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponse {
    
    /**
     * 즐겨찾기 ID (회원 ID + 상품 ID)
     */
    private Long memberId;
    private Long productId;
    
    /**
     * 상품 정보
     */
    private String productName;
    private String productContent;
    private String productPhoto;
    private Integer price;
    
    /**
     * 즐겨찾기 정렬 순서
     */
    private Integer orderby;
    
    /**
     * 즐겨찾기 추가 시간
     */
    private LocalDateTime createdAt;
}
