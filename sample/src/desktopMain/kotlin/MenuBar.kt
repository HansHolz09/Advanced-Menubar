
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.selectAll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import composeadvancedmenubar.sample.generated.resources.*
import dev.hansholz.advancedmenubar.AdvancedMacMenu
import dev.hansholz.advancedmenubar.MacCocoaMenu
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MenuBar(
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

    AdvancedMacMenu(window.title) {
        MacApplicationMenu {
            About { onClick("About") }
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
        MacCustomMenu(getString(Res.string.file)) {
            Menu(getString(Res.string.new)) {
                Item(getString(Res.string.project)) { onClick("New Project") }
                Item(getString(Res.string.file)) { onClick("New File") }
            }
            Item(getString(Res.string.open)) { onClick("Open") }
            Separator()
            Menu(getString(Res.string.export)) {
                Item(getString(Res.string.as_pdf), icon = MacCocoaMenu.MenuIcon.SFSymbol("doc.richtext")) { onClick("Export as PDF") }
                Item(getString(Res.string.as_image), icon = MacCocoaMenu.MenuIcon.SFSymbol("photo")) { onClick("Export as Image") }
            }
        }
        MacEditMenu {
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
        MacViewMenu {
            ShowToolbar(enabled = false) {}
            CustomizeToolbar(enabled = false) {}
            Separator()
            ToggleFullScreen()
        }
        customMenus.forEach {
            MacCustomMenu("${getString(Res.string.custom)} $it") {
                Item("${getString(Res.string.custom_item)} 1") { onClick("Custom Item 1 (from Custom $it)") }
                Item("${getString(Res.string.custom_item)} 2") { onClick("Custom Item 2 (from Custom $it)") }
                Separator()
                Menu(getString(Res.string.custom_submenu)) {
                    Menu("${getString(Res.string.file)} 2") {
                        Item(getString(Res.string.disabled_item), enabled = false) {}
                    }
                }
                Separator()
                Item(getString(Res.string.disabled_item), enabled = false) {}
            }
        }
        MacWindowMenu {
            Separator()
            Close()
            Minimize()
            MinimizeAll()
            Zoom()
            BringAllToFront()
        }
        MacCustomMenu(getString(Res.string.options)) {
            Checkbox("${getString(Res.string.checkbox_item)} 1", checkboxItem1.value) { checkboxItem1.value = it }
            Checkbox("${getString(Res.string.checkbox_item)} 2", checkboxItem2.value) { checkboxItem2.value = it }
            Checkbox("${getString(Res.string.checkbox_item)} 3", checkboxItem3.value) { checkboxItem3.value = it }
        }
        MacHelpMenu {
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