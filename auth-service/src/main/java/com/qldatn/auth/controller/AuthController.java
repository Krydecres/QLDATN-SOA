package com.qldatn.auth.controller;

import com.qldatn.auth.dto.LoginRequest;
import com.qldatn.auth.dto.LoginResponse;
import com.qldatn.auth.entity.TaiKhoan;
import com.qldatn.auth.repository.TaiKhoanRepository;
import com.qldatn.auth.util.JwtUtil;
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

        // Tạo JWT Token chuẩn đã được mã hóa bằng thuật toán HS256
        String jwtToken = JwtUtil.generateToken(tk.getUsername(), tk.getRole(), tk.getMaSV());

        return new LoginResponse(tk.getUsername(), tk.getHoTen(), tk.getRole(), tk.getMaSV(), jwtToken);
    }

    @GetMapping("/sinhvien")
    public List<TaiKhoan> getAllSinhVien() {
        return repo.findByRole("SINHVIEN");
    }

    @GetMapping("/sinhvien/{maSV}")
    public TaiKhoan getSinhVienByMa(@PathVariable String maSV) {
        return repo.findByMaSV(maSV)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sinh viên " + maSV));
    }
}