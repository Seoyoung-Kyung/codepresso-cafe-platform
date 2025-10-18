package com.codepresso.codepresso.repository.board;

import com.codepresso.codepresso.entity.board.BoardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 게시판 타입 관련 데이터 접근 레포지토리
 */
@Repository
public interface BoardTypeRepository extends JpaRepository<BoardType, Long> {

    /**
     * 모든 게시판 타입 조회
     * @return 게시판 타입 목록
     */
    List<BoardType> findAllByOrderByIdAsc();

    /**
     * 게시판 타입명으로 조회
     * @param boardTypeName 게시판 타입명
     * @return 게시판 타입
     */
    Optional<BoardType> findByBoardTypeName(String boardTypeName);

    /**
     * 게시판 타입명 존재 여부 확인
     * @param boardTypeName 게시판 타입명
     * @return 존재 여부
     */
    boolean existsByBoardTypeName(String boardTypeName);
}
