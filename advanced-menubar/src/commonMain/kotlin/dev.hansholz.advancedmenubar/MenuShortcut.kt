package dev.hansholz.advancedmenubar

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut

data class MenuShortcut(
    val key: Key,
    val meta: Boolean = false,  // ⌘
    val ctrl: Boolean = false,  // ⌃
    val alt: Boolean = false,   // ⌥
    val shift: Boolean = false  // ⇧
)

private fun fk(c: Int) = String(Character.toChars(c))

private fun composeKeyToCocoaChar(key: Key): String? = when (key) {
    Key.Spacebar -> " "
    Key.Tab -> "\u0009"
    Key.Enter, Key.NumPadEnter -> "\u000D"
    Key.Escape -> "\u001B"
    Key.Backspace -> "\u0008"
    Key.Delete -> "\u007F"

    Key.DirectionUp -> fk(0xF700)
    Key.DirectionDown -> fk(0xF701)
    Key.DirectionLeft -> fk(0xF702)
    Key.DirectionRight -> fk(0xF703)

    Key.Home, Key.MoveHome -> fk(0xF729)
    Key.MoveEnd -> fk(0xF72B)
    Key.PageUp -> fk(0xF72C)
    Key.PageDown -> fk(0xF72D)

    Key.F1 -> fk(0xF704)
    Key.F2 -> fk(0xF705)
    Key.F3 -> fk(0xF706)
    Key.F4 -> fk(0xF707)
    Key.F5 -> fk(0xF708)
    Key.F6 -> fk(0xF709)
    Key.F7 -> fk(0xF70A)
    Key.F8 -> fk(0xF70B)
    Key.F9 -> fk(0xF70C)
    Key.F10 -> fk(0xF70D)
    Key.F11 -> fk(0xF70E)
    Key.F12 -> fk(0xF70F)

    Key.Zero, Key.NumPad0 -> "0"
    Key.One, Key.NumPad1 -> "1"
    Key.Two, Key.NumPad2 -> "2"
    Key.Three, Key.NumPad3 -> "3"
    Key.Four, Key.NumPad4 -> "4"
    Key.Five, Key.NumPad5 -> "5"
    Key.Six, Key.NumPad6 -> "6"
    Key.Seven, Key.NumPad7 -> "7"
    Key.Eight, Key.NumPad8 -> "8"
    Key.Nine, Key.NumPad9 -> "9"

    Key.A -> "a"; Key.B -> "b"; Key.C -> "c"; Key.D -> "d"; Key.E -> "e"; Key.F -> "f"
    Key.G -> "g"; Key.H -> "h"; Key.I -> "i"; Key.J -> "j"; Key.K -> "k"; Key.L -> "l"
    Key.M -> "m"; Key.N -> "n"; Key.O -> "o"; Key.P -> "p"; Key.Q -> "q"; Key.R -> "r"
    Key.S -> "s"; Key.T -> "t"; Key.U -> "u"; Key.V -> "v"; Key.W -> "w"; Key.X -> "x"
    Key.Y -> "y"; Key.Z -> "z"

    Key.Plus, Key.NumPadAdd -> "+"
    Key.Minus, Key.NumPadSubtract -> "-"
    Key.Multiply, Key.NumPadMultiply -> "*"
    Key.Equals, Key.NumPadEquals -> "="

    Key.Comma, Key.NumPadComma -> ","
    Key.Period, Key.NumPadDot -> "."
    Key.Slash, Key.NumPadDivide -> "/"
    Key.Backslash -> "\\"
    Key.Semicolon -> ";"
    Key.Apostrophe -> "'"
    Key.Grave -> "`"
    Key.LeftBracket -> "["
    Key.RightBracket -> "]"
    Key.At -> "@"
    Key.Pound -> "#"

    else -> null
}

internal fun MenuShortcut.toCocoa(): Pair<String, Long>? {
    val keyEq = composeKeyToCocoaChar(key) ?: return null
    val mask =
        (if (meta)   1L shl 20 else 0L) or
                (if (shift)  1L shl 17 else 0L) or
                (if (alt)    1L shl 19 else 0L) or
                (if (ctrl)   1L shl 18 else 0L)
    return keyEq to mask
}

internal fun MenuShortcut.toKeyShortcut() = KeyShortcut(key, ctrl, meta, alt, shift)