package com.codepresso.codepresso.service.auth;

import com.codepresso.codepresso.dto.member.PasswordFindRequest;
import com.codepresso.codepresso.dto.member.PasswordFindResponse;
import com.codepresso.codepresso.dto.member.PasswordResetRequest;
import com.codepresso.codepresso.dto.member.PasswordResetResponse;
import com.codepresso.codepresso.entity.member.Member;
import com.codepresso.codepresso.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * 비밀번호 찾기 서비스
 * - 아이디/이메일로 사용자 확인
 * - 메모리 맵 기반 인증번호 관리
 * - 이메일 발송 및 비밀번호 재설정
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordFindService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    
    // 비밀번호 찾기용 인증번호 저장 (이메일 -> 인증번호)
    private final Map<String, String> passwordResetCodes = new ConcurrentHashMap<>();
    private final Random random = new Random();

    /**
     * 비밀번호 찾기 - 사용자 확인 및 인증번호 발급
     */
    @Transactional
    public PasswordFindResponse findPassword(PasswordFindRequest request) {
        // 아이디와 이메일로 사용자 확인
        Member member = memberRepository.findByAccountIdAndEmail(request.getAccountId(), request.getEmail())
                .orElseThrow(() -> new NoSuchElementException("아이디 또는 이메일이 일치하지 않습니다."));

        try {
            // 6자리 인증번호 생성
            String verificationCode = generatePasswordResetCode();
            
            // 인증번호 저장
            passwordResetCodes.put(request.getEmail(), verificationCode);
            
            // 이메일 발송
            emailService.sendPasswordResetEmail(request.getEmail(), request.getAccountId(), verificationCode);
            
            log.info("비밀번호 찾기 인증번호 이메일 발송 완료: {} ({})", request.getEmail(), request.getAccountId());

            return PasswordFindResponse.builder()
                    .success(true)
                    .message("인증번호가 이메일로 발송되었습니다. 이메일을 확인해주세요.")
                    .verificationCode(verificationCode) // 개발용
                    .build();
                    
        } catch (Exception e) {
            log.error("이메일 발송 실패: {} ({}) - {}", request.getEmail(), request.getAccountId(), e.getMessage());
            
            return PasswordFindResponse.builder()
                    .success(false)
                    .message("이메일 발송에 실패했습니다. 이메일 주소를 확인해주세요.")
                    .build();
        }
    }

    /**
     * 비밀번호 재설정
     */
    @Transactional
    public PasswordResetResponse resetPassword(PasswordResetRequest request) {
        // 사용자 확인
        Member member = memberRepository.findByAccountIdAndEmail(request.getAccountId(), request.getEmail())
                .orElseThrow(() -> new NoSuchElementException("아이디 또는 이메일이 일치하지 않습니다."));

        // 메모리 맵에서 인증번호 검증
        String storedCode = passwordResetCodes.remove(request.getEmail()); // 한 번 사용하면 삭제
        if (!request.getVerificationCode().equals(storedCode)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않거나 만료되었습니다.");
        }

        // 새 비밀번호와 확인 비밀번호 일치 확인
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        // 비밀번호 유효성 검사
        if (request.getNewPassword().length() < 8) {
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
        }

        // 비밀번호 암호화 및 저장
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        member.setPassword(encodedPassword);
        memberRepository.save(member);

        log.info("비밀번호 재설정 완료: {} ({})", request.getEmail(), request.getAccountId());

        return PasswordResetResponse.builder()
                .success(true)
                .message("비밀번호가 성공적으로 변경되었습니다.")
                .build();
    }
    
    /**
     * 비밀번호 재설정용 인증번호 생성
     */
    private String generatePasswordResetCode() {
        int code = 100000 + random.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(code);
    }
}