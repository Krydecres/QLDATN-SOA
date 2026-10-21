package com.qldatn.detai.controller;

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
        repo.delete(getById(id));
    }
}