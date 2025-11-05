package com.codepresso.codepresso.member.service;

import com.codepresso.codepresso.member.dto.ProfileUpdateRequest;
import com.codepresso.codepresso.member.dto.UserDetailResponse;
import com.codepresso.codepresso.member.entity.Member;
import com.codepresso.codepresso.member.repository.MemberProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * 회원 프로필 관련 비즈니스 로직 서비스
 * 내정보조회, 프로필변경 기능 담당
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberProfileService {

    private final MemberProfileRepository memberProfileRepository;

    /**
     * 회원 상세 정보 조회
     * 회원 ID로 회원 정보를 조회하여 DTO로 변환하여 반환
     * 
     * @param memberId 조회할 회원 ID
     * @return UserDetailResponse 회원 상세 정보 DTO
     * @throws NoSuchElementException 회원이 존재하지 않는 경우
     */
    public UserDetailResponse getMemberInfo(Long memberId) {
        Member member = memberProfileRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다. ID: " + memberId));
        
        return convertToUserDetailResponse(member);
    }

    /**
     * 회원 프로필 정보 수정
     * 회원의 이름, 닉네임, 생년월일, 전화번호, 이메일, 프로필 이미지를 수정
     * 
     * @param memberId 수정할 회원 ID
     * @param request 수정할 프로필 정보
     * @return UserDetailResponse 수정된 회원 상세 정보 DTO
     * @throws NoSuchElementException 회원이 존재하지 않는 경우
     */
    @Transactional
    public UserDetailResponse updateProfile(Long memberId, ProfileUpdateRequest request) {
        Member member = memberProfileRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다. ID: " + memberId));
        
        // 프로필 정보 업데이트
        if (request.getName() != null) {
            member.setName(request.getName());
        }
        if (request.getNickname() != null) {
            member.setNickname(request.getNickname());
        }
        if (request.getBirthDate() != null) {
            member.setBirthDate(request.getBirthDate());
        }
        if (request.getPhone() != null) {
            member.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            member.setEmail(request.getEmail());
        }
        if (request.getProfileImage() != null) {
            member.setProfileImage(request.getProfileImage());
        }
        
        // 변경사항 저장
        Member savedMember = memberProfileRepository.save(member);
        
        return convertToUserDetailResponse(savedMember);
    }

    /**
     * 프로필 이미지 URL 조회
     * 
     * @param memberId 조회할 회원 ID
     * @return 프로필 이미지 URL
     * @throws NoSuchElementException 회원이 존재하지 않는 경우
     */
    public String getProfileImage(Long memberId) {
        Member member = memberProfileRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다. ID: " + memberId));
        return member.getProfileImage();
    }

    /**
     * 프로필 이미지 URL 업데이트
     * 
     * @param memberId 수정할 회원 ID
     * @param imageUrl 새로운 프로필 이미지 URL
     * @throws NoSuchElementException 회원이 존재하지 않는 경우
     */
    @Transactional
    public void updateProfileImage(Long memberId, String imageUrl) {
        Member member = memberProfileRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다. ID: " + memberId));
        member.setProfileImage(imageUrl);
        memberProfileRepository.save(member);
    }

    /**
     * Member 엔티티를 UserDetailResponse DTO로 변환
     * 
     * @param member 변환할 Member 엔티티
     * @return UserDetailResponse 변환된 DTO
     */
    private UserDetailResponse convertToUserDetailResponse(Member member) {
        return UserDetailResponse.builder()
                .memberId(member.getId())
                .accountId(member.getAccountId())
                .name(member.getName())
                .nickname(member.getNickname())
                .birthDate(member.getBirthDate())
                .phone(member.getPhone())
                .email(member.getEmail())
                .profileImage(member.getProfileImage())
                .role(member.getRole().name())
                .build();
    }
}
