#!/usr/bin/env bash
set -Eeuo pipefail
# Give each service its own process group so cleanup also stops its Java child.
set -m
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_DIR="$SCRIPT_DIR/.demo-logs"
services=(auth-service detai-service dangky-service gateway-service)
ports=(8081 8082 8083 8080)
paths=(/api/auth/v3/api-docs /api/detai/v3/api-docs /api/dangky/v3/api-docs /)
databases=(qldatn_auth qldatn_detai qldatn_dangky)
pids=()
ready=(0 0 0 0)

cleanup() {
    trap - EXIT INT TERM
    for pid in "${pids[@]}"; do kill -TERM -- "-$pid" 2>/dev/null || true; done
    if ((${#pids[@]} > 0)); then
        sleep 2
        for pid in "${pids[@]}"; do
            kill -KILL -- "-$pid" 2>/dev/null || true
            wait "$pid" 2>/dev/null || true
        done
        echo 'Da dung cac service do script nay khoi dong.'
    fi
}
trap cleanup EXIT
trap 'exit 130' INT
trap 'exit 143' TERM
tcp_open() { (echo > "/dev/tcp/127.0.0.1/$1") >/dev/null 2>&1; }
fail() { echo "$*" >&2; exit 1; }

case "$(uname -s)" in
    MINGW*|MSYS*|CYGWIN*) fail 'Tren Windows, nhap dup run-all.bat. Chi chay mot script.' ;;
esac
for tool in java javac curl; do command -v "$tool" >/dev/null || fail "Khong tim thay $tool. Can JDK 21 va curl."; done
tcp_open 3306 || fail 'MySQL chua chay tai localhost:3306.'
for i in "${!services[@]}"; do
    [[ -f "$SCRIPT_DIR/${services[$i]}/mvnw" ]] || fail "Khong tim thay ${services[$i]}/mvnw."
    if tcp_open "${ports[$i]}"; then fail "Cong ${ports[$i]} dang duoc su dung. Khong chay dong thoi hai script."; fi
    if ((i < 3)); then
        properties="$SCRIPT_DIR/${services[$i]}/src/main/resources/application.properties"
        grep -Fq "spring.datasource.url=jdbc:mysql://localhost:3306/${databases[$i]}?" "$properties" || fail "Datasource ${services[$i]} phai dung ${databases[$i]}."
    fi
done
mkdir -p "$LOG_DIR"
rabbit_available=0
if tcp_open 5672; then rabbit_available=1; else
    echo 'RabbitMQ chua chay: demo API/database van chay; chua the demo gui/nhan thong diep.'
fi
echo 'Database: qldatn_auth, qldatn_detai, qldatn_dangky.'
for i in "${!services[@]}"; do
    args=(spring-boot:run)
    if [[ "${services[$i]}" == dangky-service && "$rabbit_available" == 0 ]]; then
        args+=('-Dspring-boot.run.arguments=--spring.rabbitmq.listener.simple.auto-startup=false')
    fi
    (cd "$SCRIPT_DIR/${services[$i]}" && exec bash ./mvnw "${args[@]}") >"$LOG_DIR/${services[$i]}.log" 2>&1 &
    pids+=("$!")
    echo "Dang khoi dong ${services[$i]} :${ports[$i]}..."
done
deadline=$((SECONDS + 240))
while :; do
    ready_count=0
    for i in "${!services[@]}"; do
        kill -0 "${pids[$i]}" 2>/dev/null || fail "${services[$i]} da dung. Xem log trong $LOG_DIR."
        if [[ "${ready[$i]}" == 0 ]] && curl -fsS --max-time 2 -o /dev/null "http://localhost:${ports[$i]}${paths[$i]}" 2>/dev/null; then
            ready[$i]=1
            echo "${services[$i]} da san sang."
        fi
        ready_count=$((ready_count + ready[$i]))
    done
    ((ready_count == 4)) && break
    ((SECONDS < deadline)) || fail "Qua thoi gian cho khoi dong. Xem log trong $LOG_DIR."
    sleep 1
done
echo 'Demo da san sang: http://localhost:8080/'
if command -v xdg-open >/dev/null; then xdg-open 'http://localhost:8080/' >/dev/null 2>&1 || true
elif command -v open >/dev/null; then open 'http://localhost:8080/' || true; fi
echo 'Nhan Enter de dung ca bon service (hoac Ctrl+C).'
read -r _ || true
