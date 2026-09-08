package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFF9DD67D),
    secondary = Color(0xFFBDCBAF),
    tertiary = Color(0xFFA0D0CB),
    background = Color(0xFF131512),
    surface = Color(0xFF1E211D),
    onPrimary = Color(0xFF113800),
    onSecondary = Color(0xFF283420),
    onTertiary = Color(0xFF003734),
    onBackground = Color(0xFFE2E4DE),
    onSurface = Color(0xFFE2E4DE)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFF7F9F2),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1A1C18),
    onSurface = Color(0xFF1A1C18)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  accentTheme: AccentTheme = AccentTheme.ForestSage,
  isPureBlack: Boolean = false,
  isWarmEyeComfort: Boolean = false,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val launcherColors = LauncherColorScheme.create(
    isDark = darkTheme,
    accent = accentTheme,
    isPureBlack = isPureBlack,
    isWarmEyeComfort = isWarmEyeComfort
  )

  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> darkColorScheme(
      primary = accentTheme.primary,
      secondary = launcherColors.accentContainer,
      background = launcherColors.background,
      surface = launcherColors.surface,
      onPrimary = launcherColors.accentOnPrimary,
      onSecondary = launcherColors.accentOnContainer,
      onBackground = launcherColors.textPrimary,
      onSurface = launcherColors.textPrimary
    )
    else -> lightColorScheme(
      primary = accentTheme.primary,
      secondary = launcherColors.accentContainer,
      background = launcherColors.background,
      surface = launcherColors.surface,
      onPrimary = launcherColors.accentOnPrimary,
      onSecondary = launcherColors.accentOnContainer,
      onBackground = launcherColors.textPrimary,
      onSurface = launcherColors.textPrimary
    )
  }

  CompositionLocalProvider(LocalLauncherColors provides launcherColors) {
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
  }
}

