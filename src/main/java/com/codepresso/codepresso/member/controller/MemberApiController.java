package com.codepresso.codepresso.member.controller;

import com.codepresso.codepresso.member.dto.ProfileUpdateRequest;
import com.codepresso.codepresso.member.dto.UserDetailResponse;
import com.codepresso.codepresso.common.security.LoginUser;
import com.codepresso.codepresso.member.service.MemberProfileService;
import com.codepresso.codepresso.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

/**
 * 회원 관련 RESTful API 컨트롤러
 * - JSON API
 * - 로그인 사용자 정보는 @AuthenticationPrincipal 사용
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberApiController {

    private final MemberProfileService memberProfileService;
    private final ReviewService reviewService;

    /**
     * 내정보조회 RESTful API
     */
    @GetMapping("/users/me")
    public ResponseEntity<UserDetailResponse> getMyInfo(@AuthenticationPrincipal LoginUser loginUser) {
        Long memberId = loginUser.getMemberId();
        UserDetailResponse userDetailResponse = memberProfileService.getMemberInfo(memberId);
        return ResponseEntity.ok(userDetailResponse);
    }

    /**
     * 프로필변경 RESTful API
     */
    @PutMapping("/users/me")
    public ResponseEntity<UserDetailResponse> updateMyProfile(@AuthenticationPrincipal LoginUser loginUser,
                                                              @RequestBody ProfileUpdateRequest request) {
        try {
            Long memberId = loginUser.getMemberId();
            UserDetailResponse updatedMember = memberProfileService.updateProfile(memberId, request);
            return ResponseEntity.ok(updatedMember);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
