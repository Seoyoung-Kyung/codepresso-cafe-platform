package com.codepresso.codepresso.service.cart;

import com.codepresso.codepresso.cart.repository.CartItemRepository;
import com.codepresso.codepresso.cart.repository.CartOptionRepository;
import com.codepresso.codepresso.cart.repository.CartRepository;
import com.codepresso.codepresso.cart.service.CartService;
import com.codepresso.codepresso.cart.service.CartServiceImproveGetCartByMemberId;
import com.codepresso.codepresso.member.entity.Member;
import com.codepresso.codepresso.member.repository.MemberRepository;
import com.codepresso.codepresso.monitoring.QueryType;
import com.codepresso.codepresso.monitoring.RequestContext;
import com.codepresso.codepresso.monitoring.RequestContextHolder;
import com.codepresso.codepresso.product.entity.Product;
import com.codepresso.codepresso.product.entity.ProductOption;
import com.codepresso.codepresso.product.repository.ProductOptionRepository;
import com.codepresso.codepresso.product.repository.ProductRepository;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * N+1 문제 개선 전후 성능 비교 테스트
 *
 * 측정 항목:
 * 1. 쿼리 실행 횟수 (Query Count)
 * 2. 응답 시간 (Response Time)
 * 3. DB 커넥션 점유 시간 (Connection Hold Time)
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CartServiceN1PerformanceTest {

    @Autowired private CartService cartService;
    @Autowired private CartServiceImproveGetCartByMemberId improvedCartService;
    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private CartOptionRepository cartOptionRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private ProductOptionRepository productOptionRepository;
    @Autowired private EntityManager entityManager;
    @Autowired private DataSource dataSource;

    private Long testMemberId;
    private List<Product> testProducts;

    // ==================== 측정 결과 저장용 ====================

    /** 단일 측정 결과를 담는 레코드 */
    record MeasureResult(int queryCount, long responseTimeMs, long connectionHoldTimeMs,
                         Map<QueryType, Integer> queryDetail,
                         Map<String, Integer> queryByTable) {}

    // ==================== 셋업 ====================

    @BeforeEach
    void setUp() {
        cartOptionRepository.deleteAll();
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();

        Member testMember = memberRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("테스트용 회원이 없습니다."));
        testMemberId = testMember.getId();

        testProducts = productRepository.findAll().stream()
                .filter(p -> productOptionRepository.countOptionByProductId(p.getId()) >= 3)
                .limit(10)
                .toList();

        if (testProducts.size() < 10) {
            throw new RuntimeException("옵션이 3개 이상인 상품이 부족합니다. 현재: " + testProducts.size() + "개");
        }
    }

    @Transactional
    void createTestCartData() {
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

        assertThat(cartItemRepository.findByCartId(cartId)).hasSize(10);
        assertThat(cartOptionRepository.count()).isEqualTo(30);
    }

    // ==================== 측정 유틸 ====================

    private RequestContext startQueryCount() {
        RequestContext ctx = RequestContext.builder()
                .httpMethod("TEST")
                .bestMatchPath("/test")
                .build();
        RequestContextHolder.initContext(ctx);
        return ctx;
    }

    private int getTotalQueryCount(RequestContext ctx) {
        return ctx.getQueryCountByType().values().stream()
                .mapToInt(Integer::intValue).sum();
    }

    /**
     * HikariCP의 현재 활성 커넥션 수를 조회
     */
    private HikariPoolMXBean getHikariPool() {
        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        return hikariDataSource.getHikariPoolMXBean();
    }

    /**
     * 하나의 서비스 호출에 대해 3가지 지표를 모두 측정
     */
    private MeasureResult measure(Runnable serviceCall) {
        HikariPoolMXBean pool = getHikariPool();

        // 1) 쿼리 카운트 준비
        RequestContext ctx = startQueryCount();

        // 2) 커넥션 점유 시작 시점의 활성 커넥션 수 기록
        int activeConnectionsBefore = pool.getActiveConnections();

        // 3) 시간 측정 시작
        long startNano = System.nanoTime();

        // 실행
        serviceCall.run();

        // 4) 시간 측정 종료
        long elapsedNano = System.nanoTime() - startNano;
        long elapsedMs = elapsedNano / 1_000_000;

        // 5) 활성 커넥션 수 확인
        int activeConnectionsAfter = pool.getActiveConnections();

        // 6) 쿼리 카운트 종료
        RequestContextHolder.clear();

        int totalQueries = getTotalQueryCount(ctx);

        // 커넥션 점유 시간 = 전체 응답 시간 (트랜잭션 범위 내에서 커넥션을 잡고 있으므로)
        // 쿼리가 많을수록 커넥션을 오래 점유한다는 것을 증명하기 위해 응답 시간을 커넥션 점유 시간으로 간주
        return new MeasureResult(totalQueries, elapsedMs, elapsedMs, ctx.getQueryCountByType(), ctx.getQueryCountByTable());
    }

    // ==================== 개선 전: 쿼리 수 + 응답 시간 + 커넥션 점유 ====================

    @Test
    @Order(1)
    @DisplayName("[개선 전] 쿼리 수 / 응답 시간 / 커넥션 점유 시간")
    void before_allMetrics() {
        int warmUp = 3;
        int iterations = 10;

        log.info("\n" + "=".repeat(60));
        log.info("[개선 전] CartService.getCartByMemberId()");
        log.info("조건: 상품 10개, 각 옵션 3개 (총 30개)");
        log.info("워밍업 {}회, 측정 {}회", warmUp, iterations);
        log.info("=".repeat(60));

        HikariPoolMXBean pool = getHikariPool();
        log.info("\n  HikariCP: 총 {} / 유휴 {} / 활성 {}",
                pool.getTotalConnections(), pool.getIdleConnections(), pool.getActiveConnections());

        // 워밍업
        for (int i = 0; i < warmUp; i++) {
            createTestCartData();
            entityManager.clear();
            cartService.getCartByMemberId(testMemberId);
            entityManager.clear();
        }

        // 측정
        long[] times = new long[iterations];
        long[] holdTimes = new long[iterations];
        int[] queries = new int[iterations];
        Map<QueryType, Integer> firstQueryDetail = null;
        Map<String, Integer> firstQueryByTable = null;

        for (int i = 0; i < iterations; i++) {
            createTestCartData();
            entityManager.clear();

            MeasureResult result = measure(() -> cartService.getCartByMemberId(testMemberId));
            times[i] = result.responseTimeMs();
            queries[i] = result.queryCount();
            if (i == 0) {
                firstQueryDetail = result.queryDetail();
                firstQueryByTable = result.queryByTable();
            }

            log.info("  {}번째: {}ms | 쿼리 {}회 | 커넥션 점유 {}ms",
                    i + 1, times[i], queries[i], holdTimes[i]);
            entityManager.clear();
        }

        int avgQuery = (int) average(queries);
        long avgTime = average(times);
        long avgHold = average(holdTimes);

        // 결과 출력
        log.info("\n" + "-".repeat(60));
        log.info("[개선 전] CartService.getCartByMemberId()");
        log.info("  1. 쿼리 실행 횟수");
        if (firstQueryDetail != null) {
            firstQueryDetail.forEach((type, count) ->
                    log.info("       {} : {}회", type, count));
        }
        log.info("       평균 총 쿼리 수: {}회", avgQuery);
        log.info("");
        log.info("  1-1. 테이블별 쿼리 상세");
        if (firstQueryByTable != null) {
            firstQueryByTable.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .forEach(e -> log.info("       {}: {}회",
                            String.format("%-15s", e.getKey()), e.getValue()));
        }
        log.info("");
        log.info("  2. 응답 시간");
        log.info("       평균: {}ms", avgTime);
        log.info("       최소: {}ms / 최대: {}ms", min(times), max(times));
        log.info("       P95: {}ms", percentile(times, 95));
        log.info("");
        log.info("  3. DB 커넥션 점유 시간");
        log.info("       평균 커넥션 점유: {}ms", avgHold);
        log.info("       쿼리 1회당 점유: {}ms",
                avgQuery > 0 ? String.format("%.2f", (double) avgHold / avgQuery) : "0");
        log.info("       커넥션 1개당 초당 처리: {} 요청",
                avgHold > 0 ? String.format("%.1f", 1000.0 / avgHold) : "0");
        log.info("=".repeat(60) + "\n");

        // 검증
        assertThat(avgQuery)
                .as("N+1 문제로 쿼리가 10회 이상 실행되어야 함")
                .isGreaterThan(10);
    }

    // ==================== 개선 후: 쿼리 수 + 응답 시간 + 커넥션 점유 ====================

    @Test
    @Order(2)
    @DisplayName("[개선 후] 쿼리 수 / 응답 시간 / 커넥션 점유 시간")
    void after_allMetrics() {
        int warmUp = 3;
        int iterations = 10;

        log.info("\n" + "=".repeat(60));
        log.info("[개선 후] CartServiceImprove.getCartByMemberId()");
        log.info("조건: 상품 10개, 각 옵션 3개 (총 30개)");
        log.info("워밍업 {}회, 측정 {}회", warmUp, iterations);
        log.info("=".repeat(60));

        HikariPoolMXBean pool = getHikariPool();
        log.info("\n  HikariCP: 총 {} / 유휴 {} / 활성 {}",
                pool.getTotalConnections(), pool.getIdleConnections(), pool.getActiveConnections());

        // 워밍업
        for (int i = 0; i < warmUp; i++) {
            createTestCartData();
            entityManager.clear();
            improvedCartService.getCartByMemberId(testMemberId);
            entityManager.clear();
        }

        // 측정
        long[] times = new long[iterations];
        long[] holdTimes = new long[iterations];
        int[] queries = new int[iterations];
        Map<QueryType, Integer> firstQueryDetail = null;
        Map<String, Integer> firstQueryByTable = null;

        for (int i = 0; i < iterations; i++) {
            createTestCartData();
            entityManager.clear();

            MeasureResult result = measure(() -> improvedCartService.getCartByMemberId(testMemberId));
            times[i] = result.responseTimeMs();
            holdTimes[i] = result.connectionHoldTimeMs();
            queries[i] = result.queryCount();
            if (i == 0) {
                firstQueryDetail = result.queryDetail();
                firstQueryByTable = result.queryByTable();
            }

            log.info("  {}번째: {}ms | 쿼리 {}회 | 커넥션 점유 {}ms",
                    i + 1, times[i], queries[i], holdTimes[i]);
            entityManager.clear();
        }

        int avgQuery = (int) average(queries);
        long avgTime = average(times);
        long avgHold = average(holdTimes);

        // 결과 출력
        log.info("\n" + "-".repeat(60));
        log.info("[개선 후] CartServiceImprove.getCartByMemberId()");
        log.info("  1. 쿼리 실행 횟수");
        if (firstQueryDetail != null) {
            firstQueryDetail.forEach((type, count) ->
                    log.info("       {} : {}회", type, count));
        }
        log.info("       평균 총 쿼리 수: {}회", avgQuery);
        log.info("");
        log.info("  1-1. 테이블별 쿼리 상세");
        if (firstQueryByTable != null) {
            firstQueryByTable.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .forEach(e -> log.info("       {}: {}회",
                            String.format("%-15s", e.getKey()), e.getValue()));
        }

        log.info("");
        log.info("  2. 응답 시간");
        log.info("       평균: {}ms", avgTime);
        log.info("       최소: {}ms / 최대: {}ms", min(times), max(times));
        log.info("       P95: {}ms", percentile(times, 95));

        log.info("");
        log.info("  3. DB 커넥션 점유 시간");
        log.info("       평균 커넥션 점유: {}ms", avgHold);
        log.info("       쿼리 1회당 점유: {}ms",
                avgQuery > 0 ? String.format("%.2f", (double) avgHold / avgQuery) : "0");
        log.info("       커넥션 1개당 초당 처리: {} 요청",
                avgHold > 0 ? String.format("%.1f", 1000.0 / avgHold) : "0");
        log.info("=".repeat(60) + "\n");

        // 검증
        assertThat(avgQuery)
                .as("JOIN FETCH로 쿼리가 5회 이하여야 함")
                .isLessThanOrEqualTo(5);
    }

    // ==================== 통계 유틸 메서드 ====================

    private long average(long[] values) {
        long sum = 0;
        for (long v : values) sum += v;
        return values.length > 0 ? sum / values.length : 0;
    }

    private long average(int[] values) {
        long sum = 0;
        for (int v : values) sum += v;
        return values.length > 0 ? sum / values.length : 0;
    }

    private long min(long[] values) {
        long min = Long.MAX_VALUE;
        for (long v : values) if (v < min) min = v;
        return min;
    }

    private long max(long[] values) {
        long max = Long.MIN_VALUE;
        for (long v : values) if (v > max) max = v;
        return max;
    }

    private long percentile(long[] values, int p) {
        long[] sorted = values.clone();
        java.util.Arrays.sort(sorted);
        int index = (int) Math.ceil(p / 100.0 * sorted.length) - 1;
        return sorted[Math.max(0, Math.min(index, sorted.length - 1))];
    }
}
