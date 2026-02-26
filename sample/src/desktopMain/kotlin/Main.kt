
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.hansholz.advancedmenubar.DefaultMacMenuBar
import dev.hansholz.advancedmenubar.MenubarLanguage
import org.jetbrains.skiko.hostOs
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
                if (hostOs.isMacOS) {
                    DisposableEffect(window) {
                        val listener = object : java.awt.event.WindowFocusListener {
                            override fun windowGainedFocus(e: java.awt.event.WindowEvent?) { focusTrigger = !focusTrigger }
                            override fun windowLostFocus(e: java.awt.event.WindowEvent?) {}
                        }
                        window.addWindowFocusListener(listener)
                        onDispose { window.removeWindowFocusListener(listener) }
                    }
                }

                val clickedItems = remember { mutableStateListOf<String>() }
                val customMenus = remember { mutableStateListOf(1) }

                val selectedMenu = remember { mutableStateOf(0) }

                val checkboxItem1 = remember { mutableStateOf(false) }
                val checkboxItem2 = remember { mutableStateOf(true) }
                val checkboxItem3 = remember { mutableStateOf(true) }

                val textFieldState = rememberTextFieldState()

                key(focusTrigger, language.value) {
                    when (selectedMenu.value) {
                        0 ->
                            MenuBar(
                                customMenus = customMenus,
                                checkboxItem1 = checkboxItem1,
                                checkboxItem2 = checkboxItem2,
                                checkboxItem3 = checkboxItem3,
                                textFieldState = textFieldState
                            ) {
                                clickedItems += it
                            }
                        1 ->
                            DefaultMacMenuBar(
                                onAboutClick = { clickedItems += "About" },
                                onSettingsClick = { clickedItems += "Settings" },
                                onHelpClick = { clickedItems += "Help" },
                            )
                        2 -> FullMacMenuBar()
                    }
                }

                App(
                    language = language,
                    clickedItems = clickedItems,
                    customMenus = customMenus,
                    selectedMenu = selectedMenu,
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