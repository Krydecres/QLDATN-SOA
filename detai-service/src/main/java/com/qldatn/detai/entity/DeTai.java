package com.qldatn.detai.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "DETAI")
public class DeTai {

    @Id
    @Column(name = "ma_dt", length = 10)
    private String maDT;

    @Column(name = "ten_dt", nullable = false, length = 200)
    private String tenDT;

    @Column(name = "gvhd", length = 100)
    private String gvhd;

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    public String getMaDT() { return maDT; }
    public void setMaDT(String maDT) { this.maDT = maDT; }
    public String getTenDT() { return tenDT; }
    public void setTenDT(String tenDT) { this.tenDT = tenDT; }
    public String getGvhd() { return gvhd; }
    public void setGvhd(String gvhd) { this.gvhd = gvhd; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}
