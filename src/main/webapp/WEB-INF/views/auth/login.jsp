<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
          로그인 화면 카드
          - 분홍 테마(기존 index/signup과 일관)
          - 단일 컬럼, 가운데 정렬
        -->
        <div class="hero-card login-card">
            <div>
                <div class="badge">CodePress · 로그인</div>
                <h1>반가워요! 다시 만나서 좋아요</h1>
<%--                <p>아이디와 비밀번호를 입력하고, 원하면 로그인 상태를 유지할 수 있어요.</p>--%>

                <style>
                    /*
                     로그인 전용 스타일
                     - signup과 유사하게 단일 컬럼 + 폭 제한
                    */
                    .login-card {
                        grid-template-columns: 1fr;
                        width: 100%;
                        max-width: 640px;
                        margin: 0 auto;
                        padding: 24px;
                        text-align: center; /* 상단 문구 중앙 */
                    }
                    .login-card h1 { margin: 0 0 24px; } /* 제목 아래 여백 확대: 아이디 입력과 간격 확보 */
                    .login-form { display: grid; gap: 14px; max-width: 380px; margin: 0 auto; text-align: left; }
                    .field { display: grid; gap: 6px; }
                    .row { display: flex; align-items: center; justify-content: space-between; }
                    label { font-weight: 700; }
                    input[type=text], input[type=password] {
                        width: 100%; padding: 12px; border-radius: 10px;
                        border: 1px solid rgba(255,122,162,0.35);
                        outline: none; transition: border .2s, box-shadow .2s;
                        background: rgba(255,255,255,0.9);
                    }
                    input:focus { border-color: var(--pink-1); box-shadow: 0 0 0 3px rgba(255,122,162,.22); }
                    input[type=checkbox] { accent-color: var(--pink-1); }
                    .actions { display: grid; gap: 10px; margin-top: 10px; } /* 로그인 버튼 위 여백 */
                    .sub-actions { display: flex; justify-content: space-between; align-items: center; margin-top: 6px; margin-bottom: 6px; }
                    .links { display: flex; justify-content: center; align-items: center; gap: 14px; margin-top: 14px; font-weight: 700; }
                    .links a { color: var(--pink-1); text-decoration: none; }
                    .links span { color: var(--text-2); font-weight: 500; }
                </style>

                <!--
                  스프링 시큐리티 폼 로그인 규칙
                  - action: /login (SecurityConfig.formLogin().loginProcessingUrl("/login"))
                  - name="accountId", name="password" (SecurityConfig에서 usernameParameter/passwordParameter로 매핑)
                  - name="remember-me" 체크 시 Remember-Me 쿠키 발급 (SecurityConfig.rememberMe().rememberMeParameter("remember-me"))
                -->
                <form class="login-form" method="post" action="/login">
                    <!-- CSRF: 학습 편의를 위해 현재 SecurityConfig에서 비활성화되어 있음. 운영 전환 시 활성화 권장. -->

                    <div class="field">
                        <label for="accountId">아이디</label>
                        <input type="text" id="accountId" name="accountId" placeholder="아이디 입력" required />
                    </div>

                    <div class="field">
                        <label for="password">비밀번호</label>
                        <input type="password" id="password" name="password" placeholder="비밀번호 입력" required />
                    </div>

                    <div class="sub-actions">
                        <label style="display:flex; align-items:center; gap:8px;" >
                            <input type="checkbox" name="remember-me" /> 로그인 상태 유지
                        </label>
                    </div>

                    <div class="actions">
                        <button type="submit" class="btn btn-primary">로그인</button>
                    </div>

                    <!-- 실패 시 쿼리파라미터 error=1로 돌아옴(SecurityConfig.failureUrl) -->
                    <div class="links">
                        <a href="<c:url value="/auth/id-find"/>">아이디 찾기</a>
                        <a href="<c:url value="/auth/password-find"/>">비밀번호 찾기</a>
                        <a href="<c:url value="/auth/signup"/>">회원가입</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</main>

<script>
    // 로그인 결과를 쿼리파라미터로 전달받아 알림으로 보여줍니다.
    // - 성공: /auth/login?success=1
    // - 실패: /auth/login?error=1
    (function(){
        const p = new URLSearchParams(location.search);
        if (p.get('logout') === '1') {
            alert('로그아웃 되었습니다. 다시 로그인해 주세요.');
        }
        if (p.get('success') === '1') {
            alert('로그인 성공! 환영합니다.');
        }
        if (p.get('error') === '1') {
            alert('아이디 또는 비밀번호를 확인해주세요.');
        }
    })();
</script>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
