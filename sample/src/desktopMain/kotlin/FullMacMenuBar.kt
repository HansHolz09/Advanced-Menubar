import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import dev.hansholz.advancedmenubar.AdvancedMacMenuBar
import dev.hansholz.advancedmenubar.MenuIcon.SFSymbol

@Composable
fun FrameWindowScope.FullMacMenuBar() {
    AdvancedMacMenuBar(appName = window.title) {
        MacApplicationMenu {
            About()
            Separator()
            Settings()
            Separator()
            Services()
            Separator()
            Hide()
            HideOthers()
            ShowAll()
            Separator()
            Quit()
        }

        MacFileMenu {
            FileNew {}
            FileOpen {}
            FileOpenRecent {
                Item("PDF_01", icon = SFSymbol("doc.richtext")) {}
                Item("Picture_02", icon = SFSymbol("photo")) {}
                Separator()
                FileClearRecent {}
            }
            Separator()
            FileClose()
            FileSave {}
            FileSaveAs {}
            FileDuplicate {}
            FileRename {}
            FileMoveTo {}
            Separator()
            FilePageSetup()
            FilePrint {}
        }

        MacEditMenu {
            Undo {}
            Redo {}
            Separator()
            Cut {}
            Copy {}
            Paste {}
            PasteAndMatchStyle {}
            Delete {}
            SelectAll {}

            Separator()

            FindMenu {
                Find {}
                FindAndReplace {}
                FindNext {}
                FindPrevious {}
                UseSelectionForFind {}
                JumpToSelection {}
            }

            SpellingAndGrammarMenu {
                ToggleCorrectSpellingAutomatically {}
            }

            SubstitutionsMenu {
                ToggleSmartQuotes {}
                ToggleSmartDashes {}
                ToggleSmartLinks {}
                ToggleTextReplacement {}
            }

            TransformationsMenu {
                MakeUpperCase {}
                MakeLowerCase {}
                Capitalize {}
            }

            SpeechMenu {
                StartSpeaking {}
                StopSpeaking {}
            }
        }

        MacFormatMenu {
            FontMenu {
                ShowFonts()
                Bold {}
                Italic {}
                Underline {}
                Separator()
                Bigger {}
                Smaller {}
                Separator()
                KerningMenu {
                    KerningStandard {}
                    KerningNone {}
                    KerningTighten {}
                    KerningLoosen {}
                }
                LigaturesMenu {
                    LigaturesNone {}
                    LigaturesStandard {}
                    LigaturesAll {}
                }
                BaselineMenu {
                    BaselineStandard {}
                    Superscript {}
                    Subscript {}
                    RaiseBaseline {}
                    LowerBaseline {}
                }
                Separator()
                ShowColors()
            }
            Separator()
            TextMenu {
                AlignLeft {}
                AlignCenter {}
                AlignJustified {}
                AlignRight {}
            }
        }

        MacViewMenu {
            ShowToolbar {}
            CustomizeToolbar {}
            Separator()
            ToggleSidebar {}
            ToggleTabBar {}
            Separator()
            ToggleFullScreen()
        }

        MacWindowMenu {
            Close()
            Minimize()
            MinimizeAll()
            Zoom()
            Separator()
            ShowNextTab {}
            ShowPreviousTab {}
            MergeAllWindows {}
            MoveTabToNewWindow {}
            Separator()
            BringAllToFront()
        }

        MacHelpMenu {
            AppHelp()
        }
    }
}