package com.qldatn.dangky.client;

import com.qldatn.dangky.dto.SinhVienDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SinhVienClient {

    private final RestClient rest;
    private static final String INTERNAL_SECRET = "QLDATN_INTER_SERVICE_SECRET_KEY_2026";

    // Inject RestClient.Builder đã gắn @LoadBalanced
    public SinhVienClient(@Value("${service.sinhvien.url}") String baseUrl, RestClient.Builder builder) {
        this.rest = builder.baseUrl(baseUrl).build();
    }

    public SinhVienDTO findByMa(String maSV) {
        try {
            return rest.get()
                    .uri("/{ma}", maSV)
                    .header("X-Internal-Secret", INTERNAL_SECRET)
                    .retrieve()
                    .body(SinhVienDTO.class);
        } catch (Exception e) {
            System.err.println("Lỗi gọi auth-service: " + e.getMessage());
            return null;
        }
    }
}