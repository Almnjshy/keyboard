package com.mkpro.keyboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.mkpro.keyboard.ui.navigation.MkProNavGraph
import com.mkpro.keyboard.ui.theme.MechanicalKeyboardProTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MkProApp()
        }
    }
}

@Composable
private fun MkProApp() {
    MechanicalKeyboardProTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            MkProNavGraph(navController = navController)
        }
    }
}
