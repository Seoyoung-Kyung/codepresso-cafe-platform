<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body class="my-review-page">

<style>
    @import url('${pageContext.request.contextPath}/css/myReview.css');
</style>

<%@ include file="/WEB-INF/views/common/header.jspf" %>

<main class="my-review-main">
    <div class="rcontainer">
        <%@ include file="/WEB-INF/views/member/mypage-header.jspf" %>

        <!-- 리뷰 통계 카드 -->
        <div class="review-stats-card">
            <div class="stats-item">
                <div class="stats-number" id="totalReviews">0</div>
                <div class="stats-label">작성한 리뷰</div>
            </div>
            <div class="stats-item">
                <div class="stats-number" id="averageRating">0.0</div>
                <div class="stats-label">평균 별점</div>
            </div>
        </div>

        <!-- 리뷰 필터 -->
        <div class="review-filters" id="reviewFilters" style="display: none;">
            <div class="filter-tabs" id="filterTabs">
                <button class="filter-tab active" data-filter="all">전체 (0)</button>
            </div>
        </div>


        <!-- 리뷰 목록 -->
        <div class="my-reviews-container">
            <c:choose>
                <c:when test="${empty userReviews}">
                    <div class="no-reviews">
                        <div class="no-reviews-icon">📝</div>
                        <h3>작성한 리뷰가 없습니다</h3>
                        <p>상품을 구매한 후 리뷰를 작성해보세요!</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="review" items="${userReviews}">
                        <div class="my-review-card" data-rating="${review.rating}">
                            <div class="review-product-info">
                                <div class="product-image">
                                    <img src="${review.productPhoto}" alt="${review.productName}" onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
                                    <div class="no-image" style="display:none;">이미지 없음</div>
                                </div>
                                <div class="product-details">
                                    <div class="review-date">${review.orderDate.toLocalDate()} ${review.branchName}에서 주문</div>
                                    <h3 class="product-name">${review.productName}</h3>
                                    <div class="review-date">작성일 : ${review.createdAt.toLocalDate()}</div>
                                </div>
                            </div>
                            <div class="review-content-section">
                                <div class="review-rating">
                                    <div class="stars">
                                        <c:forEach var="i" begin="1" end="5">
                                            <c:choose>
                                                <c:when test="${i <= review.rating}">
                                                    <span class="star filled">⭐</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="star">⭐</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                    </div>
                                    <span class="rating-text">${review.rating}.0</span>
                                </div>
                                <div class="review-text">${review.content}</div>
                                <c:if test="${not empty review.photoUrl}">
                                    <div class="review-images">
                                        <img src="${review.photoUrl}" alt="리뷰 사진" class="review-image"/>
                                    </div>
                                </c:if>
                                <div class="review-actions">
                                    <button class="action-btn edit-btn" data-edit="${review.id}">
                                        수정
                                    </button>
                                <button class="action-btn delete-btn" data-delete="${review.id}">
                                    삭제
                                </button>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- 페이지네이션 -->
        <div class="pagination-container" id="paginationContainer" style="display: none;">
            <div class="pagination">
                <button class="page-btn prev" id="prevBtn">이전</button>
                <div class="page-numbers" id="pageNumbers"></div>
                <button class="page-btn next" id="nextBtn">다음</button>
            </div>
        </div>

        <!-- 주문하러 가기 버튼 -->
        <div style="text-align: center; margin: 32px 0 20px;">
            <a class="btn btn-primary" href="/branch/list" style="padding: 16px 48px; font-size: 18px; font-weight: 700;">주문하러 가기</a>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>

<script>

    // 서버에서 전달된 리뷰 데이터
    <c:if test="${not empty userReviews}">
    const serverReviews = [
        <c:forEach var="review" items="${userReviews}" varStatus="status">
        {
            id: ${review.id},
            productName: '${fn:escapeXml(review.productName)}',
            productPhoto: '${review.productPhoto}',
            photoUrl: '${review.photoUrl}',
            rating: ${review.rating},
            content: '${fn:escapeXml(review.content)}',
            createdAt: [${review.createdAt.year}, ${review.createdAt.monthValue}, ${review.createdAt.dayOfMonth}],
            branchName:'${review.branchName}',
            orderDate: [${review.orderDate.year}, ${review.orderDate.monthValue}, ${review.orderDate.dayOfMonth}],
            formattedDate: '${review.formattedDate}',
            helpfulCount: 0,
            productId: ${review.productId}
        }<c:if test="${not status.last}">,</c:if>
        </c:forEach>
    ];
    </c:if>
    <c:if test="${empty userReviews}">
    const serverReviews = [];
    </c:if>

    // 페이지 로드 시 초기 설정 (간소화)
    document.addEventListener('DOMContentLoaded', function() {
        // 서버에서 렌더링된 통계 업데이트
        updateStatsFromServer();
    });


    // 서버 데이터로 통계 업데이트
    function updateStatsFromServer() {
        const reviews = serverReviews || [];
        updateStats(reviews);
    }

    // 통계 업데이트
    function updateStats(reviews) {
        const totalReviews = reviews.length;
        const averageRating = totalReviews > 0 ?
            (reviews.reduce((sum, review) => sum + review.rating, 0) / totalReviews).toFixed(1) : '0.0';
        const helpfulCount = reviews.reduce((sum, review) => sum + review.helpfulCount, 0);

        document.getElementById('totalReviews').textContent = totalReviews;
        document.getElementById('averageRating').textContent = averageRating;
        document.getElementById('helpfulCount').textContent = helpfulCount;
    }


    // 이벤트 위임을 사용한 리뷰 삭제 처리
    document.addEventListener('click', e => {
        // 삭제 버튼 클릭 처리
        const deleteBtn = e.target.closest('[data-delete]');
        if (deleteBtn) {
            const reviewId = deleteBtn.dataset.delete;
            console.log('클릭된 버튼:', deleteBtn);
            console.log('data-delete 속성 값:', reviewId);
            console.log('reviewId 타입:', typeof reviewId);

            if (!confirm('정말로 이 리뷰를 삭제하시겠습니까?')) return;

            const requestUrl = '/api/users/reviews/' + reviewId;
            console.log('requestUrl:', '/api/users/reviews/'+ reviewId);
            fetch(requestUrl, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                credentials: 'same-origin'
            })
            .then(res => {
                if (!res.ok) throw new Error(`삭제 실패 (${res.status})`);

                // 해당 리뷰 카드 제거
                const reviewCard = deleteBtn.closest('.my-review-card');
                if (reviewCard) {
                    reviewCard.remove();
                }

                alert('리뷰가 삭제되었습니다.');

                // 통계 업데이트를 위해 페이지 새로고침
                setTimeout(() => {
                    window.location.reload();
                }, 500);
            })
            .catch(err => {
                console.error('Delete error:', err);
                alert(err.message || '삭제에 실패했습니다.');
            });
        }

        // 수정 버튼 클릭 처리
        const editBtn = e.target.closest('[data-edit]');
        if (editBtn) {
            const reviewId = editBtn.dataset.edit;
            window.location.href = '/users/reviews/' + reviewId + '/edit';
        }
    });
</script>
</body>
</html>
