# Apaga los contenedores de Vínculo. Los datos se conservan en el volumen de la base de datos.
$raiz = Split-Path -Parent $PSScriptRoot
Push-Location $raiz
docker compose down
Pop-Location
