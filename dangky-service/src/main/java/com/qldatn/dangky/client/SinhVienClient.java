package com.qldatn.dangky.client;

import com.qldatn.dangky.dto.SinhVienDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SinhVienClient {

    private final RestClient rest;
    
    // Secret Key phải khớp 100% với Secret Key khai báo bên auth-service
    private static final String INTERNAL_SECRET = "QLDATN_INTER_SERVICE_SECRET_KEY_2026";

    public SinhVienClient(@Value("${service.sinhvien.url}") String baseUrl) {
        this.rest = RestClient.create(baseUrl);
    }

    public SinhVienDTO findByMa(String maSV) {
        try {
            return rest.get()
                    .uri("/{ma}", maSV)
                    .header("X-Internal-Secret", INTERNAL_SECRET) // Gửi kèm Secret Key nội bộ
                    .retrieve()
                    .body(SinhVienDTO.class);
        } catch (Exception e) {
            System.err.println("Lỗi gọi auth-service: " + e.getMessage());
            return null;
        }
    }
}