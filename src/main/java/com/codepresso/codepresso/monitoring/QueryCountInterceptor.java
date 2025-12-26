package com.codepresso.codepresso.monitoring;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueryCountInterceptor implements HandlerInterceptor {

    public static final String UNKNOWN_PATH = "UNKNOWN_PATH";
    private static final ThreadLocal<Long> startTimeHolder = new ThreadLocal<>();

    private final MeterRegistry meterRegistry;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 시작 시간 기록
        startTimeHolder.set(System.currentTimeMillis());

        String httpMethod = request.getMethod();
        String bestMatchPath = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (bestMatchPath == null) {
            bestMatchPath = UNKNOWN_PATH;
        }

        RequestContext ctx = RequestContext.builder()
                .httpMethod(httpMethod)
                .bestMatchPath(bestMatchPath)
                .build();

        RequestContextHolder.initContext(ctx);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        RequestContext ctx = RequestContextHolder.getContext();

        // 실행 시간 계산
        Long startTime = startTimeHolder.get();
        long executionTime = 0;
        if (startTime != null) {
            executionTime = System.currentTimeMillis() - startTime;
            startTimeHolder.remove(); // ThreadLocal 정리
        }

        if (ctx != null) {
            Map<QueryType, Integer> queryCountByType = ctx.getQueryCountByType();

            // 로그 출력 (쿼리 개수 + 실행 시간)
            if (!queryCountByType.isEmpty()) {
                log.info("API: {} {}, Queries: {}, 실행 시간: {}ms",
                        ctx.getHttpMethod(),
                        ctx.getBestMatchPath(),
                        queryCountByType,
                        executionTime);
            }

            // Prometheus 메트릭 기록
            queryCountByType.forEach((queryType, count) ->
                    recordQueryMetric(ctx, queryType, count));

            // 실행 시간 메트릭 기록
            recordExecutionTimeMetric(ctx, executionTime);
        }
    }

    /**
     * 쿼리 개수 메트릭 기록
     */
    private void recordQueryMetric(RequestContext ctx, QueryType queryType, Integer count) {
        DistributionSummary summary = DistributionSummary.builder("app.query.per_request")
                .description("Number of SQL queries per request")
                .tag("path", ctx.getBestMatchPath())
                .tag("http_method", ctx.getHttpMethod())
                .tag("query_type", queryType.name())
                .publishPercentiles(0.5, 0.95)
                .register(meterRegistry);

        summary.record(count);
    }

    /**
     * 실행 시간 메트릭 기록
     */
    private void recordExecutionTimeMetric(RequestContext ctx, long executionTimeMs) {
        DistributionSummary summary = DistributionSummary.builder("app.execution.time")
                .description("API execution time in milliseconds")
                .tag("path", ctx.getBestMatchPath())
                .tag("http_method", ctx.getHttpMethod())
                .baseUnit("milliseconds")
                .publishPercentiles(0.5, 0.95, 0.99)  // 50%, 95%, 99% 백분위수
                .register(meterRegistry);

        summary.record(executionTimeMs);
    }

    public void clearQueryCount() {
        RequestContext ctx = RequestContextHolder.getContext();
        if (ctx != null) {
            ctx.clearQueryCount();
        }
    }

    public Map<QueryType, Integer> getQueryCount() {
        RequestContext ctx = RequestContextHolder.getContext();
        if (ctx != null) {
            return ctx.getQueryCountByType();
        }
        return Map.of();
    }
}