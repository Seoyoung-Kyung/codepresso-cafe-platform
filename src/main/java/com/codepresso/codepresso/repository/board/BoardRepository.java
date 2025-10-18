package com.codepresso.codepresso.repository.board;

import com.codepresso.codepresso.entity.board.Board;
import com.codepresso.codepresso.entity.board.BoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 게시글 관련 데이터 접근 레포지토리
 */
@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    /**
     * 특정 게시판 타입의 모든 게시글 조회 (페이징)
     * @param boardType 게시판 타입
     * @param pageable 페이징 정보
     * @return 게시글 페이지
     */
    Page<Board> findByBoardTypeAndParentIsNullOrderByFieldDesc(BoardType boardType, Pageable pageable);

    /**
     * 특정 게시판 타입의 모든 게시글 조회 (페이징 없이)
     * @param boardType 게시판 타입
     * @return 게시글 목록
     */
    List<Board> findByBoardTypeAndParentIsNullOrderByFieldDesc(BoardType boardType);

    /**
     * 특정 게시글의 댓글들 조회
     * @param parentId 부모 게시글 ID
     * @return 댓글 목록
     */
    List<Board> findByParentIdOrderByFieldAsc(Long parentId);

    /**
     * 특정 게시글과 댓글들을 함께 조회
     * @param boardId 게시글 ID
     * @return 게시글과 댓글들
     */
    @Query("SELECT b FROM Board b LEFT JOIN FETCH b.children WHERE b.id = :boardId")
    Optional<Board> findByIdWithChildren(@Param("boardId") Long boardId);

    /**
     * 특정 게시판 타입의 게시글 수 조회
     * @param boardType 게시판 타입
     * @return 게시글 수
     */
    long countByBoardTypeAndParentIsNull(BoardType boardType);

    /**
     * 특정 게시글의 댓글 수 조회
     * @param parentId 부모 게시글 ID
     * @return 댓글 수
     */
    long countByParentId(Long parentId);

    /**
     * 특정 회원이 작성한 게시글 조회
     * @param memberId 회원 ID
     * @param pageable 페이징 정보
     * @return 게시글 페이지
     */
    Page<Board> findByMemberIdAndParentIsNullOrderByFieldDesc(Long memberId, Pageable pageable);
}
