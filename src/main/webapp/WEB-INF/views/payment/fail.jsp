<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<main class="hero payment-fail-page">
    <div class="container">
        <div class="fail-card">
            <h2>결제 실패</h2>
            <p id="code"></p>
            <p id="message"></p>
        </div>
    </div>
</main>

<style>
    .payment-fail-page {
        padding-top: 40px;
        padding-bottom: 40px;
        min-height: 60vh;
        display: flex;
        align-items: center;
    }

    .fail-card {
        background: var(--white);
        border-radius: 18px;
        padding: 48px 24px;
        text-align: center;
        box-shadow: var(--shadow);
        border: 1px solid rgba(0,0,0,0.05);
        max-width: 600px;
        margin: 0 auto;
    }

    .fail-card h2 {
        font-size: 28px;
        font-weight: 700;
        color: #dc3545;
        margin-bottom: 32px;
    }

    .fail-card p {
        font-size: 16px;
        color: var(--text-2);
        margin: 12px 0;
        padding: 8px 16px;
        background: #f8d7da;
        border-radius: 8px;
        border: 1px solid #f5c6cb;
    }

    /* 모바일 반응형 */
    @media (max-width: 768px) {
        .payment-fail-page {
            padding: 16px;
        }

        .fail-card {
            padding: 32px 16px;
        }

        .fail-card h2 {
            font-size: 24px;
        }
    }
</style>

<script>
    const urlParams = new URLSearchParams(window.location.search);

    const codeElement = document.getElementById("code");
    const messageElement = document.getElementById("message");

    codeElement.textContent = "에러코드: " + urlParams.get("code");
    messageElement.textContent = "실패 사유: " + urlParams.get("message");
</script>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>