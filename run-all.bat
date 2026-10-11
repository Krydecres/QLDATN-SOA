@echo off
REM Chay 3 dich vu doc lap, moi dich vu 1 cua so rieng, roi mo giao dien client
start "sinhvien-service :8081" cmd /k "cd /d %~dp0sinhvien-service && .\mvnw.cmd spring-boot:run"
start "detai-service :8082"    cmd /k "cd /d %~dp0detai-service && .\mvnw.cmd spring-boot:run"
start "dangky-service :8083"   cmd /k "cd /d %~dp0dangky-service && .\mvnw.cmd spring-boot:run"
echo Dang khoi dong 3 service, cho khoang 20 giay...
timeout /t 20 >nul
start "" "%~dp0client\index.html"
