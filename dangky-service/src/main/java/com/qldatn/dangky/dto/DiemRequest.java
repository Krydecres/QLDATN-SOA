package com.qldatn.dangky.dto;

/** Body khi cham diem: { "diemGVHD": 8, "diemPhanBien": 7.5, "diemHoiDong": 8.5 } */
public record DiemRequest(Double diemGVHD, Double diemPhanBien, Double diemHoiDong) {
}
