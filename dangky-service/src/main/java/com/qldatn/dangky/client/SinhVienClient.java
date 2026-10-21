package com.qldatn.dangky.client;

import com.qldatn.dangky.dto.SinhVienDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Component
public class SinhVienClient {

    private final RestClient rest;

    public SinhVienClient(@Value("${service.sinhvien.url}") String baseUrl) {
        this.rest = RestClient.create(baseUrl);
    }

    public SinhVienDTO findByMa(String maSV) {
        try {
            return rest.get().uri("/{ma}", maSV).retrieve().body(SinhVienDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            // Log lỗi ra console để dễ debug nếu không gọi được auth-service
            System.err.println("Lỗi gọi auth-service: " + e.getMessage());
            return null;
        }
    }
}