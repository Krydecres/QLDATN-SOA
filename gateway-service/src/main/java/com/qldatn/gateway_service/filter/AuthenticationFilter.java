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

            // 1. Cho phép các endpoint public đi qua
            if (isPublicEndpoint(path, method)) {
                return chain.filter(exchange);
            }

            // 2. Xác thực (Authentication): Kiểm tra Token
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

            // Parse thông tin từ Token
            Claims claims = jwtUtil.extractClaims(token);
            String username = claims.getSubject();
            String role = claims.get("role", String.class);
            String maSV = claims.get("maSV", String.class);

            // 3. Phân quyền tập trung (Authorization - Security Matrix)
            if (!isAuthorized(path, method, role)) {
                return onError(exchange, "Bạn không có quyền thực hiện thao tác này!", HttpStatus.FORBIDDEN);
            }

            // 4. Forward thông tin qua Header cho các service con
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Name", username != null ? username : "")
                    .header("X-User-Role", role != null ? role : "")
                    .header("X-User-MaSV", maSV != null ? maSV : "")
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        };
    }

    private boolean isPublicEndpoint(String path, HttpMethod method) {
        if (PUBLIC_PATH_PREFIXES.stream().anyMatch(path::contains)) {
            return true;
        }
        // Công khai việc xem danh sách (GET) đề tài và đăng ký
        if (HttpMethod.GET.equals(method) && (path.startsWith("/api/detai") || path.startsWith("/api/dangky"))) {
            return true;
        }
        return false;
    }

    /**
     * Ma trận Phân quyền tập trung ngay tại Gateway
     */
    private boolean isAuthorized(String path, HttpMethod method, String role) {
        boolean isStaff = "GIANGVIEN".equals(role) || "ADMIN".equals(role);

        // Quy tắc 1: Xem danh sách tất cả sinh viên (/api/auth/sinhvien) chỉ cho GIANGVIEN/ADMIN
        if (path.equals("/api/auth/sinhvien") && HttpMethod.GET.equals(method)) {
            return isStaff;
        }

        // Quy tắc 2: Thêm, Sửa, Xóa Đề tài (/api/detai) chỉ cho GIANGVIEN/ADMIN
        if (path.startsWith("/api/detai") && (HttpMethod.POST.equals(method) || HttpMethod.PUT.equals(method) || HttpMethod.DELETE.equals(method))) {
            return isStaff;
        }

        // Quy tắc 3: Sửa đề tài đăng ký hoặc Hủy đăng ký (/api/dangky) chỉ cho GIANGVIEN/ADMIN
        if (path.startsWith("/api/dangky") && (HttpMethod.PUT.equals(method) || HttpMethod.DELETE.equals(method)) && !path.contains("/diem")) {
            return isStaff;
        }

        // Quy tắc 4: Chấm điểm (/api/dangky/{id}/diem) chỉ cho GIANGVIEN/ADMIN
        if (path.contains("/diem") && HttpMethod.PUT.equals(method)) {
            return isStaff;
        }

        // Các trường hợp hợp lệ còn lại (ví dụ: SINHVIEN đăng ký đề tài POST /api/dangky, xem thông tin cá nhân)
        return true;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }
}