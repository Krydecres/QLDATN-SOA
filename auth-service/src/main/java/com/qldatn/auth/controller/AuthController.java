package com.qldatn.auth.controller;

import com.qldatn.auth.dto.LoginRequest;
import com.qldatn.auth.dto.LoginResponse;
import com.qldatn.auth.entity.TaiKhoan;
import com.qldatn.auth.repository.TaiKhoanRepository;
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

    // API 1: Đăng nhập
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        TaiKhoan tk = repo.findById(req.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Mã người dùng hoặc mật khẩu không đúng"));

        if (!tk.getPassword().equals(req.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Mã người dùng hoặc mật khẩu không đúng");
        }

        // Token dạng: "SV001:SINHVIEN" hoặc "gv001:GIANGVIEN"
        String token = tk.getUsername() + ":" + tk.getRole();

        return new LoginResponse(tk.getUsername(), tk.getHoTen(), tk.getRole(), tk.getMaSV(), token);
    }

    // API 2: Lấy danh sách tất cả Sinh viên (để phục vụ combobox hoặc Admin quản lý)
    @GetMapping("/sinhvien")
    public List<TaiKhoan> getAllSinhVien() {
        return repo.findByRole("SINHVIEN");
    }

    // API 3: Lấy thông tin 1 Sinh viên theo mã (cho dangky-service gọi kiểm tra)
    @GetMapping("/sinhvien/{maSV}")
    public TaiKhoan getSinhVienByMa(@PathVariable String maSV) {
        return repo.findByMaSV(maSV)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sinh viên " + maSV));
    }
}