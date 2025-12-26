package com.codepresso.codepresso;

import com.codepresso.codepresso.monitoring.QueryCountInterceptor;
import com.codepresso.codepresso.monitoring.QueryType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * N+1 쿼리 문제 자동 감지 테스트
 *
 * <p>목적: 전체 API를 자동으로 호출하여 N+1 쿼리 문제를 감지하고 리포트 생성
 *
 * <p>실행 방법:
 * <ul>
 *   <li>IDE: 클래스 우클릭 → Run 'N1QueryDetectionTest'</li>
 *   <li>Maven: mvn test -Dtest=N1QueryDetectionTest</li>
 *   <li>Gradle: ./gradlew test --tests N1QueryDetectionTest</li>
 * </ul>
 *
 * <p>N+1 판단 기준: SELECT 쿼리가 {@value N_PLUS_1_THRESHOLD}개 초과 시 의심
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class N1QueryDetectionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QueryCountInterceptor queryCountInterceptor;

    /** N+1 의심 기준: SELECT 쿼리 수 */
    private static final int N_PLUS_1_THRESHOLD = 10;

    /** 전체 테스트 결과 수집 */
    private static final List<TestResult> testResults = new ArrayList<>();

    /**
     * 테스트 결과 저장 클래스
     */
    static class TestResult {
        String apiName;
        String method;
        String path;
        int selectCount;
        int totalQueryCount;
        boolean isN1Suspected;
        int responseStatus;
        String errorMessage;

        @Override
        public String toString() {
            String status = isN1Suspected ? "🔴 N+1 의심" : "✅ 정상";
            if (errorMessage != null) {
                return String.format("%s | %s %s | 에러: %s", status, method, path, errorMessage);
            }
            return String.format("%s | %s %s | SELECT: %d | 총 쿼리: %d | 상태: %d",
                    status, method, path, selectCount, totalQueryCount, responseStatus);
        }
    }

    @BeforeEach
    void setUp() {
        // 이전 테스트의 쿼리 카운트 초기화
        // 컨텍스트가 없으면 무시됨
        queryCountInterceptor.clearQueryCount();
    }

    @AfterAll
    static void printReport() {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("📊 N+1 쿼리 감지 테스트 결과 리포트");
        System.out.println("=".repeat(100));

        List<TestResult> suspectedApis = testResults.stream()
                .filter(r -> r.isN1Suspected)
                .toList();

        List<TestResult> normalApis = testResults.stream()
                .filter(r -> !r.isN1Suspected)
                .toList();

        // N+1 의심 API
        System.out.println("\n🔴 N+1 문제 의심 API (" + suspectedApis.size() + "개)");
        System.out.println("-".repeat(100));
        if (suspectedApis.isEmpty()) {
            System.out.println("✨ N+1 문제가 발견되지 않았습니다!");
        } else {
            suspectedApis.forEach(System.out::println);
        }

        // 정상 API
        System.out.println("\n✅ 정상 API (" + normalApis.size() + "개)");
        System.out.println("-".repeat(100));
        normalApis.forEach(System.out::println);

        // 통계
        System.out.println("\n📈 통계");
        System.out.println("-".repeat(100));
        System.out.printf("전체 API: %d개%n", testResults.size());
        System.out.printf("정상: %d개%n", normalApis.size());
        System.out.printf("N+1 의심: %d개%n", suspectedApis.size());
        System.out.printf("문제 비율: %.1f%%%n", (suspectedApis.size() * 100.0 / testResults.size()));
        System.out.println("=".repeat(100));
    }

    /**
     * 테스트 실행 헬퍼 메서드
     *
     * @param apiName    API 이름 (리포트용)
     * @param method     HTTP 메서드 (GET, POST, PUT, DELETE, PATCH)
     * @param path       API 경로
     * @param needsAuth  인증 필요 여부
     */
    private void executeTest(String apiName, String method, String path, boolean needsAuth) throws Exception {
        TestResult result = new TestResult();
        result.apiName = apiName;
        result.method = method;
        result.path = path;

        try {
            MockHttpServletRequestBuilder request = createRequest(method, path);

            // 인증이 필요한 경우 user 추가
            if (needsAuth) {
                request = request.with(user("user").password("asdf1234").roles("USER"));
            }

            MvcResult mvcResult = mockMvc.perform(request)
                    .andDo(print())
                    .andReturn();

            result.responseStatus = mvcResult.getResponse().getStatus();

            // 쿼리 카운트 수집
            Map<QueryType, Integer> queryCount = queryCountInterceptor.getQueryCount();
            result.selectCount = queryCount.getOrDefault(QueryType.SELECT, 0);
            result.totalQueryCount = queryCount.values().stream()
                    .mapToInt(Integer::intValue)
                    .sum();
            result.isN1Suspected = result.selectCount > N_PLUS_1_THRESHOLD;

        } catch (Exception e) {
            result.errorMessage = e.getMessage();
            result.responseStatus = 500;
        }

        testResults.add(result);

        // 실시간 결과 출력
        System.out.println("\n" + result);

        // N+1 의심되면 경고
        if (result.isN1Suspected) {
            System.err.println("⚠️  WARNING: N+1 쿼리 문제 의심! SELECT 쿼리가 "
                    + result.selectCount + "개 발생했습니다.");
        }

        // 다음 테스트를 위해 초기화
        queryCountInterceptor.clearQueryCount();
    }

    /**
     * HTTP 메서드에 따른 MockHttpServletRequestBuilder 생성
     */
    private MockHttpServletRequestBuilder createRequest(String method, String path) {
        return switch (method.toUpperCase()) {
            case "GET" -> get(path);
            case "POST" -> post(path).contentType(MediaType.APPLICATION_JSON);
            case "PUT" -> put(path).contentType(MediaType.APPLICATION_JSON);
            case "DELETE" -> delete(path);
            case "PATCH" -> patch(path).contentType(MediaType.APPLICATION_JSON);
            default -> throw new IllegalArgumentException("지원하지 않는 HTTP 메서드: " + method);
        };
    }

    // ==================== 인증 불필요 API (14개) ====================

    @Test
    @Order(1)
    @DisplayName("중복체크 - 아이디")
    void testCheckDuplicateId() throws Exception {
        executeTest("중복체크-아이디", "GET", "/api/auth/check?field=id&value=testuser", false);
    }

    @Test
    @Order(2)
    @DisplayName("중복체크 - 닉네임")
    void testCheckDuplicateNickname() throws Exception {
        executeTest("중복체크-닉네임", "GET", "/api/auth/check?field=nickname&value=testnick", false);
    }

    @Test
    @Order(3)
    @DisplayName("중복체크 - 이메일")
    void testCheckDuplicateEmail() throws Exception {
        executeTest("중복체크-이메일", "GET", "/api/auth/check?field=email&value=test@test.com", false);
    }

    @Test
    @Order(4)
    @DisplayName("게시판 타입 목록 조회")
    void testGetBoardTypes() throws Exception {
        executeTest("게시판타입목록", "GET", "/boards/types", false);
    }

    @Test
    @Order(5)
    @DisplayName("게시판 목록 조회")
    void testGetBoardList() throws Exception {
        executeTest("게시판목록조회", "GET", "/boards?boardTypeId=1&page=0&size=10", false);
    }

    @Test
    @Order(6)
    @DisplayName("게시판 상세 조회")
    void testGetBoardDetail() throws Exception {
        executeTest("게시판상세조회", "GET", "/boards/1", false);
    }

    @Test
    @Order(7)
    @DisplayName("게시판 댓글 목록")
    void testGetBoardComments() throws Exception {
        executeTest("게시판댓글목록", "GET", "/boards/1/comments", false);
    }

    @Test
    @Order(8)
    @DisplayName("지점 정보 조회 - 강남점")
    void testGetBranchInfo1() throws Exception {
        executeTest("지점정보조회-1", "GET", "/branch/info/1", false);
    }

    @Test
    @Order(9)
    @DisplayName("지점 정보 조회 - 홍대점")
    void testGetBranchInfo2() throws Exception {
        executeTest("지점정보조회-2", "GET", "/branch/info/2", false);
    }

    @Test
    @Order(10)
    @DisplayName("전체 상품 목록 조회")
    void testGetAllProducts() throws Exception {
        executeTest("전체상품목록", "GET", "/api/products", false);
    }

    @Test
    @Order(11)
    @DisplayName("상품 랜덤 추천")
    void testGetRandomProducts() throws Exception {
        executeTest("상품랜덤추천", "GET", "/api/products/random", false);
    }

    @Test
    @Order(12)
    @DisplayName("상품 리뷰 목록 - 상품1")
    void testGetProductReviews1() throws Exception {
        executeTest("상품리뷰목록-1", "GET", "/api/products/1/reviews", false);
    }

    @Test
    @Order(13)
    @DisplayName("상품 리뷰 목록 - 상품2")
    void testGetProductReviews2() throws Exception {
        executeTest("상품리뷰목록-2", "GET", "/api/products/2/reviews", false);
    }

    @Test
    @Order(14)
    @DisplayName("상품 검색 - 키워드")
    void testSearchProductsByKeyword() throws Exception {
        executeTest("상품검색-키워드", "POST", "/api/products/search/keyword?keyword=아메리카노", false);
    }

    // ==================== 인증 필요 API - 조회 (11개) ====================

    @Test
    @Order(100)
    @DisplayName("장바구니 조회")
    void testGetCart() throws Exception {
        executeTest("장바구니조회", "GET", "/users/cart", true);
    }

    @Test
    @Order(101)
    @DisplayName("장바구니 아이템 개수")
    void testGetCartItemCount() throws Exception {
        executeTest("장바구니개수", "GET", "/users/cart/count", true);
    }

    @Test
    @Order(102)
    @DisplayName("즐겨찾기 목록 조회")
    void testGetFavorites() throws Exception {
        executeTest("즐겨찾기목록", "GET", "/users/favorites", true);
    }

    @Test
    @Order(103)
    @DisplayName("내 정보 조회")
    void testGetMyInfo() throws Exception {
        executeTest("내정보조회", "GET", "/api/users/me", true);
    }

    @Test
    @Order(104)
    @DisplayName("쿠폰 개수 조회")
    void testGetCouponCount() throws Exception {
        executeTest("쿠폰개수조회", "GET", "/api/coupons/me/count", true);
    }

    @Test
    @Order(105)
    @DisplayName("내 쿠폰 목록 조회")
    void testGetMyCoupons() throws Exception {
        executeTest("내쿠폰목록", "GET", "/api/coupons/me", true);
    }

    @Test
    @Order(106)
    @DisplayName("스탬프 조회")
    void testGetStamp() throws Exception {
        executeTest("스탬프조회", "GET", "/api/stamp", true);
    }

    @Test
    @Order(107)
    @DisplayName("주문 목록 조회 - 1개월")
    void testGetOrders1Month() throws Exception {
        executeTest("주문목록-1개월", "GET", "/users/orders?period=1개월&page=0&size=20", true);
    }

    @Test
    @Order(108)
    @DisplayName("주문 목록 조회 - 3개월")
    void testGetOrders3Months() throws Exception {
        executeTest("주문목록-3개월", "GET", "/users/orders?period=3개월&page=0&size=20", true);
    }

    @Test
    @Order(109)
    @DisplayName("주문 목록 조회 - 전체 ⚠️ N+1 의심 대상")
    void testGetOrdersAll() throws Exception {
        executeTest("주문목록-전체", "GET", "/users/orders?period=전체&page=0&size=20", true);
    }

    @Test
    @Order(110)
    @DisplayName("주문 상세 조회")
    void testGetOrderDetail() throws Exception {
        executeTest("주문상세조회", "GET", "/users/orders/1", true);
    }

    // ==================== 인증 필요 API - CUD 작업 (5개) ====================

    @Test
    @Order(200)
    @DisplayName("게시판 글 작성")
    void testCreateBoard() throws Exception {
        String requestBody = """
                {
                    "title": "테스트 게시글",
                    "content": "N+1 테스트를 위한 자동 생성 게시글입니다.",
                    "boardTypeId": 1,
                    "statusTag": "PENDING"
                }
                """;

        mockMvc.perform(post("/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("user").roles("USER")))
                .andDo(print());

        executeTest("게시판글작성", "POST", "/boards", true);
    }

    @Test
    @Order(201)
    @DisplayName("즐겨찾기 추가")
    void testAddFavorite() throws Exception {
        String requestBody = """
                {
                    "productId": 5,
                    "orderby": 1
                }
                """;

        mockMvc.perform(post("/users/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("user").roles("USER")))
                .andDo(print());

        executeTest("즐겨찾기추가", "POST", "/users/favorites", true);
    }

    @Test
    @Order(202)
    @DisplayName("장바구니 상품 추가")
    void testAddCartItem() throws Exception {
        mockMvc.perform(post("/users/cart")
                        .param("productId", "1")
                        .param("quantity", "2")
                        .with(user("user").roles("USER")))
                .andDo(print());

        executeTest("장바구니추가", "POST", "/users/cart", true);
    }

    @Test
    @Order(300)
    @DisplayName("장바구니 수량 수정")
    void testUpdateCartItem() throws Exception {
        mockMvc.perform(patch("/users/cart/1")
                        .param("quantity", "3")
                        .with(user("user").roles("USER")))
                .andDo(print());

        executeTest("장바구니수량수정", "PATCH", "/users/cart/1?quantity=3", true);
    }

    @Test
    @Order(301)
    @DisplayName("즐겨찾기 삭제")
    void testDeleteFavorite() throws Exception {
        // 먼저 추가
        String requestBody = """
                {
                    "productId": 10,
                    "orderby": 1
                }
                """;

        mockMvc.perform(post("/users/favorites")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(user("user").roles("USER")));

        // 삭제 테스트
        executeTest("즐겨찾기삭제", "DELETE", "/users/favorites/10", true);
    }
}