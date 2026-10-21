package com.qldatn.dangky.repository;

import com.qldatn.dangky.entity.DangKy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DangKyRepository extends JpaRepository<DangKy, Long> {
    Optional<DangKy> findByMaSV(String maSV);
    Optional<DangKy> findByMaDT(Long maDT);
    boolean existsByMaDT(Long maDT);
}