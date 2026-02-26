package com.codepresso.codepresso.service.cache;

import com.codepresso.codepresso.product.service.CategoryService;
import com.codepresso.codepresso.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 카테고리 캐시 도입 전후 응답 시간 비교 테스트.
 * Before: 캐시 콜드 스타트 → 썬더링 허드 → DB 조회 발생
 * After : 캐시 웜업 후     → 캐시 히트   → DB 조회 없음
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryCachePerformanceTest {

    @Autowired private ProductService productService;
    @Autowired private CategoryService categoryService;

    private static final int CONCURRENT_USERS = 100;
    private static final String SEP = "=".repeat(55);

    /** [avgResponseTime, totalElapsed] 순서로 반환 */
    private long[] runConcurrentTest() throws InterruptedException {
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endLatch  = new CountDownLatch(CONCURRENT_USERS);
        AtomicLong totalResponseTime = new AtomicLong(0);

        try (ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS)) {
            for (int i = 0; i < CONCURRENT_USERS; i++) {
                executor.submit(() -> {
                    try {
                        startGate.await();
                        long start = System.currentTimeMillis();
                        productService.findProductsWithCategory();
                        totalResponseTime.addAndGet(System.currentTimeMillis() - start);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        endLatch.countDown();
                    }
                });
            }

            long startTime = System.currentTimeMillis();
            startGate.countDown();
            boolean finished = endLatch.await(60, TimeUnit.SECONDS);
            long totalElapsed = System.currentTimeMillis() - startTime;

            assertThat(finished).as("60초 내에 모든 스레드가 완료되어야 함").isTrue();
            return new long[]{totalResponseTime.get() / CONCURRENT_USERS, totalElapsed};
        }
    }

    // ==================== Order(1): 단일 사용자 응답 시간 비교 ====================

    @Test
    @Order(1)
    @DisplayName("단일 사용자 응답 시간 비교 - 캐시 없음 vs 캐시 있음")
    void singleUserResponseTimeComparison() {
        categoryService.evictCategoriesCache();

        long startBefore = System.currentTimeMillis();
        productService.findProductsWithCategory();
        long beforeMs = System.currentTimeMillis() - startBefore;

        long startAfter = System.currentTimeMillis();
        productService.findProductsWithCategory();
        long afterMs = System.currentTimeMillis() - startAfter;

        log.info("\n" + SEP);
        log.info("단일 사용자 응답 시간 비교");
        log.info(SEP);
        log.info("  캐시 없음 (첫 호출) : {}ms", beforeMs);
        log.info("  캐시 있음 (두 번째) : {}ms", afterMs);
        if (beforeMs > 0) {
            log.info("  응답 시간 개선     : {}ms → {}ms (△ {}%)", beforeMs, afterMs, (beforeMs - afterMs) * 100 / beforeMs);
        }
        log.info(SEP + "\n");
    }

    // ==================== Order(2): 동시 100명 - Before (캐시 없음) ====================

    @Test
    @Order(2)
    @DisplayName("동시 100명 - Before (캐시 없음, 썬더링 허드 발생)")
    void concurrentUsers_before_noCache() throws InterruptedException {
        categoryService.evictCategoriesCache();

        long[] result = runConcurrentTest();

        log.info("\n" + SEP);
        log.info("동시 {}명 - Before (캐시 없음)", CONCURRENT_USERS);
        log.info(SEP);
        log.info("  평균 응답 시간     : {}ms", result[0]);
        log.info("  전체 소요 시간     : {}ms", result[1]);
        log.info(SEP + "\n");
    }

    // ==================== Order(3): 동시 100명 - After (캐시 있음) ====================

    @Test
    @Order(3)
    @DisplayName("동시 100명 - After (캐시 있음, DB 쿼리 없음)")
    void concurrentUsers_after_withCache() throws InterruptedException {
        categoryService.evictCategoriesCache();
        productService.findProductsWithCategory(); // 캐시 워밍업

        long[] result = runConcurrentTest();

        log.info("\n" + SEP);
        log.info("동시 {}명 - After (캐시 있음)", CONCURRENT_USERS);
        log.info(SEP);
        log.info("  평균 응답 시간     : {}ms", result[0]);
        log.info("  전체 소요 시간     : {}ms", result[1]);
        log.info(SEP + "\n");
    }
}