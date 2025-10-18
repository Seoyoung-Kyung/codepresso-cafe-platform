<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ include file="/WEB-INF/views/common/head.jspf" %>

<style>
    @import url('${pageContext.request.contextPath}/css/productDetail.css');
</style>

<body class="product-detail-page">
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<c:set var="currentCategory" value="${not empty product ? fn:toUpperCase(product.categoryName) : 'ALL'}"/>

<main class="product-page-main product-detail-main">
    <%@ include file="/WEB-INF/views/product/product-category-nav.jspf" %>

    <div class="pdcontainer">

        <div class="section-header">
            <h2 class="section-title">
                <button class="back-btn" onclick="history.back()">←</button>
                메뉴 상세
            </h2>
        </div>

        <!-- 에러 메시지 표시 -->
        <c:if test="${not empty errorMessage}">
            <div class="error-message">
                <strong>오류:</strong> <c:out value="${errorMessage}"/>
            </div>
        </c:if>

        <!-- 메뉴 상세 정보 -->
        <c:if test="${not empty product}">
            <div class="menu-detail-container">
                <div class="detail-main-column">
                    <section class="detail-card summary-card">
                        <div class="summary-header">
                            <div class="menu-image-container">
                                <c:choose>
                                    <c:when test="${not empty product.productPhoto}">
                                        <img src="${product.productPhoto}" alt="${product.productName}"
                                             class="menu-image">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="menu-image no-image">이미지 없음</div>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <div class="menu-content">
                                <div class="menu-info-badge">
                                    <div class="likes" onclick="toggleFavorite()">
                                        <span class="heart" id="favoriteHeart">♡</span>
                                        <span class="like-count" id="favoriteCount">${product.favCount}</span>
                                    </div>
                                    <div class="review-badge" onclick="goToReviews()">리뷰 확인</div>
                                </div>
                                <div class="menu-header">
                                    <h2 class="menu-title">${product.productName}</h2>
                                    <div class="menu-price">
                                        <fmt:formatNumber value="${product.price}" pattern="#,###"/>원
                                    </div>
                                </div>
                                <div class="menu-description">
                                    <p><c:out value="${product.productContent}"/></p>
                                </div>

                                <c:if test="${not empty product.hashtags}">
                                    <div class="category-tags">
                                        <c:forEach var="hashtag" items="${product.hashtags}">
                                            <span class="tag">${hashtag}</span>
                                        </c:forEach>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </section>

                    <c:if test="${not empty product.productOptions}">
                        <section class="detail-card options-section">
                            <header class="detail-card-header">
                                <h3>옵션 선택</h3>
                            </header>
                            <!-- JavaScript로 옵션 그룹핑 처리 -->
                            <script type="text/javascript">
                                // 서버에서 전달받은 옵션 데이터
                                var productOptionsData = [
                                    <c:forEach var="option" items="${product.productOptions}" varStatus="status">
                                    {
                                        productOptionId: ${option.optionId},
                                        optionStyleId: ${option.optionStyleId},
                                        optionName: '${fn:escapeXml(option.optionName)}',
                                        optionStyle: '${fn:escapeXml(option.optionStyleName)}',
                                        extraPrice: ${option.extraPrice}
                                    }<c:if test="${!status.last}">,</c:if>
                                    </c:forEach>
                                ];

                                // 옵션명별로 그룹화
                                var groupedOptions = {};
                                productOptionsData.forEach(function (option) {
                                    if (!groupedOptions[option.optionName]) {
                                        groupedOptions[option.optionName] = [];
                                    }
                                    groupedOptions[option.optionName].push(option);
                                });

                                // DOM 로드 후 옵션 UI 생성
                                document.addEventListener('DOMContentLoaded', function () {
                                    if (typeof createOptionUI === 'function') {
                                        createOptionUI(groupedOptions);
                                        initializeDefaultOptions();
                                    } else {
                                        console.error('createOptionUI 함수가 정의되지 않았습니다.');
                                    }
                                });
                            </script>

                            <!-- 옵션 UI가 동적으로 생성될 컨테이너 -->
                            <div id="dynamic-options-container"></div>
                        </section>
                    </c:if>

                    <!-- 영양정보 섹션 -->
                    <c:if test="${not empty product.nutritionInfo and product.categoryName != 'MD_GOODS'}">
                        <section class="detail-card nutrition-section">
                            <header class="detail-card-header">
                                <h3>영양정보</h3>
                                <span class="serving-info">1회 제공량 기준</span>
                            </header>
                            <div class="nutrition-grid">
                                <div class="nutrition-item">
                                    <span class="nutrition-label">열량(kcal)</span>
                                    <span class="nutrition-value">
                                    <fmt:formatNumber value="${product.nutritionInfo.calories}" pattern="#.#"/>
                                </span>
                                </div>
                                <div class="nutrition-item">
                                    <span class="nutrition-label">나트륨 mg</span>
                                    <span class="nutrition-value">
                                    <fmt:formatNumber value="${product.nutritionInfo.sodium}" pattern="#.##"/>
                                </span>
                                </div>
                                <div class="nutrition-item">
                                    <span class="nutrition-label">단백질 g</span>
                                    <span class="nutrition-value">
                                    <fmt:formatNumber value="${product.nutritionInfo.protein}" pattern="#.##"/>
                                </span>
                                </div>
                                <div class="nutrition-item">
                                    <span class="nutrition-label">당류 g</span>
                                    <span class="nutrition-value">
                                    <fmt:formatNumber value="${product.nutritionInfo.sugar}" pattern="#.#"/>
                                </span>
                                </div>
                                <div class="nutrition-item">
                                    <span class="nutrition-label">지방 g</span>
                                    <span class="nutrition-value">
                                    <fmt:formatNumber value="${product.nutritionInfo.fat}" pattern="#.#"/>
                                </span>
                                </div>
                                <div class="nutrition-item">
                                    <span class="nutrition-label">카페인 mg</span>
                                    <span class="nutrition-value">
                                    <fmt:formatNumber value="${product.nutritionInfo.caffeine}" pattern="#.#"/>
                                </span>
                                </div>
                                <div class="nutrition-item">
                                    <span class="nutrition-label">콜레스테롤 mg</span>
                                    <span class="nutrition-value">
                                    <fmt:formatNumber value="${product.nutritionInfo.cholesterol}" pattern="#"/>
                                </span>
                                </div>
                                <div class="nutrition-item">
                                    <span class="nutrition-label">탄수화물 g</span>
                                    <span class="nutrition-value">
                                    <fmt:formatNumber value="${product.nutritionInfo.carbohydrate}" pattern="#.##"/>
                                </span>
                                </div>
                                <div class="nutrition-item">
                                    <span class="nutrition-label">트랜스지방 g</span>
                                    <span class="nutrition-value">
                                    <fmt:formatNumber value="${product.nutritionInfo.transFat}" pattern="#.#"/>
                                </span>
                                </div>
                                <div class="nutrition-item">
                                    <span class="nutrition-label">포화지방 g</span>
                                    <span class="nutrition-value">
                                    <fmt:formatNumber value="${product.nutritionInfo.saturatedFat}" pattern="#.#"/>
                                </span>
                                </div>
                            </div>
                        </section>
                    </c:if>

                    <!-- 알레르기 유발 정보 섹션 -->
                    <c:if test="${product.categoryName != 'MD_GOODS'}">
                        <section class="detail-card allergen-section">
                            <header class="detail-card-header">
                                <h3>알레르기 유발 정보</h3>
                            </header>
                            <c:choose>
                                <c:when test="${not empty product.allergens}">
                                    <div class="allergen-grid">
                                        <c:forEach var="allergen" items="${product.allergens}">
                                            <div class="allergen-item">
                                                <span class="allergen-name">${allergen}</span>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="no-allergen-message">
                                        <span class="no-allergen-text">알레르기 유발 성분이 없습니다</span>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </section>
                    </c:if>
                </div>

                <!-- 수량 선택 및 주문 버튼 -->
                <aside class="detail-side-column" id="orderCard">
                    <section class="detail-card order-card">
                        <header class="detail-card-header">
                            <h3>총 가격</h3>
                        </header>
                        <div class="quantity-section">
                            <span class="total-price" id="totalPrice">0원</span>
                            <div class="quantity-controls">
                                <button class="quantity-btn minus" onclick="decreaseQuantity()">−</button>
                                <span class="quantity-display" id="quantity">1</span>
                                <button class="quantity-btn plus" onclick="increaseQuantity()">+</button>
                            </div>
                        </div>

                        <div class="action-buttons">
                            <button class="order-btn immediate" onclick="orderImmediately()">바로 주문하기</button>
                            <button class="order-btn cart" onclick="addToCartFromDetail()">담기</button>
                        </div>
                    </section>
                </aside>
            </div>
        </c:if>

        <!-- 상품이 없을 때 -->
        <c:if test="${empty product}">
            <div class="no-product">
                <h3>상품을 찾을 수 없습니다.</h3>
                <p>요청하신 상품이 존재하지 않거나 삭제되었습니다.</p>
                <a href="${pageContext.request.contextPath}/products">→ 메뉴 목록으로 돌아가기</a>
            </div>
        </c:if>
    </div>

    <!-- 성공 메시지 팝업 -->
    <div id="successMessage" class="success-message">장바구니에 담았습니다!</div>

    <script type="text/javascript">
        // 현재 상품 정보
        var currentProduct = {
            id: ${product.productId},
            name: '${fn:escapeXml(product.productName)}',
            price: ${product.price},
            photo: '${product.productPhoto}',
            description: '${fn:escapeXml(product.productContent)}'
        };

        var currentQuantity = 1;
        var selectedOptions = {}; // 선택된 옵션들을 저장
        var totalExtraPrice = 0; // 추가 가격 총합
        var isFavorite = false; // 즐겨찾기 상태
        var currentFavoriteCount = ${product.favCount}; // 현재 즐겨찾기 수

        // 동적으로 옵션 UI 생성
        function createOptionUI(groupedOptions) {
            const container = document.getElementById('dynamic-options-container');

            Object.keys(groupedOptions).forEach(optionName => {
                const options = groupedOptions[optionName];

                // 옵션 그룹 컨테이너 생성
                const optionGroup = document.createElement('div');
                optionGroup.className = 'option-group';

                // 옵션 제목 생성
                const title = document.createElement('h3');
                title.className = 'option-title';
                title.textContent = optionName;
                optionGroup.appendChild(title);

                if (optionName === '온도') {
                    // 온도 옵션 특별 UI
                    createTemperatureUI(optionGroup, options, optionName);
                } else {
                    // 일반 옵션 UI
                    createGeneralOptionUI(optionGroup, options, optionName);
                }

                container.appendChild(optionGroup);
            });
        }

        // 온도 옵션 UI 생성
        function createTemperatureUI(container, options, optionName) {
            // 온도 버튼들
            const tempButtons = document.createElement('div');
            tempButtons.className = 'temp-buttons';

            // 기본 선택 로직: ICE만 있으면 ICE 선택, 그렇지 않으면 두 번째 옵션 선택
            let defaultIndex = options.length === 1 && options[0].optionStyle === 'ICE' ? 0 : 1;

            options.forEach((option, index) => {
                const button = document.createElement('button');
                button.className = 'temp-btn' + (index === defaultIndex ? ' active' : '');
                button.dataset.productOptionId = option.productOptionId;
                button.dataset.optionId = option.optionStyleId;
                button.dataset.price = option.extraPrice;
                button.textContent = option.optionStyle;
                button.onclick = () => selectOption(button, optionName);
                tempButtons.appendChild(button);
            });

            container.appendChild(tempButtons);

        }

        // 일반 옵션 UI 생성
        function createGeneralOptionUI(container, options, optionName) {
            const optionButtons = document.createElement('div');
            optionButtons.className = 'option-buttons-grid';

            // 텀블러 관련 옵션인지 확인
            const isTumblerOption = optionName.includes('텀블러') ||
                options.some(opt => opt.optionStyle && opt.optionStyle.includes('텀블러'));

            options.forEach((option, index) => {
                const button = document.createElement('button');
                // 텀블러 옵션은 기본 선택하지 않음, 다른 옵션은 첫 번째를 기본 선택
                button.className = 'option-btn-card' + (!isTumblerOption && index === 0 ? ' active' : '');
                button.dataset.productOptionId = option.productOptionId;
                button.dataset.optionId = option.optionStyleId;
                button.dataset.price = option.extraPrice;
                button.dataset.isTumbler = isTumblerOption;
                button.onclick = () => isTumblerOption ? selectTumblerOption(button, optionName) : selectOptionCard(button, optionName);

                const optionText = document.createElement('div');
                optionText.className = 'option-text';
                optionText.textContent = option.optionStyle;
                button.appendChild(optionText);

                if (option.extraPrice > 0) {
                    const priceSpan = document.createElement('div');
                    priceSpan.className = 'extra-price-card';
                    priceSpan.textContent = '(+' + option.extraPrice.toLocaleString() + '원)';
                    button.appendChild(priceSpan);
                }

                optionButtons.appendChild(button);
            });

            container.appendChild(optionButtons);
        }

        // 텀블러 옵션 토글 함수
        function selectTumblerOption(button, optionName) {
            // 텀블러 옵션은 토글 가능 (on/off)
            const isCurrentlySelected = button.classList.contains('active');

            if (isCurrentlySelected) {
                // 현재 선택되어 있으면 해제
                button.classList.remove('active');
                delete selectedOptions[optionName];
            } else {
                // 현재 선택되어 있지 않으면 선택
                button.classList.add('active');
                selectedOptions[optionName] = {
                    optionId: button.dataset.optionId,
                    optionName: optionName,
                    optionValue: button.textContent.trim().split('(')[0].trim(),
                    extraPrice: parseInt(button.dataset.price) || 0
                };
            }

            // 총 가격 재계산
            calculateTotalPrice();
        }

        // 카드형 옵션 선택 함수
        function selectOptionCard(button, optionName) {
            const optionGroup = button.closest('.option-group');
            const buttons = optionGroup.querySelectorAll('.option-btn-card, .temp-btn');

            // 같은 그룹 내 다른 버튼들 비활성화
            buttons.forEach(btn => btn.classList.remove('active'));

            // 클릭된 버튼 활성화
            button.classList.add('active');

            // 온도 옵션의 경우 temp-detail도 업데이트
            if (optionName === '온도') {
                const tempOptions = optionGroup.querySelectorAll('.temp-option');
                tempOptions.forEach(option => option.classList.remove('active'));

                const selectedTemp = button.textContent.trim();
                const targetOption = optionGroup.querySelector('[data-option="' + selectedTemp + '"]');
                if (targetOption) {
                    targetOption.classList.add('active');
                }

                // 주문 섹션의 온도 표시 업데이트
                const optionTempElement = document.querySelector('.option-temp');
                if (optionTempElement) {
                    optionTempElement.textContent = selectedTemp;
                }
            }

            // 선택된 옵션 저장
            selectedOptions[optionName] = {
                optionId: button.dataset.optionId,
                optionName: optionName,
                optionValue: button.textContent.trim().split('(')[0].trim(), // 가격 부분 제거
                extraPrice: parseInt(button.dataset.price) || 0
            };

            // 총 추가 가격 계산
            calculateTotalPrice();
        }

        // 기본 옵션 초기화
        function initializeDefaultOptions() {
            console.log('=== 기본 옵션 초기화 시작 ===');

            const optionGroups = document.querySelectorAll('.option-group');
            console.log('찾은 옵션 그룹 수:', optionGroups.length);

            optionGroups.forEach(group => {
                const firstButton = group.querySelector('.option-btn.active, .temp-btn.active, .option-btn-card.active');
                if (firstButton) {
                    const optionTitle = group.querySelector('.option-title').textContent;
                    // 텀블러 옵션은 기본 초기화에서 제외
                    const isTumblerOption = firstButton.dataset.isTumbler === 'true';

                    if (!isTumblerOption) {
                        selectedOptions[optionTitle] = {
                            optionId: firstButton.dataset.optionId,
                            optionName: optionTitle,
                            optionValue: firstButton.textContent.trim().split('+')[0].trim(),
                            extraPrice: parseInt(firstButton.dataset.price) || 0
                        };
                        console.log('기본 옵션 설정:', optionTitle, selectedOptions[optionTitle]);
                    }
                }
            });

            console.log('선택된 기본 옵션들:', selectedOptions);
            calculateTotalPrice();
        }

        // 옵션 선택 함수 (기존)
        function selectOption(button, optionName) {
            selectOptionCard(button, optionName);
        }

        // 총 가격 계산
        function calculateTotalPrice() {
            totalExtraPrice = 0;
            Object.values(selectedOptions).forEach(option => {
                totalExtraPrice += option.extraPrice;
            });

            const finalPrice = (currentProduct.price + totalExtraPrice) * currentQuantity;
            const totalPriceElement = document.getElementById('totalPrice');
            if (totalPriceElement) {
                totalPriceElement.textContent = finalPrice.toLocaleString() + '원';
            }

            // 주문 섹션의 개별 가격도 업데이트
            const optionPriceElement = document.querySelector('.option-price');
            if (optionPriceElement) {
                optionPriceElement.textContent = (currentProduct.price + totalExtraPrice).toLocaleString() + '원';
            }
        }

        // 수량 증가
        function increaseQuantity() {
            currentQuantity++;
            document.getElementById('quantity').textContent = currentQuantity;
            const totalQuantityElement = document.getElementById('totalQuantity');
            if (totalQuantityElement) {
                totalQuantityElement.textContent = currentQuantity + '개';
            }
            calculateTotalPrice();
        }

        // 수량 감소
        function decreaseQuantity() {
            if (currentQuantity > 1) {
                currentQuantity--;
                document.getElementById('quantity').textContent = currentQuantity;
                const totalQuantityElement = document.getElementById('totalQuantity');
                if (totalQuantityElement) {
                    totalQuantityElement.textContent = currentQuantity + '개';
                }
                calculateTotalPrice();
            }
        }

        // 현재 즐겨찾기 상태 확인
        function checkFavoriteStatus() {
            <c:if test="${pageContext.request.userPrincipal != null}">
            console.log('=== 즐겨찾기 상태 확인 시작 ===');

            fetch('${pageContext.request.contextPath}/users/favorites', {
                method: 'GET'
            })
                .then(response => {
                    console.log('즐겨찾기 목록 응답 상태:', response.status);
                    if (response.ok) {
                        return response.json();
                    }
                    throw new Error(`즐겨찾기 목록을 가져올 수 없습니다. 상태: ${response.status}`);
                })
                .then(data => {
                    console.log('즐겨찾기 목록 응답 데이터:', data);

                    // FavoriteListResponse 구조에 맞게 수정
                    if (data && data.favorites && Array.isArray(data.favorites)) {
                        console.log('즐겨찾기 목록:', data.favorites);
                        console.log('현재 상품 ID:', currentProduct.id);

                        // 현재 상품이 즐겨찾기 목록에 있는지 확인
                        isFavorite = data.favorites.some(favorite => {
                            console.log('비교:', favorite.productId, '===', currentProduct.id);
                            return favorite.productId === currentProduct.id;
                        });

                        console.log('즐겨찾기 상태:', isFavorite);
                        updateFavoriteUI();
                    } else {
                        console.log('즐겨찾기 목록이 없거나 형식이 잘못됨');
                        isFavorite = false;
                        updateFavoriteUI();
                    }
                })
                .catch(error => {
                    console.error('즐겨찾기 상태 확인 실패:', error);
                    // 에러 시 기본값으로 설정
                    isFavorite = false;
                    updateFavoriteUI();
                });
            </c:if>
        }

        // 페이지 로드 시 초기화 - 중복 제거하고 통합
        document.addEventListener('DOMContentLoaded', function () {
            console.log('=== 페이지 초기화 시작 ===');

            // 즐겨찾기 상태 확인
            checkFavoriteStatus();

            // 즐겨찾기 수 포맷팅 적용
            const initialFavCount = ${product.favCount};
            updateFavoriteCount(initialFavCount);

            // 초기 가격 계산 (옵션 UI 생성 후 initializeDefaultOptions에서 처리됨)
            calculateTotalPrice();
        });

        // 선택된 옵션 ID들을 수집하는 함수
        function getSelectedOptionIds() {
            let optionIds = [];
            // 각 옵션 그룹에서 선택된 버튼의 productOptionId 수집
            document.querySelectorAll('.option-group .active').forEach(activeBtn => {
                if (activeBtn.dataset.productOptionId) {
                    optionIds.push(parseInt(activeBtn.dataset.productOptionId));
                }
            });
            return optionIds;
        }

        // 장바구니에 담기
        function addToCartFromDetail() {
            console.log('=== 장바구니 추가 디버깅 ===');
            console.log('currentProduct:', currentProduct);
            console.log('currentQuantity:', currentQuantity);

            const selectedOptionIds = getSelectedOptionIds();
            console.log('selectedOptionIds:', selectedOptionIds);

            const formData = new FormData();
            formData.append('productId', currentProduct.id);
            formData.append('quantity', currentQuantity);

            // 선택된 옵션 ID들 추가
            selectedOptionIds.forEach(id => {
                formData.append('optionIds', id);
            });

            // FormData 내용 확인
            console.log('FormData 내용:');
            for (let [key, value] of formData.entries()) {
                console.log(key + ': ' + value);
            }

            // API 호출
            fetch('${pageContext.request.contextPath}/users/cart', {
                method: 'POST',
                body: formData
            })
                .then(response => {
                    console.log('응답 상태:', response.status);
                    console.log('응답 헤더:', response.headers);

                    if (response.ok) {
                        return response.json();
                    } else {
                        // 에러 응답 내용도 확인
                        return response.text().then(text => {
                            console.error('에러 응답 내용:', text);
                            throw new Error(`HTTP ${response.status}: ${text}`);
                        });
                    }
                })
                .then(data => {
                    console.log('장바구니 추가 성공:', data);
                    window.dispatchEvent(new Event('cartUpdated'));
                    showSuccessMessage('장바구니에 ' + currentProduct.name + ' ' + currentQuantity + '개를 담았습니다.');
                })
                .catch(error => {
                    console.error('장바구니 추가 실패:', error);
                    showSuccessMessage('장바구니 추가 중 오류가 발생했습니다: ' + error.message);
                });
        }

        // 성공 메시지 표시
        function showSuccessMessage(message) {
            const messageElement = document.getElementById('successMessage');
            messageElement.textContent = message;
            messageElement.style.display = 'block';

            setTimeout(() => {
                messageElement.style.display = 'none';
            }, 3000);
        }

        // 즐겨찾기 토글 함수
        function toggleFavorite() {
            // 로그인 상태 확인
            <c:choose>
            <c:when test="${pageContext.request.userPrincipal != null}">
            if (isFavorite) {
                // 즐겨찾기 제거
                removeFavorite();
            } else {
                // 즐겨찾기 추가
                addFavorite();
            }
            </c:when>
            <c:otherwise>
            alert('로그인이 필요한 서비스입니다.');
            return;
            </c:otherwise>
            </c:choose>
        }

        // CSRF 토큰 가져오기
        function getCSRFToken() {
            const token = document.querySelector('meta[name="_csrf"]');
            const header = document.querySelector('meta[name="_csrf_header"]');

            const tokenValue = token ? token.getAttribute('content') : null;
            const headerName = header ? header.getAttribute('content') : 'X-CSRF-TOKEN';

            console.log('🔑 CSRF 토큰 상태:', {
                tokenExists: !!tokenValue,
                headerName: headerName,
                token: tokenValue ? tokenValue.substring(0, 10) + '...' : 'null'
            });

            return {
                token: tokenValue,
                header: headerName
            };
        }

        // 즐겨찾기 추가
        function addFavorite() {
            console.log('=== 즐겨찾기 추가 시작 ===');
            console.log('상품 ID:', currentProduct.id);

            const requestData = {
                productId: currentProduct.id
            };

            const csrf = getCSRFToken();
            const headers = {
                'Content-Type': 'application/json'
            };

            // CSRF 토큰이 있으면 헤더에 추가
            if (csrf.token) {
                headers[csrf.header] = csrf.token;
                console.log('✅ CSRF 토큰 추가됨:', csrf.header);
            } else {
                console.warn('⚠️ CSRF 토큰이 없습니다. 요청이 실패할 수 있습니다.');
            }

            console.log('요청 데이터:', requestData);
            console.log('요청 헤더:', headers);

            fetch('${pageContext.request.contextPath}/users/favorites', {
                method: 'POST',
                headers: headers,
                body: JSON.stringify(requestData)
            })
                .then(response => {
                    console.log('응답 상태:', response.status, response.statusText);
                    if (response.ok) {
                        return response.json();
                    } else {
                        return response.text().then(text => {
                            console.error('즐겨찾기 추가 실패:', text);

                            // HTTP 상태 코드별 에러 메시지
                            let errorMessage;
                            switch (response.status) {
                                case 401:
                                    errorMessage = '로그인이 필요합니다.';
                                    break;
                                case 403:
                                    errorMessage = 'CSRF 토큰 오류입니다. 페이지를 새로고침한 후 다시 시도해주세요.';
                                    break;
                                case 404:
                                    errorMessage = '상품을 찾을 수 없습니다.';
                                    break;
                                case 500:
                                    errorMessage = '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
                                    break;
                                default:
                                    errorMessage = `요청 실패 (${response.status}): ${text}`;
                            }

                            throw new Error(errorMessage);
                        });
                    }
                })
                .then(data => {
                    console.log('즐겨찾기 추가 응답:', data);

                    // AuthResponse 구조: { success: boolean, message: string }
                    if (data.success === true) {
                        console.log('즐겨찾기 추가 성공');
                        isFavorite = true;
                        currentFavoriteCount++; // 카운트 증가
                        updateFavoriteCount(currentFavoriteCount); // UI 업데이트
                        updateFavoriteUI();
                        showSuccessMessage('즐겨찾기에 추가되었습니다.');
                    } else {
                        console.log('즐겨찾기 추가 실패:', data.message);
                        showSuccessMessage(data.message || '즐겨찾기 추가 중 오류가 발생했습니다.');
                    }
                })
                .catch(error => {
                    console.error('즐겨찾기 추가 실패:', error);

                    // 네트워크 오류 vs API 응답 오류 구분
                    if (error.message.includes('Failed to fetch') || error.message.includes('NetworkError')) {
                        showSuccessMessage('네트워크 연결을 확인해주세요.');
                    } else {
                        showSuccessMessage(error.message);
                    }
                });
        }

        // 즐겨찾기 제거
        function removeFavorite() {
            console.log('=== 즐겨찾기 제거 시작 ===');
            console.log('상품 ID:', currentProduct.id);

            const csrf = getCSRFToken();
            const headers = {};

            // CSRF 토큰이 있으면 헤더에 추가
            if (csrf.token) {
                headers[csrf.header] = csrf.token;
                console.log('✅ CSRF 토큰 추가됨:', csrf.header);
            } else {
                console.warn('⚠️ CSRF 토큰이 없습니다. 요청이 실패할 수 있습니다.');
            }

            console.log('요청 헤더:', headers);

            fetch('${pageContext.request.contextPath}/users/favorites/' + currentProduct.id, {
                method: 'DELETE',
                headers: headers
            })
                .then(response => {
                    console.log('응답 상태:', response.status, response.statusText);
                    if (response.ok) {
                        return response.json();
                    } else {
                        return response.text().then(text => {
                            console.error('즐겨찾기 제거 실패:', text);

                            // HTTP 상태 코드별 에러 메시지
                            let errorMessage;
                            switch (response.status) {
                                case 401:
                                    errorMessage = '로그인이 필요합니다.';
                                    break;
                                case 403:
                                    errorMessage = 'CSRF 토큰 오류입니다. 페이지를 새로고침한 후 다시 시도해주세요.';
                                    break;
                                case 404:
                                    errorMessage = '상품이나 즐겨찾기를 찾을 수 없습니다.';
                                    break;
                                case 500:
                                    errorMessage = '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
                                    break;
                                default:
                                    errorMessage = `요청 실패 (${response.status}): ${text}`;
                            }

                            throw new Error(errorMessage);
                        });
                    }
                })
                .then(data => {
                    console.log('즐겨찾기 제거 응답:', data);

                    // AuthResponse 구조: { success: boolean, message: string }
                    if (data.success === true) {
                        console.log('즐겨찾기 제거 성공');
                        isFavorite = false;
                        currentFavoriteCount--; // 카운트 감소
                        updateFavoriteCount(currentFavoriteCount); // UI 업데이트
                        updateFavoriteUI();
                        showSuccessMessage('즐겨찾기에서 제거되었습니다.');
                    } else {
                        console.log('즐겨찾기 제거 실패:', data.message);
                        showSuccessMessage(data.message || '즐겨찾기 제거 중 오류가 발생했습니다.');
                    }
                })
                .catch(error => {
                    console.error('즐겨찾기 제거 실패:', error);

                    // 네트워크 오류 vs API 응답 오류 구분
                    if (error.message.includes('Failed to fetch') || error.message.includes('NetworkError')) {
                        showSuccessMessage('네트워크 연결을 확인해주세요.');
                    } else {
                        showSuccessMessage(error.message);
                    }
                });
        }

        // 즐겨찾기 UI 업데이트
        function updateFavoriteUI() {
            const heartElement = document.getElementById('favoriteHeart');
            if (isFavorite) {
                heartElement.textContent = '♥';
                heartElement.style.color = '#ff69b4';
            } else {
                heartElement.textContent = '♡';
                heartElement.style.color = '#ff69b4';
            }
        }

        // 검색 모달 관련 함수
        function showSearchModal() {
            document.getElementById('searchModal').style.display = 'block';
        }

        function hideSearchModal() {
            document.getElementById('searchModal').style.display = 'none';
        }

        // 장바구니 토글
        function toggleCart() {
            if (typeof window.toggleCartHandler === 'function') {
                window.toggleCartHandler();
            }
        }

        // 주문하기
        function placeOrder() {
            if (typeof window.placeOrderHandler === 'function') {
                window.placeOrderHandler();
            }
        }

        // 모달 외부 클릭시 닫기
        window.onclick = function (event) {
            const modal = document.getElementById('searchModal');
            if (event.target == modal) {
                modal.style.display = 'none';
            }
        }

        // 숫자 포맷팅 함수 (1000+ → 1천)
        function formatNumber(num) {
            if (num >= 10000) {
                return (num / 10000).toFixed(1) + '만';
            } else if (num >= 1000) {
                return (num / 1000).toFixed(1) + '천';
            }
            return num.toString();
        }

        // 즐겨찾기 수 업데이트 함수
        function updateFavoriteCount(count) {
            const favoriteCountElement = document.getElementById('favoriteCount');
            if (favoriteCountElement) {
                favoriteCountElement.textContent = formatNumber(count);
            }
        }

        // 리뷰 페이지로 이동
        function goToReviews() {
            window.location.href = '${pageContext.request.contextPath}/products/' + currentProduct.id + '/reviews';
        }

        // 바로 주문하기: 숨은 폼 POST로 checkout.jsp 이동 (/payments/direct)
        function orderImmediately() {
            const form = document.getElementById('directForm');
            if (!form) return;
            // 수량 적용
            const qtyInput = form.querySelector('input[name="quantity"]');
            if (qtyInput) qtyInput.value = currentQuantity;
            // 옵션 숨은 필드 재구성
            const selectedOptionIds = getSelectedOptionIds();
            form.querySelectorAll('input[name="optionIds"]').forEach(n => n.remove());
            selectedOptionIds.forEach(id => {
                const i = document.createElement('input');
                i.type = 'hidden';
                i.name = 'optionIds';
                i.value = String(id);
                form.appendChild(i);
            });
            
            // 선택된 매장 ID 추가
            let branchId = 1; // 기본값
            try {
                const selected = window.branchSelection && window.branchSelection.load 
                    ? window.branchSelection.load() 
                    : null;
                if (selected && selected.id) {
                    branchId = parseInt(selected.id);
                }
            } catch(e) {
                console.log('매장 선택 정보를 가져올 수 없습니다:', e);
            }
            
            // 기존 branchId 필드 제거 후 새로 추가
            form.querySelectorAll('input[name="branchId"]').forEach(n => n.remove());
            const branchIdInput = document.createElement('input');
            branchIdInput.type = 'hidden';
            branchIdInput.name = 'branchId';
            branchIdInput.value = String(branchId);
            form.appendChild(branchIdInput);
            
            console.log('선택된 매장 ID:', branchId);
            form.submit();
        }

        const card = document.querySelector('.detail-side-column');
        let lastScrollY = 0;

        window.addEventListener('scroll', () => {
            const scrollY = window.scrollY;
            const offset = Math.min(scrollY, 200); // 상한선 설정 (너무 많이 내려가지 않게)
            card.style.transform = `translateY(${offset}px)`;
            lastScrollY = scrollY;
        });

    </script>
    <script src="${pageContext.request.contextPath}/js/categoryNav.js"></script>

    <!-- 바로 주문하기 폼 (숨김) -->
    <form id="directForm" method="post" action="${pageContext.request.contextPath}/payments/direct"
          style="display:none;">
        <input type="hidden" name="productId" value="${product.productId}"/>
        <input type="hidden" name="quantity" value="1"/>
        <c:if test="${not empty _csrf}">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </c:if>
    </form>
</main>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>