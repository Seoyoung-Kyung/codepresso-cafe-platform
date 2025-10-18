package com.codepresso.codepresso.controller.member;

import com.codepresso.codepresso.service.member.FileUploadService;
import com.codepresso.codepresso.service.member.MemberProfileService;
import com.codepresso.codepresso.security.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 프로필 이미지 업로드 컨트롤러
 */
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileImageController {

    private final FileUploadService fileUploadService;
    private final MemberProfileService memberProfileService;

    /**
     * 프로필 이미지 업로드
     * POST /api/profile/image
     */
    @PostMapping("/image")
    public ResponseEntity<?> uploadProfileImage(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam("file") MultipartFile file) {
        
        try {
            Long memberId = loginUser.getMemberId();
            log.info("프로필 이미지 업로드 요청: memberId={}, filename={}", memberId, file.getOriginalFilename());

            // 기존 프로필 이미지 삭제
            String currentProfileImage = memberProfileService.getProfileImage(memberId);
            if (currentProfileImage != null && !currentProfileImage.isEmpty()) {
                fileUploadService.deleteProfileImage(currentProfileImage);
            }

            // 새 이미지 업로드
            String imageUrl = fileUploadService.uploadProfileImage(file, memberId);
            
            // 데이터베이스에 URL 저장
            memberProfileService.updateProfileImage(memberId, imageUrl);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "프로필 이미지가 업로드되었습니다.");
            response.put("imageUrl", imageUrl);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("프로필 이미지 업로드 실패: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("프로필 이미지 업로드 오류", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "프로필 이미지 업로드 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 프로필 이미지 삭제
     * DELETE /api/profile/image
     */
    @DeleteMapping("/image")
    public ResponseEntity<?> deleteProfileImage(@AuthenticationPrincipal LoginUser loginUser) {
        try {
            Long memberId = loginUser.getMemberId();
            log.info("프로필 이미지 삭제 요청: memberId={}", memberId);

            // 현재 프로필 이미지 URL 가져오기
            String currentProfileImage = memberProfileService.getProfileImage(memberId);
            
            if (currentProfileImage != null && !currentProfileImage.isEmpty()) {
                // 파일 삭제
                fileUploadService.deleteProfileImage(currentProfileImage);
                
                // 데이터베이스에서 URL 제거
                memberProfileService.updateProfileImage(memberId, null);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "프로필 이미지가 삭제되었습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("프로필 이미지 삭제 오류", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "프로필 이미지 삭제 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
