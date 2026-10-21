package com.qldatn.dangky.dto;

/** Body khi tao / sua dang ky: { "maSV": "SV003", "maDT": "DT03" } */
public record DangKyRequest(String maSV, Long maDT) {
}
