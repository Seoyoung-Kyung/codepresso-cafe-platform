<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>오류 발생</title>
    <style>
        :root {
            --pink-1: #ff7aa2;
            --pink-2: #ff9bb7;
            --pink-4: #ffe5ec;
            --text-1: #1f2937;
            --text-2: #4b5563;
        }

        body {
            margin: 0;
            font-family: 'Noto Sans KR', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
            background: linear-gradient(160deg, var(--pink-4), #fff 55%);
            min-height: 100vh;
            display: grid;
            place-items: center;
            padding: 48px 20px;
            color: var(--text-1);
        }

        .error-shell {
            max-width: 520px;
            width: 100%;
            background: #fff;
            border-radius: 32px;
            box-shadow: 0 36px 68px rgba(255, 122, 162, 0.32);
            padding: 48px 44px;
            text-align: center;
            display: grid;
            gap: 24px;
        }

        .error-icon {
            font-size: 60px;
            color: var(--pink-1);
        }

        h1 {
            margin: 0;
            font-size: 30px;
            font-weight: 800;
            color: var(--text-1);
        }

        .error-message {
            padding: 18px 20px;
            border-radius: 18px;
            background: rgba(255, 122, 162, 0.12);
            color: var(--pink-1);
            font-weight: 600;
        }

        .error-description {
            color: var(--text-2);
            line-height: 1.7;
        }

        .actions {
            display: flex;
            gap: 12px;
            justify-content: center;
            flex-wrap: wrap;
        }

        .btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            padding: 12px 26px;
            border-radius: 999px;
            font-weight: 700;
            text-decoration: none;
            border: none;
            cursor: pointer;
            transition: transform .15s ease, box-shadow .2s ease;
        }

        .btn:active {
            transform: translateY(1px);
        }

        .btn-primary {
            background: linear-gradient(135deg, var(--pink-1), var(--pink-2));
            color: #fff;
            box-shadow: 0 14px 28px rgba(255,122,162,0.3);
        }

        .btn-primary:hover {
            filter: brightness(1.02);
        }

        .btn-secondary {
            background: rgba(15,23,42,0.06);
            color: var(--text-1);
        }

        .btn-secondary:hover {
            background: rgba(15,23,42,0.1);
        }

        @media (max-width: 540px) {
            .error-shell {
                padding: 36px 28px;
            }

            .btn {
                width: 100%;
            }
        }
    </style>
</head>
<body>
<div class="error-shell">
    <div class="error-icon">⚠️</div>
    <h1>오류가 발생했습니다</h1>

    <c:choose>
        <c:when test="${not empty error}">
            <div class="error-message">
                <strong>오류 내용:</strong><br>
                    ${error}
            </div>
        </c:when>
        <c:otherwise>
            <div class="error-message">
                알 수 없는 오류가 발생했습니다.
            </div>
        </c:otherwise>
    </c:choose>

    <div class="error-description">
        요청을 처리하는 중에 문제가 발생했습니다.<br>
        잠시 후 다시 시도해 주시거나 관리자에게 문의해 주세요.
    </div>

    <div class="actions">
        <a href="/" class="btn btn-primary">홈으로 돌아가기</a>
        <a href="javascript:history.back()" class="btn btn-secondary">이전 페이지로</a>
    </div>
</div>
</body>
</html>
