package com.codepresso.codepresso.repository.order;

import com.codepresso.codepresso.dto.order.OrderSummaryProjection;
import com.codepresso.codepresso.entity.order.Orders;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.Random;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OrderRepositoryPerformanceTest {
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private EntityManager entityManager;

    private static final int ITERATION_COUNT = 5000;
    private static final int WARMUP_COUNT = 1000;
    private static final int TOTAL_MEMBERS = 500;
    private static final int PAGE_SIZE = 100;
    private final Random random = new Random();

    @Test
    @DisplayName("기존 쿼리 메서드 성능 테스트 - 5000회 반복")
    void testOriginalQueryPerformance() {
        Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by("orderDate").descending());

        // Warm-up (JVM 최적화를 위한 준비 실행)
        for (int i = 0; i < WARMUP_COUNT; i++) {
            Long randomMemberId = getRandomMemberId();
            ordersRepository.findByMemberIdWithPaging(randomMemberId, pageable);
            entityManager.clear();
        }

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < ITERATION_COUNT; i++) {
            entityManager.clear();
            Long randomMemberId = getRandomMemberId();
            Page<Orders> result = ordersRepository.findByMemberIdWithPaging(randomMemberId, pageable);

            // 실제 데이터 접근을 측정 시간 내에 포함 (Lazy Loading 강제 실행)
            result.getContent().forEach(order -> {
                order.getBranch().getBranchName();
                order.getOrdersDetails().size();
            });
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double avgTime = totalTime / (double) ITERATION_COUNT;

        System.out.println("=======================================");
        System.out.println("기존 쿼리 메서드(findByMemberIdWithPaging)");
        System.out.println("=======================================");
        System.out.println("총 실행 횟수: " + ITERATION_COUNT + "회");
        System.out.println("총 소요 시간: " + totalTime + "ms");
        System.out.println("평균 실행 시간: " + String.format("%.2f", avgTime) + "ms");
        System.out.println("========================================");
    }

    @Test
    @DisplayName("개선 쿼리 메서드 성능 테스트 - 5000회 반복")
    void testImproveQueryPerformance() {
        Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by("orderDate").descending());

        // Warm-up (JVM 최적화를 위한 준비 실행)
        for (int i = 0; i < WARMUP_COUNT; i++) {
            Long memberId = (long) (i + 1);
            ordersRepository.findByMemberIdWithPaging2(memberId, pageable);
            entityManager.clear();
        }

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < ITERATION_COUNT; i++) {
            entityManager.clear();
            Long randomMemberId = getRandomMemberId();
            Page<OrderSummaryProjection> result = ordersRepository.findByMemberIdWithPaging2(randomMemberId, pageable);

            // 실제 Projection 데이터 접근
            result.getContent().forEach(order -> {
                order.getOrderId();
                order.getBranchName();
                order.getRepresentativeProductName();
            });
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double avgTime = totalTime / (double) ITERATION_COUNT;

        System.out.println("=======================================");
        System.out.println("개선 쿼리 메서드(findByMemberIdWithPaging)");
        System.out.println("=======================================");
        System.out.println("총 실행 횟수: " + ITERATION_COUNT + "회");
        System.out.println("총 소요 시간: " + totalTime + "ms");
        System.out.println("평균 실행 시간: " + String.format("%.2f", avgTime) + "ms");
        System.out.println("========================================");
    }

    private Long getRandomMemberId() {
        return (long) (random.nextInt(TOTAL_MEMBERS)+1);
    }

}
