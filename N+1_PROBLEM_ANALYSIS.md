# N+1 쿼리 문제 분석 보고서

## 프로젝트 개요
**프로젝트명:** Codepresso Cafe Platform
**분석일자:** 2025-11-12
**JPA 버전:** Spring Data JPA

---

## 목차
1. [N+1 문제란?](#n1-문제란)
2. [심각도별 문제 분류](#심각도별-문제-분류)
3. [엔티티별 상세 분석](#엔티티별-상세-분석)
4. [해결 방안](#해결-방안)
5. [권장사항](#권장사항)

---

## N+1 문제란?

N+1 쿼리 문제는 JPA에서 연관관계가 있는 엔티티를 조회할 때 발생하는 성능 문제입니다.

### 발생 원리
```
1개의 쿼리로 N개의 데이터를 조회
↓
각 데이터의 연관 엔티티를 조회하기 위해 N개의 추가 쿼리 실행
↓
총 1 + N개의 쿼리 실행 = N+1 문제
```

### 예시
```java
// 1개의 쿼리로 10개의 주문 조회
List<Orders> orders = orderRepository.findAll();

// 각 주문의 상세정보 접근 시 10개의 추가 쿼리 발생
for (Orders order : orders) {
    order.getOrdersDetails().size(); // N번의 SELECT 발생
}
```

---

## 심각도별 문제 분류

### 🔴 HIGH (긴급 수정 필요)

#### 1. OrderService.getOrderDetail() - 중첩 컬렉션 접근
**위치:** `src/main/java/com/codepresso/codepresso/order/service/OrderService.java:147-169`

**문제 코드:**
```java
for (OrdersItemOptions option : detail.getOptions()) {
    String optionStyle = option.getOption().getOptionStyle().getOptionStyle();
    Integer extraPrice = option.getOption().getOptionStyle().getExtraPrice();
    // ...
}
```

**발생 쿼리 패턴:**
```sql
-- 1. 주문 조회 (1개)
SELECT * FROM orders WHERE id = ?

-- 2. 주문 상세 조회 (ordersDetails가 N개라면 N개 쿼리 또는 BatchSize로 최적화)
SELECT * FROM orders_detail WHERE orders_id = ?

-- 3. 각 주문상세의 옵션 조회 (M개)
SELECT * FROM orders_item_options WHERE orders_detail_id = ?

-- 4. 각 옵션의 ProductOption 조회 (M*K개)
SELECT * FROM product_option WHERE id = ?

-- 5. 각 ProductOption의 OptionStyle 조회 (M*K개)
SELECT * FROM option_style WHERE id = ?
```

**영향도:** 주문 상세 조회 시 수십~수백 개의 추가 쿼리 발생 가능

**현재 Repository:**
```java
// OrdersRepository.java
@Query("SELECT DISTINCT o FROM Orders o " +
    "LEFT JOIN FETCH o.branch " +
    "LEFT JOIN FETCH o.member " +
    "LEFT JOIN FETCH o.ordersDetails od " +
    "LEFT JOIN FETCH od.product " +
    "WHERE o.id = :orderId")
Optional<Orders> findByIdWithDetails(@Param("orderId") Long orderId);
```

**문제:** OrdersDetail의 options, 그리고 option의 productOption, optionStyle까지 FETCH하지 않음

---

### 🟡 MEDIUM (개선 권장)

#### 2. CartService.getCartByMemberId() - 옵션 스타일 접근
**위치:** `src/main/java/com/codepresso/codepresso/cart/service/CartService.java:195-232`

**문제 코드:**
```java
List<CartItemResponse> itemResponses = cart.getItems().stream()
    .map(item -> {
        List<CartOptionResponse> optionResponses = item.getOptions().stream()
            .filter(co -> co.getProductOption() != null
                    && co.getProductOption().getOptionStyle() != null
                    && co.getProductOption().getOptionStyle().getOptionStyle() != null)
            .map(cartOption -> CartOptionResponse.builder()
                .optionStyle(cartOption.getProductOption().getOptionStyle().getOptionStyle())
                // ...
```

**현재 상태:**
- `CartItemRepository.findByCart_IdAndProduct_Id()`가 `@EntityGraph`로 options와 productOption을 로드
- 하지만 `productOption.optionStyle.optionStyle` 접근 시 추가 쿼리 발생 가능

**위험도:** MEDIUM (EntityGraph가 optionStyle까지 포함하는지 확인 필요)

---

#### 3. ProductRepository.findProductById() - 제한적인 EntityGraph
**위치:** `src/main/java/com/codepresso/codepresso/product/repository/ProductRepository.java:29-34`

**현재 코드:**
```java
@EntityGraph(attributePaths = {
    "nutritionInfo",
    "category",
    "hashtags"
})
Product findProductById(@Param("id") Long id);
```

**문제:**
- Product의 `options` 컬렉션이 EntityGraph에 포함되지 않음
- Product의 `allergens` 컬렉션도 포함되지 않음
- ProductDetailResponse 생성 시 추가 쿼리 발생 가능

**영향도:**
- 상품 상세 조회 시 옵션 개수만큼 쿼리 추가 발생
- 알러지 정보 조회 시 추가 쿼리 발생

---

#### 4. Category 엔티티 - 자기 참조 관계
**위치:** `src/main/java/com/codepresso/codepresso/product/entity/Category.java`

**문제 관계:**
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "parent_category_id")
private Category parentCategory;

@OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
private List<Category> childCategories = new ArrayList<>();
```

**위험 시나리오:**
```java
// 카테고리 목록 조회
List<Category> categories = categoryRepository.findAll();

// 각 카테고리의 부모/자식 접근 시 N+1 발생
for (Category category : categories) {
    category.getParentCategory().getName(); // N개 쿼리
    category.getChildCategories().size();   // N개 쿼리
}
```

**현재 상태:** CategoryRepository에 명시적인 FETCH JOIN 메서드 없음

---

#### 5. FavoriteRepository - 기본 조회 메서드
**위치:** `src/main/java/com/codepresso/codepresso/member/repository/FavoriteRepository.java`

**잠재적 문제 메서드:**
```java
List<Favorite> findByMemberIdOrderByOrderbyAsc(Long memberId);
```

**문제:**
- Product를 FETCH JOIN하지 않음
- Favorite 목록 조회 후 각 Favorite의 Product 정보 접근 시 N+1 발생

**현재 해결책:**
```java
// 서비스에서 사용하는 최적화된 메서드
@Query("SELECT f, p FROM Favorite f JOIN f.product p ...")
List<Object[]> findFavoritesWithProductByMemberId(@Param("memberId") Long memberId);
```

**상태:** FavoriteService가 최적화된 메서드 사용 중 ✅

---

### 🟢 LOW (모니터링 필요)

#### 6. Review 엔티티
**위치:** `src/main/java/com/codepresso/codepresso/review/ReviewRepository.java`

**현재 쿼리:**
```java
@Query("SELECT r FROM Review r LEFT JOIN FETCH r.ordersDetail od WHERE od.product.id = :productId")
List<Review> findByProductReviews(@Param("productId") Long productId);
```

**상태:** ordersDetail을 FETCH하지만, Member나 다른 연관 엔티티 접근 시 추가 쿼리 가능
**위험도:** LOW (현재 사용 패턴에서는 문제 없음)

---

## 엔티티별 상세 분석

### 1. Product 엔티티
**파일:** `src/main/java/com/codepresso/codepresso/product/entity/Product.java`

**연관 관계:**<

| 관계 | 대상 엔티티 | Fetch 타입 | BatchSize | 상태 |
|------|------------|-----------|-----------|------|
| @OneToMany | ProductOption | LAZY | ❌ | ⚠️ N+1 위험 |
| @OneToOne | NutritionInfo | LAZY | ❌ | ✅ EntityGraph 포함 |
| @OneToMany | Allergen | LAZY | ❌ | ⚠️ EntityGraph 미포함 |
| @ManyToOne | Category | LAZY | ❌ | ✅ EntityGraph 포함 |
| @OneToMany | Favorite | LAZY | ❌ | ✅ 별도 조회 |
| @OneToMany | Hashtag | LAZY | ❌ | ✅ EntityGraph 포함 |

**사용 위치:**
- ProductService (findByProductId, findProductsByCategory, searchProductsByKeyword)
- CartService (addItemWithOptions, getCartByMemberId)
- OrderService (getOrderDetail, convertToOrderItem)

**권장사항:**
```java
@EntityGraph(attributePaths = {
    "nutritionInfo",
    "category",
    "hashtags",
    "options",              // 추가
    "options.optionStyle",  // 추가
    "allergens"             // 추가
})
Product findProductById(@Param("id") Long id);
```

---

### 2. Orders 엔티티
**파일:** `src/main/java/com/codepresso/codepresso/order/entity/Orders.java`

**연관 관계:**
| 관계 | 대상 엔티티 | Fetch 타입 | BatchSize | 상태 |
|------|------------|-----------|-----------|------|
| @ManyToOne | Branch | LAZY | ❌ | ✅ FETCH JOIN |
| @ManyToOne | Member | LAZY | ❌ | ✅ FETCH JOIN |
| @OneToMany | OrdersDetail | DEFAULT | ✅ 100 | ⚠️ options 미포함 |

**문제 쿼리:**
```java
// 현재 (문제)
@Query("SELECT DISTINCT o FROM Orders o " +
    "LEFT JOIN FETCH o.branch " +
    "LEFT JOIN FETCH o.member " +
    "LEFT JOIN FETCH o.ordersDetails od " +
    "LEFT JOIN FETCH od.product " +
    "WHERE o.id = :orderId")
```

**개선안:**
```java
// 권장 (해결)
@Query("SELECT DISTINCT o FROM Orders o " +
    "LEFT JOIN FETCH o.branch " +
    "LEFT JOIN FETCH o.member " +
    "LEFT JOIN FETCH o.ordersDetails od " +
    "LEFT JOIN FETCH od.product " +
    "LEFT JOIN FETCH od.options odo " +
    "LEFT JOIN FETCH odo.option po " +
    "LEFT JOIN FETCH po.optionStyle os " +
    "WHERE o.id = :orderId")
```

---

### 3. Cart 엔티티
**파일:** `src/main/java/com/codepresso/codepresso/cart/entity/Cart.java`

**연관 관계:**
| 관계 | 대상 엔티티 | Fetch 타입 | BatchSize | 상태 |
|------|------------|-----------|-----------|------|
| @OneToOne | Member | LAZY | ❌ | ✅ |
| @OneToMany | CartItem | DEFAULT | ✅ 100 | ✅ FETCH JOIN 사용 |

**현재 최적화된 쿼리:**
```java
@Query("SELECT c FROM Cart c " +
    "LEFT JOIN FETCH c.items ci " +
    "LEFT JOIN FETCH ci.product " +
    "WHERE c.member.id = :memberId")
Optional<Cart> findByMemberIdWithItems(@Param("memberId") Long memberId);
```

**상태:** ✅ 양호 (CartService가 최적화된 메서드 사용)

---

### 4. CartItem 엔티티
**파일:** `src/main/java/com/codepresso/codepresso/cart/entity/CartItem.java`

**연관 관계:**
| 관계 | 대상 엔티티 | Fetch 타입 | BatchSize | 상태 |
|------|------------|-----------|-----------|------|
| @ManyToOne | Cart | LAZY | ❌ | ✅ |
| @ManyToOne | Product | LAZY | ❌ | ✅ EntityGraph |
| @OneToMany | CartOption | DEFAULT | ✅ 100 | ✅ EntityGraph |

**현재 EntityGraph:**
```java
@EntityGraph(attributePaths = {"options", "options.productOption", "product"})
List<CartItem> findByCart_IdAndProduct_Id(Long cartId, Long productId);
```

**추가 확인 필요:**
- `options.productOption.optionStyle`까지 포함되는지 확인

---

### 5. OrdersDetail 엔티티
**파일:** `src/main/java/com/codepresso/codepresso/order/entity/OrdersDetail.java`

**연관 관계:**
| 관계 | 대상 엔티티 | Fetch 타입 | BatchSize | 상태 |
|------|------------|-----------|-----------|------|
| @ManyToOne | Orders | LAZY | ❌ | ✅ |
| @ManyToOne | Product | LAZY | ❌ | ✅ FETCH JOIN |
| @OneToMany | OrdersItemOptions | DEFAULT | ✅ 100 | 🔴 FETCH 누락 |

**문제점:**
- OrdersItemOptions를 FETCH하지 않음
- 각 OrdersDetail의 options 접근 시 BatchSize(100)로 최적화되지만 완전하지 않음
- options 내부의 ProductOption, OptionStyle 접근 시 추가 쿼리 발생

---

### 6. Favorite 엔티티
**파일:** `src/main/java/com/codepresso/codepresso/member/entity/Favorite.java`

**연관 관계:**
| 관계 | 대상 엔티티 | Fetch 타입 | 상태 |
|------|------------|-----------|------|
| @ManyToOne | Member | LAZY | ✅ |
| @ManyToOne | Product | LAZY | ⚠️ JOIN 필요 |

**최적화된 쿼리:**
```java
@Query("SELECT f, p FROM Favorite f JOIN f.product p WHERE f.member.id = :memberId ORDER BY f.orderby ASC")
List<Object[]> findFavoritesWithProductByMemberId(@Param("memberId") Long memberId);
```

**상태:** ✅ FavoriteService에서 적절히 사용 중

---

### 7. Category 엔티티 (자기 참조)
**파일:** `src/main/java/com/codepresso/codepresso/product/entity/Category.java`

**연관 관계:**
| 관계 | 대상 엔티티 | Fetch 타입 | 상태 |
|------|------------|-----------|------|
| @ManyToOne | Category (parent) | LAZY | ⚠️ N+1 위험 |
| @OneToMany | Category (children) | DEFAULT | ⚠️ N+1 위험 |
| @OneToMany | Product | LAZY | ✅ |

**권장 쿼리:**
```java
// 부모 카테고리와 함께 조회
@Query("SELECT c FROM Category c LEFT JOIN FETCH c.parentCategory WHERE c.id = :id")
Optional<Category> findByIdWithParent(@Param("id") Long id);

// 자식 카테고리와 함께 조회
@Query("SELECT c FROM Category c LEFT JOIN FETCH c.childCategories WHERE c.id = :id")
Optional<Category> findByIdWithChildren(@Param("id") Long id);
```

---

## BatchSize 설정 현황

### 현재 BatchSize 설정

| 엔티티 | 컬렉션 | BatchSize | 평가 |
|--------|--------|-----------|------|
| Cart | items | 100 | ✅ 적절 |
| CartItem | options | 100 | ✅ 적절 |
| Orders | ordersDetails | 100 | ✅ 적절 |
| OrdersDetail | options | 100 | ✅ 적절 |
| Branch | orders | 10 | ⚠️ 낮음 |

### BatchSize의 역할

**BatchSize가 있을 때:**
```sql
-- N+1 대신 IN 절로 묶어서 조회
SELECT * FROM cart_item WHERE cart_id IN (?, ?, ?, ... 100개)
```

**BatchSize가 없을 때:**
```sql
-- N번 개별 조회
SELECT * FROM cart_item WHERE cart_id = ?
SELECT * FROM cart_item WHERE cart_id = ?
... (N번 반복)
```

**권장사항:**
- BatchSize는 N+1을 완전히 해결하지 못함 (쿼리 수를 줄일 뿐)
- FETCH JOIN과 함께 사용하는 것이 가장 효과적
- Branch.orders의 BatchSize를 50~100으로 증가 권장

---

## 해결 방안

### 방법 1: FETCH JOIN (권장)

**장점:**
- 1개의 쿼리로 모든 데이터 조회
- 가장 효율적인 방법

**단점:**
- 페이징과 함께 사용 시 주의 필요
- MultipleBagFetchException 발생 가능 (여러 컬렉션 FETCH 시)

**적용 예시:**
```java
// OrdersRepository.java
@Query("SELECT DISTINCT o FROM Orders o " +
    "LEFT JOIN FETCH o.branch " +
    "LEFT JOIN FETCH o.member " +
    "LEFT JOIN FETCH o.ordersDetails od " +
    "LEFT JOIN FETCH od.product " +
    "LEFT JOIN FETCH od.options odo " +
    "LEFT JOIN FETCH odo.option po " +
    "LEFT JOIN FETCH po.optionStyle " +
    "WHERE o.id = :orderId")
Optional<Orders> findByIdWithDetails(@Param("orderId") Long orderId);
```

---

### 방법 2: @EntityGraph

**장점:**
- 간결한 코드
- 동적으로 FETCH 전략 변경 가능

**단점:**
- 복잡한 중첩 관계 처리 제한적
- LEFT JOIN만 사용 (INNER JOIN 불가)

**적용 예시:**
```java
// ProductRepository.java
@EntityGraph(attributePaths = {
    "nutritionInfo",
    "category",
    "hashtags",
    "options",
    "options.optionStyle",
    "allergens"
})
Product findProductById(@Param("id") Long id);
```

---

### 방법 3: @BatchSize

**장점:**
- 컬렉션 조회를 IN 절로 묶어서 최적화
- 간단한 설정

**단점:**
- N+1을 완전히 해결하지 못함 (N+1 → N/BatchSize+1)
- 여전히 추가 쿼리 발생

**적용 예시:**
```java
@Entity
public class Cart {
    @OneToMany(mappedBy = "cart")
    @BatchSize(size = 100)  // 100개씩 묶어서 조회
    private List<CartItem> items = new ArrayList<>();
}
```

---

### 방법 4: DTO Projection

**장점:**
- 필요한 필드만 조회 (메모리 효율적)
- N+1 발생하지 않음

**단점:**
- Entity가 아닌 DTO 반환
- 영속성 컨텍스트 관리 불가

**적용 예시:**
```java
// 이미 사용 중 (OrdersRepository.java)
@Query("SELECT new com.codepresso.codepresso.order.dto.OrderSummaryProjection(" +
    "o.id, o.orderDate, o.totalPrice, b.branchName) " +
    "FROM Orders o " +
    "JOIN o.branch b " +
    "WHERE o.member.id = :memberId")
Page<OrderSummaryProjection> findByMemberIdWithPaging2(@Param("memberId") Long memberId, Pageable pageable);
```

**상태:** ✅ 목록 조회에 적절히 사용 중

---

### 방법 5: @Fetch(FetchMode.SUBSELECT)

**장점:**
- 2개의 쿼리로 모든 데이터 조회
- 페이징과 호환

**단점:**
- 2번의 쿼리 발생
- FETCH JOIN보다 비효율적

**적용 예시:**
```java
@Entity
public class Orders {
    @OneToMany(mappedBy = "orders")
    @Fetch(FetchMode.SUBSELECT)
    private List<OrdersDetail> ordersDetails = new ArrayList<>();
}
```

---

## 권장사항

### 1. 즉시 수정 필요 (HIGH)

#### OrdersRepository.findByIdWithDetails() 개선
**파일:** `src/main/java/com/codepresso/codepresso/order/repository/OrdersRepository.java`

```java
// 현재 (문제)
@Query("SELECT DISTINCT o FROM Orders o " +
    "LEFT JOIN FETCH o.branch " +
    "LEFT JOIN FETCH o.member " +
    "LEFT JOIN FETCH o.ordersDetails od " +
    "LEFT JOIN FETCH od.product " +
    "WHERE o.id = :orderId")
Optional<Orders> findByIdWithDetails(@Param("orderId") Long orderId);

// 개선안 (권장)
@Query("SELECT DISTINCT o FROM Orders o " +
    "LEFT JOIN FETCH o.branch " +
    "LEFT JOIN FETCH o.member " +
    "LEFT JOIN FETCH o.ordersDetails od " +
    "LEFT JOIN FETCH od.product " +
    "LEFT JOIN FETCH od.options odo " +
    "LEFT JOIN FETCH odo.option po " +
    "LEFT JOIN FETCH po.optionStyle " +
    "WHERE o.id = :orderId")
Optional<Orders> findByIdWithDetails(@Param("orderId") Long orderId);
```

**예상 효과:**
- 주문 1건 조회 시 수십~수백 개의 쿼리 → 1개의 쿼리로 감소
- 응답 시간 대폭 개선

---

### 2. 개선 권장 (MEDIUM)

#### 2-1. ProductRepository.findProductById() 확장
**파일:** `src/main/java/com/codepresso/codepresso/product/repository/ProductRepository.java`

```java
// 현재
@EntityGraph(attributePaths = {
    "nutritionInfo",
    "category",
    "hashtags"
})
Product findProductById(@Param("id") Long id);

// 개선안
@EntityGraph(attributePaths = {
    "nutritionInfo",
    "category",
    "hashtags",
    "options",              // 추가
    "options.optionStyle",  // 추가
    "allergens"             // 추가
})
Product findProductById(@Param("id") Long id);
```

#### 2-2. CategoryRepository 메서드 추가
**파일:** `src/main/java/com/codepresso/codepresso/product/repository/CategoryRepository.java`

```java
// 새로운 메서드 추가
@Query("SELECT c FROM Category c LEFT JOIN FETCH c.parentCategory WHERE c.id = :id")
Optional<Category> findByIdWithParent(@Param("id") Long id);

@Query("SELECT c FROM Category c LEFT JOIN FETCH c.childCategories WHERE c.id = :id")
Optional<Category> findByIdWithChildren(@Param("id") Long id);
```

#### 2-3. CartItemRepository EntityGraph 확인
**파일:** `src/main/java/com/codepresso/codepresso/cart/repository/CartItemRepository.java`

```java
// 현재 EntityGraph에 optionStyle.optionStyle 경로 포함 여부 확인
// 필요시 명시적 FETCH JOIN으로 변경
@Query("SELECT ci FROM CartItem ci " +
    "LEFT JOIN FETCH ci.options co " +
    "LEFT JOIN FETCH co.productOption po " +
    "LEFT JOIN FETCH po.optionStyle " +
    "LEFT JOIN FETCH ci.product " +
    "WHERE ci.cart.id = :cartId AND ci.product.id = :productId")
List<CartItem> findByCart_IdAndProduct_IdWithAllOptions(
    @Param("cartId") Long cartId,
    @Param("productId") Long productId
);
```

---

### 3. 모니터링 및 검증

#### 3-1. SQL 로깅 활성화
**파일:** `src/main/resources/application.yml` (또는 application.properties)

```yaml
spring:
  jpa:
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
        show_sql: false  # 로거로만 출력
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
    org.hibernate.stat: DEBUG
```

#### 3-2. 쿼리 카운터 추가
```java
// 테스트 또는 개발 환경에서 사용
@Component
public class QueryCounterInterceptor extends EmptyInterceptor {
    private ThreadLocal<Long> queryCount = ThreadLocal.withInitial(() -> 0L);

    @Override
    public String onPrepareStatement(String sql) {
        queryCount.set(queryCount.get() + 1);
        return super.onPrepareStatement(sql);
    }

    public Long getCount() {
        return queryCount.get();
    }

    public void clear() {
        queryCount.set(0L);
    }
}
```

#### 3-3. 통합 테스트 작성
```java
@Test
@DisplayName("주문 상세 조회 시 N+1 문제 발생하지 않는지 검증")
void testOrderDetailWithoutNPlusOne() {
    // Given
    Long orderId = 1L;

    // When
    QueryCounterInterceptor.clear();
    OrderDetailResponse response = orderService.getOrderDetail(orderId, memberId);
    Long queryCount = QueryCounterInterceptor.getCount();

    // Then
    assertThat(queryCount).isLessThanOrEqualTo(1); // 1개의 쿼리만 실행되어야 함
    assertThat(response.getOrderDetails()).isNotEmpty();
}
```

---

### 4. 장기 개선 과제

#### 4-1. 글로벌 BatchSize 설정
**파일:** `src/main/resources/application.yml`

```yaml
spring:
  jpa:
    properties:
      hibernate:
        default_batch_fetch_size: 100  # 글로벌 배치 사이즈
```

#### 4-2. 쿼리 최적화 체크리스트

**새로운 Repository 메서드 작성 시 확인사항:**
- [ ] 연관 엔티티 접근이 있는가?
- [ ] FETCH JOIN 또는 EntityGraph 사용했는가?
- [ ] 페이징 사용 시 컬렉션 FETCH는 피했는가?
- [ ] BatchSize 설정이 필요한가?
- [ ] DTO Projection이 더 적합한가?

#### 4-3. 성능 모니터링 도구 도입

**추천 도구:**
- **Spring Boot Actuator + Micrometer:** JPA 쿼리 메트릭 수집
- **p6spy:** 쿼리 로깅 및 파라미터 바인딩 확인
- **Hibernate Statistics:** 쿼리 통계 분석

**p6spy 설정 예시:**
```yaml
# application.yml
decorator:
  datasource:
    p6spy:
      enable-logging: true
      multiline: true
      logging: slf4j
```

---

## 엔티티 요약 테이블

| 엔티티 | 연관관계 수 | LAZY 사용 | 위험도 | 상태 | 비고 |
|--------|------------|----------|--------|------|------|
| Product | 6 | ✅ | 🟡 MEDIUM | ⚠️ | EntityGraph 확장 필요 |
| Orders | 3 | ✅ | 🔴 HIGH | ⚠️ | options FETCH 누락 |
| OrdersDetail | 3 | ✅ | 🔴 HIGH | ⚠️ | 중첩 컬렉션 미처리 |
| OrdersItemOptions | 2 | ✅ | 🟡 MEDIUM | ⚠️ | 깊은 중첩 접근 |
| Cart | 2 | ✅ | 🟢 LOW | ✅ | FETCH JOIN 사용 |
| CartItem | 3 | ✅ | 🟡 MEDIUM | ⚠️ | EntityGraph 확인 필요 |
| CartOption | 2 | ✅ | 🟢 LOW | ✅ | |
| Favorite | 2 | ✅ | 🟢 LOW | ✅ | 최적화된 쿼리 사용 |
| Category | 3 (자기참조) | ✅ | 🟡 MEDIUM | ⚠️ | FETCH JOIN 추가 필요 |
| Review | 2 | ✅ | 🟢 LOW | ✅ | LEFT JOIN FETCH 사용 |
| ProductOption | 2 | ✅ | 🟡 MEDIUM | ⚠️ | 중첩 접근 주의 |
| OptionStyle | 2 | ✅ | 🟡 MEDIUM | ⚠️ | 깊은 중첩 |
| Branch | 1 | Default | 🟢 LOW | ✅ | BatchSize 증가 권장 |
| Member | 0 | - | 🟢 LOW | ✅ | |
| Payment | 2 | ✅ | 🟢 LOW | ✅ | |
| PaymentDetail | 2 | ✅ | 🟢 LOW | ✅ | |
| MemberCoupon | 2 | ✅ | 🟢 LOW | ✅ | |
| CouponType | 1 | Default | 🟢 LOW | ✅ | |
| Stamp | 1 | ✅ | 🟢 LOW | ✅ | |
| Allergen | 1 | ✅ | 🟢 LOW | ✅ | EntityGraph 포함 |
| Hashtag | 1 | ✅ | 🟢 LOW | ✅ | EntityGraph 포함 |
| NutritionInfo | 1 | ✅ | 🟢 LOW | ✅ | EntityGraph 포함 |

---

## 체크리스트

### 즉시 수정 (1주 이내)
- [ ] OrdersRepository.findByIdWithDetails() FETCH JOIN 추가
- [ ] OrderService.getOrderDetail() 테스트 및 검증
- [ ] SQL 로깅 활성화하여 쿼리 수 확인

### 단기 개선 (1개월 이내)
- [ ] ProductRepository.findProductById() EntityGraph 확장
- [ ] CategoryRepository FETCH JOIN 메서드 추가
- [ ] CartItemRepository EntityGraph 검증
- [ ] 통합 테스트 작성 (N+1 검증)

### 중기 개선 (3개월 이내)
- [ ] 글로벌 BatchSize 설정 적용
- [ ] p6spy 또는 쿼리 모니터링 도구 도입
- [ ] 성능 테스트 자동화
- [ ] 쿼리 최적화 가이드라인 문서화

### 장기 개선 (6개월 이내)
- [ ] 모든 Repository 메서드 N+1 검증
- [ ] 성능 모니터링 대시보드 구축
- [ ] 쿼리 성능 SLA 정의
- [ ] 정기적인 성능 리뷰 프로세스 수립

---

## 참고 자료

### JPA N+1 문제 해결 방법
1. **FETCH JOIN:** 가장 효과적, 1개의 쿼리로 해결
2. **@EntityGraph:** 간결한 코드, 동적 FETCH 전략
3. **@BatchSize:** 쿼리 수 감소 (완전한 해결은 아님)
4. **DTO Projection:** 필요한 데이터만 조회
5. **@Fetch(SUBSELECT):** 2개의 쿼리로 해결

### 페이징 + 컬렉션 FETCH 주의사항
```java
// ❌ 잘못된 예 (HHH000104 Warning 발생)
@Query("SELECT DISTINCT o FROM Orders o " +
    "LEFT JOIN FETCH o.ordersDetails " +
    "WHERE o.member.id = :memberId")
Page<Orders> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);

// ✅ 올바른 예 (DTO Projection 사용)
@Query("SELECT new OrderSummaryProjection(...) FROM Orders o ...")
Page<OrderSummaryProjection> findByMemberId(..., Pageable pageable);
```

### MultipleBagFetchException 회피
```java
// ❌ 2개 이상의 컬렉션 FETCH (에러 발생)
@Query("SELECT o FROM Orders o " +
    "LEFT JOIN FETCH o.ordersDetails " +
    "LEFT JOIN FETCH o.payments")

// ✅ 해결 방법 1: 쿼리 분리
@Query("SELECT o FROM Orders o LEFT JOIN FETCH o.ordersDetails WHERE o.id = :id")
Orders findWithDetails(@Param("id") Long id);

@Query("SELECT o FROM Orders o LEFT JOIN FETCH o.payments WHERE o.id = :id")
Orders findWithPayments(@Param("id") Long id);

// ✅ 해결 방법 2: @BatchSize 사용
@OneToMany(mappedBy = "orders")
@BatchSize(size = 100)
private List<OrdersDetail> ordersDetails;
```

---

## 문의 및 개선 제안
이 문서는 2025-11-12 기준으로 작성되었습니다.
추가적인 N+1 문제 발견 시 이 문서를 업데이트해주세요.