package com.codepresso.codepresso.monitoring;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP 요청별 쿼리 실행 정보를 저장하는 컨텍스트
 */
@Getter
@Slf4j
public class RequestContext {
    private String httpMethod;
    private String bestMatchPath;
    private final Map<QueryType, Integer> queryCountByType = new HashMap<>();

    @Builder
    public RequestContext(String httpMethod, String bestMatchPath) {
        this.httpMethod = httpMethod;
        this.bestMatchPath = bestMatchPath;
    }

    /**
     * SQL 쿼리가 실행될 때마다 호출되어 카운트 증가
     */
    public void incrementQueryCount(String sql) {
        QueryType queryType = QueryType.from(sql);
        queryCountByType.merge(queryType, 1, Integer::sum);
    }

    public void clearQueryCount() {
        // 쿼리 카운트만 초기화 (컨텍스트는 유지)
        queryCountByType.clear();
        log.debug("Query count cleared for {} {}", httpMethod, bestMatchPath);
    }
}