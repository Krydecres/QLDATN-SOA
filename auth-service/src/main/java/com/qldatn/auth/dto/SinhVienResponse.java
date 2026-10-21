package com.qldatn.auth.dto;

public record SinhVienResponse(
        String username,
        String hoTen,
        String lop,
        String email,
        String maSV
) {}