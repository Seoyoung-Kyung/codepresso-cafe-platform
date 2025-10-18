// 카테고리 한글명 매핑 (DB의 category_code와 일치)
const categoryNames = {
    'COFFEE': { ko: '커피', en: 'COFFEE' },
    'LATTE': { ko: '라떼', en: 'LATTE' },
    'JUICE': { ko: '주스 & 드링크', en: 'JUICE & DRINKS' },
    'SMOOTHIE': { ko: '바나치노 & 스무디', en: 'BANACCINO & SMOOTHIE' },
    'TEA': { ko: '티 & 에이드', en: 'TEA & ADE' },
    'DESSERT': { ko: '디저트', en: 'DESSERT' },
    'SET': { ko: '세트메뉴', en: 'SET MENU' },
    'MD_GOODS': { ko: 'MD상품', en: 'MD PRODUCTS' }
};

// 상품 카드 HTML 생성
function createProductCard(product) {
    const imageClass = product.productName.includes('핑크') || product.productName.includes('딸기') ? 'menu-image pink' :
                       product.productName.includes('바나나') ? 'menu-image yellow' : 'menu-image';

    let tag = '';
    if (product.productName.includes('시그니처')) {
        tag = '<div class="menu-tag tag-signature">시그니처</div>';
    } else if (product.productName.includes('디카페인')) {
        tag = '<div class="menu-tag tag-decaf">디카페인</div>';
    } else if (product.productName.includes('아메리카노') &&
               !product.productName.includes('시그니처') &&
               !product.productName.includes('디카페인')) {
        tag = '<div class="menu-tag tag-premium">고소함</div>';
    }

    const imageHtml = product.productPhoto
        ? '<img src="' + product.productPhoto + '" alt="' + product.productName + '" class="product-photo" loading="lazy">'
        : '';

    return '<div class="menu-item" onclick="location.href=\'' + contextPath + '/products/' + product.productId + '\'">' +
            '<div class="menu-image-container">' +
                '<div class="' + imageClass + '">' +
                    imageHtml +
                    tag +
                '</div>' +
            '</div>' +
            '<div class="menu-info">' +
                '<div class="menu-name">' + product.productName + '</div>' +
                '<div class="menu-price">' + product.price.toLocaleString() + '원</div>' +
            '</div>' +
        '</div>';
}

// 카테고리 섹션 HTML 생성
function createCategorySection(category, products) {
    const categoryInfo = categoryNames[category];
    if (!categoryInfo || products.length === 0) return '';

    const productsHTML = products.map(product => createProductCard(product)).join('');

    return '<section id="category-' + category + '" class="category-section">' +
            '<div class="section-header">' +
                '<h2 class="section-title">' +
                    categoryInfo.ko + " " +
                    '<span class="section-subtitle">' + categoryInfo.en + '</span>' +
                '</h2>' +
            '</div>' +
            '<div class="menu-grid">' +
                productsHTML +
            '</div>' +
        '</section>';
}

// 전체 상품 로드 및 렌더링
function loadAllProducts() {
    const container = document.getElementById('products-container');

    // List를 카테고리별로 그룹화 (display_order로 이미 정렬되어 있음)
    const productsByCategory = {};
    products.forEach(product => {
        const category = product.categoryCode;
        if (!productsByCategory[category]) {
            productsByCategory[category] = [];
        }
        productsByCategory[category].push(product);
    });

    // 카테고리별 섹션 생성
    const sectionsHTML = Object.entries(productsByCategory)
        .map(([category, products]) => createCategorySection(category, products))
        .join('');

    container.innerHTML = sectionsHTML || '<div class="no-products" style="text-align: center; padding: 100px 20px; color: #666;"><h3>등록된 상품이 없습니다.</h3></div>';
}

// ScrollSpy 일시 중지 플래그
let isScrollSpyPaused = false;

// sessionStorage에서 스크롤할 카테고리 확인
function checkAndScrollToCategory() {
    const scrollToCategory = sessionStorage.getItem('scrollToCategory');
    if (scrollToCategory) {
        sessionStorage.removeItem('scrollToCategory');

        // ScrollSpy 일시 중지 (수동 active 적용을 위해)
        isScrollSpyPaused = true;

        // 상품 로딩 완료 후 스크롤
        const checkAndScroll = setInterval(function() {
            const targetSection = document.getElementById('category-' + scrollToCategory);
            if (targetSection) {
                clearInterval(checkAndScroll);

                const navHeight = document.querySelector('.product-category-nav').offsetHeight;
                const headerHeight = document.querySelector('.header').offsetHeight;
                const targetPosition = targetSection.offsetTop - navHeight - headerHeight - 20;

                window.scrollTo({
                    top: targetPosition,
                    behavior: 'smooth'
                });

                // 스크롤 완료 후 active 적용 (스크롤 애니메이션 대기)
                setTimeout(function() {
                    document.querySelectorAll('.product-category-nav-item').forEach(function(item) {
                        item.classList.remove('active');
                    });
                    const activeNavItem = document.querySelector('.product-category-nav-item[data-category="' + scrollToCategory + '"]');
                    if (activeNavItem) {
                        activeNavItem.classList.add('active');
                    }

                    // 2초 후 ScrollSpy 재개
                    setTimeout(function() {
                        isScrollSpyPaused = false;
                    }, 2000);
                }, 1200); // 스크롤 애니메이션 완료 대기
            }
        }, 100);

        setTimeout(function() {
            clearInterval(checkAndScroll);
        }, 5000);
    }
}

// Smooth Scroll 구현
function initSmoothScroll() {
    const navItems = document.querySelectorAll('.product-category-nav-item');

    navItems.forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            const targetId = this.getAttribute('href');
            const targetSection = document.querySelector(targetId);

            if (targetSection) {
                // ScrollSpy 일시 중지
                isScrollSpyPaused = true;

                const navHeight = document.querySelector('.product-category-nav').offsetHeight;
                const headerHeight = document.querySelector('.header').offsetHeight;
                const targetPosition = targetSection.offsetTop - navHeight - headerHeight - 20;

                window.scrollTo({
                    top: targetPosition,
                    behavior: 'smooth'
                });

                // 클릭한 카테고리 추출
                const clickedCategory = this.getAttribute('data-category');

                // 스크롤 완료 후 active 적용
                setTimeout(function() {
                    document.querySelectorAll('.product-category-nav-item').forEach(function(navItem) {
                        navItem.classList.remove('active');
                    });
                    const activeNavItem = document.querySelector('.product-category-nav-item[data-category="' + clickedCategory + '"]');
                    if (activeNavItem) {
                        activeNavItem.classList.add('active');
                    }

                    // 2초 후 ScrollSpy 재개
                    setTimeout(function() {
                        isScrollSpyPaused = false;
                    }, 2000);
                }, 1200);
            }
        });
    });
}

// Scroll Spy 구현 (현재 보이는 섹션 하이라이트)
function initScrollSpy() {
    const navItems = document.querySelectorAll('.product-category-nav-item');
    const sections = document.querySelectorAll('.category-section');

    const observerOptions = {
        root: null,
        rootMargin: '-120px 0px -60% 0px', // 상단 120px(헤더+네비), 하단 60%
        threshold: 0
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                // ScrollSpy가 일시 중지된 경우 active 변경 스킵
                if (isScrollSpyPaused) return;

                const category = entry.target.id.replace('category-', '');

                // 모든 네비게이션 아이템에서 active 제거
                navItems.forEach(item => item.classList.remove('active'));

                // 현재 섹션에 해당하는 네비게이션 아이템에 active 추가
                const activeNavItem = document.querySelector(`.product-category-nav-item[data-category="${category}"]`);
                if (activeNavItem) {
                    activeNavItem.classList.add('active');
                }
            }
        });
    }, observerOptions);

    // 상품 로딩 완료 후 섹션 관찰 시작
    const checkSections = setInterval(() => {
        const loadedSections = document.querySelectorAll('.category-section');
        if (loadedSections.length > 0) {
            loadedSections.forEach(section => observer.observe(section));
            clearInterval(checkSections);
        }
    }, 100);
}

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', function () {
    loadAllProducts();
    initSmoothScroll();
    initScrollSpy();
    checkAndScrollToCategory();
});
