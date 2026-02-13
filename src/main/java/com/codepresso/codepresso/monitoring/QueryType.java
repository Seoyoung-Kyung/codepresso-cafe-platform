package com.codepresso.codepresso.monitoring;

public enum QueryType {
    SELECT,
    INSERT,
    UPDATE,
    DELETE,
    OTHER;

    public static QueryType from(String sql) {
        if (sql == null || sql.isBlank()) {
            return OTHER;
        }
        String trimmed = sql.trim().toUpperCase();
        if (trimmed.startsWith("SELECT")) return SELECT;
        if (trimmed.startsWith("INSERT")) return INSERT;
        if (trimmed.startsWith("UPDATE")) return UPDATE;
        if (trimmed.startsWith("DELETE")) return DELETE;
        return OTHER;
    }
}