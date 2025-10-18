package com.codepresso.codepresso.config;

import com.codepresso.codepresso.web.LoggedInRedirectInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 로그인 상태 -> 매장 선택 화면 리다이렉트
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 로그인 상태에서 홈(/)이나 로그인 페이지(/auth/login)로 접근하면 매장 선택으로 보냄
        registry.addInterceptor(new LoggedInRedirectInterceptor())
                .addPathPatterns("/", "/auth/login");
    }
}