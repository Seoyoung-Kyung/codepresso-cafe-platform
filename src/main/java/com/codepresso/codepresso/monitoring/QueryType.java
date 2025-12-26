package com.codepresso.codepresso.monitoring;

public enum QueryType {
    SELECT,
    INSERT,
    UPDATE,
    DELETE,
    UNKNOWN;

    public static QueryType from(String sql) {
        if (sql == null || sql.isEmpty()) {
            return UNKNOWN;
        }

        // SQL 정규화: 앞뒤 공백 제거, 대문자 변환
        String normalizedSql = sql.trim().toUpperCase();

        // 주석 제거 (/* */ 형태)
        normalizedSql = normalizedSql.replaceAll("/\\*.*?\\*/", "").trim();

        // 쿼리 타입 판단
        if (normalizedSql.startsWith("SELECT") || normalizedSql.startsWith("WITH")) {
            return SELECT;
        } else if (normalizedSql.startsWith("INSERT")) {
            return INSERT;
        } else if (normalizedSql.startsWith("UPDATE")) {
            return UPDATE;
        } else if (normalizedSql.startsWith("DELETE")) {
            return DELETE;
        }

        return UNKNOWN;
    }
}