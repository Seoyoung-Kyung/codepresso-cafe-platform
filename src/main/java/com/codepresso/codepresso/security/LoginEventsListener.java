package com.codepresso.codepresso.security;

import com.codepresso.codepresso.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 로그인 성공 시 Member.lastLoginAt 컬럼을 갱신하는 리스너.
 * 이벤트 종류
 * - AuthenticationSuccessEvent: 인증 공급자(예: DaoAuthenticationProvider)가 성공했을 때 발생(폼 로그인 등)
 * - InteractiveAuthenticationSuccessEvent: 인터랙티브(사용자 상호작용) 인증 성공 시 발생(필터 체인에서 발행, remember-me 로그인 등 포함)
 */
@Component
@RequiredArgsConstructor
public class LoginEventsListener {

    private final MemberRepository memberRepository;

//    @EventListener
//    @Transactional
//    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
//        updateLastLogin(event.getAuthentication());
//    }

    @EventListener
    @Transactional
    public void onInteractiveAuthenticationSuccess(InteractiveAuthenticationSuccessEvent event) {
        updateLastLogin(event.getAuthentication());
    }

    private void updateLastLogin(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) return;

        if (authentication.getPrincipal() instanceof LoginUser loginUser) {
            Long memberId = loginUser.getMemberId();

            memberRepository.updateLastLoginAt(memberId,LocalDateTime.now().withNano(0));
        }
    }
}
