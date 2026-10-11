package com.qldatn.sinhvien.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "SINHVIEN")
public class SinhVien {

    @Id
    @Column(name = "ma_sv", length = 10)
    private String maSV;

    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "lop", length = 20)
    private String lop;

    @Column(name = "email", length = 100)
    private String email;

    public String getMaSV() { return maSV; }
    public void setMaSV(String maSV) { this.maSV = maSV; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getLop() { return lop; }
    public void setLop(String lop) { this.lop = lop; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
