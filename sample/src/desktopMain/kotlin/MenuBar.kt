
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.selectAll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.window.FrameWindowScope
import composeadvancedmenubar.sample.generated.resources.Res
import composeadvancedmenubar.sample.generated.resources.allStringResources
import composeadvancedmenubar.sample.generated.resources.available_version
import composeadvancedmenubar.sample.generated.resources.check_for_updates
import composeadvancedmenubar.sample.generated.resources.checkbox_item
import composeadvancedmenubar.sample.generated.resources.community_forum
import composeadvancedmenubar.sample.generated.resources.custom
import composeadvancedmenubar.sample.generated.resources.custom_item
import composeadvancedmenubar.sample.generated.resources.custom_submenu
import composeadvancedmenubar.sample.generated.resources.disabled_item
import composeadvancedmenubar.sample.generated.resources.file
import composeadvancedmenubar.sample.generated.resources.new_update_available
import composeadvancedmenubar.sample.generated.resources.options
import composeadvancedmenubar.sample.generated.resources.release_notes
import composeadvancedmenubar.sample.generated.resources.resources
import composeadvancedmenubar.sample.generated.resources.section
import composeadvancedmenubar.sample.generated.resources.website
import dev.hansholz.advancedmenubar.CompatibilityMenu
import dev.hansholz.advancedmenubar.MacCocoaMenu.MenuIcon.SFSymbol
import dev.hansholz.advancedmenubar.MenuShortcut
import dev.hansholz.advancedmenubar.MenuVisibility
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FrameWindowScope.MenuBar(
    window: ComposeWindow,
    customMenus: List<Int>,
    checkboxItem1: MutableState<Boolean>,
    checkboxItem2: MutableState<Boolean>,
    checkboxItem3: MutableState<Boolean>,
    textFieldState: TextFieldState,
    onClick: (String) -> Unit
) {
    @Suppress("Deprecation")
    val clipboard = LocalClipboardManager.current

    val strings = Res.allStringResources.map {
        it.value to stringResource(it.value)
    }
    fun getString(stringResource: StringResource): String = strings.find { it.first == stringResource }?.second ?: "STRING NOT FOUND"

    CompatibilityMenu(window.title) {
        MacApplicationMenu {
            About { onClick("About") }
            Separator()
            Item(
                title = getString(Res.string.check_for_updates),
                macIcon = SFSymbol("arrow.down.app"),
                macSubtitle = "${getString(Res.string.available_version)}: 1.0.0",
                macTooltip = "Changelog:\n- First big new Feature\n- Fixed a big security issue\n- Many little bugfixes and stability improvements",
                macBadge = getString(Res.string.new_update_available)
            ) {
                onClick("Check for Updates")
            }
            Separator()
            Settings { onClick("Settings") }
            Separator()
            Services()
            Separator()
            Hide()
            HideOthers()
            ShowAll()
            Separator()
            Quit()
        }
        FileMenu {
            FileNew { onClick("New File") }
            FileOpen { onClick("Open File")}
            FileOpenRecent {
                Item("PDF_01", macIcon = SFSymbol("doc.richtext")) { onClick("PDF_01") }
                Item("Picture_02", macIcon = SFSymbol("photo")) { onClick("Picture_02") }
                Separator()
                FileClearRecent { onClick("Clear Recent Files") }
            }
            Separator()
            FileSave { onClick("Save File") }
            FileSaveAs { onClick("Save File as") }
            FileRename { onClick("Rename File") }
            Separator()
            FilePageSetup(visibility = MenuVisibility.MACOS_ONLY)
            FilePrint { onClick("Print") }
        }
        EditMenu {
            Undo(enabled = textFieldState.undoState.canUndo) {
                textFieldState.undoState.undo()
            }
            Redo(enabled = textFieldState.undoState.canRedo) {
                textFieldState.undoState.redo()
            }
            Separator()
            Cut(enabled = !textFieldState.selection.collapsed) {
                val sel = textFieldState.selection
                if (!sel.collapsed) {
                    clipboard.setText(AnnotatedString(textFieldState.text.substring(sel.start, sel.end)))
                    textFieldState.edit { delete(sel.start, sel.end) }
                }
            }
            Copy(enabled = !textFieldState.selection.collapsed) {
                val sel = textFieldState.selection
                if (!sel.collapsed) {
                    clipboard.setText(AnnotatedString(textFieldState.text.substring(sel.start, sel.end)))
                }
            }
            Paste(enabled = clipboard.hasText()) {
                val paste = clipboard.getText()?.text ?: ""
                val sel = textFieldState.selection
                textFieldState.edit {
                    if (!sel.collapsed) delete(sel.start, sel.end)
                    insert(selection.start, paste)
                    placeCursorBeforeCharAt(sel.start + paste.length)
                }
            }
            PasteAndMatchStyle(enabled = false) {}
            Delete(enabled = !textFieldState.selection.collapsed) {
                val sel = textFieldState.selection
                if (!sel.collapsed) textFieldState.edit { delete(sel.start, sel.end) }
            }
            SelectAll(enabled = textFieldState.text.isNotEmpty()) {
                textFieldState.edit { selectAll() }
            }
        }
        ViewMenu(visibility = MenuVisibility.MACOS_ONLY) {
            ShowToolbar(enabled = false) {}
            CustomizeToolbar(enabled = false) {}
            Separator()
            ToggleFullScreen()
        }
        customMenus.forEach {
            CustomMenu("${getString(Res.string.custom)} $it") {
                Section("${getString(Res.string.section)} 1") {
                    Item("${getString(Res.string.custom_item)} 1") { onClick("Custom Item 1 (from Custom $it)") }
                    Item("${getString(Res.string.custom_item)} 2") { onClick("Custom Item 2 (from Custom $it)") }
                }
                Section("${getString(Res.string.section)} 2") {
                    Menu(getString(Res.string.custom_submenu)) {
                        Menu("${getString(Res.string.file)} 2") {
                            Item(getString(Res.string.disabled_item), enabled = false) {}
                        }
                    }
                    Item(getString(Res.string.disabled_item), enabled = false) {}
                }
            }
        }
        CustomMenu(getString(Res.string.options)) {
            Checkbox("${getString(Res.string.checkbox_item)} 1", checkboxItem1.value) { checkboxItem1.value = it }
            Checkbox("${getString(Res.string.checkbox_item)} 2", checkboxItem2.value) { checkboxItem2.value = it }
            Checkbox("${getString(Res.string.checkbox_item)} 3", checkboxItem3.value) { checkboxItem3.value = it }
        }
        WindowMenu(visibility = MenuVisibility.MACOS_ONLY) {
            Separator()
            Close()
            Minimize()
            MinimizeAll()
            Zoom()
            BringAllToFront()
        }
        HelpMenu {
            AppHelp { onClick("Help") }
            Separator()
            Item(getString(Res.string.release_notes)) { onClick("Release Notes") }
            Menu(getString(Res.string.resources)) {
                Item(getString(Res.string.website)) { onClick("Website") }
                Item(getString(Res.string.community_forum)) { onClick("Community Forum") }
            }
        }
    }
}