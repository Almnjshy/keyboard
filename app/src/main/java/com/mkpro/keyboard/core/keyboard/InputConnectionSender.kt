package com.mkpro.keyboard.core.keyboard

import android.view.KeyEvent
import android.view.inputmethod.InputConnection

/**
 * Wraps an [InputConnection] and turns [KeyModel] presses into either a
 * committed character or a real KeyEvent down/up pair, honoring currently
 * held modifiers (CTRL/ALT/SHIFT/WIN) so combos like CTRL+C work exactly
 * like on a desktop keyboard. This is the path used whenever the phone has
 * no PC connection - i.e. always, by default.
 */
class InputConnectionSender {

    private val heldModifiers = mutableSetOf<Int>()

    fun onModifierDown(key: KeyModel) {
        AndroidKeyMapper.keyEventCodeFor(key.id)?.let { heldModifiers.add(it) }
    }

    fun onModifierUp(key: KeyModel) {
        AndroidKeyMapper.keyEventCodeFor(key.id)?.let { heldModifiers.remove(it) }
    }

    /**
     * Touch keycaps have no physical "key-up" gesture, so CTRL/ALT/WIN behave
     * like sticky keys: tap to hold, tap again to release. Whatever non-modifier
     * key is tapped next goes out as a combo (e.g. CTRL+C), then the modifier
     * auto-releases after that one keypress.
     */
    fun toggleModifier(key: KeyModel) {
        val code = AndroidKeyMapper.keyEventCodeFor(key.id) ?: return
        if (!heldModifiers.add(code)) heldModifiers.remove(code)
    }

    fun releaseModifiersAfterCombo() {
        if (heldModifiers.isNotEmpty()) heldModifiers.clear()
    }

    fun clearModifiers() = heldModifiers.clear()

    private fun metaState(): Int {
        var state = 0
        heldModifiers.forEach { code ->
            state = state or when (code) {
                KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT -> KeyEvent.META_SHIFT_ON
                KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_CTRL_RIGHT -> KeyEvent.META_CTRL_ON
                KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> KeyEvent.META_ALT_ON
                KeyEvent.KEYCODE_META_LEFT, KeyEvent.KEYCODE_META_RIGHT -> KeyEvent.META_META_ON
                else -> 0
            }
        }
        return state
    }

    /**
     * Sends [key] to [ic]. Returns true if it produced any input so callers
     * can e.g. consume a one-shot Shift after a single character.
     */
    fun send(ic: InputConnection?, key: KeyModel, shiftActive: Boolean): Boolean {
        if (ic == null) return false

        val keyCode = AndroidKeyMapper.keyEventCodeFor(key.id)
        val hasModifierHeld = heldModifiers.isNotEmpty()

        // Combo: a non-modifier key pressed while CTRL/ALT/WIN is held must go
        // through real KeyEvents (commitText can't carry modifier state).
        if (hasModifierHeld && keyCode == null) {
            val printable = AndroidKeyMapper.printableChar(key, shiftActive)
            if (printable != null) {
                val meta = metaState()
                val down = KeyEvent(0, 0, KeyEvent.ACTION_DOWN, keyCodeForChar(printable), 0, meta)
                val up = KeyEvent(0, 0, KeyEvent.ACTION_UP, keyCodeForChar(printable), 0, meta)
                ic.sendKeyEvent(down)
                ic.sendKeyEvent(up)
                releaseModifiersAfterCombo()
                return true
            }
        }

        if (keyCode != null) {
            val meta = metaState()
            ic.sendKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_DOWN, keyCode, 0, meta))
            ic.sendKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_UP, keyCode, 0, meta))
            if (hasModifierHeld) releaseModifiersAfterCombo()
            return true
        }

        val printable = AndroidKeyMapper.printableChar(key, shiftActive)
        if (printable != null) {
            ic.commitText(printable.toString(), 1)
            return true
        }

        return false
    }

    private fun keyCodeForChar(c: Char): Int {
        val events = KeyEvent(0, 0)
        val code = android.view.KeyCharacterMap.load(android.view.KeyCharacterMap.VIRTUAL_KEYBOARD)
            .runCatching { getEvents(charArrayOf(c))?.firstOrNull()?.keyCode }
            .getOrNull()
        return code ?: events.keyCode
    }
}
