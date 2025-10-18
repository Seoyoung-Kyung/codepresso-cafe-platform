package com.codepresso.codepresso.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 비활성화. 운영 전환 시 활성화하고 폼에 토큰 삽입 권장
            .csrf(AbstractHttpConfigurer::disable)

            // URL 접근 권한
            .authorizeHttpRequests(auth -> auth
                // 공개 페이지/정적 리소스
                .requestMatchers(
                    "/",
                    "/auth/login",
                    "/auth/signup",
                    "/api/auth/**",
                    "/banners/**",
                    "/error",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                // 로그인 필요 페이지/API
                .requestMatchers(
                    "/branch/**",
                    "/member/**",
                    "/favorites",
                    "/users/**",
//                    "/api/users/**",
                    "/boards/**",
                    "/products/**",
                        "/payments/**",
                        "/orders/**"
                ).authenticated()
                .anyRequest().permitAll()
            )

            // 폼 로그인(세션 기반)
            .formLogin(form -> form
                .loginPage("/auth/login")                   // 커스텀 로그인 페이지 경로 (GET)
                .loginProcessingUrl("/login")               // 로그인 처리 경로 (POST)
                .usernameParameter("accountId")             // 폼에서 사용자 ID input name
                .passwordParameter("password")              // 폼에서 비밀번호 input name
                .defaultSuccessUrl("/branch/list", true)  // 로그인 성공 시 매장 목록 화면으로 이동
                .failureUrl("/auth/login?error=1")
                .permitAll()
            )

            // Remember-me(로그인 유지) 설정
            .rememberMe(remember -> remember
                .rememberMeParameter("remember-me")         // 체크박스 name
                .tokenValiditySeconds(60 * 60 * 24 * 14)    // 14일 유지
                .key("codepresso-remember-me-key")          // 토큰 서명 키(운영에서는 환경변수로 관리)
            )

            // 로그아웃 설정 (POST /logout 권장)
            .logout(logout -> logout
                .logoutUrl("/logout")                   // 기본: POST /logout
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .logoutSuccessUrl("/auth/login?logout=1")
            );

        return http.build();
    }

    /**
     * 비밀번호 인코더 빈
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

}
