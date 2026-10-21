#!/usr/bin/env bash

# Lấy thư mục chứa file script này
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 1. Khởi động API Gateway (nếu dự án dùng gateway làm cổng chính)
echo "Đang khởi động gateway-service..."
(cd "$SCRIPT_DIR/gateway-service" && ./mvnw spring-boot:run) &

# 2. Khởi động các Microservices
echo "Đang khởi động sinhvien-service :8081..."
(cd "$SCRIPT_DIR/sinhvien-service" && ./mvnw spring-boot:run) &

echo "Đang khởi động detai-service :8082..."
(cd "$SCRIPT_DIR/detai-service" && ./mvnw spring-boot:run) &

echo "Đang khởi động dangky-service :8083..."
(cd "$SCRIPT_DIR/dangky-service" && ./mvnw spring-boot:run) &

# 3. Khởi động client-service (nếu là Spring Boot App)
echo "Đang khởi động client-service..."
(cd "$SCRIPT_DIR/client-service" && ./mvnw spring-boot:run) &

echo "Đang khởi động các service, chờ khoảng 20 giây..."
sleep 20

# 4. Mở giao diện Client HTML (nếu dùng client tĩnh trong thư mục client)
if [ -f "$SCRIPT_DIR/client/index.html" ]; then
    xdg-open "$SCRIPT_DIR/client/index.html"
fi