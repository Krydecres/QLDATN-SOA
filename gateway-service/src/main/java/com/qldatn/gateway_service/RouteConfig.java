package com.qldatn.gateway_service;

import com.qldatn.gateway_service.filter.AuthenticationFilter;
import com.qldatn.gateway_service.filter.IpRateLimiterFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    private final AuthenticationFilter authenticationFilter;
    private final IpRateLimiterFilter ipRateLimiterFilter;

    public RouteConfig(AuthenticationFilter authenticationFilter, IpRateLimiterFilter ipRateLimiterFilter) {
        this.authenticationFilter = authenticationFilter;
        this.ipRateLimiterFilter = ipRateLimiterFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // Route 1: Auth Service (Kèm bảo mật AuthFilter và RateLimit bằng ConcurrentHashMap)
            .route("auth-service", r -> r.path("/api/auth/**")
                .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                               .filter(ipRateLimiterFilter.apply(new IpRateLimiterFilter.Config())))
                .uri("lb://AUTH-SERVICE"))

            // Route 2: De Tai Service
            .route("detai-service", r -> r.path("/api/detai/**")
                .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                               .filter(ipRateLimiterFilter.apply(new IpRateLimiterFilter.Config())))
                .uri("lb://DETAI-SERVICE"))

            // Route 3: Dang Ky Service
            .route("dangky-service", r -> r.path("/api/dangky/**")
                .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                               .filter(ipRateLimiterFilter.apply(new IpRateLimiterFilter.Config())))
                .uri("lb://DANGKY-SERVICE"))
            .build();
    }
}