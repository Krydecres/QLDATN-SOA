-- Du lieu demo cho 3 service. Chay bang MySQL 8 voi ma hoa UTF-8.
-- Moi service so huu database rieng, khong JOIN/FOREIGN KEY cheo service.
-- Co the chay lai: khong xoa bang, khong ghi de cac ban ghi da ton tai.
SET NAMES utf8mb4;
SET SESSION sql_mode = 'STRICT_ALL_TABLES,NO_ENGINE_SUBSTITUTION';

-- Database cho auth-service
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

-- Ma de tai la id BIGINT tu tang, dung voi Entity DeTai.
CREATE TABLE IF NOT EXISTS DETAI (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ten_dt VARCHAR(200) NOT NULL,
    gvhd VARCHAR(100),
    mo_ta VARCHAR(500)
);

INSERT IGNORE INTO DETAI (id, ten_dt, gvhd, mo_ta) VALUES
(1,'Xây dựng hệ thống quản lý thư viện theo SOA', 'TS. Nguyễn Văn Hùng', 'Spring Boot, REST API'),
(2,'Ứng dụng web bán hàng trực tuyến', 'ThS. Trần Thu Hà', 'Microservices, MySQL'),
(3,'Chatbot hỗ trợ tuyển sinh', 'TS. Lê Quang Minh', 'NLP, Python');


-- Database cho dangky-service
CREATE DATABASE IF NOT EXISTS qldatn_dangky CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE qldatn_dangky;
CREATE TABLE IF NOT EXISTS DANGKY (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ma_sv VARCHAR(10) NOT NULL UNIQUE,
    ma_dt BIGINT NOT NULL UNIQUE,
    ngay_dang_ky DATE,
    diem_gvhd DOUBLE,
    diem_phan_bien DOUBLE,
    diem_hoi_dong DOUBLE,
    diem_tong_ket DOUBLE,
    xep_loai VARCHAR(20)
);

-- Dong bo bang da tao bang phien ban SQL cu (ma_dt la VARCHAR).
-- Khong chuyen cac ma dang DT01/DT02: MySQL se bao loi de tranh sai du lieu.
ALTER TABLE DANGKY MODIFY COLUMN ma_dt BIGINT NOT NULL;



INSERT IGNORE INTO DANGKY (id, ma_sv, ma_dt, ngay_dang_ky, diem_gvhd, diem_phan_bien, diem_hoi_dong, diem_tong_ket, xep_loai) VALUES
(1, 'SV001', 1, '2026-09-15', 8.5, 8.0, 9.0, 8.55, 'Giỏi'),
(2, 'SV002', 2, '2026-09-16', NULL, NULL, NULL, NULL, NULL);
