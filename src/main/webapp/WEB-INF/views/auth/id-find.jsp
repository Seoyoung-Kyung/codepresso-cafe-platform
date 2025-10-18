<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" session="false" %>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body class="auth-page">
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<style>
    .auth-page {
        background: linear-gradient(160deg, var(--pink-4), #fff 55%);
    }

    .hero.auth-hero {
        background: transparent;
        padding: 72px 0 96px;
    }

    .hero-card.login-card {
        background: #fff !important;
        border-radius: 28px;
        box-shadow: 0 32px 60px rgba(15,23,42,0.15);
        border: 1px solid rgba(255,122,162,0.18);
        grid-template-columns: 1fr !important;
        max-width: 640px;
        margin: 0 auto;
        padding: 40px 44px;
        text-align: center;
    }
</style>

<main class="hero auth-hero">
    <div class="container">
        <!--
          아이디 찾기 화면 카드
          - 분홍 테마(기존 index/signup과 일관)
          - 단일 컬럼, 가운데 정렬
        -->
        <div class="hero-card login-card">
            <div>
                <div class="badge">CodePress · 아이디 찾기</div>
                <h1>아이디를 잊으셨나요?</h1>
                <p>닉네임과 이메일을 입력하시면 인증번호를 발송해드립니다.</p>

                <style>
                    /*
                     아이디 찾기 전용 스타일
                     - login과 유사하게 단일 컬럼 + 폭 제한
                    */
                    .login-card h1 { margin: 0 0 24px; } /* 제목 아래 여백 확대 */
                    .login-card p { margin: 0 0 24px; color: #666; } /* 설명 문구 */
                    .login-form { display: grid; gap: 14px; max-width: 380px; margin: 0 auto; text-align: left; }
                    .field { display: grid; gap: 6px; }
                    .row { display: flex; align-items: center; justify-content: space-between; }
                    label { font-weight: 700; }
                    input[type=text], input[type=email] {
                        width: 100%; padding: 12px; border-radius: 10px;
                        border: 1px solid rgba(255,122,162,0.35);
                        outline: none; transition: border .2s, box-shadow .2s;
                        background: rgba(255,255,255,0.9);
                    }
                    input:focus { border-color: var(--pink-1); box-shadow: 0 0 0 3px rgba(255,122,162,.22); }
                    input[type=checkbox] { accent-color: var(--pink-1); }
                    .actions { display: grid; gap: 10px; margin-top: 10px; } /* 버튼 위 여백 */
                    .sub-actions { display: flex; justify-content: space-between; align-items: center; margin-top: 6px; margin-bottom: 6px; }
                    .links { display: flex; justify-content: center; align-items: center; gap: 14px; margin-top: 14px; font-weight: 700; }
                    .links a { color: var(--pink-1); text-decoration: none; }
                    .links span { color: var(--text-2); font-weight: 500; }
                    .verification-section { display: none; margin-top: 20px; padding: 20px; background: #f8f9fa; border-radius: 10px; }
                    .success-section { display: none; margin-top: 20px; padding: 20px; background: #d4edda; border-radius: 10px; color: #155724; }
                    .error-message { color: #dc3545; font-size: 14px; margin-top: 5px; }
                    .success-message { color: #28a745; font-size: 14px; margin-top: 5px; }
                </style>

                <!-- 아이디 찾기 폼 -->
                <form class="login-form" id="idFindForm">
                    <div class="field">
                        <label for="nickname">닉네임</label>
                        <input type="text" id="nickname" name="nickname" placeholder="닉네임을 입력하세요" required />
                    </div>

                    <div class="field">
                        <label for="email">이메일</label>
                        <input type="email" id="email" name="email" placeholder="이메일을 입력하세요" required />
                    </div>

                    <div class="actions">
                        <button type="submit" class="btn btn-primary" id="findIdBtn">인증번호 발송</button>
                    </div>
                </form>

                <!-- 인증번호 입력 섹션 -->
                <div class="verification-section" id="verificationSection">
                    <h3>인증번호를 입력하세요</h3>
                    <form class="login-form" id="verificationForm">
                        <div class="field">
                            <label for="verificationCode">인증번호</label>
                            <input type="text" id="verificationCode" name="verificationCode" placeholder="6자리 인증번호를 입력하세요" maxlength="6" required />
                        </div>
                        <div class="actions">
                            <button type="submit" class="btn btn-primary" id="verifyBtn">인증 확인</button>
                        </div>
                    </form>
                </div>

                <!-- 성공 섹션 -->
                <div class="success-section" id="successSection">
                    <h3>아이디를 찾았습니다!</h3>
                    <p><strong>아이디:</strong> <span id="foundAccountId"></span></p>
                    <div class="actions">
                        <a href="/auth/login" class="btn btn-primary">로그인하기</a>
                    </div>
                </div>

                <!-- 에러 메시지 -->
                <div id="errorMessage" class="error-message" style="display: none;"></div>
                <div id="successMessage" class="success-message" style="display: none;"></div>

                <!-- 하단 링크 -->
                <div class="links">
                    <a href="/auth/password-find">비밀번호 찾기</a>
                    <a href="/auth/login">로그인</a>
                </div>
            </div>
        </div>
    </div>
</main>

<script>
    // 전역 변수
    let currentEmail = '';

    // 페이지 로드 시 이벤트 리스너 설정
    document.addEventListener('DOMContentLoaded', function() {
        setupEventListeners();
    });

    // 이벤트 리스너 설정
    function setupEventListeners() {
        // 아이디 찾기 폼 제출
        document.getElementById('idFindForm').addEventListener('submit', function(e) {
            e.preventDefault();
            findId();
        });

        // 인증번호 확인 폼 제출
        document.getElementById('verificationForm').addEventListener('submit', function(e) {
            e.preventDefault();
            verifyCode();
        });
    }

    // 아이디 찾기
    function findId() {
        const nickname = document.getElementById('nickname').value.trim();
        const email = document.getElementById('email').value.trim();

        if (!nickname || !email) {
            showError('닉네임과 이메일을 모두 입력해주세요.');
            return;
        }

        // 버튼 비활성화
        const btn = document.getElementById('findIdBtn');
        btn.disabled = true;
        btn.textContent = '발송 중...';

        // API 호출
        fetch('/api/auth/find-id', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                nickname: nickname,
                email: email
            }),
            credentials: 'same-origin'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                currentEmail = email;
                showSuccess('인증번호가 발송되었습니다. 이메일을 확인해주세요.');
                document.getElementById('verificationSection').style.display = 'block';
                document.getElementById('idFindForm').style.display = 'none';
            } else {
                showError(data.message || '아이디 찾기에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showError('아이디 찾기 중 오류가 발생했습니다.');
        })
        .finally(() => {
            // 버튼 활성화
            btn.disabled = false;
            btn.textContent = '인증번호 발송';
        });
    }

    // 인증번호 확인
    function verifyCode() {
        const verificationCode = document.getElementById('verificationCode').value.trim();

        if (!verificationCode) {
            showError('인증번호를 입력해주세요.');
            return;
        }

        // 버튼 비활성화
        const btn = document.getElementById('verifyBtn');
        btn.disabled = true;
        btn.textContent = '확인 중...';

        // API 호출
        fetch('/api/auth/verify-id-code', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: currentEmail,
                verificationCode: verificationCode
            }),
            credentials: 'same-origin'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                document.getElementById('foundAccountId').textContent = data.accountId;
                document.getElementById('successSection').style.display = 'block';
                document.getElementById('verificationSection').style.display = 'none';
                showSuccess('인증이 완료되었습니다!');
            } else {
                showError(data.message || '인증번호가 일치하지 않습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showError('인증 중 오류가 발생했습니다.');
        })
        .finally(() => {
            // 버튼 활성화
            btn.disabled = false;
            btn.textContent = '인증 확인';
        });
    }

    // 에러 메시지 표시
    function showError(message) {
        const errorDiv = document.getElementById('errorMessage');
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
        
        // 성공 메시지 숨기기
        document.getElementById('successMessage').style.display = 'none';
        
        // 5초 후 자동 숨김
        setTimeout(() => {
            errorDiv.style.display = 'none';
        }, 5000);
    }

    // 성공 메시지 표시
    function showSuccess(message) {
        const successDiv = document.getElementById('successMessage');
        successDiv.textContent = message;
        successDiv.style.display = 'block';
        
        // 에러 메시지 숨기기
        document.getElementById('errorMessage').style.display = 'none';
    }
</script>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
