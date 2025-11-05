package com.codepresso.codepresso.auth.controller;

import com.codepresso.codepresso.auth.dto.SignUpRequest;
import com.codepresso.codepresso.member.entity.Member;
import com.codepresso.codepresso.auth.service.EmailService;
import com.codepresso.codepresso.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 인증
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final MemberService memberService;
    private final EmailService emailService;
    
    // 이메일 인증번호 저장용 (실제 운영에서는 Redis 등 사용 권장)
    private final Map<String, String> emailVerificationCodes = new HashMap<>();

    /** 중복체크 */
    @GetMapping("/check")
    public Map<String, Object> check(
            @RequestParam("value") String value,
            @RequestParam(value = "field", required = false) CheckField field
    ) {
        CheckField target = field != null ? field : CheckField.ID;
        boolean duplicate = isDuplicate(target, value);

        Map<String, Object> resp = new HashMap<>();

        resp.put("field", target.asKey());
        resp.put("duplicate", duplicate);

        return resp;
    }

    /** 회원가입 */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignUpRequest request) {
        log.info("[signup] req accountId={}, name={}, phone={}", request.getAccountId(), request.getName(), request.getPhone());

        Member member = memberService.register(
                request.getAccountId(),
                request.getPassword(),
                request.getNickname(),
                request.getEmail(),
                request.getName(),
                request.getPhone()
        );

        log.info("[signup] saved id={}, name={}, phone={}", member.getId(), member.getName(), member.getPhone());

        Map<String, Object> resp = new HashMap<>();
        resp.put("id", member.getId());
        resp.put("accountId", member.getAccountId());
        resp.put("name", member.getName());
        resp.put("phone", member.getPhone());
        resp.put("nickname", member.getNickname());
        resp.put("email", member.getEmail());
        return ResponseEntity.ok(resp);
    }

    /** 이메일 인증번호 발송 */
    @PostMapping("/send-email-verification")
    public ResponseEntity<?> sendEmailVerification(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        
        if (email == null || email.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "이메일을 입력해주세요.");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            // 6자리 인증번호 생성
            String verificationCode = generateVerificationCode();
            
            // 인증번호 저장 (5분 유효)
            emailVerificationCodes.put(email, verificationCode);
            
            // 이메일 발송
            emailService.sendEmailVerification(email, verificationCode);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "인증번호가 발송되었습니다.");
            response.put("verificationCode", verificationCode); // 개발용 - 실제 운영에서는 제거
            
            log.info("[email-verification] sent to={}, code={}", email, verificationCode);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("[email-verification] failed to={}, error={}", email, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "이메일 발송에 실패했습니다. 다시 시도해주세요.");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /** 이메일 인증번호 검증 */
    @PostMapping("/verify-email-code")
    public ResponseEntity<?> verifyEmailCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        
        if (email == null || email.trim().isEmpty() || code == null || code.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "이메일과 인증번호를 입력해주세요.");
            return ResponseEntity.badRequest().body(error);
        }

        String storedCode = emailVerificationCodes.remove(email); // 한 번 사용하면 삭제
        boolean isValid = code.equals(storedCode);
        
        Map<String, Object> response = new HashMap<>();
        if (isValid) {
            response.put("valid", true);
            response.put("message", "이메일 인증이 완료되었습니다.");
            log.info("[email-verification] verified successfully: email={}", email);
        } else {
            response.put("valid", false);
            response.put("message", "인증번호가 일치하지 않거나 만료되었습니다.");
            log.warn("[email-verification] verification failed: email={}, code={}", email, code);
        }
        
        return ResponseEntity.ok(response);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(code);
    }

    private boolean isDuplicate(CheckField target, String value) {
        return switch (target) {
            case NICKNAME -> memberService.isNicknameDuplicate(value);
            case EMAIL -> memberService.isEmailDuplicate(value);
            case ID -> memberService.isAccountIdDuplicate(value);
        };
    }
}
