// 전역 변수
let currentQuantity = 1;
let currentTemperature = 'ice';

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    initializeEventListeners();
    loadProductDetail();
});

// 이벤트 리스너 초기화
function initializeEventListeners() {
    // 온도 버튼 이벤트
    const tempButtons = document.querySelectorAll('.temp-btn');
    tempButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            const temp = this.getAttribute('data-temp');
            selectTemperature(temp);
        });
    });

    // 온도 옵션 클릭 이벤트
    const tempOptions = document.querySelectorAll('.temp-option');
    tempOptions.forEach(option => {
        option.addEventListener('click', function() {
            // HOT/ICE 옵션 토글 (현재는 ICE만 활성화)
            if (!this.classList.contains('active')) {
                tempOptions.forEach(opt => opt.classList.remove('active'));
                this.classList.add('active');
            }
        });
    });

    // 액션 버튼 이벤트
    const immediateBtn = document.querySelector('.order-btn.immediate');
    const cartBtn = document.querySelector('.order-btn.cart');

    if (immediateBtn) {
        immediateBtn.addEventListener('click', orderImmediately);
    }

    if (cartBtn) {
        cartBtn.addEventListener('click', addToCart);
    }

    // 오버레이 클릭 시 팝업 닫기
    const overlay = document.getElementById('menuPopupOverlay');
    if (overlay) {
        overlay.addEventListener('click', function(e) {
            if (e.target === overlay) {
                closeMenuPopup();
            }
        });
    }

    // ESC 키로 팝업 닫기
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeMenuPopup();
        }
    });
}

// 팝업 열기 - 메뉴 데이터 없이도 작동하도록 수정
function openMenuPopup(menuData) {
    const overlay = document.getElementById('menuPopupOverlay');
    if (overlay) {
        // 메뉴 데이터가 있으면 업데이트
        if (menuData) {
            updateMenuData(menuData);
        }

        overlay.classList.add('active');
        document.body.style.overflow = 'hidden'; // 스크롤 방지
    }
}

// 팝업 닫기
function closeMenuPopup() {
    const overlay = document.getElementById('menuPopupOverlay');
    if (overlay) {
        overlay.classList.remove('active');
        document.body.style.overflow = ''; // 스크롤 복원

        // 상태 초기화
        currentQuantity = 1;
        updateQuantityDisplay();
    }
}

// 온도 선택
function selectTemperature(temp) {
    currentTemperature = temp;

    // 버튼 상태 업데이트
    const tempButtons = document.querySelectorAll('.temp-btn');
    tempButtons.forEach(btn => {
        if (btn.getAttribute('data-temp') === temp) {
            btn.classList.add('active');
        } else {
            btn.classList.remove('active');
        }
    });

    // 온도 옵션 업데이트
    const tempOptions = document.querySelectorAll('.temp-option');
    tempOptions.forEach((option, index) => {
        if ((temp === 'hot' && index === 0) || (temp === 'ice' && index === 1)) {
            option.classList.add('active');
        } else {
            option.classList.remove('active');
        }
    });
}

// 수량 증가
function increaseQuantity() {
    if (currentQuantity < 99) { // 최대 수량 제한
        currentQuantity++;
        updateQuantityDisplay();
    }
}

// 수량 감소
function decreaseQuantity() {
    if (currentQuantity > 1) { // 최소 수량 제한
        currentQuantity--;
        updateQuantityDisplay();
    }
}

// 수량 표시 업데이트
function updateQuantityDisplay() {
    const quantityDisplay = document.querySelector('.quantity-display');
    if (quantityDisplay) {
        quantityDisplay.textContent = currentQuantity;
    }
}

// 바로 주문하기
function orderImmediately() {
    const orderData = {
        menuName: '허니아메리카노',
        temperature: currentTemperature,
        quantity: currentQuantity,
        action: 'immediate'
    };

    console.log('바로 주문:', orderData);

    // 주문 처리 로직
    processOrder(orderData);
}

// 장바구니에 담기
function addToCart() {
    const cartData = {
        menuName: '허니아메리카노',
        temperature: currentTemperature,
        quantity: currentQuantity,
        action: 'cart'
    };

    console.log('장바구니 담기:', cartData);

    // 장바구니 처리 로직
    addItemToCart(cartData);

    // 성공 피드백
    showSuccessMessage('장바구니에 담았습니다.');
}

// 주문 처리
function processOrder(orderData) {
    // 로딩 상태 표시
    showLoadingState();

    // 실제 주문 API 호출 (예시)
    setTimeout(() => {
        hideLoadingState();
        showSuccessMessage('주문이 완료되었습니다.');
        closeMenuPopup();

        // 주문 완료 페이지로 이동 또는 다른 처리
        // window.location.href = '/order-complete';
    }, 1500);
}

// 장바구니 추가 - 메인 페이지의 addToCart와 연동
function addItemToCart(cartData) {
    // 기존 메인 페이지의 addToCart 함수가 있다면 우선 사용
    if (typeof window.addToCartHandler === 'function') {
        window.addToCartHandler(cartData.menuName, cartData.price || 0);
        return;
    }

    // 기존 장바구니 데이터 가져오기
    let cart = JSON.parse(localStorage.getItem('cart')) || [];

    // 동일한 상품이 있는지 확인
    const existingItemIndex = cart.findIndex(item =>
        item.menuName === cartData.menuName &&
        item.temperature === cartData.temperature
    );

    if (existingItemIndex > -1) {
        // 기존 상품의 수량 업데이트
        cart[existingItemIndex].quantity += cartData.quantity;
    } else {
        // 새로운 상품 추가
        cart.push({
            ...cartData,
            id: Date.now(), // 고유 ID
            price: cartData.price || window.currentMenuData.price || 0,
            image: cartData.image || window.currentMenuData.photo || 'images/default-menu.jpg'
        });
    }

    // 장바구니 저장
    localStorage.setItem('cart', JSON.stringify(cart));

    // 장바구니 카운트 업데이트
    updateCartCount();
}

// 메뉴 데이터 업데이트
function updateMenuData(menuData) {
    // 제목 업데이트
    const title = document.querySelector('.popup-header h2');
    if (title && menuData.name) {
        title.textContent = menuData.name;
    }

    // 이미지 업데이트
    const image = document.querySelector('.menu-image');
    if (image && menuData.image) {
        image.src = menuData.image;
        image.alt = menuData.name || '메뉴 이미지';
    }

    // 설명 업데이트
    const description = document.querySelector('.menu-description p');
    if (description && menuData.description) {
        description.textContent = menuData.description;
    }

    // 좋아요 수 업데이트
    const likeCount = document.querySelector('.like-count');
    if (likeCount && menuData.likes) {
        likeCount.textContent = menuData.likes;
    }

    // 영양정보 업데이트 (필요한 경우)
    if (menuData.nutrition) {
        updateNutritionInfo(menuData.nutrition);
    }
}

// 영양정보 업데이트
function updateNutritionInfo(nutrition) {
    const nutritionTable = document.querySelector('.nutrition-table');
    if (!nutritionTable) return;

    const cells = nutritionTable.querySelectorAll('td');

    // 영양정보 매핑 (순서에 따라)
    const nutritionMapping = [
        nutrition.calories, nutrition.sodium,
        nutrition.protein, nutrition.sugar,
        nutrition.fat, nutrition.caffeine,
        nutrition.cholesterol, nutrition.carbohydrate,
        nutrition.transFat, nutrition.saturatedFat
    ];

    // 값이 있는 셀만 업데이트
    nutritionMapping.forEach((value, index) => {
        const cellIndex = Math.floor(index / 2) * 4 + (index % 2) * 2 + 1;
        if (cells[cellIndex] && value !== undefined) {
            cells[cellIndex].textContent = value;
        }
    });
}

// 로딩 상태 표시
function showLoadingState() {
    const buttons = document.querySelectorAll('.order-btn');
    buttons.forEach(btn => {
        btn.disabled = true;
        btn.style.opacity = '0.6';
    });
}

// 로딩 상태 해제
function hideLoadingState() {
    const buttons = document.querySelectorAll('.order-btn');
    buttons.forEach(btn => {
        btn.disabled = false;
        btn.style.opacity = '1';
    });
}

// 성공 메시지 표시 - 간단한 토스트 메시지로 개선
function showSuccessMessage(message) {
    // 토스트 메시지 생성
    const toast = document.createElement('div');
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: #4CAF50;
        color: white;
        padding: 12px 20px;
        border-radius: 8px;
        z-index: 10000;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        font-size: 14px;
        font-weight: 500;
        animation: slideInRight 0.3s ease-out;
    `;
    toast.textContent = message;

    // CSS 애니메이션 추가
    if (!document.getElementById('toast-styles')) {
        const style = document.createElement('style');
        style.id = 'toast-styles';
        style.textContent = `
            @keyframes slideInRight {
                from { transform: translateX(100%); opacity: 0; }
                to { transform: translateX(0); opacity: 1; }
            }
            @keyframes slideOutRight {
                from { transform: translateX(0); opacity: 1; }
                to { transform: translateX(100%); opacity: 0; }
            }
        `;
        document.head.appendChild(style);
    }

    document.body.appendChild(toast);

    // 3초 후 자동 제거
    setTimeout(() => {
        toast.style.animation = 'slideOutRight 0.3s ease-in';
        setTimeout(() => {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 300);
    }, 3000);
}

// 장바구니 카운트 업데이트 (전역 함수)
function updateCartCount() {
    const cart = JSON.parse(localStorage.getItem('cart')) || [];
    const totalCount = cart.reduce((sum, item) => sum + item.quantity, 0);

    // 장바구니 카운트 표시 요소가 있다면 업데이트
    const cartCountElement = document.querySelector('.cart-count');
    if (cartCountElement) {
        cartCountElement.textContent = totalCount;
        cartCountElement.style.display = totalCount > 0 ? 'block' : 'none';
    }
}

// 상품 상세 정보 로드
function loadProductDetail() {
    const pathSegments = window.location.pathname.split('/');
    const productId = pathSegments[pathSegments.length - 1];

    if (!productId || isNaN(productId)) {
        console.error('Invalid product ID');
        return;
    }

    // API 호출
    fetch(`/api/products/${productId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Product not found');
            }
            return response.json();
        })
        .then(data => {
            updateProductUI(data);
        })
        .catch(error => {
            console.error('Error loading product:', error);
            showErrorMessage('상품 정보를 불러올 수 없습니다.');
        });
}

// 상품 정보로 UI 업데이트
function updateProductUI(product) {
    // 상품명 업데이트
    const productName = document.querySelector('.product-name, h1');
    if (productName) {
        productName.textContent = product.productName;
    }

    // 상품 이미지 업데이트
    const productImage = document.querySelector('.product-image, .menu-image');
    if (productImage && product.productPhoto) {
        productImage.src = product.productPhoto;
        productImage.alt = product.productName;
    }

    // 상품 설명 업데이트
    const productDescription = document.querySelector('.product-description, .menu-description p');
    if (productDescription && product.productContent) {
        productDescription.textContent = product.productContent;
    }

    // 가격 업데이트
    const productPrice = document.querySelector('.product-price, .price');
    if (productPrice && product.price) {
        productPrice.textContent = `${product.price.toLocaleString()}원`;
    }

    // 영양정보 업데이트
    if (product.nutritionInfo) {
        updateNutritionInfo(product.nutritionInfo);
    }

    // 전역 변수에 상품 정보 저장
    window.currentProductData = product;
}

// 에러 메시지 표시
function showErrorMessage(message) {
    const errorToast = document.createElement('div');
    errorToast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: #f44336;
        color: white;
        padding: 12px 20px;
        border-radius: 8px;
        z-index: 10000;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        font-size: 14px;
        font-weight: 500;
    `;
    errorToast.textContent = message;

    document.body.appendChild(errorToast);

    setTimeout(() => {
        if (errorToast.parentNode) {
            errorToast.parentNode.removeChild(errorToast);
        }
    }, 3000);
}

// 외부에서 사용할 수 있는 함수들
window.openMenuPopup = openMenuPopup;
window.closeMenuPopup = closeMenuPopup;
window.increaseQuantity = increaseQuantity;
window.decreaseQuantity = decreaseQuantity;
