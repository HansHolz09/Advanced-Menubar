import AppKit
import Foundation

struct MenuDumpMetaEntry: Codable {
    let key: String
    let title: String

    let action: String?
    let tag: Int
    let keyEquivalent: String
    let modifierMaskRaw: UInt
    let hasSubmenu: Bool

    let sfSymbolNameCandidate: String?

    let menuPath: [String]
}

struct MenuDumpMetaFile: Codable {
    let createdAtISO8601: String
    let macOSVersion: String
    let appNameInMenuBar: String
    let entries: [MenuDumpMetaEntry]
}

private struct Options {
    var baselinePath: String?
    var outPath: String?
    var metaOutPath: String?
    var strict: Bool = false
    var printTree: Bool = false

    static func parse(_ argv: [String]) -> Options {
        var o = Options()
        var i = 1
        while i < argv.count {
            switch argv[i] {
            case "--baseline":
                if i + 1 < argv.count { o.baselinePath = argv[i+1]; i += 1 }
            case "--out":
                if i + 1 < argv.count { o.outPath = argv[i+1]; i += 1 }
            case "--meta-out":
                if i + 1 < argv.count { o.metaOutPath = argv[i+1]; i += 1 }
            case "--strict":
                o.strict = true
            case "--print-tree":
                o.printTree = true
            default:
                break
            }
            i += 1
        }
        return o
    }
}

enum MenuDumper {
    static func runWhenMenuIsReadyAndExit(maxAttempts: Int = 12, delaySeconds: Double = 0.25) {
        var attempts = 0

        func tick() {
            attempts += 1
            guard let mainMenu = NSApp.mainMenu, mainMenu.items.count >= 2 else {
                if attempts >= maxAttempts {
                    fputs("MenuDumper error: main menu not ready\n", stderr)
                    exit(1)
                }
                DispatchQueue.main.asyncAfter(deadline: .now() + delaySeconds) { tick() }
                return
            }

            do {
                let opts = Options.parse(CommandLine.arguments)
                if opts.printTree { printMenuTree(mainMenu) }

                let appName = mainMenu.items.first?.title ?? ProcessInfo.processInfo.processName
                let keys = (opts.baselinePath != nil) ? extractKeysFromStringsXML(atPath: opts.baselinePath!) : defaultKeys()

                let scopes = try MenuScopes(mainMenu: mainMenu)
                
                seedMissingStandardItems(scopes: scopes)

                let result = buildValuesAndMeta(keys: keys, scopes: scopes, appNameInMenuBar: appName)

                let missing = keys.filter { result.values[$0] == nil }
                if !missing.isEmpty {
                    let msg = "MenuDumper \(opts.strict ? "error" : "warning"): missing keys: \(missing.joined(separator: ", "))\n"
                    fputs(msg, stderr)
                    if opts.strict { exit(2) }
                }

                if let outPath = opts.outPath {
                    let xml: String
                    if let baseline = opts.baselinePath, FileManager.default.fileExists(atPath: baseline) {
                        let baselineContent = try String(contentsOfFile: baseline, encoding: .utf8)
                        xml = updateStringsXMLPreservingFormatting(baseline: baselineContent, newValues: result.values)
                    } else {
                        xml = generateStringsXML(keys: keys, values: result.values)
                    }
                    try writeTextFile(xml, to: outPath)
                    print("✅ Wrote \(outPath)")
                } else {
                    print(generateStringsXML(keys: keys, values: result.values))
                }

                if let metaOut = opts.metaOutPath {
                    let file = MenuDumpMetaFile(
                        createdAtISO8601: ISO8601DateFormatter().string(from: Date()),
                        macOSVersion: ProcessInfo.processInfo.operatingSystemVersionString,
                        appNameInMenuBar: appName,
                        entries: result.meta.values.sorted(by: { $0.key < $1.key })
                    )
                    try writeJSON(file, to: metaOut)
                }

                exit(0)
            } catch {
                fputs("MenuDumper error: \(error)\n", stderr)
                exit(1)
            }
        }

        tick()
    }
}

private struct MenuScopes {
    let mainMenu: NSMenu
    let appMenu: NSMenu
    let fileMenu: NSMenu
    let editMenu: NSMenu
    let formatMenu: NSMenu
    let viewMenu: NSMenu
    let windowMenu: NSMenu
    let helpMenu: NSMenu

    init(mainMenu: NSMenu) throws {
        self.mainMenu = mainMenu
        guard let appMenu = mainMenu.items.first?.submenu else { throw NSError(domain: "MenuDumper", code: 10) }
        self.appMenu = appMenu

        // Identify top level menus by content (actions inside)
        self.fileMenu = requireTopLevelSubmenu(mainMenu, containsAnyAction: ["newDocument:", "openDocument:"])
        self.editMenu = requireTopLevelSubmenu(mainMenu, containsAnyAction: ["undo:", "cut:", "copy:", "paste:"])
        self.formatMenu = requireTopLevelSubmenu(mainMenu, containsAnyAction: ["orderFrontFontPanel:", "addFontTrait:", "underline:"])
        self.viewMenu = requireTopLevelSubmenu(mainMenu, containsAnyAction: ["toggleToolbarShown:", "toggleSidebar:"])
        self.windowMenu = (NSApp.windowsMenu ?? requireTopLevelSubmenu(mainMenu, containsAnyAction: ["performMiniaturize:", "performZoom:"]))
        self.helpMenu = (NSApp.helpMenu ?? requireTopLevelSubmenu(mainMenu, containsAnyAction: ["showHelp:"]))
    }
}

private func requireTopLevelSubmenu(_ mainMenu: NSMenu, containsAnyAction actions: [String]) -> NSMenu {
    for item in mainMenu.items {
        guard let sub = item.submenu else { continue }
        if containsActionRecursively(sub, actions) { return sub }
    }
    return NSMenu()
}

private func containsActionRecursively(_ menu: NSMenu, _ actions: [String]) -> Bool {
    for item in menu.items {
        if let a = item.action.map({ NSStringFromSelector($0) }), actions.contains(a) {
            return true
        }
        if let sub = item.submenu, containsActionRecursively(sub, actions) {
            return true
        }
    }
    return false
}

private func preferredLocalizationsFromAppleLanguages() -> [String] {
    if let langs = UserDefaults.standard.array(forKey: "AppleLanguages") as? [String], !langs.isEmpty {
        return langs
    }
    if let langs = Locale.preferredLanguages as [String]?, !langs.isEmpty {
        return langs
    }
    return ["en"]
}

private func seedMissingStandardItems(scopes: MenuScopes) {
    let prefs = preferredLocalizationsFromAppleLanguages()
    let L = SystemMenuTitleLocalizer()

    func loc(_ english: String) -> String {
        L.localize(english, preferredLocalizations: prefs)
    }
    
    ensureItem(
        in: scopes.fileMenu,
        exists: { $0.actionName == "runPageLayout:" },
        make: {
            let i = NSMenuItem(title: loc("Page Setup…"),
                               action: NSSelectorFromString(("runPageLayout:")),
                               keyEquivalent: "p")
            i.keyEquivalentModifierMask = [.command, .shift]
            return i
        },
    )

    ensureItem(
        in: scopes.fileMenu,
        exists: { $0.actionName == "print:" || $0.actionName == "printDocument:" },
        make: {
            let i = NSMenuItem(title: loc("Print…"),
                               action: NSSelectorFromString(("print:")),
                               keyEquivalent: "p")
            i.keyEquivalentModifierMask = [.command]
            return i
        },
        insertAfterAction: "runPageLayout:"
    )

    ensureItem(
        in: scopes.editMenu,
        exists: { $0.actionName == "pasteAndMatchStyle:" || $0.title == loc("Paste and Match Style") },
        make: {
            let i = NSMenuItem(title: loc("Paste and Match Style"),
                               action: NSSelectorFromString(("pasteAndMatchStyle:")),
                               keyEquivalent: "v")
            i.keyEquivalentModifierMask = [.command, .option, .shift]
            return i
        },
        insertAfterAction: "paste:"
    )

    ensureItem(
        in: scopes.viewMenu,
        exists: { $0.actionName == "toggleFullScreen:" || $0.title == loc("Enter Full Screen") },
        make: {
            let i = NSMenuItem(title: loc("Enter Full Screen"),
                               action: NSSelectorFromString(("toggleFullScreen:")),
                               keyEquivalent: "f")
            i.keyEquivalentModifierMask = [.command, .control]
            return i
        },
        insertAfterAction: "runToolbarCustomizationPalette:"
    )

    ensureItem(
        in: scopes.viewMenu,
        exists: { $0.actionName == "toggleTabBar:" || $0.title == loc("Show Tab Bar") },
        make: {
            let i = NSMenuItem(title: loc("Show Tab Bar"),
                               action: NSSelectorFromString(("toggleTabBar:")),
                               keyEquivalent: "t")
            i.keyEquivalentModifierMask = [.command, .shift]
            return i
        },
        insertAfterAction: "toggleSidebar:"
    )

    ensureItem(
        in: scopes.windowMenu,
        exists: { $0.actionName == "performClose:" || $0.title == loc("Close Window") },
        make: {
            let i = NSMenuItem(title: loc("Close Window"),
                               action: NSSelectorFromString(("performClose:")),
                               keyEquivalent: "")
            return i
        },
        insertAtTop: true
    )

    ensureItem(
        in: scopes.windowMenu,
        exists: { $0.actionName == "miniaturizeAll:" || $0.title == loc("Minimize All") },
        make: {
            let i = NSMenuItem(title: loc("Minimize All"),
                               action: NSSelectorFromString(("miniaturizeAll:")),
                               keyEquivalent: "m")
            i.keyEquivalentModifierMask = [.command, .option]
            return i
        },
        insertAfterAction: "performMiniaturize:"
    )

    ensureItem(
        in: scopes.windowMenu,
        exists: { $0.actionName == "selectNextTab:" || $0.title == loc("Show Next Tab") },
        make: {
            let i = NSMenuItem(title: loc("Show Next Tab"),
                               action: NSSelectorFromString(("selectNextTab:")),
                               keyEquivalent: "\t")
            i.keyEquivalentModifierMask = [.control]
            return i
        },
        insertAfterAction: "arrangeInFront:"
    )

    ensureItem(
        in: scopes.windowMenu,
        exists: { $0.actionName == "selectPreviousTab:" || $0.title == loc("Show Previous Tab") },
        make: {
            let i = NSMenuItem(title: loc("Show Previous Tab"),
                               action: NSSelectorFromString(("selectPreviousTab:")),
                               keyEquivalent: "\t")
            i.keyEquivalentModifierMask = [.control, .shift]
            return i
        },
        insertAfterAction: "selectNextTab:"
    )

    ensureItem(
        in: scopes.windowMenu,
        exists: { $0.actionName == "mergeAllWindows:" || $0.title == loc("Merge All Windows") },
        make: {
            let i = NSMenuItem(title: loc("Merge All Windows"),
                               action: NSSelectorFromString(("mergeAllWindows:")),
                               keyEquivalent: "")
            return i
        },
        insertAfterAction: "selectPreviousTab:"
    )

    ensureItem(
        in: scopes.windowMenu,
        exists: { $0.actionName == "moveTabToNewWindow:" || $0.title == loc("Move Tab to New Window") },
        make: {
            let i = NSMenuItem(title: loc("Move Tab to New Window"),
                               action: NSSelectorFromString(("moveTabToNewWindow:")),
                               keyEquivalent: "")
            return i
        },
        insertAfterAction: "mergeAllWindows:"
    )
}

private func ensureItem(
    in menu: NSMenu,
    exists: (NSMenuItem) -> Bool,
    make: () -> NSMenuItem,
    insertAfterAction: String? = nil,
    insertAtTop: Bool = false
) {
    if findRecursive(in: menu, where: exists) != nil { return }

    let newItem = make()

    if insertAtTop {
        menu.insertItem(newItem, at: 0)
        return
    }

    if let after = insertAfterAction {
        if let idx = indexOfFirstRecursive(in: menu, where: { $0.actionName == after }) {
            menu.insertItem(newItem, at: min(idx + 1, menu.items.count))
            return
        }
    }

    menu.addItem(newItem)
}

private func findRecursive(in menu: NSMenu, where predicate: (NSMenuItem) -> Bool) -> NSMenuItem? {
    for item in menu.items {
        if predicate(item) { return item }
        if let sub = item.submenu, let found = findRecursive(in: sub, where: predicate) { return found }
    }
    return nil
}

private func indexOfFirstRecursive(in menu: NSMenu, where predicate: (NSMenuItem) -> Bool) -> Int? {
    for (idx, item) in menu.items.enumerated() {
        if predicate(item) { return idx }
    }
    return nil
}

private struct ValuesAndMeta {
    var values: [String: String]
    var meta: [String: MenuDumpMetaEntry]
}

private func buildValuesAndMeta(keys: [String], scopes: MenuScopes, appNameInMenuBar: String) -> ValuesAndMeta {
    var values: [String: String] = [:]
    var meta: [String: MenuDumpMetaEntry] = [:]

    func put(_ key: String, _ item: NSMenuItem?, path: [String]) {
        guard let item else { return }
        let normalized = normalizeAppNamePlaceholder(key: key, rawTitle: item.title, appNameInMenuBar: appNameInMenuBar)
        values[key] = normalized
        meta[key] = makeMetaEntry(key: key, title: normalized, item: item, path: path)
    }

    // Top-level titles
    put("file", findTopLevelItemTitle(for: scopes.fileMenu, in: scopes.mainMenu), path: [findTopLevelItemTitle(for: scopes.fileMenu, in: scopes.mainMenu)?.title ?? ""])
    put("edit", findTopLevelItemTitle(for: scopes.editMenu, in: scopes.mainMenu), path: [findTopLevelItemTitle(for: scopes.editMenu, in: scopes.mainMenu)?.title ?? ""])
    put("format", findTopLevelItemTitle(for: scopes.formatMenu, in: scopes.mainMenu), path: [findTopLevelItemTitle(for: scopes.formatMenu, in: scopes.mainMenu)?.title ?? ""])
    put("view", findTopLevelItemTitle(for: scopes.viewMenu, in: scopes.mainMenu), path: [findTopLevelItemTitle(for: scopes.viewMenu, in: scopes.mainMenu)?.title ?? ""])
    put("window", findTopLevelItemTitle(for: scopes.windowMenu, in: scopes.mainMenu), path: [findTopLevelItemTitle(for: scopes.windowMenu, in: scopes.mainMenu)?.title ?? ""])
    put("help", findTopLevelItemTitle(for: scopes.helpMenu, in: scopes.mainMenu), path: [findTopLevelItemTitle(for: scopes.helpMenu, in: scopes.mainMenu)?.title ?? ""])

    // App menu
    put("about", findFirst(scopes.appMenu, action: "orderFrontStandardAboutPanel:"), path: ["App"])
    put("settings", findByShortcut(scopes.appMenu, key: ",", mods: [.command]), path: ["App"])

    // Services
    if let services = NSApp.servicesMenu, let servicesItem = findItemPointingToSubmenu(scopes.appMenu, submenu: services) {
        put("services", servicesItem, path: ["App"])
    }

    put("hide", findFirst(scopes.appMenu, action: "hide:"), path: ["App"])
    put("hide_others", findFirst(scopes.appMenu, action: "hideOtherApplications:"), path: ["App"])
    put("show_all", findFirst(scopes.appMenu, action: "unhideAllApplications:"), path: ["App"])
    put("quit", findFirst(scopes.appMenu, action: "terminate:"), path: ["App"])

    // File
    put("file_new", findFirst(scopes.fileMenu, action: "newDocument:"), path: ["File"])
    put("file_open", findFirst(scopes.fileMenu, action: "openDocument:"), path: ["File"])

    if let openRecent = findContainerWithDirectChildAction(scopes.fileMenu, childAction: "clearRecentDocuments:") {
        put("file_open_recent", openRecent, path: ["File"])
        if let sub = openRecent.submenu {
            put("file_clear_recent", findFirst(sub, action: "clearRecentDocuments:"), path: ["File", openRecent.title])
        }
    }

    put("file_close", findByShortcut(scopes.fileMenu, key: "w", mods: [.command]) ?? findFirst(scopes.fileMenu, action: "performClose:"), path: ["File"])
    put("file_close_all", findByShortcut(scopes.fileMenu, key: "w", mods: [.command, .option]) ?? findFirst(scopes.fileMenu, action: "closeAll:"), path: ["File"])

    put("file_save", findFirst(scopes.fileMenu, action: "saveDocument:"), path: ["File"])
    put("file_save_as", findFirst(scopes.fileMenu, action: "saveDocumentAs:"), path: ["File"])
    put("file_duplicate", findFirst(scopes.fileMenu, action: "duplicateDocument:"), path: ["File"])
    put("file_rename", findFirst(scopes.fileMenu, action: "renameDocument:"), path: ["File"])
    put("file_move_to", findFirst(scopes.fileMenu, action: "moveDocument:"), path: ["File"])

    put("file_page_setup", findFirstAny(scopes.fileMenu, actions: ["runPageLayout:"]), path: ["File"])
    put("file_print", findFirstAny(scopes.fileMenu, actions: ["print:", "printDocument:"]), path: ["File"])

    // Edit main actions
    put("edit_undo", findFirst(scopes.editMenu, action: "undo:"), path: ["Edit"])
    put("edit_redo", findFirst(scopes.editMenu, action: "redo:"), path: ["Edit"])
    put("edit_cut", findFirst(scopes.editMenu, action: "cut:"), path: ["Edit"])
    put("edit_copy", findFirst(scopes.editMenu, action: "copy:"), path: ["Edit"])
    put("edit_paste", findFirst(scopes.editMenu, action: "paste:"), path: ["Edit"])
    put("edit_delete", findFirst(scopes.editMenu, action: "delete:"), path: ["Edit"])
    put("edit_select_all", findFirst(scopes.editMenu, action: "selectAll:"), path: ["Edit"])
    
    put("paste_and_match_style",
        findFirstAny(scopes.editMenu, actions: ["pasteAndMatchStyle:", "pasteAsPlainText:"])
        ?? findByShortcut(scopes.editMenu, key: "v", mods: [.command, .option, .shift]),
        path: ["Edit"]
    )

    // Find
    if let findMenu = findContainerWithDirectChild(scopes.editMenu, predicate: { item in
        item.actionName == "performFindPanelAction:" && item.tag == 1
    }) {
        put("find", findMenu, path: ["Edit"])
        if let sub = findMenu.submenu {
            put("find_dots", findFindAction(sub, tag: 1), path: ["Edit", findMenu.title])
            put("find_next", findFindAction(sub, tag: 2), path: ["Edit", findMenu.title])
            put("find_previous", findFindAction(sub, tag: 3), path: ["Edit", findMenu.title])
            put("use_selection_for_find", findFindAction(sub, tag: 7), path: ["Edit", findMenu.title])
            put("jump_to_selection", findFirst(sub, action: "centerSelectionInVisibleArea:"), path: ["Edit", findMenu.title])

            // Replace set: prefer official tags 5/6/4. Fallback to tag 12 if present in your OS.
            let repl = findFindAction(sub, tag: 5) ?? findFindAction(sub, tag: 12)
            let replFind = findFindAction(sub, tag: 6) ?? findFindAction(sub, tag: 12)
            let replAll = findFindAction(sub, tag: 4) ?? findFindAction(sub, tag: 12)

            put("replace", repl, path: ["Edit", findMenu.title])
            put("replace_and_find", replFind, path: ["Edit", findMenu.title])
            put("replace_all", replAll, path: ["Edit", findMenu.title])
        }
    }

    // Spelling & grammar
    if let sp = findContainerWithDirectChild(scopes.editMenu, predicate: { $0.actionName == "toggleAutomaticSpellingCorrection:" }) {
        put("spelling_and_grammar", sp, path: ["Edit"])
        if let sub = sp.submenu {
            put("toggle_correct_spelling_automatically",
                findFirst(sub, action: "toggleAutomaticSpellingCorrection:"),
                path: ["Edit", sp.title]
            )
        }
    }

    // Substitutions
    if let substs = findContainerWithDirectChild(scopes.editMenu, predicate: { $0.actionName == "toggleAutomaticQuoteSubstitution:" }) {
        put("substitutions", substs, path: ["Edit"])
        if let sub = substs.submenu {
            put("toggle_smart_quotes", findFirst(sub, action: "toggleAutomaticQuoteSubstitution:"), path: ["Edit", substs.title])
            put("toggle_smart_dashes", findFirst(sub, action: "toggleAutomaticDashSubstitution:"), path: ["Edit", substs.title])
            put("toggle_smart_links", findFirst(sub, action: "toggleAutomaticLinkDetection:"), path: ["Edit", substs.title])
            put("toggle_text_replacement", findFirst(sub, action: "toggleAutomaticTextReplacement:"), path: ["Edit", substs.title])
        }
    }

    // Transformations
    if let tr = findContainerWithDirectChild(scopes.editMenu, predicate: { $0.actionName == "uppercaseWord:" }) {
        put("transformations", tr, path: ["Edit"])
        if let sub = tr.submenu {
            put("make_upper_case", findFirst(sub, action: "uppercaseWord:"), path: ["Edit", tr.title])
            put("make_lower_case", findFirst(sub, action: "lowercaseWord:"), path: ["Edit", tr.title])
            put("capitalize", findFirst(sub, action: "capitalizeWord:"), path: ["Edit", tr.title])
        }
    }

    // Speech
    if let speech = findContainerWithDirectChild(scopes.editMenu, predicate: { $0.actionName == "startSpeaking:" }) {
        put("speech", speech, path: ["Edit"])
        if let sub = speech.submenu {
            put("start_speaking", findFirst(sub, action: "startSpeaking:"), path: ["Edit", speech.title])
            put("stop_speaking", findFirst(sub, action: "stopSpeaking:"), path: ["Edit", speech.title])
        }
    }

    // Format
    if let fontContainer = findContainerWithDirectChild(scopes.formatMenu, predicate: { $0.actionName == "orderFrontFontPanel:" }) {
        put("font", fontContainer, path: ["Format"])
        if let fontSub = fontContainer.submenu {
            put("show_fonts", findFirst(fontSub, action: "orderFrontFontPanel:"), path: ["Format", fontContainer.title])
            put("show_colors", findFirst(fontSub, action: "orderFrontColorPanel:"), path: ["Format", fontContainer.title])
            put("bold", findFirst(fontSub, action: "addFontTrait:", tag: 2), path: ["Format", fontContainer.title])
            put("italic", findFirst(fontSub, action: "addFontTrait:", tag: 1), path: ["Format", fontContainer.title])
            put("underline", findFirstAny(fontSub, actions: ["underline:"]), path: ["Format", fontContainer.title])
            put("bigger", findFirst(fontSub, action: "modifyFont:", tag: 3), path: ["Format", fontContainer.title])
            put("smaller", findFirst(fontSub, action: "modifyFont:", tag: 4), path: ["Format", fontContainer.title])
            if let kerning = findContainerWithDirectChild(fontSub, predicate: { $0.actionName == "useStandardKerning:" })?.submenu {
                put("kerning_standard", findFirst(kerning, action: "useStandardKerning:"), path: ["Format", fontContainer.title, "Kerning"])
                put("kerning_none", findFirst(kerning, action: "turnOffKerning:"), path: ["Format", fontContainer.title, "Kerning"])
                put("kerning_tighten", findFirst(kerning, action: "tightenKerning:"), path: ["Format", fontContainer.title, "Kerning"])
                put("kerning_loosen", findFirst(kerning, action: "loosenKerning:"), path: ["Format", fontContainer.title, "Kerning"])
            }
            if let lig = findContainerWithDirectChild(fontSub, predicate: { $0.actionName == "useStandardLigatures:" })?.submenu {
                put("ligatures_none", findFirst(lig, action: "turnOffLigatures:"), path: ["Format", fontContainer.title, "Ligatures"])
                put("ligatures_standard", findFirst(lig, action: "useStandardLigatures:"), path: ["Format", fontContainer.title, "Ligatures"])
                put("ligatures_all", findFirst(lig, action: "useAllLigatures:"), path: ["Format", fontContainer.title, "Ligatures"])
            }
            if let base = findContainerWithDirectChild(fontSub, predicate: { $0.actionName == "raiseBaseline:" })?.submenu {
                put("raise_baseline", findFirst(base, action: "raiseBaseline:"), path: ["Format", fontContainer.title, "Baseline"])
                put("lower_baseline", findFirst(base, action: "lowerBaseline:"), path: ["Format", fontContainer.title, "Baseline"])
                put("superscript", findFirst(base, action: "superscript:"), path: ["Format", fontContainer.title, "Baseline"])
                put("subscript", findFirst(base, action: "subscript:"), path: ["Format", fontContainer.title, "Baseline"])
            }
            if let baselineContainer = findContainerWithDirectChild(fontSub, predicate: { $0.actionName == "raiseBaseline:" }) {
                put("baseline", baselineContainer, path: ["Format", fontContainer.title])
            }
        }
    }

    // Text submenu in Format
    if let textContainer = findContainerWithDirectChild(scopes.formatMenu, predicate: { $0.actionName == "alignLeft:" }) {
        put("text", textContainer, path: ["Format"])
        if let sub = textContainer.submenu {
            put("align_left", findFirst(sub, action: "alignLeft:"), path: ["Format", textContainer.title])
            put("align_center", findFirst(sub, action: "alignCenter:"), path: ["Format", textContainer.title])
            put("align_right", findFirst(sub, action: "alignRight:"), path: ["Format", textContainer.title])
            put("align_justified", findFirst(sub, action: "alignJustified:"), path: ["Format", textContainer.title])
        }
    }

    // View
    put("show_toolbar", findFirst(scopes.viewMenu, action: "toggleToolbarShown:"), path: ["View"])
    put("customize_toolbar", findFirst(scopes.viewMenu, action: "runToolbarCustomizationPalette:"), path: ["View"])
    put("full_screen", findFirstAny(scopes.viewMenu, actions: ["toggleFullScreen:"]), path: ["View"])
    put("show_sidebar", findFirstAny(scopes.viewMenu, actions: ["toggleSidebar:", "toggleSourceList:"]), path: ["View"])
    put("show_tab_bar", findFirstAny(scopes.viewMenu, actions: ["toggleTabBar:", "toggleTabBarShown:"]), path: ["View"])

    // Window
    put("window_close", findFirst(scopes.windowMenu, action: "performClose:"), path: ["Window"])
    put("window_minimize", findFirst(scopes.windowMenu, action: "performMiniaturize:"), path: ["Window"])
    put("window_minimize_all", findFirstAny(scopes.windowMenu, actions: ["miniaturizeAll:"]), path: ["Window"])
    put("window_zoom", findFirst(scopes.windowMenu, action: "performZoom:"), path: ["Window"])
    put("bring_all_to_front", findFirst(scopes.windowMenu, action: "arrangeInFront:"), path: ["Window"])

    // Window tabbing
    put("show_next_tab", findFirstAny(scopes.windowMenu, actions: ["selectNextTab:"]), path: ["Window"])
    put("show_previous_tab", findFirstAny(scopes.windowMenu, actions: ["selectPreviousTab:"]), path: ["Window"])
    put("merge_all_windows", findFirstAny(scopes.windowMenu, actions: ["mergeAllWindows:"]), path: ["Window"])
    put("move_tab_to_new_window", findFirstAny(scopes.windowMenu, actions: ["moveTabToNewWindow:"]), path: ["Window"])

    // Help
    put("app_help", findFirst(scopes.helpMenu, action: "showHelp:"), path: ["Help"])

    return ValuesAndMeta(values: values, meta: meta)
}

private extension NSMenuItem {
    var actionName: String? { action.map { NSStringFromSelector($0) } }
}

private func findTopLevelItemTitle(for submenu: NSMenu, in mainMenu: NSMenu) -> NSMenuItem? {
    mainMenu.items.first(where: { $0.submenu === submenu })
}

private func findFirst(_ menu: NSMenu, action: String, tag: Int? = nil) -> NSMenuItem? {
    for item in menu.items {
        if item.actionName == action, tag == nil || item.tag == tag {
            return item
        }
        if let sub = item.submenu, let found = findFirst(sub, action: action, tag: tag) {
            return found
        }
    }
    return nil
}

private func findFirst(_ menu: NSMenu, action: String) -> NSMenuItem? {
    findFirst(menu, action: action, tag: nil)
}

private func findFirstAny(_ menu: NSMenu, actions: [String]) -> NSMenuItem? {
    for a in actions {
        if let item = findFirst(menu, action: a) { return item }
    }
    return nil
}

private func findByShortcut(_ menu: NSMenu, key: String, mods: NSEvent.ModifierFlags) -> NSMenuItem? {
    for item in menu.items {
        if item.keyEquivalent.lowercased() == key.lowercased(),
           item.keyEquivalentModifierMask.contains(mods) {
            return item
        }
        if let sub = item.submenu, let found = findByShortcut(sub, key: key, mods: mods) {
            return found
        }
    }
    return nil
}

private func findItemPointingToSubmenu(_ menu: NSMenu, submenu: NSMenu) -> NSMenuItem? {
    menu.items.first(where: { $0.submenu === submenu })
}

private func findContainerWithDirectChildAction(_ menu: NSMenu, childAction: String) -> NSMenuItem? {
    return findContainerWithDirectChild(menu) { $0.actionName == childAction }
}

private func findContainerWithDirectChild(_ menu: NSMenu, predicate: (NSMenuItem) -> Bool) -> NSMenuItem? {
    for item in menu.items {
        guard let sub = item.submenu else { continue }
        if sub.items.contains(where: predicate) { return item }
        if let deep = findContainerWithDirectChild(sub, predicate: predicate) { return deep }
    }
    return nil
}

private func findFindAction(_ menu: NSMenu, tag: Int) -> NSMenuItem? {
    menu.items.first { $0.actionName == "performFindPanelAction:" && $0.tag == tag }
}

private func makeMetaEntry(key: String, title: String, item: NSMenuItem, path: [String]) -> MenuDumpMetaEntry {
    var sfCandidate: String?

    if let image = item.image {
        let description = image.description
        sfCandidate = description
        if let range = description.range(of: #"symbol = ([a-zA-Z0-0\.]+)"#, options: .regularExpression) {
            // sfCandidate = description[range].replacingOccurrences(of: "symbol = ", with: "")
        }
    }

    return MenuDumpMetaEntry(
        key: key,
        title: title,
        action: item.actionName,
        tag: item.tag,
        keyEquivalent: item.keyEquivalent,
        modifierMaskRaw: item.keyEquivalentModifierMask.rawValue,
        hasSubmenu: item.submenu != nil,
        sfSymbolNameCandidate: sfCandidate,
        menuPath: path
    )
}

private func extractKeysFromStringsXML(atPath path: String) -> [String] {
    guard let content = try? String(contentsOfFile: path, encoding: .utf8) else { return [] }
    var keys: [String] = []
    for line in content.split(separator: "\n", omittingEmptySubsequences: false) {
        let s = String(line)
        guard let r = s.range(of: #"<string name=""#) else { continue }
        let after = s[r.upperBound...]
        guard let endQuote = after.firstIndex(of: "\"") else { continue }
        keys.append(String(after[..<endQuote]))
    }
    return keys
}

private func updateStringsXMLPreservingFormatting(baseline: String, newValues: [String: String]) -> String {
    var outLines: [String] = []
    let lines = baseline.split(separator: "\n", omittingEmptySubsequences: false)

    for lineSub in lines {
        let line = String(lineSub)
        guard let start = line.range(of: #"<string name=""#),
              let nameEnd = line[start.upperBound...].firstIndex(of: "\""),
              let gt = line.firstIndex(of: ">"),
              let end = line.range(of: "</string>") else {
            outLines.append(line)
            continue
        }

        let key = String(line[start.upperBound..<nameEnd])
        if let new = newValues[key] {
            let escaped = xmlEscape(new)
            let prefix = String(line[..<line.index(after: gt)])
            let suffix = String(line[end.lowerBound...])
            outLines.append(prefix + escaped + suffix)
        } else {
            outLines.append(line)
        }
    }
    return outLines.joined(separator: "\n")
}

private func generateStringsXML(keys: [String], values: [String: String]) -> String {
    var s = #"<?xml version="1.0" encoding="utf-8"?>"# + "\n"
    s += "<resources>\n"
    for k in keys {
        s += #"    <string name="\#(k)">\#(xmlEscape(values[k] ?? ""))</string>"# + "\n"
    }
    s += "</resources>\n"
    return s
}

private func xmlEscape(_ input: String) -> String {
    var s = input
    s = s.replacingOccurrences(of: "&", with: "&amp;")
    s = s.replacingOccurrences(of: "<", with: "&lt;")
    s = s.replacingOccurrences(of: ">", with: "&gt;")
    s = s.replacingOccurrences(of: "\"", with: "&quot;")
    s = s.replacingOccurrences(of: "'", with: "&apos;")
    return s
}

private func normalizeAppNamePlaceholder(key: String, rawTitle: String, appNameInMenuBar: String) -> String {
    let keysNeedingPlaceholder: Set<String> = ["about", "hide", "quit", "app_help"]
    guard keysNeedingPlaceholder.contains(key) else { return rawTitle }
    return rawTitle.replacingOccurrences(of: appNameInMenuBar, with: "%1$s")
}

private func writeTextFile(_ s: String, to path: String) throws {
    let url = URL(fileURLWithPath: path)
    try FileManager.default.createDirectory(at: url.deletingLastPathComponent(), withIntermediateDirectories: true)
    try s.write(to: url, atomically: true, encoding: .utf8)
}

private func writeJSON<T: Encodable>(_ value: T, to path: String) throws {
    let url = URL(fileURLWithPath: path)
    try FileManager.default.createDirectory(at: url.deletingLastPathComponent(), withIntermediateDirectories: true)
    let enc = JSONEncoder()
    enc.outputFormatting = [.prettyPrinted, .sortedKeys]
    try enc.encode(value).write(to: url, options: [.atomic])
}

private func defaultKeys() -> [String] {
    [
        "about","settings","services","hide","hide_others","show_all","quit",
        "file","file_new","file_open","file_open_recent","file_clear_recent","file_close","file_close_all","file_save","file_save_as","file_duplicate","file_rename","file_move_to","file_page_setup","file_print",
        "edit","edit_undo","edit_redo","edit_cut","edit_copy","edit_paste","paste_and_match_style","edit_delete","edit_select_all",
        "find","find_dots","find_next","find_previous","use_selection_for_find","jump_to_selection","replace","replace_and_find","replace_all",
        "spelling_and_grammar","toggle_correct_spelling_automatically",
        "substitutions","toggle_smart_quotes","toggle_smart_dashes","toggle_smart_links","toggle_text_replacement",
        "transformations","make_upper_case","make_lower_case","capitalize",
        "speech","start_speaking","stop_speaking",
        "format","font","show_fonts","show_colors","bold","italic","underline","bigger","smaller",
        "kerning_standard","kerning_none","kerning_tighten","kerning_loosen",
        "ligatures_none","ligatures_standard","ligatures_all",
        "baseline","raise_baseline","lower_baseline","superscript","subscript",
        "text","align_left","align_center","align_right","align_justified",
        "view","show_toolbar","customize_toolbar","full_screen","show_sidebar","show_tab_bar",
        "window","window_close","window_minimize","window_minimize_all","window_zoom","bring_all_to_front","show_next_tab","show_previous_tab","merge_all_windows","move_tab_to_new_window",
        "help","app_help"
    ]
}

private func printMenuTree(_ menu: NSMenu, indent: String = "") {
    for item in menu.items {
        let a = item.action.map { NSStringFromSelector($0) } ?? "nil"
        let k = item.keyEquivalent.isEmpty ? "" : " key=\(item.keyEquivalent)"
        let m = item.keyEquivalentModifierMask.rawValue == 0 ? "" : " mods=\(item.keyEquivalentModifierMask.rawValue)"
        let t = item.tag != 0 ? " tag=\(item.tag)" : ""
        print("\(indent)- \(item.title) action=\(a)\(t)\(k)\(m)")
        if let sub = item.submenu { printMenuTree(sub, indent: indent + "  ") }
    }
}
