package com.mkpro.keyboard.ui.screens.keyboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkpro.keyboard.core.connection.ConnectionManager
import com.mkpro.keyboard.core.keyboard.KeyModel
import com.mkpro.keyboard.core.keyboard.KeyboardController
import com.mkpro.keyboard.core.keyboard.KeyboardRenderState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class KeyboardUiState(
    val render: KeyboardRenderState = KeyboardRenderState(),
    val isConnected: Boolean = false
)

/**
 * In-app preview/onboarding surface: this is NOT the real keyboard (that's
 * KeyboardService, the actual InputMethodService). It shares the same
 * framework-agnostic KeyboardController so what the user sees here matches
 * what they'll get once they enable the keyboard system-wide. An optional
 * ConnectionManager can be attached for the "connect to PC" flow, but the
 * preview works fine without one.
 */
class KeyboardViewModel : ViewModel() {

    private val controller = KeyboardController()
    private var connectionManager: ConnectionManager? = null

    fun attachConnectionManager(manager: ConnectionManager) {
        connectionManager = manager
    }

    private val _uiState = MutableStateFlow(KeyboardUiState())
    val uiState: StateFlow<KeyboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            controller.state.collect { render ->
                _uiState.value = _uiState.value.copy(render = render)
            }
        }
        viewModelScope.launch {
            connectionManager?.connectionState?.collect { state ->
                _uiState.value = _uiState.value.copy(isConnected = state.isConnected)
            }
        }
    }

    fun onKeyPressed(key: KeyModel) {
        if (key.isModifier) {
            controller.onModifierDown(key)
            return
        }
        // Preview-only: there's no real InputConnection here, so this just
        // demonstrates the optional PC path. Local typing happens for real
        // in KeyboardService once the keyboard is enabled.
        viewModelScope.launch {
            connectionManager?.sendKeyEvent(byteArrayOf(key.hidUsageCode.toByte()))
        }
        controller.consumeOneShotShift()
    }

    fun onLayerSelected(layerId: String) = controller.switchLayer(layerId)

    fun cycleLayer() = controller.cycleLayer()

    fun toggleAdvancedPanel() = controller.toggleAdvancedPanel()

    fun displayLabelFor(key: KeyModel): String = controller.displayLabelFor(key)
}
