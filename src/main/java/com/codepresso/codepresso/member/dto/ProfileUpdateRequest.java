package com.codepresso.codepresso.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 프로필 수정 요청 DTO
 * 회원의 프로필 정보 수정 시 사용
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {
    
    /**
     * 회원 이름
     */
    private String name;
    
    /**
     * 회원 닉네임
     */
    private String nickname;
    
    /**
     * 회원 생년월일
     */
    private LocalDate birthDate;
    
    /**
     * 회원 전화번호
     */
    private String phone;
    
    /**
     * 회원 이메일
     */
    private String email;
    
    /**
     * 회원 프로필 이미지 경로
     */
    private String profileImage;
}
