<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
    <style>
        .board-page {
            background: linear-gradient(160deg, var(--pink-4), #fff 55%);
            padding: 72px 0 96px;
        }

        .board-container {
            max-width: 880px;
            margin: 0 auto;
            padding: 0 24px;
        }

        .board-card {
            background: #fff;
            border-radius: 28px;
            box-shadow: 0 32px 60px rgba(15,23,42,0.15);
            padding: 48px 56px;
            display: grid;
            gap: 28px;
        }

        .post-header {
            border-bottom: 1px solid rgba(15,23,42,0.08);
            padding-bottom: 20px;
            display: grid;
            gap: 12px;
        }

        .post-title {
            margin: 0;
            font-size: 30px;
            font-weight: 800;
            color: var(--text-1);
            line-height: 1.4;
        }

        .post-meta {
            display: flex;
            flex-wrap: wrap;
            gap: 16px;
            font-size: 14px;
            color: var(--text-2);
        }

        .post-content {
            line-height: 1.8;
            color: var(--text-1);
            font-size: 16px;
            white-space: pre-wrap;
            min-height: 220px;
        }

        .answer-section {
            background: rgba(255,122,162,0.08);
            border-radius: 22px;
            padding: 28px;
            display: grid;
            gap: 16px;
        }

        .answer-status {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 6px 14px;
            border-radius: 999px;
            font-size: 12px;
            font-weight: 700;
        }

        .answer-status.pending {
            background: rgba(255,122,162,0.18);
            color: var(--pink-1);
        }

        .answer-status.completed {
            background: rgba(34,197,94,0.15);
            color: #15803d;
        }

        .answer-content {
            background: #fff;
            border-radius: 18px;
            padding: 20px 24px;
            border: 1px solid rgba(15,23,42,0.08);
            line-height: 1.6;
            white-space: pre-wrap;
            position: relative;
        }

        .answer-actions {
            position: absolute;
            top: 12px;
            right: 12px;
            display: flex;
            gap: 8px;
        }

        .answer-actions .btn {
            padding: 6px 12px;
            font-size: 12px;
            border-radius: 6px;
        }

        .answer-label {
            font-weight: 700;
            color: var(--text-1);
        }

        .admin-answer-form {
            margin-top: 20px;
            padding: 20px;
            background: rgba(255,255,255,0.8);
            border-radius: 16px;
            border: 1px solid rgba(15,23,42,0.08);
        }

        .answer-form-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 16px;
        }

        .answer-form-content {
            margin-top: 16px;
        }

        .form-group {
            margin-bottom: 16px;
        }

        .form-group label {
            display: block;
            margin-bottom: 6px;
            font-weight: 600;
            color: var(--text-1);
        }

        .form-group input,
        .form-group textarea {
            width: 100%;
            padding: 12px 16px;
            border: 1px solid rgba(15,23,42,0.16);
            border-radius: 8px;
            font-size: 14px;
            font-family: inherit;
            transition: border-color 0.2s ease;
        }

        .form-group input:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: var(--pink-1);
            box-shadow: 0 0 0 3px rgba(255,122,162,0.1);
        }

        .form-actions {
            display: flex;
            gap: 12px;
            justify-content: flex-end;
            margin-top: 20px;
        }

        .answer-title-display {
            padding: 12px 16px;
            background: rgba(15,23,42,0.04);
            border-radius: 8px;
            font-size: 14px;
            color: var(--text-1);
            border: 1px solid rgba(15,23,42,0.08);
        }

        .action-buttons {
            display: flex;
            gap: 12px;
            justify-content: flex-end;
        }

        .btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            padding: 12px 22px;
            border-radius: 999px;
            font-weight: 700;
            font-size: 14px;
            cursor: pointer;
            border: none;
            text-decoration: none;
            transition: transform .15s ease, box-shadow .2s ease;
        }

        .btn:active { transform: translateY(1px); }

        .btn-outline {
            background: transparent;
            color: var(--text-1);
            border: 1px solid rgba(15,23,42,0.16);
        }

        .btn-outline:hover { background: rgba(15,23,42,0.06); }

        .btn-primary {
            background: linear-gradient(135deg, var(--pink-1), var(--pink-2));
            color: #fff;
            box-shadow: 0 14px 28px rgba(255,122,162,0.3);
        }

        .btn-secondary {
            background: rgba(239, 68, 68, 0.12);
            color: #b91c1c;
        }

        .btn-secondary:hover {
            background: rgba(239, 68, 68, 0.18);
        }

        .loading,
        .error {
            text-align: center;
            padding: 60px 20px;
            color: var(--text-2);
        }

        .error {
            color: #b91c1c;
        }

        @media (max-width: 820px) {
            .board-card {
                padding: 32px 24px;
            }

            .action-buttons {
                flex-direction: column;
            }

            .btn {
                width: 100%;
            }
        }
    </style>
</head>
<body>
    <main class="board-page">
        <div class="board-container">
            <section class="board-card" id="postDetail">
                <!-- 로딩 상태 -->
                <div id="loading" class="loading">
                    <h3>게시글을 불러오는 중...</h3>
                </div>

                <!-- 에러 상태 -->
                <div id="error" class="error" style="display: none;">
                    <h3>게시글을 불러올 수 없습니다</h3>
                    <p>존재하지 않는 게시글이거나 권한이 없습니다.</p>
                </div>

                <!-- 게시글 내용 -->
                <div id="postContent" style="display: none;">
                    <div class="post-header">
                        <h1 class="post-title" id="postTitle"></h1>
                        <div class="post-meta">
                            <span>작성자: <span id="postAuthor"></span></span>
                            <span>작성일자: <span id="postDate"></span></span>
                        </div>
                    </div>

                    <div class="post-content" id="postContentText"></div>

                    <!-- 답변 섹션 -->
                    <div id="answerSection" class="answer-section" style="display: none;">
                        <span id="answerStatus" class="answer-status"></span>
                        <span class="answer-label">답변내용</span>
                        <div id="answerContent" class="answer-content"></div>
                        
                        <!-- ADMIN용 답변 작성 폼 -->
                        <div id="adminAnswerForm" class="admin-answer-form" style="display: none;">
                            <div class="answer-form-header">
                                <span class="answer-label">답변 작성</span>
                                <button id="toggleAnswerForm" class="btn btn-outline" onclick="toggleAnswerForm()">답변 작성</button>
                            </div>
                            <div id="answerFormContent" class="answer-form-content" style="display: none;">
                                <form id="answerForm">
                                    <div class="form-group">
                                        <label for="answerContentText">답변 내용</label>
                                        <textarea id="answerContentText" name="content" placeholder="답변 내용을 입력하세요" rows="6" required></textarea>
                                    </div>
                                    <div class="form-actions">
                                        <button type="button" class="btn btn-outline" onclick="cancelAnswerForm()">취소</button>
                                        <button type="submit" class="btn btn-primary">답변 등록</button>
                                    </div>
                                </form>
                            </div>
                        </div>

                        <!-- ADMIN용 답변 수정 폼 -->
                        <div id="adminAnswerEditForm" class="admin-answer-form" style="display: none;">
                            <div class="answer-form-header">
                                <span class="answer-label">답변 수정</span>
                                <button id="toggleAnswerEditForm" class="btn btn-outline" onclick="toggleAnswerEditForm()">답변 수정</button>
                            </div>
                            <div id="answerEditFormContent" class="answer-form-content" style="display: none;">
                                <form id="answerEditForm">
                                    <div class="form-group">
                                        <label>제목</label>
                                        <div id="answerEditTitleDisplay" class="answer-title-display"></div>
                                    </div>
                                    <div class="form-group">
                                        <label for="answerEditContentText">답변 내용</label>
                                        <textarea id="answerEditContentText" name="content" placeholder="답변 내용을 입력하세요" rows="6" required></textarea>
                                    </div>
                                    <div class="form-actions">
                                        <button type="button" class="btn btn-outline" onclick="cancelAnswerEditForm()">취소</button>
                                        <button type="submit" class="btn btn-primary">답변 수정</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>

                    <!-- 액션 버튼들 -->
                    <div class="action-buttons">
                        <button class="btn btn-outline" onclick="goBack()">목록으로</button>
                        <button id="editBtn" class="btn btn-primary" onclick="editPost()" style="display: none;">수정</button>
                        <button id="deleteBtn" class="btn btn-secondary" onclick="deletePost()" style="display: none;">삭제</button>
                    </div>
                </div>
            </section>
        </div>
    </main>

    <script>
        // 전역 변수 선언
        var currentBoardId = null;
        var currentUserId = null;
        var currentUserRole = null;
        var currentBoardTypeId = null;

        // 페이지 로드 시 게시글 상세 정보 로드
        document.addEventListener('DOMContentLoaded', function() {
            console.log('=== board-detail DOMContentLoaded START ===');
            
            var pathParts = window.location.pathname.split('/');
            currentBoardId = pathParts[pathParts.length - 1];
            
            console.log('currentBoardId:', currentBoardId);
            console.log('pathParts:', pathParts);
            
            // 현재 사용자 ID 가져오기 (세션에서)
            loadCurrentUser();
            loadPostDetail();
        });

        // 현재 사용자 정보 로드
        function loadCurrentUser() {
            console.log('Loading current user...');
            fetch('/api/users/me', {
                credentials: 'same-origin'
            })
                .then(response => {
                    console.log('User API response status:', response.status);
                    if (!response.ok) {
                        throw new Error('Network response was not ok: ' + response.status);
                    }
                    return response.json();
                })
                .then(data => {
                    console.log('User data received:', data);
                    currentUserId = data.memberId; // data.id가 아니라 data.memberId 사용
                    currentUserRole = data.role; // 사용자 역할 저장
                    console.log('currentUserId set to:', currentUserId, 'type:', typeof currentUserId);
                    console.log('currentUserRole set to:', currentUserRole, 'type:', typeof currentUserRole);
                })
                .catch(error => {
                    console.error('Error loading user:', error);
                });
        }

        // 게시글 상세 정보 로드
        function loadPostDetail() {
            console.log('=== loadPostDetail START ===');
            console.log('Loading post detail for boardId:', currentBoardId);
            console.log('boardId type:', typeof currentBoardId);
            
            if (!currentBoardId) {
                console.error('currentBoardId is null or undefined');
                showError();
                return;
            }
            
            var url = '/boards/' + currentBoardId;
            console.log('API URL:', url);
            console.log('About to call fetch...');
            
            fetch(url, {
                credentials: 'same-origin'
            })
                .then(response => {
                    console.log('Post API response status:', response.status);
                    console.log('Response headers:', response.headers);
                    if (!response.ok) {
                        console.error('Response not ok:', response.status, response.statusText);
                        throw new Error('Post not found: ' + response.status);
                    }
                    return response.json();
                })
                .then(data => {
                    console.log('Post data received:', data);
                    console.log('Data type:', typeof data);
                    displayPostDetail(data);
                })
                .catch(error => {
                    console.error('Error loading post:', error);
                    console.error('Error stack:', error.stack);
                    showError();
                });
        }

        // 게시글 상세 정보 표시
        function displayPostDetail(data) {
            console.log('=== displayPostDetail START ===');
            console.log('Post data:', data);
            
            document.getElementById('loading').style.display = 'none';
            document.getElementById('postContent').style.display = 'block';

            // 기본 정보
            console.log('Setting post title:', data.title);
            console.log('Setting post author:', data.memberNickname);
            console.log('Setting post date:', data.field);
            console.log('Setting post content:', data.content);
            console.log('Setting board type ID:', data.boardTypeId);
            
            document.getElementById('postTitle').textContent = data.title;
            document.getElementById('postAuthor').textContent = data.memberNickname;
            document.getElementById('postDate').textContent = formatDate(data.field);
            document.getElementById('postContentText').textContent = data.content;
            
            // boardTypeId 저장
            currentBoardTypeId = data.boardTypeId;

            // 답변 상태 확인 (statusTag 기반)
            var answerSection = document.getElementById('answerSection');
            var answerStatus = document.getElementById('answerStatus');
            var answerContent = document.getElementById('answerContent');
            var adminAnswerForm = document.getElementById('adminAnswerForm');

            console.log('Status tag:', data.statusTag);
            console.log('Current user role:', currentUserRole);
            console.log('Board type ID:', currentBoardTypeId);
            
            // 공지사항(board_type_id=1)인 경우 답변 섹션 완전히 숨김
            if (currentBoardTypeId === 1) {
                answerSection.style.display = 'none';
            } else {
                // 답변 섹션 표시 (1:1문의, FAQ)
                answerSection.style.display = 'block';
            }
            
            // 공지사항이 아닌 경우에만 답변 관련 로직 실행
            if (currentBoardTypeId !== 1) {
                if (data.statusTag === 'ANSWERED') {
                    answerStatus.textContent = '답변상태: 답변완료';
                    answerStatus.className = 'answer-status completed';
                    
                    // 실제 댓글(답변) 내용 표시
                    if (data.children && data.children.length > 0) {
                        // 가장 최근 댓글을 답변으로 표시
                        var latestComment = data.children[data.children.length - 1];
                        answerContent.innerHTML = latestComment.content + 
                            '<div class="answer-actions" id="answerActions" style="display: none;">' +
                            '<button class="btn btn-outline" onclick="editAnswer(' + latestComment.id + ')">수정</button>' +
                            '<button class="btn btn-secondary" onclick="deleteAnswer(' + latestComment.id + ')">삭제</button>' +
                            '</div>';
                        
                        // ADMIN인 경우에만 답변 수정/삭제 버튼 표시
                        if (currentUserRole === 'ADMIN') {
                            document.getElementById('answerActions').style.display = 'flex';
                        }
                    } else {
                        answerContent.textContent = '답변이 등록되었습니다.';
                    }
                    
                    // ADMIN인 경우 답변 작성 폼 숨기기 (이미 답변 완료)
                    if (currentUserRole === 'ADMIN') {
                        adminAnswerForm.style.display = 'none';
                    }
                } else {
                    answerStatus.textContent = '답변상태: 답변대기';
                    answerStatus.className = 'answer-status pending';
                    answerContent.textContent = '아직 답변이 등록되지 않았습니다. 빠른 시일 내에 답변드리겠습니다.';
                    
                    // ADMIN인 경우 답변 작성 폼 표시
                    if (currentUserRole === 'ADMIN') {
                        adminAnswerForm.style.display = 'block';
                    } else {
                        adminAnswerForm.style.display = 'none';
                    }
                }
            }

            // 작성자만 수정/삭제 버튼 표시
            console.log('=== 버튼 표시 로직 ===');
            console.log('currentUserId:', currentUserId, 'type:', typeof currentUserId);
            console.log('data.memberId:', data.memberId, 'type:', typeof data.memberId);
            console.log('비교 결과:', currentUserId == data.memberId);
            
            if (currentUserId && currentUserId == data.memberId) {
                var editBtn = document.getElementById('editBtn');
                
                // 답변완료 상태가 아닐 때만 수정 버튼 표시
                if (data.statusTag !== 'ANSWERED') {
                    editBtn.style.display = 'inline-block';
                    editBtn.textContent = '수정';
                    editBtn.style.background = '';
                    editBtn.style.color = '';
                    editBtn.style.cursor = 'pointer';
                    editBtn.onclick = editPost; // 정상적인 수정 함수 연결
                } else {
                    // 답변완료 상태일 때는 수정 불가 안내
                    editBtn.style.display = 'inline-block';
                    editBtn.textContent = '수정불가 (답변완료)';
                    editBtn.style.background = 'rgba(156, 163, 175, 0.2)';
                    editBtn.style.color = '#6b7280';
                    editBtn.style.cursor = 'not-allowed';
                    editBtn.onclick = function() {
                        alert('답변이 완료된 게시글은 수정할 수 없습니다.');
                        return false;
                    };
                }
                
                // 삭제는 항상 가능 (작성자 본인만)
                document.getElementById('deleteBtn').style.display = 'inline-block';
            }
        }

        // 에러 표시
        function showError() {
            document.getElementById('loading').style.display = 'none';
            document.getElementById('error').style.display = 'block';
        }

        // 목록으로 돌아가기
        function goBack() {
            window.history.back();
        }

        // 게시글 수정
        function editPost() {
            var editUrl = '/boards/edit/' + currentBoardId;
            console.log('Redirecting to edit URL:', editUrl);
            window.location.href = editUrl;
        }

        // 게시글 삭제
        function deletePost() {
            if (confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
                var deleteUrl = '/boards/' + currentBoardId;
                console.log('Deleting post with URL:', deleteUrl);
                
                fetch(deleteUrl, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    credentials: 'same-origin'
                })
                .then(response => {
                    console.log('Delete response status:', response.status);
                    return response.json();
                })
                .then(data => {
                    console.log('Delete response data:', data);
                    if (data.success) {
                        alert('게시글이 삭제되었습니다.');
                        window.location.href = '/boards/list';
                    } else {
                        alert('삭제 중 오류가 발생했습니다: ' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Delete error:', error);
                    alert('삭제 중 오류가 발생했습니다.');
                });
            }
        }

        // 답변 작성 폼 토글
        function toggleAnswerForm() {
            var formContent = document.getElementById('answerFormContent');
            var toggleBtn = document.getElementById('toggleAnswerForm');
            
            if (formContent.style.display === 'none') {
                formContent.style.display = 'block';
                toggleBtn.textContent = '답변 작성 취소';
            } else {
                formContent.style.display = 'none';
                toggleBtn.textContent = '답변 작성';
                // 폼 초기화
                document.getElementById('answerForm').reset();
            }
        }

        // 답변 작성 폼 취소
        function cancelAnswerForm() {
            var formContent = document.getElementById('answerFormContent');
            var toggleBtn = document.getElementById('toggleAnswerForm');
            
            formContent.style.display = 'none';
            toggleBtn.textContent = '답변 작성';
            document.getElementById('answerForm').reset();
        }

        // 답변 작성 폼 제출
        function submitAnswer(event) {
            event.preventDefault();
            
            var content = document.getElementById('answerContentText').value.trim();
            
            if (!content) {
                alert('답변 내용을 입력해주세요.');
                return;
            }
            
            // 답변 제목 자동 생성 (현재 날짜/시간 기반)
            var now = new Date();
            var title = '답변 - ' + now.toLocaleDateString('ko-KR') + ' ' + now.toLocaleTimeString('ko-KR', {hour: '2-digit', minute: '2-digit'});
            
            var answerData = {
                title: title,
                content: content,
                statusTag: 'ANSWERED',
                boardTypeId: currentBoardTypeId, // 부모 게시글의 boardTypeId 사용
                parentId: currentBoardId // 현재 게시글을 부모로 설정
            };
            
            console.log('Submitting answer:', answerData);
            
            var url = '/boards/' + currentBoardId + '/comments';
            console.log('Answer API URL:', url);
            
            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'same-origin',
                body: JSON.stringify(answerData)
            })
            .then(response => {
                console.log('Answer response status:', response.status);
                return response.json();
            })
            .then(data => {
                console.log('Answer response data:', data);
                if (data.success) {
                    alert('답변이 등록되었습니다.');
                    // 페이지 새로고침하여 최신 상태 반영
                    location.reload();
                } else {
                    alert('답변 등록 중 오류가 발생했습니다: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Answer submission error:', error);
                alert('답변 등록 중 오류가 발생했습니다.');
            });
        }

        // 답변 수정 폼 토글
        function toggleAnswerEditForm() {
            var formContent = document.getElementById('answerEditFormContent');
            var toggleBtn = document.getElementById('toggleAnswerEditForm');
            
            if (formContent.style.display === 'none') {
                formContent.style.display = 'block';
                toggleBtn.textContent = '답변 수정 취소';
            } else {
                formContent.style.display = 'none';
                toggleBtn.textContent = '답변 수정';
                // 폼 초기화
                document.getElementById('answerEditForm').reset();
            }
        }

        // 답변 수정 폼 취소
        function cancelAnswerEditForm() {
            var formContent = document.getElementById('answerEditFormContent');
            var toggleBtn = document.getElementById('toggleAnswerEditForm');
            
            formContent.style.display = 'none';
            toggleBtn.textContent = '답변 수정';
            document.getElementById('answerEditForm').reset();
        }

        // 답변 수정
        function editAnswer(commentId) {
            console.log('Editing answer with ID:', commentId);
            
            // 답변 ID를 전역 변수로 저장
            window.currentAnswerId = commentId;
            
            // 답변 수정 폼 표시
            var editForm = document.getElementById('adminAnswerEditForm');
            editForm.style.display = 'block';
            
            // 기존 답변 내용을 폼에 채우기
            loadAnswerForEdit(commentId);
            
            // 답변 수정 폼 토글
            toggleAnswerEditForm();
        }

        // 답변 수정을 위한 기존 답변 내용 로드
        function loadAnswerForEdit(commentId) {
            // 현재 게시글 데이터에서 해당 댓글 찾기
            var url = '/boards/' + currentBoardId;
            fetch(url, {
                credentials: 'same-origin'
            })
            .then(response => response.json())
            .then(data => {
                if (data.children && data.children.length > 0) {
                    var targetComment = data.children.find(comment => comment.id == commentId);
                    if (targetComment) {
                        // 제목은 표시만 하고 수정 불가
                        document.getElementById('answerEditTitleDisplay').textContent = targetComment.title || '';
                        document.getElementById('answerEditContentText').value = targetComment.content || '';
                    }
                }
            })
            .catch(error => {
                console.error('Error loading answer for edit:', error);
            });
        }

        // 답변 삭제
        function deleteAnswer(commentId) {
            if (confirm('정말로 이 답변을 삭제하시겠습니까?')) {
                console.log('Deleting answer with ID:', commentId);
                
                var url = '/boards/' + currentBoardId + '/comments/' + commentId;
                console.log('Delete answer API URL:', url);
                
                fetch(url, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    credentials: 'same-origin'
                })
                .then(response => {
                    console.log('Delete answer response status:', response.status);
                    return response.json();
                })
                .then(data => {
                    console.log('Delete answer response data:', data);
                    if (data.success) {
                        alert('답변이 삭제되었습니다.');
                        // 페이지 새로고침하여 최신 상태 반영
                        location.reload();
                    } else {
                        alert('답변 삭제 중 오류가 발생했습니다: ' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Delete answer error:', error);
                    alert('답변 삭제 중 오류가 발생했습니다.');
                });
            }
        }

        // 답변 수정 폼 제출
        function submitAnswerEdit(event) {
            event.preventDefault();
            
            var content = document.getElementById('answerEditContentText').value.trim();
            
            if (!content) {
                alert('답변 내용을 입력해주세요.');
                return;
            }
            
            // 답변 ID를 전역 변수로 저장해야 함 (editAnswer에서 설정)
            if (!window.currentAnswerId) {
                alert('답변 ID를 찾을 수 없습니다.');
                return;
            }
            
            // 기존 제목 유지 (수정하지 않음)
            var title = document.getElementById('answerEditTitleDisplay').textContent;
            
            var answerData = {
                title: title,
                content: content,
                statusTag: 'ANSWERED',
                boardTypeId: currentBoardTypeId, // 부모 게시글의 boardTypeId 사용
                parentId: currentBoardId // 현재 게시글을 부모로 설정
            };
            
            console.log('Submitting answer edit:', answerData);
            console.log('Answer ID:', window.currentAnswerId);
            
            var url = '/boards/' + currentBoardId + '/comments/' + window.currentAnswerId;
            console.log('Answer edit API URL:', url);
            
            fetch(url, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'same-origin',
                body: JSON.stringify(answerData)
            })
            .then(response => {
                console.log('Answer edit response status:', response.status);
                return response.json();
            })
            .then(data => {
                console.log('Answer edit response data:', data);
                if (data.success) {
                    alert('답변이 수정되었습니다.');
                    // 페이지 새로고침하여 최신 상태 반영
                    location.reload();
                } else {
                    alert('답변 수정 중 오류가 발생했습니다: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Answer edit submission error:', error);
                alert('답변 수정 중 오류가 발생했습니다.');
            });
        }

        // 답변 작성 폼 이벤트 리스너 등록
        document.addEventListener('DOMContentLoaded', function() {
            var answerForm = document.getElementById('answerForm');
            if (answerForm) {
                answerForm.addEventListener('submit', submitAnswer);
            }
            
            var answerEditForm = document.getElementById('answerEditForm');
            if (answerEditForm) {
                answerEditForm.addEventListener('submit', submitAnswerEdit);
            }
        });

        // 날짜 포맷팅
        function formatDate(dateString) {
            console.log('Formatting date:', dateString);
            var date = new Date(dateString);
            var formattedDate = date.toLocaleDateString('ko-KR', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit'
            }).replace(/\./g, '.').replace(/\s/g, '');
            console.log('Formatted date:', formattedDate);
            return formattedDate;
        }
    </script>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
