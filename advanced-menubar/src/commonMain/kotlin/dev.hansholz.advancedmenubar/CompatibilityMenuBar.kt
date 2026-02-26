package dev.hansholz.advancedmenubar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.MenuScope
import composeadvancedmenubar.advanced_menubar.generated.resources.Res
import composeadvancedmenubar.advanced_menubar.generated.resources.about
import composeadvancedmenubar.advanced_menubar.generated.resources.align_center
import composeadvancedmenubar.advanced_menubar.generated.resources.align_justified
import composeadvancedmenubar.advanced_menubar.generated.resources.align_left
import composeadvancedmenubar.advanced_menubar.generated.resources.align_right
import composeadvancedmenubar.advanced_menubar.generated.resources.allStringResources
import composeadvancedmenubar.advanced_menubar.generated.resources.app_help
import composeadvancedmenubar.advanced_menubar.generated.resources.baseline
import composeadvancedmenubar.advanced_menubar.generated.resources.baseline_standard
import composeadvancedmenubar.advanced_menubar.generated.resources.bigger
import composeadvancedmenubar.advanced_menubar.generated.resources.bold
import composeadvancedmenubar.advanced_menubar.generated.resources.bring_all_to_front
import composeadvancedmenubar.advanced_menubar.generated.resources.capitalize
import composeadvancedmenubar.advanced_menubar.generated.resources.customize_toolbar
import composeadvancedmenubar.advanced_menubar.generated.resources.edit
import composeadvancedmenubar.advanced_menubar.generated.resources.edit_copy
import composeadvancedmenubar.advanced_menubar.generated.resources.edit_cut
import composeadvancedmenubar.advanced_menubar.generated.resources.edit_delete
import composeadvancedmenubar.advanced_menubar.generated.resources.edit_paste
import composeadvancedmenubar.advanced_menubar.generated.resources.edit_redo
import composeadvancedmenubar.advanced_menubar.generated.resources.edit_select_all
import composeadvancedmenubar.advanced_menubar.generated.resources.edit_undo
import composeadvancedmenubar.advanced_menubar.generated.resources.enter_full_screen
import composeadvancedmenubar.advanced_menubar.generated.resources.exit_full_screen
import composeadvancedmenubar.advanced_menubar.generated.resources.file
import composeadvancedmenubar.advanced_menubar.generated.resources.file_clear_recent
import composeadvancedmenubar.advanced_menubar.generated.resources.file_close
import composeadvancedmenubar.advanced_menubar.generated.resources.file_close_all
import composeadvancedmenubar.advanced_menubar.generated.resources.file_duplicate
import composeadvancedmenubar.advanced_menubar.generated.resources.file_move_to
import composeadvancedmenubar.advanced_menubar.generated.resources.file_new
import composeadvancedmenubar.advanced_menubar.generated.resources.file_open
import composeadvancedmenubar.advanced_menubar.generated.resources.file_open_recent
import composeadvancedmenubar.advanced_menubar.generated.resources.file_page_setup
import composeadvancedmenubar.advanced_menubar.generated.resources.file_print
import composeadvancedmenubar.advanced_menubar.generated.resources.file_rename
import composeadvancedmenubar.advanced_menubar.generated.resources.file_save
import composeadvancedmenubar.advanced_menubar.generated.resources.file_save_as
import composeadvancedmenubar.advanced_menubar.generated.resources.find
import composeadvancedmenubar.advanced_menubar.generated.resources.find_and_replace
import composeadvancedmenubar.advanced_menubar.generated.resources.find_dots
import composeadvancedmenubar.advanced_menubar.generated.resources.find_next
import composeadvancedmenubar.advanced_menubar.generated.resources.find_previous
import composeadvancedmenubar.advanced_menubar.generated.resources.font
import composeadvancedmenubar.advanced_menubar.generated.resources.format
import composeadvancedmenubar.advanced_menubar.generated.resources.help
import composeadvancedmenubar.advanced_menubar.generated.resources.hide
import composeadvancedmenubar.advanced_menubar.generated.resources.hide_others
import composeadvancedmenubar.advanced_menubar.generated.resources.hide_sidebar
import composeadvancedmenubar.advanced_menubar.generated.resources.hide_tab_bar
import composeadvancedmenubar.advanced_menubar.generated.resources.hide_toolbar
import composeadvancedmenubar.advanced_menubar.generated.resources.italic
import composeadvancedmenubar.advanced_menubar.generated.resources.jump_to_selection
import composeadvancedmenubar.advanced_menubar.generated.resources.kerning
import composeadvancedmenubar.advanced_menubar.generated.resources.kerning_loosen
import composeadvancedmenubar.advanced_menubar.generated.resources.kerning_none
import composeadvancedmenubar.advanced_menubar.generated.resources.kerning_standard
import composeadvancedmenubar.advanced_menubar.generated.resources.kerning_tighten
import composeadvancedmenubar.advanced_menubar.generated.resources.ligatures
import composeadvancedmenubar.advanced_menubar.generated.resources.ligatures_all
import composeadvancedmenubar.advanced_menubar.generated.resources.ligatures_none
import composeadvancedmenubar.advanced_menubar.generated.resources.ligatures_standard
import composeadvancedmenubar.advanced_menubar.generated.resources.lower_baseline
import composeadvancedmenubar.advanced_menubar.generated.resources.make_lower_case
import composeadvancedmenubar.advanced_menubar.generated.resources.make_upper_case
import composeadvancedmenubar.advanced_menubar.generated.resources.merge_all_windows
import composeadvancedmenubar.advanced_menubar.generated.resources.move_tab_to_new_window
import composeadvancedmenubar.advanced_menubar.generated.resources.paste_and_match_style
import composeadvancedmenubar.advanced_menubar.generated.resources.quit
import composeadvancedmenubar.advanced_menubar.generated.resources.raise_baseline
import composeadvancedmenubar.advanced_menubar.generated.resources.services
import composeadvancedmenubar.advanced_menubar.generated.resources.settings
import composeadvancedmenubar.advanced_menubar.generated.resources.show_all
import composeadvancedmenubar.advanced_menubar.generated.resources.show_colors
import composeadvancedmenubar.advanced_menubar.generated.resources.show_fonts
import composeadvancedmenubar.advanced_menubar.generated.resources.show_next_tab
import composeadvancedmenubar.advanced_menubar.generated.resources.show_previous_tab
import composeadvancedmenubar.advanced_menubar.generated.resources.show_sidebar
import composeadvancedmenubar.advanced_menubar.generated.resources.show_tab_bar
import composeadvancedmenubar.advanced_menubar.generated.resources.show_toolbar
import composeadvancedmenubar.advanced_menubar.generated.resources.smaller
import composeadvancedmenubar.advanced_menubar.generated.resources.speech
import composeadvancedmenubar.advanced_menubar.generated.resources.spelling_and_grammar
import composeadvancedmenubar.advanced_menubar.generated.resources.start_speaking
import composeadvancedmenubar.advanced_menubar.generated.resources.stop_speaking
import composeadvancedmenubar.advanced_menubar.generated.resources.subscript
import composeadvancedmenubar.advanced_menubar.generated.resources.substitutions
import composeadvancedmenubar.advanced_menubar.generated.resources.superscript
import composeadvancedmenubar.advanced_menubar.generated.resources.text
import composeadvancedmenubar.advanced_menubar.generated.resources.toggle_correct_spelling_automatically
import composeadvancedmenubar.advanced_menubar.generated.resources.toggle_smart_dashes
import composeadvancedmenubar.advanced_menubar.generated.resources.toggle_smart_links
import composeadvancedmenubar.advanced_menubar.generated.resources.toggle_smart_quotes
import composeadvancedmenubar.advanced_menubar.generated.resources.toggle_text_replacement
import composeadvancedmenubar.advanced_menubar.generated.resources.transformations
import composeadvancedmenubar.advanced_menubar.generated.resources.underline
import composeadvancedmenubar.advanced_menubar.generated.resources.use_selection_for_find
import composeadvancedmenubar.advanced_menubar.generated.resources.view
import composeadvancedmenubar.advanced_menubar.generated.resources.window
import composeadvancedmenubar.advanced_menubar.generated.resources.window_close
import composeadvancedmenubar.advanced_menubar.generated.resources.window_minimize
import composeadvancedmenubar.advanced_menubar.generated.resources.window_minimize_all
import composeadvancedmenubar.advanced_menubar.generated.resources.window_zoom
import dev.hansholz.advancedmenubar.MacCocoaMenuBar.CheckboxItem
import dev.hansholz.advancedmenubar.MacCocoaMenuBar.CustomItem
import dev.hansholz.advancedmenubar.MacCocoaMenuBar.EditStd
import dev.hansholz.advancedmenubar.MacCocoaMenuBar.FileStd
import dev.hansholz.advancedmenubar.MacCocoaMenuBar.FormatStd
import dev.hansholz.advancedmenubar.MacCocoaMenuBar.HelpItem
import dev.hansholz.advancedmenubar.MacCocoaMenuBar.MenuElement
import dev.hansholz.advancedmenubar.MacCocoaMenuBar.SectionHeader
import dev.hansholz.advancedmenubar.MacCocoaMenuBar.Separator
import dev.hansholz.advancedmenubar.MacCocoaMenuBar.Submenu
import dev.hansholz.advancedmenubar.MacCocoaMenuBar.SystemItem
import dev.hansholz.advancedmenubar.MacCocoaMenuBar.TopMenu
import dev.hansholz.advancedmenubar.MacCocoaMenuBar.ViewStd
import dev.hansholz.advancedmenubar.MacCocoaMenuBar.WindowStd
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.skiko.hostOs
import javax.swing.SwingUtilities

@Composable
fun FrameWindowScope.CompatibilityMenuBar(
    appName: String = window.title,
    content: AdvancedMenuScope.() -> Unit
) {
    val strings = Res.allStringResources.map {
        it.value to stringResource(it.value, appName)
    }

    val scope = AdvancedMenuScope(strings).apply {
        reset()
        content()
    }
    val model = scope.menus.toList()

    if (hostOs.isMacOS) {
        LaunchedEffect(model) {
            SwingUtilities.invokeLater {
                MacCocoaMenuBar.rebuildMenuBar(model)
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

            is SectionHeader -> Item(el.title, enabled = false) {}

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
            is EditStd.FindAndReplace          -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.FindNext                -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.FindPrevious            -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.UseSelectionForFind     -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
            is EditStd.JumpToSelection         -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
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
            is FormatStd.BaselineStandard      -> Item(el.title, enabled = el.enabled) { el.onClick?.invoke() }
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

enum class MenuVisibility {
    MACOS_ONLY,
    WINDOWS_AND_LINUX_ONLY,
    ALWAYS_VISIBLE,
}

private fun ifVisible(visibility: MenuVisibility, block: () -> Unit) {
    if (
        when (visibility) {
            MenuVisibility.MACOS_ONLY -> hostOs.isMacOS
            MenuVisibility.WINDOWS_AND_LINUX_ONLY -> !hostOs.isMacOS
            MenuVisibility.ALWAYS_VISIBLE -> true
        }
    ) {
        block()
    }
}

fun compatibilityOnClick(
    onMacClick: (() -> Unit)? = null,
    onNonMacClick: (() -> Unit),
): (() -> Unit)? =
    if (hostOs.isMacOS && onMacClick != null) {
        onMacClick
    } else if (!hostOs.isMacOS) {
        onNonMacClick
    } else {
        null
    }

@MenuDsl
class AdvancedMenuScope(private val strings: List<Pair<StringResource, String>>) {
    internal val menus = mutableListOf<TopMenu>()
    private var hasApp = false
    private var hasFile = false
    private var hasEdit = false
    private var hasFormat = false
    private var hasView = false
    private var hasWindow = false
    private var hasHelp = false

    internal fun reset() {
        menus.clear()
        hasApp = false
        hasFile = false
        hasEdit = false
        hasFormat = false
        hasView = false
        hasWindow = false
        hasHelp = false
    }

    private fun getString(stringResource: StringResource): String =
        strings.find { it.first == stringResource }?.second ?: "STRING NOT FOUND"

    @MenuDsl
    class CompatibilityMenuScope(private val strings: List<Pair<StringResource, String>>) {
        internal val elements = mutableListOf<MenuElement>()

        private fun getString(stringResource: StringResource): String =
            strings.find { it.first == stringResource }?.second ?: "STRING NOT FOUND"

        /** MacOS only **/
        fun About(
            title: String = getString(Res.string.about),
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += SystemItem.About(title, enabled, macIcon, onClick) }

        /** MacOS only **/
        fun Settings(
            title: String = getString(Res.string.settings),
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += SystemItem.Settings(title, enabled, macIcon, onClick) }

        /** MacOS only **/
        fun Services(title: String = getString(Res.string.services)) {
            elements += SystemItem.Services(title)
        }

        /** MacOS only **/
        fun Hide(
            title: String = getString(Res.string.hide),
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += SystemItem.Hide(title, enabled, macIcon, onClick) }

        /** MacOS only **/
        fun HideOthers(
            title: String = getString(Res.string.hide_others),
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += SystemItem.HideOthers(title, enabled, macIcon, onClick) }

        /** MacOS only **/
        fun ShowAll(
            title: String = getString(Res.string.show_all),
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += SystemItem.ShowAll(title, enabled, macIcon, onClick) }

        /** MacOS only **/
        fun Quit(
            title: String = getString(Res.string.quit),
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += SystemItem.Quit(title, enabled, macIcon, onClick) }


        fun FileNew(
            title: String = getString(Res.string.file_new),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?,
        ) = ifVisible(visibility) { elements += FileStd.New(title, enabled, macIcon, onClick) }

        fun FileOpen(
            title: String = getString(Res.string.file_open),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?,
        ) = ifVisible(visibility) { elements += FileStd.Open(title, enabled, macIcon, onClick) }

        fun FileOpenRecent(
            title: String = getString(Res.string.file_open_recent),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            content: CompatibilityMenuScope.() -> Unit
        ) = ifVisible(visibility) {
            val s = CompatibilityMenuScope(strings)
            s.content()
            elements += FileStd.OpenRecent(title, s.elements.toList(), enabled, macIcon)
        }

        fun FileClearRecent(
            title: String = getString(Res.string.file_clear_recent),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?,
        ) = ifVisible(visibility) { elements += FileStd.ClearRecent(title, enabled, macIcon, onClick) }

        fun FileClose(
            title: String = getString(Res.string.file_close),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) = ifVisible(visibility) { elements += FileStd.Close(title, enabled, macIcon, onClick) }

        fun FileCloseAll(
            title: String = getString(Res.string.file_close_all),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) = ifVisible(visibility) { elements += FileStd.CloseAll(title, enabled, macIcon, onClick) }

        fun FileSave(
            title: String = getString(Res.string.file_save),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?,
        ) = ifVisible(visibility) { elements += FileStd.Save(title, enabled, macIcon, onClick) }

        fun FileSaveAs(
            title: String = getString(Res.string.file_save_as),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?,
        ) = ifVisible(visibility) { elements += FileStd.SaveAs(title, enabled, macIcon, onClick) }

        fun FileDuplicate(
            title: String = getString(Res.string.file_duplicate),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?,
        ) = ifVisible(visibility) { elements += FileStd.Duplicate(title, enabled, macIcon, onClick) }

        fun FileRename(
            title: String = getString(Res.string.file_rename),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?,
        ) = ifVisible(visibility) { elements += FileStd.Rename(title, enabled, macIcon, onClick) }

        fun FileMoveTo(
            title: String = getString(Res.string.file_move_to),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?,
        ) = ifVisible(visibility) { elements += FileStd.MoveTo(title, enabled, macIcon, onClick) }

        fun FilePageSetup(
            title: String = getString(Res.string.file_page_setup),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) = ifVisible(visibility) { elements += FileStd.PageSetup(title, enabled, macIcon, onClick) }

        fun FilePrint(
            title: String = getString(Res.string.file_print),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?,
        ) = ifVisible(visibility) { elements += FileStd.Print(title, enabled, macIcon, onClick) }


        fun Undo(
            title: String = getString(Res.string.edit_undo),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) = ifVisible(visibility) { elements += EditStd.Undo(title, enabled, macIcon, onClick) }

        fun Redo(
            title: String = getString(Res.string.edit_redo),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) = ifVisible(visibility) { elements += EditStd.Redo(title, enabled, macIcon, onClick) }

        fun Cut(
            title: String = getString(Res.string.edit_cut),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) = ifVisible(visibility) { elements += EditStd.Cut(title, enabled, macIcon, onClick) }

        fun Copy(
            title: String = getString(Res.string.edit_copy),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) = ifVisible(visibility) { elements += EditStd.Copy(title, enabled, macIcon, onClick) }

        fun Paste(
            title: String = getString(Res.string.edit_paste),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) = ifVisible(visibility) { elements += EditStd.Paste(title, enabled, macIcon, onClick) }

        fun PasteAndMatchStyle(
            title: String = getString(Res.string.paste_and_match_style),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) = ifVisible(visibility) { elements += EditStd.PasteAndMatchStyle(title, enabled, macIcon, onClick) }

        fun Delete(
            title: String = getString(Res.string.edit_delete),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) = ifVisible(visibility) { elements += EditStd.Delete(title, enabled, macIcon, onClick) }

        fun SelectAll(
            title: String = getString(Res.string.edit_select_all),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) = ifVisible(visibility) { elements += EditStd.SelectAll(title, enabled, macIcon, onClick) }


        fun FindMenu(
            title: String = getString(Res.string.find),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            block: CompatibilityMenuScope.() -> Unit
        ) = Menu(title, visibility, enabled, macIcon, null, null, block)

        fun Find(
            title: String = getString(Res.string.find_dots),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) = ifVisible(visibility) { elements += EditStd.Find(title, enabled, macIcon, onClick) }

        fun FindAndReplace(
            title: String = getString(Res.string.find_and_replace),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) = ifVisible(visibility) { elements += EditStd.FindAndReplace(title, enabled, macIcon, onClick) }

        fun FindNext(
            title: String = getString(Res.string.find_next),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) = ifVisible(visibility) { elements += EditStd.FindNext(title, enabled, macIcon, onClick) }

        fun FindPrevious(
            title: String = getString(Res.string.find_previous),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) = ifVisible(visibility) { elements += EditStd.FindPrevious(title, enabled, macIcon, onClick) }

        fun UseSelectionForFind(
            title: String = getString(Res.string.use_selection_for_find),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) = ifVisible(visibility) { elements += EditStd.UseSelectionForFind(title, enabled, macIcon, onClick) }

        fun JumpToSelection(
            title: String = getString(Res.string.jump_to_selection),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) = ifVisible(visibility) { elements += EditStd.JumpToSelection(title, enabled, macIcon, onClick) }


        fun SpellingAndGrammarMenu(
            title: String = getString(Res.string.spelling_and_grammar),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            block: CompatibilityMenuScope.() -> Unit
        ) = Menu(title, visibility, enabled, macIcon, null, null, block)

        fun ToggleCorrectSpellingAutomatically(
            title: String = getString(Res.string.toggle_correct_spelling_automatically),
            checked: Boolean = false,
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onToggle: (Boolean) -> Unit
        ) = ifVisible(visibility) { elements += EditStd.ToggleSpellingCorrection(title, enabled, macIcon, checked, onToggle) }


        fun SubstitutionsMenu(
            title: String = getString(Res.string.substitutions),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            block: CompatibilityMenuScope.() -> Unit
        ) = Menu(title, visibility, enabled, macIcon, null, null, block)

        fun ToggleSmartQuotes(
            title: String = getString(Res.string.toggle_smart_quotes),
            checked: Boolean = false,
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onToggle: (Boolean) -> Unit
        ) = ifVisible(visibility) { elements += EditStd.ToggleSmartQuotes(title, enabled, macIcon, checked, onToggle) }

        fun ToggleSmartDashes(
            title: String = getString(Res.string.toggle_smart_dashes),
            checked: Boolean = false,
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onToggle: (Boolean) -> Unit
        ) = ifVisible(visibility) { elements += EditStd.ToggleSmartDashes(title, enabled, macIcon, checked, onToggle) }

        fun ToggleSmartLinks(
            title: String = getString(Res.string.toggle_smart_links),
            checked: Boolean = false,
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onToggle: (Boolean) -> Unit
        ) = ifVisible(visibility) { elements += EditStd.ToggleLinkDetection(title, enabled, macIcon, checked, onToggle) }

        fun ToggleTextReplacement(
            title: String = getString(Res.string.toggle_text_replacement),
            checked: Boolean = false,
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onToggle: ((Boolean) -> Unit)
        ) = ifVisible(visibility) { elements += EditStd.ToggleTextReplacement(title, enabled, macIcon, checked, onToggle) }


        fun TransformationsMenu(
            title: String = getString(Res.string.transformations),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            block: CompatibilityMenuScope.() -> Unit
        ) = Menu(title, visibility, enabled, macIcon, null, null, block)

        fun MakeUpperCase(
            title: String = getString(Res.string.make_upper_case),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) = ifVisible(visibility) { elements += EditStd.Uppercase(title, enabled, macIcon, onClick) }

        fun MakeLowerCase(
            title: String = getString(Res.string.make_lower_case),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) = ifVisible(visibility) { elements += EditStd.Lowercase(title, enabled, macIcon, onClick) }

        fun Capitalize(
            title: String = getString(Res.string.capitalize),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) = ifVisible(visibility) { elements += EditStd.Capitalize(title, enabled, macIcon, onClick) }


        fun SpeechMenu(
            title: String = getString(Res.string.speech),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            block: CompatibilityMenuScope.() -> Unit
        ) = Menu(title, visibility, enabled, macIcon, null, null, block)

        fun StartSpeaking(
            title: String = getString(Res.string.start_speaking),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) = ifVisible(visibility) { elements += EditStd.StartSpeaking(title, enabled, macIcon, onClick) }

        fun StopSpeaking(
            title: String = getString(Res.string.stop_speaking),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) = ifVisible(visibility) { elements += EditStd.StopSpeaking(title, enabled, macIcon, onClick) }


        fun ShowFonts(
            title: String = getString(Res.string.show_fonts),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) = ifVisible(visibility) { elements += FormatStd.ShowFonts(title, enabled, macIcon, onClick) }

        fun ShowColors(
            title: String = getString(Res.string.show_colors),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) = ifVisible(visibility) { elements += FormatStd.ShowColors(title, enabled, macIcon, onClick) }


        fun FontMenu(
            title: String = getString(Res.string.font),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            block: CompatibilityMenuScope.() -> Unit
        ) = Menu(title, visibility, enabled, macIcon, null, null, block)

        fun Bold(
            title: String = getString(Res.string.bold),
            checked: Boolean = false,
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onToggle: (Boolean) -> Unit
        ) = ifVisible(visibility) { elements += FormatStd.Bold(title, enabled, macIcon, checked, onToggle) }

        fun Italic(
            title: String = getString(Res.string.italic),
            checked: Boolean = false,
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onToggle: (Boolean) -> Unit
        ) = ifVisible(visibility) { elements += FormatStd.Italic(title, enabled, macIcon, checked, onToggle) }

        fun Underline(
            title: String = getString(Res.string.underline),
            checked: Boolean = false,
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onToggle: (Boolean) -> Unit
        ) = ifVisible(visibility) { elements += FormatStd.Underline(title, enabled, macIcon, checked, onToggle) }

        fun Bigger(
            title: String = getString(Res.string.bigger),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?
        ) = ifVisible(visibility) { elements += FormatStd.Bigger(title, enabled, macIcon, onClick) }

        fun Smaller(
            title: String = getString(Res.string.smaller),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?
        ) = ifVisible(visibility) { elements += FormatStd.Smaller(title, enabled, macIcon, onClick) }


        fun KerningMenu(
            title: String = getString(Res.string.kerning),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            block: CompatibilityMenuScope.() -> Unit
        ) = Menu(title, visibility, enabled, macIcon, null, null, block)

        fun KerningStandard(
            title: String = getString(Res.string.kerning_standard),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?
        ) = ifVisible(visibility) { elements += FormatStd.KerningStandard(title, enabled, macIcon, onClick) }

        fun KerningNone(
            title: String = getString(Res.string.kerning_none),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?
        ) = ifVisible(visibility) { elements += FormatStd.KerningNone(title, enabled, macIcon, onClick) }

        fun KerningTighten(
            title: String = getString(Res.string.kerning_tighten),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?
        ) = ifVisible(visibility) { elements += FormatStd.KerningTighten(title, enabled, macIcon, onClick) }

        fun KerningLoosen(
            title: String = getString(Res.string.kerning_loosen),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?
        ) = ifVisible(visibility) { elements += FormatStd.KerningLoosen(title, enabled, macIcon, onClick) }


        fun LigaturesMenu(
            title: String = getString(Res.string.ligatures),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            block: CompatibilityMenuScope.() -> Unit
        ) = Menu(title, visibility, enabled, macIcon, null, null, block)

        fun LigaturesNone(
            title: String = getString(Res.string.ligatures_none),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?
        ) = ifVisible(visibility) { elements += FormatStd.LigaturesNone(title, enabled, macIcon, onClick) }

        fun LigaturesStandard(
            title: String = getString(Res.string.ligatures_standard),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?
        ) = ifVisible(visibility) { elements += FormatStd.LigaturesStandard(title, enabled, macIcon, onClick) }

        fun LigaturesAll(
            title: String = getString(Res.string.ligatures_all),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?
        ) = ifVisible(visibility) { elements += FormatStd.LigaturesAll(title, enabled, macIcon, onClick) }


        fun BaselineMenu(
            title: String = getString(Res.string.baseline),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            block: CompatibilityMenuScope.() -> Unit
        ) = Menu(title, visibility, enabled, macIcon, null, null, block)


        fun BaselineStandard(
            title: String = getString(Res.string.baseline_standard),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?
        ) = ifVisible(visibility) { elements += FormatStd.RaiseBaseline(title, enabled, macIcon, onClick) }

        fun RaiseBaseline(
            title: String = getString(Res.string.raise_baseline),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?
        ) = ifVisible(visibility) { elements += FormatStd.RaiseBaseline(title, enabled, macIcon, onClick) }

        fun LowerBaseline(
            title: String = getString(Res.string.lower_baseline),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?
        ) = ifVisible(visibility) { elements += FormatStd.LowerBaseline(title, enabled, macIcon, onClick) }

        fun Superscript(
            title: String = getString(Res.string.superscript),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?
        ) = ifVisible(visibility) { elements += FormatStd.Superscript(title, enabled, macIcon, onClick) }

        fun Subscript(
            title: String = getString(Res.string.subscript),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?
        ) = ifVisible(visibility) { elements += FormatStd.Subscript(title, enabled, macIcon, onClick) }


        fun TextMenu(
            title: String = getString(Res.string.text),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            block: CompatibilityMenuScope.() -> Unit
        ) = Menu(title, visibility, enabled, macIcon, null, null, block)

        fun AlignLeft(
            title: String = getString(Res.string.align_left),
            checked: Boolean = false,
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onToggle: (Boolean) -> Unit
        ) = ifVisible(visibility) { elements += FormatStd.AlignLeft(title, enabled, macIcon, checked, onToggle) }

        fun AlignCenter(
            title: String = getString(Res.string.align_center),
            checked: Boolean = false,
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onToggle: (Boolean) -> Unit
        ) = ifVisible(visibility) { elements += FormatStd.AlignCenter(title, enabled, macIcon, checked, onToggle) }

        fun AlignRight(
            title: String = getString(Res.string.align_right),
            checked: Boolean = false,
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onToggle: (Boolean) -> Unit
        ) = ifVisible(visibility) { elements += FormatStd.AlignRight(title, enabled, macIcon, checked, onToggle) }

        fun AlignJustified(
            title: String = getString(Res.string.align_justified),
            checked: Boolean = false,
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onToggle: (Boolean) -> Unit
        ) = ifVisible(visibility) { elements += FormatStd.AlignJustified(title, enabled, macIcon, checked, onToggle) }


        fun ShowToolbar(
            state: Boolean,
            title: String = getString(if (state) Res.string.hide_toolbar else Res.string.show_toolbar),
            checked: Boolean = false,
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onToggle: (Boolean) -> Unit
        ) = ifVisible(visibility) { elements += ViewStd.ShowToolbar(title, enabled, macIcon, checked, onToggle) }

        fun CustomizeToolbar(
            title: String = getString(Res.string.customize_toolbar),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?
        ) = ifVisible(visibility) { elements += ViewStd.CustomizeToolbar(title, enabled, macIcon, onClick) }

        fun ToggleFullScreen(
            state: Boolean,
            title: String = getString(if (state) Res.string.exit_full_screen else Res.string.enter_full_screen),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) = ifVisible(visibility) { elements += ViewStd.ToggleFullScreen(title, enabled, macIcon, onClick) }

        fun ToggleSidebar(
            state: Boolean,
            title: String = getString(if (state) Res.string.hide_sidebar else Res.string.show_sidebar),
            checked: Boolean = false,
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onToggle: (Boolean) -> Unit
        ) = ifVisible(visibility) { elements += ViewStd.ToggleSidebar(title, enabled, macIcon, checked, onToggle) }

        fun ToggleTabBar(
            state: Boolean,
            title: String = getString(if (state) Res.string.hide_tab_bar else Res.string.show_tab_bar),
            checked: Boolean = false,
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onToggle: (Boolean) -> Unit
        ) = ifVisible(visibility) { elements += ViewStd.ToggleTabBar(title, enabled, macIcon, checked, onToggle) }


        fun Close(
            title: String = getString(Res.string.window_close),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) = ifVisible(visibility) { elements += WindowStd.Close(title, enabled, macIcon, onClick) }

        fun Minimize(
            title: String = getString(Res.string.window_minimize),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) = ifVisible(visibility) { elements += WindowStd.Minimize(title, enabled, macIcon, onClick) }

        fun MinimizeAll(
            title: String = getString(Res.string.window_minimize_all),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) = ifVisible(visibility) { elements += WindowStd.MinimizeAll(title, enabled, macIcon, onClick) }

        fun Zoom(
            title: String = getString(Res.string.window_zoom),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) = ifVisible(visibility) { elements += WindowStd.Zoom(title, enabled, macIcon, onClick) }

        fun BringAllToFront(
            title: String = getString(Res.string.bring_all_to_front),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) = ifVisible(visibility) { elements += WindowStd.BringAllToFront(title, enabled, macIcon, onClick) }

        fun ShowNextTab(
            title: String = getString(Res.string.show_next_tab),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?
        ) = ifVisible(visibility) { elements += WindowStd.ShowNextTab(title, enabled, macIcon, onClick) }

        fun ShowPreviousTab(
            title: String = getString(Res.string.show_previous_tab),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?
        ) = ifVisible(visibility) { elements += WindowStd.ShowPreviousTab(title, enabled, macIcon, onClick) }

        fun MergeAllWindows(
            title: String = getString(Res.string.merge_all_windows),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?
        ) = ifVisible(visibility) { elements += WindowStd.MergeAllWindows(title, enabled, macIcon, onClick) }

        fun MoveTabToNewWindow(
            title: String = getString(Res.string.move_tab_to_new_window),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)?
        ) = ifVisible(visibility) { elements += WindowStd.MoveTabToNewWindow(title, enabled, macIcon, onClick) }


        fun AppHelp(
            title: String = getString(Res.string.app_help),
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) = ifVisible(visibility) { elements += HelpItem.AppHelp(title, enabled, macIcon, onClick) }


        fun Item(
            title: String,
            shortcut: MenuShortcut? = null,
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            macSubtitle: String? = null,
            macTooltip: String? = null,
            macBadge: String? = null,
            onClick: () -> Unit
        ) = ifVisible(visibility) { elements += CustomItem(title, shortcut, enabled, macIcon, macSubtitle, macTooltip, macBadge, onClick) }

        fun Checkbox(
            title: String,
            checked: Boolean = false,
            shortcut: MenuShortcut? = null,
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            macSubtitle: String? = null,
            macTooltip: String? = null,
            macBadge: String? = null,
            onToggle: (Boolean) -> Unit
        ) = ifVisible(visibility) { elements += CheckboxItem(title, checked, shortcut, enabled, macIcon, macSubtitle, macTooltip, macBadge, onToggle) }

        fun Separator(visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE) = ifVisible(visibility) { elements += Separator }

        fun Menu(
            title: String,
            visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
            enabled: Boolean = true,
            macIcon: MenuIcon? = null,
            macSubtitle: String? = null,
            macBadge: String? = null,
            block: CompatibilityMenuScope.() -> Unit
        ) = ifVisible(visibility) {
            val s = CompatibilityMenuScope(strings)
            s.block()
            elements += Submenu(title, s.elements.toList(), enabled, macIcon, macSubtitle, macBadge)
        }

        private fun SectionHeader(title: String) {
            elements += MacCocoaMenuBar.SectionHeader(title)
        }

        fun CompatibilityMenuScope.Section(
            title: String,
            content: CompatibilityMenuScope.() -> Unit
        ) {
            Separator()
            SectionHeader(title)
            content()
            Separator()
        }
    }

    /** MacOS only **/
    fun MacApplicationMenu(content: CompatibilityMenuScope.() -> Unit) {
        if (hasApp) { println("[CompatibilityMenu] MacApplicationMenu already set – further call will be ignored."); return }
        val s = CompatibilityMenuScope(strings); s.content()
        menus += TopMenu.Application(s.elements.toList()); hasApp = true
    }

    fun FileMenu(
        title: String = getString(Res.string.file),
        visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
        content: CompatibilityMenuScope.() -> Unit
    ) = ifVisible(visibility) {
        if (hasFile) { println("[CompatibilityMenu] FileMenu already set – further call will be ignored."); return@ifVisible }
        val s = CompatibilityMenuScope(strings); s.content()
        menus += TopMenu.File(title, s.elements.toList()); hasFile = true
    }

    fun EditMenu(
        title: String = getString(Res.string.edit),
        visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
        content: CompatibilityMenuScope.() -> Unit
    ) = ifVisible(visibility) {
        if (hasEdit) { println("[CompatibilityMenu] EditMenu already set – further call will be ignored."); return@ifVisible }
        val s = CompatibilityMenuScope(strings); s.content()
        menus += TopMenu.Edit(title, s.elements.toList()); hasEdit = true
    }

    fun FormatMenu(
        title: String = getString(Res.string.format),
        visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
        content: CompatibilityMenuScope.() -> Unit
    ) = ifVisible(visibility) {
        if (hasFormat) { println("[CompatibilityMenu] FormatMenu already set – further call will be ignored."); return@ifVisible }
        val s = CompatibilityMenuScope(strings); s.content()
        menus += TopMenu.Format(title, s.elements.toList()); hasFormat = true
    }

    fun ViewMenu(
        title: String = getString(Res.string.view),
        visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
        content: CompatibilityMenuScope.() -> Unit
    ) = ifVisible(visibility) {
        if (hasView) { println("[CompatibilityMenu] ViewMenu already set – further call will be ignored."); return@ifVisible }
        val s = CompatibilityMenuScope(strings); s.content()
        menus += TopMenu.View(title, s.elements.toList()); hasView = true
    }

    fun WindowMenu(
        title: String = getString(Res.string.window),
        visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
        suppressAutoWindowList: Boolean = false,
        content: CompatibilityMenuScope.() -> Unit
    ) = ifVisible(visibility) {
        if (hasWindow) { println("[CompatibilityMenu] WindowMenu already set – further call will be ignored."); return@ifVisible }
        val s = CompatibilityMenuScope(strings); s.content()
        menus += TopMenu.Window(title, s.elements.toList(), suppressAutoWindowList); hasWindow = true
    }

    fun HelpMenu(
        title: String = getString(Res.string.help),
        visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
        content: CompatibilityMenuScope.() -> Unit
    ) = ifVisible(visibility) {
        if (hasHelp) { println("[CompatibilityMenu] HelpMenu already set – further call will be ignored."); return@ifVisible }
        val s = CompatibilityMenuScope(strings); s.content()
        menus += TopMenu.Help(title, s.elements.toList()); hasHelp = true
    }

    fun CustomMenu(
        title: String,
        visibility: MenuVisibility = MenuVisibility.ALWAYS_VISIBLE,
        content: CompatibilityMenuScope.() -> Unit
    ) = ifVisible(visibility) {
        val s = CompatibilityMenuScope(strings); s.content()
        menus += TopMenu.Custom(title, s.elements.toList())
    }
}