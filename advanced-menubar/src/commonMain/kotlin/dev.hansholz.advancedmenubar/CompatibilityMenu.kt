package dev.hansholz.advancedmenubar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.MenuScope
import composeadvancedmenubar.advanced_menubar.generated.resources.Res
import composeadvancedmenubar.advanced_menubar.generated.resources.allStringResources
import dev.hansholz.advancedmenubar.MacCocoaMenu.CheckboxItem
import dev.hansholz.advancedmenubar.MacCocoaMenu.CustomItem
import dev.hansholz.advancedmenubar.MacCocoaMenu.EditStd
import dev.hansholz.advancedmenubar.MacCocoaMenu.FileStd
import dev.hansholz.advancedmenubar.MacCocoaMenu.FormatStd
import dev.hansholz.advancedmenubar.MacCocoaMenu.HelpItem
import dev.hansholz.advancedmenubar.MacCocoaMenu.MenuElement
import dev.hansholz.advancedmenubar.MacCocoaMenu.Separator
import dev.hansholz.advancedmenubar.MacCocoaMenu.Submenu
import dev.hansholz.advancedmenubar.MacCocoaMenu.SystemItem
import dev.hansholz.advancedmenubar.MacCocoaMenu.TopMenu
import dev.hansholz.advancedmenubar.MacCocoaMenu.ViewStd
import dev.hansholz.advancedmenubar.MacCocoaMenu.WindowStd
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.skiko.hostOs
import javax.swing.SwingUtilities

@Composable
fun FrameWindowScope.CompatibilityMenu(
    appName: String,
    content: AdvancedMacMenuScope.() -> Unit
) {
    val strings = Res.allStringResources.map {
        it.value to stringResource(it.value, appName)
    }

    val scope = AdvancedMacMenuScope(strings).apply {
        reset()
        content()
    }
    val model = scope.menus.toList()

    if (hostOs.isMacOS) {
        LaunchedEffect(model) {
            SwingUtilities.invokeLater {
                MacCocoaMenu.rebuildMenuBar(model)
            }
        }
    } else {
        ComposeMenuBarFromModel(model)
    }
}

@Composable
private fun FrameWindowScope.ComposeMenuBarFromModel(menus: List<TopMenu>) {
    MenuBar {
        menus.forEach { top ->
            when (top) {
                is TopMenu.Application -> Unit
                is TopMenu.File   -> Menu(top.title)   { renderElements(top.elements) }
                is TopMenu.Edit   -> Menu(top.title)   { renderElements(top.elements) }
                is TopMenu.Format -> Menu(top.title)   { renderElements(top.elements) }
                is TopMenu.View   -> Menu(top.title)   { renderElements(top.elements) }
                is TopMenu.Window -> Menu(top.title)   { renderElements(top.elements) }
                is TopMenu.Help   -> Menu(top.title)   { renderElements(top.elements) }
                is TopMenu.Custom -> Menu(top.title)   { renderElements(top.elements) }
            }
        }
    }
}

@Composable
private fun MenuScope.renderElements(elements: List<MenuElement>) {
    elements.forEach { el ->
        when (el) {
            is CustomItem ->
                Item(
                    text = el.title,
                    enabled = el.enabled,
                    shortcut = el.shortcut?.toKeyShortcut(),
                    onClick = el.onClick
                )

            is CheckboxItem ->
                CheckboxItem(
                    text = el.title,
                    checked = el.checked,
                    enabled = el.enabled,
                    shortcut = el.shortcut?.toKeyShortcut(),
                    onCheckedChange = el.onToggle
                )

            is Submenu ->
                Menu(el.title, enabled = el.enabled) {
                    renderElements(el.children)
                }

            is Separator -> Separator()

            is SystemItem.About                -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is SystemItem.Settings             -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is SystemItem.Services             -> Item(el.title, enabled = false) {}
            is SystemItem.Hide                 -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is SystemItem.HideOthers           -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is SystemItem.ShowAll              -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is SystemItem.Quit                 -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }

            is FileStd.New                     -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FileStd.Open                    -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FileStd.OpenRecent              -> Menu(el.title, enabled = el.enabled) { renderElements(el.children) }
            is FileStd.Close                   -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FileStd.CloseAll                -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FileStd.Save                    -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FileStd.SaveAs                  -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FileStd.Duplicate               -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FileStd.Rename                  -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FileStd.MoveTo                  -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FileStd.Revert                  -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FileStd.PageSetup               -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FileStd.Print                   -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FileStd.ClearRecent             -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }

            is EditStd.Undo                    -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.Redo                    -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.Cut                     -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.Copy                    -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.Paste                   -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.PasteAndMatchStyle      -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.Delete                  -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.SelectAll               -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.Find                    -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.FindNext                -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.FindPrevious            -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.UseSelectionForFind     -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.JumpToSelection         -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.Replace                 -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.ReplaceAndFind          -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.ReplaceAll              -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.ToggleSmartQuotes       -> CheckboxItem(el.title, checked = el.checked ?: false, enabled = el.enabled) { el.onToggle?.invoke(it) }
            is EditStd.ToggleSmartDashes       -> CheckboxItem(el.title, checked = el.checked ?: false, enabled = el.enabled) { el.onToggle?.invoke(it) }
            is EditStd.ToggleLinkDetection     -> CheckboxItem(el.title, checked = el.checked ?: false, enabled = el.enabled) { el.onToggle?.invoke(it) }
            is EditStd.ToggleTextReplacement   -> CheckboxItem(el.title, checked = el.checked ?: false, enabled = el.enabled) { el.onToggle?.invoke(it) }
            is EditStd.ToggleSpellingCorrection-> CheckboxItem(el.title, checked = el.checked ?: false, enabled = el.enabled) { el.onToggle?.invoke(it) }
            is EditStd.Uppercase               -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.Lowercase               -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.Capitalize              -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.StartSpeaking           -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.StopSpeaking            -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }

            is FormatStd.ShowFonts             -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FormatStd.ShowColors            -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FormatStd.Bold                  -> CheckboxItem(el.title, checked = el.checked ?: false, enabled = el.enabled) { el.onToggle?.invoke(it) }
            is FormatStd.Italic                -> CheckboxItem(el.title, checked = el.checked ?: false, enabled = el.enabled) { el.onToggle?.invoke(it) }
            is FormatStd.Underline             -> CheckboxItem(el.title, checked = el.checked ?: false, enabled = el.enabled) { el.onToggle?.invoke(it) }
            is FormatStd.Bigger                -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FormatStd.Smaller               -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FormatStd.KerningStandard       -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FormatStd.KerningNone           -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FormatStd.KerningTighten        -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FormatStd.KerningLoosen         -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FormatStd.LigaturesNone         -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FormatStd.LigaturesStandard     -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FormatStd.LigaturesAll          -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FormatStd.RaiseBaseline         -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FormatStd.LowerBaseline         -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FormatStd.Superscript           -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FormatStd.Subscript             -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is FormatStd.AlignLeft             -> CheckboxItem(el.title, checked = el.checked ?: false, enabled = el.enabled) { el.onToggle?.invoke(it) }
            is FormatStd.AlignCenter           -> CheckboxItem(el.title, checked = el.checked ?: false, enabled = el.enabled) { el.onToggle?.invoke(it) }
            is FormatStd.AlignRight            -> CheckboxItem(el.title, checked = el.checked ?: false, enabled = el.enabled) { el.onToggle?.invoke(it) }
            is FormatStd.AlignJustified        -> CheckboxItem(el.title, checked = el.checked ?: false, enabled = el.enabled) { el.onToggle?.invoke(it) }

            is ViewStd.ShowToolbar             -> CheckboxItem(el.title, checked = el.checked ?: false, enabled = el.enabled) { el.onToggle?.invoke(it) }
            is ViewStd.CustomizeToolbar        -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is ViewStd.ToggleFullScreen        -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is ViewStd.ToggleSidebar           -> CheckboxItem(el.title, checked = el.checked ?: false, enabled = el.enabled) { el.onToggle?.invoke(it) }
            is ViewStd.ToggleTabBar            -> CheckboxItem(el.title, checked = el.checked ?: false, enabled = el.enabled) { el.onToggle?.invoke(it) }

            is WindowStd.Close                 -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is WindowStd.Minimize              -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is WindowStd.MinimizeAll           -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is WindowStd.Zoom                  -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is WindowStd.BringAllToFront       -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is WindowStd.ShowNextTab           -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is WindowStd.ShowPreviousTab       -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is WindowStd.MergeAllWindows       -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is WindowStd.MoveTabToNewWindow    -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }

            is HelpItem.AppHelp                -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
        }
    }
}