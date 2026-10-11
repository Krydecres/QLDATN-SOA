package com.qldatn.dangky.client;

import com.qldatn.dangky.dto.SinhVienDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

/** Goi sinhvien-service qua HTTP/REST (GET /api/sinhvien/{maSV}). */
@Component
public class SinhVienClient {

    private final RestClient rest;

    public SinhVienClient(@Value("${service.sinhvien.url}") String baseUrl) {
        this.rest = RestClient.create(baseUrl);
    }

    /** Tra ve null neu sinh vien khong ton tai; nem 503 neu service khong chay. */
    public SinhVienDTO findByMa(String maSV) {
        try {
            return rest.get().uri("/{ma}", maSV).retrieve().body(SinhVienDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Không kết nối được sinhvien-service (port 8081)");
        }
    }
}
