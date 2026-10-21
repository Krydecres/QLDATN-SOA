package com.qldatn.auth.repository;

import com.qldatn.auth.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, String> {
    List<TaiKhoan> findByRole(String role);
    Optional<TaiKhoan> findByMaSVIgnoreCase(String maSV);
}