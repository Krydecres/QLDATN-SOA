package com.qldatn.detai.controller;

import com.qldatn.detai.client.DangKyClient;
import com.qldatn.detai.dto.DeTaiRequest;
import com.qldatn.detai.entity.DeTai;
import com.qldatn.detai.repository.DeTaiRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/detai")
public class DeTaiController {

    private final DeTaiRepository repo;
    private final DangKyClient dangKyClient;

    public DeTaiController(DeTaiRepository repo, DangKyClient dangKyClient) {
        this.repo = repo;
        this.dangKyClient = dangKyClient;
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
    public DeTai create(@RequestBody DeTaiRequest req) {
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
    public DeTai update(@PathVariable Long id, @RequestBody DeTaiRequest req) {
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
    public void delete(@PathVariable Long id) {
        DeTai dt = getById(id);

        // KIỂM TRA NGHIỆP VỤ NỘI BỘ VỚI DANGKY-SERVICE:
        // Đề tài đã có sinh viên đăng ký thì KHÔNG ĐƯỢC XÓA!
        if (dangKyClient.isDeTaiDaDuocDangKy(dt.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Không thể xóa đề tài này vì đã có sinh viên đăng ký!");
        }

        repo.delete(dt);
    }
}