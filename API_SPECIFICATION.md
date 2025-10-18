# CodePresso API 명세서

## 목차
- [인증 (Authentication)](#인증-authentication)
- [회원 (Member)](#회원-member)
- [상품 (Product)](#상품-product)
- [장바구니 (Cart)](#장바구니-cart)
- [주문 (Order)](#주문-order)
- [결제 (Payment)](#결제-payment)
- [리뷰 (Review)](#리뷰-review)
- [게시판 (Board)](#게시판-board)
- [지점 (Branch)](#지점-branch)
- [쿠폰 (Coupon)](#쿠폰-coupon)

---

## 인증 (Authentication)

### 1. 중복 체크
중복된 아이디, 닉네임, 이메일을 확인합니다.

**Endpoint:** `GET /api/auth/check`

**인증:** 불필요

**Query Parameters:**
- `value` (required): 확인할 값
- `field` (optional): 체크할 필드 (`ID`, `NICKNAME`, `EMAIL`) - 기본값: `ID`

**Response:**
```json
{
  "field": "ID",
  "duplicate": false
}
```

---

### 2. 회원가입
새로운 회원을 등록합니다.

**Endpoint:** `POST /api/auth/signup`

**인증:** 불필요

**Request Body:**
```json
{
  "accountId": "user123",
  "password": "password123!",
  "nickname": "홍길동",
  "email": "user@example.com",
  "name": "홍길동",
  "phone": "010-1234-5678"
}
```

**Response:**
```json
{
  "id": 1,
  "accountId": "user123",
  "name": "홍길동",
  "phone": "010-1234-5678",
  "nickname": "홍길동",
  "email": "user@example.com"
}
```

---

### 3. 이메일 인증번호 발송
이메일로 6자리 인증번호를 발송합니다.

**Endpoint:** `POST /api/auth/send-email-verification`

**인증:** 불필요

**Request Body:**
```json
{
  "email": "user@example.com"
}
```

**Response:**
```json
{
  "message": "인증번호가 발송되었습니다.",
  "verificationCode": "123456"
}
```

---

### 4. 이메일 인증번호 검증
발송된 인증번호를 확인합니다.

**Endpoint:** `POST /api/auth/verify-email-code`

**인증:** 불필요

**Request Body:**
```json
{
  "email": "user@example.com",
  "code": "123456"
}
```

**Response:**
```json
{
  "valid": true,
  "message": "이메일 인증이 완료되었습니다."
}
```

---

### 5. 아이디 찾기
닉네임과 이메일로 아이디를 찾고 인증번호를 발송합니다.

**Endpoint:** `POST /api/auth/find-id`

**인증:** 불필요

**Request Body:**
```json
{
  "nickname": "홍길동",
  "email": "user@example.com"
}
```

**Response:**
```json
{
  "success": true,
  "message": "인증번호가 이메일로 발송되었습니다.",
  "email": "user@example.com"
}
```

---

### 6. 아이디 찾기 인증번호 검증
인증번호를 확인하고 아이디를 반환합니다.

**Endpoint:** `POST /api/auth/verify-id-code`

**인증:** 불필요

**Request Body:**
```json
{
  "email": "user@example.com",
  "verificationCode": "123456"
}
```

**Response:**
```json
{
  "success": true,
  "message": "아이디를 찾았습니다.",
  "accountId": "user123"
}
```

---

### 7. 비밀번호 찾기
사용자 정보를 확인하고 인증번호를 발송합니다.

**Endpoint:** `POST /api/password/find`

**인증:** 불필요

**Request Body:**
```json
{
  "accountId": "user123",
  "name": "홍길동",
  "email": "user@example.com"
}
```

**Response:**
```json
{
  "success": true,
  "message": "인증번호가 이메일로 발송되었습니다.",
  "email": "user@example.com"
}
```

---

### 8. 비밀번호 재설정
새로운 비밀번호로 변경합니다.

**Endpoint:** `POST /api/password/reset`

**인증:** 불필요

**Request Body:**
```json
{
  "email": "user@example.com",
  "verificationCode": "123456",
  "newPassword": "newPassword123!"
}
```

**Response:**
```json
{
  "success": true,
  "message": "비밀번호가 성공적으로 재설정되었습니다."
}
```

---

## 회원 (Member)

### 1. 내 정보 조회
로그인한 사용자의 정보를 조회합니다.

**Endpoint:** `GET /api/users/me`

**인증:** 필요

**Response:**
```json
{
  "memberId": 1,
  "accountId": "user123",
  "nickname": "홍길동",
  "email": "user@example.com",
  "name": "홍길동",
  "phone": "010-1234-5678",
  "profileImage": "/uploads/profile-images/user123.jpg",
  "createdAt": "2025-01-01T00:00:00"
}
```

---

### 2. 프로필 수정
로그인한 사용자의 프로필을 수정합니다.

**Endpoint:** `PUT /api/users/me`

**인증:** 필요

**Request Body:**
```json
{
  "nickname": "새닉네임",
  "phone": "010-9876-5432"
}
```

**Response:**
```json
{
  "memberId": 1,
  "accountId": "user123",
  "nickname": "새닉네임",
  "email": "user@example.com",
  "name": "홍길동",
  "phone": "010-9876-5432",
  "profileImage": "/uploads/profile-images/user123.jpg"
}
```

---

### 3. 프로필 이미지 업로드
프로필 이미지를 업로드합니다.

**Endpoint:** `POST /api/profile/image`

**인증:** 필요

**Content-Type:** `multipart/form-data`

**Request:**
- `file`: 이미지 파일 (최대 5MB)

**Response:**
```json
{
  "success": true,
  "message": "프로필 이미지가 업로드되었습니다.",
  "imageUrl": "/uploads/profile-images/user123_1234567890.jpg"
}
```

---

### 4. 프로필 이미지 삭제
프로필 이미지를 삭제합니다.

**Endpoint:** `DELETE /api/profile/image`

**인증:** 필요

**Response:**
```json
{
  "success": true,
  "message": "프로필 이미지가 삭제되었습니다."
}
```

---

### 5. 찜 목록 추가
상품을 찜 목록에 추가합니다.

**Endpoint:** `POST /users/favorites`

**인증:** 필요

**Request Body:**
```json
{
  "productId": 1
}
```

**Response:**
```json
{
  "success": true,
  "message": "찜 목록에 추가되었습니다."
}
```

---

### 6. 찜 목록 조회
내 찜 목록을 조회합니다.

**Endpoint:** `GET /users/favorites`

**인증:** 필요

**Response:**
```json
{
  "favorites": [
    {
      "favoriteId": 1,
      "productId": 1,
      "productName": "아메리카노",
      "productPhoto": "/images/americano.jpg",
      "price": 4500,
      "categoryName": "커피"
    }
  ]
}
```

---

### 7. 찜 목록 삭제
찜 목록에서 상품을 제거합니다.

**Endpoint:** `DELETE /users/favorites/{productId}`

**인증:** 필요

**Path Parameters:**
- `productId`: 상품 ID

**Response:**
```json
{
  "success": true,
  "message": "찜 목록에서 제거되었습니다."
}
```

---

## 상품 (Product)

### 1. 전체 상품 조회
카테고리별 전체 상품을 조회합니다.

**Endpoint:** `GET /api/products`

**인증:** 불필요

**Response:**
```json
[
  {
    "productId": 1,
    "productName": "아메리카노",
    "productPhoto": "/images/americano.jpg",
    "price": 4500,
    "categoryName": "커피",
    "categoryCode": "COFFEE"
  }
]
```

---

### 2. 상품 랜덤 추천
랜덤으로 4개의 상품을 추천합니다.

**Endpoint:** `GET /api/products/random`

**인증:** 불필요

**Response:**
```json
[
  {
    "productId": 1,
    "productName": "아메리카노",
    "productPhoto": "/images/americano.jpg",
    "price": 4500,
    "categoryName": "커피",
    "categoryCode": "COFFEE"
  }
]
```

---

### 3. 상품별 리뷰 조회
특정 상품의 리뷰를 조회합니다.

**Endpoint:** `GET /api/products/{productId}/reviews`

**인증:** 불필요

**Path Parameters:**
- `productId`: 상품 ID

**Response:**
```json
[
  {
    "reviewId": 1,
    "memberId": 1,
    "memberNickname": "홍길동",
    "rating": 5,
    "content": "정말 맛있어요!",
    "createdAt": "2025-01-01T10:00:00"
  }
]
```

---

### 4. 상품 검색 (키워드)
키워드로 상품을 검색합니다.

**Endpoint:** `POST /api/products/search/keyword`

**인증:** 불필요

**Query Parameters:**
- `keyword`: 검색 키워드

**Response:**
```json
[
  {
    "productId": 1,
    "productName": "아메리카노",
    "productPhoto": "/images/americano.jpg",
    "price": 4500,
    "categoryName": "커피",
    "categoryCode": "COFFEE"
  }
]
```

---

### 5. 상품 검색 (해시태그)
여러 해시태그로 상품을 검색합니다.

**Endpoint:** `POST /api/products/search/hashtags`

**인증:** 불필요

**Query Parameters:**
- `hashtags`: 해시태그 목록 (배열)

**Response:**
```json
[
  {
    "productId": 1,
    "productName": "아메리카노",
    "productPhoto": "/images/americano.jpg",
    "price": 4500,
    "categoryName": "커피",
    "categoryCode": "COFFEE"
  }
]
```

---

## 장바구니 (Cart)

### 1. 장바구니 조회
내 장바구니를 조회합니다.

**Endpoint:** `GET /users/cart`

**인증:** 필요

**Response:**
```json
{
  "cartId": 1,
  "items": [
    {
      "cartItemId": 1,
      "productId": 1,
      "productName": "아메리카노",
      "quantity": 2,
      "price": 9000,
      "options": [
        {
          "optionId": 1,
          "extraPrice": 500,
          "optionStyle": "샷 추가"
        }
      ]
    }
  ],
  "totalPrice": 9000
}
```

---

### 2. 장바구니 아이템 개수 조회
장바구니에 담긴 상품 개수를 조회합니다.

**Endpoint:** `GET /users/cart/count`

**인증:** 필요

**Response:**
```json
3
```

---

### 3. 장바구니에 상품 추가
장바구니에 상품을 추가합니다.

**Endpoint:** `POST /users/cart`

**인증:** 필요

**Query Parameters:**
- `productId`: 상품 ID
- `quantity`: 수량
- `optionIds` (optional): 옵션 ID 목록

**Response:**
```json
{
  "cartItemId": 1,
  "productId": 1,
  "productName": "아메리카노",
  "quantity": 2,
  "price": 9000,
  "options": [
    {
      "optionId": 1,
      "extraPrice": 500,
      "optionStyle": "샷 추가"
    }
  ]
}
```

---

### 4. 장바구니 상품 수량 수정
장바구니 상품의 수량을 변경합니다.

**Endpoint:** `PATCH /users/cart/{cartItemId}`

**인증:** 필요

**Path Parameters:**
- `cartItemId`: 장바구니 아이템 ID

**Request Body:**
```json
{
  "quantity": 3
}
```

**Response:**
```json
"수량이 변경되었습니다."
```

---

### 5. 장바구니 상품 삭제
장바구니에서 상품을 삭제합니다.

**Endpoint:** `DELETE /users/cart/{cartItemId}`

**인증:** 필요

**Path Parameters:**
- `cartItemId`: 장바구니 아이템 ID

**Response:**
```json
"아이템이 삭제되었습니다."
```

---

### 6. 장바구니 비우기
장바구니의 모든 상품을 삭제합니다.

**Endpoint:** `POST /users/cart/clear`

**인증:** 필요

**Query Parameters:**
- `cartId`: 장바구니 ID

**Response:**
```json
"장바구니가 비워졌습니다."
```

---

## 주문 (Order)

### 1. 주문 내역 조회
기간별 주문 내역을 조회합니다.

**Endpoint:** `GET /users/orders`

**인증:** 필요

**Query Parameters:**
- `period` (optional): 조회 기간 (`1개월`, `3개월`, `전체`) - 기본값: `1개월`
- `page` (optional): 페이지 번호 (0부터 시작) - 기본값: 0
- `size` (optional): 페이지 크기 - 기본값: 10

**Response:**
```json
{
  "orders": [
    {
      "orderId": 1,
      "orderDate": "2025-01-01T10:00:00",
      "totalAmount": 15000,
      "status": "COMPLETED",
      "itemCount": 2
    }
  ],
  "currentPage": 0,
  "totalPages": 5,
  "totalElements": 50
}
```

---

### 2. 주문 상세 조회
주문의 상세 정보를 조회합니다.

**Endpoint:** `GET /users/orders/{orderId}`

**인증:** 필요

**Path Parameters:**
- `orderId`: 주문 ID

**Response:**
```json
{
  "orderId": 1,
  "orderDate": "2025-01-01T10:00:00",
  "status": "COMPLETED",
  "totalAmount": 15000,
  "items": [
    {
      "productId": 1,
      "productName": "아메리카노",
      "quantity": 2,
      "price": 9000,
      "options": ["샷 추가"]
    }
  ],
  "deliveryAddress": "서울시 강남구 테헤란로 123",
  "paymentMethod": "카드"
}
```

---

## 결제 (Payment)

### 1. 장바구니 결제 페이지 데이터 조회
장바구니 기반 결제 페이지 데이터를 조회합니다.

**Endpoint:** `GET /api/payments/cart`

**인증:** 필요

**Response:**
```json
{
  "orderId": "ORDER_20250101_123456",
  "orderName": "아메리카노 외 2건",
  "totalAmount": 15000,
  "items": [
    {
      "productId": 1,
      "productName": "아메리카노",
      "quantity": 2,
      "price": 9000
    }
  ]
}
```

---

### 2. 직접 결제 페이지 데이터 조회
단일 상품 직접 결제 페이지 데이터를 조회합니다.

**Endpoint:** `GET /api/payments/direct`

**인증:** 필요

**Request Body:**
```json
{
  "productId": 1,
  "quantity": 2,
  "optionIds": [1, 2]
}
```

**Response:**
```json
{
  "orderId": "ORDER_20250101_123456",
  "orderName": "아메리카노",
  "totalAmount": 9000,
  "items": [
    {
      "productId": 1,
      "productName": "아메리카노",
      "quantity": 2,
      "price": 9000
    }
  ]
}
```

---

### 3. Toss Payments 결제 성공 처리
토스페이먼츠 결제 성공 후 주문을 생성합니다.

**Endpoint:** `POST /api/payments/toss-success`

**인증:** 필요

**Request Body:**
```json
{
  "paymentKey": "payment_key_12345",
  "orderId": "ORDER_20250101_123456",
  "amount": 15000
}
```

**Response:**
```json
{
  "orderId": "ORDER_20250101_123456",
  "orderName": "아메리카노 외 2건",
  "totalAmount": 15000,
  "status": "COMPLETED"
}
```

---

## 리뷰 (Review)

### 1. 리뷰 수정
작성한 리뷰를 수정합니다.

**Endpoint:** `PATCH /api/users/reviews/{reviewId}`

**인증:** 필요

**Path Parameters:**
- `reviewId`: 리뷰 ID

**Request Body:**
```json
{
  "rating": 5,
  "content": "정말 맛있어요!"
}
```

**Response:**
```json
{
  "reviewId": 1,
  "memberId": 1,
  "productId": 1,
  "rating": 5,
  "content": "정말 맛있어요!",
  "updatedAt": "2025-01-02T10:00:00"
}
```

---

### 2. 리뷰 삭제
작성한 리뷰를 삭제합니다.

**Endpoint:** `DELETE /api/users/reviews/{reviewId}`

**인증:** 필요

**Path Parameters:**
- `reviewId`: 리뷰 ID

**Response:**
- HTTP Status: 204 No Content

---

## 게시판 (Board)

### 1. 게시판 타입 목록 조회
게시판 타입 목록을 조회합니다.

**Endpoint:** `GET /boards/types`

**인증:** 불필요

**Response:**
```json
[
  {
    "boardTypeId": 1,
    "typeName": "공지사항",
    "description": "운영진 공지사항"
  },
  {
    "boardTypeId": 2,
    "typeName": "자유게시판",
    "description": "자유롭게 소통하는 공간"
  }
]
```

---

### 2. 게시글 목록 조회
게시판 타입별 게시글 목록을 조회합니다.

**Endpoint:** `GET /boards`

**인증:** 불필요

**Query Parameters:**
- `boardTypeId`: 게시판 타입 ID
- `page` (optional): 페이지 번호 - 기본값: 0
- `size` (optional): 페이지 크기 - 기본값: 10

**Response:**
```json
{
  "boards": [
    {
      "boardId": 1,
      "title": "공지사항입니다",
      "content": "내용...",
      "authorNickname": "관리자",
      "createdAt": "2025-01-01T10:00:00",
      "viewCount": 100,
      "commentCount": 5
    }
  ],
  "currentPage": 0,
  "totalPages": 10,
  "totalElements": 100
}
```

---

### 3. 게시글 상세 조회
게시글의 상세 내용을 조회합니다.

**Endpoint:** `GET /boards/{boardId}`

**인증:** 불필요

**Path Parameters:**
- `boardId`: 게시글 ID

**Response:**
```json
{
  "boardId": 1,
  "title": "공지사항입니다",
  "content": "내용...",
  "authorNickname": "관리자",
  "createdAt": "2025-01-01T10:00:00",
  "updatedAt": "2025-01-01T10:00:00",
  "viewCount": 100,
  "children": []
}
```

---

### 4. 게시글 작성
새로운 게시글을 작성합니다.

**Endpoint:** `POST /boards`

**인증:** 필요

**Request Body:**
```json
{
  "boardTypeId": 2,
  "title": "게시글 제목",
  "content": "게시글 내용"
}
```

**Response:**
```json
{
  "success": true,
  "message": "게시글이 작성되었습니다.",
  "data": {
    "boardId": 1
  }
}
```

---

### 5. 게시글 수정
작성한 게시글을 수정합니다.

**Endpoint:** `PUT /boards/{boardId}`

**인증:** 필요

**Path Parameters:**
- `boardId`: 게시글 ID

**Request Body:**
```json
{
  "title": "수정된 제목",
  "content": "수정된 내용"
}
```

**Response:**
```json
{
  "success": true,
  "message": "게시글이 수정되었습니다."
}
```

---

### 6. 게시글 삭제
작성한 게시글을 삭제합니다.

**Endpoint:** `DELETE /boards/{boardId}`

**인증:** 필요

**Path Parameters:**
- `boardId`: 게시글 ID

**Response:**
```json
{
  "success": true,
  "message": "게시글이 삭제되었습니다."
}
```

---

### 7. 댓글 목록 조회
게시글의 댓글을 조회합니다.

**Endpoint:** `GET /boards/{parentBoardId}/comments`

**인증:** 불필요

**Path Parameters:**
- `parentBoardId`: 부모 게시글 ID

**Response:**
```json
[
  {
    "boardId": 2,
    "content": "댓글 내용",
    "authorNickname": "홍길동",
    "createdAt": "2025-01-01T11:00:00"
  }
]
```

---

### 8. 댓글 작성
게시글에 댓글을 작성합니다.

**Endpoint:** `POST /boards/{parentBoardId}/comments`

**인증:** 필요

**Path Parameters:**
- `parentBoardId`: 부모 게시글 ID

**Request Body:**
```json
{
  "content": "댓글 내용"
}
```

**Response:**
```json
{
  "success": true,
  "message": "댓글이 작성되었습니다."
}
```

---

### 9. 댓글 수정
작성한 댓글을 수정합니다.

**Endpoint:** `PUT /boards/{parentBoardId}/comments/{boardId}`

**인증:** 필요

**Path Parameters:**
- `parentBoardId`: 부모 게시글 ID
- `boardId`: 댓글 ID

**Request Body:**
```json
{
  "content": "수정된 댓글 내용"
}
```

**Response:**
```json
{
  "success": true,
  "message": "댓글이 수정되었습니다."
}
```

---

### 10. 댓글 삭제
작성한 댓글을 삭제합니다.

**Endpoint:** `DELETE /boards/{parentBoardId}/comments/{boardId}`

**인증:** 필요

**Path Parameters:**
- `parentBoardId`: 부모 게시글 ID
- `boardId`: 댓글 ID

**Response:**
```json
{
  "success": true,
  "message": "댓글이 삭제되었습니다."
}
```

---

## 지점 (Branch)

### 1. 지점 정보 조회
특정 지점의 정보를 조회합니다.

**Endpoint:** `GET /branch/info/{branchId}`

**인증:** 불필요

**Path Parameters:**
- `branchId`: 지점 ID

**Response:**
```json
{
  "id": 1,
  "name": "강남점",
  "address": "서울시 강남구 테헤란로 123",
  "openingTime": "09:00",
  "closingTime": "22:00"
}
```

---

## 쿠폰 (Coupon)

### 1. 사용 가능한 쿠폰 개수 조회
내가 보유한 사용 가능한 쿠폰 개수를 조회합니다.

**Endpoint:** `GET /api/coupons/me/count`

**인증:** 필요

**Response:**
```json
{
  "validCouponCount": 3
}
```

---

### 2. 내 쿠폰 목록 조회
보유한 쿠폰 목록을 조회합니다.

**Endpoint:** `GET /api/coupons/me`

**인증:** 필요

**Response:**
```json
[
  {
    "couponId": 1,
    "couponName": "신규 가입 10% 할인",
    "discountType": "PERCENTAGE",
    "discountValue": 10,
    "expiryDate": "2025-12-31",
    "isUsed": false
  }
]
```

---

### 3. 내 스탬프 조회
보유한 스탬프 개수를 조회합니다.

**Endpoint:** `GET /api/stamp`

**인증:** 필요

**Response:**
```json
{
  "currentStamp": 5
}
```

---

## 공통 에러 응답

### 400 Bad Request
요청 데이터가 잘못되었을 때

```json
{
  "success": false,
  "message": "잘못된 요청입니다."
}
```

### 401 Unauthorized
인증이 필요한 API에 인증 없이 접근했을 때

```json
{
  "message": "인증이 필요합니다."
}
```

### 403 Forbidden
권한이 없을 때 (예: 다른 사용자의 리소스 수정 시도)

```json
{
  "message": "권한이 없습니다."
}
```

### 404 Not Found
리소스를 찾을 수 없을 때

```json
{
  "message": "리소스를 찾을 수 없습니다."
}
```

### 500 Internal Server Error
서버 내부 오류

```json
{
  "message": "서버 오류가 발생했습니다."
}
```

---

## 인증 방식

CodePresso API는 **Spring Security**를 기반으로 **세션 기반 인증**을 사용합니다.

### 로그인
- **Endpoint:** `/login` (POST)
- **Form Data:**
  - `username`: 계정 아이디
  - `password`: 비밀번호

로그인 성공 시 세션이 생성되며, 이후 요청에 세션 쿠키(`JSESSIONID`)를 포함하여 인증됩니다.

### 로그아웃
- **Endpoint:** `/logout` (POST)

---

## Base URL

- **개발 환경:** `http://localhost:8080`
- **프로덕션 환경:** TBD

---

## Swagger UI

API 문서를 대화형으로 테스트할 수 있습니다:
- **URL:** http://localhost:8080/swagger-ui.html

---

**Version:** 0.0.1-SNAPSHOT
**Last Updated:** 2025-10-09
