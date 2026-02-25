import SwiftUI
import UniformTypeIdentifiers
import AppKit

struct MenuDumperTextDocument: FileDocument {
    static var readableContentTypes: [UTType] { [.plainText] }
    var text: String = "Menu Dumper"

    init() {}

    init(configuration: ReadConfiguration) throws {
        guard let data = configuration.file.regularFileContents else { return }
        text = String(data: data, encoding: .utf8) ?? ""
    }

    func fileWrapper(configuration: WriteConfiguration) throws -> FileWrapper {
        FileWrapper(regularFileWithContents: Data(text.utf8))
    }
}

struct RichTextEditor: NSViewRepresentable {
    @Binding var text: String

    func makeNSView(context: Context) -> NSScrollView {
        let scroll = NSScrollView()
        scroll.hasVerticalScroller = true
        scroll.hasHorizontalScroller = false
        scroll.autohidesScrollers = true

        let textView = NSTextView()
        textView.isEditable = true
        textView.isSelectable = true
        textView.isRichText = true
        textView.allowsUndo = true
        textView.usesFindPanel = true
        textView.string = text

        scroll.documentView = textView
        context.coordinator.textView = textView
        textView.delegate = context.coordinator
        return scroll
    }

    func updateNSView(_ nsView: NSScrollView, context: Context) {
        guard let tv = context.coordinator.textView else { return }
        if tv.string != text {
            tv.string = text
        }
    }

    func makeCoordinator() -> Coordinator { Coordinator(text: $text) }

    final class Coordinator: NSObject, NSTextViewDelegate {
        @Binding var text: String
        weak var textView: NSTextView?

        init(text: Binding<String>) { _text = text }

        func textDidChange(_ notification: Notification) {
            if let tv = notification.object as? NSTextView {
                text = tv.string
            }
        }
    }
}

struct DocumentView: View {
    @Binding var document: MenuDumperTextDocument

    var body: some View {
        RichTextEditor(text: $document.text)
            .frame(minWidth: 720, minHeight: 520)
    }
}

struct DumperSettingsView: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            Text("Menu Dumper").font(.title2)
            Text("Used by CI to dump localized standard macOS menu strings.")
                .foregroundStyle(.secondary)
        }
        .padding(20)
        .frame(minWidth: 520)
    }
}

final class DumpAppDelegate: NSObject, NSApplicationDelegate {
    func applicationWillFinishLaunching(_ notification: Notification) {
        NSWindow.allowsAutomaticWindowTabbing = true
    }

    func applicationDidFinishLaunching(_ notification: Notification) {
        NSApp.setActivationPolicy(.regular)
        NSApp.activate(ignoringOtherApps: true)

        DispatchQueue.main.asyncAfter(deadline: .now() + 0.6) {
            MenuDumper.runWhenMenuIsReadyAndExit(maxAttempts: 12, delaySeconds: 0.25)
        }
    }
}

@main
struct MenuDumperApp: App {
    @NSApplicationDelegateAdaptor(DumpAppDelegate.self) private var appDelegate

    var body: some Scene {
        DocumentGroup(newDocument: MenuDumperTextDocument()) { file in
            DocumentView(document: file.$document)
        }
        .commands {
            SidebarCommands()
            TextEditingCommands()
            TextFormattingCommands()
            ToolbarCommands()
            ImportFromDevicesCommands()
            InspectorCommands()
        }

        Settings {
            DumperSettingsView()
        }
    }
}
