package com.codepresso.codepresso.config.init;

import com.codepresso.codepresso.entity.member.Member;
import com.codepresso.codepresso.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * MemberInitializer
 * - 부팅 시 가장 먼저 실행되는 초기화 러너입니다.
 * - 다른 도메인(상품/지점/주문 등)들이 멤버를 참조하므로 멤버가 최우선입니다.
 *
 * 사용 가이드(샘플)
 * 1) 필요한 저장소 주입(생성자 주입)
 * 2) 트랜잭션 적용(필요 시 @Transactional)
 * 3) 멱등성 보장(존재 여부 확인 후 생성)
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class MemberInitializer implements ApplicationRunner {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("[Init] MemberInitializer start");

        if (memberRepository.findByAccountId("admin").isEmpty()) {
            memberRepository.save(Member.builder()
                    .accountId("admin")
                    .password("$2a$10$tcUQJq1JuY98bN.aFslJKuKPjw9X0Ohz9RuN1TVoqXkRpEZEcrkU.") // asdf1234 (BCrypt)
                    .name("Administrator")
                    .nickname("admin")
                    .role(Member.Role.ADMIN)
                    .build());
            log.info("[Init] default admin created");
        } else {
            log.info("[Init] admin exists, skip");
        }

        if (memberRepository.findByAccountId("user").isEmpty()) {
            memberRepository.save(Member.builder()
                    .accountId("user")
                    .password("$2a$10$tcUQJq1JuY98bN.aFslJKuKPjw9X0Ohz9RuN1TVoqXkRpEZEcrkU.") // asdf1234 (BCrypt)
                    .name("User")
                    .nickname("user")
                    .role(Member.Role.USER)
                    .build());
            log.info("[Init] default user created");
        } else {
            log.info("[Init] user exists, skip");
        }

        log.info("[Init] MemberInitializer done");

        for (int i = 1; i <= 500; i++) {
            String accountId = "testuser" + i;

            // 이미 존재하면 스킵
            if (memberRepository.existsByAccountId(accountId)) {
                continue;
            }

            Member member = Member.builder()
                    .accountId(accountId)
                    .password("$2a$10$tcUQJq1JuY98bN.aFslJKuKPjw9X0Ohz9RuN1TVoqXkRpEZEcrkU.")
                    .name("Test User " + i)
                    .nickname("testuser" + i)
                    .email("testuser" + i + "@test.com")
                    .role(Member.Role.USER)
                    .build();

            memberRepository.save(member);
        }
    }
}
