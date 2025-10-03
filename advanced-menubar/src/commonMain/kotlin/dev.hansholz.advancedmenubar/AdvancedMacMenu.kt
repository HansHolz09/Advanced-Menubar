package dev.hansholz.advancedmenubar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import composeadvancedmenubar.advanced_menubar.generated.resources.*
import dev.hansholz.advancedmenubar.MacCocoaMenu.CheckboxItem
import dev.hansholz.advancedmenubar.MacCocoaMenu.CustomItem
import dev.hansholz.advancedmenubar.MacCocoaMenu.EditStd
import dev.hansholz.advancedmenubar.MacCocoaMenu.FileStd
import dev.hansholz.advancedmenubar.MacCocoaMenu.FormatStd
import dev.hansholz.advancedmenubar.MacCocoaMenu.HelpItem
import dev.hansholz.advancedmenubar.MacCocoaMenu.MenuElement
import dev.hansholz.advancedmenubar.MacCocoaMenu.MenuIcon
import dev.hansholz.advancedmenubar.MacCocoaMenu.Modifiers
import dev.hansholz.advancedmenubar.MacCocoaMenu.Separator
import dev.hansholz.advancedmenubar.MacCocoaMenu.Submenu
import dev.hansholz.advancedmenubar.MacCocoaMenu.SystemItem
import dev.hansholz.advancedmenubar.MacCocoaMenu.TopMenu
import dev.hansholz.advancedmenubar.MacCocoaMenu.ViewStd
import dev.hansholz.advancedmenubar.MacCocoaMenu.WindowStd
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import javax.swing.SwingUtilities

@DslMarker
annotation class MenuDsl

@Composable
fun rememberMenuIconFrom(
    imageVector: ImageVector,
    sizeDp: Dp = 16.dp,
    template: Boolean = true
): MenuIcon {
    val density = LocalDensity.current
    val px = with(density) { sizeDp.roundToPx().coerceAtLeast(1) }
    val painter = rememberVectorPainter(imageVector)
    val bytes = remember(imageVector, px, density) {
        val ib = ImageBitmap(px, px)
        val canvas = Canvas(ib)
        val drawScope = CanvasDrawScope()
        drawScope.draw(
            density = Density(density.density, density.fontScale),
            layoutDirection = LayoutDirection.Ltr,
            canvas = canvas,
            size = Size(px.toFloat(), px.toFloat())
        ) { with(painter) { draw(Size(px.toFloat(), px.toFloat())) } }

        val pm = ib.toPixelMap()
        val awt = BufferedImage(px, px, BufferedImage.TYPE_INT_ARGB)
        for (y in 0 until px) for (x in 0 until px) awt.setRGB(x, y, pm[x, y].toArgb())
        val baos = ByteArrayOutputStream(); ImageIO.write(awt, "png", baos); baos.toByteArray()
    }
    return MenuIcon.Png(bytes, template)
}

@Composable
fun AdvancedMacMenu(appName: String, content: AdvancedMacMenuScope.() -> Unit) {
    val strings = Res.allStringResources.map {
        it.value to stringResource(it.value, appName)
    }

    val scope = remember { AdvancedMacMenuScope(strings) }
    scope.reset()
    scope.content()

    val model = remember(scope.menus.toList()) { scope.menus.toList() }
    LaunchedEffect(model) {
        SwingUtilities.invokeLater {
            MacCocoaMenu.rebuildMenuBar(model)
        }
    }
}



@MenuDsl
class AdvancedMacMenuScope(private val strings: List<Pair<StringResource, String>>) {
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
    class MacMenuScope(private val strings: List<Pair<StringResource, String>>) {
        internal val elements = mutableListOf<MenuElement>()

        private fun getString(stringResource: StringResource): String =
            strings.find { it.first == stringResource }?.second ?: "STRING NOT FOUND"

        fun About(
            title: String = getString(Res.string.about),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += SystemItem.About(title, enabled, icon, onClick) }

        fun Settings(
            title: String = getString(Res.string.settings),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += SystemItem.Settings(title, enabled, icon, onClick) }

        fun Services(title: String = getString(Res.string.services)) {
            elements += SystemItem.Services(title)
        }

        fun Hide(
            title: String = getString(Res.string.hide),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += SystemItem.Hide(title, enabled, icon, onClick) }

        fun HideOthers(
            title: String = getString(Res.string.hide_others),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += SystemItem.HideOthers(title, enabled, icon, onClick) }

        fun ShowAll(
            title: String = getString(Res.string.show_all),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += SystemItem.ShowAll(title, enabled, icon, onClick) }

        fun Quit(
            title: String = getString(Res.string.quit),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += SystemItem.Quit(title, enabled, icon, onClick) }


        fun FileNew(
            title: String = getString(Res.string.file_new),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FileStd.New(title, enabled, icon, onClick) }

        fun FileOpen(
            title: String = getString(Res.string.file_open),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FileStd.Open(title, enabled, icon, onClick) }

        fun FileOpenRecent(
            title: String = getString(Res.string.file_open_recent),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            content: MacMenuScope.() -> Unit
        ) {
            val s = MacMenuScope(strings)
            s.content()
            elements += FileStd.OpenRecent(title, s.elements.toList(), enabled, icon)
        }

        fun FileClearRecent(
            title: String = getString(Res.string.file_clear_recent),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FileStd.ClearRecent(title, enabled, icon, onClick) }

        fun FileClose(
            title: String = getString(Res.string.file_close),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FileStd.Close(title, enabled, icon, onClick) }

        fun FileCloseAll(
            title: String = getString(Res.string.file_close_all),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FileStd.CloseAll(title, enabled, icon, onClick) }

        fun FileSave(
            title: String = getString(Res.string.file_save),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FileStd.Save(title, enabled, icon, onClick) }

        fun FileSaveAs(
            title: String = getString(Res.string.file_save_as),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FileStd.SaveAs(title, enabled, icon, onClick) }

        fun FileDuplicate(
            title: String = getString(Res.string.file_duplicate),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FileStd.Duplicate(title, enabled, icon, onClick) }

        fun FileRename(
            title: String = getString(Res.string.file_rename),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FileStd.Rename(title, enabled, icon, onClick) }

        fun FileMoveTo(
            title: String = getString(Res.string.file_move_to),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FileStd.MoveTo(title, enabled, icon, onClick) }

        fun FileRevert(
            title: String = getString(Res.string.file_revert),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FileStd.Revert(title, enabled, icon, onClick) }

        fun FilePageSetup(
            title: String = getString(Res.string.file_page_setup),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FileStd.PageSetup(title, enabled, icon, onClick) }

        fun FilePrint(
            title: String = getString(Res.string.file_print),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FileStd.Print(title, enabled, icon, onClick) }


        fun Undo(
            title: String = getString(Res.string.undo),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.Undo(title, enabled, icon, onClick) }

        fun Redo(
            title: String = getString(Res.string.redo),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.Redo(title, enabled, icon, onClick) }

        fun Cut(
            title: String = getString(Res.string.cut),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.Cut(title, enabled, icon, onClick) }

        fun Copy(
            title: String = getString(Res.string.copy),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.Copy(title, enabled, icon, onClick) }

        fun Paste(
            title: String = getString(Res.string.paste),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.Paste(title, enabled, icon, onClick) }

        fun PasteAndMatchStyle(
            title: String = getString(Res.string.paste_and_match_style),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.PasteAndMatchStyle(title, enabled, icon, onClick) }

        fun Delete(
            title: String = getString(Res.string.delete),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.Delete(title, enabled, icon, onClick) }

        fun SelectAll(
            title: String = getString(Res.string.select_all),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.SelectAll(title, enabled, icon, onClick) }


        fun FindMenu(
            title: String = getString(Res.string.find),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            block: MacMenuScope.() -> Unit
        ) = Menu(title, enabled, icon, block)

        fun Find(
            title: String = getString(Res.string.find_dots),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.Find(title, enabled, icon, onClick) }

        fun FindNext(
            title: String = getString(Res.string.find_next),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.FindNext(title, enabled, icon, onClick) }

        fun FindPrevious(
            title: String = getString(Res.string.find_previous),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.FindPrevious(title, enabled, icon, onClick) }

        fun UseSelectionForFind(
            title: String = getString(Res.string.use_selection_for_find),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.UseSelectionForFind(title, enabled, icon, onClick) }

        fun JumpToSelection(
            title: String = getString(Res.string.jump_to_selection),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.JumpToSelection(title, enabled, icon, onClick) }

        fun Replace(
            title: String = getString(Res.string.replace),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.Replace(title, enabled, icon, onClick) }

        fun ReplaceAndFind(
            title: String = getString(Res.string.replace_and_find),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.ReplaceAndFind(title, enabled, icon, onClick) }

        fun ReplaceAll(
            title: String = getString(Res.string.replace_all),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.ReplaceAll(title, enabled, icon, onClick) }


        fun SubstitutionsMenu(
            title: String = getString(Res.string.substitutions),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            block: MacMenuScope.() -> Unit
        ) = Menu(title, enabled, icon, block)

        fun ToggleSmartQuotes(
            title: String = getString(Res.string.toggle_smart_quotes),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.ToggleSmartQuotes(title, enabled, icon, onClick) }

        fun ToggleSmartDashes(
            title: String = getString(Res.string.toggle_smart_dashes),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.ToggleSmartDashes(title, enabled, icon, onClick) }

        fun ToggleSmartLinks(
            title: String = getString(Res.string.toggle_smart_links),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.ToggleLinkDetection(title, enabled, icon, onClick) }

        fun ToggleTextReplacement(
            title: String = getString(Res.string.toggle_text_replacement),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.ToggleTextReplacement(title, enabled, icon, onClick) }

        fun ToggleCorrectSpellingAutomatically(
            title: String = getString(Res.string.toggle_correct_spelling_automatically),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.ToggleSpellingCorrection(title, enabled, icon, onClick) }


        fun TransformationsMenu(
            title: String = getString(Res.string.transformations),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            block: MacMenuScope.() -> Unit
        ) = Menu(title, enabled, icon, block)

        fun MakeUpperCase(
            title: String = getString(Res.string.make_upper_case),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.Uppercase(title, enabled, icon, onClick) }

        fun MakeLowerCase(
            title: String = getString(Res.string.make_lower_case),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.Lowercase(title, enabled, icon, onClick) }

        fun Capitalize(
            title: String = getString(Res.string.capitalize),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.Capitalize(title, enabled, icon, onClick) }


        fun SpeechMenu(
            title: String = getString(Res.string.speech),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            block: MacMenuScope.() -> Unit
        ) = Menu(title, enabled, icon, block)

        fun StartSpeaking(
            title: String = getString(Res.string.start_speaking),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.StartSpeaking(title, enabled, icon, onClick) }

        fun StopSpeaking(
            title: String = getString(Res.string.stop_speaking),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)
        ) { elements += EditStd.StopSpeaking(title, enabled, icon, onClick) }


        fun ShowFonts(
            title: String = getString(Res.string.show_fonts),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FormatStd.ShowFonts(title, enabled, icon, onClick) }

        fun ShowColors(
            title: String = getString(Res.string.show_colors),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FormatStd.ShowColors(title, enabled, icon, onClick) }

        fun Bold(
            title: String = getString(Res.string.bold),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FormatStd.Bold(title, enabled, icon, onClick) }

        fun Italic(
            title: String = getString(Res.string.italic),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FormatStd.Italic(title, enabled, icon, onClick) }

        fun Underline(
            title: String = getString(Res.string.underline),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FormatStd.Underline(title, enabled, icon, onClick) }

        fun Bigger(
            title: String = getString(Res.string.bigger),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FormatStd.Bigger(title, enabled, icon, onClick) }

        fun Smaller(
            title: String = getString(Res.string.smaller),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FormatStd.Smaller(title, enabled, icon, onClick) }

        fun KerningStandard(
            title: String = getString(Res.string.kerning_standard),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FormatStd.KerningStandard(title, enabled, icon, onClick) }

        fun KerningNone(
            title: String = getString(Res.string.kerning_none),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FormatStd.KerningNone(title, enabled, icon, onClick) }

        fun KerningTighten(
            title: String = getString(Res.string.kerning_tighten),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FormatStd.KerningTighten(title, enabled, icon, onClick) }

        fun KerningLoosen(
            title: String = getString(Res.string.kerning_loosen),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FormatStd.KerningLoosen(title, enabled, icon, onClick) }

        fun LigaturesNone(
            title: String = getString(Res.string.ligatures_none),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FormatStd.LigaturesNone(title, enabled, icon, onClick) }

        fun LigaturesStandard(
            title: String = getString(Res.string.ligatures_standard),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FormatStd.LigaturesStandard(title, enabled, icon, onClick) }

        fun LigaturesAll(
            title: String = getString(Res.string.ligatures_all),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FormatStd.LigaturesAll(title, enabled, icon, onClick) }

        fun RaiseBaseline(
            title: String = getString(Res.string.raise_baseline),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FormatStd.RaiseBaseline(title, enabled, icon, onClick) }

        fun LowerBaseline(
            title: String = getString(Res.string.lower_baseline),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FormatStd.LowerBaseline(title, enabled, icon, onClick) }

        fun Superscript(
            title: String = getString(Res.string.superscript),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FormatStd.Superscript(title, enabled, icon, onClick) }

        fun Subscript(
            title: String = getString(Res.string.subscript),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FormatStd.Subscript(title, enabled, icon, onClick) }

        fun AlignLeft(
            title: String = getString(Res.string.align_left),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FormatStd.AlignLeft(title, enabled, icon, onClick) }

        fun AlignCenter(
            title: String = getString(Res.string.align_center),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FormatStd.AlignCenter(title, enabled, icon, onClick) }

        fun AlignRight(
            title: String = getString(Res.string.align_right),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FormatStd.AlignRight(title, enabled, icon, onClick) }

        fun AlignJustified(
            title: String = getString(Res.string.align_justified),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += FormatStd.AlignJustified(title, enabled, icon, onClick) }


        fun ShowToolbar(
            title: String = getString(Res.string.show_toolbar),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += ViewStd.ShowToolbar(title, enabled, icon, onClick) }

        fun CustomizeToolbar(
            title: String = getString(Res.string.customize_toolbar),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += ViewStd.CustomizeToolbar(title, enabled, icon, onClick) }

        fun ToggleFullScreen(
            title: String = getString(Res.string.full_screen),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += ViewStd.ToggleFullScreen(title, enabled, icon, onClick) }

        fun ToggleSidebar(
            title: String = getString(Res.string.show_sidebar),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += ViewStd.ToggleSidebar(title, enabled, icon, onClick) }

        fun ToggleTabBar(
            title: String = getString(Res.string.show_tab_bar),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += ViewStd.ToggleTabBar(title, enabled, icon, onClick) }


        fun Close(
            title: String = getString(Res.string.close),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += WindowStd.Close(title, enabled, icon, onClick) }

        fun Minimize(
            title: String = getString(Res.string.minimize),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += WindowStd.Minimize(title, enabled, icon, onClick) }

        fun MinimizeAll(
            title: String = getString(Res.string.minimize_all),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += WindowStd.MinimizeAll(title, enabled, icon, onClick) }

        fun Zoom(
            title: String = getString(Res.string.zoom),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += WindowStd.Zoom(title, enabled, icon, onClick) }

        fun BringAllToFront(
            title: String = getString(Res.string.bring_all_to_front),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += WindowStd.BringAllToFront(title, enabled, icon, onClick) }

        fun ShowNextTab(
            title: String = getString(Res.string.show_next_tab),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += WindowStd.ShowNextTab(title, enabled, icon, onClick) }

        fun ShowPreviousTab(
            title: String = getString(Res.string.show_previous_tab),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += WindowStd.ShowPreviousTab(title, enabled, icon, onClick) }

        fun MergeAllWindows(
            title: String = getString(Res.string.merge_all_windows),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += WindowStd.MergeAllWindows(title, enabled, icon, onClick) }

        fun MoveTabToNewWindow(
            title: String = getString(Res.string.move_tab_to_new_window),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += WindowStd.MoveTabToNewWindow(title, enabled, icon, onClick) }


        fun AppHelp(
            title: String = getString(Res.string.app_help),
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: (() -> Unit)? = null
        ) { elements += HelpItem.AppHelp(title, enabled, icon, onClick) }


        fun Item(
            title: String,
            key: String = "",
            modifiers: Long = Modifiers.none,
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onClick: () -> Unit
        ) { elements += CustomItem(title, key, modifiers, enabled, icon, onClick) }

        fun Checkbox(
            title: String,
            checked: Boolean = false,
            key: String = "",
            modifiers: Long = Modifiers.none,
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            onToggle: (Boolean) -> Unit
        ) { elements += CheckboxItem(title, checked, key, modifiers, enabled, icon, onToggle) }

        fun Separator() { elements += Separator }

        fun Menu(
            title: String,
            enabled: Boolean = true,
            icon: MenuIcon? = null,
            block: MacMenuScope.() -> Unit
        ) {
            val s = MacMenuScope(strings)
            s.block()
            elements += Submenu(title, s.elements.toList(), enabled, icon)
        }
    }

    fun MacApplicationMenu(content: MacMenuScope.() -> Unit) {
        if (hasApp) { println("[AdvancedMacMenu] MacApplicationMenu already set – further call will be ignored."); return }
        val s = MacMenuScope(strings); s.content()
        menus += TopMenu.Application(s.elements.toList()); hasApp = true
    }

    fun MacFileMenu(title: String = getString(Res.string.file), content: MacMenuScope.() -> Unit) {
        if (hasFile) { println("[AdvancedMacMenu] MacFileMenu already set – further call will be ignored."); return }
        val s = MacMenuScope(strings); s.content()
        menus += TopMenu.File(title, s.elements.toList()); hasFile = true
    }

    fun MacEditMenu(title: String = getString(Res.string.edit), content: MacMenuScope.() -> Unit) {
        if (hasEdit) { println("[AdvancedMacMenu] MacEditMenu already set – further call will be ignored."); return }
        val s = MacMenuScope(strings); s.content()
        menus += TopMenu.Edit(title, s.elements.toList()); hasEdit = true
    }

    fun MacFormatMenu(title: String = getString(Res.string.format), content: MacMenuScope.() -> Unit) {
        if (hasFormat) { println("[AdvancedMacMenu] MacFormatMenu already set – further call will be ignored."); return }
        val s = MacMenuScope(strings); s.content()
        menus += TopMenu.Format(title, s.elements.toList()); hasFormat = true
    }

    fun MacViewMenu(title: String = getString(Res.string.view), content: MacMenuScope.() -> Unit) {
        if (hasView) { println("[AdvancedMacMenu] MacViewMenu already set – further call will be ignored."); return }
        val s = MacMenuScope(strings); s.content()
        menus += TopMenu.View(title, s.elements.toList()); hasView = true
    }

    fun MacWindowMenu(
        title: String = getString(Res.string.window),
        suppressAutoWindowList: Boolean = false,
        content: MacMenuScope.() -> Unit
    ) {
        if (hasWindow) { println("[AdvancedMacMenu] MacWindowMenu already set – further call will be ignored."); return }
        val s = MacMenuScope(strings); s.content()
        menus += TopMenu.Window(title, s.elements.toList(), suppressAutoWindowList); hasWindow = true
    }

    fun MacHelpMenu(title: String = getString(Res.string.help), content: MacMenuScope.() -> Unit) {
        if (hasHelp) { println("[AdvancedMacMenu] MacHelpMenu already set – further call will be ignored."); return }
        val s = MacMenuScope(strings); s.content()
        menus += TopMenu.Help(title, s.elements.toList()); hasHelp = true
    }

    fun MacCustomMenu(title: String, content: MacMenuScope.() -> Unit) {
        val s = MacMenuScope(strings); s.content()
        menus += TopMenu.Custom(title, s.elements.toList())
    }
}