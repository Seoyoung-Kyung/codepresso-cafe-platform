package com.codepresso.codepresso.service.member;

import com.codepresso.codepresso.member.service.FavoriteAtomicService;
import com.codepresso.codepresso.member.service.FavoriteOptimisticService;
import com.codepresso.codepresso.member.service.FavoritePessimisticService;
import com.codepresso.codepresso.member.service.FavoriteService;
import com.codepresso.codepresso.product.entity.Product;
import com.codepresso.codepresso.member.repository.FavoriteRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.*;
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
    private FavoriteAtomicService favoriteAtomicService;

    @Autowired
    private FavoriteRepository favoriteRepository;

    private static final int THREAD_COUNT = 1000;
    private Product testProduct;

    @Test
    @Order(1)
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

        log.info("=====================================");
        log.info("락 없이 즐겨찾기 동시성 테스트 결과 - 1000명");
        log.info("실행시간 : {}ms", duration);
        log.info("성공 요청 : {}", successCount);
        log.info("실패 요청 : {}", failCount);
        log.info("실제 Favorite 레코드 수 : {}", finalCount);
        log.info("데이터 정합성 : {} (예상 : {}, 실제 : {})",
                finalCount == THREAD_COUNT ? "성공" : "실패",
                THREAD_COUNT, finalCount);
        log.info("=====================================");

    }

    @Test
    @Order(2)
    @DisplayName("낙관적 락 - 1000명 동시 요청")
    void testOptimisticLock() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();
        long productId = 1;

        for (int i = 0; i < THREAD_COUNT; i++) {
            final long memberId = i + 1L;
            executorService.execute(() -> {
                try {
                    optimisticService.addFavoriteWithRetry(memberId, productId);
                    successCount.incrementAndGet();
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

        log.info("=====================================");
        log.info("낙관적 락 즐겨찾기 동시성 테스트 결과 - 1000명");
        log.info("실행시간 : {}ms", duration);
        log.info("성공 요청 : {}", successCount);
        log.info("실패 요청 : {}", failCount);
        log.info("실제 Favorite 레코드 수 : {}", finalCount);
        log.info("데이터 정합성 : {} (예상 : {}, 실제 : {})",
                finalCount == THREAD_COUNT ? "성공" : "실패",
                THREAD_COUNT, finalCount);
        log.info("=====================================");

        assertThat(finalCount).isEqualTo(THREAD_COUNT);
        assertThat(actualFavoriteCount).isEqualTo(THREAD_COUNT);
        assertThat(finalCount).isEqualTo(actualFavoriteCount);

    }

    @Test
    @Order(3)
    @DisplayName("비관적 락 - 1000명 동시 요청")
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

        log.info("=====================================");
        log.info("비관적 락 즐겨찾기 동시성 테스트 결과 - 1000명");
        log.info("실행시간 : {}ms", duration);
        log.info("성공 요청 : {}", successCount);
        log.info("실패 요청 : {}", failCount);
        log.info("실제 Favorite 레코드 수 : {}", finalCount);
        log.info("데이터 정합성 : {} (예상 : {}, 실제 : {})",
                finalCount == THREAD_COUNT ? "성공" : "실패",
                THREAD_COUNT, finalCount);
        log.info("=====================================");

        assertThat(finalCount).isEqualTo(THREAD_COUNT);
        assertThat(actualFavoriteCount).isEqualTo(THREAD_COUNT);
        assertThat(finalCount).isEqualTo(actualFavoriteCount);
    }

    @Test
    @Order(4)
    @DisplayName("원자적 Update 방식 - 1000명 즐겨찾기 동시 요청")
    void testAtomicUpdate() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        long startTime = System.currentTimeMillis();
        long productId = 1;

        for (int i = 0; i < THREAD_COUNT; i++) {
            final long memberId = i + 1L;
            executorService.execute(() -> {
                try {
                    favoriteAtomicService.addFavoriteWithAtomicUpdate(memberId, productId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.decrementAndGet();
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

        log.info("=====================================");
        log.info("원자적 Update 방식 즐겨찾기 동시성 테스트 결과 - 1000명");
        log.info("실행시간 : {}ms", duration);
        log.info("성공 요청 : {}", successCount);
        log.info("실패 요청 : {}", failCount);
        log.info("실제 Favorite 레코드 수 : {}", finalCount);
        log.info("데이터 정합성 : {} (예상 : {}, 실제 : {})",
                finalCount == THREAD_COUNT ? "성공" : "실패",
                THREAD_COUNT, finalCount);
        log.info("=====================================");

        assertThat(finalCount).isEqualTo(THREAD_COUNT);
        assertThat(actualFavoriteCount).isEqualTo(THREAD_COUNT);
        assertThat(actualFavoriteCount).isEqualTo(finalCount);
    }
}
