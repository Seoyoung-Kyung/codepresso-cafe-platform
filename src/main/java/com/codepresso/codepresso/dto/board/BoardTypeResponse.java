package com.codepresso.codepresso.dto.board;

import com.codepresso.codepresso.entity.board.BoardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시판 타입 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardTypeResponse {

    /**
     * 게시판 타입 ID
     */
    private Long id;

    /**
     * 게시판 타입명
     */
    private String boardTypeName;

    /**
     * 게시판 타입 설명
     */
    private String boardTypeDescription;

    /**
     * BoardType 엔티티를 BoardTypeResponse로 변환
     */
    public static BoardTypeResponse from(BoardType boardType) {
        return BoardTypeResponse.builder()
                .id(boardType.getId())
                .boardTypeName(boardType.getBoardTypeName())
                .boardTypeDescription(boardType.getBoardTypeDescription())
                .build();
    }
}
