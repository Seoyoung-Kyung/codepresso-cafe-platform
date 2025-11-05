package com.codepresso.codepresso.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 즐겨찾기 추가 요청 DTO
 * 회원이 상품을 즐겨찾기에 추가할 때 사용
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRequest {
    
    /**
     * 즐겨찾기에 추가할 상품 ID
     */
    private Long productId;
    
    /**
     * 즐겨찾기 정렬 순서 (선택사항)
     */
    private Integer orderby;
}
