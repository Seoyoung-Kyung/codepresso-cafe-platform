package com.codepresso.codepresso.member.service;

import com.codepresso.codepresso.member.entity.Member;
import com.codepresso.codepresso.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 아이디 중복 여부 확인
     */
    public boolean isAccountIdDuplicate(String accountId) {
        return memberRepository.existsByAccountId(accountId);
    }

    /**
     * 닉네임 중복 여부 확인
     */
    public boolean isNicknameDuplicate(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    /**
     * 이메일 중복 여부 확인
     */
    public boolean isEmailDuplicate(String email) {
        return memberRepository.existsByEmail(email);
    }

    /**
     * 회원가입 처리
     */
    @Transactional
    public Member register(String accountId, String rawPassword, String nickname, String email,
                           String name, String phone) {
        if (isAccountIdDuplicate(accountId)) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (isNicknameDuplicate(nickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        if (isEmailDuplicate(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        String encoded = passwordEncoder.encode(rawPassword);

        // 전화번호 정규화: 숫자만 남기기 (빈 값이면 null)
        String normalizedPhone = (phone == null) ? null : phone.replaceAll("[^0-9]", "").trim();
        if (normalizedPhone != null && normalizedPhone.isEmpty()) normalizedPhone = null;
        String trimmedName = name == null ? null : name.trim();

        Member member = Member.builder()
                .accountId(accountId)
                .password(encoded)
                .nickname(nickname)
                .email(email)
                .name(trimmedName)
                .phone(normalizedPhone)
                .build();
        try {
            return memberRepository.save(member);
        } catch (DataIntegrityViolationException e) {
            // 동시성으로 인한 유니크 제약 위반 커버 (선행 중복 체크를 통과했지만 저장 시점에 충돌)
            throw new IllegalArgumentException("중복된 정보가 존재합니다.");
        }
    }
}
