package com.qldatn.dangky.service;

import com.qldatn.dangky.client.DeTaiClient;
import com.qldatn.dangky.client.SinhVienClient;
import com.qldatn.dangky.dto.*;
import com.qldatn.dangky.entity.DangKy;
import com.qldatn.dangky.repository.DangKyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class DangKyService {

    private final DangKyRepository repo;
    private final SinhVienClient sinhVienClient;
    private final DeTaiClient deTaiClient;

    public DangKyService(DangKyRepository repo, SinhVienClient sinhVienClient, DeTaiClient deTaiClient) {
        this.repo = repo;
        this.sinhVienClient = sinhVienClient;
        this.deTaiClient = deTaiClient;
    }

    public List<DangKyView> getAll() {
        return repo.findAll().stream().map(this::toView).toList();
    }

    public DangKyView getById(Long id) {
        return toView(find(id));
    }

    public DangKyView create(DangKyRequest req) {
        validate(req, null);
        DangKy dk = new DangKy();
        dk.setMaSV(req.maSV());
        dk.setMaDT(req.maDT());
        dk.setNgayDangKy(LocalDate.now());
        return toView(repo.save(dk));
    }

    public DangKyView update(Long id, DangKyRequest req) {
        DangKy dk = find(id);
        
        // GIỚI HẠN NGHIỆP VỤ: Đã chấm điểm thì KHÔNG ĐƯỢC ĐỔI/SỬA ĐỀ TÀI
        if (dk.getDiemTongKet() != null || dk.getDiemGVHD() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đề tài này đã được chấm điểm, không thể thay đổi thông tin đăng ký!");
        }

        validate(req, id);
        dk.setMaSV(req.maSV());
        dk.setMaDT(req.maDT());
        return toView(repo.save(dk));
    }

    public void delete(Long id) {
        DangKy dk = find(id);

        // GIỚI HẠN NGHIỆP VỤ: Đã chấm điểm thì KHÔNG ĐƯỢC HỦY ĐĂNG KÝ
        if (dk.getDiemTongKet() != null || dk.getDiemGVHD() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đề tài này đã được chấm điểm, không thể hủy đăng ký!");
        }

        repo.delete(dk);
    }

    public DangKyView chamDiem(Long id, DiemRequest d) {
        DangKy dk = find(id);
        checkDiem(d.diemGVHD(), "GVHD");
        checkDiem(d.diemPhanBien(), "phản biện");
        checkDiem(d.diemHoiDong(), "hội đồng");

        double tongKet = d.diemGVHD() * 0.3 + d.diemPhanBien() * 0.3 + d.diemHoiDong() * 0.4;
        tongKet = Math.round(tongKet * 100) / 100.0;

        dk.setDiemGVHD(d.diemGVHD());
        dk.setDiemPhanBien(d.diemPhanBien());
        dk.setDiemHoiDong(d.diemHoiDong());
        dk.setDiemTongKet(tongKet);
        dk.setXepLoai(xepLoai(tongKet));
        return toView(repo.save(dk));
    }

    static String xepLoai(double diem) {
        if (diem >= 9) return "Xuất sắc";
        if (diem >= 8) return "Giỏi";
        if (diem >= 6.5) return "Khá";
        if (diem >= 5) return "Trung bình";
        return "Không đạt";
    }

    private DangKy find(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đăng ký " + id));
    }

    private void checkDiem(Double diem, String ten) {
        if (diem == null || diem < 0 || diem > 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Điểm " + ten + " phải nằm trong khoảng 0 - 10");
        }
    }

    private void validate(DangKyRequest req, Long currentId) {
        if (req.maSV() == null || req.maSV().isBlank() || req.maDT() == null || req.maDT().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã SV và mã đề tài không được để trống");
        }
        if (sinhVienClient.findByMa(req.maSV()) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sinh viên " + req.maSV() + " không tồn tại (theo sinhvien-service)");
        }
        if (deTaiClient.findByMa(req.maDT()) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đề tài " + req.maDT() + " không tồn tại (theo detai-service)");
        }
        repo.findByMaSV(req.maSV())
                .filter(d -> !d.getId().equals(currentId))
                .ifPresent(d -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Sinh viên " + req.maSV() + " đã đăng ký đề tài " + d.getMaDT());
                });
        repo.findByMaDT(req.maDT())
                .filter(d -> !d.getId().equals(currentId))
                .ifPresent(d -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Đề tài " + req.maDT() + " đã có sinh viên " + d.getMaSV() + " đăng ký");
                });
    }

    private DangKyView toView(DangKy dk) {
        SinhVienDTO sv = null;
        DeTaiDTO dt = null;
        try { sv = sinhVienClient.findByMa(dk.getMaSV()); } catch (ResponseStatusException ignored) { }
        try { dt = deTaiClient.findByMa(dk.getMaDT()); } catch (ResponseStatusException ignored) { }

        return new DangKyView(
                dk.getId(),
                dk.getMaSV(),
                sv != null ? sv.hoTen() : "(không lấy được)",
                sv != null ? sv.lop() : null,
                dk.getMaDT(),
                dt != null ? dt.tenDT() : "(không lấy được)",
                dt != null ? dt.gvhd() : null,
                dk.getNgayDangKy(),
                dk.getDiemGVHD(),
                dk.getDiemPhanBien(),
                dk.getDiemHoiDong(),
                dk.getDiemTongKet(),
                dk.getXepLoai());
    }

    public boolean existsByMaDT(String maDT) {
        return repo.existsByMaDT(maDT);
    }
}