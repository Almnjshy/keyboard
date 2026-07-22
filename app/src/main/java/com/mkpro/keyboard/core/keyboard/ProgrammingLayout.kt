package com.mkpro.keyboard.core.keyboard

/** Layer 3 (Programming): symbols coders reach for constantly, one tap each. */
object ProgrammingLayout {

    fun rows(): List<List<KeyModel>> = listOf(
        listOf("{", "}", "[", "]", "(", ")", "<", ">").map { KeyModel(it.hashCode().toString(), it) },
        listOf(";", ":", "\"", "'", "\\", "|", "`", "~").map { KeyModel(it.hashCode().toString(), it) },
        listOf("=", "+", "-", "*", "/", "%", "&", "^").map { KeyModel(it.hashCode().toString(), it) },
        listOf(
            KeyModel("tab", "TAB", widthWeight = 1.5f),
            KeyModel("underscore", "_"),
            KeyModel("dollar", "$"),
            KeyModel("hash", "#"),
            KeyModel("at", "@"),
            KeyModel("space", "SPACE", widthWeight = 3f),
            KeyModel("enter", "ENTER", widthWeight = 1.5f)
        )
    )
}
