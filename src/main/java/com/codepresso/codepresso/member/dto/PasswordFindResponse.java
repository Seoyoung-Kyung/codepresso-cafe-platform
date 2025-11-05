package com.codepresso.codepresso.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 비밀번호 찾기 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordFindResponse {
    private boolean success;
    private String message;
    private String verificationCode; // 하드코딩된 인증번호 (개발용)
}
