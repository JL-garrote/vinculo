# Arranca Vínculo: Docker Desktop (si hace falta), los contenedores y la app en su propia ventana.
$ErrorActionPreference = 'Stop'
$raiz = Split-Path -Parent $PSScriptRoot
$url = 'http://localhost:3000'
$Host.UI.RawUI.WindowTitle = 'Vínculo'

function Fallo($mensaje) {
    Write-Host "`n$mensaje" -ForegroundColor Red
    Read-Host 'Pulsa Enter para cerrar'
    exit 1
}

function DockerListo {
    docker info *> $null
    return $LASTEXITCODE -eq 0
}

Write-Host 'Vínculo · arrancando...' -ForegroundColor Cyan

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Fallo 'No se encuentra Docker. Instala Docker Desktop y vuelve a intentarlo.'
}

if (-not (DockerListo)) {
    Write-Host '· Abriendo Docker Desktop...'
    $dockerDesktop = @(
        "$env:LOCALAPPDATA\Programs\DockerDesktop\Docker Desktop.exe",
        "$env:ProgramFiles\Docker\Docker\Docker Desktop.exe"
    ) | Where-Object { Test-Path $_ } | Select-Object -First 1
    if (-not $dockerDesktop) { Fallo 'No se encuentra Docker Desktop.' }
    Start-Process $dockerDesktop

    $limite = (Get-Date).AddMinutes(3)
    while (-not (DockerListo)) {
        if ((Get-Date) -gt $limite) { Fallo 'Docker Desktop no ha arrancado en 3 minutos.' }
        Start-Sleep -Seconds 2
    }
}

Write-Host '· Levantando contenedores (la primera vez tarda unos minutos)...'
Push-Location $raiz
try {
    # --build: si el código ha cambiado se reconstruye; si no, Docker reutiliza la caché y tarda segundos
    docker compose up -d --build
    if ($LASTEXITCODE -ne 0) { Fallo 'docker compose ha fallado. Revisa los mensajes de arriba.' }
} finally {
    Pop-Location
}

Write-Host '· Esperando a que la aplicación responda...'
$limite = (Get-Date).AddMinutes(3)
while ($true) {
    try {
        Invoke-WebRequest "$url/api/controller/perfil" -UseBasicParsing -TimeoutSec 3 | Out-Null
        break
    } catch {
        if ((Get-Date) -gt $limite) { Fallo "La aplicación no responde en $url." }
        Start-Sleep -Seconds 2
    }
}

# Ventana propia sin pestañas ni barra de direcciones; si no hay Edge, el navegador por defecto
$edge = @(
    "${env:ProgramFiles(x86)}\Microsoft\Edge\Application\msedge.exe",
    "$env:ProgramFiles\Microsoft\Edge\Application\msedge.exe"
) | Where-Object { Test-Path $_ } | Select-Object -First 1
if ($edge) { Start-Process $edge "--app=$url" } else { Start-Process $url }
