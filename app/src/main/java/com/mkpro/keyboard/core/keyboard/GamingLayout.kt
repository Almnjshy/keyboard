package com.mkpro.keyboard.core.keyboard

/** Layer 4 (Gaming): oversized WASD cluster + programmable macro keys, per spec's Gaming Mode. */
object GamingLayout {

    fun rows(): List<List<KeyModel>> = listOf(
        listOf(
            KeyModel("macro1", "M1", widthWeight = 1.5f, action = KeyAction.MACRO, macroId = "macro1"),
            KeyModel("macro2", "M2", widthWeight = 1.5f, action = KeyAction.MACRO, macroId = "macro2"),
            KeyModel("macro3", "M3", widthWeight = 1.5f, action = KeyAction.MACRO, macroId = "macro3"),
            KeyModel("macro4", "M4", widthWeight = 1.5f, action = KeyAction.MACRO, macroId = "macro4")
        ),
        listOf(
            KeyModel("w", "W", widthWeight = 2f)
        ),
        listOf(
            KeyModel("a", "A", widthWeight = 2f),
            KeyModel("s", "S", widthWeight = 2f),
            KeyModel("d", "D", widthWeight = 2f)
        ),
        listOf(
            KeyModel("shift_l", "SHIFT", widthWeight = 2f, isModifier = true),
            KeyModel("space", "JUMP", widthWeight = 4f),
            KeyModel("ctrl_l", "CROUCH", widthWeight = 2f, isModifier = true)
        )
    )
}
