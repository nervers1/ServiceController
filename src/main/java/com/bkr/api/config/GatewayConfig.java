package com.bkr.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@Slf4j
public class GatewayConfig {

    private final CustomGatewayProperties customGatewayProperties;

    public GatewayConfig(CustomGatewayProperties customGatewayProperties) {
        this.customGatewayProperties = customGatewayProperties;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        String[] orderPaths = customGatewayProperties.getOrderPaths().toArray(new String[0]);
        return builder.routes()

                // ORD 및 OMS 경로 처리
                .route("order_route", r -> r
                        .predicate(exchange -> {
                            String path = exchange.getRequest().getURI().getPath();
                            return Arrays.stream(orderPaths)
                                    .anyMatch(path::startsWith);
                        })
                        /*.filters(f -> f.filter((exchange, chain) -> {
                            long startTime = System.currentTimeMillis();
                            log.info("==> [9001] 요청 시작: {} {}", 
                                    exchange.getRequest().getMethod(), 
                                    exchange.getRequest().getURI().getPath());
                            
                            return chain.filter(exchange).then(
                                    reactor.core.publisher.Mono.fromRunnable(() -> {
                                        long endTime = System.currentTimeMillis();
                                        long executionTime = endTime - startTime;
                                        log.info("<== [9001] 응답 완료: {} {} | 소요시간: {}ms", 
                                                exchange.getRequest().getMethod(), 
                                                exchange.getRequest().getURI().getPath(),
                                                executionTime);
                                    })
                            );
                        }))*/
                        .uri("http://localhost:9001"))

                // 그 외 /api/v1/** 경로 처리
                .route("default_route", r -> r
                        .predicate(exchange -> {
                            String path = exchange.getRequest().getURI().getPath();
                            return path.startsWith("/api/v1/")
                                    && Arrays.stream(orderPaths)
                                    .noneMatch(path::startsWith);
                        })
                        /*.filters(f -> f.filter((exchange, chain) -> {
                            long startTime = System.currentTimeMillis();
                            log.info("==> [9002] 요청 시작: {} {}", 
                                    exchange.getRequest().getMethod(), 
                                    exchange.getRequest().getURI().getPath());
                            
                            return chain.filter(exchange).then(
                                    reactor.core.publisher.Mono.fromRunnable(() -> {
                                        long endTime = System.currentTimeMillis();
                                        long executionTime = endTime - startTime;
                                        log.info("<== [9002] 응답 완료: {} {} | 소요시간: {}ms", 
                                                exchange.getRequest().getMethod(), 
                                                exchange.getRequest().getURI().getPath(),
                                                executionTime);
                                    })
                            );
                        }))*/
                        .uri("http://localhost:9002"))

                .build();
    }

}
