package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.home.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {
    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Habilita renderizado Edge-to-Edge nativo y gestión de WindowInsets
        enableEdgeToEdge()
        setContent {
            val state by viewModel.state.collectAsState()
            
            // Evaluamos si el sistema está en modo oscuro
            val isSystemDark = isSystemInDarkTheme()
            
            // Decidimos el valor final basado en el estado (ThemeMode.SYSTEM) o el que diga el usuario
            val useDarkTheme = if (state.themeMode == ThemeMode.SYSTEM) {
                isSystemDark
            } else {
                state.isDarkThemeActive
            }
            
            MyApplicationTheme(
                darkTheme = useDarkTheme,
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
}


