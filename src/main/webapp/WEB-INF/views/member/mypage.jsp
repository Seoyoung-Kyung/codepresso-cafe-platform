<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body class="mypage-body">
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<style>
    .mypage-main {
        padding: 40px 0 80px;
    }
    .mypage-container {
        max-width: 960px;
        margin: 0 auto;
        padding: 0 20px;
    }
    .mypage-body .btn.btn-ghost {
        background: #fff;
        color: var(--pink-1);
        border: 1px solid rgba(255,122,162,0.6);
    }
    .mypage-body .btn.btn-ghost:hover {
        border-color: var(--pink-1);
        background: rgba(255,122,162,0.08);
        color: var(--pink-1);
    }
</style>

<main class="mypage-main">
    <div class="mypage-container">
        <%@ include file="/WEB-INF/views/member/mypage-header.jspf" %>

        <c:if test="${not empty success}">
            <div class="success-message" style="background: #d4edda; color: #155724; padding: 12px 16px; border-radius: 8px; margin-bottom: 16px;">✅ ${success}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="error-message" style="background: #f8d7da; color: #721c24; padding: 12px 16px; border-radius: 8px; margin-bottom: 16px;">❌ ${error}</div>
        </c:if>

        <style>
            .info-list { list-style: none; padding: 0; margin: 20px 0; display: grid; gap: 16px; }
            .info-item {
                display: grid;
                grid-template-columns: 140px 1fr;
                align-items: center;
                gap: 12px;
                padding-bottom: 14px;
                border-bottom: 1px dashed rgba(255, 122, 162, 0.5);
            }
            .info-item:last-child { border-bottom: none; padding-bottom: 0; }
            .info-item b { font-weight: 700; color: var(--text-1); }
            .edit-mode { display: none; }
            .display-mode { display: block; }
            .edit-mode input {
                width: 100%;
                padding: 10px 14px;
                border: 1px solid rgba(15,23,42,0.12);
                border-radius: 12px;
                font-size: 14px;
                margin-top: 4px;
            }
        </style>

        <!--
                  간단 표시: accountId는 userPrincipal.name으로 확인 가능
                  추가 정보(이메일/닉네임 등)는 API로 불러오거나, 시큐리티 태그로 principal 확장 정보를 노출하는 방식으로 확장할 수 있어요.
                -->
                <ul class="info-list">
                    <li class="info-item">
                        <b>프로필 이미지</b>
                        <div style="display: flex; align-items: center; gap: 12px;">
                            <div id="profile-image-container" style="width: 200px; height: 200px; border-radius: 50%; overflow: hidden; border: 2px solid #ddd; background: #f8f9fa; display: flex; align-items: center; justify-content: center;">
                                <img id="profile-image" src="" alt="프로필 이미지" style="width: 100%; height: 100%; object-fit: cover; display: none;">
                                <span id="profile-placeholder" style="color: #999; font-size: 12px;">이미지 없음</span>
                            </div>
                            <div class="edit-mode" id="profile-image-edit" style="display: none; flex-direction: column; gap: 8px;">
                                <input type="file" id="profile-image-input" accept="image/*" style="display: none;">
                                <button type="button" class="btn btn-ghost" onclick="document.getElementById('profile-image-input').click()" style="padding: 6px 12px; font-size: 12px;">이미지 선택</button>
                                <button type="button" class="btn btn-ghost" onclick="deleteProfileImage()" style="padding: 6px 12px; font-size: 12px; color: #dc3545;">이미지 삭제</button>
                            </div>
                        </div>
                        <div id="profile-image-status" style="font-size: 12px; margin-top: 4px; white-space: nowrap; word-wrap: break-word; overflow-wrap: break-word;"></div>
                    </li>
                    <li class="info-item">
                        <b>아이디</b> ${pageContext.request.userPrincipal.name}
                    </li>
                    <li class="info-item">
                        <b>이름</b> 
                        <span id="name-display" class="display-mode">불러오는 중...</span>
                        <div class="edit-mode" id="name-edit">
                            <input type="text" id="name-input" placeholder="이름을 입력하세요">
                        </div>
                    </li>
                    <li class="info-item">
                        <b>전화번호</b> 
                        <span id="phone-display" class="display-mode">불러오는 중...</span>
                        <div class="edit-mode" id="phone-edit">
                            <input type="text" id="phone-input" placeholder="전화번호를 입력하세요">
                        </div>
                    </li>
                    <li class="info-item">
                        <b>이메일</b> 
                        <span id="email-display" class="display-mode">불러오는 중...</span>
                        <div class="edit-mode" id="email-edit">
                            <div style="display: flex; gap: 8px; align-items: center;">
                                <input type="email" id="email-input" placeholder="이메일을 입력하세요" style="flex: 1; min-width: 300px; padding: 10px 14px; border: 1px solid #ddd; border-radius: 8px; font-size: 14px;">
                                <button type="button" class="btn btn-ghost" onclick="sendEmailVerification()" style="padding: 8px 12px; font-size: 12px; white-space: nowrap;">인증번호발송</button>
                            </div>
                            <div id="email-status" style="font-size: 12px; margin-top: 4px; white-space: nowrap; word-wrap: break-word; overflow-wrap: break-word;"></div>
                            
                            <!-- 이메일 인증 영역 -->
                            <div class="email-verification" id="emailVerification" style="display: none; margin-top: 12px; padding: 16px 20px; background: #f8f9fa; border-radius: 8px; min-width: 500px;">
                                <div style="font-size: 12px; color: #666; margin-bottom: 8px;">
                                    <strong>인증번호가 이메일로 발송되었습니다!</strong><br>
                                    이메일을 확인하고 인증번호를 입력해주세요.
                                </div>
                                <div style="display: flex; gap: 8px; align-items: center;">
                                    <input type="text" id="emailVerificationCode" placeholder="인증번호 입력" style="flex: 1; min-width: 200px; padding: 10px 14px; border: 1px solid #ddd; border-radius: 8px; font-size: 14px;">
                                    <button type="button" class="btn btn-ghost" onclick="confirmEmailVerification()" style="padding: 8px 12px; font-size: 12px; white-space: nowrap;">인증확인</button>
                                </div>
                                <div id="emailVerificationMsg" style="font-size: 12px; margin-top: 4px;"></div>
                            </div>
                        </div>
                    </li>
                    <li class="info-item">
                        <b>닉네임</b> 
                        <span id="nickname-display" class="display-mode">불러오는 중...</span>
                        <div class="edit-mode" id="nickname-edit">
                            <div style="display: flex; gap: 8px; align-items: center;">
                                <input type="text" id="nickname-input" placeholder="닉네임을 입력하세요" style="flex: 1; min-width: 300px; padding: 10px 14px; border: 1px solid #ddd; border-radius: 8px; font-size: 14px;">
                                <button type="button" class="btn btn-ghost" onclick="checkNicknameDuplicate()" style="padding: 8px 12px; font-size: 12px; white-space: nowrap;">중복체크</button>
                            </div>
                            <div id="nickname-status" style="font-size: 12px; margin-top: 4px; white-space: nowrap; word-wrap: break-word; overflow-wrap: break-word;"></div>
                        </div>
                    </li>
                </ul>

                <!-- 수정 모드 저장/취소 버튼 -->
                <div class="edit-mode" id="edit-controls" style="text-align: center; margin: 20px 0;">
                    <button class="btn btn-primary" onclick="saveProfile()" style="padding: 12px 32px; font-size: 16px;">💾 저장</button>
                    <button class="btn btn-ghost" onclick="cancelEdit()" style="padding: 12px 32px; font-size: 16px; margin-left: 12px;">❌ 취소</button>
                </div>

                <!-- 하단 버튼 -->
                <div style="text-align: center; margin: 32px 0 20px;">
                    <a class="btn btn-primary" href="/branch/list" style="padding: 16px 48px; font-size: 18px; font-weight: 700;">주문하러 가기</a>
                    <button class="btn btn-ghost" id="edit-btn" onclick="toggleEditMode()" style="padding: 16px 48px; font-size: 18px; font-weight: 700; margin-left: 12px;">정보 수정</button>
                </div>

                <script>
                    let isEditMode = false;
                    let originalData = {};
                    
                    // 이메일 인증 관련 변수
                    let isEmailVerified = false;
                    let emailVerificationCode = '';

                    // 프로필 이미지 관련 변수
                    let currentProfileImage = null;

                    // 필요 시, 본인 정보 API(/api/users/me)를 호출해 닉네임/이메일을 채워 넣습니다.
                    function formatPhone(value){
                        const d = (value||'').replace(/\D/g,'').slice(0,11);
                        if(!d) return '';
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

                    // 편집 모드 토글
                    function toggleEditMode() {
                        isEditMode = !isEditMode;
                        
                        if (isEditMode) {
                            // 편집 모드로 전환
                            document.getElementById('edit-btn').style.display = 'none';
                            document.getElementById('edit-controls').style.display = 'block';
                            
                            // 기존 정보 표시 부분 숨기기
                            document.querySelectorAll('.display-mode').forEach(el => {
                                el.style.display = 'none';
                            });
                            
                            // 모든 편집 필드 표시
                            document.querySelectorAll('.edit-mode').forEach(el => {
                                if (el.id !== 'edit-controls') {
                                    if (el.id === 'profile-image-edit') {
                                        el.style.display = 'flex'; // 프로필 이미지 편집 영역은 flex로
                                    } else {
                                        el.style.display = 'block'; // 나머지는 block으로
                                    }
                                }
                            });
                            
                            // 현재 값들을 입력 필드에 설정
                            document.getElementById('name-input').value = originalData.name || '';
                            document.getElementById('phone-input').value = originalData.phone || '';
                            document.getElementById('email-input').value = originalData.email || '';
                            document.getElementById('nickname-input').value = originalData.nickname || '';
                            
                            // 중복 확인 상태 초기화
                            document.getElementById('email-status').textContent = '';
                            document.getElementById('nickname-status').textContent = '';
                            
                            // 이메일 인증 상태 초기화
                            isEmailVerified = false;
                            document.getElementById('emailVerification').style.display = 'none';
                            document.getElementById('emailVerificationCode').value = '';
                            document.getElementById('emailVerificationMsg').textContent = '';
                            
                            // 프로필 이미지 상태 메시지 초기화
                            document.getElementById('profile-image-status').textContent = '';
                            
                        } else {
                            // 보기 모드로 전환
                            document.getElementById('edit-btn').style.display = 'block';
                            document.getElementById('edit-controls').style.display = 'none';
                            
                            // 기존 정보 표시 부분 다시 보이기
                            document.querySelectorAll('.display-mode').forEach(el => {
                                el.style.display = 'block';
                            });
                            
                            // 모든 편집 필드 숨김
                            document.querySelectorAll('.edit-mode').forEach(el => {
                                if (el.id !== 'edit-controls') {
                                    el.style.display = 'none';
                                }
                            });
                        }
                    }

                    // 편집 취소
                    function cancelEdit() {
                        toggleEditMode();
                    }

                    // 한글 라벨 변환 (signup.jsp와 동일)
                    function toKoreanField(f) {
                        switch(f) {
                            case 'id': return '아이디';
                            case 'nickname': return '닉네임';
                            case 'email': return '이메일';
                            default: return '아이디';
                        }
                    }

                    // 2단계 폴백 방식 중복체크 함수 (signup.jsp와 동일)
                    async function checkDup(endpoint, value, msgEl, label) {
                        // 허용되지 않은 endpoint가 오면 기본값 id로 보정
                        const field = (endpoint === 'id' || endpoint === 'nickname' || endpoint === 'email') ? endpoint : 'id';
                        // 라벨은 항상 필드에서 도출하여 빈 문자열 문제 방지
                        const labelText = toKoreanField(field);
                        if (!value) { 
                            msgEl.textContent = labelText + '를 입력해주세요.'; 
                            msgEl.className = 'msg bad'; 
                            return; 
                        }
                        
                        const q = encodeURIComponent(value);
                        try {
                            // 1차 시도: 쿼리 파라미터 방식 (/check?field={field}&value=...)
                            const url1 = '/api/auth/check?field=' + encodeURIComponent(field) + '&value=' + q;
                            console.log('[dup-check] try1', field, url1);
                            let res = await fetch(url1);
                            
                            // 2차 시도: RESTful 경로 (/check/{field}?value=...)
                            if (!res.ok) {
                                const url2 = '/api/auth/check/' + encodeURIComponent(field) + '?value=' + q;
                                console.log('[dup-check] try2', field, url2);
                                res = await fetch(url2);
                            }
                            
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

                    // 이메일 인증번호 발송 (중복체크 + 인증번호 발송)
                    async function sendEmailVerification() {
                        const email = document.getElementById('email-input').value.trim();
                        const statusDiv = document.getElementById('email-status');
                        const verificationSection = document.getElementById('emailVerification');
                        const verificationMsg = document.getElementById('emailVerificationMsg');

                        if (!email) {
                            alert('이메일을 입력해주세요.');
                            return;
                        }

                        // 현재 이메일과 같으면 인증 불필요
                        if (email === originalData.email) {
                            statusDiv.textContent = '현재 사용 중인 이메일입니다.';
                            statusDiv.style.color = '#666';
                            verificationSection.style.display = 'none';
                            isEmailVerified = true;
                            return;
                        }

                        try {
                            // 1. 이메일 중복체크
                            const checkRes = await fetch('/api/auth/check?field=email&value=' + encodeURIComponent(email));
                            if (!checkRes.ok) throw new Error('중복체크 실패');

                            const checkData = await checkRes.json();
                            if (checkData.duplicate) {
                                statusDiv.textContent = '이미 사용 중인 이메일입니다.';
                                statusDiv.style.color = '#dc3545';
                                verificationSection.style.display = 'none';
                                isEmailVerified = false;
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

                            statusDiv.textContent = '인증번호가 이메일로 발송되었습니다.';
                            statusDiv.style.color = '#28a745';
                            verificationSection.style.display = 'block';
                            isEmailVerified = false;

                        } catch (error) {
                            console.error('이메일 인증 발송 실패:', error);
                            statusDiv.textContent = error.message || '이메일 발송에 실패했습니다. 다시 시도해주세요.';
                            statusDiv.style.color = '#dc3545';
                            verificationSection.style.display = 'none';
                            isEmailVerified = false;
                        }
                    }

                    // 이메일 인증번호 확인
                    async function confirmEmailVerification() {
                        const inputCode = document.getElementById('emailVerificationCode').value.trim();
                        const verificationMsg = document.getElementById('emailVerificationMsg');
                        const statusDiv = document.getElementById('email-status');

                        if (!inputCode) {
                            verificationMsg.textContent = '인증번호를 입력해주세요.';
                            verificationMsg.style.color = '#dc3545';
                            return;
                        }

                        try {
                            const response = await fetch('/api/auth/verify-email-code', {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify({
                                    email: document.getElementById('email-input').value.trim(),
                                    code: inputCode
                                })
                            });

                            const data = await response.json();

                            if (data.valid) {
                                verificationMsg.textContent = '이메일 인증이 완료되었습니다.';
                                verificationMsg.style.color = '#28a745';
                                statusDiv.textContent = '이메일 인증이 완료되었습니다.';
                                statusDiv.style.color = '#28a745';
                                isEmailVerified = true;
                                
                                // 인증 완료 후 3초 뒤 인증 영역 숨김
                                setTimeout(() => {
                                    document.getElementById('emailVerification').style.display = 'none';
                                }, 3000);
                            } else {
                                verificationMsg.textContent = data.message || '인증번호가 일치하지 않습니다.';
                                verificationMsg.style.color = '#dc3545';
                                isEmailVerified = false;
                            }
                        } catch (error) {
                            console.error('이메일 인증 확인 실패:', error);
                            verificationMsg.textContent = '인증 중 오류가 발생했습니다.';
                            verificationMsg.style.color = '#dc3545';
                            isEmailVerified = false;
                        }
                    }

                    // 닉네임 중복 확인
                    function checkNicknameDuplicate() {
                        const nickname = document.getElementById('nickname-input').value;
                        const statusDiv = document.getElementById('nickname-status');
                        
                        if (!nickname) {
                            alert('닉네임을 입력해주세요.');
                            return;
                        }

                        // 현재 닉네임과 같으면 중복 확인하지 않음
                        if (nickname === originalData.nickname) {
                            statusDiv.textContent = '현재 사용 중인 닉네임입니다.';
                            statusDiv.style.color = '#666';
                            return;
                        }

                        checkDup('nickname', nickname, statusDiv, '닉네임');
                    }



                    // 프로필 저장
                    function saveProfile() {
                        const email = document.getElementById('email-input').value;
                        const nickname = document.getElementById('nickname-input').value;
                        const emailStatus = document.getElementById('email-status').textContent;
                        const nicknameStatus = document.getElementById('nickname-status').textContent;

                        // 이메일이 변경되었는데 인증을 하지 않은 경우
                        if (email && email !== originalData.email && !isEmailVerified) {
                            alert('이메일 인증을 완료해주세요.');
                            return;
                        }

                        // 닉네임이 변경되었는데 중복 확인을 하지 않은 경우
                        if (nickname && nickname !== originalData.nickname && !nicknameStatus.includes('사용 가능') && !nicknameStatus.includes('현재 사용 중')) {
                            alert('닉네임 중복체크를 해주세요.');
                            return;
                        }

                        // 중복된 값으로는 저장 불가
                        if (email && email !== originalData.email && emailStatus.includes('이미 사용 중')) {
                            alert('이메일이 중복됩니다. 다른 이메일을 입력해주세요.');
                            return;
                        }

                        if (nickname && nickname !== originalData.nickname && nicknameStatus.includes('이미 사용 중')) {
                            alert('닉네임이 중복됩니다. 다른 닉네임을 입력해주세요.');
                            return;
                        }

                        const requestData = {
                            name: document.getElementById('name-input').value,
                            phone: document.getElementById('phone-input').value,
                            email: email,
                            nickname: nickname
                        };

                        fetch('/api/users/me', {
                            method: 'PUT',
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify(requestData)
                        })
                        .then(response => {
                            if (response.ok) {
                                // 성공 시 페이지 새로고침
                                location.reload();
                            } else {
                                alert('프로필 수정에 실패했습니다.');
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            alert('프로필 수정 중 오류가 발생했습니다.');
                        });
                    }

                    // 프로필 이미지 업로드
                    function uploadProfileImage() {
                        const fileInput = document.getElementById('profile-image-input');
                        const file = fileInput.files[0];
                        
                        if (!file) {
                            showProfileImageStatus('파일을 선택해주세요.', false);
                            return;
                        }

                        const formData = new FormData();
                        formData.append('file', file);

                        fetch('/api/profile/image', {
                            method: 'POST',
                            body: formData,
                            credentials: 'same-origin'
                        })
                        .then(response => response.json())
                        .then(data => {
                            if (data.success) {
                                showProfileImageStatus('프로필 이미지가 업로드되었습니다.', true);
                                displayProfileImage(data.imageUrl);
                                currentProfileImage = data.imageUrl;
                            } else {
                                showProfileImageStatus(data.message || '업로드에 실패했습니다.', false);
                            }
                        })
                        .catch(error => {
                            console.error('프로필 이미지 업로드 오류:', error);
                            showProfileImageStatus('업로드 중 오류가 발생했습니다.', false);
                        });
                    }

                    // 프로필 이미지 삭제
                    function deleteProfileImage() {
                        if (!currentProfileImage) {
                            showProfileImageStatus('삭제할 이미지가 없습니다.', false);
                            return;
                        }

                        if (!confirm('프로필 이미지를 삭제하시겠습니까?')) {
                            return;
                        }

                        fetch('/api/profile/image', {
                            method: 'DELETE',
                            credentials: 'same-origin'
                        })
                        .then(response => response.json())
                        .then(data => {
                            if (data.success) {
                                showProfileImageStatus('프로필 이미지가 삭제되었습니다.', true);
                                hideProfileImage();
                                currentProfileImage = null;
                            } else {
                                showProfileImageStatus(data.message || '삭제에 실패했습니다.', false);
                            }
                        })
                        .catch(error => {
                            console.error('프로필 이미지 삭제 오류:', error);
                            showProfileImageStatus('삭제 중 오류가 발생했습니다.', false);
                        });
                    }

                    // 프로필 이미지 표시
                    function displayProfileImage(imageUrl) {
                        const img = document.getElementById('profile-image');
                        const placeholder = document.getElementById('profile-placeholder');
                        
                        img.src = imageUrl;
                        img.style.display = 'block';
                        placeholder.style.display = 'none';
                    }

                    // 프로필 이미지 숨김
                    function hideProfileImage() {
                        const img = document.getElementById('profile-image');
                        const placeholder = document.getElementById('profile-placeholder');
                        
                        img.style.display = 'none';
                        placeholder.style.display = 'block';
                    }

                    // 프로필 이미지 상태 메시지 표시
                    function showProfileImageStatus(message, isSuccess) {
                        const statusDiv = document.getElementById('profile-image-status');
                        statusDiv.textContent = message;
                        statusDiv.style.color = isSuccess ? '#28a745' : '#dc3545';
                        
                        if (isSuccess) {
                            setTimeout(() => {
                                statusDiv.textContent = '';
                            }, 3000);
                        }
                    }

                    // 파일 선택 이벤트 리스너
                    document.getElementById('profile-image-input').addEventListener('change', uploadProfileImage);

                    // 페이지 로드 시 사용자 정보 가져오기
                    (async function(){
                        try {
                            const res = await fetch('/api/users/me');
                            if (!res.ok) {
                                if (res.status === 401) {
                                    alert('로그인이 필요합니다. 로그인 페이지로 이동합니다.');
                                    window.location.href = '/auth/login';
                                    return;
                                }
                                return; // 기타 오류
                            }
                            const data = await res.json();
                            
                            // 원본 데이터 저장
                            originalData = data;
                            
                            // 화면에 표시
                            if (data.name) document.getElementById('name-display').textContent = data.name;
                            if (data.phone) document.getElementById('phone-display').textContent = formatPhone(data.phone);
                            if (data.email) document.getElementById('email-display').textContent = data.email;
                            if (data.nickname) document.getElementById('nickname-display').textContent = data.nickname;
                            
                            // 프로필 이미지 표시
                            if (data.profileImage) {
                                displayProfileImage(data.profileImage);
                                currentProfileImage = data.profileImage;
                            }
                        } catch (e) { 
                            console.error('사용자 정보 로드 오류:', e);
                            alert('사용자 정보를 불러올 수 없습니다. 로그인 페이지로 이동합니다.');
                            window.location.href = '/auth/login';
                        }
                    })();
                </script>
            </div>
        </div>
    </div>
    
</main>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
