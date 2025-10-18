package com.codepresso.codepresso.security;

import com.codepresso.codepresso.entity.member.Member;
import com.codepresso.codepresso.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 사용자 정보를 로드하여 스프링 시큐리티에 전달하는 서비스.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /**
     * 스프링 시큐리티가 로그인 시 호출하는 메서드.
     * - 여기서 accountId(=username)를 기반으로 회원을 찾아 UserDetails로 변환해 반환합니다.
     *
     * @param username 로그인 폼에서 넘어온 사용자 ID (여기서는 accountId)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByAccountId(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 권한(Authority) 부여: ROLE_ 접두사는 스프링 관례 (roles() 사용 시 자동 부착)
        Collection<? extends GrantedAuthority> authorities = toAuthorities(member.getRole());

        // 커스텀 Principal(LoginUser)로 매핑하여 추가 정보까지 담아둡니다.
        return new LoginUser(member, authorities);
    }

    private List<SimpleGrantedAuthority> toAuthorities(Member.Role role) {
        String name = role == null ? "USER" : role.name();
        return List.of(new SimpleGrantedAuthority("ROLE_" + name));
    }
}