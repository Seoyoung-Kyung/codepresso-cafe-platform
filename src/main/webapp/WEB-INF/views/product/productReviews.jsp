<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body class="product-review-page">

<style>
    @import url('${pageContext.request.contextPath}/css/productReviews.css');
</style>

<%@ include file="/WEB-INF/views/common/header.jspf" %>
<c:set var="currentCategory" value="${not empty product ? fn:toUpperCase(product.categoryName) : 'ALL'}"/>
<main class="product-page-main product-review-main">
    <%@ include file="/WEB-INF/views/product/product-category-nav.jspf" %>
    <div class="rcontainer">
        <div class="section-header">
            <h2 class="section-title">
            <button class="back-btn" onclick="history.back()">←</button>
            메뉴별 리뷰
        </h2>
        </div>
        <!-- 메뉴 정보 헤더 -->
        <div class="menu-header">
            <div class="menu-info-card">
                <div class="menu-image">
                    <c:choose>
                        <c:when test="${not empty product.productPhoto}">
                            <img src="${product.productPhoto}" alt="${product.productName}"/>
                        </c:when>
                        <c:otherwise>
                            <div class="no-image">이미지 없음</div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="menu-details">
                    <h1>${product.productName}</h1>
                    <p class="menu-price"><fmt:formatNumber value="${product.price}" pattern="#,###"/>원</p>
                    <p class="menu-description">${product.productContent}</p>
                    <div class="menu-rating">
                        <div class="stars" id="avgRatingStars">
                            <c:choose>
                                <c:when test="${not empty reviews}">
                                    <c:set var="avgRating" value="${reviews[0].avgRating}"/>
                                    <c:set var="fullStars" value="${avgRating != null ? avgRating.intValue() : 0}"/>
                                    <c:forEach begin="1" end="5" var="i">
                                        <c:choose>
                                            <c:when test="${i <= fullStars}">
                                                <span class="star filled">★</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="star">★</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach begin="1" end="5" var="i">
                                        <span class="star">★</span>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <span class="rating-score">
                                <c:choose>
                                    <c:when test="${not empty reviews and reviews[0].avgRating != null}">
                                        <fmt:formatNumber value="${reviews[0].avgRating}" pattern="#.#"/>
                                    </c:when>
                                    <c:otherwise>0.0</c:otherwise>
                                </c:choose>
                            </span>
                        <span class="review-count" id="totalReviewCount">(0개 리뷰)</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- 리뷰 필터 -->
        <div class="review-filters" id="reviewFilters" style="display: none;">
            <div class="filter-tabs" id="filterTabs">
                <button class="filter-tab active" data-filter="all">전체 (0)</button>
            </div>
        </div>

        <!-- 리뷰 목록 -->
        <div class="reviews-container">
            <div style="text-align: center; padding: 50px;">리뷰를 불러오는 중...</div>
        </div>

    </div>
</main>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>


<script>
    // 페이지 로드 시 초기 데이터 로딩
    document.addEventListener('DOMContentLoaded', function() {
        loadInitialReviews();
    });

    // 초기 리뷰 데이터 로딩
    function loadInitialReviews() {
        const currentPath = window.location.pathname;
        const pathMatch = currentPath.match(/\/products\/(\d+)\/reviews/);

        if (!pathMatch) {
            console.error('상품 ID를 찾을 수 없습니다');
            return;
        }

        const productId = pathMatch[1];
        const requestUrl = '/api/products/' + productId + '/reviews';

        fetch(requestUrl)
            .then(response => {
                if (!response.ok) {
                    throw new Error('HTTP 오류! 상태: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                // 리뷰 데이터 렌더링
                renderReviews(data);

                // 별점 및 리뷰 수 업데이트
                updateAverageRating(data);
                updateTotalReviewCount(data.length);

                // 필터 탭 생성
                createFilterTabs(data);

                // 필터 표시
                if (data && data.length > 0) {
                    document.getElementById('reviewFilters').style.display = 'block';
                }
            })
            .catch(error => {
                console.error('리뷰 로딩 실패:', error);
                const reviewsContainer = document.querySelector('.reviews-container');
                reviewsContainer.innerHTML = '<div style="text-align: center; padding: 50px; color: red;">리뷰를 불러올 수 없습니다. 오류: ' + error.message + '</div>';
            });
    }

    // 평균 별점 업데이트
    function updateAverageRating(reviews) {
        if (!reviews || reviews.length === 0) return;

        const avgRating = reviews[0].avgRating || 0;
        const fullStars = Math.floor(avgRating);
        const avgRatingStars = document.getElementById('avgRatingStars');

        if (avgRatingStars) {
            let starsHtml = '';
            for (let i = 1; i <= 5; i++) {
                if (i <= fullStars) {
                    starsHtml += '<span class="star filled">★</span>';
                } else {
                    starsHtml += '<span class="star">★</span>';
                }
            }
            avgRatingStars.innerHTML = starsHtml;
        }

        // 별점 점수 업데이트
        const ratingScore = document.querySelector('.rating-score');
        if (ratingScore) {
            ratingScore.textContent = avgRating.toFixed(1);
        }
    }

    // 총 리뷰 수 업데이트
    function updateTotalReviewCount(totalElements) {
        const totalReviewCount = document.getElementById('totalReviewCount');
        if (totalReviewCount) {
            totalReviewCount.textContent = '(' + totalElements + '개 리뷰)';
        }
    }

    // 필터 탭 생성
    function createFilterTabs(reviews) {
        if (!reviews || reviews.length === 0) return;

        const ratingCounts = { 5: 0, 4: 0, 3: 0, 2: 0, 1: 0 };

        reviews.forEach(review => {
            if (ratingCounts.hasOwnProperty(review.rating)) {
                ratingCounts[review.rating]++;
            }
        });

        const filterTabs = document.getElementById('filterTabs');
        let tabsHtml = '<button class="filter-tab active" data-filter="all">전체 (' + reviews.length + ')</button>';

        for (let rating = 5; rating >= 1; rating--) {
            if (ratingCounts[rating] > 0) {
                const stars = '★'.repeat(rating) + '☆'.repeat(5 - rating);
                tabsHtml += '<button class="filter-tab" data-filter="' + rating + '">' +
                    '<span class="filter-stars">' + stars + '</span>' +
                    '<span class="filter-count">(' + ratingCounts[rating] + ')</span>' +
                    '</button>';
            }
        }

        filterTabs.innerHTML = tabsHtml;

        // 필터 탭 이벤트 리스너 재등록
        attachFilterTabListeners();
    }

    // 필터 탭 이벤트 리스너 등록
    function attachFilterTabListeners() {
        document.querySelectorAll('.filter-tab').forEach(tab => {
            tab.addEventListener('click', function () {
                // 활성 탭 변경
                document.querySelectorAll('.filter-tab').forEach(t => t.classList.remove('active'));
                this.classList.add('active');

                // 리뷰 필터링
                const filterValue = this.dataset.filter;
                const reviews = document.querySelectorAll('.review-card');
                let visibleCount = 0;

                reviews.forEach(review => {
                    if (filterValue === 'all' || review.dataset.rating === filterValue) {
                        // 페이드인 애니메이션
                        review.style.opacity = '0';
                        review.style.display = 'block';
                        setTimeout(() => {
                            review.style.transition = 'opacity 0.3s ease';
                            review.style.opacity = '1';
                        }, 10);
                        visibleCount++;
                    } else {
                        // 페이드아웃 애니메이션
                        review.style.transition = 'opacity 0.2s ease';
                        review.style.opacity = '0';
                        setTimeout(() => {
                            review.style.display = 'none';
                        }, 200);
                    }
                });

                // 필터 결과 업데이트
                updateFilterResults(filterValue, visibleCount);
            });
        });
    }


    // 필터 결과 업데이트 함수
    function updateFilterResults(filterValue, count) {
        // 기존 결과 메시지 제거
        const existingMessage = document.querySelector('.filter-result-message');
        if (existingMessage) {
            existingMessage.remove();
        }

        // 필터 결과가 없는 경우 메시지 표시
        if (count === 0 && filterValue !== 'all') {
            const reviewsContainer = document.querySelector('.reviews-container');
            const noResultMessage = document.createElement('div');
            noResultMessage.className = 'filter-result-message no-filter-results';
            noResultMessage.innerHTML = '<div class="no-results-icon">🔍</div>' +
                '<h3>해당 별점의 리뷰가 없습니다</h3>' +
                '<p>다른 별점을 선택하거나 전체 리뷰를 확인해보세요.</p>';
            reviewsContainer.appendChild(noResultMessage);
        }
    }

    // 도움돼요 버튼 기능
    document.querySelectorAll('.action-btn.helpful').forEach(btn => {
        btn.addEventListener('click', function () {
            // 여기에 AJAX 호출 로직 추가
            this.style.background = 'var(--pink-1)';
            this.style.color = 'var(--white)';
            this.style.borderColor = 'var(--pink-1)';
        });
    });


    // 리뷰 렌더링 함수
    function renderReviews(reviews) {
        const reviewsContainer = document.querySelector('.reviews-container');

        if (!reviews || reviews.length === 0) {
            reviewsContainer.innerHTML = '<div class="no-reviews">' +
                '<div class="no-reviews-icon">📝</div>' +
                '<h3>아직 작성된 리뷰가 없습니다</h3>' +
                '<p>이 상품의 첫 번째 리뷰를 작성해보세요!</p>' +
            '</div>';
            return;
        }

        let html = '';
        reviews.forEach(review => {
            html += '<div class="review-card" data-rating="' + review.rating + '">' +
                '<div class="review-header">' +
                    '<div class="reviewer-info">' +
                        '<div class="reviewer-avatar">' + review.nickname.substring(0, 1) + '**</div>' +
                        '<div class="reviewer-details">' +
                            '<div class="reviewer-name">' + review.nickname.substring(0, 1) + '**</div>' +
                            '<div class="review-date">' + formatDate(review.createdAt) + '</div>' +
                        '</div>' +
                    '</div>' +
                    '<div class="review-rating">' +
                        '<div class="stars">' +
                            generateStars(review.rating) +
                        '</div>' +
                    '</div>' +
                '</div>' +
                '<div class="review-content">' +
                    '<p class="review-text">' + review.content + '</p>' +
                    (review.photoUrl ? '<div class="review-images"><img src="' + review.photoUrl + '" alt="리뷰 사진" class="review-image"/></div>' : '') +
                '</div>' +
                '<div class="review-actions">' +
                    '<button class="action-btn helpful">' +
                        '<span class="icon">👍</span>' +
                        '도움돼요 (0)' +
                    '</button>' +
                '</div>' +
            '</div>';
        });

        reviewsContainer.innerHTML = html;
    }


    // 별점 생성 함수
    function generateStars(rating) {
        let stars = '';
        for (let i = 1; i <= 5; i++) {
            if (i <= rating) {
                stars += '<span class="star filled">★</span>';
            } else {
                stars += '<span class="star">★</span>';
            }
        }
        return stars;
    }

    // 날짜 포맷 함수
    function formatDate(dateArray) {
        if (!dateArray || dateArray.length < 3) return '';
        const [year, month, day] = dateArray;
        return year + '.' + String(month).padStart(2, '0') + '.' + String(day).padStart(2, '0');
    }

</script>
<script src="${pageContext.request.contextPath}/js/categoryNav.js"></script>
</body>
</html>