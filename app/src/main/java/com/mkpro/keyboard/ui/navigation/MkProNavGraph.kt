package com.mkpro.keyboard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mkpro.keyboard.ui.screens.connection.ConnectionScreen
import com.mkpro.keyboard.ui.screens.home.HomeScreen
import com.mkpro.keyboard.ui.screens.keyboard.KeyboardScreen
import com.mkpro.keyboard.ui.screens.splash.SplashScreen

/**
 * Splash -> Home is the real flow now: Home is where users enable/select the
 * IME. Connection (the PC-link feature) and the Keyboard preview are both
 * reached from Home as optional/secondary destinations, not the main path.
 */
@Composable
fun MkProNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(Screen.Splash.route) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onOpenPreview = { navController.navigate(Screen.Keyboard.route) },
                onOpenConnection = { navController.navigate(Screen.Connection.route) }
            )
        }

        composable(Screen.Connection.route) {
            ConnectionScreen(
                onDeviceConnected = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Connection.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Keyboard.route) {
            KeyboardScreen()
        }
    }
}
