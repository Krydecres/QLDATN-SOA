package com.qldatn.sinhvien.controller;

import com.qldatn.sinhvien.entity.SinhVien;
import com.qldatn.sinhvien.repository.SinhVienRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Dich vu SINH VIEN - CRUD bang SINHVIEN.
 * Base URL: http://localhost:8081/api/sinhvien
 */
@RestController
@RequestMapping("/api/sinhvien")
@CrossOrigin(origins = "*")
public class SinhVienController {

    private final SinhVienRepository repo;

    public SinhVienController(SinhVienRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<SinhVien> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{maSV}")
    public SinhVien getById(@PathVariable String maSV) {
        return repo.findById(maSV)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sinh viên " + maSV));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SinhVien create(@RequestBody SinhVien sv) {
        if (sv.getMaSV() == null || sv.getMaSV().isBlank() || sv.getHoTen() == null || sv.getHoTen().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã SV và họ tên không được để trống");
        }
        if (repo.existsById(sv.getMaSV())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mã SV " + sv.getMaSV() + " đã tồn tại");
        }
        return repo.save(sv);
    }

    @PutMapping("/{maSV}")
    public SinhVien update(@PathVariable String maSV, @RequestBody SinhVien sv) {
        SinhVien old = getById(maSV);
        old.setHoTen(sv.getHoTen());
        old.setLop(sv.getLop());
        old.setEmail(sv.getEmail());
        return repo.save(old);
    }

    @DeleteMapping("/{maSV}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String maSV) {
        repo.delete(getById(maSV));
    }
}
