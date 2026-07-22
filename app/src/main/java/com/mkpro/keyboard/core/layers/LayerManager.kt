package com.mkpro.keyboard.core.layers

import com.mkpro.keyboard.core.keyboard.GamingLayout
import com.mkpro.keyboard.core.keyboard.PcKeysLayout
import com.mkpro.keyboard.core.keyboard.ProgrammingLayout
import com.mkpro.keyboard.core.keyboard.StandardLayout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Holds the set of available layers (Default, PC Keys, Programming, Gaming,
 * Macros, Custom) and the currently active one. Switching is just a state
 * update - no I/O - so it's instant per spec. Layer 1 (Default) is always
 * the IME's normal typing layer; it works with zero setup and with no PC
 * connection.
 */
class LayerManager {

    private val builtInLayers = listOf(
        Layer(id = "default", name = "Default", rows = StandardLayout.rows()),
        Layer(id = "pc_keys", name = "PC Keys", rows = PcKeysLayout.rows()),
        Layer(id = "programming", name = "Programming", rows = ProgrammingLayout.rows()),
        Layer(id = "gaming", name = "Gaming", rows = GamingLayout.rows()),
        Layer(id = "macros", name = "Macros", rows = emptyList()), // populated from MacroEngine.macros
        Layer(id = "custom", name = "Custom", rows = emptyList())  // populated by the keyboard designer
    )

    private val _layers = MutableStateFlow(builtInLayers)
    val layers: StateFlow<List<Layer>> = _layers.asStateFlow()

    private val _activeLayerId = MutableStateFlow(builtInLayers.first().id)
    val activeLayerId: StateFlow<String> = _activeLayerId.asStateFlow()

    fun switchTo(layerId: String) {
        if (_layers.value.any { it.id == layerId }) {
            _activeLayerId.value = layerId
        }
    }

    fun addCustomLayer(layer: Layer) {
        _layers.value = _layers.value + layer
    }

    fun removeLayer(layerId: String) {
        _layers.value = _layers.value.filterNot { it.id == layerId }
    }
}
