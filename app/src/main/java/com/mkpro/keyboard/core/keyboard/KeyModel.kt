package com.mkpro.keyboard.core.keyboard

enum class SwitchType { BLUE, RED, BROWN, BLACK, SILENT }

enum class KeyAction { HID_KEY, MACRO, LAYER_SWITCH, SYSTEM_COMMAND }

/**
 * A single key on the virtual keyboard. `widthWeight` lets keys like Space
 * or Enter span more horizontal space in the row-based layout (see
 * KeyboardLayout). `hidUsageCode` is the USB HID usage ID sent over the
 * optional PC transport; `androidKeyCode` (an android.view.KeyEvent.KEYCODE_*
 * constant) is what the IME uses to type locally on the phone - this is the
 * primary, always-available path. `isModifier` marks CTRL/ALT/SHIFT/WIN/FN so
 * the IME can track held-modifier combos (e.g. CTRL+C) instead of treating
 * every keycap as a single tap. `macroId`/`command` are used when
 * action != HID_KEY.
 */
data class KeyModel(
    val id: String,
    val label: String,
    val hidUsageCode: Int = 0,
    val androidKeyCode: Int? = null,
    val isModifier: Boolean = false,
    val widthWeight: Float = 1f,
    val action: KeyAction = KeyAction.HID_KEY,
    val macroId: String? = null,
    val command: String? = null,
    val icon: String? = null
)
