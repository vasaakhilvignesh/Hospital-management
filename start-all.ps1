# ============================================================
# Hospital Management System - Startup Script
# Usage: .\start-all.ps1
# The script will prompt securely for your MySQL root password.
# ============================================================

param(
    [string]$MysqlUser = "root",
    [switch]$NoWait
)

$BACKEND   = "$PSScriptRoot\backend"
$MYSQL_BIN = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
if (-not (Test-Path $MYSQL_BIN)) {
    $cmd = Get-Command mysql.exe -ErrorAction SilentlyContinue
    if ($cmd) { $MYSQL_BIN = $cmd.Source }
}

# --- Securely collect MySQL password at runtime ---
if (-not $env:MYSQL_PASSWORD) {
    $envFile = Join-Path $PSScriptRoot ".env"
    $envExample = Join-Path $PSScriptRoot ".env.example"
    $candidatePass = $null
    if (Test-Path $envFile) {
        $line = Get-Content $envFile | Where-Object { $_ -match "^MYSQL_PASSWORD=(.*)$" }
        if ($line -match "^MYSQL_PASSWORD=(.*)$") { $candidatePass = $matches[1].Trim() }
    }
    if (-not $candidatePass -and (Test-Path $envExample)) {
        $line = Get-Content $envExample | Where-Object { $_ -match "^MYSQL_PASSWORD=(.*)$" }
        if ($line -match "^MYSQL_PASSWORD=(.*)$") { $candidatePass = $matches[1].Trim() }
    }
    if ($candidatePass) {
        $testOut = & $MYSQL_BIN -u $MysqlUser --password="$candidatePass" -e "SELECT 1;" 2>&1
        if ($LASTEXITCODE -eq 0) {
            $dbPass = $candidatePass
        }
    }
    if (-not $dbPass) {
        $securePass = Read-Host "Enter MySQL root password" -AsSecureString
        $BSTR       = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePass)
        $dbPass     = [Runtime.InteropServices.Marshal]::PtrToStringAuto($BSTR)
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($BSTR)
    }
} else {
    $dbPass = $env:MYSQL_PASSWORD
}

# --- Verify credential once before doing anything ---
Write-Host "Verifying MySQL credentials..." -ForegroundColor Cyan
$verifyOutput = & $MYSQL_BIN -u $MysqlUser --password="$dbPass" -e "SELECT 1;" 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "The supplied MySQL credentials are invalid." -ForegroundColor Red
    Write-Host "Aborting startup." -ForegroundColor Red
    exit 1
}
Write-Host "MySQL credentials verified." -ForegroundColor Green

# --- Export as environment variable for child JVM processes ---
$env:MYSQL_PASSWORD = $dbPass
$env:MYSQL_USERNAME = $MysqlUser
$env:MYSQL_HOST     = "localhost"
$env:MYSQL_PORT     = "3306"
$env:EUREKA_SERVER_URL = "http://localhost:8761/eureka/"
$env:EUREKA_INSTANCE_IP_ADDRESS = "127.0.0.1"

# Clear plaintext from memory as soon as env var is set
$dbPass = $null

# --- Helper: find the executable JAR for a service ---
function Get-ServiceJar {
    param([string]$ServiceName)
    $targetDir = Join-Path $BACKEND "$ServiceName\target"
    if (-not (Test-Path $targetDir)) { return $null }
    $jar = Get-ChildItem $targetDir -Filter "*.jar" |
           Where-Object { $_.Name -notlike "*original*" -and $_.Name -notlike "*sources*" } |
           Sort-Object LastWriteTime -Descending |
           Select-Object -First 1
    return $jar.FullName
}

# --- Helper: launch a service in background ---
function Start-AppService {
    param([string]$ServiceName, [int]$Port)
    $conn = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
    if ($conn) {
        Write-Host "  $ServiceName is already running on port $Port." -ForegroundColor Green
        return $null
    }
    $jar = Get-ServiceJar $ServiceName
    if (-not $jar) {
        Write-Host "  [ERROR] JAR not found for $ServiceName. Run build-all.ps1 first." -ForegroundColor Red
        return $null
    }
    $logOut = Join-Path $BACKEND "$ServiceName\service.log"
    $logErr = Join-Path $BACKEND "$ServiceName\service-err.log"
    Write-Host "  Starting $ServiceName (port $Port) ..." -ForegroundColor Yellow
    $proc = Start-Process -FilePath "java" `
        -ArgumentList "-jar `"$jar`"" `
        -RedirectStandardOutput $logOut `
        -RedirectStandardError  $logErr `
        -PassThru -NoNewWindow
    return $proc
}

# --- Helper: poll /actuator/health until UP or timeout ---
function Wait-ForHealth {
    param([string]$ServiceName, [int]$Port, [int]$TimeoutSec = 120)
    $url     = "http://localhost:$Port/actuator/health"
    $elapsed = 0
    Write-Host "  Waiting for $ServiceName to be healthy" -ForegroundColor Cyan -NoNewline
    while ($elapsed -lt $TimeoutSec) {
        Start-Sleep -Seconds 3
        $elapsed += 3
        try {
            $r = Invoke-WebRequest -Uri $url -TimeoutSec 4 -UseBasicParsing -ErrorAction Stop
            if ($r.StatusCode -eq 200) {
                Write-Host "  UP  ($elapsed s)" -ForegroundColor Green
                return $true
            }
        } catch { }
        Write-Host "." -NoNewline
    }
    Write-Host "  TIMEOUT after ${TimeoutSec}s" -ForegroundColor Red
    return $false
}

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  Hospital Management System -- Starting All Services" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

# Services in dependency order: Infrastructure first, then Microservices
$services = @(
    @{ Name = "discovery-server";     Port = 8761 },
    @{ Name = "config-server";        Port = 8888 },
    @{ Name = "api-gateway";          Port = 8080 },
    @{ Name = "auth-service";         Port = 8081 },
    @{ Name = "patient-service";      Port = 8082 },
    @{ Name = "doctor-service";       Port = 8083 },
    @{ Name = "appointment-service";  Port = 8084 },
    @{ Name = "pharmacy-service";     Port = 8085 },
    @{ Name = "billing-service";      Port = 8086 },
    @{ Name = "report-service";       Port = 8087 },
    @{ Name = "notification-service"; Port = 8088 }
)

foreach ($svc in $services) {
    Write-Host ""
    Write-Host "--- $($svc.Name) ---" -ForegroundColor Magenta
    Start-AppService -ServiceName $svc.Name -Port $svc.Port | Out-Null
    Wait-ForHealth   -ServiceName $svc.Name -Port $svc.Port | Out-Null
}

Write-Host ""
Write-Host "============================================================" -ForegroundColor Green
Write-Host "  All services are up." -ForegroundColor Green
Write-Host "  Eureka Dashboard : http://localhost:8761" -ForegroundColor Cyan
Write-Host "  API Gateway      : http://localhost:8080" -ForegroundColor Cyan
Write-Host "  Start frontend   : cd frontend ; npm run dev" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Green

if (-not $NoWait) {
    while ($true) {
        Start-Sleep -Seconds 3600
    }
}
