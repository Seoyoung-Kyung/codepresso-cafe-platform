<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
    <style>
        .board-page {
            background: linear-gradient(150deg, var(--pink-4), #fff 55%);
            padding: 72px 0 96px;
        }

        .board-container {
            max-width: 720px;
            margin: 0 auto;
            padding: 0 24px;
        }

        .board-card {
            background: #fff;
            border-radius: 28px;
            box-shadow: 0 32px 60px rgba(15,23,42,0.15);
            padding: 48px;
            display: grid;
            gap: 28px;
        }

        .page-header h1 {
            margin: 0;
            font-size: 30px;
            font-weight: 800;
            color: var(--text-1);
        }

        .board-form .form-group {
            display: grid;
            gap: 10px;
        }

        .board-form label {
            font-weight: 700;
            color: var(--text-1);
            font-size: 15px;
        }

        .board-form label.required::after {
            content: ' *';
            color: var(--pink-1);
        }

        .board-form input,
        .board-form select,
        .board-form textarea {
            width: 100%;
            border: 1px solid rgba(15,23,42,0.12);
            border-radius: 16px;
            padding: 14px 18px;
            font-size: 15px;
            font-family: inherit;
            background: rgba(255,255,255,0.9);
            transition: border-color .2s ease, box-shadow .2s ease;
        }

        .board-form textarea {
            min-height: 280px;
            resize: vertical;
            line-height: 1.6;
        }

        .board-form input:focus,
        .board-form select:focus,
        .board-form textarea:focus {
            outline: none;
            border-color: rgba(255,122,162,0.65);
            box-shadow: 0 0 0 4px rgba(255,122,162,0.18);
        }

        .char-count {
            text-align: right;
            font-size: 12px;
            color: var(--text-2);
        }

        .char-count.warning { color: #f59e0b; }
        .char-count.danger { color: #ef4444; }

        .form-messages {
            display: grid;
            gap: 12px;
        }

        .alert {
            padding: 14px 18px;
            border-radius: 16px;
            font-size: 14px;
            display: none;
        }

        .alert.error {
            background: rgba(239, 68, 68, 0.1);
            color: #b91c1c;
            border: 1px solid rgba(239, 68, 68, 0.25);
        }

        .alert.success {
            background: rgba(34, 197, 94, 0.12);
            color: #166534;
            border: 1px solid rgba(34, 197, 94, 0.25);
        }

        .form-actions {
            display: flex;
            gap: 12px;
            justify-content: flex-end;
        }

        .btn {
            display: inline-flex;
            justify-content: center;
            align-items: center;
            padding: 14px 22px;
            border-radius: 999px;
            font-weight: 700;
            font-size: 15px;
            cursor: pointer;
            border: none;
            transition: transform .15s ease, box-shadow .2s ease;
        }

        .btn:active { transform: translateY(1px); }

        .btn-secondary {
            background: rgba(15,23,42,0.06);
            color: var(--text-1);
        }

        .btn-secondary:hover {
            background: rgba(15,23,42,0.1);
        }

        .submit-btn {
            background: linear-gradient(135deg, var(--pink-1), var(--pink-2));
            color: #fff;
            box-shadow: 0 16px 32px rgba(255,122,162,0.35);
            min-width: 160px;
        }

        .submit-btn:disabled {
            background: rgba(15,23,42,0.2);
            box-shadow: none;
            cursor: not-allowed;
        }

        @media (max-width: 720px) {
            .board-card {
                padding: 32px 24px;
            }

            .form-actions {
                flex-direction: column-reverse;
            }

            .btn,
            .submit-btn {
                width: 100%;
            }
        }
    </style>
</head>
<body>
    <main class="board-page">
        <div class="board-container">
            <section class="board-card">
                <div class="page-header">
                    <h1>글쓰기</h1>
                </div>

                <div class="form-messages">
                    <div id="errorMessage" class="alert error"></div>
                    <div id="successMessage" class="alert success"></div>
                </div>

                <form id="writeForm" class="board-form">

                    <div class="form-group">
                        <label for="title" class="required">제목</label>
                        <input type="text" id="title" name="title" placeholder="제목을 입력하세요" maxlength="200" required>
                        <div class="char-count" id="titleCount">0 / 200</div>
                    </div>

                    <div class="form-group">
                        <label for="content" class="required">내용</label>
                        <textarea id="content" name="content" placeholder="문의 내용을 상세히 작성해 주세요" required></textarea>
                        <div class="char-count" id="contentCount">0</div>
                    </div>

                    <div class="form-actions">
                        <button type="button" class="btn btn-secondary" onclick="goBack()">취소</button>
                        <button type="submit" class="btn submit-btn" id="submitBtn">제출하기</button>
                    </div>
                </form>
            </section>
        </div>
    </main>

    <script>
        // 폼 요소들
        const form = document.getElementById('writeForm');
        const titleInput = document.getElementById('title');
        const contentInput = document.getElementById('content');
        const titleCount = document.getElementById('titleCount');
        const contentCount = document.getElementById('contentCount');
        const submitBtn = document.getElementById('submitBtn');
        
        // 사용자 정보
        let currentUserRole = null;
        let currentBoardTypeId = null;

        // 수정 모드 확인
        const isEditMode = window.location.pathname.includes('/edit/');
        const boardId = isEditMode ? window.location.pathname.split('/edit/')[1] : null;

        // 페이지 로드 시 이벤트 리스너 설정
        document.addEventListener('DOMContentLoaded', function() {
            setupEventListeners();
            
            // URL에서 게시판 타입 가져오기
            getBoardTypeFromUrl();
            
            // 사용자 역할 확인
            checkUserRole();
            
            // 수정 모드인 경우 기존 데이터 로드
            if (isEditMode && boardId) {
                loadBoardData(boardId);
            }
        });

        // URL에서 게시판 타입 가져오기
        function getBoardTypeFromUrl() {
            const urlParams = new URLSearchParams(window.location.search);
            const boardType = urlParams.get('boardType');
            
            if (boardType) {
                currentBoardTypeId = parseInt(boardType);
                console.log('Board type from URL:', currentBoardTypeId);
            } else {
                // 기본값: 1:1문의
                currentBoardTypeId = 2;
                console.log('No board type in URL, defaulting to 1:1문의');
            }
        }

        // 사용자 역할 확인
        function checkUserRole() {
            fetch('/api/users/me', {
                credentials: 'same-origin'
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('User not logged in');
                }
                return response.json();
            })
            .then(data => {
                currentUserRole = data.role;
                console.log('Current user role:', currentUserRole);
            })
            .catch(error => {
                console.error('Error checking user role:', error);
                // 기본적으로 USER로 처리
                currentUserRole = 'USER';
            });
        }

        // 이벤트 리스너 설정
        function setupEventListeners() {
            // 제목 입력 이벤트
            titleInput.addEventListener('input', function() {
                updateCharCount(titleInput, titleCount, 200);
            });

            // 내용 입력 이벤트
            contentInput.addEventListener('input', function() {
                updateCharCount(contentInput, contentCount);
            });

            // 폼 제출 이벤트
            form.addEventListener('submit', function(e) {
                e.preventDefault();
                submitForm();
            });
        }

        // 글자 수 업데이트
        function updateCharCount(input, countElement, maxLength = null) {
            const length = input.value.length;
            countElement.textContent = maxLength ? `${length} / ${maxLength}` : length;
            
            // 경고 색상 설정
            if (maxLength) {
                if (length > maxLength * 0.9) {
                    countElement.className = 'char-count danger';
                } else if (length > maxLength * 0.8) {
                    countElement.className = 'char-count warning';
                } else {
                    countElement.className = 'char-count';
                }
            }
        }

        // 기존 게시글 데이터 로드 (수정 모드)
        function loadBoardData(boardId) {
            fetch(`/boards/${boardId}`, {
                credentials: 'same-origin'
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('게시글을 불러올 수 없습니다.');
                }
                return response.json();
            })
            .then(data => {
                // 폼에 기존 데이터 채우기
                titleInput.value = data.title || '';
                contentInput.value = data.content || '';
                currentBoardTypeId = data.boardTypeId || currentBoardTypeId;
                
                // 글자 수 업데이트
                updateCharCount(titleInput, titleCount, 200);
                updateCharCount(contentInput, contentCount);
                
                // 제목 변경
                document.querySelector('.page-header h1').textContent = '게시글 수정';
                submitBtn.textContent = '수정하기';
            })
            .catch(error => {
                console.error('Error loading board data:', error);
                showError('게시글을 불러오는 중 오류가 발생했습니다.');
            });
        }

        // 폼 제출
        function submitForm() {
            // 유효성 검사
            if (!validateForm()) {
                return;
            }

            // 제출 버튼 비활성화
            submitBtn.disabled = true;
            submitBtn.textContent = isEditMode ? '수정 중...' : '제출 중...';

            // 폼 데이터 수집
            const formData = {
                title: titleInput.value.trim(),
                content: contentInput.value.trim(),
                statusTag: 'PENDING', // 항상 답변대기로 설정
                boardTypeId: currentBoardTypeId
            };

            // API 호출 (수정 모드인지 확인)
            const url = isEditMode ? `/boards/${boardId}` : '/boards';
            const method = isEditMode ? 'PUT' : 'POST';

            fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData),
                credentials: 'same-origin'
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    const message = isEditMode ? '게시글이 성공적으로 수정되었습니다.' : '게시글이 성공적으로 작성되었습니다.';
                    showSuccess(message);
                    setTimeout(() => {
                        window.location.href = '/boards/list';
                    }, 1500);
                } else {
                    const errorMessage = isEditMode ? '게시글 수정에 실패했습니다.' : '게시글 작성에 실패했습니다.';
                    showError(data.message || errorMessage);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                const errorMessage = isEditMode ? '게시글 수정 중 오류가 발생했습니다.' : '게시글 작성 중 오류가 발생했습니다.';
                showError(errorMessage);
            })
            .finally(() => {
                // 제출 버튼 활성화
                submitBtn.disabled = false;
                submitBtn.textContent = isEditMode ? '수정하기' : '제출하기';
            });
        }

        // 폼 유효성 검사
        function validateForm() {
            const title = titleInput.value.trim();
            const content = contentInput.value.trim();

            if (!title) {
                showError('제목을 입력해주세요.');
                titleInput.focus();
                return false;
            }

            if (title.length > 200) {
                showError('제목은 200자를 초과할 수 없습니다.');
                titleInput.focus();
                return false;
            }

            if (!content) {
                showError('내용을 입력해주세요.');
                contentInput.focus();
                return false;
            }

            return true;
        }

        // 에러 메시지 표시
        function showError(message) {
            const errorDiv = document.getElementById('errorMessage');
            errorDiv.textContent = message;
            errorDiv.style.display = 'block';
            
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
        }

        // 뒤로가기
        function goBack() {
            if (confirm('작성 중인 내용이 사라집니다. 정말로 나가시겠습니까?')) {
                window.history.back();
            }
        }
    </script>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
