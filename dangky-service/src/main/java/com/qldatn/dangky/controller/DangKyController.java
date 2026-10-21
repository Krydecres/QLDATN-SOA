package com.qldatn.dangky.controller;

import com.qldatn.dangky.dto.DangKyRequest;
import com.qldatn.dangky.dto.DangKyView;
import com.qldatn.dangky.dto.DiemRequest;
import com.qldatn.dangky.service.DangKyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/dangky")
public class DangKyController {

    private final DangKyService service;

    public DangKyController(DangKyService service) {
        this.service = service;
    }

    @GetMapping
    public List<DangKyView> getAll() {
        return service.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "BearerAuth")
    public DangKyView create(
            @RequestBody DangKyRequest req,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestHeader(value = "X-User-MaSV", required = false) String tokenMaSV) {

        if ("SINHVIEN".equals(role) && !tokenMaSV.equalsIgnoreCase(req.maSV())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn chỉ được phép đăng ký đề tài cho chính mình!");
        }

        return service.create(req);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "BearerAuth")
    public DangKyView update(@PathVariable Long id, @RequestBody DangKyRequest req) {
        // Chức năng đổi/sửa đề tài đăng ký
        return service.update(id, req);
    }

    @PutMapping("/{id}/diem")
    @SecurityRequirement(name = "BearerAuth")
    public DangKyView chamDiem(@PathVariable Long id, @RequestBody DiemRequest req) {
        return service.chamDiem(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "BearerAuth")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }


    // Endpoint NỘI BỘ dành riêng cho detai-service gọi kiểm tra trước khi xóa đề tài
    @GetMapping("/internal/check-detai/{maDT}")
    public boolean checkDeTaiDaDangKy(
            @PathVariable String maDT,
            @RequestHeader(value = "X-Internal-Secret", required = false) String secret) {

        String INTERNAL_SECRET = "QLDATN_INTER_SERVICE_SECRET_KEY_2026";
        if (secret == null || !secret.equals(INTERNAL_SECRET)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Từ chối truy cập: Thiếu hoặc sai Secret Key nội bộ!");
        }

        return service.existsByMaDT(maDT);
    }
}