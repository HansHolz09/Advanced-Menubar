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
import dev.hansholz.advancedmenubar.MacCocoaMenu.HelpItem
import dev.hansholz.advancedmenubar.MacCocoaMenu.MenuElement
import dev.hansholz.advancedmenubar.MacCocoaMenu.MenuIcon
import dev.hansholz.advancedmenubar.MacCocoaMenu.Modifiers
import dev.hansholz.advancedmenubar.MacCocoaMenu.Separator
import dev.hansholz.advancedmenubar.MacCocoaMenu.Submenu
import dev.hansholz.advancedmenubar.MacCocoaMenu.SystemItem
import dev.hansholz.advancedmenubar.MacCocoaMenu.TextItem
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
    private var hasEdit = false
    private var hasView = false
    private var hasWindow = false
    private var hasHelp = false

    internal fun reset() {
        menus.clear()
        hasApp = false; hasEdit = false; hasView = false; hasWindow = false; hasHelp = false
    }

    private fun getString(stringResource: StringResource): String = strings.find { it.first == stringResource }?.second ?: "STRING NOT FOUND"

    @MenuDsl class MacMenuScope(private val strings: List<Pair<StringResource, String>>) {
        internal val elements = mutableListOf<MenuElement>()

        private fun getString(stringResource: StringResource): String = strings.find { it.first == stringResource }?.second ?: "STRING NOT FOUND"

        fun About(title: String = getString(Res.string.about), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)? = null) {
            elements += SystemItem.About(title, enabled, icon, onClick)
        }
        fun Settings(title: String = getString(Res.string.settings), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)? = null) {
            elements += SystemItem.Settings(title, enabled, icon, onClick)
        }
        fun Services(title: String = getString(Res.string.services)) {
            elements += SystemItem.Services(title)
        }
        fun Hide(title: String = getString(Res.string.hide), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)? = null) {
            elements += SystemItem.Hide(title, enabled, icon, onClick)
        }
        fun HideOthers(title: String = getString(Res.string.hide_others), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)? = null) {
            elements += SystemItem.HideOthers(title, enabled, icon, onClick)
        }
        fun ShowAll(title: String = getString(Res.string.show_all), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)? = null) {
            elements += SystemItem.ShowAll(title, enabled, icon, onClick)
        }
        fun Quit(title: String = getString(Res.string.quit), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)? = null) {
            elements += SystemItem.Quit(title, enabled, icon, onClick)
        }

        fun Undo(title: String = getString(Res.string.undo), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)) {
            elements += EditStd.Undo(title, enabled, icon, onClick)
        }
        fun Redo(title: String = getString(Res.string.redo), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)) {
            elements += EditStd.Redo(title, enabled, icon, onClick)
        }
        fun Cut(title: String = getString(Res.string.cut), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)) {
            elements += EditStd.Cut(title, enabled, icon, onClick)
        }
        fun Copy(title: String = getString(Res.string.copy), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)) {
            elements += EditStd.Copy(title, enabled, icon, onClick)
        }
        fun Paste(title: String = getString(Res.string.paste), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)) {
            elements += EditStd.Paste(title, enabled, icon, onClick)
        }
        fun PasteAndMatchStyle(title: String = getString(Res.string.paste_and_match_style), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)) {
            elements += EditStd.PasteAndMatchStyle(title, enabled, icon, onClick)
        }
        fun Delete(title: String = getString(Res.string.delete), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)) {
            elements += EditStd.Delete(title, enabled, icon, onClick)
        }
        fun SelectAll(title: String = getString(Res.string.select_all), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)) {
            elements += EditStd.SelectAll(title, enabled, icon, onClick)
        }

        fun ShowToolbar(title: String = getString(Res.string.show_toolbar), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)? = null) {
            elements += ViewStd.ShowToolbar(title, enabled, icon, onClick)
        }
        fun CustomizeToolbar(title: String = getString(Res.string.customize_toolbar), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)? = null) {
            elements += ViewStd.CustomizeToolbar(title, enabled, icon, onClick)
        }
        fun ToggleFullScreen(title: String = getString(Res.string.full_screen), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)? = null) {
            elements += ViewStd.ToggleFullScreen(title, enabled, icon, onClick)
        }

        fun Close(title: String = getString(Res.string.close), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)? = null) {
            elements += WindowStd.Close(title, enabled, icon, onClick)
        }
        fun Minimize(title: String = getString(Res.string.minimize), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)? = null) {
            elements += WindowStd.Minimize(title, enabled, icon, onClick)
        }
        fun MinimizeAll(title: String = getString(Res.string.minimize_all), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)? = null) {
            elements += WindowStd.MinimizeAll(title, enabled, icon, onClick)
        }
        fun Zoom(title: String = getString(Res.string.zoom), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)? = null) {
            elements += WindowStd.Zoom(title, enabled, icon, onClick)
        }
        fun BringAllToFront(title: String = getString(Res.string.bring_all_to_front), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)? = null) {
            elements += WindowStd.BringAllToFront(title, enabled, icon, onClick)
        }
        fun ShowNextTab(title: String = getString(Res.string.show_next_tab), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)? = null) {
            elements += WindowStd.ShowNextTab(title, enabled, icon, onClick)
        }
        fun ShowPreviousTab(title: String = getString(Res.string.show_previous_tab), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)? = null) {
            elements += WindowStd.ShowPreviousTab(title, enabled, icon, onClick)
        }
        fun MergeAllWindows(title: String = getString(Res.string.merge_all_windows), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)? = null) {
            elements += WindowStd.MergeAllWindows(title, enabled, icon, onClick)
        }
        fun MoveTabToNewWindow(title: String = getString(Res.string.move_tab_to_new_window), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)? = null) {
            elements += WindowStd.MoveTabToNewWindow(title, enabled, icon, onClick)
        }

        fun AppHelp(title: String = getString(Res.string.app_help), enabled: Boolean = true, icon: MenuIcon? = null, onClick: (() -> Unit)? = null) {
            elements += HelpItem.AppHelp(title, enabled, icon, onClick)
        }

        fun Item(title: String, key: String = "", modifiers: Long = Modifiers.none, enabled: Boolean = true, icon: MenuIcon? = null, onClick: () -> Unit) {
            elements += CustomItem(title, key, modifiers, enabled, icon, onClick)
        }
        fun Checkbox(title: String, checked: Boolean = false, key: String = "", modifiers: Long = Modifiers.none, enabled: Boolean = true, icon: MenuIcon? = null, onToggle: (Boolean) -> Unit) {
            elements += CheckboxItem(title, checked, key, modifiers, enabled, icon, onToggle)
        }
        fun Text(title: String, enabled: Boolean = false, icon: MenuIcon? = null) {
            elements += TextItem(title, enabled, icon)
        }
        fun Separator() { elements += Separator }
        fun Menu(title: String, enabled: Boolean = true, icon: MenuIcon? = null, block: MacMenuScope.() -> Unit) {
            val s = MacMenuScope(strings)
            s.block()
            elements += Submenu(title, s.elements.toList(), enabled, icon)
        }
    }

    fun MacApplicationMenu(content: MacMenuScope.() -> Unit) {
        if (hasApp) { println("[AdvancedMacMenu] MacApplicationMenu bereits gesetzt – weiterer Aufruf wird ignoriert."); return }
        val s = MacMenuScope(strings); s.content()
        menus += TopMenu.Application(s.elements.toList()); hasApp = true
    }
    fun MacEditMenu(title: String = getString(Res.string.edit), content: MacMenuScope.() -> Unit) {
        if (hasEdit) { println("[AdvancedMacMenu] MacEditMenu bereits gesetzt – weiterer Aufruf wird ignoriert."); return }
        val s = MacMenuScope(strings); s.content()
        menus += TopMenu.Edit(title, s.elements.toList()); hasEdit = true
    }
    fun MacViewMenu(title: String = getString(Res.string.view), content: MacMenuScope.() -> Unit) {
        if (hasView) { println("[AdvancedMacMenu] MacViewMenu bereits gesetzt – weiterer Aufruf wird ignoriert."); return }
        val s = MacMenuScope(strings); s.content()
        menus += TopMenu.View(title, s.elements.toList()); hasView = true
    }
    fun MacWindowMenu(title: String = getString(Res.string.window), suppressAutoWindowList: Boolean = false, content: MacMenuScope.() -> Unit) {
        if (hasWindow) { println("[AdvancedMacMenu] MacWindowMenu bereits gesetzt – weiterer Aufruf wird ignoriert."); return }
        val s = MacMenuScope(strings); s.content()
        menus += TopMenu.Window(title, s.elements.toList(), suppressAutoWindowList); hasWindow = true
    }
    fun MacHelpMenu(title: String = getString(Res.string.help), content: MacMenuScope.() -> Unit) {
        if (hasHelp) { println("[AdvancedMacMenu] MacHelpMenu bereits gesetzt – weiterer Aufruf wird ignoriert."); return }
        val s = MacMenuScope(strings); s.content()
        menus += TopMenu.Help(title, s.elements.toList()); hasHelp = true
    }
    fun MacCustomMenu(title: String, content: MacMenuScope.() -> Unit) {
        val s = MacMenuScope(strings); s.content()
        menus += TopMenu.Custom(title, s.elements.toList())
    }
}