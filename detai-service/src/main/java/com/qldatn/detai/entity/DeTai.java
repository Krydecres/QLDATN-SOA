package com.qldatn.detai.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "DETAI")
public class DeTai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_dt", nullable = false, length = 200)
    private String tenDT;

    @Column(name = "gvhd", length = 100)
    private String gvhd;

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTenDT() { return tenDT; }
    public void setTenDT(String tenDT) { this.tenDT = tenDT; }
    public String getGvhd() { return gvhd; }
    public void setGvhd(String gvhd) { this.gvhd = gvhd; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}