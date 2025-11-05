package com.codepresso.codepresso.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 아이디 찾기 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdFindResponse {
    private boolean success;      // 성공 여부
    private String message;       // 응답 메시지
    private String accountId;     // 찾은 아이디 (성공 시에만)
    private String verificationCode; // 개발용 인증번호 (실제 운영에서는 제거)
}
