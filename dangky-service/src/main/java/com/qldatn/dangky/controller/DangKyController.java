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
    public DangKyView update(
            @PathVariable Long id, 
            @RequestBody DangKyRequest req,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if ("SINHVIEN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sinh viên không được tự ý sửa đề tài! Vui lòng liên hệ Giảng viên.");
        }

        return service.update(id, req);
    }

    @PutMapping("/{id}/diem")
    @SecurityRequirement(name = "BearerAuth")
    public DangKyView chamDiem(
            @PathVariable Long id, 
            @RequestBody DiemRequest req,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if (!"GIANGVIEN".equals(role) && !"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chỉ Giảng viên hoặc Admin mới có quyền chấm điểm!");
        }

        return service.chamDiem(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "BearerAuth")
    public void delete(
            @PathVariable Long id, 
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if ("SINHVIEN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sinh viên không được tự ý hủy đăng ký!");
        }

        service.delete(id);
    }
}