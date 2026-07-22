package com.mkpro.keyboard.ime

import android.inputmethodservice.InputMethodService
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import com.mkpro.keyboard.core.connection.ConnectionManager
import com.mkpro.keyboard.core.connection.ConnectionState
import com.mkpro.keyboard.core.keyboard.InputConnectionSender
import com.mkpro.keyboard.core.keyboard.KeyAction
import com.mkpro.keyboard.core.keyboard.KeyModel
import com.mkpro.keyboard.core.keyboard.KeyboardController
import com.mkpro.keyboard.core.keyboard.KeyboardRenderState
import com.mkpro.keyboard.core.macro.MacroEngine
import com.mkpro.keyboard.core.settings.SettingsRepository
import com.mkpro.keyboard.ui.screens.keyboard.KeyboardScreen
import com.mkpro.keyboard.ui.theme.MechanicalKeyboardProTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * The Android system keyboard. This is the app's real product: once the
 * user enables it in Settings > Languages & input, this service replaces
 * the default keyboard everywhere - browser, chat apps, Termux, games,
 * any text field. Everything here works fully offline; ConnectionManager
 * (the optional "phone as a wireless PC keyboard" feature) is only touched
 * when the user explicitly connects to a computer from the app.
 */
class KeyboardService : InputMethodService() {

    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private val lifecycleOwner = ImeLifecycleOwner()

    private val controller = KeyboardController()
    private val sender = InputConnectionSender()

    // Optional PC-connection path. Never required for the keyboard to type.
    private var connectionManager: ConnectionManager? = null
    private var macroEngine: MacroEngine? = null

    private var vibrator: Vibrator? = null
    private var hapticsEnabled = true
    private var soundEnabled = true

    override fun onCreate() {
        super.onCreate()
        vibrator = getSystemService(Vibrator::class.java)
        lifecycleOwner.performRestore()
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        // Connection + macros are constructed lazily/optionally - the keyboard
        // must fully function before either of these ever runs.
        val manager = ConnectionManager(this)
        connectionManager = manager
        macroEngine = MacroEngine(manager)

        serviceScope.launch {
            SettingsRepository(this@KeyboardService).settingsFlow.collect { settings ->
                hapticsEnabled = settings.vibrationEnabled
                soundEnabled = settings.soundEnabled
            }
        }
    }

    override fun onCreateInputView(): View {
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)

        val composeView = ComposeView(this).apply {
            setContent {
                val render: KeyboardRenderState by controller.state.collectAsState()
                val connectionState: ConnectionState by connectionManager!!.connectionState.collectAsState()

                MechanicalKeyboardProTheme {
                    KeyboardScreen(
                        render = render,
                        isConnected = connectionState.isConnected,
                        displayLabelFor = controller::displayLabelFor,
                        onKeyPressed = ::handleKeyPress,
                        onToggleExpanded = controller::toggleAdvancedPanel,
                        onCycleLayer = controller::cycleLayer
                    )
                }
            }
        }
        lifecycleOwner.attachTo(composeView)
        return composeView
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        sender.clearModifiers()
        controller.releaseNonLockingModifiers()
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    override fun onDestroy() {
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onDestroy()
    }

    private fun handleKeyPress(key: KeyModel) {
        performFeedback()

        when (key.action) {
            KeyAction.LAYER_SWITCH -> key.command?.let { controller.switchLayer(it) }
            KeyAction.MACRO -> key.macroId?.let { id ->
                serviceScope.launch { macroEngine?.run(id) }
            }
            KeyAction.SYSTEM_COMMAND -> Unit // reserved for future system-level actions
            KeyAction.HID_KEY -> {
                if (key.isModifier) {
                    controller.onModifierDown(key)
                    sender.toggleModifier(key)
                    return
                }
                // Primary path: type locally into whatever app currently has focus.
                val sentLocally = sender.send(currentInputConnection, key, controller.state.value.shiftActive)
                if (sentLocally) {
                    controller.consumeOneShotShift()
                    controller.releaseNonLockingModifiers()
                }

                // Optional secondary path: mirror the keystroke to a connected PC.
                val manager = connectionManager
                if (manager?.connectionState?.value?.isConnected == true) {
                    serviceScope.launch { manager.sendKeyEvent(byteArrayOf(key.hidUsageCode.toByte())) }
                }
            }
        }
    }

    private fun performFeedback() {
        if (hapticsEnabled) {
            vibrator?.vibrate(VibrationEffect.createOneShot(8, VibrationEffect.DEFAULT_AMPLITUDE))
        }
        // Click-sound playback (soundEnabled) hooks into a SoundPool player
        // once the mechanical switch sample set is bundled in res/raw.
    }
}
