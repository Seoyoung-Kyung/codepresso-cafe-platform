<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<main class="hero toss-checkout-page">
    <div class="container">
        <!-- 페이지 헤더 -->
        <div class="page-header">
            <button class="back-btn" onclick="history.back()">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                    <path d="M15 18L9 12L15 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
            </button>
            <h1 class="page-title">토스페이먼츠 결제</h1>
        </div>

        <!-- 토스페이먼츠 SDK -->
        <script src="https://js.tosspayments.com/v2/standard"></script>

        <!-- 결제 금액 표시 -->
        <div class="payment-amount">
            <h2 class="amount-title">결제 금액</h2>
            <div class="amount-display">
                <span class="amount-value" id="finalAmount">
                    <c:if test="${not empty totalAmount}">
                        <fmt:formatNumber value="${totalAmount}" type="currency" currencySymbol="₩"/>
                    </c:if>
                    <c:if test="${empty totalAmount}">
                        0원
                    </c:if>
                </span>
            </div>
        </div>



        <!-- 결제 UI -->
        <div class="payment-section">
            <h3>결제 수단</h3>
            <div id="payment-method"></div>
        </div>

        <!-- 이용약관 UI -->
        <div class="payment-section">
            <h3>이용약관</h3>
            <div id="agreement"></div>
        </div>

        <!-- 결제하기 버튼 -->
        <button class="payment-button" id="payment-button">
            결제하기
        </button>
    </div>
</main>

<style>
    /* 토스페이먼츠 결제 페이지 스타일 */
    .toss-checkout-page {
        padding-top: 40px;
        padding-bottom: 40px;
    }

    .page-header {
        display: flex;
        align-items: center;
        margin-bottom: 32px;
        gap: 16px;
    }

    .back-btn {
        background: var(--white);
        border: 1px solid rgba(0,0,0,0.08);
        color: var(--text-2);
        cursor: pointer;
        padding: 12px;
        border-radius: 12px;
        transition: all 0.2s;
        box-shadow: var(--shadow);
    }

    .back-btn:hover {
        background-color: var(--pink-4);
        border-color: var(--pink-2);
        color: var(--pink-1);
    }

    .page-title {
        font-size: 32px;
        font-weight: 800;
        color: var(--text-1);
        margin: 0;
    }

    .payment-amount {
        background: var(--white);
        border-radius: 18px;
        padding: 24px;
        margin-bottom: 24px;
        box-shadow: var(--shadow);
        border: 1px solid rgba(0,0,0,0.05);
        text-align: center;
    }

    .amount-title {
        font-size: 20px;
        font-weight: 700;
        color: var(--text-1);
        margin: 0 0 16px 0;
    }

    .amount-display {
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .amount-value {
        font-size: 32px;
        font-weight: 800;
        color: var(--pink-1);
    }



    .payment-section {
        background: var(--white);
        border-radius: 18px;
        padding: 24px;
        margin-bottom: 24px;
        box-shadow: var(--shadow);
        border: 1px solid rgba(0,0,0,0.05);
    }

    .payment-section h3 {
        font-size: 20px;
        font-weight: 700;
        color: var(--text-1);
        margin: 0 0 20px 0;
    }

    #payment-method {
        border: 1px solid #e1e5e9;
        border-radius: 12px;
        padding: 20px;
        margin-bottom: 16px;
        background: #fafafa;
    }

    /* 결제 수단 컨테이너 스타일 */
    #payment-method {
        min-height: 300px;
    }

    #agreement {
        border: 1px solid #e1e5e9;
        border-radius: 12px;
        padding: 20px;
        margin-bottom: 16px;
        background: #fafafa;
    }

    .payment-button {
        width: 100%;
        background: linear-gradient(135deg, var(--pink-1), var(--pink-2));
        color: var(--white);
        border: none;
        border-radius: 12px;
        padding: 16px 24px;
        font-size: 18px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.2s;
        box-shadow: 0 8px 16px rgba(255, 122, 162, 0.35);
    }

    .payment-button:hover {
        filter: brightness(1.02);
        transform: translateY(-1px);
    }

    .payment-button:disabled {
        background: #6c757d;
        cursor: not-allowed;
        transform: none;
    }

    .error-message {
        background: #f8d7da;
        color: #721c24;
        padding: 12px 16px;
        border-radius: 8px;
        margin-bottom: 16px;
        border: 1px solid #f5c6cb;
    }

    /* 반응형 */
    @media (max-width: 768px) {
        .toss-checkout-page {
            padding: 16px;
        }

        .payment-amount, .payment-section {
            padding: 16px;
        }

        .amount-value {
            font-size: 24px;
        }

        .page-title {
            font-size: 24px;
        }
    }
</style>

<script>
    // 토스페이먼츠 초기화
    async function main() {
        const button = document.getElementById("payment-button");

        // ------ 결제위젯 초기화 ------
        const clientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
        const tossPayments = TossPayments(clientKey);

        // 회원 결제
        const customerKey = "customer_" + Date.now() + "_" + Math.random().toString(36).substr(2, 9);
        const widgets = tossPayments.widgets({
            customerKey
        });

        // ------ 주문의 결제 금액 설정 ------
        const serverAmount = ${totalAmount != null ? totalAmount : 5000};
        console.log('toss-checkout.jsp - 서버에서 전달된 totalAmount:', ${totalAmount != null ? totalAmount : 'null'});
        console.log('toss-checkout.jsp - 최종 사용할 serverAmount:', serverAmount);
        
        await widgets.setAmount({
            currency: "KRW",
            value: serverAmount,
        });

        await Promise.all([
            // ------ 결제 UI 렌더링 ------
            widgets.renderPaymentMethods({
                selector: "#payment-method",
                variantKey: "DEFAULT"
            }),
            // ------ 이용약관 UI 렌더링 ------
            widgets.renderAgreement({
                selector: "#agreement",
                variantKey: "AGREEMENT"
            }),
        ]);





        // ------ '결제하기' 버튼 누르면 결제창 띄우기 ------
        button.addEventListener("click", async function () {
            // sessionStorage에서 주문 옵션 가져오기
            const checkoutOptions = JSON.parse(sessionStorage.getItem('checkoutOptions') || '{}');

            // localStorage에서 branch 정보 가져오기
            const getBranchInfo = () => {
                try {
                    const selected = window.branchSelection && window.branchSelection.load
                        ? window.branchSelection.load()
                        : null;
                    return {
                        id: selected && selected.id ? parseInt(selected.id) : 1,
                        name: selected && selected.name ? selected.name : '매장을 선택해주세요'
                    };
                } catch(e) {
                    return { id: 1, name: '매장을 선택해주세요' };
                }
            };
            const branchInfo = getBranchInfo();

            // 픽업 예정시간 계산
            const pickupMinutes = checkoutOptions.pickupMinutes || 5;
            const pickupTime = new Date(Date.now() + pickupMinutes * 60 * 1000).toISOString();

            // 주문 아이템 구성
            const orderItems = [
                <c:choose>
                    <%-- 직접결제인 경우 --%>
                    <c:when test="${not empty directItems}">
                        <c:forEach var="item" items="${directItems}" varStatus="status">
                        {
                            productId: ${item.productId},
                            quantity: ${item.quantity},
                            price: ${item.price},
                            optionIds: [
                                <c:if test="${not empty item.optionIds}">
                                    <c:forEach var="optId" items="${item.optionIds}" varStatus="optStatus">
                                        ${optId}<c:if test="${!optStatus.last}">,</c:if>
                                    </c:forEach>
                                </c:if>
                            ]
                        }<c:if test="${!status.last}">,</c:if>
                        </c:forEach>
                    </c:when>
                    <%-- 장바구니 결제인 경우 --%>
                    <c:when test="${not empty orderItems}">
                        <c:forEach var="item" items="${orderItems}" varStatus="status">
                        {
                            productId: ${item.productId},
                            quantity: ${item.quantity},
                            price: Math.round(${item.price} / ${item.quantity}),
                            optionIds: [
                                <c:if test="${not empty item.optionIds}">
                                    <c:forEach var="optId" items="${item.optionIds}" varStatus="optStatus">
                                        ${optId}<c:if test="${!optStatus.last}">,</c:if>
                                    </c:forEach>
                                </c:if>
                            ]
                        }<c:if test="${!status.last}">,</c:if>
                        </c:forEach>
                    </c:when>
                </c:choose>
            ];

            // 주문 정보를 세션 스토리지에 저장
            const orderInfo = {
                memberId: ${memberId != null ? memberId : 1},
                branchId: branchInfo.id,
                branchName: branchInfo.name,
                totalAmount: serverAmount,
                totalQuantity: ${totalQuantity != null ? totalQuantity : 1},
                orderType: checkoutOptions.orderType || 'takeout',
                package: checkoutOptions.packageType || 'none',
                pickupTime: pickupTime,
                requestNote: checkoutOptions.requestNote || '',
                useCoupon: checkoutOptions.useCoupon || false,
                discountAmount: checkoutOptions.discountAmount || 0,
                isFromCart: ${isFromCart != null ? isFromCart : false},
                orderItems: orderItems
            };

            sessionStorage.setItem('tossOrderInfo', JSON.stringify(orderInfo));
            console.log('주문 정보를 세션 스토리지에 저장:', orderInfo);

            // 주문명 구성
            const orderName = (() => {
                <c:choose>
                    <c:when test="${not empty directItems}">
                        const firstProductName = '${directItems[0].productName}';
                        const itemCount = ${directItemsCount};
                        return itemCount > 1 ? firstProductName + ' 외 ' + (itemCount - 1) + '건' : firstProductName;
                    </c:when>
                    <c:when test="${not empty orderItems}">
                        const firstProductName = '${orderItems[0].productName}';
                        const itemCount = ${orderItems.size()};
                        return itemCount > 1 ? firstProductName + ' 외 ' + (itemCount - 1) + '건' : firstProductName;
                    </c:when>
                    <c:otherwise>
                        return '상품 주문';
                    </c:otherwise>
                </c:choose>
            })();

            await widgets.requestPayment({
                orderId: "order_" + Date.now() + "_" + Math.floor(Math.random() * 1000),
                orderName: orderName,
                successUrl: window.location.origin + "/payments/success",
                failUrl: window.location.origin + "/payments/fail",
                customerEmail: '${memberEmail != null ? memberEmail : "customer@example.com"}',
                customerName: '${memberName != null ? memberName : "고객"}',
                customerMobilePhone: '${memberPhone != null ? memberPhone : "01000000000"}',
            });
        });
    }

    // 페이지 로드 시 실행
    document.addEventListener('DOMContentLoaded', function() {
        // 초기 금액 표시 (서버에서 전달된 금액 사용)
        const serverAmount = ${totalAmount != null ? totalAmount : 50000};
        document.getElementById('finalAmount').textContent = serverAmount.toLocaleString() + '원';
        main();
    });
</script>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>