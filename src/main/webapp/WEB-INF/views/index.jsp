<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" session="false" %>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body class="home-page">
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<style>
    .home-page .hero {
        background: transparent;
    }

    .home-page .hero-card {
        background: #fff;
        border-radius: 28px;
        border: 1px solid rgba(255,122,162,0.18);
        box-shadow: 0 32px 60px rgba(15,23,42,0.12);
    }

    .home-page .hero-card .mockup {
        background: #fff;
        box-shadow: inset 0 0 0 1px rgba(255,122,162,0.12);
    }

    .home-page .hero-card .btn.btn-ghost {
        border: 1px solid rgba(255,122,162,0.6);
        color: var(--pink-1);
    }

    .home-page .hero-card .btn.btn-ghost:hover {
        border-color: var(--pink-1);
        color: var(--pink-1);
        background: rgba(255,122,162,0.08);
    }

    .home-page .cup-label {
        background: linear-gradient(135deg, var(--pink-1), var(--pink-2));
    }
</style>
<%@ include file="/WEB-INF/views/home/main.jspf" %>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
