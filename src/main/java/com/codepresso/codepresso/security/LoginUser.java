package com.codepresso.codepresso.security;

import com.codepresso.codepresso.entity.member.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Principal(UserDetails).
 * - 스프링 시큐리티가 세션에 보관하며, 컨트롤러에서 @AuthenticationPrincipal 로 바로 주입받아 사용
 */
@Getter
public class LoginUser extends User {

    private final Long memberId;    // 회원 고유 ID
    private final String accountId; // 로그인 ID
    private final String email;     // 이메일
    private final String name;      // 이름(실명)
    private final String phone;     // 휴대전화(숫자만 저장)
    private final String nickname;  // 닉네임
    private final String role;      // ROLE 문자열(예: USER/ADMIN)

    public LoginUser(Member member,
                     Collection<? extends GrantedAuthority> authorities) {
        super(member.getAccountId(), member.getPassword(), authorities);
        this.memberId = member.getId();
        this.accountId = member.getAccountId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.phone = member.getPhone();
        this.nickname = member.getNickname();
        this.role = member.getRole() == null ? "USER" : member.getRole().name();
    }

}
