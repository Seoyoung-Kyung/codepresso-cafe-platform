package com.codepresso.codepresso.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 회원 상세 정보 응답 DTO
 * 내정보조회 API 응답에 사용
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponse {

    private Long memberId;
    private String accountId;
    private String name;
    private String nickname;
    private LocalDate birthDate;
    private String phone;
    private String email;
    private String profileImage;
    private String role;
}
