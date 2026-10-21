package com.qldatn.detai.controller;

import com.qldatn.detai.dto.DeTaiRequest;
import com.qldatn.detai.entity.DeTai;
import com.qldatn.detai.repository.DeTaiRepository;
import com.qldatn.detai.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/detai")
public class DeTaiController {

    private final DeTaiRepository repo;

    public DeTaiController(DeTaiRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<DeTai> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public DeTai getById(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đề tài " + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "BearerAuth")
    public DeTai create(
            @RequestBody DeTaiRequest req,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        checkPermission(authHeader, "Chỉ Giảng viên hoặc Admin mới có quyền thêm đề tài!");

        if (req.tenDT() == null || req.tenDT().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên đề tài không được để trống");
        }

        DeTai dt = new DeTai();
        dt.setTenDT(req.tenDT());
        dt.setGvhd(req.gvhd());
        dt.setMoTa(req.moTa());

        return repo.save(dt);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "BearerAuth")
    public DeTai update(
            @PathVariable Long id,
            @RequestBody DeTaiRequest req,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        checkPermission(authHeader, "Chỉ Giảng viên hoặc Admin mới có quyền sửa đề tài!");

        DeTai old = getById(id);
        if (req.tenDT() == null || req.tenDT().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên đề tài không được để trống");
        }
        old.setTenDT(req.tenDT());
        old.setGvhd(req.gvhd());
        old.setMoTa(req.moTa());

        return repo.save(old);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "BearerAuth")
    public void delete(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        checkPermission(authHeader, "Chỉ Giảng viên hoặc Admin mới có quyền xóa đề tài!");

        repo.delete(getById(id));
    }

    private void checkPermission(String authHeader, String errorMessage) {
        Claims claims = JwtUtil.parseToken(authHeader);
        String role = claims.get("role", String.class);

        if ("SINHVIEN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
        }
    }
}