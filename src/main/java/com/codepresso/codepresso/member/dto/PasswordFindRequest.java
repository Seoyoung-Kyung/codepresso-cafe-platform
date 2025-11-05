package com.codepresso.codepresso.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 비밀번호 찾기 요청 DTO
 * - 아이디와 이메일을 통한 사용자 확인
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordFindRequest {
    private String accountId;
    private String email;
}
