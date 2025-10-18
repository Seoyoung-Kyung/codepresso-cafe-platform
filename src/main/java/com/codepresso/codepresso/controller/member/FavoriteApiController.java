package com.codepresso.codepresso.controller.member;

import com.codepresso.codepresso.dto.AuthResponse;
import com.codepresso.codepresso.dto.member.FavoriteListResponse;
import com.codepresso.codepresso.dto.member.FavoriteRequest;
import com.codepresso.codepresso.security.LoginUser;
import com.codepresso.codepresso.service.member.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 즐겨찾기 관련 RESTful API 컨트롤러
 * 즐겨찾기추가, 즐겨찾기목록, 즐겨찾기삭제 API 엔드포인트 제공
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class FavoriteApiController {

    private final FavoriteService favoriteService;

    /**
     * 즐겨찾기 추가 API
     */
    @PostMapping("/favorites")
    public ResponseEntity<AuthResponse> addFavorite(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestBody FavoriteRequest request) {
        Long memberId = loginUser.getMemberId();
        AuthResponse response = favoriteService.addFavorite(memberId, request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 즐겨찾기 목록 조회 API
     */
    @GetMapping("/favorites")
    public ResponseEntity<FavoriteListResponse> getFavoriteList(
            @AuthenticationPrincipal LoginUser loginUser) {
        Long memberId = loginUser.getMemberId();
        FavoriteListResponse response = favoriteService.getFavoriteList(memberId);
        return ResponseEntity.ok(response);
    }

    /**
     * 즐겨찾기 삭제 API
     * 회원의 특정 상품을 즐겨찾기에서 제거
     */
    @DeleteMapping("/favorites/{productId}")
    public ResponseEntity<AuthResponse> removeFavorite(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable Long productId) {
        Long memberId = loginUser.getMemberId();
        AuthResponse response = favoriteService.removeFavorite(memberId, productId);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
