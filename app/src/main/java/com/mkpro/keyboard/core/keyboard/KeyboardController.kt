package com.mkpro.keyboard.core.keyboard

import com.mkpro.keyboard.core.layers.LayerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

enum class ShiftState { NONE, ONE_SHOT, CAPS_LOCK }

data class KeyboardRenderState(
    val baseRows: List<List<KeyModel>> = emptyList(),
    val pcKeysRows: List<List<KeyModel>> = emptyList(),
    val activeLayerName: String = "Default",
    val isAdvancedPanelExpanded: Boolean = false,
    val shiftState: ShiftState = ShiftState.NONE,
    val heldModifierIds: Set<String> = emptySet()
) {
    /** Rows to actually render: base layer, plus the PC-keys cluster when expanded. */
    val visibleRows: List<List<KeyModel>>
        get() = if (isAdvancedPanelExpanded) baseRows + pcKeysRows else baseRows

    val shiftActive: Boolean get() = shiftState != ShiftState.NONE
}

/**
 * Everything about "which keys are on screen right now" lives here, with no
 * Android Context/InputConnection dependency - it's shared as-is by
 * KeyboardService (the real IME) and KeyboardViewModel (the in-app preview).
 * Sending the resulting key to the phone or to a PC transport is the
 * caller's job (see InputConnectionSender / ConnectionManager).
 */
class KeyboardController(private val layerManager: LayerManager = LayerManager()) {

    private val scope = CoroutineScope(SupervisorJob())

    private val _state = MutableStateFlow(KeyboardRenderState())
    val state: StateFlow<KeyboardRenderState> = _state.asStateFlow()

    init {
        scope.launch {
            combine(layerManager.layers, layerManager.activeLayerId) { layers, activeId ->
                layers.firstOrNull { it.id == activeId }
            }.collect { active ->
                _state.value = _state.value.copy(
                    baseRows = active?.rows.orEmpty(),
                    pcKeysRows = if (active?.id == "default") PcKeysLayout.rows() else emptyList(),
                    activeLayerName = active?.name ?: "Default"
                )
            }
        }
    }

    fun switchLayer(layerId: String) = layerManager.switchTo(layerId)

    private val cyclableLayerIds = listOf("default", "pc_keys", "programming", "gaming")

    /** Cycles Default -> PC Keys -> Programming -> Gaming -> Default, per spec's "switch layers instantly". */
    fun cycleLayer() {
        val currentIndex = cyclableLayerIds.indexOf(layerManager.activeLayerId.value).coerceAtLeast(0)
        val next = cyclableLayerIds[(currentIndex + 1) % cyclableLayerIds.size]
        layerManager.switchTo(next)
    }

    fun toggleAdvancedPanel() {
        _state.value = _state.value.copy(isAdvancedPanelExpanded = !_state.value.isAdvancedPanelExpanded)
    }

    /** Renders a key's label uppercased while Shift/Caps is active, for display only. */
    fun displayLabelFor(key: KeyModel): String {
        if (key.label.length != 1 || !key.label[0].isLetter()) return key.label
        val shiftOn = _state.value.shiftActive
        return if (shiftOn) key.label.uppercase() else key.label.lowercase()
    }

    fun onModifierDown(key: KeyModel) {
        when (key.id) {
            "shift_l", "shift_r" -> cycleShift()
            "caps" -> {
                _state.value = _state.value.copy(
                    shiftState = if (_state.value.shiftState == ShiftState.CAPS_LOCK) ShiftState.NONE else ShiftState.CAPS_LOCK
                )
            }
            else -> _state.value = _state.value.copy(heldModifierIds = _state.value.heldModifierIds + key.id)
        }
    }

    fun releaseNonLockingModifiers() {
        _state.value = _state.value.copy(heldModifierIds = emptySet())
    }

    /** Called after a printable character is typed, to consume a one-shot Shift tap. */
    fun consumeOneShotShift() {
        if (_state.value.shiftState == ShiftState.ONE_SHOT) {
            _state.value = _state.value.copy(shiftState = ShiftState.NONE)
        }
    }

    private fun cycleShift() {
        _state.value = _state.value.copy(
            shiftState = if (_state.value.shiftState == ShiftState.ONE_SHOT) ShiftState.NONE else ShiftState.ONE_SHOT
        )
    }
}
