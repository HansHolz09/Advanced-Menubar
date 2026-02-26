package dev.hansholz.advancedmenubar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowPlacement
import dev.hansholz.advancedmenubar.MenuIcon.SFSymbol
import org.jetbrains.skiko.hostOs
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

@Composable
fun FrameWindowScope.DefaultMacMenuBar(
    appName: String = window.title,
    onAboutClick: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null,
    onHelpClick: (() -> Unit)? = null,
    viewMenu: Boolean = true,
    windowMenu: Boolean = true,
    helpMenu: Boolean = true,
) {
    if (!hostOs.isMacOS) return

    val majorVersion = remember {
        System.getProperty("os.version").split('.').firstOrNull()?.toIntOrNull() ?: 0
    }

    var isFullscreen by remember { mutableStateOf(false) }
    window.addComponentListener(object : ComponentAdapter() {
        override fun componentResized(e: ComponentEvent?) {
            isFullscreen = window.placement == WindowPlacement.Fullscreen
        }
    })

    AdvancedMacMenuBar(appName) {
        MacApplicationMenu {
            About(onClick = onAboutClick, icon = if (majorVersion >= 26) SFSymbol("info.circle") else null)
            Separator()
            onSettingsClick?.let {
                Settings(onClick = it, icon = if (majorVersion >= 26) SFSymbol("gear") else null)
                Separator()
            }
            Services()
            Separator()
            Hide()
            HideOthers()
            ShowAll()
            Separator()
            Quit()
        }

        if (viewMenu) {
            MacViewMenu {
                ToggleFullScreen(
                    icon = if (majorVersion >= 26) {
                        if (isFullscreen) {
                            SFSymbol("arrow.down.right.and.arrow.up.left.rectangle")
                        } else {
                            SFSymbol("arrow.up.left.and.arrow.down.right.rectangle")
                        }
                    } else null,
                )
            }
        }

        if (windowMenu) {
            MacWindowMenu {
                Minimize()
                Zoom()
                Separator()
                BringAllToFront()
            }
        }

        if (helpMenu) {
            MacHelpMenu {
                onHelpClick?.let {
                    AppHelp(onClick = it)
                }
            }
        }
    }
}