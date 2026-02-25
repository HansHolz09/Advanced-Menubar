#!/usr/bin/env bash
set -euo pipefail

python3 tools/dump_menu_localizations.py
python3 tools/generate_language_enum.py \
  --out advanced-menubar/src/commonMain/kotlin/dev.hansholz.advancedmenubar/MenubarLanguage.kt