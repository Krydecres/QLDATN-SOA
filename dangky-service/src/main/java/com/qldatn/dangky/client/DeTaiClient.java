package com.qldatn.dangky.client;

import com.qldatn.dangky.dto.DeTaiDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

/** Goi detai-service qua HTTP/REST (GET /api/detai/{maDT}). */
@Component
public class DeTaiClient {

    private final RestClient rest;

    public DeTaiClient(@Value("${service.detai.url}") String baseUrl) {
        this.rest = RestClient.create(baseUrl);
    }

    /** Tra ve null neu de tai khong ton tai; nem 503 neu service khong chay. */
    public DeTaiDTO findByMa(String maDT) {
        try {
            return rest.get().uri("/{ma}", maDT).retrieve().body(DeTaiDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Không kết nối được detai-service (port 8082)");
        }
    }
}
