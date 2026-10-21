package com.qldatn.detai.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class DangKyClient {

    private final RestClient rest;
    private static final String INTERNAL_SECRET = "QLDATN_INTER_SERVICE_SECRET_KEY_2026";

    public DangKyClient(@Value("${service.dangky.url:http://localhost:8083/api/dangky/internal/check-detai}") String baseUrl) {
        this.rest = RestClient.create(baseUrl);
    }

    public boolean isDeTaiDaDuocDangKy(String maDT) {
        try {
            Boolean result = rest.get()
                    .uri("/{maDT}", maDT)
                    .header("X-Internal-Secret", INTERNAL_SECRET)
                    .retrieve()
                    .body(Boolean.class);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            System.err.println("Lỗi gọi dangky-service: " + e.getMessage());
            return false;
        }
    }
}