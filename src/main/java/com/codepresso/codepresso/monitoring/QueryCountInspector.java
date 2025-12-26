package com.codepresso.codepresso.monitoring;


import org.hibernate.resource.jdbc.spi.StatementInspector;

/**
 * Hibernate가 실행하는 모든 SQL을 가로채서 카운트하는 Inspector
 */
public class QueryCountInspector implements StatementInspector {

    @Override
    public String inspect(String sql) {
        // 현재 스레드의 RequestContext 가져오기
        RequestContext ctx = RequestContextHolder.getContext();

        if (ctx != null) {
            // 쿼리 카운트 증가
            ctx.incrementQueryCount(sql);
        }

        // 원본 SQL 그대로 반환
        return sql;
    }
}
