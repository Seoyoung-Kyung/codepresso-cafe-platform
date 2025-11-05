package com.codepresso.codepresso.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 작성 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardCreateRequest {

    /**
     * 게시글 제목
     */
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다.")
    private String title;

    /**
     * 게시글 내용
     */
    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    /**
     * 상태 태그
     */
    @Size(max = 20, message = "상태 태그는 20자를 초과할 수 없습니다.")
    private String statusTag;

    /**
     * 게시판 타입 ID
     */
    @NotNull(message = "게시판 타입은 필수입니다.")
    private Long boardTypeId;

    /**
     * 부모 게시글 ID (댓글 작성 시 사용)
     */
    private Long parentId;
}
