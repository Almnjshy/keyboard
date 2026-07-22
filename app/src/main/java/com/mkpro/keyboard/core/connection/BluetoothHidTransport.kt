package com.mkpro.keyboard.core.connection

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Bluetooth HID transport. This is a scaffold: it wires the real Android
 * BluetoothHidDevice APIs (BluetoothHidDeviceAppSdpSettings / registerApp /
 * sendReport) in a later pass. Kept isolated behind KeyboardTransport so the
 * rest of the app never depends on Bluetooth APIs directly.
 */
class BluetoothHidTransport(private val context: Context) : KeyboardTransport {

    override val type: ConnectionType = ConnectionType.BLUETOOTH_HID

    private val _state = MutableStateFlow(ConnectionState(type = type))
    override val state: StateFlow<ConnectionState> = _state

    override suspend fun scan(): List<DiscoveredDevice> {
        // TODO: BluetoothAdapter.getBondedDevices() + LE scan for HID-capable hosts.
        return emptyList()
    }

    override suspend fun connect(device: DiscoveredDevice): Boolean {
        // TODO: BluetoothHidDevice.registerApp(...) then connect(device).
        _state.value = _state.value.copy(isConnected = false, deviceName = device.name)
        return false
    }

    override suspend fun disconnect() {
        _state.value = _state.value.copy(isConnected = false)
    }

    override suspend fun sendKeyEvent(hidReport: ByteArray) {
        // TODO: BluetoothHidDevice.sendReport(device, reportId, hidReport)
    }
}
