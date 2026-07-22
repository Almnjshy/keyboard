package com.mkpro.keyboard.core.keyboard

import android.view.KeyEvent

/**
 * Translates a [KeyModel] press into what the IME should actually do on the
 * phone: either commit a literal character (letters, digits, symbols) or
 * dispatch a real [KeyEvent] (ESC, TAB, arrows, function keys, navigation
 * cluster, modifiers...). This is what makes "PC keys on a phone" real -
 * every one of these keycodes is delivered to the focused app exactly like a
 * physical USB/Bluetooth keyboard would deliver it.
 */
object AndroidKeyMapper {

    /** Non-printable / control keys. Returns null when there is no 1:1 Android keycode. */
    fun keyEventCodeFor(keyId: String): Int? = when (keyId) {
        "esc" -> KeyEvent.KEYCODE_ESCAPE
        "tab" -> KeyEvent.KEYCODE_TAB
        "backspace" -> KeyEvent.KEYCODE_DEL
        "enter" -> KeyEvent.KEYCODE_ENTER
        "space" -> KeyEvent.KEYCODE_SPACE
        "caps" -> KeyEvent.KEYCODE_CAPS_LOCK
        "shift_l" -> KeyEvent.KEYCODE_SHIFT_LEFT
        "shift_r" -> KeyEvent.KEYCODE_SHIFT_RIGHT
        "ctrl_l" -> KeyEvent.KEYCODE_CTRL_LEFT
        "ctrl_r" -> KeyEvent.KEYCODE_CTRL_RIGHT
        "alt_l" -> KeyEvent.KEYCODE_ALT_LEFT
        "alt_r" -> KeyEvent.KEYCODE_ALT_RIGHT
        "win" -> KeyEvent.KEYCODE_META_LEFT
        "menu" -> KeyEvent.KEYCODE_MENU
        "fn" -> KeyEvent.KEYCODE_FUNCTION
        "arrow_up" -> KeyEvent.KEYCODE_DPAD_UP
        "arrow_down" -> KeyEvent.KEYCODE_DPAD_DOWN
        "arrow_left" -> KeyEvent.KEYCODE_DPAD_LEFT
        "arrow_right" -> KeyEvent.KEYCODE_DPAD_RIGHT
        "home" -> KeyEvent.KEYCODE_MOVE_HOME
        "end" -> KeyEvent.KEYCODE_MOVE_END
        "insert" -> KeyEvent.KEYCODE_INSERT
        "delete" -> KeyEvent.KEYCODE_FORWARD_DEL
        "page_up" -> KeyEvent.KEYCODE_PAGE_UP
        "page_down" -> KeyEvent.KEYCODE_PAGE_DOWN
        "print_screen" -> KeyEvent.KEYCODE_SYSRQ
        "scroll_lock" -> KeyEvent.KEYCODE_SCROLL_LOCK
        "pause_break" -> KeyEvent.KEYCODE_BREAK
        else -> functionKeyCode(keyId)
    }

    private fun functionKeyCode(keyId: String): Int? {
        val match = Regex("^f(\\d{1,2})$").find(keyId) ?: return null
        val n = match.groupValues[1].toIntOrNull() ?: return null
        // The Android framework only defines physical keycodes up to F12;
        // F13-F24 have no KEYCODE_* equivalent, so those keys are exposed in
        // the UI (for the optional PC/HID mode) but are no-ops for local typing.
        return if (n in 1..12) KeyEvent.KEYCODE_F1 + (n - 1) else null
    }

    /** True for keys that should be *held* (as modifier state) rather than tapped. */
    fun isModifierId(keyId: String): Boolean =
        keyId in setOf("ctrl_l", "ctrl_r", "alt_l", "alt_r", "shift_l", "shift_r", "win", "fn")

    /** Printable character for a key given current shift/caps state, or null for non-printable keys. */
    fun printableChar(key: KeyModel, shiftActive: Boolean): Char? {
        val label = key.label
        if (label.length != 1) return null
        val c = label[0]
        return when {
            c.isLetter() -> if (shiftActive) c.uppercaseChar() else c.lowercaseChar()
            shiftActive -> shiftedSymbol(c) ?: c
            else -> c
        }
    }

    private fun shiftedSymbol(c: Char): Char? = when (c) {
        '1' -> '!'; '2' -> '@'; '3' -> '#'; '4' -> '$'; '5' -> '%'
        '6' -> '^'; '7' -> '&'; '8' -> '*'; '9' -> '('; '0' -> ')'
        '-' -> '_'; '=' -> '+'; '[' -> '{'; ']' -> '}'; ';' -> ':'
        '\'' -> '"'; ',' -> '<'; '.' -> '>'; '/' -> '?'; '`' -> '~'; '\\' -> '|'
        else -> null
    }
}
