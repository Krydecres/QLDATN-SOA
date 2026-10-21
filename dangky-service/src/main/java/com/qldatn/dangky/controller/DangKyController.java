package com.qldatn.dangky.controller;

import com.qldatn.dangky.dto.DangKyRequest;
import com.qldatn.dangky.dto.DangKyView;
import com.qldatn.dangky.dto.DiemRequest;
import com.qldatn.dangky.service.DangKyService;
import com.qldatn.dangky.util.JwtUtil;
import io.jsonwebtoken.Claims;
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

    // 1. Xem danh sách: Ai cũng xem được (Sinh viên, Giảng viên, Admin)
    @GetMapping
    public List<DangKyView> getAll() {
        return service.getAll();
    }

    // 2. Tạo đăng ký mới: Sinh viên chỉ được đăng ký cho chính mình
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DangKyView create(@RequestBody DangKyRequest req, 
                             @RequestHeader("Authorization") String authHeader) {
        Claims claims = JwtUtil.parseToken(authHeader);
        String role = claims.get("role", String.class);
        String tokenMaSV = claims.get("maSV", String.class);

        if ("SINHVIEN".equals(role) && !tokenMaSV.equalsIgnoreCase(req.maSV())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn chỉ được phép đăng ký đề tài cho chính mình!");
        }

        return service.create(req);
    }

    // 3. Đổi đề tài: Chặn Sinh viên, chỉ ADMIN/GIANGVIEN được sửa
    @PutMapping("/{id}")
    public DangKyView update(@PathVariable Long id, 
                             @RequestBody DangKyRequest req,
                             @RequestHeader("Authorization") String authHeader) {
        Claims claims = JwtUtil.parseToken(authHeader);
        String role = claims.get("role", String.class);

        if ("SINHVIEN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sinh viên không được tự ý sửa đề tài! Vui lòng liên hệ Giảng viên.");
        }

        return service.update(id, req);
    }

    // 4. Chấm điểm: Chỉ GIANGVIEN hoặc ADMIN
    @PutMapping("/{id}/diem")
    public DangKyView chamDiem(@PathVariable Long id, 
                               @RequestBody DiemRequest req,
                               @RequestHeader("Authorization") String authHeader) {
        Claims claims = JwtUtil.parseToken(authHeader);
        String role = claims.get("role", String.class);

        if (!"GIANGVIEN".equals(role) && !"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chỉ Giảng viên hoặc Admin mới có quyền chấm điểm!");
        }

        return service.chamDiem(id, req);
    }

    // 5. Hủy/Xóa đăng ký: Chặn Sinh viên
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, 
                       @RequestHeader("Authorization") String authHeader) {
        Claims claims = JwtUtil.parseToken(authHeader);
        String role = claims.get("role", String.class);

        if ("SINHVIEN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sinh viên không được tự ý hủy đăng ký!");
        }

        service.delete(id);
    }
}