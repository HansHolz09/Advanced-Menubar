import Foundation

final class SystemMenuTitleLocalizer {
    struct Hit {
        let bundle: Bundle
        let table: String?
        let key: String
    }

    private var hitsByEnglishTitle: [String: Hit] = [:]
    private var loadedForLanguage: String?

    func localize(_ englishTitle: String, preferredLocalizations: [String]) -> String {
        let lang = preferredLocalizations.first ?? "en"
        if loadedForLanguage != lang {
            hitsByEnglishTitle = buildHitIndex()
            loadedForLanguage = lang
        }

        for v in variants(of: englishTitle) {
            if let hit = hitsByEnglishTitle[v] {
                return localized(hit: hit, value: englishTitle, preferredLocalizations: preferredLocalizations)
            }
        }

        return englishTitle
    }

    private func buildHitIndex() -> [String: Hit] {
        // Targets to fix
        let targets = [
            "Print…",
            "Page Setup…",
            "Paste and Match Style",
            "Hide Toolbar",
            "Enter Full Screen",
            "Exit Full Screen",
            "Hide Sidebar",
            "Show Tab Bar",
            "Hide Tab Bar",
            "Close Window",
            "Minimize All",
            "Show Next Tab",
            "Show Previous Tab",
            "Merge All Windows",
            "Move Tab to New Window"
        ].flatMap { variants(of: $0) }

        var hits: [String: Hit] = [:]

        let candidates = (Bundle.allFrameworks + Bundle.allBundles)

        for bundle in candidates {
            indexBundleLoctables(bundle: bundle, englishTargets: targets, into: &hits)
            if hits.count >= targets.count { break }
        }

        return hits
    }

    private func indexBundleLoctables(bundle: Bundle, englishTargets: [String], into hits: inout [String: Hit]) {
        let loctables = bundle.urls(forResourcesWithExtension: "loctable", subdirectory: nil) ?? []
        if loctables.isEmpty { return }

        for url in loctables {
            guard let data = try? Data(contentsOf: url) else { continue }
            guard
                let plist = try? PropertyListSerialization.propertyList(from: data, options: [], format: nil),
                let top = plist as? [String: Any]
            else { continue }

            let tableName = url.deletingPathExtension().lastPathComponent
            let table: String? = (tableName == "Localizable") ? nil : tableName

            guard let enDict = bestLocaleDict(top, preferred: ["en", "en_US", "en-GB", "Base"]) else { continue }

            for (kAny, vAny) in enDict {
                guard let key = kAny as? String, let value = vAny as? String else { continue }
                guard englishTargets.contains(value) else { continue }

                if hits[value] == nil {
                    hits[value] = Hit(bundle: bundle, table: table, key: key)
                }
            }
        }
    }

    private func localized(hit: Hit, value: String, preferredLocalizations: [String]) -> String {
        if #available(macOS 15.4, *) {
            return hit.bundle.localizedString(forKey: hit.key, value: value, table: hit.table, localizations: preferredLocalizations.map(Locale.Language.init(identifier:)))
        } else {
            return hit.bundle.localizedString(forKey: hit.key, value: value, table: hit.table)
        }
    }

    private func bestLocaleDict(_ top: [String: Any], preferred: [String]) -> [AnyHashable: Any]? {
        func get(_ loc: String) -> [AnyHashable: Any]? {
            if let d = top[loc] as? [AnyHashable: Any] { return d }
            let dash = loc.replacingOccurrences(of: "_", with: "-")
            if let d = top[dash] as? [AnyHashable: Any] { return d }
            let underscore = loc.replacingOccurrences(of: "-", with: "_")
            if let d = top[underscore] as? [AnyHashable: Any] { return d }
            return nil
        }

        for p in preferred {
            if let d = get(p) { return d }
            if let base = p.split(separator: "-").first, let d = get(String(base)) { return d }
            if let base = p.split(separator: "_").first, let d = get(String(base)) { return d }
        }

        for (_, v) in top {
            if let d = v as? [AnyHashable: Any] { return d }
        }
        return nil
    }

    private func variants(of s: String) -> [String] {
        var out: [String] = []
        func add(_ x: String) { if !out.contains(x) { out.append(x) } }

        add(s)
        add(s.replacingOccurrences(of: "...", with: "…"))
        add(s.replacingOccurrences(of: "…", with: "..."))
        add(s.replacingOccurrences(of: " …", with: "…"))
        add(s.replacingOccurrences(of: "…", with: " …"))
        return out
    }
}

private extension Set where Element == String {
    func flatMap(_ transform: (String) -> [String]) -> Set<String> {
        var s = Set<String>()
        for e in self { for v in transform(e) { s.insert(v) } }
        return s
    }
}
