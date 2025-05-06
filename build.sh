#!/usr/bin/env bash
set -euo pipefail

# --- KONFIGURACJA ---
# Nazwa modułu/artifactId oraz wersja (dopasuj do swojego pom.xml)
ARTIFACT_ID="snmp-pr"
VERSION="1.1"
# Ścieżka do głównego katalogu projektu (jeśli uruchamiasz z innego miejsca)
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Katalog wyjściowy
OUT_DIR="${PROJECT_ROOT}/out"

# --- KROKI SKRYPTU ---

echo "1. Czyste budowanie projektu Maven..."
cd "${PROJECT_ROOT}"
mvn clean package

echo "2. Tworzę katalog out/ (jeśli nie istnieje)..."
mkdir -p "${OUT_DIR}"

echo "3. Kopiuję zbudowany JAR do out/ ..."
JAR_SRC="${PROJECT_ROOT}/target/${ARTIFACT_ID}-${VERSION}.jar"
if [[ ! -f "${JAR_SRC}" ]]; then
  echo "  ❌ Nie znaleziono pliku JAR: ${JAR_SRC}"
  exit 1
fi
cp "${JAR_SRC}" "${OUT_DIR}/"

echo "4. Kopiuję katalog src/ wraz z SQL, LOG i JSON..."
# Kopiujemy cały katalog src/, zachowując strukturę
rsync -av --include='*/' \
          --include='*.sql' \
          --include='*.log' \
          --include='*.json' \
          --exclude='*' \
          "${PROJECT_ROOT}/src/" \
          "${OUT_DIR}/src/"

echo "✅ Gotowe! Wyniki znajdują się w ${OUT_DIR}/"
