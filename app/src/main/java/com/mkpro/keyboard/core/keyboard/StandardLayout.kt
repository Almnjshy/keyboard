package com.mkpro.keyboard.core.keyboard

/**
 * Row-based definition of the default QWERTY layer, matching the mockup's
 * "Standard Mode": ESC/F-row, number row, QWERTY rows, modifiers, space row.
 * This is intentionally data, not UI - KeyboardScreen just renders whatever
 * KeyboardLayout it's given, so Function/Gaming/custom layers reuse the
 * same renderer.
 */
object StandardLayout {

    fun rows(): List<List<KeyModel>> = listOf(
        listOf(
            KeyModel("esc", "ESC"),
            *("F1 F2 F3 F4 F5 F6 F7 F8 F9 F10 F11 F12".split(" ")
                .map { KeyModel(it.lowercase(), it) }.toTypedArray())
        ),
        listOf(
            KeyModel("grave", "`"),
            *("1 2 3 4 5 6 7 8 9 0".split(" ").map { KeyModel("n$it", it) }.toTypedArray()),
            KeyModel("minus", "-"),
            KeyModel("equal", "="),
            KeyModel("backspace", "⌫", widthWeight = 2f)
        ),
        listOf(
            KeyModel("tab", "TAB", widthWeight = 1.5f),
            *("Q W E R T Y U I O P".split(" ").map { KeyModel(it.lowercase(), it) }.toTypedArray()),
            KeyModel("bracket_l", "["),
            KeyModel("bracket_r", "]")
        ),
        listOf(
            KeyModel("caps", "CAPS", widthWeight = 1.75f),
            *("A S D F G H J K L".split(" ").map { KeyModel(it.lowercase(), it) }.toTypedArray()),
            KeyModel("semicolon", ";"),
            KeyModel("quote", "'"),
            KeyModel("enter", "ENTER", widthWeight = 2f)
        ),
        listOf(
            KeyModel("shift_l", "SHIFT", widthWeight = 2.25f, isModifier = true),
            *("Z X C V B N M".split(" ").map { KeyModel(it.lowercase(), it) }.toTypedArray()),
            KeyModel("comma", ","),
            KeyModel("period", "."),
            KeyModel("slash", "/"),
            KeyModel("shift_r", "SHIFT", widthWeight = 2.25f, isModifier = true)
        ),
        listOf(
            KeyModel("ctrl_l", "CTRL", widthWeight = 1.25f, isModifier = true),
            KeyModel("win", "WIN", widthWeight = 1.25f, isModifier = true),
            KeyModel("alt_l", "ALT", widthWeight = 1.25f, isModifier = true),
            KeyModel("space", "SPACE", widthWeight = 6f),
            KeyModel("alt_r", "ALT", widthWeight = 1.25f, isModifier = true),
            KeyModel("fn", "FN", widthWeight = 1.25f, isModifier = true),
            KeyModel("ctrl_r", "CTRL", widthWeight = 1.25f, isModifier = true)
        )
    )
}
