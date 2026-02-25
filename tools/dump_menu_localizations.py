#!/usr/bin/env python3
import argparse
import os
import re
import subprocess
from pathlib import Path
import plistlib

DEFAULT_RES_ROOT = Path("advanced-menubar/src/commonMain/composeResources")
DEFAULT_BASELINE = DEFAULT_RES_ROOT / "values/strings.xml"

ANCHORS = {
    "Print…",
    "Page Setup…",
    "Enter Full Screen",
    "Show Tab Bar",
    "Paste and Match Style",
    "Revert To Saved",
}

LANG_RE = re.compile(r"^[a-z]{2,3}$")  # language-only
BLACKLIST = {
    "base", "root", "und", "mul", "zxx", "mis", "locprovenance"
}

def run(cmd: list[str]) -> None:
    print(">", " ".join(cmd))
    subprocess.run(cmd, check=True)

def build_menudumper(xcodeproj: Path, scheme: str, derived_data: Path) -> Path:
    run([
        "xcodebuild",
        "-project", str(xcodeproj),
        "-scheme", scheme,
        "-configuration", "Release",
        "-destination", "platform=macOS",
        "-derivedDataPath", str(derived_data),
        "CODE_SIGNING_ALLOWED=NO",
        "build",
    ])
    exe = derived_data / "Build/Products/Release/MenuDumper.app/Contents/MacOS/MenuDumper"
    if not exe.exists():
        raise SystemExit(f"MenuDumper not found at {exe}")
    return exe

def find_languages_from_loctables() -> set[str]:
    roots = [
        Path("/System/Library/Frameworks"),
        Path("/System/Library/PrivateFrameworks"),
    ]

    langs: set[str] = set()

    def best_en_dict(top: dict) -> dict | None:
        for k in ("en", "en_US", "en-US", "Base"):
            d = top.get(k)
            if isinstance(d, dict):
                return d
        for v in top.values():
            if isinstance(v, dict):
                return v
        return None

    for root in roots:
        if not root.exists():
            continue
        for p in root.rglob("*.loctable"):
            try:
                top = plistlib.loads(p.read_bytes())
                if not isinstance(top, dict):
                    continue
                en = best_en_dict(top)
                if not isinstance(en, dict):
                    continue

                values = {v for v in en.values() if isinstance(v, str)}
                if not (values & ANCHORS):
                    continue

                for loc_key, loc_dict in top.items():
                    if not isinstance(loc_dict, dict):
                        continue
                    # normalize "de-DE" / "de_DE" -> "de"
                    k = str(loc_key).strip().replace("_", "-")
                    lang = k.split("-")[0].lower()

                    if lang in BLACKLIST:
                        continue
                    if LANG_RE.fullmatch(lang):
                        langs.add(lang)
            except Exception:
                continue

    # Always include English baseline
    langs.add("en")
    return langs

def write_strings_for_language(menudumper: Path, baseline: Path, out_xml: Path, lang: str, dry_run: bool) -> None:
    out_xml.parent.mkdir(parents=True, exist_ok=True)
    cmd = [
        str(menudumper),
        "--baseline", str(baseline),
        "--out", str(out_xml),
        "-AppleLanguages", f"({lang})"
    ]
    if dry_run:
        print("[dry-run]", " ".join(cmd))
    else:
        run(cmd)

def main() -> None:
    ap = argparse.ArgumentParser()
    ap.add_argument("--menudumper", type=Path, default=None,
                    help="Path to MenuDumper executable; if omitted, it will be built.")
    ap.add_argument("--xcodeproj", type=Path, default=Path("tools/MenuDumper/MenuDumper.xcodeproj"))
    ap.add_argument("--scheme", type=str, default="MenuDumper")
    ap.add_argument("--derived-data", type=Path, default=Path("build/DerivedData"))
    ap.add_argument("--res-root", type=Path, default=DEFAULT_RES_ROOT)
    ap.add_argument("--baseline", type=Path, default=DEFAULT_BASELINE)
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()

    repo = Path.cwd()
    res_root = (repo / args.res_root).resolve()
    baseline = (repo / args.baseline).resolve()
    if not baseline.exists():
        raise SystemExit(f"Baseline not found: {baseline}")

    menudumper = (repo / args.menudumper).resolve() if args.menudumper else build_menudumper(
        xcodeproj=(repo / args.xcodeproj).resolve(),
        scheme=args.scheme,
        derived_data=(repo / args.derived_data).resolve()
    )

    langs = sorted(find_languages_from_loctables())
    print(f"Languages: {', '.join(langs)}")

    bad = res_root / "values-locprovenance"
    if bad.exists():
        print(f"Removing invalid folder: {bad}")
        if not args.dry_run:
            for child in bad.rglob("*"):
                if child.is_file():
                    child.unlink()
            for child in sorted(bad.rglob("*"), reverse=True):
                if child.is_dir():
                    child.rmdir()
            bad.rmdir()

    for lang in langs:
        if lang == "en":
            out_xml = res_root / "values/strings.xml"
        else:
            out_xml = res_root / f"values-{lang}/strings.xml"

        write_strings_for_language(menudumper, baseline, out_xml, lang, args.dry_run)

    print("Done.")

if __name__ == "__main__":
    main()