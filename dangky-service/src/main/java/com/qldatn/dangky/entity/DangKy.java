package com.qldatn.dangky.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Bang DANGKY chi luu MA sinh vien va MA de tai (khong khoa ngoai).
 * Thong tin chi tiet duoc lay tu sinhvien-service va detai-service qua REST.
 */
@Entity
@Table(name = "DANGKY")
public class DangKy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_sv", nullable = false, unique = true, length = 10)
    private String maSV;

    @Column(name = "ma_dt", nullable = false, unique = true, length = 10)
    private String maDT;

    @Column(name = "ngay_dang_ky")
    private LocalDate ngayDangKy;

    @Column(name = "diem_gvhd")
    private Double diemGVHD;

    @Column(name = "diem_phan_bien")
    private Double diemPhanBien;

    @Column(name = "diem_hoi_dong")
    private Double diemHoiDong;

    @Column(name = "diem_tong_ket")
    private Double diemTongKet;

    @Column(name = "xep_loai", length = 20)
    private String xepLoai;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMaSV() { return maSV; }
    public void setMaSV(String maSV) { this.maSV = maSV; }
    public String getMaDT() { return maDT; }
    public void setMaDT(String maDT) { this.maDT = maDT; }
    public LocalDate getNgayDangKy() { return ngayDangKy; }
    public void setNgayDangKy(LocalDate ngayDangKy) { this.ngayDangKy = ngayDangKy; }
    public Double getDiemGVHD() { return diemGVHD; }
    public void setDiemGVHD(Double diemGVHD) { this.diemGVHD = diemGVHD; }
    public Double getDiemPhanBien() { return diemPhanBien; }
    public void setDiemPhanBien(Double diemPhanBien) { this.diemPhanBien = diemPhanBien; }
    public Double getDiemHoiDong() { return diemHoiDong; }
    public void setDiemHoiDong(Double diemHoiDong) { this.diemHoiDong = diemHoiDong; }
    public Double getDiemTongKet() { return diemTongKet; }
    public void setDiemTongKet(Double diemTongKet) { this.diemTongKet = diemTongKet; }
    public String getXepLoai() { return xepLoai; }
    public void setXepLoai(String xepLoai) { this.xepLoai = xepLoai; }
}
