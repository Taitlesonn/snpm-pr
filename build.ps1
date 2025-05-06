#!/usr/bin/env pwsh

# Przykładowy skrypt PowerShell do budowania projektu Maven i tworzenia dystrybucji
# Zapisz go jako build-and-package.ps1 i uruchom w PowerShell (Win/Linux z PowerShell Core).

# Ustawienie zatrzymania na błędach
$ErrorActionPreference = 'Stop'

# --- KONFIGURACJA ---
# Nazwa modułu/artifactId oraz wersja (dopasuj do swojego pom.xml)
$ARTIFACT_ID = 'snmp-pr'
$VERSION     = '1.1'

# Ścieżka do głównego katalogu skryptu
$ProjectRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition

# Katalog wyjściowy
$OutDir = Join-Path $ProjectRoot 'out'

# --- KROKI SKRYPTU ---

Write-Host '1. Czyste budowanie projektu Maven...'
Set-Location $ProjectRoot
mvn clean package

Write-Host '2. Tworzę katalog out/ (jeśli nie istnieje)...'
if (-not (Test-Path $OutDir)) {
    New-Item -ItemType Directory -Path $OutDir | Out-Null
}

Write-Host '3. Kopiuję zbudowany JAR do out/...'
$JarSrc = Join-Path $ProjectRoot "target\$ARTIFACT_ID-$VERSION.jar"
if (-not (Test-Path $JarSrc)) {
    Write-Error "Nie znaleziono pliku JAR: $JarSrc"
    exit 1
}
Copy-Item -Path $JarSrc -Destination $OutDir -Force

Write-Host '4. Kopiuję katalog src/ wraz z SQL, LOG i JSON...'
$SourceDir = Join-Path $ProjectRoot 'src'
$DestDir   = Join-Path $OutDir      'src'

# Użycie robocopy do zachowania struktury katalogów:
#   /E - kopiuj podkatalogi, także puste
#   *.sql, *.log, *.json - tylko te pliki
robocopy $SourceDir $DestDir *.sql *.log *.json /E | Out-Null

Write-Host "✅ Gotowe! Wyniki znajdują się w $OutDir/"
