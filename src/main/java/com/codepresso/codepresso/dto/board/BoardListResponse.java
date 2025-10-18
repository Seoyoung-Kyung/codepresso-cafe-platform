package com.codepresso.codepresso.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 게시글 목록 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardListResponse {

    /**
     * 게시글 목록
     */
    private List<BoardResponse> boards;

    /**
     * 전체 게시글 수
     */
    private Long totalCount;

    /**
     * 현재 페이지
     */
    private int currentPage;

    /**
     * 전체 페이지 수
     */
    private int totalPages;

    /**
     * 페이지 크기
     */
    private int pageSize;

    /**
     * 첫 번째 페이지 여부
     */
    private boolean first;

    /**
     * 마지막 페이지 여부
     */
    private boolean last;
}
