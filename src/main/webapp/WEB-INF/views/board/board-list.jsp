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
            max-width: 1120px;
            margin: 0 auto;
            padding: 0 24px;
        }

        .board-card {
            background: #fff;
            border-radius: 28px;
            box-shadow: 0 32px 60px rgba(15, 23, 42, 0.15);
            padding: 48px 56px;
            display: grid;
            gap: 32px;
        }

        .board-heading {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 16px;
        }

        .board-heading h1 {
            margin: 0;
            font-size: 32px;
            font-weight: 800;
            color: var(--text-1);
        }

        .category-tabs {
            display: flex;
            gap: 16px;
            border-bottom: 1px solid rgba(15,23,42,0.08);
            padding-bottom: 12px;
        }

        .category-tabs .tab {
            border: none;
            background: none;
            padding: 10px 18px;
            font-size: 15px;
            font-weight: 700;
            color: var(--text-2);
            border-radius: 999px;
            cursor: pointer;
            transition: background .2s ease, color .2s ease;
        }

        .category-tabs .tab:hover {
            background: rgba(255, 122, 162, 0.12);
            color: var(--pink-1);
        }

        .category-tabs .tab.active {
            background: linear-gradient(135deg, var(--pink-1), var(--pink-2));
            color: #fff;
            box-shadow: 0 10px 20px rgba(255, 122, 162, 0.35);
        }

        .board-table {
            width: 100%;
            border-collapse: collapse;
            font-size: 15px;
            color: var(--text-2);
        }

        .board-table thead th {
            text-align: left;
            padding: 14px 12px;
            font-weight: 700;
            color: var(--text-1);
            background: rgba(255, 122, 162, 0.08);
        }

        .board-table tbody td {
            padding: 16px 12px;
            border-bottom: 1px solid rgba(15,23,42,0.08);
        }

        .board-table tbody tr:hover {
            background: rgba(255, 122, 162, 0.05);
        }

        /* FAQ 아코디언 스타일 */
        .faq-container {
            display: none;
        }

        .faq-item {
            background: #fff;
            border: 1px solid rgba(15,23,42,0.08);
            border-radius: 12px;
            margin-bottom: 12px;
            overflow: hidden;
            transition: all 0.3s ease;
        }

        .faq-item:hover {
            box-shadow: 0 4px 12px rgba(15,23,42,0.1);
        }

        .faq-question {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 20px 24px;
            cursor: pointer;
            background: #fff;
            border: none;
            width: 100%;
            text-align: left;
            font-size: 16px;
            font-weight: 600;
            color: var(--text-1);
            transition: background-color 0.2s ease;
        }

        .faq-question:hover {
            background: rgba(255, 122, 162, 0.05);
        }

        .faq-question .q-prefix {
            color: var(--pink-1);
            font-weight: 700;
            margin-right: 8px;
        }

        .faq-question .arrow {
            color: var(--text-2);
            font-size: 18px;
            transition: transform 0.3s ease;
        }

        .faq-question.active .arrow {
            transform: rotate(180deg);
        }

        .faq-answer {
            max-height: 0;
            overflow: hidden;
            transition: max-height 0.3s ease;
            background: rgba(255, 122, 162, 0.02);
        }

        .faq-answer.active {
            max-height: 1000px;
        }

        .faq-answer-content {
            padding: 0 24px 24px 24px;
            line-height: 1.6;
            color: var(--text-1);
            white-space: pre-wrap;
        }

        .post-title-container {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .post-title {
            text-decoration: none;
            color: var(--text-1);
            font-weight: 600;
        }

        .post-title:hover {
            color: var(--pink-1);
        }

        .status-badge {
            display: inline-flex;
            align-items: center;
            gap: 4px;
            border-radius: 999px;
            padding: 4px 10px;
            font-size: 11px;
            font-weight: 700;
            text-transform: uppercase;
        }

        .status-badge.pending {
            background: rgba(255, 122, 162, 0.15);
            color: var(--pink-1);
        }

        .status-badge.answered {
            background: rgba(34, 197, 94, 0.15);
            color: #15803d;
        }

        .cta {
            display: flex;
            justify-content: flex-end;
            gap: 12px;
        }

        .btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            padding: 12px 24px;
            border-radius: 999px;
            font-weight: 700;
            font-size: 14px;
            text-decoration: none;
            cursor: pointer;
            border: none;
            transition: transform .15s ease, box-shadow .2s ease;
        }

        .btn:active {
            transform: translateY(1px);
        }

        .btn-primary {
            background: linear-gradient(135deg, var(--pink-1), var(--pink-2));
            color: #fff;
            box-shadow: 0 12px 24px rgba(255, 122, 162, 0.35);
        }

        .btn-primary:hover {
            filter: brightness(1.02);
        }

        .write-btn {
            background: #fff;
            color: var(--pink-1);
            border: 1px solid rgba(255, 122, 162, 0.4);
        }

        .write-btn:hover {
            background: rgba(255, 122, 162, 0.08);
        }

        .empty-state {
            text-align: center;
            padding: 48px 20px;
            color: var(--text-2);
        }

        .empty-state h3 {
            margin: 0 0 8px;
            color: var(--text-1);
        }

        .delete-btn {
            background: rgba(239, 68, 68, 0.12);
            color: #b91c1c;
            border: none;
            padding: 6px 12px;
            border-radius: 999px;
            font-weight: 600;
            cursor: pointer;
        }

        .delete-btn:hover {
            background: rgba(239, 68, 68, 0.18);
        }

        .pagination {
            display: flex;
            justify-content: center;
            gap: 10px;
            flex-wrap: wrap;
        }

        .page-btn {
            padding: 8px 14px;
            border-radius: 999px;
            border: 1px solid rgba(15, 23, 42, 0.12);
            color: var(--text-2);
            text-decoration: none;
            transition: background .2s ease, color .2s ease;
        }

        .page-btn:hover {
            background: rgba(255, 122, 162, 0.12);
            color: var(--pink-1);
        }

        .page-btn.active {
            background: linear-gradient(135deg, var(--pink-1), var(--pink-2));
            color: #fff;
            border-color: transparent;
            box-shadow: 0 10px 20px rgba(255, 122, 162, 0.3);
        }

        @media (max-width: 900px) {
            .board-card {
                padding: 32px 24px;
            }

            .board-heading {
                flex-direction: column;
                align-items: flex-start;
            }

            .category-tabs {
                flex-wrap: wrap;
                gap: 8px;
            }

            .board-table {
                font-size: 14px;
            }

            .board-table thead th,
            .board-table tbody td {
                padding: 12px 8px;
            }

            .cta {
                flex-direction: column;
                align-items: stretch;
            }

            .btn,
            .write-btn {
                width: 100%;
                justify-content: center;
            }
        }
    </style>
</head>
<body>
    <main class="board-page">
        <div class="board-container">
            <section class="board-card">
            <div class="board-heading">
                <h1>커뮤니티 게시판</h1>
                <a class="btn btn-primary" href="/branch/list">주문하러 가기</a>
            </div>

            <div class="category-tabs">
                <button class="tab" onclick="loadBoard(1, 0)">공지사항</button>
                <button class="tab active" onclick="loadBoard(2, 0)">1대1문의</button>
                <button class="tab" onclick="loadBoard(3, 0)">FAQ</button>
            </div>

            <!-- 게시글 목록 -->
            <div id="boardList">
                <!-- 일반 게시판 테이블 -->
                <table class="board-table" id="boardTable">
                    <thead>
                        <tr>
                            <th style="width: 80px;">번호</th>
                            <th>제목</th>
                            <th style="width: 100px;">작성자</th>
                            <th style="width: 120px;">작성일자</th>
                            <th style="width: 80px;">조회수</th>
                            <th class="action-cell"></th>
                        </tr>
                    </thead>
                    <tbody id="boardTableBody">
                        <!-- 게시글 목록이 여기에 동적으로 로드됩니다 -->
                    </tbody>
                </table>

                <!-- FAQ 아코디언 -->
                <div id="faqContainer" class="faq-container">
                    <!-- FAQ 항목들이 여기에 동적으로 로드됩니다 -->
                </div>

                <!-- 빈 상태 -->
                <div id="emptyState" class="empty-state" style="display: none;">
                    <h3>게시글이 없습니다</h3>
                    <p>아직 등록된 게시글이 없습니다.</p>
                </div>

                <!-- 페이지네이션 -->
                <div id="pagination" class="pagination">
                    <!-- 페이지네이션이 여기에 동적으로 로드됩니다 -->
                </div>

                <!-- CTA 버튼들 -->
                <div class="cta" id="writeButtonContainer">
                    <button class="btn write-btn" onclick="goToWrite()" id="writeButton">글쓰기</button>
                </div>
            </div>
            </section>
        </div>
    </main>

    <script>
        // 전역 변수 선언
        var currentBoardType = 2; // 기본값: 1대1문의
        var currentPage = 0;
        var totalPages = 0;
        var currentMemberId = null; // 현재 로그인한 사용자 ID
        var currentUserRole = null; // 현재 로그인한 사용자 역할

        // 페이지 로드 시 초기 데이터 로드
        document.addEventListener('DOMContentLoaded', function() {
            console.log('=== DOMContentLoaded START ===');
            console.log('currentBoardType:', currentBoardType);
            console.log('currentPage:', currentPage);
            console.log('Page loaded, calling loadBoard(2)');
            console.log('About to call loadBoard with boardTypeId=2, page=0');
            
            // 현재 로그인한 사용자 정보 가져오기
            getCurrentUser();
            
            // 명시적으로 숫자 값 전달
            loadBoard(2, 0);
        });

        // 게시판 로드 함수
        function loadBoard(boardTypeId, page) {
            console.log('=== loadBoard START ===');
            console.log('Arguments received:', arguments);
            console.log('boardTypeId:', boardTypeId, 'type:', typeof boardTypeId);
            console.log('page:', page, 'type:', typeof page);
            
            // page 기본값 설정
            if (page === undefined || page === null) {
                page = 0;
            }
            
            // 파라미터 검증 및 강제 변환
            if (boardTypeId === undefined || boardTypeId === null || boardTypeId === '') {
                console.error('boardTypeId is invalid:', boardTypeId);
                showEmptyState();
                return;
            }
            
            // 문자열을 숫자로 변환
            var convertedBoardTypeId = parseInt(boardTypeId);
            var convertedPage = parseInt(page) || 0;
            
            console.log('After conversion - boardTypeId:', convertedBoardTypeId, 'page:', convertedPage);
            
            if (isNaN(convertedBoardTypeId) || convertedBoardTypeId < 1 || convertedBoardTypeId > 3) {
                console.error('Invalid boardTypeId:', convertedBoardTypeId);
                showEmptyState();
                return;
            }
            
            // 변환된 값 사용
            var finalBoardTypeId = convertedBoardTypeId;
            var finalPage = convertedPage;
            
            console.log('Final values - boardTypeId:', finalBoardTypeId, 'page:', finalPage);
            
            currentBoardType = finalBoardTypeId;
            currentPage = finalPage;

            // 탭 활성화 상태 변경
            document.querySelectorAll('.tab').forEach(tab => tab.classList.remove('active'));
            
            // boardTypeId에 해당하는 탭을 활성화
            const tabs = document.querySelectorAll('.tab');
            if (boardTypeId === 1) tabs[0].classList.add('active');
            else if (boardTypeId === 2) tabs[1].classList.add('active');
            else if (boardTypeId === 3) tabs[2].classList.add('active');
            
            // 글쓰기 버튼 표시 제어
            controlWriteButton(finalBoardTypeId);

            // API 호출 전 최종 검증
            if (!boardTypeId || boardTypeId === '' || isNaN(boardTypeId)) {
                console.error('Invalid boardTypeId for API call:', boardTypeId);
                showEmptyState();
                return;
            }
            
            console.log('Calling API with boardTypeId:', finalBoardTypeId, 'page:', finalPage);
            
            // URL을 더 명시적으로 생성
            var url = '/boards?boardTypeId=' + finalBoardTypeId + '&page=' + finalPage + '&size=10';
            console.log('API URL:', url);
            console.log('URL parts - boardTypeId:', finalBoardTypeId, 'page:', finalPage);
            
            // URL이 올바른지 확인
            if (url.includes('boardTypeId=&') || url.includes('page=&')) {
                console.error('URL contains empty parameters:', url);
                console.error('boardTypeId value:', boardTypeId, 'type:', typeof boardTypeId);
                console.error('page value:', page, 'type:', typeof page);
                showEmptyState();
                return;
            }
            
            fetch(url, {
                credentials: 'same-origin'
            })
                .then(response => {
                    console.log('API response status:', response.status);
                    if (!response.ok) {
                        throw new Error('Network response was not ok: ' + response.status);
                    }
                    return response.json();
                })
                .then(data => {
                    console.log('API response data:', data);
                    displayBoardList(data);
                })
                .catch(error => {
                    console.error('API Error:', error);
                    showEmptyState();
                });
        }

        // 게시글 목록 표시
        function displayBoardList(data) {
            console.log('=== displayBoardList called ===');
            console.log('data:', data);
            console.log('data.boards:', data.boards);
            console.log('data.boards.length:', data.boards ? data.boards.length : 'undefined');
            console.log('currentBoardType:', currentBoardType);
            
            const tbody = document.getElementById('boardTableBody');
            const faqContainer = document.getElementById('faqContainer');
            const boardTable = document.getElementById('boardTable');
            const emptyState = document.getElementById('emptyState');
            const pagination = document.getElementById('pagination');

            // FAQ 화면인 경우 아코디언으로 표시
            if (currentBoardType === 3) {
                displayFAQList(data);
                return;
            }

            // 일반 게시판 화면 - 테이블 표시하고 FAQ 컨테이너 숨기기
            boardTable.style.display = 'table';
            faqContainer.style.display = 'none';

            // 일반 게시판 화면
            if (data.boards && data.boards.length > 0) {
                console.log('Displaying board list with', data.boards.length, 'items');
                tbody.innerHTML = '';
                data.boards.forEach((board, index) => {
                    console.log('Processing board:', board);
                    console.log('Board id:', board.id, 'type:', typeof board.id);
                    console.log('Board title:', board.title);
                    console.log('Board memberNickname:', board.memberNickname);
                    console.log('Board field:', board.field);
                    
                    const row = document.createElement('tr');
                    
                    // 번호 계산
                    const rowNumber = data.totalCount - (currentPage * 10 + index);
                    
                    // board.id가 유효한지 확인
                    if (!board.id) {
                        console.error('Board ID is missing for board:', board);
                        return;
                    }
                    
                    // 날짜 포맷팅 (분까지만)
                    var formattedDate = formatDateToMinute(board.field);
                    
                    // 삭제 버튼 HTML (본인 글인 경우에만)
                    var deleteButtonHtml = '';
                    if (currentMemberId && board.memberId === currentMemberId) {
                        deleteButtonHtml = '<button class="delete-btn" onclick="deletePost(' + board.id + ')">삭제</button>';
                    }
                    
                    // 답변상태 배지 생성 (공지사항이 아닌 경우에만)
                    var statusBadge = '';
                    if (currentBoardType !== 1) { // 공지사항(board_type_id=1)이 아닌 경우에만
                        if (board.statusTag === 'ANSWERED') {
                            statusBadge = '<span class="status-badge answered">답변완료</span>';
                        } else {
                            statusBadge = '<span class="status-badge pending">답변대기</span>';
                        }
                    }
                    
                    // HTML을 문자열 연결로 생성
                    row.innerHTML = '<td>' + rowNumber + '</td>' +
                                   '<td><div class="post-title-container"><a href="#" class="post-title" onclick="viewPost(' + board.id + ')">' + board.title + '</a>' + statusBadge + '</div></td>' +
                                   '<td>' + board.memberNickname + '</td>' +
                                   '<td>' + formattedDate + '</td>' +
                                   '<td>0</td>' +
                                   '<td class="action-cell">' + deleteButtonHtml + '</td>';
                    
                    console.log('Created row HTML:', row.innerHTML);
                    tbody.appendChild(row);
                });

                emptyState.style.display = 'none';
                displayPagination(data);
            } else {
                console.log('No boards found, showing empty state');
                showEmptyState();
            }
        }

        // FAQ 목록 표시 (아코디언 형태)
        function displayFAQList(data) {
            console.log('=== displayFAQList called ===');
            console.log('data:', data);
            
            const faqContainer = document.getElementById('faqContainer');
            const boardTable = document.getElementById('boardTable');
            const emptyState = document.getElementById('emptyState');
            const pagination = document.getElementById('pagination');

            // 일반 테이블 숨기고 FAQ 컨테이너 표시
            boardTable.style.display = 'none';
            faqContainer.style.display = 'block';

            if (data.boards && data.boards.length > 0) {
                console.log('Displaying FAQ list with', data.boards.length, 'items');
                faqContainer.innerHTML = '';
                
                data.boards.forEach((faq, index) => {
                    console.log('Processing FAQ:', faq);
                    console.log('FAQ Title:', faq.title);
                    console.log('FAQ Content:', faq.content);
                    
                    const faqItem = document.createElement('div');
                    faqItem.className = 'faq-item';
                    
                    // HTML을 직접 생성하여 이스케이프 문제 방지
                    const questionButton = document.createElement('button');
                    questionButton.className = 'faq-question';
                    questionButton.setAttribute('onclick', 'toggleFAQ(' + faq.id + ')');
                    
                    const questionSpan = document.createElement('span');
                    const qPrefix = document.createElement('span');
                    qPrefix.className = 'q-prefix';
                    qPrefix.textContent = 'Q.';
                    const titleText = document.createElement('span');
                    titleText.textContent = faq.title;
                    
                    questionSpan.appendChild(qPrefix);
                    questionSpan.appendChild(document.createTextNode(' '));
                    questionSpan.appendChild(titleText);
                    
                    const arrowSpan = document.createElement('span');
                    arrowSpan.className = 'arrow';
                    arrowSpan.textContent = '▼';
                    
                    questionButton.appendChild(questionSpan);
                    questionButton.appendChild(arrowSpan);
                    
                    const answerDiv = document.createElement('div');
                    answerDiv.className = 'faq-answer';
                    answerDiv.id = 'faq-answer-' + faq.id;
                    
                    const answerContent = document.createElement('div');
                    answerContent.className = 'faq-answer-content';
                    answerContent.textContent = faq.content;
                    
                    answerDiv.appendChild(answerContent);
                    
                    faqItem.appendChild(questionButton);
                    faqItem.appendChild(answerDiv);
                    
                    faqContainer.appendChild(faqItem);
                });

                emptyState.style.display = 'none';
                displayPagination(data);
            } else {
                console.log('No FAQ found, showing empty state');
                showEmptyState();
            }
        }

        // FAQ 토글 함수
        function toggleFAQ(faqId) {
            console.log('toggleFAQ called with faqId:', faqId);
            
            const question = document.querySelector('button[onclick="toggleFAQ(' + faqId + ')"]');
            const answer = document.getElementById('faq-answer-' + faqId);
            
            if (!question || !answer) {
                console.error('FAQ elements not found for ID:', faqId);
                console.error('Question element:', question);
                console.error('Answer element:', answer);
                return;
            }
            
            // 현재 상태 확인
            const isActive = question.classList.contains('active');
            console.log('Current active state:', isActive);
            
            // 모든 FAQ 닫기
            document.querySelectorAll('.faq-question').forEach(q => {
                q.classList.remove('active');
            });
            document.querySelectorAll('.faq-answer').forEach(a => {
                a.classList.remove('active');
            });
            
            // 클릭한 FAQ가 닫혀있었다면 열기
            if (!isActive) {
                question.classList.add('active');
                answer.classList.add('active');
                console.log('FAQ opened:', faqId);
            } else {
                console.log('FAQ closed:', faqId);
            }
        }

        // 빈 상태 표시
        function showEmptyState() {
            const boardTable = document.getElementById('boardTable');
            const faqContainer = document.getElementById('faqContainer');
            const emptyState = document.getElementById('emptyState');
            const pagination = document.getElementById('pagination');
            
            // FAQ 화면인 경우
            if (currentBoardType === 3) {
                boardTable.style.display = 'none';
                faqContainer.style.display = 'none';
            } else {
                // 일반 게시판 화면
                document.getElementById('boardTableBody').innerHTML = '';
                boardTable.style.display = 'table';
                faqContainer.style.display = 'none';
            }
            
            emptyState.style.display = 'block';
            pagination.innerHTML = '';
        }

        // 페이지네이션 표시
        function displayPagination(data) {
            const pagination = document.getElementById('pagination');
            pagination.innerHTML = '';

            totalPages = data.totalPages;

            // 이전 페이지 버튼
            if (currentPage > 0) {
                const prevBtn = document.createElement('a');
                prevBtn.href = '#';
                prevBtn.className = 'page-btn';
                prevBtn.textContent = '이전';
                prevBtn.onclick = (e) => {
                    e.preventDefault();
                    loadBoard(currentBoardType, currentPage - 1);
                };
                pagination.appendChild(prevBtn);
            }

            // 페이지 번호들
            const startPage = Math.max(0, currentPage - 2);
            const endPage = Math.min(totalPages - 1, currentPage + 2);

            for (let i = startPage; i <= endPage; i++) {
                const pageBtn = document.createElement('a');
                pageBtn.href = '#';
                pageBtn.className = `page-btn ${i == currentPage ? 'active' : ''}`;
                pageBtn.textContent = i + 1;
                pageBtn.onclick = (e) => {
                    e.preventDefault();
                    loadBoard(currentBoardType, i);
                };
                pagination.appendChild(pageBtn);
            }

            // 다음 페이지 버튼
            if (currentPage < totalPages - 1) {
                const nextBtn = document.createElement('a');
                nextBtn.href = '#';
                nextBtn.className = 'page-btn';
                nextBtn.textContent = '다음';
                nextBtn.onclick = (e) => {
                    e.preventDefault();
                    loadBoard(currentBoardType, currentPage + 1);
                };
                pagination.appendChild(nextBtn);
            }
        }

        // 게시글 상세보기
        function viewPost(boardId) {
            console.log('viewPost called with boardId:', boardId);
            console.log('boardId type:', typeof boardId);
            
            if (!boardId) {
                console.error('boardId is null or undefined');
                alert('게시글 ID가 올바르지 않습니다.');
                return;
            }
            
            var detailUrl = '/boards/detail/' + boardId;
            console.log('Redirecting to:', detailUrl);
            window.location.href = detailUrl;
        }

        // 글쓰기 버튼 표시 제어
        function controlWriteButton(boardTypeId) {
            const writeButton = document.getElementById('writeButton');
            const writeButtonContainer = document.getElementById('writeButtonContainer');
            
            if (boardTypeId === 1) { // 공지사항
                if (currentUserRole === 'ADMIN') {
                    writeButtonContainer.style.display = 'flex';
                    writeButton.textContent = '공지사항 작성';
                } else {
                    writeButtonContainer.style.display = 'none';
                }
            } else if (boardTypeId === 2) { // 1:1문의
                writeButtonContainer.style.display = 'flex';
                writeButton.textContent = '글쓰기';
            } else if (boardTypeId === 3) { // FAQ
                writeButtonContainer.style.display = 'none';
            }
        }

        // 글쓰기 페이지로 이동
        function goToWrite() {
            // 공지사항 게시판인 경우 ADMIN만 글쓰기 가능
            if (currentBoardType === 1) {
                if (currentUserRole !== 'ADMIN') {
                    alert('공지사항은 관리자만 작성할 수 있습니다.');
                    return;
                }
            }
            // URL 파라미터로 게시판 타입 전달
            window.location.href = '/boards/write?boardType=' + currentBoardType;
        }

        // 현재 로그인한 사용자 정보 가져오기
        function getCurrentUser() {
            fetch('/api/users/me', {
                credentials: 'same-origin'
            })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    console.log('User not logged in or API not available');
                    return null;
                }
            })
            .then(data => {
                if (data && data.memberId) {
                    currentMemberId = data.memberId;
                    currentUserRole = data.role;
                    console.log('Current user ID:', currentMemberId);
                    console.log('Current user role:', currentUserRole);
                } else {
                    console.log('No user data available');
                }
            })
            .catch(error => {
                console.log('Error getting user info:', error);
            });
        }

        // 게시글 삭제
        function deletePost(boardId) {
            console.log('deletePost called with boardId:', boardId);
            
            if (!boardId) {
                console.error('boardId is null or undefined');
                alert('게시글 ID가 올바르지 않습니다.');
                return;
            }
            
            // 삭제 확인
            if (!confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
                return;
            }
            
            fetch('/boards/' + boardId, {
                method: 'DELETE',
                credentials: 'same-origin'
            })
            .then(response => {
                console.log('Delete response status:', response.status);
                if (!response.ok) {
                    throw new Error('Network response was not ok: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                console.log('Delete response data:', data);
                if (data.success) {
                    alert('게시글이 삭제되었습니다.');
                    // 목록 새로고침
                    loadBoard(currentBoardType, currentPage);
                } else {
                    alert('삭제 실패: ' + (data.message || '알 수 없는 오류'));
                }
            })
            .catch(error => {
                console.error('Delete error:', error);
                alert('삭제 중 오류가 발생했습니다: ' + error.message);
            });
        }

        // 날짜 포맷팅 (분까지만)
        function formatDateToMinute(dateString) {
            const date = new Date(dateString);
            return date.toLocaleString('ko-KR', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit',
                hour12: false
            }).replace(/\./g, '.').replace(/\s/g, ' ');
        }

        // 날짜 포맷팅 (기존 함수 - 상세 페이지용)
        function formatDate(dateString) {
            const date = new Date(dateString);
            return date.toLocaleDateString('ko-KR', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit'
            }).replace(/\./g, '.').replace(/\s/g, '');
        }
    </script>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
