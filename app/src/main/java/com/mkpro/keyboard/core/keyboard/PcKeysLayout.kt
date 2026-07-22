package com.mkpro.keyboard.core.keyboard

/**
 * The desktop keys a phone doesn't normally have. Rendered as extra rows
 * appended below the active layer when the CommandBar's advanced panel is
 * expanded (see KeyboardScreen) - kept separate from StandardLayout so the
 * everyday typing layout stays uncluttered per spec ("collapse/expand
 * prevents UI overcrowding").
 */
object PcKeysLayout {

    fun rows(): List<List<KeyModel>> = listOf(
        listOf(
            KeyModel("print_screen", "PRTSC"),
            KeyModel("scroll_lock", "SCRLK"),
            KeyModel("pause_break", "PAUSE"),
            KeyModel("insert", "INS"),
            KeyModel("home", "HOME"),
            KeyModel("page_up", "PGUP")
        ),
        listOf(
            KeyModel("delete", "DEL"),
            KeyModel("end", "END"),
            KeyModel("page_down", "PGDN"),
            KeyModel("arrow_up", "▲")
        ),
        listOf(
            KeyModel("arrow_left", "◀"),
            KeyModel("arrow_down", "▼"),
            KeyModel("arrow_right", "▶"),
            *("F13 F14 F15 F16 F17 F18 F19 F20 F21 F22 F23 F24".split(" ")
                .map { KeyModel(it.lowercase(), it) }.toTypedArray())
        )
    )
}
