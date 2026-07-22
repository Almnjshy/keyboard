package com.mkpro.keyboard.ui.screens.home

import android.content.Context
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mkpro.keyboard.ui.theme.MkAccentGreen
import com.mkpro.keyboard.ui.theme.MkAccentPurple
import com.mkpro.keyboard.ui.theme.MkBackground
import com.mkpro.keyboard.ui.theme.MkSurface
import com.mkpro.keyboard.ui.theme.MkTextPrimary
import com.mkpro.keyboard.ui.theme.MkTextSecondary

/** True once the user has ticked "Mechanical Keyboard Pro" on in Settings > Languages & input. */
private fun isImeEnabled(context: Context): Boolean {
    val enabled = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_INPUT_METHODS)
    return enabled?.contains(context.packageName) == true
}

/** True once it's also the *active* keyboard the user is currently typing with. */
private fun isImeSelected(context: Context): Boolean {
    val current = Settings.Secure.getString(context.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
    return current?.startsWith(context.packageName) == true
}

/**
 * The app's home screen. The real product is the system keyboard
 * (KeyboardService); this screen's only job is walking the user through
 * enabling + selecting it, previewing it, and - optionally - linking a PC.
 */
@Composable
fun HomeScreen(
    onOpenPreview: () -> Unit,
    onOpenConnection: () -> Unit
) {
    val context = LocalContext.current
    var enabled by remember { mutableStateOf(isImeEnabled(context)) }
    var selected by remember { mutableStateOf(isImeSelected(context)) }

    // Re-check status whenever the user comes back from Settings/the picker.
    androidx.compose.runtime.LaunchedEffect(Unit) {
        enabled = isImeEnabled(context)
        selected = isImeSelected(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MkBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Mechanical Keyboard Pro",
            color = MkTextPrimary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "A full mechanical-style keyboard for your phone. Enable it once, " +
                "then use it everywhere - browser, chat apps, coding tools, Termux, games.",
            color = MkTextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )

        StatusCard(enabled = enabled, selected = selected)

        if (!enabled) {
            Button(
                onClick = {
                    context.startActivity(
                        android.content.Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("1. Enable in Settings") }
        }

        if (enabled && !selected) {
            Button(
                onClick = {
                    val imm = context.getSystemService(InputMethodManager::class.java)
                    imm.showInputMethodPicker()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("2. Choose as active keyboard") }
        }

        OutlinedButton(onClick = onOpenPreview, modifier = Modifier.fillMaxWidth()) {
            Text("Preview the keyboard")
        }

        OutlinedButton(onClick = onOpenConnection, modifier = Modifier.fillMaxWidth()) {
            Text("Optional: connect to a PC")
        }
    }
}

@Composable
private fun StatusCard(enabled: Boolean, selected: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MkSurface, RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = when {
                selected -> "✓ Active - you're typing with Mechanical Keyboard Pro"
                enabled -> "✓ Enabled - now select it as your active keyboard"
                else -> "Not enabled yet"
            },
            color = if (selected) MkAccentGreen else if (enabled) MkAccentPurple else MkTextSecondary,
            style = MaterialTheme.typography.titleSmall
        )
    }
}
