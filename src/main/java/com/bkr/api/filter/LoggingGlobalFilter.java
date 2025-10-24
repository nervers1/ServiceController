package com.bkr.api.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    private static final String REQUEST_TIME_ATTR = "requestTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 요청 받은 시간 기록
        long startTime = System.currentTimeMillis();
        String uri = exchange.getRequest().getURI().toString();
        exchange.getAttributes().put(REQUEST_TIME_ATTR, startTime);

        /*String formattedDateTime = getLocalDateTime(startTime);

        String method = exchange.getRequest().getMethod().toString();*/
        
//        log.info("==> 요청 시작: {} {} at {}", method, uri, formattedDateTime);
        log.info(">> {} :: begin", uri);

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    // 응답 완료 시간 기록
                    Long requestTime = exchange.getAttribute(REQUEST_TIME_ATTR);
                    if (requestTime != null) {
                        /*long endTime = System.currentTimeMillis();*/
                        /*long executionTime = endTime - requestTime;*/
                        
                        // 백엔드 URI 가져오기 (URI 객체로 받아서 String으로 변환)
                        URI targetUri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
                        String targetUriStr = targetUri != null ? targetUri.toString() : "unknown";
                        
                        /*int statusCode = exchange.getResponse().getStatusCode() != null
                                ? exchange.getResponse().getStatusCode().value() 
                                : 0;*/
                        
                        /*log.info("<== 응답 완료: {} {} -> 백엔드: {} | 상태코드: {} | 소요시간: {}ms",
                                method, uri, targetUriStr, statusCode, executionTime);*/
                        log.info("<< {} :: done. [{}]", uri, targetUriStr);
                    }
                }));
    }

    /*private static String getLocalDateTime(long startTime) {
        // Convert milliseconds to LocalDateTime
        LocalDateTime localDateTime = Instant.ofEpochMilli(startTime)
                .atZone(ZoneId.systemDefault()) // Use system's default timezone
                .toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return localDateTime.format(formatter);
    }*/

    @Override
    public int getOrder() {
        // 가장 먼저 실행되도록 설정
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
