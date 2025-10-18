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
          비밀번호 찾기 화면 카드
          - 분홍 테마(기존 login/signup과 일관)
          - 단일 컬럼, 가운데 정렬
        -->
        <div class="hero-card login-card">
            <div>
                <div class="badge">CodePress · 비밀번호 찾기</div>
                <h1>비밀번호를 잊으셨나요?</h1>
                <p>아이디와 이메일을 입력하시면 인증번호를 발급해드립니다.</p>

                <style>
                    /*
                     비밀번호 찾기 전용 스타일
                     - login과 유사하게 단일 컬럼 + 폭 제한
                    */
                    .login-card {
                        grid-template-columns: 1fr;
                        width: 100%;
                        max-width: 640px;
                        margin: 0 auto;
                        padding: 24px;
                        text-align: center; /* 상단 문구 중앙 */
                    }
                    .login-card h1 { margin: 0 0 24px; } /* 제목 아래 여백 확대 */
                    .login-card p { margin: 0 0 24px; color: #666; } /* 설명 문구 */
                    .password-find-form { display: grid; gap: 14px; max-width: 380px; margin: 0 auto; text-align: left; }
                    .field { display: grid; gap: 6px; }
                    label { font-weight: 700; }
                    input[type=text], input[type=email], input[type=password] {
                        width: 100%; padding: 12px; border-radius: 10px;
                        border: 1px solid rgba(255,122,162,0.35);
                        outline: none; transition: border .2s, box-shadow .2s;
                        background: rgba(255,255,255,0.9);
                    }
                    input:focus { border-color: var(--pink-1); box-shadow: 0 0 0 3px rgba(255,122,162,.22); }
                    .actions { display: grid; gap: 10px; margin-top: 10px; }
                    .link { color: var(--pink-1); text-decoration: none; font-weight: 700; text-align: center; display: inline-block; margin-top: 12px; }
                    .step { display: none; }
                    .step.active { display: block; }
                    .verification-info { 
                        background: #f8f9fa; 
                        padding: 12px; 
                        border-radius: 8px; 
                        margin: 12px 0; 
                        font-size: 14px; 
                        color: #666;
                    }
                </style>

                <!-- 1단계: 아이디/이메일 입력 -->
                <div id="step1" class="step active">
                    <form class="password-find-form" id="findForm">
                        <div class="field">
                            <label for="accountId">아이디</label>
                            <input type="text" id="accountId" name="accountId" placeholder="아이디 입력" required />
                        </div>

                        <div class="field">
                            <label for="email">이메일</label>
                            <input type="email" id="email" name="email" placeholder="이메일 입력" required />
                        </div>

                        <div class="actions">
                            <button type="submit" class="btn btn-primary">인증번호 발급</button>
                        </div>

                        <a class="link" href="/auth/login">로그인으로 돌아가기</a>
                    </form>
                </div>

                <!-- 2단계: 인증번호 입력 -->
                <div id="step2" class="step">
                    <form class="password-find-form" id="verificationForm">
                        <div class="verification-info">
                            <strong>인증번호가 이메일로 발송되었습니다!</strong><br>
                            이메일을 확인하고 인증번호를 입력해주세요.<br>
                        </div>

                        <div class="field">
                            <label for="verificationCode">인증번호</label>
                            <input type="text" id="verificationCode" name="verificationCode" placeholder="인증번호 입력" required />
                        </div>

                        <div class="actions">
                            <button type="submit" class="btn btn-primary">인증번호 확인</button>
                        </div>

                        <a class="link" href="#" onclick="goToStep1()">이전 단계로</a>
                    </form>
                </div>

                <!-- 3단계: 새 비밀번호 입력 -->
                <div id="step3" class="step">
                    <form class="password-find-form" id="resetForm">
                        <div class="field">
                            <label for="newPassword">새 비밀번호</label>
                            <input type="password" id="newPassword" name="newPassword" placeholder="새 비밀번호 입력 (8자 이상)" required />
                        </div>

                        <div class="field">
                            <label for="confirmPassword">비밀번호 확인</label>
                            <input type="password" id="confirmPassword" name="confirmPassword" placeholder="비밀번호 확인" required />
                        </div>

                        <div class="actions">
                            <button type="submit" class="btn btn-primary">비밀번호 변경</button>
                        </div>

                        <a class="link" href="#" onclick="goToStep2()">이전 단계로</a>
                    </form>
                </div>
            </div>
        </div>
    </div>
</main>

<script>
    let currentStep = 1;
    let userInfo = {};
    let verificationCode = '';

    // 1단계: 아이디/이메일로 사용자 확인
    document.getElementById('findForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const accountId = document.getElementById('accountId').value;
        const email = document.getElementById('email').value;
        
        try {
            const response = await fetch('/api/password/find', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ accountId, email })
            });
            
            const data = await response.json();
            
            if (data.success) {
                userInfo = { accountId, email };
                verificationCode = data.verificationCode; // 서버에서 받은 인증번호 저장
                goToStep2();
            } else {
                alert(data.message || '오류가 발생했습니다.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('네트워크 오류가 발생했습니다.');
        }
    });

    // 2단계: 인증번호 확인
    document.getElementById('verificationForm').addEventListener('submit', function(e) {
        e.preventDefault();
        
        const inputCode = document.getElementById('verificationCode').value;
        
        if (inputCode === verificationCode) {
            goToStep3();
        } else {
            alert('인증번호가 일치하지 않습니다.');
        }
    });

    // 3단계: 비밀번호 재설정
    document.getElementById('resetForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        
        if (newPassword !== confirmPassword) {
            alert('새 비밀번호와 확인 비밀번호가 일치하지 않습니다.');
            return;
        }
        
        if (newPassword.length < 8) {
            alert('비밀번호는 8자 이상이어야 합니다.');
            return;
        }
        
        try {
            const response = await fetch('/api/password/reset', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    ...userInfo,
                    verificationCode: verificationCode, // 실제 인증번호 사용
                    newPassword,
                    confirmPassword
                })
            });
            
            const data = await response.json();
            
            if (data.success) {
                alert('비밀번호가 성공적으로 변경되었습니다!');
                window.location.href = '/auth/login';
            } else {
                alert(data.message || '오류가 발생했습니다.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('네트워크 오류가 발생했습니다.');
        }
    });

    function goToStep1() {
        document.getElementById('step1').classList.add('active');
        document.getElementById('step2').classList.remove('active');
        document.getElementById('step3').classList.remove('active');
        currentStep = 1;
    }

    function goToStep2() {
        document.getElementById('step1').classList.remove('active');
        document.getElementById('step2').classList.add('active');
        document.getElementById('step3').classList.remove('active');
        currentStep = 2;
    }

    function goToStep3() {
        document.getElementById('step1').classList.remove('active');
        document.getElementById('step2').classList.remove('active');
        document.getElementById('step3').classList.add('active');
        currentStep = 3;
    }
</script>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>