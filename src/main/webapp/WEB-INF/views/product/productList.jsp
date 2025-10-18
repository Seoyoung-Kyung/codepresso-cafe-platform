<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<style>
    @import url('${pageContext.request.contextPath}/css/productList.css');
</style>
<body class="product-list-page">
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<main class="product-page-main product-list-main">
    <%@ include file="/WEB-INF/views/product/product-category-nav.jspf" %>

    <div class="container" id="products-container">
        <!-- 상품 목록이 JavaScript로 동적으로 로드됩니다 -->
        <div class="loading-spinner" style="text-align: center; padding: 100px 20px;">
            <p>상품 목록을 불러오는 중...</p>
        </div>
    </div>

    <!-- 상품 데이터 변환 -->
    <script type="text/javascript">
        const contextPath = '${pageContext.request.contextPath}';

        // 서버에서 전달받은 상품 데이터를 JavaScript 배열로 변환
        const products = [
            <c:forEach items="${products}" var="product" varStatus="status">
            {
                productId: ${product.productId},
                productName: '${fn:escapeXml(product.productName)}',
                productPhoto: '${product.productPhoto}',
                price: ${product.price},
                categoryCode: '${product.categoryCode}'
            }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];
    </script>

    <!-- 상품 목록 로드 및 렌더링 스크립트 -->
    <script src="${pageContext.request.contextPath}/js/productList.js"></script>

</main>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>