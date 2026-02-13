package com.codepresso.codepresso.common.config;

import com.codepresso.codepresso.monitoring.QueryCountInterceptor;
import com.codepresso.codepresso.web.LoggedInRedirectInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 로그인 상태 -> 매장 선택 화면 리다이렉트
 */
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.file.upload.path}")
    private String uploadPath;

    private final QueryCountInterceptor queryCountInterceptor;

    public WebMvcConfig(QueryCountInterceptor queryCountInterceptor) {
        this.queryCountInterceptor = queryCountInterceptor;
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/profile-images/**")
                .addResourceLocations("file:" + uploadPath);

        registry.addResourceHandler("/uploads/reviews/**")
                .addResourceLocations("file:src/main/resources/static/uploads/reviews/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 로그인 상태에서 홈(/)이나 로그인 페이지(/auth/login)로 접근하면 매장 선택으로 보냄
        registry.addInterceptor(new LoggedInRedirectInterceptor())
                .addPathPatterns("/", "/auth/login");

        // 쿼리 카운트 모니터링 인터셉터
        registry.addInterceptor(queryCountInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**", "/images/**", "/uploads/**", "/actuator/**");
    }

    /**
     * HTTP 메서드 필터 (PUT, DELETE 등을 지원하기 위함)
     */
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

}