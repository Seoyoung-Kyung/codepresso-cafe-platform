<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" session="false" %>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body class="auth-page">
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<style>
    .auth-page {
        background: linear-gradient(160deg, var(--pink-4), #fff 55%);
        background-repeat: no-repeat;
        background-size: cover;
        background-attachment: fixed;
    }

    .hero.auth-hero {
        background: transparent;
        padding: 72px 0 96px;
    }

    .hero-card.signup-card {
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

    .links { display: flex; justify-content: center; align-items: center; gap: 14px; margin-top: 14px; font-weight: 700; }
    .links a { color: var(--pink-1); text-decoration: none; }
    .links span { color: var(--text-2); font-weight: 500; }
</style>

<main class="hero auth-hero">
    <div class="container">
        <div class="hero-card signup-card">
            <div>
                <div class="badge">CodePress · 회원가입</div>
                <h1>지금 CodePress에 가입하세요</h1>
<%--                <p>아이디, 비밀번호, 이름, 전화번호, 이메일, 닉네임을 입력하세요.</p>--%>

                <style>
                    /* 회원가입 페이지는 마스코트 영역 없이 단일 컬럼으로 표시 */
                    .signup-card {
                        grid-template-columns: 1fr;
                        width: 100%;
                        max-width: 640px;   /* 카드 전체 너비 더 축소 */
                        margin: 0 auto;     /* 가운데 정렬 */
                        padding: 24px;      /* 패딩도 조금 더 줄임 */
                        text-align: center; /* 상단 문구 중앙 정렬 */
                    }
                    .form { display: grid; gap: 14px; max-width: 380px; margin: 0 auto; text-align: left; }
                    .field { display: grid; gap: 6px; }
                    .row { display: flex; gap: 8px; }
                    .row > button { white-space: nowrap; }
                    label { font-weight: 700; }
                    input[type=text], input[type=password], input[type=email] {
                        width: 100%; padding: 12px 12px; border-radius: 10px;
                        border: 1px solid rgba(255,122,162,0.6);
                        outline: none; transition: border .2s, box-shadow .2s;
                        background: rgba(255,255,255,0.9);
                    }
                    input:focus { border-color: var(--pink-1); box-shadow: 0 0 0 3px rgba(255,122,162,.22); }
                    .btn.secondary {
                        background: var(--white);
                        color: var(--pink-1);
                        border: 1px solid rgba(255,122,162,0.6);
                    }
                    .btn.secondary:hover {
                        border-color: var(--pink-1);
                        background: rgba(255,122,162,0.08);
                        color: var(--pink-1);
                    }
                    .hint { font-size: 12px; color: var(--text-2); }
                    /* 라벨 옆 힌트 간격+톤 조정 */
                    .field label .hint { margin-left: 10px; color: #6b7280; font-weight: 400; }
                    .field label .hint .em { font-weight: 600; color: #4b5563; }
                    .msg { font-size: 13px; }
                    .ok { color: #2f855a; }
                    .bad { color: #c53030; }

                    /* 이메일 인증 관련 스타일 */
                    .email-verification { display: none; }
                    .email-verification.active { display: block; }
                    .verification-info {
                        background: #f8f9fa;
                        padding: 12px;
                        border-radius: 8px;
                        margin: 12px 0;
                        font-size: 14px;
                        color: #666;
                    }
                </style>

                <div class="form">
                    <div class="field">
                        <label for="accountId">아이디</label>
                        <div class="row">
                            <input type="text" id="accountId" placeholder="아이디" />
                            <button class="btn secondary" id="checkIdBtn">중복체크</button>
                        </div>
                        <div class="msg" id="idMsg"></div>
                    </div>

                    <div class="field">
                        <label for="password">비밀번호 <span class="hint">6자 이상, 영문/숫자 포함</span></label>
                        <input type="password" id="password" placeholder="비밀번호" />
                        <div class="msg" id="pwMsg"></div>
                    </div>

                    <div class="field">
                        <label for="name">이름</label>
                        <input type="text" id="name" placeholder="이름" />
                        <div class="msg" id="nameMsg"></div>
                    </div>

                    <div class="field">
                        <label for="phone">전화번호 <span class="hint">숫자만 입력해도 자동으로 하이픈이 들어갑니다.</span></label>
                        <input type="text" id="phone" placeholder="010-1234-5678" inputmode="numeric" />
                        <div class="msg" id="phoneMsg"></div>
                    </div>

                    <div class="field">
                        <label for="email">이메일</label>
                        <div class="row">
                            <input type="email" id="email" placeholder="example@domain.com" />
                            <button class="btn secondary" id="verifyEmailBtn">인증번호발송</button>
                        </div>
                        <div class="msg" id="emailMsg"></div>

                        <!-- 이메일 인증 영역 -->
                        <div class="email-verification" id="emailVerification">
                            <div class="verification-info">
                                <strong>인증번호가 이메일로 발송되었습니다!</strong><br>
                                이메일을 확인하고 인증번호를 입력해주세요.
                            </div>
                            <div class="row">
                                <input type="text" id="emailVerificationCode" placeholder="인증번호 입력" />
                                <button class="btn secondary" id="confirmEmailBtn">인증확인</button>
                            </div>
                            <div class="msg" id="emailVerificationMsg"></div>
                        </div>
                    </div>

                    <div class="field">
                        <label for="nickname">닉네임</label>
                        <div class="row">
                            <input type="text" id="nickname" placeholder="닉네임" />
                            <button class="btn secondary" id="checkNicknameBtn">중복체크</button>
                        </div>
                        <div class="msg" id="nicknameMsg"></div>
                    </div>

                    

                    <div class="field">
                        <button class="btn btn-primary" id="signupBtn">회원가입</button>
                    </div>

                    <div class="links">
                        <a href="<c:url value="/auth/id-find"/>">아이디 찾기</a>
                        <a href="<c:url value="/auth/password-find"/>">비밀번호 찾기</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<script>
    // ===== 1) DOM 요소 참조 =====
    const idInput = document.getElementById('accountId');
    const pwInput = document.getElementById('password');
    const nameInput = document.getElementById('name');
    const phoneInput = document.getElementById('phone');
    const emailInput = document.getElementById('email');
    const nickInput = document.getElementById('nickname');
    const emailVerificationCodeInput = document.getElementById('emailVerificationCode');

    const idMsg = document.getElementById('idMsg');
    const pwMsg = document.getElementById('pwMsg');
    const nameMsg = document.getElementById('nameMsg');
    const phoneMsg = document.getElementById('phoneMsg');
    const emailMsg = document.getElementById('emailMsg');
    const nicknameMsg = document.getElementById('nicknameMsg');
    const emailVerificationMsg = document.getElementById('emailVerificationMsg');

    // 이메일 인증 상태
    let isEmailVerified = false;
    let emailVerificationCode = '';

    // ===== 2) 유효성 검사 규칙 =====
    // - accountId: 4~50자, 영문/숫자/밑줄
    // - password: 6~100자, 영문 + 숫자 포함
    // - nickname: 2~50자 (문자 조합은 자유, 필요 시 제약 추가 가능)
    // - email: 간단한 이메일 형식
    const patterns = {
        accountId: /^[A-Za-z0-9_]{4,50}$/,
        password: /^(?=.*[A-Za-z])(?=.*\d).{6,100}$/,
        email: /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    };

    function setMsg(el, text, ok){
        el.textContent = text;
        el.className = ok ? 'msg ok' : 'msg bad';
    }

    // ===== 3) 유효성 검사 함수 =====
    function validateAccountId(val){
        if(!val) return '아이디를 입력해주세요.';
        if(!patterns.accountId.test(val)) return '아이디는 4~50자, 영문/숫자/밑줄만 가능합니다.';
        return '';
    }
    function validatePassword(val){
        if(!val) return '비밀번호를 입력해주세요.';
        if(!patterns.password.test(val)) return '비밀번호는 6~100자이며 영문과 숫자를 포함해야 합니다.';
        return '';
    }
    function validateNickname(val){
        if(!val) return '닉네임을 입력해주세요.';
        const len = val.length;
        if(len < 2 || len > 50) return '닉네임은 2~50자여야 합니다.';
        return '';
    }
    function validateEmail(val){
        if(!val) return '이메일을 입력해주세요.';
        if(!patterns.email.test(val)) return '올바른 이메일 형식이 아닙니다.';
        return '';
    }

    function validateName(val){
        if(!val) return '이름을 입력해주세요.';
        if(val.length < 2) return '이름은 2자 이상이어야 합니다.';
        return '';
    }

    function validatePhone(val){
        const digits = (val||'').replace(/\D/g,'');
        if(digits.length < 10 || digits.length > 11) return '전화번호는 10~11자리 숫자여야 합니다.';
        return '';
    }

    function formatPhone(value){
        const d = (value||'').replace(/\D/g,'').slice(0,11);
        if(d.startsWith('02')){
            if(d.length <= 2) return d;
            if(d.length <= 5) return d.slice(0,2)+'-'+d.slice(2);
            if(d.length <= 9) return d.slice(0,2)+'-'+d.slice(2,5)+'-'+d.slice(5);
            return d.slice(0,2)+'-'+d.slice(2,6)+'-'+d.slice(6);
        }else{
            if(d.length <= 3) return d;
            if(d.length <= 7) return d.slice(0,3)+'-'+d.slice(3);
            if(d.length <= 10) return d.slice(0,3)+'-'+d.slice(3,6)+'-'+d.slice(6);
            return d.slice(0,3)+'-'+d.slice(3,7)+'-'+d.slice(7);
        }
    }

    // ===== 4) 한글 라벨 변환 =====
    function toKoreanField(f){
        switch(f){
            case 'id': return '아이디';
            case 'nickname': return '닉네임';
            case 'email': return '이메일';
            default: return '아이디';
        }
    }

    // ===== 5) 중복체크 요청 함수 =====
    // - endpoint: 'id' | 'nickname' | 'email'
    // - /api/auth/check?field={field}&value=... 형태로 호출
    // - 주의: JSP의 $ { } EL과 충돌 방지를 위해 템플릿 리터럴을 쓰지 않고 문자열 결합 사용
    async function checkDup(endpoint, value, msgEl, label){
        // 허용되지 않은 endpoint가 오면 기본값 id로 보정
        const field = (endpoint === 'id' || endpoint === 'nickname' || endpoint === 'email') ? endpoint : 'id';
        // 라벨은 항상 필드에서 도출하여 빈 문자열 문제 방지
        const labelText = toKoreanField(field);
        if(!value){ msgEl.textContent = labelText + '를 입력해주세요.'; msgEl.className='msg bad'; return; }
        const q = encodeURIComponent(value);
        try {
            // 1차 시도: 쿼리 파라미터 방식 (/check?field={field}&value=...)
            const url = '/api/auth/check?field=' + encodeURIComponent(field) + '&value=' + q;
            console.log('[dup-check]', field, url);
            const res = await fetch(url);
            if (!res.ok) throw new Error('중복체크 실패');
            const data = await res.json();
            const lbl = toKoreanField((data && data.field) ? data.field : field);
            if (data.duplicate) {
                msgEl.textContent = '이미 사용 중인 ' + lbl + '입니다.';
                msgEl.className = 'msg bad';
            } else {
                msgEl.textContent = '사용 가능한 ' + lbl + '입니다.';
                msgEl.className = 'msg ok';
            }
        } catch(e) {
            msgEl.textContent = '요청 중 오류가 발생했습니다.';
            msgEl.className = 'msg bad';
        }
    }

    // ===== 6) 이메일 인증 함수 =====
    async function sendEmailVerification() {
        const email = emailInput.value.trim();
        const emailValidation = validateEmail(email);

        if (emailValidation) {
            setMsg(emailMsg, emailValidation, false);
            return;
        }

        try {
            // 1. 이메일 중복체크
            const checkRes = await fetch('/api/auth/check?field=email&value=' + encodeURIComponent(email));
            if (!checkRes.ok) throw new Error('중복체크 실패');

            const checkData = await checkRes.json();
            if (checkData.duplicate) {
                setMsg(emailMsg, '이미 사용 중인 이메일입니다.', false);
                return;
            }

            // 2. 이메일 인증번호 발송
            const response = await fetch('/api/auth/send-email-verification', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email })
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || '이메일 발송 실패');
            }

            const data = await response.json();
            emailVerificationCode = data.verificationCode;

            setMsg(emailMsg, '인증번호가 이메일로 발송되었습니다.', true);
            document.getElementById('emailVerification').classList.add('active');

        } catch (error) {
            console.error('이메일 인증 발송 실패:', error);
            setMsg(emailMsg, error.message || '이메일 발송에 실패했습니다.', false);
        }
    }

    async function confirmEmailVerification() {
        const inputCode = emailVerificationCodeInput.value.trim();

        if (!inputCode) {
            setMsg(emailVerificationMsg, '인증번호를 입력해주세요.', false);
            return;
        }

        if (inputCode === emailVerificationCode) {
            setMsg(emailVerificationMsg, '이메일 인증이 완료되었습니다.', true);
            isEmailVerified = true;
            document.getElementById('verifyEmailBtn').textContent = '인증완료';
            document.getElementById('verifyEmailBtn').disabled = true;
        } else {
            setMsg(emailVerificationMsg, '인증번호가 일치하지 않습니다.', false);
        }
    }

    // 입력값 블러 시 즉시 유효성 메시지 표시
    // idInput.addEventListener('blur', () => {
    //     const m = validateAccountId(idInput.value.trim());
    //     if(m) setMsg(idMsg, m, false); else setMsg(idMsg, '형식이 유효합니다.', true);
    // });
    // pwInput.addEventListener('blur', () => {
    //     const m = validatePassword(pwInput.value.trim());
    //     if(m) setMsg(pwMsg, m, false); else setMsg(pwMsg, '형식이 유효합니다.', true);
    // });
    // nickInput.addEventListener('blur', () => {
    //     const m = validateNickname(nickInput.value.trim());
    //     if(m) setMsg(nicknameMsg, m, false); else setMsg(nicknameMsg, '형식이 유효합니다.', true);
    // });
    // emailInput.addEventListener('blur', () => {
    //     const m = validateEmail(emailInput.value.trim());
    //     if(m) setMsg(emailMsg, m, false); else setMsg(emailMsg, '형식이 유효합니다.', true);
    // });

    // ===== 7) 이벤트 바인딩 =====
    // 중복 체크 버튼: 유효할 때만 서버 요청
    document.getElementById('checkIdBtn').addEventListener('click', () => {
        const m = validateAccountId(idInput.value.trim());
        if(m) { setMsg(idMsg, m, false); return; }
        checkDup('id', idInput.value.trim(), idMsg, '아이디');
    });
    document.getElementById('checkNicknameBtn').addEventListener('click', () => {
        const m = validateNickname(nickInput.value.trim());
        if(m) { setMsg(nicknameMsg, m, false); return; }
        checkDup('nickname', nickInput.value.trim(), nicknameMsg, '닉네임');
    });

    // 이메일 인증 버튼
    document.getElementById('verifyEmailBtn').addEventListener('click', sendEmailVerification);
    document.getElementById('confirmEmailBtn').addEventListener('click', confirmEmailVerification);

    // 전화번호 자동 하이픈 포맷팅
    phoneInput.addEventListener('input', () => {
        const formatted = formatPhone(phoneInput.value);
        phoneInput.value = formatted;
    });

    document.getElementById('signupBtn').addEventListener('click', async () => {
        const accountId = idInput.value.trim();
        const password = pwInput.value.trim();
        const name = nameInput.value.trim();
        const phone = phoneInput.value.trim();
        const email = emailInput.value.trim();
        const nickname = nickInput.value.trim();

        // 종합 유효성 검사
        const v = [
            {m: validateAccountId(accountId), el: idMsg, focusEl: idInput},
            {m: validatePassword(password), el: pwMsg, focusEl: pwInput},
            {m: validateName(name), el: nameMsg, focusEl: nameInput},
            {m: validatePhone(phone), el: phoneMsg, focusEl: phoneInput},
            {m: validateEmail(email), el: emailMsg, focusEl: emailInput},
            {m: validateNickname(nickname), el: nicknameMsg, focusEl: nickInput}
        ];

        // 이메일 인증 확인
        if (!isEmailVerified) {
            setMsg(emailMsg, '이메일 인증을 완료해주세요.', false);
            emailInput.focus();
            return;
        }

        const firstInvalid = v.find(x => x.m);
        if(firstInvalid){
            setMsg(firstInvalid.el, firstInvalid.m, false);
            firstInvalid.focusEl.focus();
            alert('입력값을 확인해주세요.');
            return;
        }

        try{
            const res = await fetch('/api/auth/signup', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ accountId, password, nickname, email, name, phone })
            });
            if(!res.ok){
                const msg = await res.text();
                alert('회원가입 실패: ' + msg);
                return;
            }
            const data = await res.json();
            // 성공 알림은 모달로 표시되며, 사용자가 확인을 누르면 다음 줄이 실행됩니다.
            alert('회원가입을 축하합니다! ' + data.nickname + '님');
            // 확인을 누른 뒤 메인 페이지로 이동합니다.
            location.href = '/';
        }catch(e){
            alert('요청 중 오류가 발생했습니다.');
        }
    });
</script>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>