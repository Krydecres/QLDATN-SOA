-- Active: 1791519077776@@127.0.0.1@3306@qldatn_auth
-- Database cho sinhvien-service
CREATE DATABASE IF NOT EXISTS qldatn_auth CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE qldatn_auth;

CREATE TABLE IF NOT EXISTS TAIKHOAN (
    username VARCHAR(50) PRIMARY KEY, -- Mã SV (với SV) hoặc gv001 / admin
    password VARCHAR(255) NOT NULL,
    ho_ten VARCHAR(100) NOT NULL,
    lop VARCHAR(20) NULL,             -- Chỉ dành cho Sinh viên
    email VARCHAR(100) NULL,
    role VARCHAR(20) NOT NULL,        -- 'SINHVIEN', 'GIANGVIEN', 'ADMIN'
    ma_sv VARCHAR(10) NULL            -- Giống username nếu role = SINHVIEN
);

-- Thêm dữ liệu mẫu
INSERT IGNORE INTO TAIKHOAN (username, password, ho_ten, lop, email, role, ma_sv) VALUES
('admin', '123456', 'Quản trị viên Khoa', NULL, 'admin@fit.edu.vn', 'ADMIN', NULL),
('gv001', '123456', 'TS. Nguyễn Văn Hùng', NULL, 'hung.nv@fit.edu.vn', 'GIANGVIEN', NULL),
('SV001', '123456', 'Nguyễn Văn An', 'CNTT01', 'an.nv@student.edu.vn', 'SINHVIEN', 'SV001'),
('SV002', '123456', 'Trần Thị Bình', 'CNTT01', 'binh.tt@student.edu.vn', 'SINHVIEN', 'SV002'),
('SV003', '123456', 'Lê Hoàng Cường', 'CNTT02', 'cuong.lh@student.edu.vn', 'SINHVIEN', 'SV003');


-- Database cho detai-service
CREATE DATABASE IF NOT EXISTS qldatn_detai CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE qldatn_detai;

-- Tạo lại bảng DETAI với id tự tăng
CREATE TABLE DETAI (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ten_dt VARCHAR(200) NOT NULL,
    gvhd VARCHAR(100),
    mo_ta VARCHAR(500)
);

INSERT INTO DETAI (ten_dt, gvhd, mo_ta) VALUES
('Xây dựng hệ thống quản lý thư viện theo SOA', 'TS. Nguyễn Văn Hùng', 'Spring Boot, REST API'),
('Ứng dụng web bán hàng trực tuyến', 'ThS. Trần Thu Hà', 'Microservices, MySQL'),
('Chatbot hỗ trợ tuyển sinh', 'TS. Lê Quang Minh', 'NLP, Python');

-- Database cho dangky-service
CREATE DATABASE IF NOT EXISTS qldatn_dangky CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE qldatn_dangky;
CREATE TABLE IF NOT EXISTS DANGKY (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ma_sv VARCHAR(10) NOT NULL UNIQUE,
    ma_dt VARCHAR(10) NOT NULL UNIQUE,
    ngay_dang_ky DATE,
    diem_gvhd DOUBLE,
    diem_phan_bien DOUBLE,
    diem_hoi_dong DOUBLE,
    diem_tong_ket DOUBLE,
    xep_loai VARCHAR(20)
);




use qldatn_dangky;
INSERT IGNORE INTO DANGKY (id, ma_sv, ma_dt, ngay_dang_ky, diem_gvhd, diem_phan_bien, diem_hoi_dong, diem_tong_ket, xep_loai) VALUES
(1, 'SV001', 'DT01', '2026-09-15', 8.5, 8.0, 9.0, 8.55, 'Giỏi'),
(2, 'SV002', 'DT02', '2026-09-16', NULL, NULL, NULL, NULL, NULL);
select * from DANGKY