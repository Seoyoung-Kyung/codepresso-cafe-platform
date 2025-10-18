package com.codepresso.codepresso.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 아이디 찾기 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdFindRequest {
    private String nickname;  // 닉네임
    private String email;     // 이메일
}
