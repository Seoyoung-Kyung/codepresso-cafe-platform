<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<jsp:useBean id="branches" scope="request" type="java.util.List"/>
<c:forEach var="b" items="${branches}">
    <article class="branch-card" 
             data-branch-id="${b.id}"
             data-lat="${b.latitude}"
             data-lng="${b.longitude}"
             data-name="${fn:escapeXml(b.branchName)}"
             data-address="${fn:escapeXml(b.address)}"
             data-opening-time="${fn:escapeXml(b.openingTime)}"
             data-closing-time="${fn:escapeXml(b.closingTime)}">
        <div class="branch-cover" aria-label="매장 이미지">
            <c:choose>
                <c:when test="${not empty b.photoUrl}">
                    <img src="${b.photoUrl}"
                         alt="${b.branchName}"
                         onerror="this.src='/banners/mascot.png'; this.onerror=null;" loading="lazy" />
                </c:when>
                <c:otherwise>
                    <picture>
                        <source srcset="/banners/mascot-small.webp" type="image/webp">
                        <img src="/banners/mascot.png" alt="CodePress Mascot" loading="lazy" />
                    </picture>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="branch-body">
            <div class="branch-header">
                <div class="branch-name">${b.branchName}</div>
                <div class="branch-distance" aria-label="내 위치와의 거리"></div>
            </div>
            <div class="branch-meta">
                <span class="icon" aria-hidden="true">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="2"/>
                        <path d="M12 7v5l3 2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                </span>
                <span>${b.openingTime} ~ ${b.closingTime}</span>
            </div>
            <div class="branch-meta">
                <span class="icon" aria-hidden="true">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M12 22s7-6.4 7-12a7 7 0 1 0-14 0c0 5.6 7 12 7 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        <circle cx="12" cy="10" r="2.5" stroke="currentColor" stroke-width="2"/>
                    </svg>
                </span>
                <span class="branch-address">${b.address}</span>
            </div>
            <div>
                <span class="badge-open" data-open="${b.openingTime}" data-close="${b.closingTime}"></span>
            </div>
        </div>
    </article>
</c:forEach>
