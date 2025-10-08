
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import androidx.compose.ui.input.key.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.hansholz.advancedmenubar.DefaultMacMenu
import dev.hansholz.advancedmenubar.MenubarLanguage
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun main() = application {
    val language = remember { mutableStateOf<MenubarLanguage?>(null) }

    val windows = remember { mutableStateListOf("Sample" to true) }
    windows.forEachIndexed { index, (title, visible) ->
        if (visible) {
            Window(
                onCloseRequest = {
                    if (windows.size == 1) {
                        exitApplication()
                    } else {
                        windows[index] = title to false
                    }
                },
                title = title,
                onPreviewKeyEvent = { e ->
                    if (e.type != KeyEventType.KeyDown) return@Window false
                    val primary = e.isCtrlPressed || e.isMetaPressed
                    when {
                        // Ignore some shortcuts in Compose, those are handled from the MenuBar
                        primary && e.key == Key.C -> { true }
                        primary && e.key == Key.V -> { true }
                        primary && e.key == Key.X -> { true }
                        primary && e.key == Key.A -> { true }
                        else -> false
                    }
                }
            ) {
                window.apply {
                    rootPane.putClientProperty("apple.awt.fullWindowContent", true)
                    rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
                    rootPane.putClientProperty("apple.awt.windowTitleVisible", true)
                }

                var focusTrigger by remember { mutableStateOf(false) }
                DisposableEffect(window) {
                    val listener = object : java.awt.event.WindowFocusListener {
                        override fun windowGainedFocus(e: java.awt.event.WindowEvent?) { focusTrigger = !focusTrigger }
                        override fun windowLostFocus(e: java.awt.event.WindowEvent?) {}
                    }
                    window.addWindowFocusListener(listener)
                    onDispose { window.removeWindowFocusListener(listener) }
                }

                val clickedItems = remember { mutableStateListOf<String>() }
                val customMenus = remember { mutableStateListOf<Int>() }

                val showDefaultMenu = remember { mutableStateOf(false) }

                val checkboxItem1 = remember { mutableStateOf(false) }
                val checkboxItem2 = remember { mutableStateOf(true) }
                val checkboxItem3 = remember { mutableStateOf(true) }

                val textFieldState = rememberTextFieldState()

                key(focusTrigger, language.value) {
                    if (showDefaultMenu.value) {
                        DefaultMacMenu(
                            onAboutClick = { println("About Clicked") },
                            onSettingsClick = { println("Settings Clicked") },
                            onHelpClick = { println("Help Clicked") },
                        )
                    } else {
                        MenuBar(
                            window = window,
                            customMenus = customMenus,
                            checkboxItem1 = checkboxItem1,
                            checkboxItem2 = checkboxItem2,
                            checkboxItem3 = checkboxItem3,
                            textFieldState = textFieldState
                        ) {
                            clickedItems += it
                        }
                    }
                }

                App(
                    language = language,
                    clickedItems = clickedItems,
                    customMenus = customMenus,
                    showDefaultMenu = showDefaultMenu,
                    checkboxItem1 = checkboxItem1,
                    checkboxItem2 = checkboxItem2,
                    checkboxItem3 = checkboxItem3,
                    textFieldState = textFieldState
                ) {
                    windows += "Sample Window ${windows.size + 1}" to true
                }
            }
        }
    }
}