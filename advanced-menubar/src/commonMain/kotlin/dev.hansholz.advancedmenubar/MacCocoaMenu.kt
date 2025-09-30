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
        data class Settings(val title: String = "Einstellungen …", val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : SystemItem()
        data class Services(val title: String = "Dienste") : SystemItem()
        data class Hide(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : SystemItem()
        data class HideOthers(val title: String = "Andere ausblenden", val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : SystemItem()
        data class ShowAll(val title: String = "Alle einblenden", val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : SystemItem()
        data class Quit(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : SystemItem()
    }

    sealed class HelpItem : MenuElement {
        data class AppHelp(val title: String, val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : HelpItem()
    }

    sealed class EditStd : MenuElement {
        data class Undo(val title: String = "Widerrufen", val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()
        data class Redo(val title: String = "Wiederholen", val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()
        data class Cut(val title: String = "Ausschneiden", val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()
        data class Copy(val title: String = "Kopieren", val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()
        data class Paste(val title: String = "Einsetzen", val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()
        data class PasteAndMatchStyle(val title: String = "Format übernehmen", val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()
        data class Delete(val title: String = "Löschen", val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()
        data class SelectAll(val title: String = "Alles auswählen", val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : EditStd()
    }

    sealed class ViewStd : MenuElement {
        data class ShowToolbar(val title: String = "Symbolleiste ein-/ausblenden", val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : ViewStd()
        data class CustomizeToolbar(val title: String = "Symbolleiste anpassen …", val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : ViewStd()
        data class ToggleFullScreen(val title: String = "Vollbild", val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : ViewStd()
    }

    sealed class WindowStd : MenuElement {
        data class Close(val title: String = "Fenster schließen", val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : WindowStd()
        data class Minimize(val title: String = "Im Dock ablegen", val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : WindowStd()
        data class MinimizeAll(val title: String = "Alle minimieren", val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : WindowStd()
        data class Zoom(val title: String = "Zoomen", val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : WindowStd()
        data class BringAllToFront(val title: String = "Alle nach vorne bringen", val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : WindowStd()

        // Tabs optional
        data class ShowNextTab(val title: String = "Nächster Tab", val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : WindowStd()
        data class ShowPreviousTab(val title: String = "Vorheriger Tab", val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : WindowStd()
        data class MergeAllWindows(val title: String = "Alle Fenster zusammenführen", val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : WindowStd()
        data class MoveTabToNewWindow(val title: String = "Tab in neues Fenster bewegen", val enabled: Boolean = true, val icon: MenuIcon? = null, val onClick: (() -> Unit)? = null) : WindowStd()
    }

    data class TextItem(val title: String, val enabled: Boolean = false, val icon: MenuIcon? = null) : MenuElement
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
        data class Edit(val title: String, val elements: List<MenuElement>) : TopMenu()
        data class View(val title: String, val elements: List<MenuElement>) : TopMenu()
        data class Window(val title: String, val elements: List<MenuElement>, val suppressAutoWindowList: Boolean) : TopMenu()
        data class Help(val title: String, val elements: List<MenuElement>) : TopMenu()
        data class Custom(val title: String, val elements: List<MenuElement>) : TopMenu()
    }

    sealed interface MenuIcon {
        data class SFSymbol(val name: String, val template: Boolean = true) : MenuIcon
        data class Png(val bytes: ByteArray, val template: Boolean = true) : MenuIcon
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

    private fun isNull(p: Pointer?): Boolean = p == null || Pointer.nativeValue(p) == 0L
    private fun nn(p: Pointer?): Pointer = p ?: Pointer.NULL
    private fun selPtr(cmd: String) = sel(cmd)

    private fun msgSendP(recv: Pointer?, cmd: String): Pointer =
        try { objc.objc_msgSend(nn(recv), selPtr(cmd)) } catch (_: Throwable) { Pointer.NULL }

    private fun msgSendPP(recv: Pointer?, cmd: String, a: Pointer?): Pointer =
        try { objc.objc_msgSend(nn(recv), selPtr(cmd), nn(a)) } catch (_: Throwable) { Pointer.NULL }

    private fun msgSendPL(recv: Pointer?, cmd: String, l: Long): Pointer =
        try { objc.objc_msgSend(nn(recv), selPtr(cmd), l) } catch (_: Throwable) { Pointer.NULL }

    private fun msgSendPPP(recv: Pointer?, cmd: String, a: Pointer?, b: Pointer?): Pointer =
        try { objc.objc_msgSend(nn(recv), selPtr(cmd), nn(a), nn(b)) } catch (_: Throwable) { Pointer.NULL }

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

    private val actionSelectorName = "invokeMenuItem:"
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
                is TextItem -> {
                    val it = createMenuItem(child.title, null, ""); setEnabled(it, child.enabled); setImage(it, child.icon); addItemToMenu(menuPtr, it)
                }
                is Submenu -> {
                    val parent = createMenuItem(child.title, null, ""); val sub = createMenu(child.title)
                    setEnabled(parent, child.enabled); setImage(parent, child.icon)
                    msgSendPP(parent, "setSubmenu:", sub); addItemToMenu(menuPtr, parent)
                    populateSubmenu(sub, child.children, nsapp, customTarget)
                }
                Separator -> addSeparator(menuPtr)

                // Allowed in Submenus
                is SystemItem.Hide -> addSystemItemTo(menuPtr, child.title, "hide:", "h", Modifiers.command, child.enabled, child.icon, nsapp, child.onClick)
                is SystemItem.HideOthers -> addSystemItemTo(menuPtr, child.title, "hideOtherApplications:", "h", Modifiers.combo(Modifiers.command, Modifiers.option), child.enabled, child.icon, nsapp, child.onClick)
                is SystemItem.ShowAll -> addSystemItemTo(menuPtr, child.title, "unhideAllApplications:", "", Modifiers.none, child.enabled, child.icon, nsapp, child.onClick)

                // Not allowed in Submenus
                else -> Unit
            }
        }
    }

    private fun addSystemItemTo(menu: Pointer, title: String, defaultSelector: String, key: String, mods: Long, enabled: Boolean, icon: MenuIcon?, nsapp: Pointer, override: (() -> Unit)?) {
        val it = createMenuItem(title, if (override != null) actionSelectorName else defaultSelector, key)
        if (mods != 0L) setModifiers(it, mods)
        setEnabled(it, enabled); setImage(it, icon)
        setTarget(it, if (override != null) ensureActionTargetInstance() else nsapp)
        addItemToMenu(menu, it)
        if (override != null) menuActions[Pointer.nativeValue(it)] = override
    }


    private fun buildApplicationSubmenu(elements: List<MenuElement>): Pointer {
        val nsapp = getNsApp()
        val submenu = createMenu("")

        val customTarget = ensureActionTargetInstance()
        for (el in elements) {
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
                is TextItem -> { val it = createMenuItem(el.title, null, ""); setEnabled(it, el.enabled); setImage(it, el.icon); addItemToMenu(submenu, it) }
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
                is TextItem -> { val it = createMenuItem(el.title, null, ""); setEnabled(it, el.enabled); setImage(it, el.icon); addItemToMenu(menu, it) }
                is Submenu -> {
                    val it = createMenuItem(el.title, null, ""); val sub = createMenu(el.title)
                    setEnabled(it, el.enabled); setImage(it, el.icon); msgSendPP(it, "setSubmenu:", sub); addItemToMenu(menu, it)
                    populateSubmenu(sub, el.children, nsapp = nsapp, customTarget = target)
                }
                Separator -> addSeparator(menu)
                is EditStd, is ViewStd, is WindowStd, is HelpItem, is SystemItem -> {}
            }
        }
        return menu
    }

    private fun buildEditMenu(title: String, elements: List<MenuElement>): Pointer {
        val menu = createMenu(title)
        val target = ensureActionTargetInstance()
        for (el in elements) {
            when (el) {
                is EditStd.Undo -> addStd(menu, el.title, "undo:", "z", Modifiers.command, el.enabled, el.icon, el.onClick, target)
                is EditStd.Redo -> addStd(menu, el.title, "redo:", "Z", Modifiers.combo(Modifiers.command, Modifiers.shift), el.enabled, el.icon, el.onClick, target)
                is EditStd.Cut -> addStd(menu, el.title, "cut:", "x", Modifiers.command, el.enabled, el.icon, el.onClick, target)
                is EditStd.Copy -> addStd(menu, el.title, "copy:", "c", Modifiers.command, el.enabled, el.icon, el.onClick, target)
                is EditStd.Paste -> addStd(menu, el.title, "paste:", "v", Modifiers.command, el.enabled, el.icon, el.onClick, target)
                is EditStd.PasteAndMatchStyle -> addStd(menu, el.title, "pasteAsPlainText:", "V", Modifiers.combo(Modifiers.command, Modifiers.option), el.enabled, el.icon, el.onClick, target)
                is EditStd.Delete -> addStd(menu, el.title, "delete:", "\u0008", Modifiers.none, el.enabled, el.icon, el.onClick, target)
                is EditStd.SelectAll -> addStd(menu, el.title, "selectAll:", "a", Modifiers.command, el.enabled, el.icon, el.onClick, target)
                else -> { when (el) {
                    is CustomItem -> addCustom(menu, el, target)
                    is CheckboxItem -> addCheckbox(menu, el, target)
                    is TextItem -> { val it = createMenuItem(el.title, null, ""); setEnabled(it, el.enabled); setImage(it, el.icon); addItemToMenu(menu, it) }
                    is Submenu -> { val it = createMenuItem(el.title, null, ""); val sub = createMenu(el.title); msgSendPP(it, "setSubmenu:", sub); addItemToMenu(menu, it); populateSubmenu(sub, el.children, getNsApp(), target) }
                    Separator -> addSeparator(menu)
                    else -> Unit
                } }
            }
        }
        return menu
    }

    private fun buildViewMenu(title: String, elements: List<MenuElement>): Pointer {
        val menu = createMenu(title)
        val target = ensureActionTargetInstance()
        for (el in elements) {
            when (el) {
                is ViewStd.ShowToolbar -> addStd(menu, el.title, "toggleToolbarShown:", "t", Modifiers.combo(Modifiers.command, Modifiers.option), el.enabled, el.icon, el.onClick, target)
                is ViewStd.CustomizeToolbar -> addStd(menu, el.title, "runToolbarCustomizationPalette:", "", Modifiers.none, el.enabled, el.icon, el.onClick, target)
                is ViewStd.ToggleFullScreen -> addStd(menu, el.title, "toggleFullScreen:", "f", Modifiers.combo(Modifiers.command, Modifiers.control), el.enabled, el.icon, el.onClick, target)
                else -> { when (el) {
                    is CustomItem -> addCustom(menu, el, target)
                    is CheckboxItem -> addCheckbox(menu, el, target)
                    is TextItem -> { val it = createMenuItem(el.title, null, ""); setEnabled(it, el.enabled); setImage(it, el.icon); addItemToMenu(menu, it) }
                    is Submenu -> { val it = createMenuItem(el.title, null, ""); val sub = createMenu(el.title); msgSendPP(it, "setSubmenu:", sub); addItemToMenu(menu, it); populateSubmenu(sub, el.children, getNsApp(), target) }
                    Separator -> addSeparator(menu)
                    else -> Unit
                } }
            }
        }
        return menu
    }

    private fun buildHelpMenu(title: String, elements: List<MenuElement>): Pointer {
        val menu = createMenu(title)
        val nsapp = getNsApp()
        val target = ensureActionTargetInstance()
        for (el in elements) {
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
                is CustomItem -> addCustom(menu, el, target)
                is CheckboxItem -> addCheckbox(menu, el, target)
                is TextItem -> { val it = createMenuItem(el.title, null, ""); setEnabled(it, el.enabled); setImage(it, el.icon); addItemToMenu(menu, it) }
                is Submenu -> { val it = createMenuItem(el.title, null, ""); val sub = createMenu(el.title); msgSendPP(it, "setSubmenu:", sub); addItemToMenu(menu, it); populateSubmenu(sub, el.children, nsapp, target) }
                Separator -> addSeparator(menu)
                is SystemItem, is EditStd, is ViewStd, is WindowStd -> Unit
            }
        }
        return menu
    }

    private fun buildWindowMenu(title: String, elements: List<MenuElement>, suppressAutoWindowList: Boolean): Pointer {
        val nsapp = getNsApp()
        val menu = createMenu(title)
        val target = ensureActionTargetInstance()

        if (!suppressAutoWindowList) msgSendPP(nsapp, "setWindowsMenu:", menu)

        for (el in elements) {
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

                else -> { when (el) {
                    is CustomItem -> addCustom(menu, el, target)
                    is CheckboxItem -> addCheckbox(menu, el, target)
                    is TextItem -> { val it = createMenuItem(el.title, null, ""); setEnabled(it, el.enabled); setImage(it, el.icon); addItemToMenu(menu, it) }
                    is Submenu -> { val it = createMenuItem(el.title, null, ""); val sub = createMenu(el.title); msgSendPP(it, "setSubmenu:", sub); addItemToMenu(menu, it); populateSubmenu(sub, el.children, nsapp, target) }
                    Separator -> addSeparator(menu)
                    else -> Unit
                } }
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


    fun rebuildMenuBar(menus: List<TopMenu>) {
        if (!System.getProperty("os.name").lowercase().contains("mac")) return
        runOnMain {
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
                    is TopMenu.Custom -> {
                        val item = createMenuItem(m.title, null, "")
                        val sub = buildGenericMenu(m.title, m.elements)
                        msgSendPP(item, "setSubmenu:", sub); addItemToMenu(menubar, item)
                    }
                    is TopMenu.Edit -> {
                        val item = createMenuItem(m.title, null, "")
                        val sub = buildEditMenu(m.title, m.elements)
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
                    else -> {}
                }
            }

            msgSendPP(nsapp, "setMainMenu:", menubar)
        }
    }
}