package com.codepresso.codepresso.controller.auth;

import com.codepresso.codepresso.dto.member.IdFindRequest;
import com.codepresso.codepresso.dto.member.IdFindResponse;
import com.codepresso.codepresso.service.auth.IdFindService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 아이디 찾기 컨트롤러
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class IdFindController {

    private final IdFindService idFindService;

    /**
     * 아이디 찾기 - 인증번호 발송
     */
    @PostMapping("/find-id")
    public ResponseEntity<?> findId(@RequestBody IdFindRequest request) {
        log.info("[find-id] req nickname={}, email={}", request.getNickname(), request.getEmail());

        try {
            IdFindResponse response = idFindService.findId(request);
            
            if (response.isSuccess()) {
                log.info("[find-id] success: email={}", request.getEmail());
                return ResponseEntity.ok(response);
            } else {
                log.warn("[find-id] failed: email={}, message={}", request.getEmail(), response.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            log.error("[find-id] error: email={}, error={}", request.getEmail(), e.getMessage(), e);
            
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("message", "아이디 찾기 중 오류가 발생했습니다. 다시 시도해주세요.");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 인증번호 검증 후 아이디 반환
     */
    @PostMapping("/verify-id-code")
    public ResponseEntity<?> verifyIdCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String verificationCode = request.get("verificationCode");
        
        if (email == null || email.trim().isEmpty() || verificationCode == null || verificationCode.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("message", "이메일과 인증번호를 입력해주세요.");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            IdFindResponse response = idFindService.verifyCodeAndGetId(email, verificationCode);
            
            if (response.isSuccess()) {
                log.info("[verify-id-code] success: email={}, accountId={}", email, response.getAccountId());
                return ResponseEntity.ok(response);
            } else {
                log.warn("[verify-id-code] failed: email={}, message={}", email, response.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            log.error("[verify-id-code] error: email={}, error={}", email, e.getMessage(), e);
            
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("message", "인증번호 검증 중 오류가 발생했습니다. 다시 시도해주세요.");
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
