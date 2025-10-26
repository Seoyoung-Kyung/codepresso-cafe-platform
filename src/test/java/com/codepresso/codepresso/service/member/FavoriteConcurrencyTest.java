package com.codepresso.codepresso.service.member;

import com.codepresso.codepresso.entity.product.Product;
import com.codepresso.codepresso.repository.member.FavoriteConcurrencyRepository;
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
    private FavoritePessimisticService pessimisticService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FavoriteConcurrencyRepository favoriteRepository;

    private static final int THREAD_COUNT = 500;
    private Product testProduct;

    @Test
    @Order(1)
    @DisplayName("낙관적 락 - 500명 동시 요청")
    void testOptimisticLock() throws InterruptedException {
        ExecutorService executorService = new ExecutorService(THREAD_COUNT);
    }

    @Test
    @Order(2)
    @DisplayName("비관적 락 - 500명 동시 요청")
    void testPessimisticLock() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT); // 스레드 풀(Thread Pool)을 관리하는 인터페이스
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();
        long productId = 1;

        for(int i = 0; i < THREAD_COUNT; i++) {
            final long memberId = i + 1L;
            executorService.execute(() -> {
                try {
                    pessimisticService.addFavorite(memberId, productId);
                    successCount.incrementAndGet();
                } catch(Exception e){
                    failCount.incrementAndGet();
                    log.debug("실패 : {} ", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(120, TimeUnit.SECONDS);
        executorService.shutdown();

        long duration = System.currentTimeMillis() - startTime;

        // then
        long actualFavorites = favoriteRepository.countByProductId(productId);

        log.info("========================");
        log.info("비관적 락 테스트 결과");
        log.info("실행시간 : {}ms", duration);
        log.info("성공 요청 : {}", successCount);
        log.info("실패 요청 : {}", failCount);
        log.info("실제 Favorite 레코드 수 : {}", actualFavorites);
        log.info("데이터 정합성 : {} (예상 : {}, 실제 : {})",
                actualFavorites == THREAD_COUNT ? "성공" : "실패",
                THREAD_COUNT, actualFavorites);
        log.info("========================");

        assertThat(actualFavorites).isEqualTo(THREAD_COUNT);
    }
}
