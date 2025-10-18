<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<main class="cart-page">
    <div class="cart-shell">
        <c:set var="itemCount" value="0" />
        <c:set var="totalPrice" value="0" />
        <c:if test="${not empty cart and not empty cart.items}">
            <c:forEach var="item" items="${cart.items}">
                <c:set var="itemCount" value="${itemCount + item.quantity}" />
                <c:set var="totalPrice" value="${totalPrice + item.price}" />
            </c:forEach>
        </c:if>

        <section class="cart-content">
            <header class="cart-header-bar">
                <div>
                    <h1>장바구니</h1>
                    <p>주문 전 담긴 메뉴와 옵션을 한 번 더 확인해 주세요.</p>
                </div>
            </header>

            <c:choose>
                <c:when test="${itemCount == 0}">
                    <section class="cart-empty">
                        <div class="cart-empty-illust"></div>
                        <h2>장바구니가 비어 있어요</h2>
                        <p>지금 인기 메뉴를 둘러보고 원하는 음료를 담아보세요.</p>
                        <a class="btn btn-primary" href="/branch/list">메뉴 보러가기</a>
                    </section>
                </c:when>
                <c:otherwise>
                    <div class="cart-item-group">
                        <div class="cart-group-header">
                            <form class="cart-clear-form" action="<c:url value='/users/cart/clear'/>" method="post">
                                <input type="hidden" name="cartId" value="${cart.cartId}" />
                                <input type="hidden" name="redirect" value="/cart" />
                                <button type="submit" class="btn btn-outline btn-clear">전체 비우기</button>
                            </form>
                        </div>
                        <ul class="cart-item-list">
                            <c:forEach var="item" items="${cart.items}">
                                <li class="cart-item">
                                    <div class="cart-item-thumb" aria-hidden="true">
                                        <c:choose>
                                            <c:when test="${not empty item.productPhoto}">
                                                <img src="${item.productPhoto}"
                                                     alt="${item.productName}"
                                                     onerror="this.src='/banners/mascot.png'; this.onerror=null;" />
                                            </c:when>
                                            <c:otherwise>
                                                <img src="/banners/mascot.png" alt="CodePress Mascot" />
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="cart-item-info">
                                        <div class="cart-item-header">
                                            <h3>${item.productName}</h3>
                                            <button type="button" class="btn-text" data-delete="${item.cartItemId}">삭제</button>
                                        </div>
                                        <div class="cart-item-meta">
                                            <div class="meta-text">
                                                <span>수량 ${item.quantity}개</span>
                                                <span class="dot">·</span>
                                                <span><fmt:formatNumber value="${item.price}" type="number" />원</span>
                                            </div>
                                            <div class="qty-actions">
                                                <div class="qty-control" data-item="${item.cartItemId}">
                                                    <button type="button" class="qty-btn" data-type="minus">-</button>
                                                    <input type="number" min="1" value="${item.quantity}" />
                                                    <button type="button" class="qty-btn" data-type="plus">+</button>
                                                </div>
                                                <button type="button" class="btn btn-outline btn-apply" data-update="${item.cartItemId}">적용</button>
                                            </div>
                                        </div>
                                        <c:if test="${not empty item.options}">
                                            <ul class="cart-option-list">
                                                <c:forEach var="opt" items="${item.options}">
                                                    <li>
                                                        <span>${opt.optionStyle}</span>
                                                        <c:if test="${opt.extraPrice ne null && opt.extraPrice ne 0}">
                                                            <em>+<fmt:formatNumber value="${opt.extraPrice}" type="number" />원</em>
                                                        </c:if>
                                                    </li>
                                                </c:forEach>
                                            </ul>
                                        </c:if>
                                    </div>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

        <aside class="cart-sidebar">
            <div class="branch-select-card" id="branchSelectionSection">
                <h2>선택된 매장</h2>
                <div id="branchSelectedInfo" class="branch-selected ${empty branchId ? 'is-hidden' : ''}">
                    <div class="branch-selected-text">
                        <strong id="branchSelectedName">
                            <c:choose>
                                <c:when test="${not empty branchName}">${branchName}</c:when>
                                <c:when test="${not empty branchId}">매장 정보를 불러오는 중...</c:when>
                                <c:otherwise></c:otherwise>
                            </c:choose>
                        </strong>
                    </div>
                    <button type="button" class="btn btn-outline btn-sm" data-open-branch-modal>변경</button>
                </div>
                <div id="branchEmptyState" class="branch-empty ${not empty branchId ? 'is-hidden' : ''}">
                    <span>매장을 선택해주세요.</span>
                    <button type="button" class="btn btn-primary btn-sm" data-open-branch-modal>선택</button>
                </div>
                <input type="hidden" id="selectedBranchIdInput" value="${branchId != null ? branchId : ''}" />
                <input type="hidden" id="selectedBranchNameInput" value="${branchName != null ? branchName : ''}" />
            </div>
            <div class="summary-card">
                <h2>총 주문 수량 ${itemCount}개</h2>
                <h2>결제 예정 금액</h2>
                <dl>
                    <div class="row">
                        <dt>상품 금액</dt>
                        <dd><fmt:formatNumber value="${totalPrice}" type="number" />원</dd>
                    </div>
                    <div class="row">
                        <dt>할인</dt>
                        <dd>0원</dd>
                    </div>
                    <div class="total">
                        <dt>총 결제 금액</dt>
                        <dd><fmt:formatNumber value="${totalPrice}" type="number" />원</dd>
                    </div>
                </dl>
                <button class="btn btn-primary btn-block${empty branchId ? ' btn-disabled' : ''}" type="button"${empty branchId ? ' disabled' : ''}>주문하기</button>
            </div>
        </aside>
    </div>
</main>

<%@ include file="/WEB-INF/views/branch/branch-select-modal.jspf" %>

<style>
    body { background-color: #f7f7f7; }
    .cart-page { padding: 48px 0 64px; }
    .cart-shell {
        max-width: 1100px;
        margin: 0 auto;
        display: grid;
        grid-template-columns: minmax(0, 1.5fr) minmax(0, 1fr);
        gap: 32px;
        padding: 0 20px;
    }
    .cart-content { display: grid; gap: 24px; }
    .cart-header-bar {
        display: flex;
        justify-content: space-between;
        align-items: flex-end;
        gap: 20px;
        padding: 24px 28px;
        background: #fff;
        border-radius: 20px;
        box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
    }
    .cart-header-bar h1 { margin: 0 0 8px; font-size: 28px; }
    .cart-header-bar p { margin: 0; color: var(--text-2); font-size: 15px; }
    .cart-header-info { display: flex; gap: 12px; color: var(--text-2); font-weight: 600; }
    .cart-header-info strong { color: var(--text-1); }

    .cart-empty {
        text-align: center;
        padding: 60px 40px;
        background: #fff;
        border-radius: 20px;
        box-shadow: 0 18px 40px rgba(15,23,42,0.08);
        display: grid; gap: 16px; justify-items: center;
    }
    .cart-empty h2 { margin: 0; font-size: 24px; }
    .cart-empty p { margin: 0; color: var(--text-2); }
    .cart-empty-illust {
        width: 88px; height: 88px;
        border-radius: 50%;
        background: linear-gradient(135deg, #ffe5ec, #ffd5e4);
        box-shadow: inset 0 6px 12px rgba(255, 255, 255, 0.6);
    }

    .cart-item-list { list-style: none; margin: 0; padding: 0; display: block; }
    .cart-item {
        display: grid;
        grid-template-columns: 96px 1fr;
        gap: 20px;
        padding: 16px 4px 16px 0;
        background: transparent;
        border-radius: 0;
        border: none;
        box-shadow: none;
        position: relative;
    }
    .cart-item + .cart-item {
        border-top: 1px dashed rgba(255, 122, 162, 0.35);
        margin-top: 12px;
        padding-top: 28px;
    }
    .cart-item-group {
        background: rgba(255, 255, 255, 0.85);
        border-radius: 26px;
        padding: 28px 30px;
        box-shadow: 0 24px 48px rgba(15, 23, 42, 0.1);
        backdrop-filter: blur(6px);
    }
    .cart-item-group .cart-item-list { display: block; }
    .cart-item-thumb {
        background: linear-gradient(135deg, var(--pink-3), var(--pink-4));
        position: relative;
    }
    .cart-item-thumb img { width: 100%; height: 100%; object-fit: cover; display: block; }
    .cart-item-info { display: grid; gap: 12px; }
    .cart-item-header { display: flex; justify-content: space-between; align-items: flex-start; gap: 16px; }
    .cart-item-header h3 { margin: 0; font-size: 20px; }
    .btn-text { background: none; border: 0; cursor: pointer; color: var(--text-2); font-size: 14px; }
    .btn-text:hover { color: var(--text-1); }
    .cart-item-meta { color: var(--text-2); font-size: 14px; display: flex; justify-content: space-between; align-items: center; gap: 16px; flex-wrap: wrap; }
    .cart-item-meta .dot { opacity: 0.4; }
    .meta-text { display: flex; gap: 8px; align-items: center; }
    .qty-actions { display: flex; align-items: center; gap: 10px; }
    .qty-control {
        display: inline-flex;
        align-items: center;
        gap: 6px;
        border: 1px solid rgba(0,0,0,0.1);
        border-radius: 999px;
        padding: 4px 10px;
        background: #faf5f7;
    }
    .qty-control input {
        width: 42px;
        border: none;
        background: transparent;
        text-align: center;
        font-size: 14px;
        color: var(--text-1);
    }
    .qty-control input:focus { outline: none; }
    .qty-btn {
        width: 22px;
        height: 22px;
        border-radius: 50%;
        border: none;
        background: #fff;
        box-shadow: 0 2px 6px rgba(15,23,42,0.12);
        cursor: pointer;
        display: grid;
        place-items: center;
        font-weight: 700;
        color: var(--text-1);
    }
    .qty-btn:hover { background: #ffe5ec; }
    .btn-apply { padding: 8px 14px; font-size: 13px; }
    .cart-option-list { display: flex; flex-wrap: wrap; gap: 8px; list-style: none; margin: 0; padding: 0; }
    .cart-option-list li { background: rgba(255,122,162,0.12); padding: 6px 12px; border-radius: 999px; font-size: 13px; }
    .cart-option-list em { margin-left: 6px; color: var(--pink-1); font-style: normal; font-weight: 600; }

    .cart-group-header {
        display: flex;
        justify-content: flex-end;
        margin-bottom: 12px;
    }
    .cart-item-group .cart-clear-form {
        text-align: right;
        margin: 0;
    }

    .cart-sidebar { display: grid; gap: 20px; align-content: start; }
    .branch-select-card {
        background: #fff;
        border-radius: 20px;
        padding: 24px;
        box-shadow: 0 18px 36px rgba(15,23,42,0.08);
        display: grid;
        gap: 16px;
    }
    .branch-select-card h2 { margin: 0; font-size: 20px; }
    .branch-selected {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 16px;
        font-size: 15px;
    }
    .branch-selected-text { display: grid; gap: 4px; }
    .branch-selected-text strong { font-size: 17px; color: var(--text-1); }
    .branch-selected-text span { font-size: 13px; color: var(--text-2); }
    .branch-empty {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 12px;
        font-size: 15px;
        color: var(--text-2);
    }
    .branch-empty span { font-weight: 600; }
    .btn-sm { padding: 8px 16px; font-size: 14px; }
    .is-hidden { display: none !important; }
    body.modal-open { overflow: hidden; }
    .branch-modal-overlay {
        position: fixed;
        inset: 0;
        background: rgba(15, 23, 42, 0.45);
        z-index: 1000;
    }
    .branch-modal {
        position: fixed;
        inset: 0;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 20px;
        z-index: 1001;
    }
    .branch-modal-inner {
        background: #fff;
        border-radius: 24px;
        width: min(960px, 95vw);
        max-height: 90vh;
        display: flex;
        flex-direction: column;
        box-shadow: 0 24px 48px rgba(15,23,42,0.2);
        overflow: hidden;
    }
    .branch-modal-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        gap: 16px;
        padding: 20px 28px 16px;
    }
    .branch-modal-title { display: grid; gap: 6px; }
    .branch-modal-title h2 { margin: 0; font-size: 22px; }
    .branch-modal-title p { margin: 0; font-size: 14px; color: var(--text-2); }
    .branch-modal-close {
        background: none;
        border: 0;
        font-size: 24px;
        cursor: pointer;
        color: var(--text-2);
    }
    .branch-modal-close:hover { color: var(--text-1); }
    .branch-modal-actions {
        display: flex;
        gap: 12px;
        align-items: center;
        flex-wrap: wrap;
        padding: 0 28px 16px;
        border-bottom: 1px solid rgba(0,0,0,0.08);
    }
    .branch-modal-search {
        flex: 1;
        display: flex;
        gap: 8px;
        min-width: 0;
    }
    .branch-modal-search input {
        flex: 1;
        border-radius: 12px;
        border: 1px solid rgba(0,0,0,0.12);
        padding: 10px 14px;
        font-size: 15px;
        transition: border-color .2s ease, box-shadow .2s ease;
    }
    .branch-modal-search input:focus {
        outline: none;
        border-color: var(--pink-1);
        box-shadow: 0 0 0 3px rgba(255,122,162,0.18);
    }
    .branch-modal-body {
        padding: 24px 28px;
        overflow-y: auto;
    }
    .branch-modal-footer {
        padding: 16px 28px 24px;
        border-top: 1px solid rgba(0,0,0,0.05);
    }
    .branch-modal-grid {
        display: grid;
        gap: 18px;
        grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    }
    .branch-modal-empty {
        text-align: center;
        padding: 40px 0;
        color: var(--text-2);
    }
    .branch-card {
        background: var(--white);
        border-radius: 18px;
        box-shadow: var(--shadow);
        overflow: hidden;
        display: grid;
        grid-template-rows: 160px auto;
        cursor: pointer;
        transition: transform .12s ease, box-shadow .2s ease;
        will-change: transform;
    }
    .branch-card:hover { transform: translateY(-3px); box-shadow: 0 14px 34px rgba(0,0,0,0.12); }
    .branch-cover { background: linear-gradient(135deg, var(--pink-3), var(--pink-4)); position: relative; }
    .branch-cover img { width: 100%; height: 100%; object-fit: cover; display: block; }
    .branch-body { padding: 16px; display: grid; gap: 8px; }
    .branch-header { display:flex; justify-content: space-between; align-items: baseline; gap: 8px; }
    .branch-name { font-size: 18px; font-weight: 800; }
    .branch-meta { color: var(--text-2); font-size: 14px; display:flex; align-items:center; gap:6px; }
    .branch-meta .icon { display:inline-grid; place-items:center; width:16px; height:16px; color: var(--text-2); }
    .branch-address { overflow:hidden; text-overflow: ellipsis; display:-webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
    .branch-distance { color: var(--pink-1); font-size: 14px; font-weight: 800; white-space: nowrap; display: inline-flex; gap: 4px; align-items: baseline; }
    .branch-distance-value { font-size: 16px; font-weight: 800; }
    .branch-distance-unit { font-size: 12px; font-weight: 700; opacity: 0.85; }
    .badge-open { display: inline-block; padding: 4px 10px; border-radius: 999px; background: var(--pink-4); color: var(--pink-1); font-weight: 800; font-size: 12px; }
    .badge-open.is-closed { background: rgba(0,0,0,0.08); color: var(--text-2); }
    .summary-card, .add-card {
        background: #fff;
        border-radius: 20px;
        padding: 28px;
        box-shadow: 0 18px 36px rgba(15,23,42,0.08);
        display: grid; gap: 18px;
    }
    .summary-card h2 { margin: 0; font-size: 22px; }
    .summary-card dl { margin: 0; display: grid; gap: 12px; }
    .summary-card .row { display: flex; justify-content: space-between; color: var(--text-2); }
    .summary-card .total { display: flex; justify-content: space-between; font-size: 18px; font-weight: 700; color: var(--text-1); border-top: 1px solid rgba(0,0,0,0.06); padding-top: 12px; }
    .btn-block { width: 100%; }
    .btn-outline {
        background: #fff;
        border: 1px solid rgba(255,122,162,0.6);
        color: var(--pink-1);
        transition: box-shadow .2s ease, transform .08s ease, background .2s ease;
    }
    .btn-outline:hover {
        box-shadow: 0 8px 16px rgba(15, 23, 42, 0.12);
        border-color: var(--pink-1);
        background: rgba(255,122,162,0.08);
        color: var(--pink-1);
    }
    .btn-clear {
        padding: 8px 18px;
        border-radius: 999px;
        font-size: 13px;
        font-weight: 600;
        color: var(--pink-1);
    }
    .btn-clear:hover {
        color: var(--pink-1);
        border-color: var(--pink-1);
    }
    .btn-disabled { opacity: 0.6; pointer-events: none; }

    .add-card h3 { margin: 0; font-size: 18px; }
    .add-card p { margin: 0; color: var(--text-2); font-size: 14px; }
    .input-group { display: grid; gap: 6px; font-size: 14px; }
    .input-group span { color: var(--text-2); font-weight: 600; }
    .input-group input {
        border-radius: 12px;
        border: 1px solid rgba(0,0,0,0.08);
        padding: 10px 12px;
        font-size: 15px;
        transition: border-color .2s ease, box-shadow .2s ease;
    }
    .input-group input:focus {
        outline: none;
        border-color: var(--pink-1);
        box-shadow: 0 0 0 3px rgba(255,122,162,0.18);
    }

    @media (max-width: 1024px) {
        .cart-shell { grid-template-columns: 1fr; }
        .cart-sidebar { order: 0; }
        .branch-select-card.branch-card-mobile { margin-top: 0; }
    }
</style>

<script>
    // 브라우저 뒤로가기 캐시로 인해 비워진 장바구니가 다시 보이는 문제 방지
    window.addEventListener('pageshow', function(e) {
        if (e.persisted) {
            try { window.location.reload(); } catch (_) {}
        }
    });

    // branch-selection.js가 로드될 때까지 대기
    function waitForBranchSelection(callback) {
        if (window.branchSelection) {
            callback();
        } else {
            setTimeout(function() { waitForBranchSelection(callback); }, 50);
        }
    }

    const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
    const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
    const csrfToken = csrfTokenMeta ? csrfTokenMeta.getAttribute('content') : null;
    const csrfHeader = csrfHeaderMeta ? csrfHeaderMeta.getAttribute('content') : null;
    const withCsrf = (headers = {}) => {
        if (csrfToken && csrfHeader) {
            return Object.assign({}, headers, { [csrfHeader]: csrfToken });
        }
        return headers;
    };

    const cartBaseUrl = '<c:url value="/users/cart/" />';

    const clampQuantity = value => Math.max(1, Number.isNaN(value) ? 1 : value);

    const branchModal = document.getElementById('branchSelectModal');
    const branchModalOverlay = document.getElementById('branchSelectOverlay');
    const branchModalGrid = document.getElementById('branchModalGrid');
    const branchModalCloseBtn = branchModal ? branchModal.querySelector('.branch-modal-close') : null;
    const branchModalLoadMore = document.getElementById('branchModalLoadMore');
    const branchModalSearchInput = document.getElementById('branchModalSearchInput');
    const branchModalSearchButton = document.getElementById('branchModalSearchButton');
    const branchModalLocateButton = document.getElementById('branchModalLocateButton');
    const branchOpenButtons = document.querySelectorAll('[data-open-branch-modal]');
    const branchEmptyState = document.getElementById('branchEmptyState');
    const branchSelectedInfo = document.getElementById('branchSelectedInfo');
    const branchSelectedNameEl = document.getElementById('branchSelectedName');
    const branchSelectedIdEl = document.getElementById('branchSelectedId');
    const selectedBranchIdInput = document.getElementById('selectedBranchIdInput');
    const selectedBranchNameInput = document.getElementById('selectedBranchNameInput');
    const orderButton = document.querySelector('.summary-card .btn-primary');
    const cartHeaderBar = document.querySelector('.cart-header-bar');
    const branchCard = document.querySelector('.branch-select-card');
    const branchOriginalParent = branchCard ? branchCard.parentElement : null;
    const branchOriginalNextSibling = branchCard ? branchCard.nextElementSibling : null;
    const mobileLayoutQuery = typeof window.matchMedia === 'function' ? window.matchMedia('(max-width: 1024px)') : null;

    // 좁은 화면에서 매장 카드가 장바구니 헤더 아래에 오도록 이동
    const moveBranchCardForMobile = () => {
        if (!branchCard || !cartHeaderBar) return;
        if (branchCard.previousElementSibling === cartHeaderBar) return;
        cartHeaderBar.insertAdjacentElement('afterend', branchCard);
        branchCard.classList.add('branch-card-mobile');
    };

    const restoreBranchCardForDesktop = () => {
        if (!branchCard || !branchOriginalParent) return;
        if (branchCard.parentElement === branchOriginalParent) return;
        if (branchOriginalNextSibling && branchOriginalParent.contains(branchOriginalNextSibling)) {
            branchOriginalParent.insertBefore(branchCard, branchOriginalNextSibling);
        } else {
            branchOriginalParent.appendChild(branchCard);
        }
        branchCard.classList.remove('branch-card-mobile');
    };

    const syncResponsiveLayout = () => {
        if (!mobileLayoutQuery) return;
        if (mobileLayoutQuery.matches) {
            moveBranchCardForMobile();
        } else {
            restoreBranchCardForDesktop();
        }
    };

    syncResponsiveLayout();
    if (mobileLayoutQuery) {
        if (typeof mobileLayoutQuery.addEventListener === 'function') {
            mobileLayoutQuery.addEventListener('change', syncResponsiveLayout);
        } else if (typeof mobileLayoutQuery.addListener === 'function') {
            mobileLayoutQuery.addListener(syncResponsiveLayout);
        }
    }

    let modalNextPage = branchModal && branchModal.dataset.nextPage ? parseInt(branchModal.dataset.nextPage, 10) : null;
    const modalPageSize = branchModal && branchModal.dataset.pageSize ? parseInt(branchModal.dataset.pageSize, 10) : 6;
    let branchModalQuery = '';
    let branchModalLat = null;
    let branchModalLng = null;
    const branchModalRadius = 2;

    const toggleBodyScroll = shouldLock => {
        if (shouldLock) {
            document.body.classList.add('modal-open');
        } else {
            document.body.classList.remove('modal-open');
        }
    };

    const toggleBranchSections = hasSelection => {
        if (branchEmptyState && branchSelectedInfo) {
            if (hasSelection) {
                branchEmptyState.classList.add('is-hidden');
                branchSelectedInfo.classList.remove('is-hidden');
            } else {
                branchEmptyState.classList.remove('is-hidden');
                branchSelectedInfo.classList.add('is-hidden');
            }
        }
        if (orderButton) {
            orderButton.disabled = !hasSelection;
            orderButton.classList.toggle('btn-disabled', !hasSelection);
        }
    };

    const setBranchSelection = (branchId, branchName, extra) => {
        const idValue = branchId ? String(branchId).trim() : '';
        const nameValue = branchName ? String(branchName).trim() : '';

        if (selectedBranchIdInput) {
            selectedBranchIdInput.value = idValue;
        }
        if (selectedBranchNameInput) {
            selectedBranchNameInput.value = nameValue;
        }

        if (branchSelectedNameEl) {
            branchSelectedNameEl.textContent = nameValue || '선택된 매장';
        }
        if (branchSelectedIdEl) {
            branchSelectedIdEl.textContent = idValue ? `ID ${idValue}` : '';
        }

        toggleBranchSections(Boolean(idValue));

        if (window.branchSelection && typeof window.branchSelection.save === 'function') {
            if (idValue) {
                window.branchSelection.save({ id: idValue, name: nameValue });
            } else if (typeof window.branchSelection.clear === 'function') {
                window.branchSelection.clear();
            }
        }
    };

    const openBranchModal = () => {
        if (!branchModal || !branchModalOverlay) return;
        branchModal.classList.remove('is-hidden');
        branchModalOverlay.classList.remove('is-hidden');
        toggleBodyScroll(true);
    };

    const closeBranchModal = () => {
        if (!branchModal || !branchModalOverlay) return;
        branchModal.classList.add('is-hidden');
        branchModalOverlay.classList.add('is-hidden');
        toggleBodyScroll(false);
    };

    const distKm = (lat1, lng1, lat2, lng2) => {
        const R = 6371;
        const toRad = d => d * Math.PI / 180;
        const dLat = toRad(lat2 - lat1);
        const dLng = toRad(lng2 - lng1);
        const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
            Math.sin(dLng / 2) * Math.sin(dLng / 2);
        const c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    };

    const distanceParts = km => {
        if (!Number.isFinite(km)) return null;
        if (km < 1) {
            const meters = Math.round(km * 1000);
            return { value: String(meters), unit: 'm' };
        }
        return { value: km.toFixed(1), unit: 'km' };
    };

    const renderDistances = (lat, lng, root) => {
        if (lat == null || lng == null) return;
        const scope = root || document;
        scope.querySelectorAll('.branch-card').forEach(card => {
            const cardLat = parseFloat(card.getAttribute('data-lat'));
            const cardLng = parseFloat(card.getAttribute('data-lng'));
            if (!Number.isFinite(cardLat) || !Number.isFinite(cardLng)) return;
            const km = distKm(lat, lng, cardLat, cardLng);
            const distanceEl = card.querySelector('.branch-distance');
            if (distanceEl) {
                const parts = distanceParts(km);
                distanceEl.textContent = '';
                if (parts) {
                    const valueSpan = document.createElement('span');
                    valueSpan.className = 'branch-distance-value';
                    valueSpan.textContent = parts.value;
                    const unitSpan = document.createElement('span');
                    unitSpan.className = 'branch-distance-unit';
                    unitSpan.textContent = parts.unit;
                    distanceEl.appendChild(valueSpan);
                    distanceEl.appendChild(unitSpan);
                }
            }
        });
    };

    const minutesFromHm = str => {
        if (!str) return null;
        const parts = String(str).split(':');
        const h = parseInt(parts[0], 10);
        const m = parseInt(parts[1] || '0', 10);
        if (!Number.isFinite(h) || !Number.isFinite(m)) return null;
        return h * 60 + m;
    };

    const isOpenNow = (openStr, closeStr) => {
        const open = minutesFromHm(openStr);
        const close = minutesFromHm(closeStr);
        if (open == null || close == null) return false;
        const now = new Date();
        const current = now.getHours() * 60 + now.getMinutes();
        if (open === close) return true;
        if (close > open) return current >= open && current < close;
        return current >= open || current < close;
    };

    const renderOpenBadges = root => {
        if (!root) return;
        root.querySelectorAll('.badge-open').forEach(badge => {
            const open = badge.getAttribute('data-open');
            const close = badge.getAttribute('data-close');
            if (!open || !close) {
                badge.textContent = '';
                return;
            }
            const openNow = isOpenNow(open, close);
            badge.textContent = openNow ? '주문 가능' : '영업 종료';
            badge.classList.toggle('is-closed', !openNow);
        });
    };

    const buildBranchPageUrl = page => {
        const url = new URL('/branch/page', window.location.origin);
        url.searchParams.set('page', page);
        url.searchParams.set('size', modalPageSize);
        if (branchModalQuery) {
            url.searchParams.set('q', branchModalQuery);
        } else if (branchModalLat != null && branchModalLng != null) {
            url.searchParams.set('lat', branchModalLat);
            url.searchParams.set('lng', branchModalLng);
            url.searchParams.set('radius', branchModalRadius);
        }
        return url;
    };

    const fetchBranches = async ({ page, append } = { page: 0, append: false }) => {
        if (!branchModal) return;
        try {
            if (append && branchModalLoadMore) {
                branchModalLoadMore.disabled = true;
            }
            const res = await fetch(buildBranchPageUrl(page), { headers: { 'X-Requested-With': 'XMLHttpRequest' } });
            if (!res.ok) throw new Error('매장 목록을 불러오지 못했습니다.');
            const html = await res.text();
            if (!append && branchModalGrid) {
                branchModalGrid.innerHTML = '';
            }
            if (html && html.trim().length > 0 && branchModalGrid) {
                const temp = document.createElement('div');
                temp.innerHTML = html;
                renderOpenBadges(temp);
                renderDistances(branchModalLat, branchModalLng, temp);
                Array.from(temp.children).forEach(child => branchModalGrid.appendChild(child));
            } else if (!append && branchModalGrid) {
                branchModalGrid.innerHTML = '<div class="branch-modal-empty">조건에 맞는 매장이 없습니다.</div>';
            }
            const hasNext = res.headers.get('X-Has-Next') === 'true';
            modalNextPage = hasNext ? page + 1 : null;
            if (branchModalLoadMore) {
                branchModalLoadMore.classList.toggle('is-hidden', !hasNext);
                branchModalLoadMore.disabled = false;
            }
            if (branchModalGrid) {
                renderDistances(branchModalLat, branchModalLng, branchModalGrid);
            }
        } catch (error) {
            console.error(error);
            if (append && branchModalLoadMore) {
                branchModalLoadMore.disabled = false;
            }
        }
    };

    const handleBranchSearch = () => {
        if (!branchModalSearchInput) return;
        branchModalQuery = branchModalSearchInput.value.trim();
        if (branchModalQuery.length > 0) {
            branchModalLat = null;
            branchModalLng = null;
        }
        fetchBranches({ page: 0, append: false });
    };

    const resetLocateButtonLabel = label => {
        if (branchModalLocateButton) {
            branchModalLocateButton.disabled = false;
            branchModalLocateButton.textContent = label || '내 주변 매장';
        }
    };

    const handleBranchLocate = () => {
        if (!branchModalLocateButton) return;
        const originalText = branchModalLocateButton.textContent;
        if (!('geolocation' in navigator)) {
            alert('이 브라우저는 위치 정보를 지원하지 않습니다.');
            return;
        }
        branchModalLocateButton.disabled = true;
        branchModalLocateButton.textContent = '위치 확인 중...';
        navigator.geolocation.getCurrentPosition(position => {
            branchModalLat = position.coords.latitude;
            branchModalLng = position.coords.longitude;
            branchModalQuery = '';
            if (branchModalSearchInput) {
                branchModalSearchInput.value = '';
            }
            fetchBranches({ page: 0, append: false }).finally(() => resetLocateButtonLabel(originalText));
        }, () => {
            alert('위치 정보를 가져올 수 없습니다. 위치 권한을 확인해주세요.');
            resetLocateButtonLabel(originalText);
        }, { enableHighAccuracy: true, timeout: 5000 });
    };

    const hydrateSelectionFromServer = () => {
        if (!window.branchSelection) {
            console.warn('branchSelection이 아직 로드되지 않았습니다.');
            return;
        }

        const initialId = selectedBranchIdInput ? selectedBranchIdInput.value : '';
        const initialName = selectedBranchNameInput ? selectedBranchNameInput.value : '';

        if (initialId) {
            if (initialName) {
                setBranchSelection(initialId, initialName);
            } else {
                setBranchSelection(initialId, '매장 정보를 불러오는 중...');
                fetch(`/branch/info/${initialId}`)
                    .then(res => {
                        if (!res.ok) throw new Error('Failed to load branch info');
                        return res.json();
                    })
                    .then(data => {
                        setBranchSelection(String(data.id), data.name || `ID ${data.id}`);
                    })
                    .catch(err => {
                        console.error(err);
                        setBranchSelection(initialId, '매장 정보를 불러오지 못했습니다');
                    });
            }
        } else {
            const stored = window.branchSelection.load();
            console.log('localStorage에서 로드한 매장 정보:', stored);
            if (stored && stored.id) {
                setBranchSelection(stored.id, stored.name);
            } else {
                toggleBranchSections(false);
            }
        }
    };

    const handleBranchCardClick = event => {
        const card = event.target.closest('.branch-card');
        if (!card || !branchModalGrid || !branchModalGrid.contains(card)) return;
        const branchId = card.getAttribute('data-branch-id');
        const branchName = card.getAttribute('data-name') || (card.querySelector('.branch-name') ? card.querySelector('.branch-name').textContent.trim() : '선택한 매장');
        setBranchSelection(branchId, branchName);
        closeBranchModal();
    };

    const loadMoreBranches = () => {
        if (modalNextPage == null) {
            if (branchModalLoadMore) branchModalLoadMore.classList.add('is-hidden');
            return;
        }
        fetchBranches({ page: modalNextPage, append: true });
    };

    if (branchModalGrid) {
        renderOpenBadges(branchModalGrid);
        renderDistances(branchModalLat, branchModalLng, branchModalGrid);
        branchModalGrid.addEventListener('click', handleBranchCardClick);
    }
    branchOpenButtons.forEach(btn => btn.addEventListener('click', openBranchModal));
    if (branchModalOverlay) {
        branchModalOverlay.addEventListener('click', closeBranchModal);
    }
    if (branchModalCloseBtn) {
        branchModalCloseBtn.addEventListener('click', closeBranchModal);
    }
    if (branchModalSearchButton) {
        branchModalSearchButton.addEventListener('click', handleBranchSearch);
    }
    if (branchModalSearchInput) {
        branchModalSearchInput.addEventListener('keydown', event => {
            if (event.key === 'Enter') {
                event.preventDefault();
                handleBranchSearch();
            }
        });
    }
    if (branchModalLocateButton) {
        branchModalLocateButton.addEventListener('click', handleBranchLocate);
    }
    if (branchModalLoadMore) {
        branchModalLoadMore.addEventListener('click', loadMoreBranches);
    }
    document.addEventListener('keydown', event => {
        if (event.key === 'Escape' && branchModal && !branchModal.classList.contains('is-hidden')) {
            closeBranchModal();
        }
    });

    // branchSelection이 로드된 후에 실행
    waitForBranchSelection(hydrateSelectionFromServer);

    // 주문하기 버튼: 선택된 매장 ID를 쿼리스트링으로 넘겨 결제 페이지로 이동
    if (orderButton) {
        orderButton.addEventListener('click', () => {
            if (orderButton.disabled) return;
            const id = selectedBranchIdInput ? String(selectedBranchIdInput.value || '').trim() : '';
            const url = new URL('/payments/cart', window.location.origin);
            if (id) url.searchParams.set('branchId', id);
            window.location.href = url.toString();
        });
    }

    document.querySelectorAll('.qty-control').forEach(control => {
        const input = control.querySelector('input');
        control.addEventListener('click', event => {
            const btn = event.target.closest('.qty-btn');
            if (!btn) return;
            const type = btn.dataset.type;
            let current = clampQuantity(parseInt(input.value, 10));
            current = type === 'minus' ? Math.max(1, current - 1) : current + 1;
            input.value = current;
        });

        input.addEventListener('change', () => {
            input.value = clampQuantity(parseInt(input.value, 10));
        });
    });

    document.querySelectorAll('[data-update]').forEach(button => {
        button.addEventListener('click', () => {
            const item = button.closest('.cart-item');
            if (!item) return;

            const input = item.querySelector('.qty-control input');
            const quantity = clampQuantity(parseInt(input?.value, 10));
            const cartItemId = button.dataset.update;
            if (!cartItemId) return;

            const requestUrl = cartBaseUrl + cartItemId;
            fetch(requestUrl, {
                method: 'PATCH',
                headers: withCsrf({ 'Content-Type': 'application/json' }),
                credentials: 'include',
                body: JSON.stringify({ quantity })
            })
                .then(res => {
                    if (!res.ok) throw new Error(`수정 실패: ${res.status}`);
                    return res.text();
                })
                .then(() => window.location.reload())
                .catch(err => console.error(err));
        });
    });

    const clearForm = document.querySelector('.cart-clear-form');
    if (clearForm) {
        clearForm.addEventListener('submit', event => {
            event.preventDefault();
            const formData = new URLSearchParams();
            Array.from(new FormData(clearForm).entries()).forEach(([key, value]) => {
                formData.append(key, value);
            });

            fetch(clearForm.action, {
                method: 'POST',
                headers: withCsrf({
                    'Content-Type': 'application/x-www-form-urlencoded'
                }),
                credentials: 'include',
                body: formData.toString()
            })
                .then(res => {
                    if (!res.ok) {
                        throw new Error(`Failed to clear cart: ${res.status}`);
                    }
                })
                .then(() => window.location.reload())
                .catch(error => {
                    console.error(error);
                    alert('장바구니를 비우지 못했습니다. 잠시 후 다시 시도해주세요.');
                });
        });
    }

    document.addEventListener('click', e => {
        const btn = e.target.closest('[data-delete]');
        if (!btn) return;
        const cartItemId = btn.dataset.delete;
        if (!confirm('정말 삭제하시겠습니까?')) return;

        const requestUrl = cartBaseUrl + cartItemId;
        fetch(requestUrl, {
            method: 'DELETE',
            headers: withCsrf(),
        })
            .then(res => {
                if (!res.ok) throw new Error(`삭제 실패 (${res.status})`);
                btn.closest('.cart-item').remove();
            })
            .then(() => window.location.reload())
            .catch(err => {
                alert(err.message || '삭제 실패');
            });
    });
</script>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>