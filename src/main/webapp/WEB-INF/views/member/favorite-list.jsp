<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<body class="favorite-page">
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<style>
    .favorite-main {
        padding: 40px 0 80px;
    }

    .favorite-container {
        max-width: 960px;
        margin: 0 auto;
        padding: 0 20px;
    }

    .favorite-page .favorite-item {
        background: #fff;
        border-radius: 18px;
        border: 1px solid rgba(255, 122, 162, 0.2);
        box-shadow: 0 24px 48px rgba(15, 23, 42, 0.08);
        padding: 18px;
        transition: transform .12s ease, box-shadow .2s ease;
        display: flex;
        flex-direction: column;
        height: 100%;
    }

    .favorite-actions {
        display: flex;
        gap: 8px;
        justify-content: space-between;
        margin-top: auto;
    }

    .favorite-actions .btn {
        font-size: 14px;
    }

    .favorite-page .favorite-item:hover {
        transform: translateY(-4px);
        box-shadow: 0 28px 60px rgba(15, 23, 42, 0.14);
    }

    .favorite-page .btn.btn-ghost {
        background: #fff;
        color: var(--pink-1);
        border: 1px solid rgba(255, 122, 162, 0.6);
    }

    .favorite-page .btn.btn-ghost:hover {
        border-color: var(--pink-1);
        background: rgba(255, 122, 162, 0.08);
        color: var(--pink-1);
    }

    /* 상품 이미지 컨테이너 */
    .favorite-page .product-image-wrapper {
        width: 100%;
        height: 100px;
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: #fff;
        border-radius: 8px;
        margin-bottom: 8px;
        overflow: hidden;
    }

    /* 상품 이미지 크기 조정 */
    .favorite-page .product-image {
        max-width: 100%;
        max-height: 100%;
        object-fit: contain;
    }
</style>

<main class="favorite-main">
    <div class="favorite-container">
        <%@ include file="/WEB-INF/views/member/mypage-header.jspf" %>

        <c:if test="${not empty success}">
            <div class="success-message">✅ ${success}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="error-message">❌ ${error}</div>
        </c:if>

        <c:choose>
            <c:when test="${not empty favoriteList.favorites and favoriteList.totalCount > 0}">
                <!-- 즐겨찾기 통계 -->
                <div class="favorite-stats"
                     style="background: var(--pink-4); padding: 12px 16px; border-radius: 12px; text-align:center;">
                    <strong style="color: var(--pink-1);">총 ${favoriteList.totalCount}개 즐겨찾기</strong>
                </div>

                <!-- 즐겨찾기 목록 -->
                <div class="favorite-grid"
                     style="display:grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap:16px; margin-top:16px;">
                    <c:forEach var="favorite" items="${favoriteList.favorites}" varStatus="status">
                        <div class="favorite-item">


                            <div class="product-image-wrapper">
                                <c:if test="${not empty favorite.productPhoto}">
                                    <img src="${favorite.productPhoto}" alt="${favorite.productName}" class="product-image"
                                         onerror="this.src='data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAwIiBoZWlnaHQ9IjIwMCIgdmlld0JveD0iMCAwIDMwMCAyMDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIzMDAiIGhlaWdodD0iMjAwIiBmaWxsPSIjRjVGNUY1Ii8+Cjx0ZXh0IHg9IjE1MCIgeT0iMTAwIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmaWxsPSIjOTk5IiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTQiPkltYWdlPC90ZXh0Pgo8L3N2Zz4='; this.onerror=null;">
                                </c:if>
                                <c:if test="${empty favorite.productPhoto}">
                                    <img src="data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAwIiBoZWlnaHQ9IjIwMCIgdmlld0JveD0iMCAwIDMwMCAyMDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIzMDAiIGhlaWdodD0iMjAwIiBmaWxsPSIjRjVGNUY1Ii8+Cjx0ZXh0IHg9IjE1MCIgeT0iMTAwIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmaWxsPSIjOTk5IiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTQiPk5vIEltYWdlPC90ZXh0Pgo8L3N2Zz4="
                                         alt="No Image" class="product-image">
                                </c:if>
                            </div>

                            <div class="product-name"
                                 style="font-weight:800; font-size:18px; margin:6px 0;">${favorite.productName}</div>
                            <div class="product-content"
                                 style="color:var(--text-2); font-size:14px; margin-bottom:6px; display:-webkit-box; -webkit-line-clamp:2; -webkit-box-orient:vertical; overflow:hidden;">${favorite.productContent}</div>
                            <div class="product-price"
                                 style="font-weight:800; color: var(--pink-1); margin-bottom: 16px;">
                                <fmt:formatNumber value="${favorite.price}" type="currency" currencySymbol="₩"/>
                            </div>

                            <div class="favorite-actions" style="display:flex; gap:8px; justify-content:space-between;">
                                <a href="/products/${favorite.productId}" class="btn btn-ghost">상품 보기</a>
                                <button class="btn btn-primary" onclick="removeFavorite('${favorite.productId}')">즐겨찾기
                                    삭제
                                </button>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <!-- 즐겨찾기가 없는 경우 -->
                <div class="empty-state" style="text-align:center; padding:40px 16px; color:var(--text-2);">
                    <h3 style="color:var(--text-2);">즐겨찾기가 비어있습니다</h3>
                    <p>마음에 드는 상품을 즐겨찾기에 추가해보세요!</p>
                </div>
            </c:otherwise>
        </c:choose>

        <!-- 주문하러 가기 버튼 -->
        <div style="text-align: center; margin: 32px 0 20px;">
            <a class="btn btn-primary" href="/branch/list"
               style="padding: 16px 48px; font-size: 18px; font-weight: 700;">주문하러 가기</a>
        </div>
    </div>
</main>

<script>
    // 즐겨찾기 삭제 함수
    function removeFavorite(productId) {
        if (confirm('정말로 이 상품을 즐겨찾기에서 삭제하시겠습니까?')) {
            fetch('/users/favorites/' + productId, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        alert(data.message);
                        location.reload(); // 페이지 새로고침
                    } else {
                        alert('삭제 실패: ' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('삭제 중 오류가 발생했습니다.');
                });
        }
    }
</script>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
