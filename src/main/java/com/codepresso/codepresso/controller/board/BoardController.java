package com.codepresso.codepresso.controller.board;

import com.codepresso.codepresso.dto.AuthResponse;
import com.codepresso.codepresso.dto.board.*;
import com.codepresso.codepresso.security.LoginUser;
import com.codepresso.codepresso.service.board.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 게시판 관련 RESTful API 컨트롤러
 * API 명세서에 따른 엔드포인트 제공
 */
@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시글 목록 조회 API
     * GET /boards
     */
    @GetMapping
    public ResponseEntity<BoardListResponse> getBoardList(
            @RequestParam("boardTypeId") Long boardTypeId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        try {
            System.out.println("=== BoardController.getBoardList ===");
            System.out.println("boardTypeId: " + boardTypeId);
            System.out.println("page: " + page);
            System.out.println("size: " + size);
            
            BoardListResponse response = boardService.getBoardList(boardTypeId, page, size);
            System.out.println("response: " + response);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("=== BoardController.getBoardList ERROR ===");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 게시글 상세 조회 API
     * GET /boards/{boardId}
     */
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponse> getBoardDetail(@PathVariable Long boardId) {
        try {
            BoardResponse response = boardService.getBoardDetail(boardId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 게시글 작성 API
     * POST /boards
     */
    @PostMapping
    public ResponseEntity<AuthResponse> createBoard(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody BoardCreateRequest request) {
        
        System.out.println("=== BoardController.createBoard ===");
        System.out.println("request: " + request);
        
        Long memberId = loginUser.getMemberId();
        System.out.println("memberId: " + memberId);
        
        AuthResponse response = boardService.createBoard(memberId, request);
        
        System.out.println("createBoard response: " + response);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 게시글 수정 API
     * PUT /boards/{boardId}
     */
    @PutMapping("/{boardId}")
    public ResponseEntity<AuthResponse> updateBoard(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable Long boardId,
            @Valid @RequestBody BoardUpdateRequest request) {
        
        Long memberId = loginUser.getMemberId();
        AuthResponse response = boardService.updateBoard(memberId, boardId, request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 댓글 수정 API
     * PUT /boards/{parentBoardId}/comments/{boardId}
     */
    @PutMapping("/{parentBoardId}/comments/{boardId}")
    public ResponseEntity<AuthResponse> updateComment(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable Long parentBoardId,
            @PathVariable Long boardId,
            @Valid @RequestBody BoardUpdateRequest request) {
        
        Long memberId = loginUser.getMemberId();
        AuthResponse response = boardService.updateComment(memberId, parentBoardId, boardId, request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 게시글 삭제 API
     * DELETE /boards/{boardId}
     */
    @DeleteMapping("/{boardId}")
    public ResponseEntity<AuthResponse> deleteBoard(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable Long boardId) {
        
        Long memberId = loginUser.getMemberId();
        AuthResponse response = boardService.deleteBoard(memberId, boardId);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 댓글 삭제 API
     * DELETE /boards/{parentBoardId}/comments/{boardId}
     */
    @DeleteMapping("/{parentBoardId}/comments/{boardId}")
    public ResponseEntity<AuthResponse> deleteComment(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable Long parentBoardId,
            @PathVariable Long boardId) {
        
        Long memberId = loginUser.getMemberId();
        AuthResponse response = boardService.deleteComment(memberId, parentBoardId, boardId);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 게시판 타입 목록 조회 API
     * GET /boards/types
     */
    @GetMapping("/types")
    public ResponseEntity<List<BoardTypeResponse>> getBoardTypes() {
        List<BoardTypeResponse> response = boardService.getBoardTypes();
        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 작성 API
     * POST /boards/{parentBoardId}/comments
     */
    @PostMapping("/{parentBoardId}/comments")
    public ResponseEntity<AuthResponse> createComment(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable Long parentBoardId,
            @Valid @RequestBody BoardCreateRequest request) {
        
        System.out.println("=== BoardController.createComment ===");
        System.out.println("parentBoardId: " + parentBoardId);
        System.out.println("request: " + request);
        
        Long memberId = loginUser.getMemberId();
        System.out.println("memberId: " + memberId);
        
        AuthResponse response = boardService.createComment(memberId, parentBoardId, request);
        
        System.out.println("createComment response: " + response);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 댓글 목록 조회 API
     * GET /boards/{parentBoardId}/comments
     */
    @GetMapping("/{parentBoardId}/comments")
    public ResponseEntity<List<BoardResponse>> getComments(@PathVariable Long parentBoardId) {
        try {
            BoardResponse boardResponse = boardService.getBoardDetail(parentBoardId);
            return ResponseEntity.ok(boardResponse.getChildren());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
