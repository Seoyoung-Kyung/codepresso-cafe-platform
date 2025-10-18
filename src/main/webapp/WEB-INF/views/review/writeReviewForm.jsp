<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/common/head.jspf" %>

<body>
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<%--<!-- 카테고리 네비게이션 -->--%>
<%--<div style="background: var(--white); border-bottom: 1px solid rgba(0,0,0,0.05); padding: 16px 0;">--%>
<%--    <div class="container">--%>
<%--        <nav style="display: flex; gap: 40px; justify-content: center; align-items: center;">--%>
<%--            <a href="#" style="text-decoration: none; color: var(--text-2); font-weight: 600; padding: 8px 0;">커피</a>--%>
<%--            <a href="#" style="text-decoration: none; color: var(--text-2); font-weight: 600; padding: 8px 0;">라떼</a>--%>
<%--            <a href="#" style="text-decoration: none; color: var(--text-2); font-weight: 600; padding: 8px 0;">주스 & 드링크</a>--%>
<%--            <a href="#" style="text-decoration: none; color: var(--text-2); font-weight: 600; padding: 8px 0;">바나치노 & 스무디</a>--%>
<%--            <a href="#" style="text-decoration: none; color: var(--text-2); font-weight: 600; padding: 8px 0;">티 & 에이드</a>--%>
<%--            <a href="#" style="text-decoration: none; color: var(--text-2); font-weight: 600; padding: 8px 0;">디저트</a>--%>
<%--            <a href="#" style="text-decoration: none; color: var(--text-2); font-weight: 600; padding: 8px 0;">세트메뉴</a>--%>
<%--            <a href="#" style="text-decoration: none; color: var(--pink-1); font-weight: 700; padding: 8px 0; border-bottom: 2px solid var(--pink-1);">MD</a>--%>
<%--        </nav>--%>
<%--    </div>--%>
<%--</div>--%>

<main style="padding: 40px 0; flex: 1;">
    <div class="container">
        <div style="display: grid; grid-template-columns: 1.2fr 0.8fr; gap: 40px; align-items: start;">

            <!-- 왼쪽: 리뷰 작성 폼 -->
            <div style="background: var(--white); border-radius: var(--radius); padding: 32px; box-shadow: var(--shadow);">
                <div style="display: flex; align-items: center; gap: 16px; margin-bottom: 32px;">
                    <h2 style="margin: 0; font-size: 28px; font-weight: 800; color: var(--text-1);">${empty reviewId ? '리뷰 작성' : '리뷰 수정'}</h2>
                    <span style="background: linear-gradient(135deg, var(--pink-1), var(--pink-2)); color: var(--white); padding: 4px 12px; border-radius: 999px; font-size: 12px; font-weight: 700;">${empty reviewId ? 'NEW' : 'EDIT'}</span>
                </div>
                <p style="color: var(--text-2); margin: 0 0 32px; font-size: 16px;">${empty reviewId ? '상품에 대한 솔직한 리뷰를 작성해 주세요.' : '리뷰 내용을 수정해 주세요.'}</p>

                <form method="post" action="${empty reviewId ? '/users/reviews/create' : '/users/reviews/update'}" enctype="multipart/form-data">
                    <c:if test="${not empty _csrf}">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                    </c:if>

                    <!-- 수정 모드일 때 reviewId 추가 -->
                    <c:if test="${not empty reviewId}">
                        <input type="hidden" name="reviewId" value="${reviewId}" />
                        <input type="hidden" name="photoUrl" value="${review.photoUrl}" />
                    </c:if>

                    <!-- orderDetailId hidden input (작성 모드일 때만) -->
                    <c:if test="${empty reviewId}">
                        <input type="hidden" name="orderDetailId" value="${orderDetail.orderDetailId}" />
                    </c:if>

                    <!-- 상품 정보 (작성/수정 모드 모두 표시) -->
                    <div style="background: linear-gradient(120deg, var(--pink-4), #fff); border-radius: 12px; padding: 20px; margin-bottom: 24px; border: 1px solid rgba(255,122,162,0.1);">
                        <div style="display: flex; align-items: center; gap: 16px;">
                            <div style="width: 60px; height: 60px; background: var(--white); border-radius: 8px; display: grid; place-items: center; box-shadow: 0 4px 8px rgba(0,0,0,0.05);">
                                <c:choose>
                                    <c:when test="${empty reviewId}">
                                        <img src="${orderDetail.productPhoto}" alt="상품 이미지" style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;" />
                                    </c:when>
                                    <c:otherwise>
                                        <img src="${review.productPhoto}" alt="상품 이미지" style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;" />
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div>
                                <c:choose>
                                    <c:when test="${empty reviewId}">
                                        <h4 style="margin: 0 0 4px; font-size: 16px; font-weight: 700; color: var(--text-1);">${orderDetail.productName}</h4>
                                        <p style="margin: 0; color: var(--text-2); font-size: 14px;">${orderDetail.branchName}에서 주문한 상품</p>
                                    </c:when>
                                    <c:otherwise>
                                        <h4 style="margin: 0 0 4px; font-size: 16px; font-weight: 700; color: var(--text-1);">${review.productName}</h4>
                                        <p style="margin: 0; color: var(--text-2); font-size: 14px;">${review.branchName}에서 주문한 상품</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>

                    <!-- 별점 -->
                    <div style="margin-bottom: 24px;">
                        <label style="display: block; margin-bottom: 8px; font-weight: 700; color: var(--text-1);">별점</label>
                        <div style="display: flex; gap: 4px; align-items: center;">
                            <div class="star-rating" style="display: flex; gap: 2px;">
                                <input type="radio" name="rating" value="1" id="star1" style="display: none;" <c:if test="${review.rating.intValue() == 1}">checked</c:if>>
                                <label for="star1" style="cursor: pointer; font-size: 24px; color: #ddd;">★</label>
                                <input type="radio" name="rating" value="2" id="star2" style="display: none;" <c:if test="${review.rating.intValue() == 2}">checked</c:if>>
                                <label for="star2" style="cursor: pointer; font-size: 24px; color: #ddd;">★</label>
                                <input type="radio" name="rating" value="3" id="star3" style="display: none;" <c:if test="${review.rating.intValue() == 3}">checked</c:if>>
                                <label for="star3" style="cursor: pointer; font-size: 24px; color: #ddd;">★</label>
                                <input type="radio" name="rating" value="4" id="star4" style="display: none;" <c:if test="${review.rating.intValue() == 4}">checked</c:if>>
                                <label for="star4" style="cursor: pointer; font-size: 24px; color: #ddd;">★</label>
                                <input type="radio" name="rating" value="5" id="star5" style="display: none;" <c:if test="${review.rating.intValue() == 5}">checked</c:if>>
                                <label for="star5" style="cursor: pointer; font-size: 24px; color: #ddd;">★</label>
                            </div>
                            <span id="rating-text" style="margin-left: 8px; color: var(--text-2); font-size: 14px;">별점을 선택해주세요</span>
                        </div>
                    </div>


                    <!-- 리뷰 내용 -->
                    <div style="margin-bottom: 24px;">
                        <label for="content" style="display: block; margin-bottom: 8px; font-weight: 700; color: var(--text-1);">리뷰 내용</label>
                        <textarea id="content" name="content" placeholder="상품에 대한 솔직한 리뷰를 작성해주세요" required
                                  style="width: 100%; height: 120px; padding: 12px 16px; border: 1px solid rgba(0,0,0,0.1); border-radius: 8px; font-size: 14px; resize: vertical; font-family: inherit; background: var(--white);">${review.content}</textarea>
                    </div>

                    <!-- 사진 첨부 -->
                    <div style="margin-bottom: 32px;">
                        <label for="photos" style="display: block; margin-bottom: 8px; font-weight: 700; color: var(--text-1);">사진 첨부 (선택)</label>
                        <div style="border: 2px dashed rgba(255,122,162,0.3); border-radius: 8px; padding: 24px; text-align: center; background: linear-gradient(120deg, var(--pink-4), #fff);">
                            <input type="file" id="photos" name="photos" accept="image/*" style="display: none;">
                            <label for="photos" style="cursor: pointer; display: block;">
                                <div style="color: var(--pink-1); font-size: 32px; margin-bottom: 8px;">📷</div>
                                <p style="margin: 0; color: var(--text-2); font-size: 14px;">클릭하여 사진을 업로드하세요</p>
                                <p style="margin: 4px 0 0; color: var(--text-2); font-size: 12px;">한 장의 사진을 업로드하세요</p>
                            </label>
                        </div>
                        <div id="photo-preview" style="margin-top: 12px; display: flex; gap: 8px; flex-wrap: wrap;">
                            <!-- 기존 사진이 있을 때 미리보기 -->
                            <c:if test="${not empty review.photoUrl}">
                                <div style="width: 120px; height: 120px; border-radius: 8px; overflow: hidden; background-image: url(${review.photoUrl}); background-size: cover; background-position: center; position: relative; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                                    <button type="button" onclick="removeExistingPhoto()" style="position: absolute; top: 4px; right: 4px; width: 24px; height: 24px; border: none; border-radius: 50%; background: rgba(0,0,0,0.7); color: white; cursor: pointer; font-size: 14px; display: flex; align-items: center; justify-content: center;">×</button>
                                </div>
                            </c:if>
                        </div>
                    </div>

                    <!-- 버튼 -->
                    <div style="display: flex; gap: 12px; justify-content: flex-end;">
                        <button type="button" onclick="history.back()" class="btn btn-ghost">취소</button>
                        <button type="submit" class="btn btn-primary">${empty reviewId ? '리뷰 등록' : '리뷰 수정'}</button>
                    </div>
                </form>
            </div>

            <!-- 오른쪽: 리뷰 작성 가이드 -->
            <div>
                <!-- 리뷰 작성 팁 -->
                <div style="background: var(--white); border-radius: var(--radius); padding: 24px; box-shadow: var(--shadow); margin-bottom: 20px;">
                    <h3 style="margin: 0 0 16px; font-size: 18px; font-weight: 700; color: var(--text-1);">리뷰 작성 가이드</h3>
                    <div style="color: var(--text-2); font-size: 14px; line-height: 1.6;">
                        <p style="margin: 0 0 12px;">✨ <strong>솔직한 후기</strong>를 작성해 주세요</p>
                        <p style="margin: 0 0 12px;">📸 <strong>실제 사진</strong>을 첨부하면 더욱 도움이 됩니다</p>
                        <p style="margin: 0 0 12px;">💝 <strong>구체적인 장점</strong>과 아쉬운 점을 알려주세요</p>
                        <p style="margin: 0;">🎯 다른 고객들에게 <strong>도움이 되는 정보</strong>를 포함해 주세요</p>
                    </div>
                </div>

                <!-- 리뷰 혜택 -->
<%--                <div style="background: linear-gradient(135deg, var(--pink-3), var(--pink-4)); border-radius: var(--radius); padding: 24px; box-shadow: var(--shadow);">--%>
<%--                    <h3 style="margin: 0 0 16px; font-size: 18px; font-weight: 700; color: var(--text-1);">리뷰 작성 혜택</h3>--%>

<%--                    <div style="background: var(--white); border-radius: 8px; padding: 16px; margin-bottom: 12px;">--%>
<%--                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">--%>
<%--                            <span style="font-weight: 600; color: var(--text-1);">일반 리뷰</span>--%>
<%--                            <span style="background: var(--pink-1); color: var(--white); padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: 600;">100P</span>--%>
<%--                        </div>--%>
<%--                        <p style="margin: 0; color: var(--text-2); font-size: 13px;">텍스트 리뷰 작성 시</p>--%>
<%--                    </div>--%>

<%--                    <div style="background: var(--white); border-radius: 8px; padding: 16px;">--%>
<%--                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">--%>
<%--                            <span style="font-weight: 600; color: var(--text-1);">포토 리뷰</span>--%>
<%--                            <span style="background: var(--pink-1); color: var(--white); padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: 600;">500P</span>--%>
<%--                        </div>--%>
<%--                        <p style="margin: 0; color: var(--text-2); font-size: 13px;">사진 + 텍스트 리뷰 작성 시</p>--%>
<%--                    </div>--%>

<%--                    <div style="margin-top: 16px; padding: 12px; background: rgba(255,255,255,0.7); border-radius: 6px; text-align: center;">--%>
<%--                        <p style="margin: 0; color: var(--text-2); font-size: 12px;">--%>
<%--                            💡 포인트는 리뷰 승인 후 적립됩니다--%>
<%--                        </p>--%>
<%--                    </div>--%>
<%--                </div>--%>
            </div>
        </div>
    </div>
</main>

<script>
    // 별점 기능
    document.addEventListener('DOMContentLoaded', function() {
        const stars = document.querySelectorAll('.star-rating label');
        const ratingText = document.getElementById('rating-text');
        const ratings = ['별점을 선택해주세요', '별로예요', '그저그래요', '보통이에요', '좋아요', '최고예요'];

        // 초기 별점 설정 (수정 모드일 때)
        const checkedStar = document.querySelector('input[name="rating"]:checked');
        if (checkedStar) {
            const checkedIndex = parseInt(checkedStar.value) - 1;
            stars.forEach((s, i) => {
                if (i <= checkedIndex) {
                    s.style.color = 'var(--pink-1)';
                } else {
                    s.style.color = '#ddd';
                }
            });
            ratingText.textContent = ratings[checkedIndex + 1];
        }

        stars.forEach((star, index) => {
            star.addEventListener('click', function() {
                // 모든 별 초기화
                stars.forEach((s, i) => {
                    if (i <= index) {
                        s.style.color = 'var(--pink-1)';
                    } else {
                        s.style.color = '#ddd';
                    }
                });
                ratingText.textContent = ratings[index + 1];
            });

            star.addEventListener('mouseenter', function() {
                stars.forEach((s, i) => {
                    if (i <= index) {
                        s.style.color = 'var(--pink-2)';
                    } else {
                        s.style.color = '#ddd';
                    }
                });
            });
        });

        document.querySelector('.star-rating').addEventListener('mouseleave', function() {
            const checkedStar = document.querySelector('input[name="rating"]:checked');
            if (checkedStar) {
                const checkedIndex = parseInt(checkedStar.value) - 1;
                stars.forEach((s, i) => {
                    if (i <= checkedIndex) {
                        s.style.color = 'var(--pink-1)';
                    } else {
                        s.style.color = '#ddd';
                    }
                });
            } else {
                stars.forEach(s => s.style.color = '#ddd');
            }
        });
    });

    // 사진 미리보기 기능
    document.getElementById('photos').addEventListener('change', function(e) {
        const files = Array.from(e.target.files);
        const preview = document.getElementById('photo-preview');
        preview.innerHTML = '';

        if (files.length > 1) {
            alert('한 장의 사진만 업로드할 수 있습니다.');
            e.target.value = '';
            return;
        }

        const file = files[0]; // 첫 번째 파일만 처리
        if (file && file.type.startsWith('image/')) {
            const reader = new FileReader();
            reader.onload = function(e) {
                const img = document.createElement('div');
                img.style.cssText = `
                width: 120px; height: 120px; border-radius: 8px; overflow: hidden;
                background-image: url(${e.target.result}); background-size: cover; background-position: center;
                position: relative; box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            `;

                const deleteBtn = document.createElement('button');
                deleteBtn.innerHTML = '×';
                deleteBtn.style.cssText = `
                position: absolute; top: 4px; right: 4px; width: 24px; height: 24px;
                border: none; border-radius: 50%; background: rgba(0,0,0,0.7); color: white;
                cursor: pointer; font-size: 14px; display: flex; align-items: center; justify-content: center;
            `;
                deleteBtn.onclick = function() {
                    img.remove();
                    document.getElementById('photos').value = ''; // 파일 input 초기화
                };

                img.appendChild(deleteBtn);
                preview.appendChild(img);
            };
            reader.readAsDataURL(file);
        }
    });

    // 기존 사진 제거 기능
    function removeExistingPhoto() {
        const existingPhoto = document.querySelector('#photo-preview > div');
        if (existingPhoto) {
            existingPhoto.remove();
        }
        // hidden input을 추가하여 사진 삭제 표시
        const form = document.querySelector('form');
        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = 'removePhoto';
        hiddenInput.value = 'true';
        form.appendChild(hiddenInput);
    }
</script>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>