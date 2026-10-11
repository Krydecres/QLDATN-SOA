package com.qldatn.dangky.dto;

import java.time.LocalDate;

/** Ket qua tra ve cho client: du lieu DANGKY + thong tin ghep tu 2 service kia. */
public record DangKyView(
        Long id,
        String maSV,
        String hoTen,
        String lop,
        String maDT,
        String tenDT,
        String gvhd,
        LocalDate ngayDangKy,
        Double diemGVHD,
        Double diemPhanBien,
        Double diemHoiDong,
        Double diemTongKet,
        String xepLoai) {
}
