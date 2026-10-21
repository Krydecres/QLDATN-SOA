package com.qldatn.gateway_service.filter;

import com.qldatn.gateway_service.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JwtUtil jwtUtil;

    // Các đường dẫn public hoàn toàn (mọi HTTP Method)
    private static final List<String> PUBLIC_PATH_PREFIXES = List.of(
            "/api/auth/login",
            "/v3/api-docs",
            "/swagger-ui"
    );

    public AuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            HttpMethod method = request.getMethod();

            // Bỏ qua xác thực cho các endpoint public
            if (isPublicEndpoint(path, method)) {
                return chain.filter(exchange);
            }

            // Kiểm tra sự tồn tại của header Authorization
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Thiếu Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Header Authorization không hợp lệ", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return onError(exchange, "Token không hợp lệ hoặc đã hết hạn!", HttpStatus.UNAUTHORIZED);
            }

            // Parse token và lấy thông tin User
            Claims claims = jwtUtil.extractClaims(token);
            String username = claims.getSubject();
            String role = claims.get("role", String.class);
            String maSV = claims.get("maSV", String.class);

            // Forward thông tin qua Header bổ sung cho các Service phía sau
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Name", username != null ? username : "")
                    .header("X-User-Role", role != null ? role : "")
                    .header("X-User-MaSV", maSV != null ? maSV : "")
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        };
    }

    private boolean isPublicEndpoint(String path, HttpMethod method) {
        // 1. Cho phép các đường dẫn auth/login, swagger docs
        if (PUBLIC_PATH_PREFIXES.stream().anyMatch(path::contains)) {
            return true;
        }

        // 2. Cho phép xem danh sách (GET) đề tài và đăng ký công khai
        if (HttpMethod.GET.equals(method) && (path.startsWith("/api/detai") || path.startsWith("/api/dangky"))) {
            return true;
        }

        return false;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }
}