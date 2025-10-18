/**
 * 카테고리 네비게이션 공통 스크립트
 * product-category-nav.jspf를 사용하는 모든 페이지에서 공통으로 사용
 */

// 페이지 로드 시 카테고리 네비게이션 이벤트 설정
document.addEventListener('DOMContentLoaded', function() {
    initCategoryNavigation();
});

/**
 * 카테고리 네비게이션 초기화
 */
function initCategoryNavigation() {
    const navItems = document.querySelectorAll('.product-category-nav-item');

    navItems.forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            const category = this.getAttribute('data-category');

            // sessionStorage에 선택된 카테고리 저장
            sessionStorage.setItem('scrollToCategory', category);

            // 현재 페이지 확인
            const currentPath = window.location.pathname;

            // productList.jsp 페이지가 아니면 /products로 이동
            if (!currentPath.includes('/products') || currentPath.includes('/products/')) {
                // contextPath 추출
                const contextPath = item.closest('a').href.split('#')[0].replace(/#.*$/, '');
                const baseContextPath = contextPath.substring(0, contextPath.lastIndexOf('/products'));
                window.location.href = baseContextPath + '/products';
            }
            // productList.jsp에서는 이미 구현된 스크롤 로직이 작동
        });
    });
}
