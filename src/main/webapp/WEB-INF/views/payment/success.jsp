<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<main class="hero payment-success-page">
    <div class="container">
        <div class="success-card">
            <h1>결제 성공!</h1>
            <p id="redirectMessage" style="display: none; color: var(--pink-1); font-weight: 600; margin-top: 20px;">
                주문 상세 페이지로 이동합니다...
            </p>
        </div>
    </div>
</main>

<style>
    .payment-success-page {
        padding-top: 40px;
        padding-bottom: 40px;
        min-height: 60vh;
        display: flex;
        align-items: center;
    }

    .success-card {
        background: var(--white);
        border-radius: 18px;
        padding: 48px 24px;
        text-align: center;
        box-shadow: var(--shadow);
        border: 1px solid rgba(0,0,0,0.05);
        max-width: 600px;
        margin: 0 auto;
    }

    .success-card h1 {
        font-size: 32px;
        font-weight: 700;
        color: var(--text-1);
        margin-bottom: 32px;
    }

    .success-card p {
        font-size: 16px;
        color: var(--pink-1);
        margin: 12px 0;
        font-weight: 600;
    }

    /* 모바일 반응형 */
    @media (max-width: 768px) {
        .payment-success-page {
            padding: 16px;
        }

        .success-card {
            padding: 32px 16px;
        }

        .success-card h1 {
            font-size: 28px;
        }
    }
</style>

<script>
    // 쿼리 파라미터 값이 결제 요청할 때 보낸 데이터와 동일한지 반드시 확인하세요.
    // 클라이언트에서 결제 금액을 조작하는 행위를 방지할 수 있습니다.
    const urlParams = new URLSearchParams(window.location.search);
    const paymentKey = urlParams.get("paymentKey");
    const orderId = urlParams.get("orderId");
    const amount = urlParams.get("amount");

    async function createOrderAfterPayment() {
        try {
            // 세션 스토리지에서 주문 정보 가져오기
            const orderDataStr = sessionStorage.getItem('tossOrderInfo');
            if (!orderDataStr) {
                throw new Error('주문 정보를 찾을 수 없습니다.');
            }

            const orderData = JSON.parse(orderDataStr);
            console.log('세션에서 가져온 주문 정보:', orderData);

            // 토스페이먼츠 결제 성공 시 주문 생성 API 호출
            const requestData = {
                memberId: orderData.memberId || 1, // 기본값 설정
                branchId: orderData.branchId || 1, // 기본값 설정
                paymentKey: paymentKey,
                orderId: orderId,
                amount: parseInt(amount),
                orderItems: orderData.orderItems || [],
                isTakeout: orderData.orderType === 'takeout',
                pickupTime: orderData.pickupTime,
                requestNote: orderData.requestNote || '',
                pickupMethod: orderData.package === 'carrier' ? '전체포장(케리어)' : '포장안함',
                useCoupon: orderData.useCoupon || false,
                discountAmount: orderData.discountAmount || 0,
                isFromCart: orderData.isFromCart || false,
                finalAmount: parseInt(amount)
            };

            console.log('주문 생성 요청 데이터:', requestData);

            const response = await fetch("/api/payments/toss-success", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(requestData),
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || '주문 생성에 실패했습니다.');
            }

            const result = await response.json();
            console.log('주문 생성 성공:', result);

            // 주문 상세 페이지로 리다이렉트
            if (result.orderId) {
                // 세션 스토리지에 주문 상세 정보 저장
                sessionStorage.setItem('orderDetailData', JSON.stringify(result));
                
                // 리다이렉트 메시지 표시
                const redirectMessage = document.getElementById('redirectMessage');
                if (redirectMessage) {
                    redirectMessage.style.display = 'block';
                }
                
                // 2초 후 주문 상세 페이지로 이동
                setTimeout(() => {
                    window.location.href = '/orders/' + result.orderId;
                }, 2000);
            } else {
                throw new Error('주문 ID를 받지 못했습니다.');
            }

        } catch (error) {
            console.error('주문 생성 오류:', error);
            alert('주문 생성 중 오류가 발생했습니다: ' + error.message);
            
            // 오류 발생 시 주문 목록으로 이동
            window.location.href = '/orders';
        }
    }

    // 페이지 로드 시 주문 생성 실행
    document.addEventListener('DOMContentLoaded', function() {
        // 주문 생성 실행
        createOrderAfterPayment();
    });
</script>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>