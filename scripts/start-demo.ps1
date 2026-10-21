[CmdletBinding()]
param(
    [switch]$NoBrowser,
    [switch]$StopAfterReady,
    [ValidateRange(10, 900)][int]$StartupTimeoutSeconds = 240
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$logDirectory = Join-Path $projectRoot '.demo-logs'
$started = @()
$exitCode = 0
$services = @(
    @{ Name = 'auth-service'; Port = 8081; Path = '/api/auth/v3/api-docs'; Database = 'qldatn_auth' },
    @{ Name = 'detai-service'; Port = 8082; Path = '/api/detai/v3/api-docs'; Database = 'qldatn_detai' },
    @{ Name = 'dangky-service'; Port = 8083; Path = '/api/dangky/v3/api-docs'; Database = 'qldatn_dangky' },
    @{ Name = 'gateway-service'; Port = 8080; Path = '/'; Database = $null }
)

function Test-LocalPort([int]$Port) {
    $client = New-Object System.Net.Sockets.TcpClient
    try {
        $connection = $client.BeginConnect('127.0.0.1', $Port, $null, $null)
        if (-not $connection.AsyncWaitHandle.WaitOne(500)) { return $false }
        $client.EndConnect($connection)
        return $true
    } catch { return $false }
    finally { $client.Dispose() }
}

function Test-ServiceHttp([hashtable]$Service) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:$($Service.Port)$($Service.Path)" -UseBasicParsing -TimeoutSec 2
        return $response.StatusCode -eq 200
    } catch { return $false }
}

try {
    foreach ($tool in @('java', 'javac')) {
        if (-not (Get-Command $tool -ErrorAction SilentlyContinue)) {
            throw 'Can cai JDK 21 va them Java vao PATH truoc khi chay demo.'
        }
    }
    if (-not (Test-LocalPort 3306)) { throw 'MySQL chua chay tai localhost:3306. Hay bat MySQL roi mo lai run-all.bat.' }

    foreach ($service in $services) {
        $serviceDirectory = Join-Path $projectRoot $service.Name
        if (-not (Test-Path -LiteralPath (Join-Path $serviceDirectory 'mvnw.cmd'))) {
            throw "Khong tim thay Maven Wrapper trong $serviceDirectory."
        }
        if (Test-LocalPort $service.Port) {
            throw "Cong $($service.Port) dang duoc su dung. Hay dung lan demo truoc; chi chay mot trong hai script."
        }
        if ($service.Database) {
            $properties = Get-Content -LiteralPath (Join-Path $serviceDirectory 'src/main/resources/application.properties') -Raw
            $expectedUrl = "(?m)^spring\.datasource\.url=jdbc:mysql://localhost:3306/$($service.Database)\?"
            if ($properties -notmatch $expectedUrl) { throw "Datasource cua $($service.Name) phai dung database $($service.Database)." }
        }
    }

    New-Item -ItemType Directory -Path $logDirectory -Force | Out-Null
    $rabbitAvailable = Test-LocalPort 5672
    if (-not $rabbitAvailable) {
        Write-Host 'RabbitMQ chua chay: demo API/database van chay; chua the demo gui/nhan thong diep.' -ForegroundColor Yellow
    }
    Write-Host 'Database: qldatn_auth, qldatn_detai, qldatn_dangky.'
    foreach ($service in $services) {
        $arguments = '/d /c .\mvnw.cmd spring-boot:run'
        if ($service.Name -eq 'dangky-service' -and -not $rabbitAvailable) {
            $arguments += ' "-Dspring-boot.run.arguments=--spring.rabbitmq.listener.simple.auto-startup=false"'
        }
        $process = Start-Process -FilePath $env:ComSpec -ArgumentList $arguments `
            -WorkingDirectory (Join-Path $projectRoot $service.Name) -WindowStyle Hidden -PassThru `
            -RedirectStandardOutput (Join-Path $logDirectory "$($service.Name).log") `
            -RedirectStandardError (Join-Path $logDirectory "$($service.Name).error.log")
        $started += @{ Service = $service; Process = $process }
        Write-Host "Dang khoi dong $($service.Name) :$($service.Port)..."
    }

    $ready = @{}
    $deadline = [DateTime]::UtcNow.AddSeconds($StartupTimeoutSeconds)
    while ($ready.Count -lt $services.Count) {
        foreach ($entry in $started) {
            if ($entry.Process.HasExited) { throw "$($entry.Service.Name) da dung. Xem log trong $logDirectory." }
            if (-not $ready.ContainsKey($entry.Service.Name) -and (Test-ServiceHttp $entry.Service)) {
                $ready[$entry.Service.Name] = $true
                Write-Host "$($entry.Service.Name) da san sang." -ForegroundColor Green
            }
        }
        if ($ready.Count -eq $services.Count) { break }
        if ([DateTime]::UtcNow -ge $deadline) { throw "Qua thoi gian cho khoi dong. Xem log trong $logDirectory." }
        Start-Sleep -Seconds 1
    }

    Write-Host 'Demo da san sang: http://localhost:8080/' -ForegroundColor Green
    Write-Host "Log cua bon service: $logDirectory"
    if (-not $NoBrowser) { Start-Process 'http://localhost:8080/' | Out-Null }
    if (-not $StopAfterReady) {
        Read-Host 'Giu cua so nay khi demo. Nhan Enter tai day de dung ca bon service' | Out-Null
    }
} catch {
    Write-Host "Khong khoi dong duoc demo: $($_.Exception.Message)" -ForegroundColor Red
    $exitCode = 1
} finally {
    foreach ($entry in $started) {
        if (-not $entry.Process.HasExited) {
            & "$env:SystemRoot\System32\taskkill.exe" /PID $entry.Process.Id /T /F 2>&1 | Out-Null
        }
    }
    if ($started.Count -gt 0) { Write-Host 'Da dung cac service do script nay khoi dong.' }
}
if ($exitCode -ne 0 -and -not $StopAfterReady) { Read-Host 'Nhan Enter de dong cua so' | Out-Null }
exit $exitCode
