package dev.hansholz.advancedmenubar

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import org.jetbrains.skiko.hostOs

@Composable
fun FrameWindowScope.DefaultMacMenu(
    appName: String = window.title,
    onAboutClick: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null,
    onHelpClick: (() -> Unit)? = null,
    viewMenu: Boolean = true,
    windowMenu: Boolean = true,
    helpMenu: Boolean = true,
) {
    if (!hostOs.isMacOS) return

    AdvancedMacMenu(appName) {
        MacApplicationMenu {
            About(onClick = onAboutClick)
            Separator()
            onSettingsClick?.let {
                Settings(onClick = it)
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
                ToggleFullScreen()
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