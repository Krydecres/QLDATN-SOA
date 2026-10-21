# Demo SOA – Quản lý Đồ án Tốt nghiệp (Spring Boot)

Hệ thống gồm **3 dịch vụ độc lập**. Mỗi dịch vụ là một project Spring Boot riêng, chạy ở port riêng và chỉ quản lý một bảng.
Các dịch vụ trao đổi dữ liệu với nhau qua **HTTP/REST (JSON)**.

```
                 ┌──────────────────────────┐
                 │  Client (client/index.html)│
                 └─────┬──────────┬─────────┬┘
                  REST │     REST │    REST │
        ┌──────────────▼┐  ┌──────▼───────┐ ┌▼──────────────────┐
        │sinhvien-service│  │detai-service │ │  dangky-service   │
        │    :8081       │  │   :8082      │ │     :8083         │
        │ bảng SINHVIEN  │  │ bảng DETAI   │ │  bảng DANGKY      │
        └───────▲────────┘  └──────▲───────┘ └──┬────────────┬───┘
                │   GET /api/sinhvien/{ma}      │            │
                └───────────────────────────────┘            │
                               GET /api/detai/{ma}           │
                               ◄─────────────────────────────┘
                      (MySQL: database qldatn)
```

| Dịch vụ | Port | Bảng | Chức năng |
|---|---|---|---|
| sinhvien-service | 8081 | SINHVIEN | CRUD sinh viên |
| detai-service | 8082 | DETAI | CRUD đề tài |
| dangky-service | 8083 | DANGKY | Đăng ký đề tài, chấm điểm. **Gọi 2 service kia** để kiểm tra SV/đề tài có tồn tại và lấy họ tên, tên đề tài |

### Đặc trưng SOA thể hiện trong demo
- **Dịch vụ độc lập**: 3 project tách biệt, tắt 1 service thì các service khác vẫn chạy (thử tắt `detai-service` rồi xem danh sách đăng ký: cột tên đề tài hiện "(không lấy được)", không bị lỗi cả hệ thống).
- **Giao tiếp qua giao thức chuẩn**: HTTP + JSON. Bảng DANGKY không JOIN sang bảng khác mà gọi REST (`client/SinhVienClient.java`, `client/DeTaiClient.java`).
- **Tái sử dụng**: `sinhvien-service` và `detai-service` vừa được client dùng trực tiếp, vừa được `dangky-service` tái sử dụng.

## Cấu trúc thư mục
```
QLDATN-SOA/
├── database/qldatn.sql      # Script tạo CSDL + dữ liệu mẫu
├── sinhvien-service/        # entity / repository / controller
├── detai-service/           # entity / repository / controller
├── dangky-service/          # entity / repository / controller / service / client / dto
├── client/index.html        # Giao diện web gọi API
└── run-all.bat              # Chạy cả 3 service + mở client
```

## Cách chạy
Yêu cầu: **Java 21**, **MySQL** (user `root`, mật khẩu `123456`. Nếu khác thì sửa trong `src/main/resources/application.properties` của từng service).

1. Tạo CSDL (chỉ cần làm 1 lần):
   ```
   mysql -u root -p < database/qldatn.sql
   ```
   (Hoặc mở file trong MySQL Workbench rồi bấm Execute.)
2. Nhấp đúp **`run-all.bat`**: mở 3 cửa sổ cho 3 service, sau khoảng 20 giây tự mở giao diện client.
   Có thể chạy thủ công từng service: `cd sinhvien-service` rồi `mvnw spring-boot:run`, hoặc mở bằng IntelliJ và chạy class `*Application`.
3. Dùng giao diện `client/index.html`. Góc trên bên phải có đèn xanh/đỏ báo trạng thái từng service.

## Danh sách API

### sinhvien-service – `http://localhost:8081/api/sinhvien`
| Method | URL | Mô tả |
|---|---|---|
| GET | `/api/sinhvien` | Lấy tất cả |
| GET | `/api/sinhvien/{maSV}` | Lấy 1 sinh viên |
| POST | `/api/sinhvien` | Thêm `{"maSV":"SV005","hoTen":"...","lop":"CNTT01","email":"..."}` |
| PUT | `/api/sinhvien/{maSV}` | Sửa |
| DELETE | `/api/sinhvien/{maSV}` | Xóa |

### detai-service – `http://localhost:8082/api/detai`
Tương tự: GET, GET `/{maDT}`, POST `{"maDT":"DT05","tenDT":"...","gvhd":"...","moTa":"..."}`, PUT `/{maDT}`, DELETE `/{maDT}`.

### dangky-service – `http://localhost:8083/api/dangky`
| Method | URL | Mô tả |
|---|---|---|
| GET | `/api/dangky` | Danh sách đăng ký (đã ghép họ tên SV + tên đề tài từ 2 service kia) |
| GET | `/api/dangky/{id}` | Xem 1 đăng ký |
| POST | `/api/dangky` | Đăng ký `{"maSV":"SV003","maDT":"DT03"}` |
| PUT | `/api/dangky/{id}` | Đổi SV/đề tài |
| PUT | `/api/dangky/{id}/diem` | **Chấm điểm** `{"diemGVHD":8,"diemPhanBien":7.5,"diemHoiDong":9}` |
| DELETE | `/api/dangky/{id}` | Hủy đăng ký |

**Quy tắc nghiệp vụ**
- Mỗi sinh viên chỉ đăng ký 1 đề tài, mỗi đề tài chỉ 1 sinh viên.
- Khi đăng ký, `dangky-service` gọi `sinhvien-service` và `detai-service` để kiểm tra mã có tồn tại.
- **Tính điểm**: `Tổng kết = GVHD × 30% + Phản biện × 30% + Hội đồng × 40%`, làm tròn 2 chữ số.
- **Xếp loại**: ≥ 9 Xuất sắc · ≥ 8 Giỏi · ≥ 6.5 Khá · ≥ 5 Trung bình · < 5 Không đạt.

### Test nhanh bằng curl / Postman
```
curl http://localhost:8083/api/dangky
curl -X POST http://localhost:8083/api/dangky -H "Content-Type: application/json" -d "{\"maSV\":\"SV003\",\"maDT\":\"DT03\"}"
curl -X PUT http://localhost:8083/api/dangky/2/diem -H "Content-Type: application/json" -d "{\"diemGVHD\":8,\"diemPhanBien\":7.5,\"diemHoiDong\":9}"
```



2. Interoperability (Khả năng cộng tác)
Hiện trạng trong repo:
Repo đã đạt tốt phần này nhờ dùng chuẩn REST API (HTTP + JSON). Các dịch vụ trao đổi bằng JSON độc lập với nền tảng/ngôn ngữ.   
Cách tối ưu thêm (Chuẩn hóa API Contract bằng OpenAPI/Swagger): Để nâng cao khả năng tích hợp với hệ thống bên thứ ba (ví dụ ứng dụng di động, hệ thống cũ...), bạn nên chuẩn hóa tài liệu API công khai bằng Springdoc OpenAPI (Swagger).

Thêm dependency Swagger vào pom.xml của cả 3 service:

<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
Cấu hình Swagger UI:
Khi chạy ứng dụng, tài liệu chuẩn hóa API và schema DTO/JSON sẽ tự động tạo tại:

http://localhost:8081/swagger-ui.html (Sinh viên)
http://localhost:8082/swagger-ui.html (Đề tài)
http://localhost:8083/swagger-ui.html (Đăng ký)

Bất kỳ client nào (Node.js, C#, Python, Flutter) chỉ cần xem OpenAPI Spec (JSON/YAML) này là có thể gọi API chính xác mà không cần quan tâm backend viết bằng Java Spring Boot.

http://localhost:8800/index.html