package com.qldatn.auth.controller;

import com.qldatn.auth.dto.LoginRequest;
import com.qldatn.auth.dto.LoginResponse;
import com.qldatn.auth.dto.SinhVienResponse;
import com.qldatn.auth.entity.TaiKhoan;
import com.qldatn.auth.repository.TaiKhoanRepository;
import com.qldatn.auth.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final TaiKhoanRepository repo;

    public AuthController(TaiKhoanRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        TaiKhoan tk = repo.findById(req.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Mã người dùng hoặc mật khẩu không đúng"));

        if (!tk.getPassword().equals(req.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Mã người dùng hoặc mật khẩu không đúng");
        }

        // Tạo JWT Token chứa username, role, maSV
        String jwtToken = JwtUtil.generateToken(tk.getUsername(), tk.getRole(), tk.getMaSV());

        return new LoginResponse(tk.getUsername(), tk.getHoTen(), tk.getRole(), tk.getMaSV(), jwtToken);
    }

    // GET tất cả SV: Chỉ GIANGVIEN hoặc ADMIN mới có quyền xem danh sách
    @GetMapping("/sinhvien")
    @SecurityRequirement(name = "BearerAuth")
    public List<SinhVienResponse> getAllSinhVien(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Claims claims = JwtUtil.parseToken(authHeader);
        String role = claims.get("role", String.class);

        if ("SINHVIEN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sinh viên không có quyền xem danh sách tất cả sinh viên!");
        }

        return repo.findByRole("SINHVIEN").stream()
                .map(this::toResponse)
                .toList();
    }

    // GET 1 SV: Sinh viên chỉ xem được chính mình; GIANGVIEN/ADMIN xem được bất kỳ ai
    @GetMapping("/sinhvien/{maSV}")
    @SecurityRequirement(name = "BearerAuth")
    public SinhVienResponse getSinhVienByMa(
            @PathVariable String maSV,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Claims claims = JwtUtil.parseToken(authHeader);
        String role = claims.get("role", String.class);
        String tokenMaSV = claims.get("maSV", String.class);

        if ("SINHVIEN".equals(role) && !maSV.equalsIgnoreCase(tokenMaSV)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn chỉ được phép xem thông tin của chính mình!");
        }

        TaiKhoan tk = repo.findByMaSVIgnoreCase(maSV)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sinh viên " + maSV));

        return toResponse(tk);
    }

    private SinhVienResponse toResponse(TaiKhoan tk) {
        return new SinhVienResponse(
                tk.getUsername(),
                tk.getHoTen(),
                tk.getLop(),
                tk.getEmail(),
                tk.getMaSV()
        );
    }


    // Endpoint NỘI BỘ dành riêng cho các Service khác (dangky-service, detai-service) gọi tra cứu
    @GetMapping("/sinhvien/internal/{maSV}")
    public SinhVienResponse getSinhVienInternal(
            @PathVariable String maSV,
            @RequestHeader(value = "X-Internal-Secret", required = false) String secret) {

        // Khóa bí mật dùng chung giữa các Microservices
        String INTERNAL_SECRET = "QLDATN_INTER_SERVICE_SECRET_KEY_2026";

        if (secret == null || !secret.equals(INTERNAL_SECRET)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Từ chối truy cập: Thiếu hoặc sai Secret Key nội bộ!");
        }

        TaiKhoan tk = repo.findByMaSVIgnoreCase(maSV)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sinh viên " + maSV));

        // Trả về DTO đã ẩn password và role
        return new SinhVienResponse(
                tk.getUsername(),
                tk.getHoTen(),
                tk.getLop(),
                tk.getEmail(),
                tk.getMaSV()
        );
    }
}