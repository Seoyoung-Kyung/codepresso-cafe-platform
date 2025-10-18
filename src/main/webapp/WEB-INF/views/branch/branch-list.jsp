<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<body>
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<main class="hero branch-page">
    <div class="container">
        <!--
          매장 선택 화면 (하드코딩 데이터 3개)
          - 카드형식, 분홍 테마 유지
          - 이미지: 마스코트 이미지 재사용 (/banners/mascot.png)

          학습 포인트
          - 지금은 서버 API 없이 정적인 카드로 구성합니다.
          - 나중에 DB/서비스 연동 시, 같은 레이아웃 안에서 목록을 서버에서 주입하면 됩니다.
        -->
        <section class="branch-hero" style="display:flex; align-items:end; justify-content:space-between; gap:12px; flex-wrap:wrap;">
            <h1 style="margin:0;">가까운 CodePress 매장을 선택하세요</h1>
            <form method="get" action="/branch/list" class="search" style="display:flex; gap:8px; align-items:center;">
                <input type="text" name="q" value="${q}" placeholder="매장명 검색" style="height:40px; padding:0 12px; border-radius: 8px; border:1px solid rgba(0,0,0,0.12);" />
                <button type="submit" class="icon-btn" aria-label="검색">
                    <!-- 돋보기 아이콘 (SVG) -->
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                        <circle cx="11" cy="11" r="7" stroke="currentColor" stroke-width="2"/>
                        <line x1="20" y1="20" x2="16.65" y2="16.65" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    </svg>
                </button>
                <c:if test="${not empty lat}">
                    <input type="hidden" name="lat" value="${lat}" />
                </c:if>
                <c:if test="${not empty lng}">
                    <input type="hidden" name="lng" value="${lng}" />
                </c:if>
                <c:if test="${not empty radius}">
                    <input type="hidden" name="radius" value="${radius}" />
                </c:if>
            </form>
        </section>

        <style>
            /* 이 페이지는 상단 여백을 줄입니다 */
            .hero.branch-page { padding-top: 40px; }
            /* 매장 목록 페이지에서는 헤더의 모든 네비게이션 버튼 숨김 */
            .header-branch-select { display: none !important; }
            .search-icon { display: none !important; }
            .nav a[href="/boards/list"] { display: none !important; }
            .nav a[href="/member/mypage"] { display: none !important; }
            .nav .cart-link { display: none !important; }
            /* 히어로 섹션과 카드 그리드 사이 여백 */
            .branch-hero { margin-bottom: 24px; }
            /* 레이아웃: 카드 그리드 */
            .branch-grid {
                display: grid;
                grid-template-columns: repeat(3, 1fr);
                gap: 16px;
            }
            @media (max-width: 900px) {
                .branch-grid { grid-template-columns: 1fr; }
            }

            /* 카드 */
            .branch-card {
                background: var(--white);
                border-radius: 18px;
                box-shadow: var(--shadow);
                overflow: hidden;
                display: grid;
                grid-template-rows: 160px auto;
                cursor: pointer;
                transition: transform .12s ease, box-shadow .2s ease;
                will-change: transform;
            }
            .branch-card:hover { transform: translateY(-3px); box-shadow: 0 14px 34px rgba(0,0,0,0.12); }
            .branch-card:active { transform: translateY(-1px); box-shadow: 0 10px 24px rgba(0,0,0,0.10); }
            /* 이미지 고정: 호버 시 이미지 확대/이동 없음 */
            .branch-cover {
                background: linear-gradient(135deg, var(--pink-3), var(--pink-4));
                position: relative;
            }
            .branch-cover img { width: 100%; height: 100%; object-fit: cover; display: block; }
            .branch-body { padding: 16px; display: grid; gap: 8px; }
            .branch-header { display:flex; justify-content: space-between; align-items: baseline; gap: 8px; }
            .branch-name { font-size: 18px; font-weight: 800; }
            .branch-meta { color: var(--text-2); font-size: 14px; display:flex; align-items:center; gap:6px; }
            .branch-meta .icon { display:inline-grid; place-items:center; width:16px; height:16px; color: var(--text-2); }
            .branch-address { overflow:hidden; text-overflow: ellipsis; display:-webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
            .branch-distance { color: var(--pink-1); font-size: 14px; font-weight: 800; white-space: nowrap; }
            .badge-open {
                display: inline-block; padding: 4px 10px; border-radius: 999px;
                background: var(--pink-4); color: var(--pink-1); font-weight: 800; font-size: 12px;
            }
            .card-actions { display: flex; justify-content: flex-end; }

            /* 텍스트 링크 스타일 (톤 다운) */
            .text-link { color: var(--text-2); text-decoration: none; font-weight: 600; }
            .text-link:hover { text-decoration: underline; }
            .text-muted { color: var(--text-2); }

            /* 검색 아이콘 버튼: 아이콘만 표시 */
            .search .icon-btn {
                appearance: none;
                background: transparent;
                border: none;
                padding: 0;
                margin: 0;
                display: inline-grid; place-items: center;
                cursor: pointer;
                color: var(--text-2);
                line-height: 0; /* 버튼 높이로 인한 여백 제거 */
                transition: color .15s ease;
            }
            .search .icon-btn svg { display: block; }
            .search .icon-btn:hover { color: var(--pink-1); }
            .search .icon-btn:active { transform: translateY(1px); }
            .search .icon-btn:focus-visible { outline: 2px solid rgba(255,122,162,0.55); outline-offset: 2px; border-radius: 6px; }
        </style>

        <c:choose>
            <c:when test="${not empty branches}">
                <div id="branchGrid" class="branch-grid">
                    <jsp:include page="/WEB-INF/views/branch/branch-cards.jsp" />
                </div>
                <div class="cta" style="justify-content:center; margin-top: 16px; gap: 12px;">
                    <a id="loadMoreLink" href="#" class="text-link">더 보기</a>
                    <span id="moreSep" class="text-muted">|</span>
                    <a id="scrollTopLink" href="#" class="text-link">맨위로 이동</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state" style="text-align:center; padding:40px 16px; color:var(--text-2);">
                    <h3>등록된 매장이 없습니다</h3>
                    <p>관리자 페이지에서 매장을 먼저 등록해주세요.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<script>
  // 거리 계산 유틸 (하버사인)
  function distKm(lat1, lng1, lat2, lng2) {
    var R = 6371; // km
    var toRad = function(d){ return d * Math.PI / 180; };
    var dLat = toRad(lat2 - lat1);
    var dLng = toRad(lng2 - lng1);
    var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
            Math.sin(dLng/2) * Math.sin(dLng/2);
    var c = 2 * Math.asin(Math.sqrt(a));
    return R * c;
  }

  function formatDistance(km){
    if (!isFinite(km)) return '';
    var m = Math.round(km * 1000);
    if (m < 1000) return m + 'm';
    return (km).toFixed(1) + 'km';
  }

  function renderDistances(userLat, userLng, root){
    if (userLat == null || userLng == null) return;
    var scope = root || document;
    var cards = scope.querySelectorAll('.branch-card');
    cards.forEach(function(card){
      var lat = parseFloat(card.getAttribute('data-lat'));
      var lng = parseFloat(card.getAttribute('data-lng'));
      if (!isFinite(lat) || !isFinite(lng)) return;
      var km = distKm(userLat, userLng, lat, lng);
      var el = card.querySelector('.branch-distance');
      if (el) el.textContent = formatDistance(km);
    });
  }

  // 영업중/종료 표시 계산
  function minutesFromHm(str){
    if (!str) return null;
    var s = String(str);
    var parts = s.split(':');
    var h = parseInt(parts[0], 10);
    var m = parseInt(parts[1] || '0', 10);
    if (!isFinite(h) || !isFinite(m)) return null;
    return h * 60 + m;
  }
  function isOpenNow(openStr, closeStr, now){
    var o = minutesFromHm(openStr);
    var c = minutesFromHm(closeStr);
    if (o == null || c == null) return false;
    if (!now) now = new Date();
    var n = now.getHours() * 60 + now.getMinutes();
    if (o === c) return true; // 24시간으로 간주
    if (c > o) return n >= o && n < c; // 같은날 마감
    return n >= o || n < c; // 자정 넘김
  }
  function renderOpenBadges(root){
    var scope = root || document;
    scope.querySelectorAll('.badge-open').forEach(function(el){
      var open = el.getAttribute('data-open');
      var close = el.getAttribute('data-close');
      if (!open || !close) {
        // 영업시간 정보가 없으면 숨기지 않음(데이터 정비 전 과도기 처리)
        el.textContent = '';
        return;
      }
      var openNow = isOpenNow(open, close);
      el.textContent = openNow ? '주문 가능' : '영업 종료';
      if (!openNow) {
        var card = el.closest('.branch-card');
        if (card) card.style.display = 'none';
      }
    });
  }

  (function(){
    var nextPage = ${hasNext ? nextPage : -1};
    var pageSize = ${pageSize};
    var grid = document.getElementById('branchGrid');
    var more = document.getElementById('loadMoreLink');
    var sep = document.getElementById('moreSep');
    var toTop = document.getElementById('scrollTopLink');
    var params = new URLSearchParams(location.search);
    var q = params.get('q');
    var lat = params.get('lat');
    var lng = params.get('lng');
    var radius = params.get('radius');

    // 검색어가 비워질 때 자동 전환: 위치가 있으면 근처 매장, 없으면 전체 목록
    var searchInput = document.querySelector('.search input[name="q"]');
    if (searchInput) {
      var hadText = (q && q.trim().length > 0) || searchInput.value.trim().length > 0;
      var debounceId = null;
      searchInput.addEventListener('input', function(){
        var val = searchInput.value.trim();
        if (debounceId) clearTimeout(debounceId);
        debounceId = setTimeout(function(){
          if (val === '' && hadText) {
            try {
              var url = new URL(location.href);
              url.searchParams.delete('q'); // 검색어 제거
              // lat/lng/radius는 그대로 두어: 있으면 근처, 없으면 전체
              location.replace(url.toString());
            } catch(e) {}
          }
          hadText = val.length > 0;
        }, 300);
      });
    }
    if (grid) {
      grid.addEventListener('click', function(e){
        var card = e.target.closest('.branch-card');
        if (!card || !grid.contains(card)) return;
        var normalizeTime = function(value) {
          if (value == null) return '';
          var str = String(value).trim();
          if (str === '' || str.toLowerCase() === 'null' || str.toLowerCase() === 'undefined') {
            return '';
          }
          var parts = str.split(':');
          if (parts.length >= 2) {
            return parts[0].padStart(2, '0') + ':' + parts[1].padStart(2, '0');
          }
          return str;
        };

        var rawOpening = card.getAttribute('data-opening-time');
        var rawClosing = card.getAttribute('data-closing-time');

        var selection = {
          id: card.getAttribute('data-branch-id'),
          name: card.getAttribute('data-name') || (card.querySelector('.branch-name') ? card.querySelector('.branch-name').textContent.trim() : ''),
          address: card.getAttribute('data-address') || '',
          openingTime: normalizeTime(rawOpening),
          closingTime: normalizeTime(rawClosing)
        };

        if (window.branchSelection && typeof window.branchSelection.save === 'function') {
          window.branchSelection.save(selection);
        }

        window.location.href = '/products';
      });
    }
    if (lat && lng) {
      renderDistances(parseFloat(lat), parseFloat(lng));
    }
    renderOpenBadges();
    if (toTop) {
      toTop.addEventListener('click', function(e){ e.preventDefault(); window.scrollTo({ top: 0, behavior: 'smooth' }); });
    }
    if (!more) return; // 빈 상태
    if (nextPage === -1) {
      more.style.display = 'none';
      if (sep) sep.style.display = 'none';
    }
    more.addEventListener('click', async function(e){
      e.preventDefault();
      try {
        more.style.pointerEvents = 'none';
        var url = new URL('/branch/page', location.origin);
        url.searchParams.set('page', nextPage);
        url.searchParams.set('size', pageSize);
        if (q) url.searchParams.set('q', q);
        if (!q && lat && lng) { // 위치 기반은 검색이 없을 때만
          url.searchParams.set('lat', lat);
          url.searchParams.set('lng', lng);
          if (radius) url.searchParams.set('radius', radius);
        }
        var res = await fetch(url, { headers: { 'X-Requested-With': 'XMLHttpRequest' }});
        if (!res.ok) { more.style.pointerEvents = 'auto'; return; }
        var html = await res.text();
        if (html && html.trim().length > 0) {
          var temp = document.createElement('div');
          temp.innerHTML = html;
          // 거리/영업 상태 계산을 위해 임시 DOM에서 먼저 렌더링
          if (lat && lng) {
            renderDistances(parseFloat(lat), parseFloat(lng), temp);
          }
          renderOpenBadges(temp);
          grid.insertAdjacentHTML('beforeend', temp.innerHTML);
        }
        var hasNext = res.headers.get('X-Has-Next') === 'true';
        if (hasNext) {
          nextPage = nextPage + 1;
          more.style.pointerEvents = 'auto';
        } else {
          more.style.display = 'none';
          if (sep) sep.style.display = 'none';
        }
      } catch(e) {
        more.style.pointerEvents = 'auto';
      }
    });
  })();

  // 최초 진입 시(q가 없고 lat/lng가 없을 때), 위치 권한 요청 후 근처 매장만 보기
  (function(){
    var params = new URLSearchParams(location.search);
    if (params.get('q')) return; // 검색 중에는 위치 사용 안 함
    if (params.get('lat') || params.get('lng')) return; // 이미 위치 있음
    if (!('geolocation' in navigator)) return;
    navigator.geolocation.getCurrentPosition(function(pos){
      try {
        var url = new URL(location.href);
        url.searchParams.set('lat', pos.coords.latitude);
        url.searchParams.set('lng', pos.coords.longitude);
        url.searchParams.set('radius', 2);
        location.replace(url.toString());
      } catch(e) {}
    }, function(){ /* 사용자가 거부하면 전체 목록 유지 */ }, { enableHighAccuracy: true, timeout: 5000 });
  })();
  
  // 로그인 이후 잘못된 페이지 접근으로 리다이렉트된 경우(예: / 또는 /auth/login) 안내 알림
  (function(){
    var p = new URLSearchParams(location.search);
    // if (p.get('blocked') === '1') {
    //   alert('이미 로그인되어 있어 접근할 수 없는 페이지입니다. 매장을 선택해주세요.');
    // }
  })();
</script>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
