package com.codepresso.codepresso.service.cart;

import com.codepresso.codepresso.cart.entity.CartItem;
import com.codepresso.codepresso.cart.repository.CartItemRepository;
import com.codepresso.codepresso.cart.repository.CartOptionRepository;
import com.codepresso.codepresso.cart.repository.CartRepository;
import com.codepresso.codepresso.cart.service.CartService;
import com.codepresso.codepresso.member.entity.Member;
import com.codepresso.codepresso.member.repository.MemberRepository;
import com.codepresso.codepresso.product.entity.Product;
import com.codepresso.codepresso.product.entity.ProductOption;
import com.codepresso.codepresso.product.repository.ProductOptionRepository;
import com.codepresso.codepresso.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class CartServicePerformanceTest {

    @Autowired private CartService cartService;
    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private CartOptionRepository cartOptionRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private ProductOptionRepository productOptionRepository;
    @Autowired private EntityManager entityManager;

    private Long testMemberId;
    private List<Product> testProducts;

    @BeforeEach
    void setUp() {
        // 기존 데이터 정리
        cartOptionRepository.deleteAll();
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();

        // 테스트용 회원
        Member testMember = memberRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("테스트용 회원이 없습니다."));
        testMemberId = testMember.getId();

        // 옵션이 3개 이상인 상품 10개 선택
        testProducts = productRepository.findAll().stream()
                .filter(p -> productOptionRepository.countOptionByProductId(p.getId()) >= 3)
                .limit(10)
                .toList();

        if (testProducts.size() < 10) {
            throw new RuntimeException("옵션이 3개 이상인 상품이 부족합니다. 현재: " + testProducts.size() + "개");
        }
    }

    /**
     * 테스트용 장바구니 데이터 생성
     * - 상품 10개, 각 상품당 옵션 3개 (총 30개)
     */
    @Transactional
    Long createTestCartData() {
        cartOptionRepository.deleteAll();
        cartItemRepository.deleteAll();

        for (Product product : testProducts) {
            List<Long> options = productOptionRepository.findOptionByProductId(product.getId())
                    .stream()
                    .limit(3)
                    .map(ProductOption::getId)
                    .toList();

            cartService.addItemWithOptions(testMemberId, product.getId(), 1, options);
        }

        Long cartId = cartRepository.findByMemberId(testMemberId)
                .orElseThrow(() -> new RuntimeException("장바구니 생성 실패"))
                .getId();

        // 데이터 검증
        long itemCount = cartItemRepository.findByCartId(cartId).size();
        long optionCount = cartOptionRepository.count();

        assertThat(itemCount).isEqualTo(10);
        assertThat(optionCount).isEqualTo(30);

        return cartId;
    }

    @Test
    @DisplayName("장바구니 삭제 성능 비교: 4가지 방식")
    void compareCartClearPerformance() {
        int iterations = 10;

        log.info("\n" + "=".repeat(70));
        log.info("장바구니 삭제 성능 비교 테스트");
        log.info("=".repeat(70));
        log.info("조건: 상품 10개, 옵션 30개, 반복 {}번\n", iterations);

        // 방식 1: 개선 전 (반복문 + deleteAllInBatch)
        long beforeTotal = measurePerformance("방식 1: 개선 전 (반복문 + deleteAllInBatch)", iterations, () -> {
            Long cartId = createTestCartData();
            entityManager.clear();
            cartService.clearCart(testMemberId, cartId);
            entityManager.clear();
        });

        // 방식 2: 벌크 삭제 (@Query + @Modifying)
        long bulkTotal = measurePerformance("방식 2: 벌크 삭제 (@Query + @Modifying)", iterations, () -> {
            Long cartId = createTestCartData();
            entityManager.clear();
            cartService.clearCartByBulk(testMemberId, cartId);
            entityManager.clear();
        });

        // 방식 3: 벌크 삭제 (deleteAllInBatch)
        long bulk2Total = measurePerformance("방식 3: 벌크 삭제 (deleteAllInBatch)", iterations, () -> {
            Long cartId = createTestCartData();
            entityManager.clear();
            cartService.clearCartByBulk2(testMemberId, cartId);
            entityManager.clear();
        });

        // 방식 4: Cascade 활용
        long cascadeTotal = measurePerformance("방식 4: Cascade 활용", iterations, () -> {
            Long cartId = createTestCartData();
            entityManager.clear();
            cartService.clearCartByCascade(testMemberId, cartId);
            entityManager.clear();
        });

        // 결과 출력
        printResult(beforeTotal, bulkTotal, bulk2Total, cascadeTotal, iterations);

        // 검증
        assertThat(bulkTotal).isLessThan(beforeTotal);
        assertThat(bulk2Total).isLessThan(beforeTotal);
    }

    @Test
    @DisplayName("쿼리 개수 확인: 4가지 방식")
    void verifyQueryCount() {
        log.info("\n" + "=".repeat(70));
        log.info("DELETE 쿼리 개수 확인");
        log.info("=".repeat(70) + "\n");

        // ===== 방식 1: 개선 전 =====
        log.info("방식 1: 개선 전 (반복문 + deleteAllInBatch)");
        log.info("   예상: DELETE 11번 (Option 10번 + Item 1번)");
        log.info("-".repeat(70));

        Long cartId1 = createTestCartData();
        entityManager.clear();
        log.info("삭제 전: CartItem {}개, CartOption {}개",
                cartItemRepository.findByCartId(cartId1).size(),
                cartOptionRepository.count());

        cartService.clearCart(testMemberId, cartId1);

        log.info("삭제 후: CartItem {}개, CartOption {}개\n",
                cartItemRepository.findByCartId(cartId1).size(),
                cartOptionRepository.count());

        // ===== 방식 2: 벌크 삭제 (@Query) =====
        log.info("방식 2: 벌크 삭제 (@Query + @Modifying)");
        log.info("   예상: DELETE 2번 (Option 1번 + Item 1번)");
        log.info("-".repeat(70));

        Long cartId2 = createTestCartData();
        entityManager.clear();
        log.info("삭제 전: CartItem {}개, CartOption {}개",
                cartItemRepository.findByCartId(cartId2).size(),
                cartOptionRepository.count());

        cartService.clearCartByBulk(testMemberId, cartId2);

        log.info("삭제 후: CartItem {}개, CartOption {}개\n",
                cartItemRepository.findByCartId(cartId2).size(),
                cartOptionRepository.count());

        // ===== 방식 3: 벌크 삭제 (deleteAllInBatch) =====
        log.info("방식 3: 벌크 삭제 (deleteAllInBatch)");
        log.info("   예상: DELETE 4번 (SELECT 2번 + DELETE 2번)");
        log.info("-".repeat(70));

        Long cartId3 = createTestCartData();
        entityManager.clear();
        log.info("삭제 전: CartItem {}개, CartOption {}개",
                cartItemRepository.findByCartId(cartId3).size(),
                cartOptionRepository.count());

        cartService.clearCartByBulk2(testMemberId, cartId3);

        log.info("삭제 후: CartItem {}개, CartOption {}개\n",
                cartItemRepository.findByCartId(cartId3).size(),
                cartOptionRepository.count());

        // ===== 방식 4: Cascade =====
        log.info("방식 4: Cascade 활용");
        log.info("   예상: DELETE 여러 번 (JPA 개별 처리)");
        log.info("-".repeat(70));

        Long cartId4 = createTestCartData();
        entityManager.clear();
        log.info("삭제 전: CartItem {}개, CartOption {}개",
                cartItemRepository.findByCartId(cartId4).size(),
                cartOptionRepository.count());

        cartService.clearCartByCascade(testMemberId, cartId4);

        log.info("삭제 후: CartItem {}개, CartOption {}개",
                cartItemRepository.findByCartId(cartId4).size(),
                cartOptionRepository.count());

        log.info("\n" + "=".repeat(70) + "\n");
    }

    @Test
    @DisplayName("테스트 데이터 구조 확인")
    @Transactional
    void verifyTestDataStructure() {
        Long cartId = createTestCartData();
        List<CartItem> items = cartItemRepository.findByCartId(cartId);

        log.info("\n" + "=".repeat(70));
        log.info("테스트 데이터 구조");
        log.info("=".repeat(70));

        int totalOptions = 0;
        for (int i = 0; i < items.size(); i++) {
            int optionCount = items.get(i).getCartOptions().size();
            totalOptions += optionCount;
            log.info("{}. {} - 옵션 {}개",
                    i + 1,
                    items.get(i).getProduct().getProductName(),
                    optionCount);
        }

        log.info("\n총 상품: {}개, 총 옵션: {}개", items.size(), totalOptions);
        log.info("=".repeat(70) + "\n");

        assertThat(items).hasSize(10);
        assertThat(totalOptions).isEqualTo(30);
    }

    // ===== 헬퍼 메서드 =====

    private long measurePerformance(String label, int iterations, Runnable task) {
        log.info("{} 측정 시작", label);
        log.info("-".repeat(70));

        long totalTime = 0;
        for (int i = 0; i < iterations; i++) {
            long startTime = System.currentTimeMillis();
            task.run();
            long endTime = System.currentTimeMillis();
            long elapsed = endTime - startTime;

            totalTime += elapsed;
            log.info("  {}번째: {}ms", i + 1, elapsed);
        }

        long average = totalTime / iterations;
        log.info("  평균: {}ms\n", average);
        return totalTime;
    }

    private void printResult(long beforeTotal, long bulkTotal, long bulk2Total, long cascadeTotal, int iterations) {
        long beforeAvg = beforeTotal / iterations;
        long bulkAvg = bulkTotal / iterations;
        long bulk2Avg = bulk2Total / iterations;
        long cascadeAvg = cascadeTotal / iterations;

        log.info("\n" + "=".repeat(70));
        log.info("성능 비교 결과");
        log.info("=".repeat(70));
        log.info("");

        log.info("방식 1: 개선 전 (반복문 + deleteAllInBatch)");
        log.info("   - 총 시간: {}ms", beforeTotal);
        log.info("   - 평균: {}ms", beforeAvg);
        log.info("   - 쿼리: DELETE 11번 (Option 10번 + Item 1번)");
        log.info("");

        log.info("방식 2: 벌크 삭제 (@Query + @Modifying)");
        log.info("   - 총 시간: {}ms", bulkTotal);
        log.info("   - 평균: {}ms", bulkAvg);
        log.info("   - 쿼리: DELETE 2번 (Option 1번 + Item 1번)");
        log.info("");

        log.info("방식 3: 벌크 삭제 (deleteAllInBatch)");
        log.info("   - 총 시간: {}ms", bulk2Total);
        log.info("   - 평균: {}ms", bulk2Avg);
        log.info("   - 쿼리: SELECT 2번 + DELETE 2번 (총 4번)");
        log.info("");

        log.info("방식 4: Cascade 활용");
        log.info("   - 총 시간: {}ms", cascadeTotal);
        log.info("   - 평균: {}ms", cascadeAvg);
        log.info("   - 쿼리: DELETE 여러 번 (JPA 개별 처리)");
        log.info("");

        // 성능 비교
        log.info("성능 비교:");
        log.info("   - @Query vs 반복문: {:.2f}배 빠름 ({:.1f}% 향상)",
                (double) beforeAvg / bulkAvg,
                ((double) (beforeAvg - bulkAvg) / beforeAvg) * 100);
        log.info("   - @Query vs deleteAllInBatch: {:.2f}배 빠름",
                (double) bulk2Avg / bulkAvg);
        log.info("   - @Query vs Cascade: {:.2f}배 빠름",
                (double) cascadeAvg / bulkAvg);
        log.info("");

        // 순위
        log.info("성능 순위:");
        log.info("   1등: @Query + @Modifying ({}ms)", bulkAvg);
        log.info("   2등: deleteAllInBatch ({}ms)", bulk2Avg);

        if (beforeAvg < cascadeAvg) {
            log.info("   3등: 반복문 ({}ms)", beforeAvg);
            log.info("   4등: Cascade ({}ms)", cascadeAvg);
        } else {
            log.info("   3등: Cascade ({}ms)", cascadeAvg);
            log.info("   4등: 반복문 ({}ms)", beforeAvg);
        }

        log.info("");
        log.info("=".repeat(70) + "\n");
    }
}