package com.codepresso.codepresso.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 즐겨찾기 목록 전체 응답 DTO
 * 
 * 회원의 모든 즐겨찾기 항목들을 묶어서 관리하는 컨테이너 객체입니다.
 * 즐겨찾기 목록 조회 API의 최종 응답 형태로 사용되며,
 * 개별 즐겨찾기 항목들과 전체 통계 정보를 포함합니다.
 * 
 * 포함 정보:
 * - 즐겨찾기 항목들의 배열 (List<FavoriteResponse>)
 * - 전체 즐겨찾기 개수 (totalCount)
 * - 향후 페이징, 정렬, 필터링 정보 확장 가능
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteListResponse {
    
    /**
     * 즐겨찾기 목록
     */
    private List<FavoriteResponse> favorites;
    
    /**
     * 총 개수
     */
    private long totalCount;
}
