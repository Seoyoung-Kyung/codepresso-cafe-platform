package com.codepresso.codepresso.service.product;

import com.codepresso.codepresso.product.service.ProductService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ProductServicePerformanceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("readOnly 성능 비교")
    void compareReadOnlyPerformance() {
        int iterations = 10;

        System.out.println("\n" + "=".repeat(70));
        System.out.println("readOnly 성능 비교 테스트 (10회 반복)");
        System.out.println("=".repeat(70) + "\n");

        // 1. readOnly = false (일반)
        long normalTotal = 0;
        long productId = 1;
        System.out.println("일반 @Transactional");
        for (int i = 0; i < iterations; i++) {

            long start = System.currentTimeMillis();
            productService.findByProductId(productId); // readOnly 없는 메서드
            long end = System.currentTimeMillis();

            long elapsed = end - start;
            normalTotal += elapsed;
            System.out.println("  " + (i + 1) + "번째: " + elapsed + "ms");

            productId += 1L;

        }
        long normalAvg = normalTotal / iterations;
        System.out.println("  평균: " + normalAvg + "ms\n");

        // 2. readOnly = true
        long readOnlyTotal = 0;
        productId = 1;
        System.out.println("@Transactional(readOnly = true)");
        for (int i = 0; i < iterations; i++) {

            long start = System.currentTimeMillis();
            productService.findByProductIdReadOnly(productId); // readOnly 있는 메서드
            long end = System.currentTimeMillis();

            long elapsed = end - start;
            readOnlyTotal += elapsed;
            System.out.println("  " + (i + 1) + "번째: " + elapsed + "ms");

            productId += 1L;

        }
        long readOnlyAvg = readOnlyTotal / iterations;
        System.out.println("  평균: " + readOnlyAvg + "ms\n");

        // 결과
        System.out.println("=".repeat(70));
        System.out.println("@Transactional readOnly 성능 비교");
        System.out.println("=".repeat(70));
        System.out.println("readOnly = false: " + normalAvg + "ms");
        System.out.println("readOnly = true: " + readOnlyAvg + "ms");
        System.out.println("개선: " + (normalAvg - readOnlyAvg) + "ms 단축");
        System.out.println("=".repeat(70) + "\n");
    }
}