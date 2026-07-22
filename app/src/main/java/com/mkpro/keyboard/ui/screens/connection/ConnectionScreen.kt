package com.mkpro.keyboard.ui.screens.connection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mkpro.keyboard.core.connection.ConnectionType
import com.mkpro.keyboard.ui.theme.MkAccentCyan
import com.mkpro.keyboard.ui.theme.MkBackground
import com.mkpro.keyboard.ui.theme.MkBorder
import com.mkpro.keyboard.ui.theme.MkSurface
import com.mkpro.keyboard.ui.theme.MkTextPrimary
import com.mkpro.keyboard.ui.theme.MkTextSecondary

/**
 * Lists available transports (Bluetooth / USB / Wi-Fi) and recent devices.
 * Wired to ConnectionManager once device-picking UI/permission flows are
 * built out; for now onDeviceConnected can be triggered directly to
 * validate the navigation flow end-to-end.
 */
@Composable
fun ConnectionScreen(onDeviceConnected: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MkBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "اتصال",
            color = MkTextPrimary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "اختر طريقة الاتصال بالحاسوب",
            color = MkTextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )

        listOf(
            "Bluetooth" to ConnectionType.BLUETOOTH_HID,
            "USB" to ConnectionType.USB_HID,
            "Wi-Fi" to ConnectionType.WIFI
        ).forEach { (label, _) ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MkSurface, RoundedCornerShape(14.dp))
                    .clickable { /* TODO: launch scan flow for this transport */ }
                    .padding(16.dp)
            ) {
                Text(text = label, color = MkTextPrimary, style = MaterialTheme.typography.titleMedium)
                Text(text = "لا توجد أجهزة متصلة حالياً", color = MkTextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
        }

        Button(onClick = onDeviceConnected, modifier = Modifier.fillMaxWidth()) {
            Text("متابعة (وضع تجريبي)")
        }
    }
}
