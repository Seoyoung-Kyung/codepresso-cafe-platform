package com.codepresso.codepresso.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 스케줄러 설정
 * @EnableScheduling 어노테이션으로 스케줄링 기능 활성화
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {
    // 스케줄링 기능 활성화를 위한 설정 클래스
}