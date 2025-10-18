package com.codepresso.codepresso.dto.board;

import com.codepresso.codepresso.entity.board.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시글 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponse {

    /**
     * 게시글 ID
     */
    private Long id;

    /**
     * 작성자 ID
     */
    private Long memberId;

    /**
     * 작성자 닉네임
     */
    private String memberNickname;

    /**
     * 게시글 제목
     */
    private String title;

    /**
     * 게시글 내용
     */
    private String content;

    /**
     * 상태 태그
     */
    private String statusTag;

    /**
     * 작성일시
     */
    private LocalDateTime field;

    /**
     * 게시판 타입 ID
     */
    private Long boardTypeId;

    /**
     * 게시판 타입명
     */
    private String boardTypeName;

    /**
     * 부모 게시글 ID (댓글인 경우)
     */
    private Long parentId;

    /**
     * 댓글 목록
     */
    private List<BoardResponse> children;

    /**
     * 댓글 수
     */
    private Long commentCount;

    /**
     * Board 엔티티를 BoardResponse로 변환
     */
    public static BoardResponse from(Board board) {
        return BoardResponse.builder()
                .id(board.getId())
                .memberId(board.getMember().getId())
                .memberNickname(board.getMember().getNickname())
                .title(board.getTitle())
                .content(board.getContent())
                .statusTag(board.getStatusTag())
                .field(board.getField())
                .boardTypeId(board.getBoardType().getId())
                .boardTypeName(board.getBoardType().getBoardTypeName())
                .parentId(board.getParent() != null ? board.getParent().getId() : null)
                .children(board.getChildren().stream()
                        .map(BoardResponse::from)
                        .collect(Collectors.toList()))
                .commentCount((long) board.getChildren().size())
                .build();
    }

    /**
     * 댓글을 위한 간단한 변환 (무한 댓글 방지)
     */
    public static BoardResponse fromSimple(Board board) {
        return BoardResponse.builder()
                .id(board.getId())
                .memberId(board.getMember().getId())
                .memberNickname(board.getMember().getNickname())
                .title(board.getTitle())
                .content(board.getContent())
                .statusTag(board.getStatusTag())
                .field(board.getField())
                .boardTypeId(board.getBoardType().getId())
                .boardTypeName(board.getBoardType().getBoardTypeName())
                .parentId(board.getParent() != null ? board.getParent().getId() : null)
                .commentCount((long) board.getChildren().size())
                .build();
    }
}
