package com.codepresso.codepresso.monitoring;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class RequestContext {
    private String httpMethod;
    private String bestMatchPath;
    private final Map<QueryType, Integer> queryCountByType = new HashMap<>();
    private final Map<String, Integer> queryCountByTable = new HashMap<>();

    private static final Pattern FROM_PATTERN = Pattern.compile("\\bFROM\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern INSERT_PATTERN = Pattern.compile("\\bINTO\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern UPDATE_PATTERN = Pattern.compile("\\bUPDATE\\s+(\\w+)", Pattern.CASE_INSENSITIVE);

    @Builder
    public RequestContext(String httpMethod, String bestMatchPath) {
        this.httpMethod = httpMethod;
        this.bestMatchPath = bestMatchPath;
    }

    public void incrementQueryCount(String sql) {
        QueryType queryType = QueryType.from(sql);
        queryCountByType.merge(queryType, 1, Integer::sum);

        String table = extractTableName(sql);
        if (table != null) {
            queryCountByTable.merge(table, 1, Integer::sum);
        }
    }

    private String extractTableName(String sql) {
        if (sql == null || sql.isBlank()) {
            return null;
        }
        String trimmed = sql.trim().toUpperCase();
        Matcher matcher;

        if (trimmed.startsWith("INSERT")) {
            matcher = INSERT_PATTERN.matcher(sql);
        } else if (trimmed.startsWith("UPDATE")) {
            matcher = UPDATE_PATTERN.matcher(sql);
        } else {
            matcher = FROM_PATTERN.matcher(sql);
        }

        if (matcher.find()) {
            return matcher.group(1).toLowerCase();
        }
        return null;
    }
}