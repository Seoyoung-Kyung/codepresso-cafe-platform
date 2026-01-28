# CodePresso Cafe Platform

카페 온라인 주문 및 관리 플랫폼 시스템

<br>

## 프로젝트 개요

### 개발 기간
- 2024.09 ~ 2024.10 (1개월)

### 개발 인원
- 5인 프로젝트

### 프로젝트 목표
- 실무 수준의 Spring Boot 웹 애플리케이션 개발 역량 강화
- JPA를 활용한 복잡한 도메인 모델 설계 및 구현
- 외부 API 연동 경험 (결제, 이메일)
- 보안을 고려한 인증/인가 시스템 구축

### 벤치마크
- 바나프레소(Banapresso) 온라인 주문 시스템을 참고하여 설계


<br>

## ERD
<img width="2920" height="2092" alt="코드프레소" src="https://github.com/user-attachments/assets/f21c3d89-1543-43f2-899d-2f0a1b33bf01" />

### 핵심 엔티티 및 관계

#### 1. 회원 (Member)
- 시스템의 핵심 사용자 엔티티
- **연관관계**:
  - 주문(Order): 1:N - 한 회원이 여러 주문 생성 가능
  - 장바구니(Cart): 1:N - 회원별 장바구니 아이템 관리
  - 리뷰(Review): 1:N - 구매 상품에 대한 리뷰 작성
  - 쿠폰(Coupon): N:M - 회원_쿠폰(Member_Coupon) 중간 테이블로 연결
  - 찜목록(Favorite): 1:N - 관심 상품 저장

#### 2. 상품 (Product)
- 판매 상품 정보 관리
- **연관관계**:
  - 카테고리(Category): N:1 - 상품은 하나의 카테고리에 속함
  - 옵션(Option): 1:N - 상품별 다양한 옵션 제공 (사이즈, 온도 등)
  - 리뷰(Review): 1:N - 상품별 리뷰 관리
  - 영양정보(Nutrition_Info): 1:1 - 상품별 영양성분 정보

#### 3. 주문 (Order)
- 주문 정보 및 상태 관리
- **연관관계**:
  - 회원(Member): N:1 - 주문자 정보
  - 주문상세(Order_Detail): 1:N - 주문에 포함된 상품 목록
  - 결제(Payment): 1:1 - 주문별 결제 정보
  - 쿠폰(Coupon): N:1 - 주문 시 사용한 쿠폰 (선택적)

#### 4. 결제 (Payment)
- Toss Payments 연동 결제 정보
- **주요 필드**: 결제수단, 금액, 상태, 승인번호
- **연관관계**: Order와 1:1 관계

#### 5. 지점 (Branch)
- 오프라인 매장 정보 관리
- **연관관계**: 
  - 주문(Order): 1:N - 픽업 지점 정보
  - 게시판(Board): 1:N - 지점별 공지사항

### 주요 비즈니스 로직

#### 주문 프로세스
1. 회원이 장바구니에 상품 추가
2. 주문 생성 (쿠폰 적용 가능)
3. 결제 진행 (Toss Payments)
4. 주문 상태 관리 (대기 → 확인 → 준비 → 완료)

#### 리뷰 시스템
- 주문 완료된 상품에 대해서만 리뷰 작성 가능
- 평점과 텍스트 리뷰 지원
- 상품별 평균 평점 자동 계산

#### 쿠폰 시스템
- 할인쿠폰/적립쿠폰 구분
- 사용조건 설정 (최소주문금액, 유효기간)
- 1회 사용 제한

### 핵심 엔티티 및 관계

#### 1. 회원 (Member)
- 시스템의 핵심 사용자 엔티티
- **연관관계**:
  - 주문(Order): 1:N - 한 회원이 여러 주문 생성 가능
  - 장바구니(Cart): 1:N - 회원별 장바구니 아이템 관리
  - 리뷰(Review): 1:N - 구매 상품에 대한 리뷰 작성
  - 쿠폰(Coupon): N:M - 회원_쿠폰(Member_Coupon) 중간 테이블로 연결
  - 찜목록(Favorite): 1:N - 관심 상품 저장

#### 2. 상품 (Product)
- 판매 상품 정보 관리
- **연관관계**:
  - 카테고리(Category): N:1 - 상품은 하나의 카테고리에 속함
  - 옵션(Option): 1:N - 상품별 다양한 옵션 제공 (사이즈, 온도 등)
  - 리뷰(Review): 1:N - 상품별 리뷰 관리
  - 영양정보(Nutrition_Info): 1:1 - 상품별 영양성분 정보

#### 3. 주문 (Order)
- 주문 정보 및 상태 관리
- **연관관계**:
  - 회원(Member): N:1 - 주문자 정보
  - 주문상세(Order_Detail): 1:N - 주문에 포함된 상품 목록
  - 결제(Payment): 1:1 - 주문별 결제 정보
  - 쿠폰(Coupon): N:1 - 주문 시 사용한 쿠폰 (선택적)

#### 4. 결제 (Payment)
- Toss Payments 연동 결제 정보
- **주요 필드**: 결제수단, 금액, 상태, 승인번호
- **연관관계**: Order와 1:1 관계

#### 5. 지점 (Branch)
- 오프라인 매장 정보 관리
- **연관관계**: 
  - 주문(Order): 1:N - 픽업 지점 정보
  - 게시판(Board): 1:N - 지점별 공지사항

### 주요 비즈니스 로직

#### 주문 프로세스
1. 회원이 장바구니에 상품 추가
2. 주문 생성 (쿠폰 적용 가능)
3. 결제 진행 (Toss Payments)
4. 주문 상태 관리 (대기 → 확인 → 준비 → 완료)

#### 리뷰 시스템
- 주문 완료된 상품에 대해서만 리뷰 작성 가능
- 평점과 텍스트 리뷰 지원
- 상품별 평균 평점 자동 계산

#### 쿠폰 시스템
- 할인쿠폰/적립쿠폰 구분
- 사용조건 설정 (최소주문금액, 유효기간)
- 1회 사용 제한
<br>

## 주요 기능

### 회원 관리
- 회원가입 및 로그인 (Spring Security 기반 인증)
- 이메일 인증 (Naver SMTP)
- 프로필 관리 (프로필 이미지 업로드)
- 아이디/비밀번호 찾기
- 찜 목록 관리

### 상품 관리
- 상품 카테고리별 조회
- 상품 검색 기능
- 상품 상세 정보
- 할인가격 적용

### 장바구니
- 장바구니 추가/수정/삭제
- 장바구니 목록 조회

### 주문 및 결제
- 주문 생성 및 관리
- Toss Payments 결제 연동
- 주문 내역 조회
- 주문 상세 정보

### 리뷰 시스템
- 상품 리뷰 작성/수정/삭제
- 리뷰 조회 및 평점

### 게시판
- 공지사항 및 게시글 작성/수정/삭제
- 게시판 타입별 분류
- 게시글 목록 및 상세 조회

### 쿠폰 시스템
- 쿠폰 발급 및 관리
- 쿠폰 적용

### 지점 관리
- 지점 정보 조회
- 지점 목록


<br>


## 기술 스택

### Backend
- **Java 21**
- **Spring Boot 3.5.5**
  - Spring MVC
  - Spring Data JPA
  - Spring Security
  - Spring Boot DevTools
  - Spring Boot Docker Compose
- **Hibernate** (JPA 구현체)
- **Lombok** (보일러플레이트 코드 제거)
- **Bean Validation** (입력 검증)

### Database
- **MySQL 8.4** (Docker Container)
- **JPA/Hibernate** (ORM)

### View
- **JSP (Jakarta Server Pages)**
- **JSTL 3.0** (Jakarta Standard Tag Library)

### Build Tool
- **Gradle 8.x**

### DevOps
- **Docker Compose** (MySQL 컨테이너)

### External Services
- **Toss Payments API** (결제 시스템)
- **Naver SMTP** (이메일 발송)

### API Documentation
- **Swagger UI / OpenAPI 3** (SpringDoc)

### Testing
- **JUnit 5**
- **Spring Boot Test**
- **H2 Database** (테스트용 인메모리 DB)


<br>

## 프로젝트 구조

```
codepresso-cafe-platform/
├── src/
│   ├── main/
│   │   ├── java/com/codepresso/codepresso/
│   │   │   ├── auth/                # 인증 도메인
│   │   │   │   ├── controller/      # 인증 컨트롤러
│   │   │   │   ├── dto/             # 인증 DTO
│   │   │   │   └── service/         # 인증 서비스
│   │   │   ├── board/               # 게시판 도메인
│   │   │   │   ├── controller/
│   │   │   │   ├── converter/
│   │   │   │   ├── dto/
│   │   │   │   └── service/
│   │   │   ├── branch/              # 지점 도메인
│   │   │   │   ├── controller/
│   │   │   │   ├── entity/
│   │   │   │   ├── repository/
│   │   │   │   └── service/
│   │   │   ├── cart/                # 장바구니 도메인
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── repository/
│   │   │   │   └── service/
│   │   │   ├── common/              # 공통 모듈
│   │   │   │   ├── config/          # 설정 (Security, Swagger, init 등)
│   │   │   │   ├── controller/      # 공통 컨트롤러
│   │   │   │   ├── exception/       # 예외 처리
│   │   │   │   ├── response/        # 공통 응답
│   │   │   │   └── security/        # Spring Security 설정
│   │   │   ├── coupon/              # 쿠폰 도메인
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── repository/
│   │   │   │   └── service/
│   │   │   ├── member/              # 회원 도메인
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── repository/
│   │   │   │   └── service/
│   │   │   ├── monitoring/          # 모니터링
│   │   │   ├── order/               # 주문 도메인
│   │   │   │   ├── controller/
│   │   │   │   ├── converter/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── repository/
│   │   │   │   └── service/
│   │   │   ├── payment/             # 결제 도메인
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── repository/
│   │   │   │   └── service/
│   │   │   ├── product/             # 상품 도메인
│   │   │   │   ├── controller/
│   │   │   │   ├── converter/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── repository/
│   │   │   │   └── service/
│   │   │   ├── review/              # 리뷰 도메인
│   │   │   │   ├── controller/
│   │   │   │   ├── converter/
│   │   │   │   └── dto/
│   │   │   ├── web/                 # 웹 관련
│   │   │   └── CodepressoApplication.java
│   │   ├── resources/
│   │   │   ├── application.yml      # 애플리케이션 설정
│   │   │   ├── db/seed/             # 데이터베이스 시드 데이터
│   │   │   └── static/              # 정적 리소스
│   │   │       ├── banners/         # 배너 이미지
│   │   │       ├── css/             # CSS 파일
│   │   │       ├── js/              # JavaScript 파일
│   │   │       └── uploads/         # 업로드 파일
│   │   │           ├── profile-images/
│   │   │           └── reviews/
│   │   └── webapp/
│   │       └── WEB-INF/
│   │           └── views/           # JSP 뷰 파일
│   │               ├── auth/
│   │               ├── board/
│   │               ├── branch/
│   │               ├── cart/
│   │               ├── common/
│   │               ├── home/
│   │               ├── member/
│   │               ├── order/
│   │               ├── payment/
│   │               ├── product/
│   │               ├── review/
│   │               └── test/
│   └── test/                        # 테스트 코드
│       ├── http/                    # HTTP 테스트
│       ├── java/com/codepresso/codepresso/
│       │   ├── repository/          # 레포지토리 테스트
│       │   │   ├── cart/
│       │   │   └── order/
│       │   └── service/             # 서비스 테스트
│       │       ├── member/
│       │       └── payment/
│       └── resources/               # 테스트 리소스
├── build.gradle                     # Gradle 빌드 설정
├── docker-compose.yml               # Docker Compose 설정 (MySQL)
└── README.md
```


<br>


### 아키텍처

프로젝트는 **레이어드 아키텍처 (Layered Architecture)** 패턴을 따릅니다:

1. **Controller Layer**: HTTP 요청/응답 처리
2. **Service Layer**: 비즈니스 로직 처리
3. **Repository Layer**: 데이터베이스 접근
4. **Entity Layer**: 도메인 모델 (JPA 엔티티)
5. **DTO Layer**: 계층 간 데이터 전송
6. **Converter Layer**: Entity와 DTO 간 변환


## Git 브랜치 전략

프로젝트는 **Git Flow** 전략을 따릅니다:

- `main`: 프로덕션 배포 브랜치
- `develop`: 개발 통합 브랜치
- `feature/*`: 새로운 기능 개발 브랜치
- `hotfix/*`: 긴급 버그 수정 브랜치
