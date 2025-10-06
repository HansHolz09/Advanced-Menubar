package dev.hansholz.advancedmenubar

import com.sun.jna.*
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap

object MacCocoaMenu {
    interface ObjC : Library {
        fun objc_getClass(name: String): Pointer
        fun sel_registerName(name: String): Pointer

        fun objc_msgSend(receiver: Pointer, selector: Pointer): Pointer
        fun objc_msgSend(receiver: Pointer, selector: Pointer, arg: Pointer): Pointer
        fun objc_msgSend(receiver: Pointer, selector: Pointer, arg: Long): Pointer
        fun objc_msgSend(receiver: Pointer, selector: Pointer, arg1: Pointer, arg2: Pointer): Pointer
        fun objc_msgSend(receiver: Pointer, selector: Pointer, arg1: Pointer, arg2: Long): Pointer

        fun objc_allocateClassPair(supercls: Pointer, name: String, extraBytes: Long): Pointer
        fun objc_registerClassPair(cls: Pointer)
        fun class_addMethod(cls: Pointer, name: Pointer, imp: Callback, types: String): Byte
    }
    private val objc: ObjC = Native.load("objc", ObjC::class.java)
    private fun sel(name: String): Pointer = objc.sel_registerName(name)

    interface Dispatch : Library {
        fun dispatch_get_main_queue(): Pointer
        fun dispatch_sync_f(queue: Pointer, context: Pointer, work: DispatchFunction)
    }
    interface DispatchFunction : Callback { fun invoke(context: Pointer) }
    private val dispatch: Dispatch? by lazy {
        try { Native.load("dispatch", Dispatch::class.java) } catch (_: Throwable) { null }
    }

    sealed interface MenuElement
    data object Separator : MenuElement

    sealed class SystemItem : MenuElement {
        data class About(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : SystemItem()
        data class Settings(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : SystemItem()
        data class Services(val title: String) : SystemItem()
        data class Hide(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : SystemItem()
        data class HideOthers(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : SystemItem()
        data class ShowAll(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : SystemItem()
        data class Quit(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : SystemItem()
    }

    sealed class FileStd : MenuElement {
        data class New(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FileStd()
        data class Open(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FileStd()
        data class OpenRecent(val title: String, val children: List<MenuElement> = emptyList(), val enabled: Boolean = true, val icon: MenuIcon? = null): FileStd()
        data class Close(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FileStd()
        data class CloseAll(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FileStd()
        data class Save(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FileStd()
        data class SaveAs(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FileStd()
        data class Duplicate(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FileStd()
        data class Rename(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FileStd()
        data class MoveTo(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FileStd()
        data class Revert(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FileStd()
        data class PageSetup(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FileStd()
        data class Print(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FileStd()
        data class ClearRecent(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FileStd()
    }

    sealed class EditStd : MenuElement {
        data class Undo(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()
        data class Redo(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()
        data class Cut(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()
        data class Copy(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()
        data class Paste(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()
        data class PasteAndMatchStyle(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()
        data class Delete(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()
        data class SelectAll(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()

        data class Find(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()
        data class FindNext(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()
        data class FindPrevious(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()
        data class UseSelectionForFind(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()
        data class JumpToSelection(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()
        data class Replace(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()
        data class ReplaceAndFind(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()
        data class ReplaceAll(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()

        data class ToggleSmartQuotes(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val checked: Boolean? = null, val onToggle: ((Boolean) -> Unit)? = null): EditStd()
        data class ToggleSmartDashes(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val checked: Boolean? = null, val onToggle: ((Boolean) -> Unit)? = null): EditStd()
        data class ToggleLinkDetection(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val checked: Boolean? = null, val onToggle: ((Boolean) -> Unit)? = null): EditStd()
        data class ToggleTextReplacement(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val checked: Boolean? = null, val onToggle: ((Boolean) -> Unit)? = null): EditStd()
        data class ToggleSpellingCorrection(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val checked: Boolean? = null, val onToggle: ((Boolean) -> Unit)? = null): EditStd()

        data class Uppercase(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): EditStd()
        data class Lowercase(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): EditStd()
        data class Capitalize(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): EditStd()

        data class StartSpeaking(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): EditStd()
        data class StopSpeaking(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): EditStd()
    }

    sealed class FormatStd : MenuElement {
        data class ShowFonts(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FormatStd()
        data class ShowColors(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FormatStd()

        data class Bold(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val checked: Boolean? = null, val onToggle: ((Boolean) -> Unit)? = null): FormatStd()
        data class Italic(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val checked: Boolean? = null, val onToggle: ((Boolean) -> Unit)? = null): FormatStd()
        data class Underline(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val checked: Boolean? = null, val onToggle: ((Boolean) -> Unit)? = null): FormatStd()
        data class Bigger(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FormatStd()
        data class Smaller(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FormatStd()

        data class KerningStandard(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FormatStd()
        data class KerningNone(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FormatStd()
        data class KerningTighten(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FormatStd()
        data class KerningLoosen(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FormatStd()

        data class LigaturesNone(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FormatStd()
        data class LigaturesStandard(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FormatStd()
        data class LigaturesAll(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FormatStd()

        data class RaiseBaseline(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FormatStd()
        data class LowerBaseline(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FormatStd()
        data class Superscript(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FormatStd()
        data class Subscript(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null): FormatStd()

        data class AlignLeft(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val checked: Boolean? = null, val onToggle: ((Boolean) -> Unit)? = null): FormatStd()
        data class AlignCenter(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val checked: Boolean? = null, val onToggle: ((Boolean) -> Unit)? = null): FormatStd()
        data class AlignRight(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val checked: Boolean? = null, val onToggle: ((Boolean) -> Unit)? = null): FormatStd()
        data class AlignJustified(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val checked: Boolean? = null, val onToggle: ((Boolean) -> Unit)? = null): FormatStd()
    }

    sealed class ViewStd : MenuElement {
        data class ShowToolbar(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val checked: Boolean? = null, val onToggle: ((Boolean) -> Unit)? = null) : ViewStd()
        data class CustomizeToolbar(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : ViewStd()
        data class ToggleFullScreen(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : ViewStd()
        data class ToggleSidebar(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val checked: Boolean? = null, val onToggle: ((Boolean) -> Unit)? = null): ViewStd()
        data class ToggleTabBar(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val checked: Boolean? = null, val onToggle: ((Boolean) -> Unit)? = null): ViewStd()
    }

    sealed class WindowStd : MenuElement {
        data class Close(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : WindowStd()
        data class Minimize(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : WindowStd()
        data class MinimizeAll(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : WindowStd()
        data class Zoom(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : WindowStd()
        data class BringAllToFront(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : WindowStd()

        data class ShowNextTab(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : WindowStd()
        data class ShowPreviousTab(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : WindowStd()
        data class MergeAllWindows(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : WindowStd()
        data class MoveTabToNewWindow(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : WindowStd()
    }

    sealed class HelpItem : MenuElement {
        data class AppHelp(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : HelpItem()
    }

    data class CustomItem(
        val title: String,
        val keyEquivalent: String = "",
        val modifierMask: Long = Modifiers.none,
        val enabled: Boolean = true,
        val icon: MenuIcon? = null,
        val onClick: () -> Unit
    ) : MenuElement
    data class CheckboxItem(
        val title: String,
        val checked: Boolean = false,
        val keyEquivalent: String = "",
        val modifierMask: Long = Modifiers.none,
        val enabled: Boolean = true,
        val icon: MenuIcon? = null,
        val onToggle: (Boolean) -> Unit
    ) : MenuElement
    data class Submenu(val title: String, val children: List<MenuElement>, val enabled: Boolean = true, val icon: MenuIcon? = null) : MenuElement

    sealed class TopMenu {
        data class Application(val elements: List<MenuElement>) : TopMenu()
        data class File(val title: String, val elements: List<MenuElement>) : TopMenu()
        data class Edit(val title: String, val elements: List<MenuElement>) : TopMenu()
        data class Format(val title: String, val elements: List<MenuElement>) : TopMenu()
        data class View(val title: String, val elements: List<MenuElement>) : TopMenu()
        data class Window(val title: String, val elements: List<MenuElement>, val suppressAutoWindowList: Boolean) : TopMenu()
        data class Help(val title: String, val elements: List<MenuElement>) : TopMenu()
        data class Custom(val title: String, val elements: List<MenuElement>) : TopMenu()
    }

    sealed interface MenuIcon {
        data class SFSymbol(val name: String, val template: Boolean = true) : MenuIcon
        data class Png(val bytes: ByteArray, val template: Boolean = true) : MenuIcon {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Png

                if (template != other.template) return false
                if (!bytes.contentEquals(other.bytes)) return false

                return true
            }

            override fun hashCode(): Int {
                var result = template.hashCode()
                result = 31 * result + bytes.contentHashCode()
                return result
            }
        }

        data class File(val path: String, val template: Boolean = true) : MenuIcon
    }

    object Modifiers {
        const val none: Long = 0L
        const val shift: Long = 1L shl 17
        const val control: Long = 1L shl 18
        const val option: Long = 1L shl 19
        const val command: Long = 1L shl 20
        fun combo(vararg flags: Long): Long = flags.fold(0L) { acc, f -> acc or f }
    }


    @Volatile private var currentMenuBar: Pointer? = null
    @Volatile private var lastStructureSignature: List<String>? = null

    private fun elementsOfTopMenu(t: TopMenu): List<MenuElement> = when (t) {
        is TopMenu.Application -> t.elements
        is TopMenu.File -> t.elements
        is TopMenu.Edit -> t.elements
        is TopMenu.Format -> t.elements
        is TopMenu.View -> t.elements
        is TopMenu.Window -> t.elements
        is TopMenu.Help -> t.elements
        is TopMenu.Custom -> t.elements
    }

    private fun enabledOf(el: MenuElement): Boolean? = when (el) {
        is SystemItem.About -> el.enabled
        is SystemItem.Settings -> el.enabled
        is SystemItem.Hide -> el.enabled
        is SystemItem.HideOthers -> el.enabled
        is SystemItem.ShowAll -> el.enabled
        is SystemItem.Quit -> el.enabled

        is FileStd.New -> el.enabled
        is FileStd.Open -> el.enabled
        is FileStd.OpenRecent -> el.enabled
        is FileStd.Close -> el.enabled
        is FileStd.CloseAll -> el.enabled
        is FileStd.Save -> el.enabled
        is FileStd.SaveAs -> el.enabled
        is FileStd.Duplicate -> el.enabled
        is FileStd.Rename -> el.enabled
        is FileStd.MoveTo -> el.enabled
        is FileStd.Revert -> el.enabled
        is FileStd.PageSetup -> el.enabled
        is FileStd.Print -> el.enabled
        is FileStd.ClearRecent -> el.enabled

        is EditStd.Undo -> el.enabled
        is EditStd.Redo -> el.enabled
        is EditStd.Cut -> el.enabled
        is EditStd.Copy -> el.enabled
        is EditStd.Paste -> el.enabled
        is EditStd.PasteAndMatchStyle -> el.enabled
        is EditStd.Delete -> el.enabled
        is EditStd.SelectAll -> el.enabled
        is EditStd.Find -> el.enabled
        is EditStd.FindNext -> el.enabled
        is EditStd.FindPrevious -> el.enabled
        is EditStd.UseSelectionForFind -> el.enabled
        is EditStd.JumpToSelection -> el.enabled
        is EditStd.Replace -> el.enabled
        is EditStd.ReplaceAndFind -> el.enabled
        is EditStd.ReplaceAll -> el.enabled
        is EditStd.ToggleSmartQuotes -> el.enabled
        is EditStd.ToggleSmartDashes -> el.enabled
        is EditStd.ToggleLinkDetection -> el.enabled
        is EditStd.ToggleTextReplacement -> el.enabled
        is EditStd.ToggleSpellingCorrection -> el.enabled
        is EditStd.Uppercase -> el.enabled
        is EditStd.Lowercase -> el.enabled
        is EditStd.Capitalize -> el.enabled
        is EditStd.StartSpeaking -> el.enabled
        is EditStd.StopSpeaking -> el.enabled

        is FormatStd.ShowFonts -> el.enabled
        is FormatStd.ShowColors -> el.enabled
        is FormatStd.Bold -> el.enabled
        is FormatStd.Italic -> el.enabled
        is FormatStd.Underline -> el.enabled
        is FormatStd.Bigger -> el.enabled
        is FormatStd.Smaller -> el.enabled
        is FormatStd.KerningStandard -> el.enabled
        is FormatStd.KerningNone -> el.enabled
        is FormatStd.KerningTighten -> el.enabled
        is FormatStd.KerningLoosen -> el.enabled
        is FormatStd.LigaturesNone -> el.enabled
        is FormatStd.LigaturesStandard -> el.enabled
        is FormatStd.LigaturesAll -> el.enabled
        is FormatStd.RaiseBaseline -> el.enabled
        is FormatStd.LowerBaseline -> el.enabled
        is FormatStd.Superscript -> el.enabled
        is FormatStd.Subscript -> el.enabled
        is FormatStd.AlignLeft -> el.enabled
        is FormatStd.AlignCenter -> el.enabled
        is FormatStd.AlignRight -> el.enabled
        is FormatStd.AlignJustified -> el.enabled

        is ViewStd.ShowToolbar -> el.enabled
        is ViewStd.CustomizeToolbar -> el.enabled
        is ViewStd.ToggleFullScreen -> el.enabled
        is ViewStd.ToggleSidebar -> el.enabled
        is ViewStd.ToggleTabBar -> el.enabled

        is WindowStd.Close -> el.enabled
        is WindowStd.Minimize -> el.enabled
        is WindowStd.MinimizeAll -> el.enabled
        is WindowStd.Zoom -> el.enabled
        is WindowStd.BringAllToFront -> el.enabled
        is WindowStd.ShowNextTab -> el.enabled
        is WindowStd.ShowPreviousTab -> el.enabled
        is WindowStd.MergeAllWindows -> el.enabled
        is WindowStd.MoveTabToNewWindow -> el.enabled

        is HelpItem.AppHelp -> el.enabled

        is CustomItem -> el.enabled
        is CheckboxItem -> el.enabled
        is Submenu -> el.enabled
        else -> null
    }

    private fun signatureOf(el: MenuElement): String {
        val k = el::class.java.simpleName
        val title = when (el) {
            is SystemItem.About -> el.title
            is SystemItem.Settings -> el.title
            is SystemItem.Services -> el.title
            is SystemItem.Hide -> el.title
            is SystemItem.HideOthers -> el.title
            is SystemItem.ShowAll -> el.title
            is SystemItem.Quit -> el.title

            is FileStd.New -> el.title
            is FileStd.Open -> el.title
            is FileStd.OpenRecent -> el.title
            is FileStd.Close -> el.title
            is FileStd.CloseAll -> el.title
            is FileStd.Save -> el.title
            is FileStd.SaveAs -> el.title
            is FileStd.Duplicate -> el.title
            is FileStd.Rename -> el.title
            is FileStd.MoveTo -> el.title
            is FileStd.Revert -> el.title
            is FileStd.PageSetup -> el.title
            is FileStd.Print -> el.title
            is FileStd.ClearRecent -> el.title

            is EditStd.Undo -> el.title
            is EditStd.Redo -> el.title
            is EditStd.Cut -> el.title
            is EditStd.Copy -> el.title
            is EditStd.Paste -> el.title
            is EditStd.PasteAndMatchStyle -> el.title
            is EditStd.Delete -> el.title
            is EditStd.SelectAll -> el.title
            is EditStd.Find -> el.title
            is EditStd.FindNext -> el.title
            is EditStd.FindPrevious -> el.title
            is EditStd.UseSelectionForFind -> el.title
            is EditStd.JumpToSelection -> el.title
            is EditStd.Replace -> el.title
            is EditStd.ReplaceAndFind -> el.title
            is EditStd.ReplaceAll -> el.title
            is EditStd.ToggleSmartQuotes -> el.title
            is EditStd.ToggleSmartDashes -> el.title
            is EditStd.ToggleLinkDetection -> el.title
            is EditStd.ToggleTextReplacement -> el.title
            is EditStd.ToggleSpellingCorrection -> el.title
            is EditStd.Uppercase -> el.title
            is EditStd.Lowercase -> el.title
            is EditStd.Capitalize -> el.title
            is EditStd.StartSpeaking -> el.title
            is EditStd.StopSpeaking -> el.title

            is FormatStd.ShowFonts -> el.title
            is FormatStd.ShowColors -> el.title
            is FormatStd.Bold -> el.title
            is FormatStd.Italic -> el.title
            is FormatStd.Underline -> el.title
            is FormatStd.Bigger -> el.title
            is FormatStd.Smaller -> el.title
            is FormatStd.KerningStandard -> el.title
            is FormatStd.KerningNone -> el.title
            is FormatStd.KerningTighten -> el.title
            is FormatStd.KerningLoosen -> el.title
            is FormatStd.LigaturesNone -> el.title
            is FormatStd.LigaturesStandard -> el.title
            is FormatStd.LigaturesAll -> el.title
            is FormatStd.RaiseBaseline -> el.title
            is FormatStd.LowerBaseline -> el.title
            is FormatStd.Superscript -> el.title
            is FormatStd.Subscript -> el.title
            is FormatStd.AlignLeft -> el.title
            is FormatStd.AlignCenter -> el.title
            is FormatStd.AlignRight -> el.title
            is FormatStd.AlignJustified -> el.title

            is ViewStd.ShowToolbar -> el.title
            is ViewStd.CustomizeToolbar -> el.title
            is ViewStd.ToggleFullScreen -> el.title
            is ViewStd.ToggleSidebar -> el.title
            is ViewStd.ToggleTabBar -> el.title

            is WindowStd.Close -> el.title
            is WindowStd.Minimize -> el.title
            is WindowStd.MinimizeAll -> el.title
            is WindowStd.Zoom -> el.title
            is WindowStd.BringAllToFront -> el.title
            is WindowStd.ShowNextTab -> el.title
            is WindowStd.ShowPreviousTab -> el.title
            is WindowStd.MergeAllWindows -> el.title
            is WindowStd.MoveTabToNewWindow -> el.title

            is HelpItem.AppHelp -> el.title

            is CustomItem -> "${el.title}|${el.keyEquivalent}|${el.modifierMask}"
            is CheckboxItem -> "${el.title}|${el.keyEquivalent}|${el.modifierMask}"
            is Submenu -> el.title
            Separator -> "-"
        }
        val children = if (el is Submenu) el.children.size else -1
        return "$k|$title|$children"
    }

    private fun topSignatureOf(t: TopMenu): String = when (t) {
        is TopMenu.Application -> "T|Application"
        is TopMenu.File        -> "T|File|${t.title}"
        is TopMenu.Edit        -> "T|Edit|${t.title}"
        is TopMenu.Format      -> "T|Format|${t.title}"
        is TopMenu.View        -> "T|View|${t.title}"
        is TopMenu.Window      -> "T|Window|${t.title}|${t.suppressAutoWindowList}"
        is TopMenu.Help        -> "T|Help|${t.title}"
        is TopMenu.Custom      -> "T|Custom|${t.title}"
    }

    private fun collectStructureSignature(menus: List<TopMenu>): List<String> {
        val sig = mutableListOf<String>()
        fun walk(list: List<MenuElement>) {
            list.forEach { el ->
                sig += "E|" + signatureOf(el)
                if (el is Submenu) walk(el.children)
            }
        }
        menus.forEach { t ->
            sig += topSignatureOf(t)
            walk(elementsOfTopMenu(t))
        }
        return sig
    }

    private fun itemAt(menu: Pointer?, index: Int): Pointer =
        msgSendPL(menu, "itemAtIndex:", index.toLong())

    private fun submenuOf(item: Pointer?): Pointer =
        msgSendP(item, "submenu")

    private fun topModelIndexToMenubarIndex(menus: List<TopMenu>, modelIdx: Int): Int {
        val hasApp = menus.any { it is TopMenu.Application }
        return if (hasApp) {
            if (menus[modelIdx] is TopMenu.Application) 0
            else {
                val nonAppUpTo = menus.subList(0, modelIdx + 1).count { it !is TopMenu.Application }
                nonAppUpTo
            }
        } else 1 + modelIdx
    }

    private fun applyEnabledLinear(menus: List<TopMenu>) {
        val bar = currentMenuBar ?: return
        fun walk(menuPtr: Pointer, els: List<MenuElement>) {
            var i = 0
            for (el in els) {
                val item = itemAt(menuPtr, i++)
                enabledOf(el)?.let { setEnabled(item, it) }
                if (el is Submenu) {
                    val sub = submenuOf(item)
                    walk(sub, el.children)
                }
            }
        }
        menus.forEachIndexed { idx, t ->
            val menubarIdx = topModelIndexToMenubarIndex(menus, idx)
            val topItem = itemAt(bar, menubarIdx)
            val topSub = submenuOf(topItem)
            walk(topSub, elementsOfTopMenu(t))
        }
    }


    private fun isNull(p: Pointer?): Boolean = p == null || Pointer.nativeValue(p) == 0L
    private fun nn(p: Pointer?): Pointer = p ?: Pointer.NULL
    private fun selPtr(cmd: String) = sel(cmd)

    private fun msgSendP(recv: Pointer?, cmd: String): Pointer =
        try { objc.objc_msgSend(nn(recv), selPtr(cmd)) } catch (_: Throwable) { Pointer.NULL }

    private fun msgSendPP(recv: Pointer?, cmd: String, a: Pointer?): Pointer =
        try { objc.objc_msgSend(nn(recv), selPtr(cmd), nn(a)) } catch (_: Throwable) { Pointer.NULL }

    private fun msgSendPL(recv: Pointer?, cmd: String, l: Long): Pointer =
        try { objc.objc_msgSend(nn(recv), selPtr(cmd), l) } catch (_: Throwable) { Pointer.NULL }

    @Suppress("SameParameterValue")
    private fun msgSendPPP(recv: Pointer?, cmd: String, a: Pointer?, b: Pointer?): Pointer =
        try { objc.objc_msgSend(nn(recv), selPtr(cmd), nn(a), nn(b)) } catch (_: Throwable) { Pointer.NULL }

    @Suppress("SameParameterValue")
    private fun msgSendPPL(recv: Pointer?, cmd: String, a: Pointer?, l: Long): Pointer =
        try { objc.objc_msgSend(nn(recv), selPtr(cmd), nn(a), l) } catch (_: Throwable) { Pointer.NULL }

    private fun runOnMain(block: () -> Unit) {
        val d = dispatch
        if (d != null) {
            d.dispatch_sync_f(d.dispatch_get_main_queue(), Pointer.NULL, object : DispatchFunction {
                override fun invoke(context: Pointer) = block()
            })
        } else block()
    }

    private fun getNsApp(): Pointer {
        val NSApplication = objc.objc_getClass("NSApplication")
        return msgSendP(NSApplication, "sharedApplication")
    }

    private fun nsString(str: String): Pointer {
        val NSString = objc.objc_getClass("NSString")
        val bytes = str.toByteArray(StandardCharsets.UTF_8)
        val mem = Memory((bytes.size + 1).toLong()).also {
            it.write(0, bytes, 0, bytes.size)
            it.setByte(bytes.size.toLong(), 0)
        }
        return msgSendPP(NSString, "stringWithUTF8String:", mem)
    }

    private fun nsDataFrom(bytes: ByteArray): Pointer {
        val NSData = objc.objc_getClass("NSData")
        val mem = Memory(bytes.size.toLong()).also { it.write(0, bytes, 0, bytes.size) }
        val alloc = msgSendP(NSData, "alloc")
        return msgSendPPL(alloc, "initWithBytes:length:", mem, bytes.size.toLong())
    }

    private fun nsImageFrom(icon: MenuIcon): Pointer {
        val NSImage = objc.objc_getClass("NSImage")
        val img = when (icon) {
            is MenuIcon.SFSymbol -> msgSendPPP(NSImage, "imageWithSystemSymbolName:accessibilityDescription:", nsString(icon.name), nsString(""))
            is MenuIcon.Png -> msgSendPP(msgSendP(NSImage, "alloc"), "initWithData:", nsDataFrom(icon.bytes))
            is MenuIcon.File -> msgSendPP(msgSendP(NSImage, "alloc"), "initWithContentsOfFile:", nsString(icon.path))
        }
        val template = when (icon) { is MenuIcon.SFSymbol -> icon.template; is MenuIcon.Png -> icon.template; is MenuIcon.File -> icon.template }
        if (!isNull(img)) msgSendPL(img, "setTemplate:", if (template) 1 else 0)
        return img
    }

    private fun createMenu(title: String): Pointer {
        val NSMenu = objc.objc_getClass("NSMenu")
        val menu = msgSendPP(msgSendP(NSMenu, "alloc"), "initWithTitle:", nsString(title))
        msgSendPL(menu, "setAutoenablesItems:", 0)
        return menu
    }

    private fun createMenuItem(title: String, actionSelName: String?, key: String): Pointer {
        val NSMenuItem = objc.objc_getClass("NSMenuItem")
        val item = msgSendP(msgSendP(NSMenuItem, "alloc"), "init")
        if (isNull(item)) return Pointer.NULL
        msgSendPP(item, "setTitle:", nsString(title))
        if (actionSelName != null) msgSendPP(item, "setAction:", sel(actionSelName))
        msgSendPP(item, "setKeyEquivalent:", nsString(key))
        return item
    }

    private fun addItemToMenu(menu: Pointer?, item: Pointer?) {
        if (isNull(menu) || isNull(item)) return
        msgSendPP(menu, "addItem:", item)
    }

    private fun addSeparator(menu: Pointer) {
        val NSMenuItem = objc.objc_getClass("NSMenuItem")
        addItemToMenu(menu, msgSendP(NSMenuItem, "separatorItem"))
    }

    private fun setModifiers(item: Pointer?, mask: Long) { if (!isNull(item) && mask != 0L) msgSendPL(item, "setKeyEquivalentModifierMask:", mask) }
    private fun setEnabled(item: Pointer?, enabled: Boolean) { if (!isNull(item)) msgSendPL(item, "setEnabled:", if (enabled) 1 else 0) }
    private fun setState(item: Pointer?, checked: Boolean) { if (!isNull(item)) msgSendPL(item, "setState:", if (checked) 1 else 0) }
    private fun setImage(item: Pointer?, icon: MenuIcon?) { if (!isNull(item) && icon != null) msgSendPP(item, "setImage:", nsImageFrom(icon)) }
    private fun setTarget(item: Pointer?, target: Pointer?) { if (!isNull(item) && !isNull(target)) msgSendPP(item, "setTarget:", target) }
    private fun setTag(item: Pointer?, tag: Int) { if (!isNull(item)) msgSendPL(item, "setTag:", tag.toLong()) }

    private const val actionSelectorName = "invokeMenuItem:"
    private val actionSelector: Pointer get() = sel(actionSelectorName)
    private val menuActions = ConcurrentHashMap<Long, () -> Unit>()
    private val checkboxStates = ConcurrentHashMap<Long, Boolean>()
    @Volatile private var actionTargetInstance: Pointer? = null

    interface MenuItemInvokeCallback : Callback { fun invoke(self: Pointer?, _cmd: Pointer?, sender: Pointer?) }
    private val invokeCallback = object : MenuItemInvokeCallback {
        override fun invoke(self: Pointer?, _cmd: Pointer?, sender: Pointer?) {
            val key = Pointer.nativeValue(nn(sender))
            menuActions[key]?.invoke()
        }
    }

    private fun ensureActionTargetInstance(): Pointer {
        actionTargetInstance?.let { return it }
        synchronized(this) {
            actionTargetInstance?.let { return it }
            val NSObject = objc.objc_getClass("NSObject")
            val clsName = "JNAActionTarget"
            var cls = objc.objc_allocateClassPair(NSObject, clsName, 0)
            cls = if (isNull(cls)) objc.objc_getClass(clsName) else cls.also {
                objc.class_addMethod(it, actionSelector, invokeCallback, "v@:@")
                objc.objc_registerClassPair(it)
            }
            val inst = msgSendP(msgSendP(cls, "alloc"), "init")
            actionTargetInstance = inst
            return inst
        }
    }

    private fun populateSubmenu(menuPtr: Pointer, children: List<MenuElement>, nsapp: Pointer, customTarget: Pointer) {
        for (child in children) {
            when (child) {
                is CustomItem -> {
                    val it = createMenuItem(child.title, actionSelectorName, child.keyEquivalent)
                    setModifiers(it, child.modifierMask); setEnabled(it, child.enabled); setImage(it, child.icon)
                    setTarget(it, customTarget); addItemToMenu(menuPtr, it)
                    menuActions[Pointer.nativeValue(it)] = child.onClick
                }
                is CheckboxItem -> {
                    val it = createMenuItem(child.title, actionSelectorName, child.keyEquivalent)
                    setModifiers(it, child.modifierMask); setEnabled(it, child.enabled); setState(it, child.checked); setImage(it, child.icon)
                    setTarget(it, customTarget); addItemToMenu(menuPtr, it)
                    val key = Pointer.nativeValue(it)
                    checkboxStates[key] = child.checked
                    menuActions[key] = {
                        val now = !(checkboxStates[key] ?: false); checkboxStates[key] = now; setState(it, now); child.onToggle(now)
                    }
                }
                is Submenu -> {
                    val parent = createMenuItem(child.title, null, ""); val sub = createMenu(child.title)
                    setEnabled(parent, child.enabled); setImage(parent, child.icon)
                    msgSendPP(parent, "setSubmenu:", sub); addItemToMenu(menuPtr, parent)
                    populateSubmenu(sub, child.children, nsapp, customTarget)
                }
                is Separator -> addSeparator(menuPtr)
                is SystemItem-> addSystemStd(menuPtr, child, nsapp, customTarget)
                is FileStd   -> addFileStd(menuPtr, child, nsapp, customTarget)
                is EditStd   -> addEditStd(menuPtr, child, customTarget)
                is FormatStd -> addFormatStd(menuPtr, child, customTarget)
                is ViewStd   -> addViewStd(menuPtr, child, customTarget)
                is WindowStd -> addWindowStd(menuPtr, child, nsapp, customTarget)
                is HelpItem  -> addHelpStd(menuPtr, child, nsapp, customTarget)
            }
        }
    }

    private fun buildGenericMenu(title: String, elements: List<MenuElement>): Pointer {
        val menu = createMenu(title)
        val nsapp = getNsApp()
        val target = ensureActionTargetInstance()
        for (el in elements) {
            when (el) {
                is CustomItem -> {
                    val it = createMenuItem(el.title, actionSelectorName, el.keyEquivalent)
                    setModifiers(it, el.modifierMask); setEnabled(it, el.enabled); setImage(it, el.icon)
                    setTarget(it, target); addItemToMenu(menu, it); menuActions[Pointer.nativeValue(it)] = el.onClick
                }
                is CheckboxItem -> {
                    val it = createMenuItem(el.title, actionSelectorName, el.keyEquivalent)
                    setModifiers(it, el.modifierMask); setEnabled(it, el.enabled); setState(it, el.checked); setImage(it, el.icon)
                    setTarget(it, target); addItemToMenu(menu, it)
                    val key = Pointer.nativeValue(it); checkboxStates[key] = el.checked
                    menuActions[key] = { val now = !(checkboxStates[key] ?: false); checkboxStates[key] = now; setState(it, now); el.onToggle(now) }
                }
                is Submenu -> {
                    val it = createMenuItem(el.title, null, ""); val sub = createMenu(el.title)
                    setEnabled(it, el.enabled); setImage(it, el.icon); msgSendPP(it, "setSubmenu:", sub); addItemToMenu(menu, it)
                    populateSubmenu(sub, el.children, nsapp = nsapp, customTarget = target)
                }
                Separator -> addSeparator(menu)
                is EditStd, is ViewStd, is WindowStd, is HelpItem, is SystemItem, is FileStd, is FormatStd -> {}
            }
        }
        return menu
    }

    private fun addSystemItemTo(menu: Pointer, title: String, defaultSelector: String, key: String, mods: Long, enabled: Boolean, icon: MenuIcon?, nsapp: Pointer, override: (() -> Unit)?) {
        val it = createMenuItem(title, if (override != null) actionSelectorName else defaultSelector, key)
        if (mods != 0L) setModifiers(it, mods)
        setEnabled(it, enabled); setImage(it, icon)
        setTarget(it, if (override != null) ensureActionTargetInstance() else nsapp)
        addItemToMenu(menu, it)
        if (override != null) menuActions[Pointer.nativeValue(it)] = override
    }

    private fun addSystemStd(submenu: Pointer, el: SystemItem, nsapp: Pointer, customTarget: Pointer) {
        when (el) {
            is SystemItem.About -> {
                val has = el.onClick != null
                val it = createMenuItem(el.title, if (has) actionSelectorName else "orderFrontStandardAboutPanel:", "")
                setEnabled(it, el.enabled); setImage(it, el.icon); setTarget(it, if (has) customTarget else nsapp)
                addItemToMenu(submenu, it); if (has) menuActions[Pointer.nativeValue(it)] = el.onClick
            }
            is SystemItem.Settings -> {
                val has = el.onClick != null
                val it = createMenuItem(el.title, if (has) actionSelectorName else "showPreferences:", ",")
                setModifiers(it, Modifiers.command); setEnabled(it, el.enabled); setImage(it, el.icon)
                setTarget(it, if (has) customTarget else nsapp); addItemToMenu(submenu, it)
                if (has) menuActions[Pointer.nativeValue(it)] = el.onClick
            }
            is SystemItem.Services -> {
                val services = createMenu(el.title)
                msgSendPP(nsapp, "setServicesMenu:", services)
                val it = createMenuItem(el.title, null, "")
                msgSendPP(it, "setSubmenu:", services)
                addItemToMenu(submenu, it)
            }
            is SystemItem.Hide -> addSystemItemTo(submenu, el.title, "hide:", "h", Modifiers.command, el.enabled, el.icon, nsapp, el.onClick)
            is SystemItem.HideOthers -> addSystemItemTo(submenu, el.title, "hideOtherApplications:", "h", Modifiers.combo(Modifiers.command, Modifiers.option), el.enabled, el.icon, nsapp, el.onClick)
            is SystemItem.ShowAll -> addSystemItemTo(submenu, el.title, "unhideAllApplications:", "", Modifiers.none, el.enabled, el.icon, nsapp, el.onClick)
            is SystemItem.Quit -> {
                val has = el.onClick != null
                val it = createMenuItem(el.title, if (has) actionSelectorName else "terminate:", "q")
                setModifiers(it, Modifiers.command); setEnabled(it, el.enabled); setImage(it, el.icon)
                setTarget(it, if (has) customTarget else nsapp); addItemToMenu(submenu, it)
                if (has) menuActions[Pointer.nativeValue(it)] = el.onClick
            }
        }
    }

    private fun addFileStd(menu: Pointer, el: FileStd, nsapp: Pointer, target: Pointer) {
        when (el) {
            is FileStd.New -> addStd(menu, el.title, "newDocument:", "n", Modifiers.command, el.enabled, el.icon, el.onClick, target)
            is FileStd.Open -> addStd(menu, el.title, "openDocument:", "o", Modifiers.command, el.enabled, el.icon, el.onClick, target)
            is FileStd.OpenRecent -> {
                val parent = createMenuItem(el.title, null, "")
                val sub = createMenu(el.title)
                setEnabled(parent, el.enabled); setImage(parent, el.icon)
                if (el.children.isNotEmpty()) {
                    populateSubmenu(sub, el.children, nsapp, target)
                    addSeparator(sub)
                }
                msgSendPP(parent, "setSubmenu:", sub)
                addItemToMenu(menu, parent)
            }
            is FileStd.Close -> addStd(menu, el.title, "performClose:", "w", Modifiers.command, el.enabled, el.icon, el.onClick, target)
            is FileStd.CloseAll -> {
                val it = createMenuItem(el.title, el.onClick?.let { actionSelectorName } ?: "closeAllDocuments:", "w")
                setModifiers(it, Modifiers.combo(Modifiers.command, Modifiers.option)); setEnabled(it, el.enabled); setImage(it, el.icon)
                if (el.onClick != null) { setTarget(it, target); menuActions[Pointer.nativeValue(it)] = el.onClick }
                addItemToMenu(menu, it)
            }
            is FileStd.Save -> addStd(menu, el.title, "saveDocument:", "s", Modifiers.command, el.enabled, el.icon, el.onClick, target)
            is FileStd.SaveAs -> addStd(menu, el.title, "saveDocumentAs:", "S", Modifiers.combo(Modifiers.command, Modifiers.shift), el.enabled, el.icon, el.onClick, target)
            is FileStd.Duplicate -> addStd(menu, el.title, "duplicateDocument:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
            is FileStd.Rename -> addStd(menu, el.title, "renameDocument:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
            is FileStd.MoveTo -> addStd(menu, el.title, "moveDocument:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
            is FileStd.Revert -> addStd(menu, el.title, "revertDocument:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
            is FileStd.PageSetup -> addStd(menu, el.title, "runPageLayout:", "P", Modifiers.combo(Modifiers.command, Modifiers.shift), el.enabled, el.icon, el.onClick, target)
            is FileStd.Print -> addStd(menu, el.title, "printDocument:", "p", Modifiers.command, el.enabled, el.icon, el.onClick, target)
            is FileStd.ClearRecent -> addStd(menu, el.title, "clearRecentDocuments:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
        }
    }

    private fun addEditStd(menu: Pointer, el: EditStd, target: Pointer) {
        when (el) {
            is EditStd.Undo -> addStd(menu, el.title, "undo:", "z", Modifiers.command, el.enabled, el.icon, el.onClick, target)
            is EditStd.Redo -> addStd(menu, el.title, "redo:", "Z", Modifiers.combo(Modifiers.command, Modifiers.shift), el.enabled, el.icon, el.onClick, target)
            is EditStd.Cut -> addStd(menu, el.title, "cut:", "x", Modifiers.command, el.enabled, el.icon, el.onClick, target)
            is EditStd.Copy -> addStd(menu, el.title, "copy:", "c", Modifiers.command, el.enabled, el.icon, el.onClick, target)
            is EditStd.Paste -> addStd(menu, el.title, "paste:", "v", Modifiers.command, el.enabled, el.icon, el.onClick, target)
            is EditStd.PasteAndMatchStyle -> addStd(menu, el.title, "pasteAndMatchStyle:", "V", Modifiers.combo(Modifiers.command, Modifiers.option, Modifiers.shift), el.enabled, el.icon, el.onClick, target)
            is EditStd.Delete -> addStd(menu, el.title, "delete:", "\u0008", Modifiers.none, el.enabled, el.icon, el.onClick, target)
            is EditStd.SelectAll -> addStd(menu, el.title, "selectAll:", "a", Modifiers.command, el.enabled, el.icon, el.onClick, target)

            is EditStd.Find -> addFind(menu, el.title, FIND_TAG_SHOW, "f", Modifiers.command, el.enabled, el.icon, el.onClick, target)
            is EditStd.FindNext -> addFind(menu, el.title, FIND_TAG_NEXT, "g", Modifiers.command, el.enabled, el.icon, el.onClick, target)
            is EditStd.FindPrevious -> addFind(menu, el.title, FIND_TAG_PREV, "G", Modifiers.combo(Modifiers.command, Modifiers.shift), el.enabled, el.icon, el.onClick, target)
            is EditStd.UseSelectionForFind -> addFind(menu, el.title, FIND_TAG_SET_FIND, "e", Modifiers.command, el.enabled, el.icon, el.onClick, target)
            is EditStd.JumpToSelection -> addStd(menu, el.title, "centerSelectionInVisibleArea:", "j", Modifiers.command, el.enabled, el.icon, el.onClick, target)
            is EditStd.Replace -> addFind(menu, el.title, FIND_TAG_REPLACE, "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
            is EditStd.ReplaceAndFind -> addFind(menu, el.title, FIND_TAG_REPLACE_AND_FIND, "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
            is EditStd.ReplaceAll -> addFind(menu, el.title, FIND_TAG_REPLACE_ALL, "", Modifiers.none, el.enabled, el.icon, el.onClick, target)

            is EditStd.ToggleSmartQuotes -> addStdToggle(menu, el.title, "toggleAutomaticQuoteSubstitution:", "", Modifiers.none, el.enabled, el.icon, el.checked, el.onToggle, target)
            is EditStd.ToggleSmartDashes -> addStdToggle(menu, el.title, "toggleAutomaticDashSubstitution:", "", Modifiers.none, el.enabled, el.icon, el.checked, el.onToggle, target)
            is EditStd.ToggleLinkDetection -> addStdToggle(menu, el.title, "toggleAutomaticLinkDetection:", "", Modifiers.none, el.enabled, el.icon, el.checked, el.onToggle, target)
            is EditStd.ToggleTextReplacement -> addStdToggle(menu, el.title, "toggleAutomaticTextReplacement:", "", Modifiers.none, el.enabled, el.icon, el.checked, el.onToggle, target)
            is EditStd.ToggleSpellingCorrection -> addStdToggle(menu, el.title, "toggleAutomaticSpellingCorrection:", "", Modifiers.none, el.enabled, el.icon, el.checked, el.onToggle, target)

            is EditStd.Uppercase -> addStd(menu, el.title, "uppercaseWord:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
            is EditStd.Lowercase -> addStd(menu, el.title, "lowercaseWord:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
            is EditStd.Capitalize -> addStd(menu, el.title, "capitalizeWord:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)

            is EditStd.StartSpeaking -> addStd(menu, el.title, "startSpeaking:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
            is EditStd.StopSpeaking -> addStd(menu, el.title, "stopSpeaking:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
        }
    }

    private fun addFormatStd(menu: Pointer, el: FormatStd, target: Pointer) {
        when (el) {
            is FormatStd.ShowFonts -> addStd(menu, el.title, "orderFrontFontPanel:", "t", Modifiers.command, el.enabled, el.icon, el.onClick, target)
            is FormatStd.ShowColors -> addStd(menu, el.title, "orderFrontColorPanel:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)

            is FormatStd.Bold -> addStdToggle(menu, el.title, "toggleBoldface:", "b", Modifiers.command, el.enabled, el.icon, el.checked, el.onToggle, target)
            is FormatStd.Italic -> addStdToggle(menu, el.title, "toggleItalics:", "i", Modifiers.command, el.enabled, el.icon, el.checked, el.onToggle, target)
            is FormatStd.Underline -> addStdToggle(menu, el.title, "toggleUnderline:", "u", Modifiers.command, el.enabled, el.icon, el.checked, el.onToggle, target)
            is FormatStd.Bigger -> addStd(menu, el.title, "makeTextBigger:", "=", Modifiers.command, el.enabled, el.icon, el.onClick, target)
            is FormatStd.Smaller -> addStd(menu, el.title, "makeTextSmaller:", "-", Modifiers.command, el.enabled, el.icon, el.onClick, target)

            is FormatStd.KerningStandard -> addStd(menu, el.title, "useStandardKerning:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
            is FormatStd.KerningNone -> addStd(menu, el.title, "turnOffKerning:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
            is FormatStd.KerningTighten -> addStd(menu, el.title, "tightenKerning:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
            is FormatStd.KerningLoosen -> addStd(menu, el.title, "loosenKerning:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)

            is FormatStd.LigaturesNone -> addStd(menu, el.title, "turnOffLigatures:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
            is FormatStd.LigaturesStandard -> addStd(menu, el.title, "useStandardLigatures:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
            is FormatStd.LigaturesAll -> addStd(menu, el.title, "useAllLigatures:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)

            is FormatStd.RaiseBaseline -> addStd(menu, el.title, "raiseBaseline:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
            is FormatStd.LowerBaseline -> addStd(menu, el.title, "lowerBaseline:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
            is FormatStd.Superscript -> addStd(menu, el.title, "superscript:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
            is FormatStd.Subscript -> addStd(menu, el.title, "subscript:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)

            is FormatStd.AlignLeft -> addStdToggle(menu, el.title, "alignLeft:", "", Modifiers.none, el.enabled, el.icon, el.checked, el.onToggle, target)
            is FormatStd.AlignCenter -> addStdToggle(menu, el.title, "alignCenter:", "", Modifiers.none, el.enabled, el.icon, el.checked, el.onToggle, target)
            is FormatStd.AlignRight -> addStdToggle(menu, el.title, "alignRight:", "", Modifiers.none, el.enabled, el.icon, el.checked, el.onToggle, target)
            is FormatStd.AlignJustified -> addStdToggle(menu, el.title, "alignJustified:", "", Modifiers.none, el.enabled, el.icon, el.checked, el.onToggle, target)
        }
    }

    private fun addViewStd(menu: Pointer, el: ViewStd, target: Pointer) {
        when (el) {
            is ViewStd.ShowToolbar -> addStdToggle(menu, el.title, "toggleToolbarShown:", "t", Modifiers.combo(Modifiers.command, Modifiers.option), el.enabled, el.icon, el.checked, el.onToggle, target)
            is ViewStd.CustomizeToolbar -> addStd(menu, el.title, "runToolbarCustomizationPalette:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
            is ViewStd.ToggleFullScreen -> addStd(menu, el.title, "toggleFullScreen:", "f", Modifiers.combo(Modifiers.command, Modifiers.control), el.enabled, el.icon, el.onClick, target)
            is ViewStd.ToggleSidebar -> addStdToggle(menu, el.title, "toggleSidebar:", "s", Modifiers.combo(Modifiers.command, Modifiers.option), el.enabled, el.icon, el.checked, el.onToggle, target)
            is ViewStd.ToggleTabBar -> addStdToggle(menu, el.title, "toggleTabBar:", "T", Modifiers.combo(Modifiers.command, Modifiers.shift), el.enabled, el.icon, el.checked, el.onToggle, target)
        }
    }

    private fun addWindowStd(menu: Pointer, el: WindowStd, nsapp: Pointer, target: Pointer) {
        when (el) {
            is WindowStd.Close -> addStd(menu, el.title, "performClose:", "w", Modifiers.command, el.enabled, el.icon, el.onClick, target)
            is WindowStd.Minimize -> addStd(menu, el.title, "performMiniaturize:", "m", Modifiers.command, el.enabled, el.icon, el.onClick, target)
            is WindowStd.MinimizeAll -> {
                val has = el.onClick != null
                val it = createMenuItem(el.title, if (has) actionSelectorName else "miniaturizeAll:", "m")
                setModifiers(it, Modifiers.combo(Modifiers.command, Modifiers.option))
                setEnabled(it, el.enabled); setImage(it, el.icon)
                setTarget(it, if (has) target else nsapp); addItemToMenu(menu, it)
                if (has) menuActions[Pointer.nativeValue(it)] = el.onClick
            }
            is WindowStd.Zoom -> addStd(menu, el.title, "performZoom:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
            is WindowStd.BringAllToFront -> {
                val has = el.onClick != null
                val it = createMenuItem(el.title, if (has) actionSelectorName else "arrangeInFront:", "")
                setEnabled(it, el.enabled); setImage(it, el.icon)
                setTarget(it, if (has) target else nsapp); addItemToMenu(menu, it)
                if (has) menuActions[Pointer.nativeValue(it)] = el.onClick
            }
            is WindowStd.ShowNextTab -> addStd(menu, el.title, "selectNextTab:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
            is WindowStd.ShowPreviousTab -> addStd(menu, el.title, "selectPreviousTab:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
            is WindowStd.MergeAllWindows -> addStd(menu, el.title, "mergeAllWindows:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
            is WindowStd.MoveTabToNewWindow -> addStd(menu, el.title, "moveTabToNewWindow:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
        }
    }

    private fun addHelpStd(menu: Pointer, el: HelpItem, nsapp: Pointer, target: Pointer) {
        when (el) {
            is HelpItem.AppHelp -> {
                val has = el.onClick != null
                val it = createMenuItem(el.title, if (has) actionSelectorName else "showHelp:", "?")
                setModifiers(it, Modifiers.combo(Modifiers.command, Modifiers.shift))
                setEnabled(it, el.enabled); setImage(it, el.icon)
                setTarget(it, if (has) target else nsapp)
                addItemToMenu(menu, it)
                if (has) menuActions[Pointer.nativeValue(it)] = el.onClick
            }
        }
    }

    private fun buildApplicationSubmenu(elements: List<MenuElement>): Pointer {
        val nsapp = getNsApp()
        val submenu = createMenu("")

        val customTarget = ensureActionTargetInstance()
        for (el in elements) {
            when (el) {
                is SystemItem -> addSystemStd(submenu, el, nsapp, customTarget)
                is CustomItem -> {
                    val it = createMenuItem(el.title, actionSelectorName, el.keyEquivalent)
                    setModifiers(it, el.modifierMask); setEnabled(it, el.enabled); setImage(it, el.icon)
                    setTarget(it, customTarget); addItemToMenu(submenu, it); menuActions[Pointer.nativeValue(it)] = el.onClick
                }
                is CheckboxItem -> {
                    val it = createMenuItem(el.title, actionSelectorName, el.keyEquivalent)
                    setModifiers(it, el.modifierMask); setEnabled(it, el.enabled); setState(it, el.checked); setImage(it, el.icon)
                    setTarget(it, customTarget); addItemToMenu(submenu, it)
                    val key = Pointer.nativeValue(it); checkboxStates[key] = el.checked
                    menuActions[key] = { val now = !(checkboxStates[key] ?: false); checkboxStates[key] = now; setState(it, now); el.onToggle(now) }
                }
                is Submenu -> {
                    val it = createMenuItem(el.title, null, ""); val sub = createMenu(el.title)
                    setEnabled(it, el.enabled); setImage(it, el.icon); msgSendPP(it, "setSubmenu:", sub); addItemToMenu(submenu, it)
                    populateSubmenu(sub, el.children, nsapp, customTarget)
                }
                Separator -> addSeparator(submenu)
                else -> Unit
            }
        }
        return submenu
    }

    private fun buildFileMenu(title: String, elements: List<MenuElement>): Pointer {
        val menu = createMenu(title)
        val nsapp = getNsApp()
        val target = ensureActionTargetInstance()
        for (el in elements) {
            when (el) {
                is FileStd -> addFileStd(menu, el, nsapp, target)
                is CustomItem -> addCustom(menu, el, target)
                is CheckboxItem -> addCheckbox(menu, el, target)
                is Submenu -> { val it = createMenuItem(el.title, null, ""); val sub = createMenu(el.title); msgSendPP(it, "setSubmenu:", sub); addItemToMenu(menu, it); populateSubmenu(sub, el.children, nsapp, target) }
                Separator -> addSeparator(menu)
                else -> Unit
            }
        }
        return menu
    }

    private fun buildEditMenu(title: String, elements: List<MenuElement>): Pointer {
        val menu = createMenu(title)
        val target = ensureActionTargetInstance()
        for (el in elements) {
            when (el) {
                else -> { when (el) {
                    is EditStd -> addEditStd(menu, el, target)
                    is CustomItem -> addCustom(menu, el, target)
                    is CheckboxItem -> addCheckbox(menu, el, target)
                    is Submenu -> { val it = createMenuItem(el.title, null, ""); val sub = createMenu(el.title); msgSendPP(it, "setSubmenu:", sub); addItemToMenu(menu, it); populateSubmenu(sub, el.children, getNsApp(), target) }
                    Separator -> addSeparator(menu)
                    else -> Unit
                } }
            }
        }
        return menu
    }

    private fun buildFormatMenu(title: String, elements: List<MenuElement>): Pointer {
        val menu = createMenu(title)
        val target = ensureActionTargetInstance()
        for (el in elements) {
            when (el) {
                is FormatStd -> addFormatStd(menu, el, target)
                is CustomItem -> addCustom(menu, el, target)
                is CheckboxItem -> addCheckbox(menu, el, target)
                is Submenu -> { val it = createMenuItem(el.title, null, ""); val sub = createMenu(el.title); msgSendPP(it, "setSubmenu:", sub); addItemToMenu(menu, it); populateSubmenu(sub, el.children, getNsApp(), target) }
                Separator -> addSeparator(menu)
                else -> Unit
            }
        }
        return menu
    }

    private fun buildViewMenu(title: String, elements: List<MenuElement>): Pointer {
        val menu = createMenu(title)
        val target = ensureActionTargetInstance()
        for (el in elements) {
            when (el) {
                is ViewStd -> addViewStd(menu, el, target)
                is CustomItem -> addCustom(menu, el, target)
                is CheckboxItem -> addCheckbox(menu, el, target)
                is Submenu -> { val it = createMenuItem(el.title, null, ""); val sub = createMenu(el.title); msgSendPP(it, "setSubmenu:", sub); addItemToMenu(menu, it); populateSubmenu(sub, el.children, getNsApp(), target) }
                Separator -> addSeparator(menu)
                else -> Unit
            }
        }
        return menu
    }

    private fun buildWindowMenu(title: String, elements: List<MenuElement>, suppressAutoWindowList: Boolean): Pointer {
        val nsapp = getNsApp()
        val menu = createMenu(title)
        val target = ensureActionTargetInstance()

        for (el in elements) {
            when (el) {
                is WindowStd -> addWindowStd(menu, el, nsapp, target)
                is CustomItem -> addCustom(menu, el, target)
                is CheckboxItem -> addCheckbox(menu, el, target)
                is Submenu -> { val it = createMenuItem(el.title, null, ""); val sub = createMenu(el.title); msgSendPP(it, "setSubmenu:", sub); addItemToMenu(menu, it); populateSubmenu(sub, el.children, nsapp, target) }
                Separator -> addSeparator(menu)
                else -> Unit
            }
        }

        if (!suppressAutoWindowList) msgSendPP(nsapp, "setWindowsMenu:", menu)

        return menu
    }

    private fun buildHelpMenu(title: String, elements: List<MenuElement>): Pointer {
        val menu = createMenu(title)
        val nsapp = getNsApp()
        val target = ensureActionTargetInstance()
        for (el in elements) {
            when (el) {
                is HelpItem.AppHelp -> addHelpStd(menu, el, nsapp, target)
                is CustomItem -> addCustom(menu, el, target)
                is CheckboxItem -> addCheckbox(menu, el, target)
                is Submenu -> { val it = createMenuItem(el.title, null, ""); val sub = createMenu(el.title); msgSendPP(it, "setSubmenu:", sub); addItemToMenu(menu, it); populateSubmenu(sub, el.children, nsapp, target) }
                Separator -> addSeparator(menu)
                is SystemItem, is EditStd, is ViewStd, is WindowStd, is FileStd, is FormatStd -> Unit
            }
        }
        return menu
    }

    private fun addStd(menu: Pointer, title: String, defaultSel: String, key: String, mods: Long, enabled: Boolean, icon: MenuIcon?, onClick: (() -> Unit)?, target: Pointer) {
        val has = onClick != null
        val it = createMenuItem(title, if (has) actionSelectorName else defaultSel, key)
        if (mods != 0L) setModifiers(it, mods)
        setEnabled(it, enabled); setImage(it, icon)
        if (has) { setTarget(it, target); menuActions[Pointer.nativeValue(it)] = onClick }
        addItemToMenu(menu, it)
    }
    private fun addStdToggle(menu: Pointer, title: String, defaultSel: String, key: String, mods: Long, enabled: Boolean, icon: MenuIcon?, initial: Boolean?, onToggle: ((Boolean) -> Unit)?, target: Pointer) {
        val has = onToggle != null
        val it = createMenuItem(title, if (has) actionSelectorName else defaultSel, key)
        if (mods != 0L) setModifiers(it, mods)
        setEnabled(it, enabled); setImage(it, icon)
        setState(it, initial ?: false)
        if (has) {
            setTarget(it, target)
            val id = Pointer.nativeValue(it)
            checkboxStates[id] = initial ?: false
            menuActions[id] = {
                val now = !(checkboxStates[id] ?: false)
                checkboxStates[id] = now
                setState(it, now)
                onToggle.invoke(now)
            }
        }
        addItemToMenu(menu, it)
    }
    private fun addCustom(menu: Pointer, el: CustomItem, target: Pointer) {
        val it = createMenuItem(el.title, actionSelectorName, el.keyEquivalent)
        setModifiers(it, el.modifierMask); setEnabled(it, el.enabled); setImage(it, el.icon)
        setTarget(it, target); addItemToMenu(menu, it); menuActions[Pointer.nativeValue(it)] = el.onClick
    }
    private fun addCheckbox(menu: Pointer, el: CheckboxItem, target: Pointer) {
        val it = createMenuItem(el.title, actionSelectorName, el.keyEquivalent)
        setModifiers(it, el.modifierMask); setEnabled(it, el.enabled); setState(it, el.checked); setImage(it, el.icon)
        setTarget(it, target); addItemToMenu(menu, it)
        val key = Pointer.nativeValue(it); checkboxStates[key] = el.checked
        menuActions[key] = { val now = !(checkboxStates[key] ?: false); checkboxStates[key] = now; setState(it, now); el.onToggle(now) }
    }

    private const val FIND_TAG_SHOW = 1
    private const val FIND_TAG_NEXT = 2
    private const val FIND_TAG_PREV = 3
    private const val FIND_TAG_REPLACE_ALL = 4
    private const val FIND_TAG_REPLACE = 5
    private const val FIND_TAG_REPLACE_AND_FIND = 6
    private const val FIND_TAG_SET_FIND = 7

    private fun addFind(menu: Pointer, title: String, tag: Int, key: String, mods: Long, enabled: Boolean, icon: MenuIcon?, onClick: (() -> Unit)?, target: Pointer) {
        val has = onClick != null
        val it = createMenuItem(title, if (has) actionSelectorName else "performFindPanelAction:", key)
        if (mods != 0L) setModifiers(it, mods)
        setEnabled(it, enabled); setImage(it, icon)
        if (has) {
            setTarget(it, target)
            menuActions[Pointer.nativeValue(it)] = onClick
        } else {
            setTag(it, tag)
        }
        addItemToMenu(menu, it)
    }

    fun rebuildMenuBar(menus: List<TopMenu>) {
        if (!System.getProperty("os.name").lowercase().contains("mac")) return
        runOnMain {
            val newSig = collectStructureSignature(menus)

            if (currentMenuBar != null && lastStructureSignature == newSig) {
                applyEnabledLinear(menus)
                return@runOnMain
            }

            val nsapp = getNsApp()
            val menubar = createMenu("MainMenu")

            val appIdx = menus.indexOfFirst { it is TopMenu.Application }
            if (appIdx >= 0) {
                val app = menus[appIdx] as TopMenu.Application
                val appItem = createMenuItem("", null, "")
                val appSub = buildApplicationSubmenu(app.elements)
                msgSendPP(appItem, "setSubmenu:", appSub)
                addItemToMenu(menubar, appItem)
            } else {
                val fallback = createMenuItem("", null, "")
                msgSendPP(fallback, "setSubmenu:", createMenu(""))
                addItemToMenu(menubar, fallback)
            }

            for (m in menus) {
                if (m is TopMenu.Application) continue
                when (m) {
                    is TopMenu.File -> {
                        val item = createMenuItem(m.title, null, "")
                        val sub = buildFileMenu(m.title, m.elements)
                        msgSendPP(item, "setSubmenu:", sub); addItemToMenu(menubar, item)
                    }
                    is TopMenu.Edit -> {
                        val item = createMenuItem(m.title, null, "")
                        val sub = buildEditMenu(m.title, m.elements)
                        msgSendPP(item, "setSubmenu:", sub); addItemToMenu(menubar, item)
                    }
                    is TopMenu.Format -> {
                        val item = createMenuItem(m.title, null, "")
                        val sub = buildFormatMenu(m.title, m.elements)
                        msgSendPP(item, "setSubmenu:", sub); addItemToMenu(menubar, item)
                    }
                    is TopMenu.View -> {
                        val item = createMenuItem(m.title, null, "")
                        val sub = buildViewMenu(m.title, m.elements)
                        msgSendPP(item, "setSubmenu:", sub); addItemToMenu(menubar, item)
                    }
                    is TopMenu.Window -> {
                        val item = createMenuItem(m.title, null, "")
                        val sub = buildWindowMenu(m.title, m.elements, m.suppressAutoWindowList)
                        msgSendPP(item, "setSubmenu:", sub); addItemToMenu(menubar, item)
                    }
                    is TopMenu.Help -> {
                        val item = createMenuItem(m.title, null, "")
                        val sub = buildHelpMenu(m.title, m.elements)
                        msgSendPP(item, "setSubmenu:", sub); addItemToMenu(menubar, item)
                        msgSendPP(nsapp, "setHelpMenu:", sub)
                    }
                    is TopMenu.Custom -> {
                        val item = createMenuItem(m.title, null, "")
                        val sub = buildGenericMenu(m.title, m.elements)
                        msgSendPP(item, "setSubmenu:", sub); addItemToMenu(menubar, item)
                    }
                    else -> {}
                }
            }

            msgSendPP(nsapp, "setMainMenu:", menubar)

            currentMenuBar = menubar
            lastStructureSignature = newSig
        }
    }
}