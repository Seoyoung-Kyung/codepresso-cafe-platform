let cart = {};
let cartCount = 0;
let totalAmount = 0;

// 장바구니에서 상품 완전 삭제
function deleteFromCart(name) {
    delete cart[name];
    updateCart();
}

// 장바구니 UI 업데이트
function updateCart() {
    const cartItems = document.getElementById('cartItems');
    const cartCountEl = document.getElementById('cartCount');
    const totalAmountEl = document.getElementById('totalAmount');
    const orderBtn = document.getElementById('orderBtn');

    cartItems.innerHTML = '';
    cartCount = 0;
    totalAmount = 0;

    if (Object.keys(cart).length === 0) {
        cartItems.innerHTML = `
            <div style="text-align: center; color: #666; padding: 40px 20px;">
                장바구니가 비어있습니다
            </div>
        `;
        orderBtn.disabled = true;
    } else {
        orderBtn.disabled = false;
        Object.values(cart).forEach(item => {
            cartCount += item.quantity;
            totalAmount += item.price * item.quantity;

            const cartItem = document.createElement('div');
            cartItem.className = 'cart-item';
            cartItem.innerHTML = `
                <div class="cart-item-info">
                    <h4>${item.name}</h4>
                    <div class="cart-item-price">${item.price.toLocaleString()}원</div>
                </div>
                <div class="quantity-controls">
                    <button class="quantity-btn" onclick="removeFromCart('${item.name}')">-</button>
                    <span>${item.quantity}</span>
                    <button class="quantity-btn" onclick="addToCart('${item.name}', ${item.price})">+</button>
                </div>
            `;
            cartItems.appendChild(cartItem);
        });
    }

    cartCountEl.textContent = cartCount;
    totalAmountEl.textContent = totalAmount.toLocaleString();
}

// 주문하기 함수
function placeOrder() {
    if (cartCount > 0) {
        // 주문 확인 모달
        if (confirm(`주문하시겠습니까?\n총 ${cartCount}개 상품\n결제금액: ${totalAmount.toLocaleString()}원`)) {

            // 실제 구현에서는 서버로 주문 데이터 전송
            const orderData = {
                items: cart,
                totalCount: cartCount,
                totalAmount: totalAmount,
                orderTime: new Date().toISOString(),
                storeId: getSelectedStore() // 선택된 매장 정보
            };

            // 서버로 주문 전송 (예시)
            submitOrder(orderData);
        }
    }
}

// 서버로 주문 제출 (실제 구현 예시)
function submitOrder(orderData) {
    // 로딩 상태 표시
    const orderBtn = document.getElementById('orderBtn');
    const originalText = orderBtn.textContent;
    orderBtn.textContent = '주문 중...';
    orderBtn.disabled = true;

    // AJAX 요청 예시
    fetch('/api/order', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: JSON.stringify(orderData)
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('주문이 성공적으로 접수되었습니다!\n주문번호: ' + data.orderNumber);
                cart = {};
                updateCart();
                toggleCart();
            } else {
                alert('주문 처리 중 오류가 발생했습니다: ' + data.message);
            }
        })
        .catch(error => {
            console.error('주문 처리 오류:', error);
            alert('주문 처리 중 오류가 발생했습니다. 다시 시도해주세요.');
        })
        .finally(() => {
            orderBtn.textContent = originalText;
            orderBtn.disabled = false;
        });
}

// 카테고리별 메뉴 로드
function loadCategoryMenu(category) {
    // 실제 구현에서는 서버에서 카테고리별 메뉴 데이터를 가져옴
    console.log(`${category} 카테고리 메뉴 로드`);


    // fetch(`/api/menu?category=${encodeURIComponent(category)}`)
    //     .then(response => response.json())
    //     .then(data => {
    //         renderMenuItems(data.menuList);
    //         updateSectionTitle(category);
    //     });
}

// 메뉴 검색
function searchMenu(searchTerm) {
    console.log(`메뉴 검색: ${searchTerm}`);

    // 실제 구현에서는 서버에서 검색 결과를 가져옴
    // fetch(`/api/menu/search?q=${encodeURIComponent(searchTerm)}`)
    //     .then(response => response.json())
    //     .then(data => {
    //         renderMenuItems(data.menuList);
    //         updateSectionTitle(`검색 결과: "${searchTerm}"`);
    //     });
}

// 필터 모달 표시
function showFilterModal() {
    // 필터 옵션 모달 구현
    const filterOptions = {
        price: ['전체', '5천원 이하', '5천원-1만원', '1만원 이상'],
        tag: ['전체', '디카페인', '시그니처', '신메뉴'],
        temperature: ['전체', 'HOT', 'ICED']
    };

    console.log('필터 모달 표시');
    // 실제로는 모달 UI를 동적으로 생성하거나 숨겨진 모달을 표시
}

// 선택된 매장 정보 가져오기
function getSelectedStore() {
    // 실제로는 선택된 매장 정보를 반환
    return {
        storeId: '001',
        storeName: '강남점',
        address: '서울시 강남구...'
    };
}

// 장바구니 추가 애니메이션
function showAddToCartAnimation() {
    const cartBtn = document.querySelector('.cart-btn');
    cartBtn.style.transform = 'scale(1.1)';
    cartBtn.style.transition = 'transform 0.2s ease';

    setTimeout(() => {
        cartBtn.style.transform = 'scale(1)';
    }, 200);
}

// 메뉴 아이템 렌더링 (동적 생성용)
function renderMenuItems(menuList) {
    const menuGrid = document.querySelector('.menu-grid');
    menuGrid.innerHTML = '';

    menuList.forEach(menu => {
        const menuItem = document.createElement('div');
        menuItem.className = 'menu-item';
        menuItem.onclick = () => addToCart(menu.name, menu.price);

        menuItem.innerHTML = `
            <div class="menu-image-container">
                <div class="menu-image ${menu.imageClass || ''}">
                    ${menu.tag ? `<div class="menu-tag ${menu.tagClass}">${menu.tag}</div>` : ''}
                </div>
            </div>
            <div class="menu-info">
                <div class="menu-name">${menu.name}</div>
                <button class="add-to-cart" onclick="event.stopPropagation(); addToCart('${menu.name}', ${menu.price})">🛒</button>
            </div>
        `;

        menuGrid.appendChild(menuItem);
    });

    // 새로 생성된 메뉴 아이템에 호버 이벤트 추가
    addHoverEffectToMenuItems();
}

// 메뉴 아이템에 호버 효과 추가
function addHoverEffectToMenuItems() {
    document.querySelectorAll('.menu-item').forEach(item => {
        item.addEventListener('mouseenter', function() {
            this.style.border = '3px solid #ff6b9d';
            this.style.backgroundColor = '#fafafa';
        });

        item.addEventListener('mouseleave', function() {
            this.style.border = '3px solid transparent';
            this.style.backgroundColor = 'transparent';
        });
    });
}

// 섹션 제목 업데이트
function updateSectionTitle(title) {
    const sectionTitle = document.querySelector('.section-title');
    if (sectionTitle) {
        sectionTitle.innerHTML = `${title} <span class="section-subtitle">MENU</span>`;
    }
}

// 키보드 단축키 지원
document.addEventListener('keydown', function(e) {
    // ESC 키로 장바구니 닫기
    if (e.key === 'Escape') {
        const cartPanel = document.getElementById('cartPanel');
        if (cartPanel.classList.contains('open')) {
            toggleCart();
        }
    }

    // Ctrl + / 로 검색
    if (e.ctrlKey && e.key === '/') {
        e.preventDefault();
        document.querySelector('.search-btn').click();
    }
});

// 스크롤 위치에 따른 헤더 스타일 변경
window.addEventListener('scroll', function() {
    const nav = document.querySelector('.product-category-nav');
    if (!nav) {
        return;
    }

    if (window.scrollY > 100) {
        nav.style.boxShadow = '0 2px 10px rgba(0,0,0,0.1)';
    } else {
        nav.style.boxShadow = 'none';
    }
});

// 카테고리에 따른 메뉴 - JSP 서버 링크를 사용하도록 주석 처리
/*
document.addEventListener('DOMContentLoaded', function() {
    const navItems = document.querySelectorAll('.product-category-nav-item'); // 네비 메뉴 아이템들
    const sections = document.querySelectorAll('.section');  // 스크롤할 섹션들

    // 1. 네비게이션 메뉴 클릭시 해당 섹션으로 스크롤
    navItems.forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();

            // data-target 속성에서 타겟 섹션 ID 가져오기
            const targetId = this.getAttribute('data-target');
            const targetSection = document.getElementById(targetId);

            if (targetSection) {
                // 부드러운 스크롤로 이동
                targetSection.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });

                // 활성 메뉴 변경
                navItems.forEach(nav => nav.classList.remove('active'));
                this.classList.add('active');
            }
        });
    });

    // 2. 스크롤 위치에 따라 네비게이션 메뉴 자동 활성화
    window.addEventListener('scroll', function() {
        let current = '';

        sections.forEach(section => {
            const sectionTop = section.offsetTop;
            const sectionHeight = section.clientHeight;

            // 현재 스크롤 위치가 섹션 영역 안에 있으면
            if (window.pageYOffset >= sectionTop - 200) {
                current = section.getAttribute('id');
            }
        });

        // 해당하는 네비메뉴를 active로 변경
        navItems.forEach(item => {
            item.classList.remove('active');
            if (item.getAttribute('data-target') === current) {
                item.classList.add('active');
            }
        });
    });
});
*/
// 장바구니 자동 저장 (로컬 스토리지)
function saveCartToStorage() {
    try {
        localStorage.setItem('banapresso_cart', JSON.stringify(cart));
    } catch (e) {
        console.warn('장바구니 저장 실패:', e);
    }
}

// 장바구니 복원 (로컬 스토리지에서)
function loadCartFromStorage() {
    try {
        const savedCart = localStorage.getItem('banapresso_cart');
        if (savedCart) {
            cart = JSON.parse(savedCart);
            updateCart();
        }
    } catch (e) {
        console.warn('장바구니 복원 실패:', e);
        cart = {};
    }
}

// 페이지 언로드 시 장바구니 저장
window.addEventListener('beforeunload', function() {
    saveCartToStorage();
});

// 페이지 로드 시 장바구니 복원
window.addEventListener('load', function() {
    loadCartFromStorage();
});// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    initializeEventListeners();
    updateCart();
});

// 이벤트 리스너 초기화
function initializeEventListeners() {
    // 네비게이션 탭 전환 - JSP 서버 링크를 사용하도록 주석 처리
    /*
    document.querySelectorAll('.product-category-nav-item').forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();

            // 기존 active 클래스 제거
            document.querySelector('.product-category-nav-item.active')?.classList.remove('active');

            // 현재 클릭된 항목에 active 클래스 추가
            this.classList.add('active');

            // 카테고리별 메뉴 로드 (실제 구현 시 서버 통신)
            const category = this.textContent;
            loadCategoryMenu(category);
        });
    });
    */

    // 검색 버튼 이벤트
    const searchButton = document.querySelector('.search-btn');
    if (searchButton) {
        searchButton.addEventListener('click', function() {
            const searchTerm = prompt('검색할 메뉴를 입력하세요:');
            if (searchTerm) {
                searchMenu(searchTerm);
            }
        });
    }

    // 필터 버튼 이벤트
    const filterButton = document.querySelector('.filter-btn');
    if (filterButton) {
        filterButton.addEventListener('click', function() {
            showFilterModal();
        });
    }
}

// 장바구니 토글 함수
function toggleCart() {
    const cartPanel = document.getElementById('cartPanel');
    const cartOverlay = document.getElementById('cartOverlay');

    cartPanel.classList.toggle('open');
    cartOverlay.classList.toggle('open');

    // body 스크롤 제어
    if (cartPanel.classList.contains('open')) {
        document.body.style.overflow = 'hidden';
    } else {
        document.body.style.overflow = 'auto';
    }
}

// 장바구니에 상품 추가
function addToCart(name, price) {
    if (cart[name]) {
        cart[name].quantity++;
    } else {
        cart[name] = {
            name: name,
            price: price,
            quantity: 1
        };
    }
    updateCart();

    // 피드백 애니메이션
    showAddToCartAnimation();
}

// 장바구니에서 상품 제거 (수량 감소)
function removeFromCart(name) {
    if (cart[name]) {
        if (cart[name].quantity > 1) {
            cart[name].quantity--;
        } else {
            delete cart[name];
        }
        updateCart();
    }
}
