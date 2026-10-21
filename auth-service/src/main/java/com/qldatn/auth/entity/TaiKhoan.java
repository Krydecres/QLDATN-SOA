package com.qldatn.auth.entity; // Hoặc package com.qldatn.auth.entity

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TAIKHOAN")
public class TaiKhoan {

    @Id
    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "lop", length = 20)
    private String lop;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "role", nullable = false, length = 20)
    private String role; // SINHVIEN, GIANGVIEN, ADMIN

    @Column(name = "ma_sv", length = 10)
    private String maSV;

    // Getter & Setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getLop() { return lop; }
    public void setLop(String lop) { this.lop = lop; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getMaSV() { return maSV; }
    public void setMaSV(String maSV) { this.maSV = maSV; }
}