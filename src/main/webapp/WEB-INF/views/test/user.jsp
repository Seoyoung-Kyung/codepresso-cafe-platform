<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>사용자 상세 정보</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            text-align: center;
            margin-bottom: 30px;
        }
        .user-info {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        .info-row {
            display: flex;
            margin-bottom: 15px;
            align-items: center;
        }
        .info-label {
            font-weight: bold;
            color: #555;
            width: 100px;
            margin-right: 15px;
        }
        .info-value {
            color: #333;
            font-size: 16px;
        }
        .btn {
            background-color: #007bff;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            font-size: 14px;
            margin-right: 10px;
        }
        .btn:hover {
            background-color: #0056b3;
        }
        .btn-secondary {
            background-color: #6c757d;
        }
        .btn-secondary:hover {
            background-color: #545b62;
        }
        .actions {
            text-align: center;
            margin-top: 30px;
        }
        .error {
            background-color: #f8d7da;
            color: #721c24;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
            text-align: center;
        }
        .user-card {
            border: 1px solid #dee2e6;
            border-radius: 8px;
            overflow: hidden;
        }
        .user-header {
            background-color: #007bff;
            color: white;
            padding: 15px;
            text-align: center;
        }
        .user-body {
            padding: 20px;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>사용자 상세 정보</h1>

    <c:choose>
        <c:when test="${not empty user}">
            <div class="user-card">
                <div class="user-header">
                    <h2>${user.name}</h2>
                </div>
                <div class="user-body">
                    <div class="info-row">
                        <span class="info-label">ID:</span>
                        <span class="info-value">${user.id}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">이름:</span>
                        <span class="info-value">${user.name}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">이메일:</span>
                        <span class="info-value">${user.email}</span>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="error">
                사용자 정보를 찾을 수 없습니다.
            </div>
        </c:otherwise>
    </c:choose>

    <div class="actions">
        <a href="/" class="btn btn-secondary">목록으로 돌아가기</a>
    </div>
</div>
</body>
</html>