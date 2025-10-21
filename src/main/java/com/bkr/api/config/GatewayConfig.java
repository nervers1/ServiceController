package com.bkr.api.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class GatewayConfig {

    private static final String[] orderPaths = {"/api/v1/ord", "/api/v1/oms"};

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // ORD 및 OMS 경로 처리
                .route("order_route", r -> r
                        .predicate(exchange -> {
                            String path = exchange.getRequest().getURI().getPath();
                            return Arrays.stream(orderPaths)
                                    .anyMatch(path::startsWith);
                        })
                        .uri("http://orderService"))

                // 그 외 /api/v1/** 경로 처리
                .route("default_route", r -> r
                        .predicate(exchange -> {
                            String path = exchange.getRequest().getURI().getPath();
                            return path.startsWith("/api/v1/")
                                    && Arrays.stream(orderPaths)
                                    .noneMatch(path::startsWith);
                        })
                        .uri("http://defaultService"))

                .build();
    }

}
