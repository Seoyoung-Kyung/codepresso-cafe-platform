package com.codepresso.codepresso.dto.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 수정 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardUpdateRequest {

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
}
