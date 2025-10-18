// ================================================
// PRODUCT SEARCH PAGE JAVASCRIPT
// ================================================

// 전역 변수
let currentTab = 'keyword';
let recentSearches = [];
let selectedFilters = {
    beverage: [],
    temperature: [],
    taste: [],
    fruit: [],
    brand: []
};

// ================================================
// 1. 초기화 함수
// ================================================
function initializeSearch() {
    loadRecentSearches();
}

// ================================================
// 2. 탭 전환
// ================================================
function switchTab(tabName) {
    currentTab = tabName;

    // 탭 버튼 활성화 상태 변경
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');

    // 탭 컨텐츠 표시/숨김
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.remove('active');
    });

    if (tabName === 'keyword') {
        document.getElementById('keywordTab').classList.add('active');
    } else {
        document.getElementById('featureTab').classList.add('active');
    }

    // 검색 결과 초기화
    resetSearchResults();
}

// ================================================
// 3. 검색 기능
// ================================================
function performSearch() {
    const searchInput = document.getElementById('searchInput');
    const keyword = searchInput.value.trim();

    if (keyword === '') {
        // 검색어가 없으면 초기 상태로 되돌림
        resetSearchResults();
        return;
    }

    // 최근 검색어에 추가
    addToRecentSearches(keyword);

    // 검색 실행
    searchProducts(keyword);

    // 검색 후 입력창 비우기
    searchInput.value = '';
}

function handleSearchKeyPress(event) {
    if (event.key === 'Enter') {
        performSearch();
    }
}

async function searchProducts(keyword) {
    // 랜덤 추천 섹션 숨기기
    hideRecommendSection();

    try {
        // API 호출
        const formData = new URLSearchParams();
        formData.append('keyword', keyword);

        const response = await fetch(`${contextPath}/api/products/search/keyword`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: formData
        });

        if (!response.ok) {
            throw new Error('검색 요청에 실패했습니다.');
        }

        const results = await response.json();
        displaySearchResults(results);
    } catch (error) {
        console.error('검색 오류:', error);
        alert('검색 중 오류가 발생했습니다.');
    }
}

// ================================================
// 4. 최근 검색어 관리
// ================================================
function loadRecentSearches() {
    const saved = localStorage.getItem('recentSearches');
    if (saved) {
        recentSearches = JSON.parse(saved);
        displayRecentSearches();
    }
}

function addToRecentSearches(keyword) {
    // 중복 제거
    recentSearches = recentSearches.filter(item => item !== keyword);

    // 맨 앞에 추가
    recentSearches.unshift(keyword);

    // 최대 10개까지만 유지
    if (recentSearches.length > 10) {
        recentSearches = recentSearches.slice(0, 10);
    }

    // 로컬 스토리지에 저장
    localStorage.setItem('recentSearches', JSON.stringify(recentSearches));

    displayRecentSearches();
}

function displayRecentSearches() {
    const container = document.getElementById('recentSearchTags');

    if (recentSearches.length === 0) {
        container.innerHTML = '<p style="color: #999; font-size: 14px;">최근 검색어가 없습니다.</p>';
        return;
    }

    container.innerHTML = recentSearches.map(keyword => `
        <span class="search-tag" onclick="searchFromRecent('${keyword}')">
            ${keyword}
            <span class="remove-tag" onclick="removeRecentSearch(event, '${keyword}')">×</span>
        </span>
    `).join('');
}

function searchFromRecent(keyword) {
    document.getElementById('searchInput').value = keyword;
    searchProducts(keyword);
}

function removeRecentSearch(event, keyword) {
    event.stopPropagation();
    recentSearches = recentSearches.filter(item => item !== keyword);
    localStorage.setItem('recentSearches', JSON.stringify(recentSearches));
    displayRecentSearches();
}

// ================================================
// 5. 랜덤 추천
// ================================================
async function getRandomRecommendation() {
    try {
        // 랜덤 상품 4개 조회
        const response = await fetch(`${contextPath}/api/products/random`);

        if (!response.ok) {
            throw new Error('상품 조회에 실패했습니다.');
        }

        // API가 List<ProductListResponse> 배열을 반환
        const randomProducts = await response.json();

        if (!randomProducts || randomProducts.length === 0) {
            alert('추천할 상품이 없습니다.');
            return;
        }

        displaySearchResults(randomProducts);

        // 추천 버튼이 헤더에 가리지 않도록 추천 버튼 섹션으로 스크롤
        const recommendSection = document.querySelector('.random-recommend-section');
        const headerHeight = 100; // 헤더 높이만큼 여유 공간
        const elementPosition = recommendSection.getBoundingClientRect().top;
        const offsetPosition = elementPosition + window.pageYOffset - headerHeight;

        window.scrollTo({
            top: offsetPosition,
            behavior: 'smooth'
        });
    } catch (error) {
        console.error('랜덤 추천 오류:', error);
        alert('추천 중 오류가 발생했습니다.');
    }
}

// ================================================
// 6. 필터 관리
// ================================================
function toggleFilter(element) {
    const category = element.dataset.category;
    const value = element.dataset.value;

    element.classList.toggle('selected');

    if (element.classList.contains('selected')) {
        if (!selectedFilters[category].includes(value)) {
            selectedFilters[category].push(value);
        }
    } else {
        selectedFilters[category] = selectedFilters[category].filter(item => item !== value);
    }
}

function resetFilter(category) {
    selectedFilters[category] = [];

    // UI 업데이트
    document.querySelectorAll(`.filter-tag[data-category="${category}"]`).forEach(tag => {
        tag.classList.remove('selected');
    });
}

async function applyFilters() {
    // 선택된 필터가 없으면 경고
    const hasSelectedFilters = Object.values(selectedFilters).some(arr => arr.length > 0);
    if (!hasSelectedFilters) {
        alert('하나 이상의 특징을 선택해주세요.');
        return;
    }

    // 랜덤 추천 섹션 숨기기
    hideRecommendSection();

    try {
        // 모든 선택된 해시태그 수집
        const allHashtags = [];
        for (const category in selectedFilters) {
            if (selectedFilters[category].length > 0) {
                allHashtags.push(...selectedFilters[category]);
            }
        }

        if (allHashtags.length === 0) {
            alert('필터를 선택해주세요.');
            return;
        }

        // API 호출 - 다중 해시태그 검색
        const formData = new URLSearchParams();
        allHashtags.forEach(hashtag => {
            formData.append('hashtags', hashtag);
        });

        const response = await fetch(`${contextPath}/api/products/search/hashtags`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: formData
        });

        if (!response.ok) {
            throw new Error('필터 검색에 실패했습니다.');
        }

        const results = await response.json();

        displaySearchResults(results);

        // 결과로 스크롤
        document.getElementById('searchResults').scrollIntoView({
            behavior: 'smooth',
            block: 'start'
        });
    } catch (error) {
        console.error('필터 적용 오류:', error);
        alert('필터 적용 중 오류가 발생했습니다.');
    }
}

// ================================================
// 7. 검색 결과 표시
// ================================================
function displaySearchResults(results) {
    const emptyState = document.getElementById('emptyState');
    const resultsList = document.getElementById('resultsList');

    if (results.length === 0) {
        emptyState.style.display = 'block';
        resultsList.style.display = 'none';
        emptyState.innerHTML = `
            <p class="empty-message">검색 결과가 없습니다.<br>다른 검색어나 특징을 선택해주세요.</p>
        `;
        // 추천 버튼 다시 표시
        showRecommendSection();
        return;
    }

    emptyState.style.display = 'none';
    resultsList.style.display = 'grid';

    resultsList.innerHTML = results.map(product => createProductCard(product)).join('');
}

function createProductCard(product) {
    const imageHtml = product.productPhoto
        ? `<img src="${product.productPhoto}" alt="${product.productName}" class="result-image" loading="lazy">`
        : `<div class="result-image no-image">이미지 없음</div>`;

    // ProductListResponse에는 hashtags가 없으므로 categoryName 사용
    const categoryTag = product.categoryName
        ? `<span class="result-tag">${product.categoryName}</span>`
        : '';

    return `
        <a href="${contextPath}/products/${product.productId}" class="result-card">
            <div class="result-image-wrapper">
                ${imageHtml}
            </div>
            <div class="result-content">
                <h3 class="result-name">${product.productName}</h3>
                <div class="result-price">${product.price.toLocaleString()}원</div>
                <div class="result-tags">${categoryTag}</div>
            </div>
        </a>
    `;
}

function resetSearchResults() {
    const emptyState = document.getElementById('emptyState');
    const resultsList = document.getElementById('resultsList');

    emptyState.style.display = 'block';
    resultsList.style.display = 'none';
    emptyState.innerHTML = `
        <p class="empty-message">찾고 싶은 메뉴를 검색하거나<br>특징을 선택해주세요!</p>
    `;

    // 랜덤 추천 섹션 다시 표시
    showRecommendSection();
}

// ================================================
// 8. 유틸리티 함수
// ================================================
function hideRecommendSection() {
    const recommendSection = document.querySelector('.random-recommend-section');
    if (recommendSection) {
        recommendSection.style.display = 'none';
    }
}

function showRecommendSection() {
    const recommendSection = document.querySelector('.random-recommend-section');
    if (recommendSection) {
        recommendSection.style.display = 'block';
    }
}

function formatNumber(num) {
    if (num >= 10000) {
        return (num / 10000).toFixed(1) + '만';
    } else if (num >= 1000) {
        return (num / 1000).toFixed(1) + '천';
    }
    return num.toString();
}

// ================================================
// 9. 이벤트 리스너
// ================================================
document.addEventListener('DOMContentLoaded', function() {
    initializeSearch();
});