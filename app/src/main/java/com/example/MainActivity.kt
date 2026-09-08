package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.home.HomeScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Habilita renderizado Edge-to-Edge nativo y gestión de WindowInsets
        enableEdgeToEdge()
        setContent {
            val state by viewModel.state.collectAsState()
            MyApplicationTheme(
                darkTheme = state.isDarkThemeActive,
                accentTheme = state.accentTheme,
                isPureBlack = state.isPureBlack,
                isWarmEyeComfort = state.isWarmEyeComfort
            ) {
                HomeScreen(viewModel = viewModel)
            }
        }
    }

    /**
     * Invocado cuando el sistema vuelve al Launcher mediante launchMode="singleTask"
     * o al presionar el botón de inicio (Home) desde otra aplicación.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // En un launcher de sistema, el botón de retroceso no debe cerrar la Activity.
        // La navegación y el repliegue de overlays se gestionan mediante BackHandler en Compose.
    }
}


