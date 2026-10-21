package com.qldatn.detai.controller;

import com.qldatn.detai.entity.DeTai;
import com.qldatn.detai.repository.DeTaiRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Dich vu DE TAI - CRUD bang DETAI.
 * Base URL: http://localhost:8082/api/detai
 */
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

    @GetMapping("/{maDT}")
    public DeTai getById(@PathVariable String maDT) {
        return repo.findById(maDT)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đề tài " + maDT));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeTai create(@RequestBody DeTai dt) {
        if (dt.getMaDT() == null || dt.getMaDT().isBlank() || dt.getTenDT() == null || dt.getTenDT().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã đề tài và tên đề tài không được để trống");
        }
        if (repo.existsById(dt.getMaDT())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mã đề tài " + dt.getMaDT() + " đã tồn tại");
        }
        return repo.save(dt);
    }

    @PutMapping("/{maDT}")
    public DeTai update(@PathVariable String maDT, @RequestBody DeTai dt) {
        DeTai old = getById(maDT);
        old.setTenDT(dt.getTenDT());
        old.setGvhd(dt.getGvhd());
        old.setMoTa(dt.getMoTa());
        return repo.save(old);
    }

    @DeleteMapping("/{maDT}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String maDT) {
        repo.delete(getById(maDT));
    }
}
