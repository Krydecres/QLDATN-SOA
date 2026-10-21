# Demo SOA – Quản lý Đồ án Tốt nghiệp

## Chạy dự án

**Windows: nhấp đúp `run-all.bat`. Chỉ chạy một script, không chạy đồng thời với `run.sh`.**

Script tự chạy bốn service, đợi chúng sẵn sàng rồi mở http://localhost:8080/ trên trình duyệt. Bạn không cần mở terminal cho từng service. Giữ cửa sổ launcher khi demo; nhấn Enter ở đó để dừng cả bốn service. Log nằm trong `.demo-logs/`.

`run.sh` dành cho Bash trên Linux/macOS/WSL, với Java/MySQL cài và truy cập được trong môi trường đó. Script cũng chạy đủ bốn service và mở cùng địa chỉ Gateway. Git Bash trên Windows sẽ hướng dẫn dùng `run-all.bat`.

Yêu cầu: JDK 21, MySQL đang chạy tại localhost:3306 và dữ liệu đã nạp từ `database/qldatn.sql`. Maven Wrapper đã có trong từng service, không cần cài Maven riêng. Lần đầu có thể lâu vì cần tải dependency. Launcher báo lỗi nếu cổng service đã bị chiếm hoặc quá thời gian khởi động.

## Kiến trúc và database hiện tại

Giao diện được phục vụ từ `gateway-service/src/main/resources/static/index.html`. Các request đi qua Gateway tại cổng 8080, sau đó tới service nghiệp vụ. Không cần một client-service riêng.

| Service | Cổng | Database | Bảng/chức năng |
|---|---:|---|---|
| gateway-service | 8080 | Không có database nghiệp vụ | Giao diện, định tuyến, JWT và phân quyền |
| auth-service | 8081 | qldatn_auth | TAIKHOAN, đăng nhập và tra cứu sinh viên |
| detai-service | 8082 | qldatn_detai | DETAI, quản lý đề tài |
| dangky-service | 8083 | qldatn_dangky | DANGKY, đăng ký và chấm điểm |

Database chung `qldatn` cũ không còn được sử dụng. Mỗi service thao tác database riêng; Đăng ký gọi REST sang Auth/Đề tài để kiểm tra mã và bổ sung tên vào response. Không có khóa ngoại chéo database.

Ba datasource nằm trong `src/main/resources/application.properties` của từng service dữ liệu. Cấu hình local hiện dùng MySQL `root` / `123456`. Nếu tài khoản MySQL khác, sửa cấu hình tương ứng.

## Nạp dữ liệu

Database đã được tạo/nạp trên máy hiện tại. Trên máy khác, mở `database/qldatn.sql` bằng MySQL Workbench và Execute, hoặc dùng lệnh SOURCE trong MySQL client. Script tạo ba database mới, không xóa và không ghi đè các bản ghi đã có cùng khóa.

SQL hiện có 5 tài khoản, 3 đề tài và 2 đăng ký. ID đề tài là số BIGINT, ví dụ `3`, không phải chuỗi `DT03`.

| Tài khoản | Quyền | Mật khẩu mẫu |
|---|---|---|
| admin | ADMIN | 123456 |
| gv001 | GIANGVIEN | 123456 |
| SV001, SV002, SV003 | SINHVIEN | 123456 |

SV001 đã có đề tài 1 và điểm 8.55 (Giỏi). SV002 đã có đề tài 2, chưa chấm điểm. SV003 và đề tài 3 còn trống trong dữ liệu mẫu để demo đăng ký.

## RabbitMQ

Để demo gửi/nhận thông điệp, cần RabbitMQ tại localhost:5672. Nếu đã cài Docker Desktop, có thể chạy:

```text
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:4-management
```

Nếu container đã tồn tại và đang dừng, dùng `docker start rabbitmq`. Khi broker chưa chạy, launcher thông báo và tắt consumer RabbitMQ cho lần chạy đó; phần API/database vẫn hoạt động. Khi tạo đăng ký, producer vẫn thử gửi và có thể ghi log lỗi kết nối. Đây không phải chế độ để chứng minh messaging thành công.

## API qua Gateway

Giao diện: http://localhost:8080/

Swagger UI: http://localhost:8080/swagger-ui.html

| Method | Đường dẫn | Công dụng |
|---|---|---|
| POST | /api/auth/login | Đăng nhập, nhận JWT |
| GET | /api/auth/sinhvien | Danh sách sinh viên, dành cho Admin/Giảng viên |
| GET | /api/auth/sinhvien/{maSV} | Tra cứu một sinh viên |
| GET | /api/detai | Danh sách đề tài |
| GET | /api/detai/{id} | Chi tiết đề tài |
| POST/PUT/DELETE | /api/detai, /api/detai/{id} | Quản lý đề tài, dành cho Admin/Giảng viên |
| GET | /api/dangky | Danh sách đăng ký có thông tin tổng hợp |
| POST | /api/dangky | Tạo đăng ký |
| PUT | /api/dangky/{id} | Đổi thông tin đăng ký chưa chấm điểm |
| PUT | /api/dangky/{id}/diem | Chấm điểm, dành cho Admin/Giảng viên |
| DELETE | /api/dangky/{id} | Hủy đăng ký chưa chấm điểm, dành cho Admin/Giảng viên |

Body tạo đăng ký:

```json
{"maSV":"SV003","maDT":3}
```

Body chấm điểm:

```json
{"diemGVHD":8,"diemPhanBien":7.5,"diemHoiDong":9}
```

Điểm tổng kết = GVHD × 30% + phản biện × 30% + hội đồng × 40%, làm tròn hai chữ số. Với ví dụ trên, kết quả là 8.25, xếp loại Giỏi. Mỗi sinh viên/đề tài chỉ có một đăng ký. Đăng ký đã chấm điểm không được đổi hoặc hủy.

## Tài liệu chi tiết

Xem `docs/BAO_CAO_DATABASE_VA_HUONG_DAN_DEMO.md` để đọc báo cáo sửa database, giải thích code theo slide và kịch bản trình diễn.
