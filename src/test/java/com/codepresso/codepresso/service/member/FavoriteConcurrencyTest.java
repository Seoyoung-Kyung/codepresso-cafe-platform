package com.codepresso.codepresso.service.member;

import com.codepresso.codepresso.entity.member.Favorite;
import com.codepresso.codepresso.entity.product.Product;
import com.codepresso.codepresso.repository.member.FavoriteRepository;
import com.codepresso.codepresso.repository.member.ProductConcurrencyRepository;
import com.codepresso.codepresso.repository.product.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FavoriteConcurrencyTest {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private FavoriteOptimisticService optimisticService;

    @Autowired
    private FavoritePessimisticService pessimisticService;

    @Autowired
    private FavoriteRepository favoriteRepository;

    private static final int THREAD_COUNT = 1000;
    private Product testProduct;

    @Test
    @DisplayName("락 없이 1000명 동시 요청 - 동시성 문제 발생 예상")
    void testWithoutLock() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0); // 멀티스레드 환경에서 안전하게 정수를 다룰 수 있게 해주는 클래스
        AtomicInteger failCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();
        long productId = 1;

        for (int i = 0; i < THREAD_COUNT; i++) {
            final long memberId = i + 1L;
            executorService.execute(() -> {
                try {
                    favoriteService.addFavoriteWithOutLock(memberId, productId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });


        }
        latch.await(60, TimeUnit.SECONDS);
        executorService.shutdown();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // then
        Long finalCount = favoriteService.getFavoriteCount(productId);

        log.info("========================");
        log.info("락 없이 즐겨찾기 동시성 테스트 결과");
        log.info("실행시간 : {}ms", duration);
        log.info("성공 요청 : {}", successCount);
        log.info("실패 요청 : {}", failCount);
        log.info("실제 Favorite 레코드 수 : {}", finalCount);
        log.info("데이터 정합성 : {} (예상 : {}, 실제 : {})",
                finalCount == THREAD_COUNT ? "성공" : "실패",
                THREAD_COUNT, finalCount);
        log.info("========================");

    }

    @Test
    @Order(1)
    @DisplayName("낙관적 락 - 1000명 동시 요청")
    void testOptimisticLock() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicInteger totalRetries = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();
        long productId = 1;

        for (int i = 0; i < THREAD_COUNT; i++) {
            final long memberId = i + 1L;
            executorService.execute(() -> {
                try {
                    int retries = optimisticService.addFavoriteWithRetry(memberId, productId, 10);
                    successCount.incrementAndGet();
                    totalRetries.addAndGet(retries);
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    log.error("실패 : {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(60, TimeUnit.SECONDS);
        executorService.shutdown();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // then
        Long finalCount = optimisticService.getFavoriteCount(productId);
        long actualFavoriteCount = favoriteRepository.countByProductId(productId);

        log.info("========================");
        log.info("낙관적 락 테스트 결과");
        log.info("실행시간 : {}ms", duration);
        log.info("성공 요청 : {}", successCount);
        log.info("실패 요청 : {}", failCount);
        log.info("재시도 횟수 : {}", totalRetries);
        log.info("실제 Favorite 레코드 수 : {}", finalCount);
        log.info("데이터 정합성 : {} (예상 : {}, 실제 : {})",
                finalCount == THREAD_COUNT ? "성공" : "실패",
                THREAD_COUNT, finalCount);

        assertThat(finalCount).isEqualTo(THREAD_COUNT);
        assertThat(actualFavoriteCount).isEqualTo(THREAD_COUNT);
        assertThat(finalCount).isEqualTo(actualFavoriteCount);

    }

    @Test
    @Order(2)
    @DisplayName("비관적 락 - 500명 동시 요청")
    void testPessimisticLock() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT); // 스레드 풀(Thread Pool)을 관리하는 인터페이스
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT); // 스레드 풀 작업이 완료될 때까지 카운팅하는 도구
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();
        long productId = 1;

        // 테스트 전후 product like 비교 필요
        for (int i = 0; i < THREAD_COUNT; i++) {
            final long memberId = i + 1L;
            executorService.execute(() -> {
                try {
                    pessimisticService.addFavorite(memberId, productId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(60, TimeUnit.SECONDS);
        executorService.shutdown();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        Long finalCount = pessimisticService.getFavoriteCount(productId);
        long actualFavoriteCount = favoriteRepository.countByProductId(productId);

        log.info("========================");
        log.info("비관적 락 테스트 결과");
        log.info("실행시간 : {}ms", duration);
        log.info("성공 요청 : {}", successCount);
        log.info("실패 요청 : {}", failCount);
        log.info("실제 Favorite 레코드 수 : {}", finalCount);
        log.info("데이터 정합성 : {} (예상 : {}, 실제 : {})",
                finalCount == THREAD_COUNT ? "성공" : "실패",
                THREAD_COUNT, finalCount);
        log.info("========================");

        assertThat(finalCount).isEqualTo(THREAD_COUNT);
        assertThat(actualFavoriteCount).isEqualTo(THREAD_COUNT);
        assertThat(finalCount).isEqualTo(actualFavoriteCount);
    }
}
