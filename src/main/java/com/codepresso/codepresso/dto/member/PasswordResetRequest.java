package com.codepresso.codepresso.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 비밀번호 재설정 요청 DTO
 * - 인증번호 확인 후 새 비밀번호 설정
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequest {
    private String accountId;
    private String email;
    private String verificationCode;
    private String newPassword;
    private String confirmPassword;
}
