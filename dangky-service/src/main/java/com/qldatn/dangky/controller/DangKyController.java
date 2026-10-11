package com.qldatn.dangky.controller;

import com.qldatn.dangky.dto.DangKyRequest;
import com.qldatn.dangky.dto.DangKyView;
import com.qldatn.dangky.dto.DiemRequest;
import com.qldatn.dangky.service.DangKyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Dich vu DANG KY DE TAI + CHAM DIEM.
 * Base URL: http://localhost:8083/api/dangky
 */
@RestController
@RequestMapping("/api/dangky")
@CrossOrigin(origins = "*")
public class DangKyController {

    private final DangKyService service;

    public DangKyController(DangKyService service) {
        this.service = service;
    }

    @GetMapping
    public List<DangKyView> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public DangKyView getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DangKyView create(@RequestBody DangKyRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public DangKyView update(@PathVariable Long id, @RequestBody DangKyRequest req) {
        return service.update(id, req);
    }

    @PutMapping("/{id}/diem")
    public DangKyView chamDiem(@PathVariable Long id, @RequestBody DiemRequest req) {
        return service.chamDiem(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
