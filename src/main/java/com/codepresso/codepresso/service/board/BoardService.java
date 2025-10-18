package com.codepresso.codepresso.service.board;

import com.codepresso.codepresso.dto.AuthResponse;
import com.codepresso.codepresso.dto.board.*;
import com.codepresso.codepresso.entity.board.Board;
import com.codepresso.codepresso.entity.board.BoardType;
import com.codepresso.codepresso.entity.member.Member;
import com.codepresso.codepresso.repository.board.BoardRepository;
import com.codepresso.codepresso.repository.board.BoardTypeRepository;
import com.codepresso.codepresso.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * 게시판 관련 비즈니스 로직 서비스
 */
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardTypeRepository boardTypeRepository;
    private final MemberRepository memberRepository;

    /**
     * 게시글 목록 조회
     * @param boardTypeId 게시판 타입 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 게시글 목록 응답
     */
    @Transactional(readOnly = true)
    public BoardListResponse getBoardList(Long boardTypeId, int page, int size) {
        try {
            System.out.println("=== BoardService.getBoardList ===");
            System.out.println("boardTypeId: " + boardTypeId);
            
            // 게시판 타입 조회
            BoardType boardType = boardTypeRepository.findById(boardTypeId)
                    .orElseThrow(() -> new NoSuchElementException("게시판을 찾을 수 없습니다. ID: " + boardTypeId));
            
            System.out.println("boardType found: " + boardType);

            // 페이징 설정
            Pageable pageable = PageRequest.of(page, size);
            
            // 게시글 목록 조회 (댓글 제외)
            Page<Board> boardPage = boardRepository.findByBoardTypeAndParentIsNullOrderByFieldDesc(boardType, pageable);

            // DTO 변환
            List<BoardResponse> boardResponses = boardPage.getContent().stream()
                    .map(BoardResponse::fromSimple)
                    .collect(Collectors.toList());

            return BoardListResponse.builder()
                    .boards(boardResponses)
                    .totalCount(boardPage.getTotalElements())
                    .currentPage(boardPage.getNumber())
                    .totalPages(boardPage.getTotalPages())
                    .pageSize(boardPage.getSize())
                    .first(boardPage.isFirst())
                    .last(boardPage.isLast())
                    .build();

        } catch (NoSuchElementException e) {
            return BoardListResponse.builder()
                    .boards(List.of())
                    .totalCount(0L)
                    .currentPage(0)
                    .totalPages(0)
                    .pageSize(size)
                    .first(true)
                    .last(true)
                    .build();
        }
    }

    /**
     * 게시글 상세 조회
     * @param boardId 게시글 ID
     * @return 게시글 상세 응답
     */
    @Transactional(readOnly = true)
    public BoardResponse getBoardDetail(Long boardId) {
        System.out.println("=== BoardService.getBoardDetail ===");
        System.out.println("boardId: " + boardId);
        
        Board board = boardRepository.findByIdWithChildren(boardId)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다. ID: " + boardId));

        System.out.println("게시글 상태: " + board.getStatusTag());
        System.out.println("댓글 수: " + board.getChildren().size());
        
        // 댓글들을 명시적으로 로드
        List<Board> comments = boardRepository.findByParentIdOrderByFieldAsc(boardId);
        System.out.println("명시적으로 로드된 댓글 수: " + comments.size());
        
        board.getChildren().clear(); // Clear existing (potentially lazy-loaded) children
        board.getChildren().addAll(comments); // Add explicitly loaded children
        
        BoardResponse response = BoardResponse.from(board);
        System.out.println("응답 생성 완료. children 수: " + (response.getChildren() != null ? response.getChildren().size() : 0));
        
        return response;
    }

    /**
     * 게시글 작성
     * @param memberId 작성자 ID
     * @param request 게시글 작성 요청
     * @return 성공/실패 응답
     */
    @Transactional
    public AuthResponse createBoard(Long memberId, BoardCreateRequest request) {
        try {
            // 회원 조회
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다. ID: " + memberId));

            // 게시판 타입 조회
            BoardType boardType = boardTypeRepository.findById(request.getBoardTypeId())
                    .orElseThrow(() -> new NoSuchElementException("게시판을 찾을 수 없습니다. ID: " + request.getBoardTypeId()));

            // 부모 게시글 조회 (댓글인 경우)
            Board parent = null;
            if (request.getParentId() != null) {
                parent = boardRepository.findById(request.getParentId())
                        .orElseThrow(() -> new NoSuchElementException("부모 게시글을 찾을 수 없습니다. ID: " + request.getParentId()));
            }

            // 게시글 생성
            Board board = Board.builder()
                    .member(member)
                    .title(request.getTitle())
                    .content(request.getContent())
                    .statusTag(request.getStatusTag() != null ? request.getStatusTag() : "PENDING")
                    .field(LocalDateTime.now())
                    .boardType(boardType)
                    .parent(parent)
                    .build();

            boardRepository.save(board);

            return AuthResponse.builder()
                    .success(true)
                    .message("게시글이 작성되었습니다.")
                    .build();

        } catch (NoSuchElementException e) {
            return AuthResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return AuthResponse.builder()
                    .success(false)
                    .message("게시글 작성 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 게시글 수정
     * @param memberId 수정자 ID
     * @param boardId 게시글 ID
     * @param request 게시글 수정 요청
     * @return 성공/실패 응답
     */
    @Transactional
    public AuthResponse updateBoard(Long memberId, Long boardId, BoardUpdateRequest request) {
        try {
            // 게시글 조회
            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다. ID: " + boardId));

            // 댓글이 아닌 게시글인지 확인
            if (board.getParent() != null) {
                return AuthResponse.builder()
                        .success(false)
                        .message("댓글은 이 API로 수정할 수 없습니다. 댓글 수정 API를 사용하세요.")
                        .build();
            }

            // 권한 확인 (작성자만 수정 가능)
            if (!board.getMember().getId().equals(memberId)) {
                return AuthResponse.builder()
                        .success(false)
                        .message("게시글을 수정할 권한이 없습니다.")
                        .build();
            }

            // 게시글 수정
            board.setTitle(request.getTitle());
            board.setContent(request.getContent());
            board.setStatusTag(request.getStatusTag());

            boardRepository.save(board);

            return AuthResponse.builder()
                    .success(true)
                    .message("게시글이 수정되었습니다.")
                    .build();

        } catch (NoSuchElementException e) {
            return AuthResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return AuthResponse.builder()
                    .success(false)
                    .message("게시글 수정 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 댓글 수정
     * @param memberId 수정자 ID
     * @param parentBoardId 부모 게시글 ID
     * @param boardId 댓글 ID (board 테이블의 board_id)
     * @param request 댓글 수정 요청
     * @return 성공/실패 응답
     */
    @Transactional
    public AuthResponse updateComment(Long memberId, Long parentBoardId, Long boardId, BoardUpdateRequest request) {
        try {
            // 댓글 조회
            Board comment = boardRepository.findById(boardId)
                    .orElseThrow(() -> new NoSuchElementException("댓글을 찾을 수 없습니다. ID: " + boardId));

            // 댓글이 해당 게시글의 댓글인지 확인
            if (comment.getParent() == null || !comment.getParent().getId().equals(parentBoardId)) {
                return AuthResponse.builder()
                        .success(false)
                        .message("해당 게시글의 댓글이 아닙니다.")
                        .build();
            }

            // 권한 확인 (작성자만 수정 가능)
            if (!comment.getMember().getId().equals(memberId)) {
                return AuthResponse.builder()
                        .success(false)
                        .message("댓글을 수정할 권한이 없습니다.")
                        .build();
            }

            // 댓글 수정
            comment.setTitle(request.getTitle());
            comment.setContent(request.getContent());
            comment.setStatusTag(request.getStatusTag());

            boardRepository.save(comment);

            return AuthResponse.builder()
                    .success(true)
                    .message("댓글이 수정되었습니다.")
                    .build();

        } catch (NoSuchElementException e) {
            return AuthResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return AuthResponse.builder()
                    .success(false)
                    .message("댓글 수정 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 게시글 삭제
     * @param memberId 삭제자 ID
     * @param boardId 게시글 ID
     * @return 성공/실패 응답
     */
    @Transactional
    public AuthResponse deleteBoard(Long memberId, Long boardId) {
        try {
            // 게시글 조회
            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다. ID: " + boardId));

            // 댓글이 아닌 게시글인지 확인
            if (board.getParent() != null) {
                return AuthResponse.builder()
                        .success(false)
                        .message("댓글은 이 API로 삭제할 수 없습니다. 댓글 삭제 API를 사용하세요.")
                        .build();
            }

            // 권한 확인 (작성자만 삭제 가능)
            if (!board.getMember().getId().equals(memberId)) {
                return AuthResponse.builder()
                        .success(false)
                        .message("게시글을 삭제할 권한이 없습니다.")
                        .build();
            }

            // 게시글 삭제 (댓글도 함께 삭제됨 - cascade 설정)
            boardRepository.delete(board);

            return AuthResponse.builder()
                    .success(true)
                    .message("게시글이 삭제되었습니다.")
                    .build();

        } catch (NoSuchElementException e) {
            return AuthResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return AuthResponse.builder()
                    .success(false)
                    .message("게시글 삭제 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 댓글 삭제
     * @param memberId 삭제자 ID
     * @param parentBoardId 부모 게시글 ID
     * @param boardId 댓글 ID (board 테이블의 board_id)
     * @return 성공/실패 응답
     */
    @Transactional
    public AuthResponse deleteComment(Long memberId, Long parentBoardId, Long boardId) {
        try {
            // 댓글 조회
            Board comment = boardRepository.findById(boardId)
                    .orElseThrow(() -> new NoSuchElementException("댓글을 찾을 수 없습니다. ID: " + boardId));

            // 댓글이 해당 게시글의 댓글인지 확인
            if (comment.getParent() == null || !comment.getParent().getId().equals(parentBoardId)) {
                return AuthResponse.builder()
                        .success(false)
                        .message("해당 게시글의 댓글이 아닙니다.")
                        .build();
            }

            // 권한 확인 (작성자만 삭제 가능)
            if (!comment.getMember().getId().equals(memberId)) {
                return AuthResponse.builder()
                        .success(false)
                        .message("댓글을 삭제할 권한이 없습니다.")
                        .build();
            }

            // 부모 게시글 조회
            Board parentBoard = boardRepository.findById(parentBoardId)
                    .orElseThrow(() -> new NoSuchElementException("부모 게시글을 찾을 수 없습니다. ID: " + parentBoardId));

            // 댓글 삭제
            boardRepository.delete(comment);

            // 부모 게시글의 댓글 수 확인 후 답변 상태 업데이트
            long remainingComments = boardRepository.countByParentId(parentBoardId);
            if (remainingComments == 0) {
                // 댓글이 없으면 답변대기로 변경
                parentBoard.setStatusTag("PENDING");
                boardRepository.save(parentBoard);
            }

            return AuthResponse.builder()
                    .success(true)
                    .message("댓글이 삭제되었습니다.")
                    .build();

        } catch (NoSuchElementException e) {
            return AuthResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return AuthResponse.builder()
                    .success(false)
                    .message("댓글 삭제 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 댓글 작성
     * @param memberId 작성자 ID
     * @param parentBoardId 부모 게시글 ID
     * @param request 댓글 작성 요청
     * @return 성공/실패 응답
     */
    @Transactional
    public AuthResponse createComment(Long memberId, Long parentBoardId, BoardCreateRequest request) {
        try {
            System.out.println("=== BoardService.createComment ===");
            System.out.println("memberId: " + memberId);
            System.out.println("parentBoardId: " + parentBoardId);
            System.out.println("request: " + request);
            
            // 회원 조회
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다. ID: " + memberId));

            // 부모 게시글 조회
            Board parentBoard = boardRepository.findById(parentBoardId)
                    .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다. ID: " + parentBoardId));

            // 부모 게시글이 댓글이 아닌지 확인
            if (parentBoard.getParent() != null) {
                return AuthResponse.builder()
                        .success(false)
                        .message("댓글에는 댓글을 달 수 없습니다.")
                        .build();
            }

            // 게시판 타입 조회 (부모 게시글과 동일한 타입 사용)
            BoardType boardType = parentBoard.getBoardType();

            // 댓글 생성
            Board comment = Board.builder()
                    .member(member)
                    .title(request.getTitle())
                    .content(request.getContent())
                    .statusTag(request.getStatusTag())
                    .field(LocalDateTime.now())
                    .boardType(boardType)
                    .parent(parentBoard)
                    .build();

            boardRepository.save(comment);

            // 부모 게시글의 답변 상태를 'ANSWERED'로 업데이트
            System.out.println("댓글 작성 전 부모 게시글 상태: " + parentBoard.getStatusTag());
            parentBoard.setStatusTag("ANSWERED");
            boardRepository.save(parentBoard);
            System.out.println("댓글 작성 후 부모 게시글 상태: " + parentBoard.getStatusTag());

            return AuthResponse.builder()
                    .success(true)
                    .message("댓글이 작성되었습니다.")
                    .build();

        } catch (NoSuchElementException e) {
            return AuthResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return AuthResponse.builder()
                    .success(false)
                    .message("댓글 작성 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 게시판 타입 목록 조회
     * @return 게시판 타입 목록
     */
    @Transactional(readOnly = true)
    public List<BoardTypeResponse> getBoardTypes() {
        List<BoardType> boardTypes = boardTypeRepository.findAllByOrderByIdAsc();
        return boardTypes.stream()
                .map(BoardTypeResponse::from)
                .collect(Collectors.toList());
    }
}
