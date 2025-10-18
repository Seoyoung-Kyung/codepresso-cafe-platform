<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ include file="/WEB-INF/views/common/head.jspf" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/productSearch.css">

<body class="product-search-page">
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<main class="search-page-main">
    <%@ include file="/WEB-INF/views/product/product-category-nav.jspf" %>
    <div class="search-container">

        <!-- 검색 헤더 -->
        <div class="search-header">
            <h1 class="page-title">메뉴 검색</h1>
        </div>

        <!-- 탭 네비게이션 -->
        <div class="search-tabs">
            <button class="tab-btn active" onclick="switchTab('keyword')">검색어로 찾기</button>
            <button class="tab-btn" onclick="switchTab('feature')">특징으로 찾기</button>
        </div>

        <!-- 검색어로 찾기 탭 -->
        <div id="keywordTab" class="tab-content active">
            <!-- 검색 입력 -->
            <div class="search-input-wrapper">
                <input type="text"
                       id="searchInput"
                       class="search-input"
                       placeholder="찾고 싶은 메뉴를 검색하세요"
                       onkeypress="handleSearchKeyPress(event)">
                <button class="search-btn" onclick="performSearch()">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="11" cy="11" r="8"></circle>
                        <path d="m21 21-4.35-4.35"></path>
                    </svg>
                </button>
            </div>

            <!-- 최근 검색어 -->
            <div class="recent-searches" id="recentSearches">
                <h3 class="section-subtitle">최근 검색</h3>
                <div class="search-tags" id="recentSearchTags">
                    <!-- JavaScript로 동적 생성 -->
                </div>
            </div>

            <!-- 랜덤 추천 섹션 -->
            <div class="random-recommend-section">
                <div class="recommend-header">
                    <h3 class="section-subtitle">고민되면 제가 추천해드릴까요?</h3>
                    <div class="mascot-icon">
                        <picture>
                            <source srcset="/banners/mascot-small.webp" type="image/webp">
                            <img src="/banners/mascot.png" alt="Mascot" loading="lazy" />
                        </picture>
                    </div>
                </div>
                <button class="recommend-btn" onclick="getRandomRecommendation()">
                    다른 걸로 추천해줘!
                </button>
            </div>
        </div>

        <!-- 특징으로 찾기 탭 -->
        <div id="featureTab" class="tab-content">
            <div class="feature-filters">

                <!-- 음료 필터 -->
                <div class="filter-group">
                    <h3 class="filter-title">
                        <span>음료</span>
                        <button class="reset-btn" onclick="resetFilter('beverage')">초기화 ↻</button>
                    </h3>
                    <div class="filter-options">
                        <button class="filter-tag" data-category="beverage" data-value="에스프레소" onclick="toggleFilter(this)">에스프레소</button>
                        <button class="filter-tag" data-category="beverage" data-value="콜드브루" onclick="toggleFilter(this)">콜드브루</button>
                        <button class="filter-tag" data-category="beverage" data-value="커피믹스" onclick="toggleFilter(this)">커피믹스</button>
                        <button class="filter-tag" data-category="beverage" data-value="디카페인(노카페인)" onclick="toggleFilter(this)">디카페인(노카페인)</button>
                        <button class="filter-tag" data-category="beverage" data-value="카페인" onclick="toggleFilter(this)">카페인</button>
                        <button class="filter-tag" data-category="beverage" data-value="우유" onclick="toggleFilter(this)">우유</button>
                        <button class="filter-tag" data-category="beverage" data-value="크림" onclick="toggleFilter(this)">크림</button>
                        <button class="filter-tag" data-category="beverage" data-value="요거트" onclick="toggleFilter(this)">요거트</button>
                        <button class="filter-tag" data-category="beverage" data-value="아이스블렌디드" onclick="toggleFilter(this)">아이스 블렌디드</button>
                        <button class="filter-tag" data-category="beverage" data-value="탄산" onclick="toggleFilter(this)">탄산</button>
                        <button class="filter-tag" data-category="beverage" data-value="토핑" onclick="toggleFilter(this)">토핑</button>
                        <button class="filter-tag" data-category="beverage" data-value="펄포함" onclick="toggleFilter(this)">펄포함</button>
                        <button class="filter-tag" data-category="beverage" data-value="홍차" onclick="toggleFilter(this)">홍차</button>
                        <button class="filter-tag" data-category="beverage" data-value="휘핑크림" onclick="toggleFilter(this)">휘핑크림</button>
                        <button class="filter-tag" data-category="beverage" data-value="Teabag" onclick="toggleFilter(this)">Tea Bag</button>
                    </div>
                </div>

                <!-- 온도 필터 -->
                <div class="filter-group">
                    <h3 class="filter-title">
                        <span>온도</span>
                        <button class="reset-btn" onclick="resetFilter('temperature')">초기화 ↻</button>
                    </h3>
                    <div class="filter-options">
                        <button class="filter-tag" data-category="temperature" data-value="HOT" onclick="toggleFilter(this)">HOT</button>
                        <button class="filter-tag" data-category="temperature" data-value="ICE" onclick="toggleFilter(this)">ICE</button>
                    </div>
                </div>

                <!-- 맛 필터 -->
                <div class="filter-group">
                    <h3 class="filter-title">
                        <span>맛</span>
                        <button class="reset-btn" onclick="resetFilter('taste')">초기화 ↻</button>
                    </h3>
                    <div class="filter-options">
                        <button class="filter-tag" data-category="taste" data-value="단맛" onclick="toggleFilter(this)">단맛</button>
                        <button class="filter-tag" data-category="taste" data-value="달지않음" onclick="toggleFilter(this)">달지 않음</button>
                        <button class="filter-tag" data-category="taste" data-value="고소한" onclick="toggleFilter(this)">고소한</button>
                        <button class="filter-tag" data-category="taste" data-value="신맛" onclick="toggleFilter(this)">신맛</button>
                        <button class="filter-tag" data-category="taste" data-value="쓴맛" onclick="toggleFilter(this)">쓴맛</button>
                        <button class="filter-tag" data-category="taste" data-value="짠맛" onclick="toggleFilter(this)">짠맛</button>
                        <button class="filter-tag" data-category="taste" data-value="매운맛" onclick="toggleFilter(this)">매운맛</button>
                    </div>
                </div>

                <!-- 과일류 필터 -->
                <div class="filter-group">
                    <h3 class="filter-title">
                        <span>과일류</span>
                        <button class="reset-btn" onclick="resetFilter('fruit')">초기화 ↻</button>
                    </h3>
                    <div class="filter-options">
                        <button class="filter-tag" data-category="fruit" data-value="과일" onclick="toggleFilter(this)">과일</button>
                        <button class="filter-tag" data-category="fruit" data-value="과육" onclick="toggleFilter(this)">과육</button>
                    </div>
                </div>

                <!-- 브랜드/디저트/세트 필터 -->
                <div class="filter-group">
                    <h3 class="filter-title">
                        <span>브랜드/디저트/세트</span>
                        <button class="reset-btn" onclick="resetFilter('brand')">초기화 ↻</button>
                    </h3>
                    <div class="filter-options">
                        <button class="filter-tag" data-category="brand" data-value="핫 디저트" onclick="toggleFilter(this)">핫 디저트</button>
                        <button class="filter-tag" data-category="brand" data-value="콜드 디저트" onclick="toggleFilter(this)">콜드 디저트</button>
                        <button class="filter-tag" data-category="brand" data-value="디저트류" onclick="toggleFilter(this)">디저트류</button>
                        <button class="filter-tag" data-category="brand" data-value="빵류" onclick="toggleFilter(this)">빵류</button>
                        <button class="filter-tag" data-category="brand" data-value="세트" onclick="toggleFilter(this)">세트</button>
                        <button class="filter-tag" data-category="brand" data-value="식사대용" onclick="toggleFilter(this)">식사대용</button>
                    </div>
                </div>

                <!-- 필터 적용 버튼 -->
                <div class="filter-actions">
                    <button class="apply-filter-btn" onclick="applyFilters()">선택한 특징으로 찾기</button>
                </div>
            </div>
        </div>

        <!-- 검색 결과 섹션 -->
        <div class="search-results" id="searchResults">
            <!-- 검색 전 상태 -->
            <div class="empty-state" id="emptyState">
                <p class="empty-message">찾고 싶은 메뉴를 검색하거나<br>특징을 선택해주세요!</p>
            </div>

            <!-- 검색 결과 리스트 (JavaScript로 동적 생성) -->
            <div class="results-list" id="resultsList" style="display: none;">
                <!-- 결과가 여기에 동적으로 추가됩니다 -->
            </div>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>

<script>
    // contextPath 전역 변수
    const contextPath = '${pageContext.request.contextPath}';
</script>

<script src="${pageContext.request.contextPath}/js/categoryNav.js" defer></script>
<script src="${pageContext.request.contextPath}/js/productSearch.js" defer></script>

</body>
</html>