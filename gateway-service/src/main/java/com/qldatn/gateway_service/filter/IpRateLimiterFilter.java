package com.qldatn.gateway_service.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class IpRateLimiterFilter extends AbstractGatewayFilterFactory<IpRateLimiterFilter.Config> {

    // Lưu trữ thông tin request theo IP: Map<IP, ClientWindow>. và lưu ở ram
    private final ConcurrentHashMap<String, RequestWindow> ipRequests = new ConcurrentHashMap<>();

    // Giới hạn: Tối đa 10 request trong vòng 1 giây cho mỗi IP
    private static final int MAX_REQUESTS_PER_SECOND = 10;

    public IpRateLimiterFilter() {
        super(Config.class);
    }

    public static class Config {
        // Có thể mở rộng cấu hình giới hạn riêng cho từng route nếu muốn
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            // Lấy địa chỉ IP của client gửi request lên
            String clientIp = "Unknown";
            if (request.getRemoteAddress() != null && request.getRemoteAddress().getAddress() != null) {
                clientIp = request.getRemoteAddress().getAddress().getHostAddress();
            }

            long currentSecond = Instant.now().getEpochSecond();

            // Kiểm tra và cập nhật bộ đếm qua ConcurrentHashMap an toàn đa luồng
            RequestWindow window = ipRequests.compute(clientIp, (ip, existingWindow) -> {
                if (existingWindow == null || existingWindow.second != currentSecond) {
                    // Sang giây mới hoặc chưa có -> reset bộ đếm về 1
                    return new RequestWindow(currentSecond, new AtomicInteger(1));
                } else {
                    // Cùng trong 1 giây -> tăng số lượng request lên
                    existingWindow.count.incrementAndGet();
                    return existingWindow;
                }
            });

            // Nếu vượt quá giới hạn cho phép trong giây đó -> Trả về lỗi 429 Too Many Requests
            if (window.count.get() > MAX_REQUESTS_PER_SECOND) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                return response.setComplete();
            }

            return chain.filter(exchange);
        };
    }

    // Lớp nội bộ đại diện cho cửa sổ thời gian (theo giây)
    private static class RequestWindow {
        long second;
        AtomicInteger count;

        public RequestWindow(long second, AtomicInteger count) {
            this.second = second;
            this.count = count;
        }
    }
}