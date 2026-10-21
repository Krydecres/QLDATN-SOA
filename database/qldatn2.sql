-- Active: 1791519077776@@127.0.0.1@3306@qldatn_dangky
-- Database cho sinhvien-service
CREATE DATABASE IF NOT EXISTS qldatn_sinhvien CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE qldatn_sinhvien;
CREATE TABLE IF NOT EXISTS SINHVIEN (
    ma_sv VARCHAR(10) PRIMARY KEY,
    ho_ten VARCHAR(100) NOT NULL,
    lop VARCHAR(20),
    email VARCHAR(100)
);

-- Database cho detai-service
CREATE DATABASE IF NOT EXISTS qldatn_detai CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE qldatn_detai;
CREATE TABLE IF NOT EXISTS DETAI (
    ma_dt VARCHAR(10) PRIMARY KEY,
    ten_dt VARCHAR(200) NOT NULL,
    gvhd VARCHAR(100),
    mo_ta VARCHAR(500)
);

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
use qldatn_sinhvien;
INSERT IGNORE INTO SINHVIEN VALUES
('SV001', 'Nguyễn Văn An',   'CNTT01', 'an.nv@student.edu.vn'),
('SV002', 'Trần Thị Bình',   'CNTT01', 'binh.tt@student.edu.vn'),
('SV003', 'Lê Hoàng Cường',  'CNTT02', 'cuong.lh@student.edu.vn'),
('SV004', 'Phạm Minh Dũng',  'CNTT02', 'dung.pm@student.edu.vn');
SELECT * FROM SINHVIEN

use qldatn_detai;
INSERT IGNORE INTO DETAI VALUES
('DT01', 'Xây dựng hệ thống quản lý thư viện theo SOA', 'TS. Nguyễn Văn Hùng', 'Spring Boot, REST API'),
('DT02', 'Ứng dụng web bán hàng trực tuyến',            'ThS. Trần Thu Hà',    'Microservices, MySQL'),
('DT03', 'Chatbot hỗ trợ tuyển sinh',                    'TS. Lê Quang Minh',   'NLP, Python'),
('DT04', 'Hệ thống điểm danh bằng nhận diện khuôn mặt',  'ThS. Đỗ Thị Lan',     'Computer Vision');
SELECT * FROM DETAI

use qldatn_dangky;
INSERT IGNORE INTO DANGKY (id, ma_sv, ma_dt, ngay_dang_ky, diem_gvhd, diem_phan_bien, diem_hoi_dong, diem_tong_ket, xep_loai) VALUES
(1, 'SV001', 'DT01', '2026-09-15', 8.5, 8.0, 9.0, 8.55, 'Giỏi'),
(2, 'SV002', 'DT02', '2026-09-16', NULL, NULL, NULL, NULL, NULL);
select * from DANGKY