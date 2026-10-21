@echo off
setlocal
REM Windows: nhap dup file nay. Khong chay dong thoi voi run.sh.
powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\start-demo.ps1" %*
exit /b %errorlevel%
