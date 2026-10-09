package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Warm Minimalist Launcher Color Scheme
 * Fidelidad exacta a las especificaciones de Stitch para Light Mode (Warm Minimalist) y Dark Mode.
 */
data class LauncherColorScheme(
    val isDark: Boolean,
    val isPureBlack: Boolean = false,
    val isWarmEyeComfort: Boolean = false,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val surfaceElevated: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val divider: Color,
    val iconTint: Color,
    val accentPrimary: Color,
    val accentOnPrimary: Color,
    val accentTextOnSurface: Color,
    val accentContainer: Color,
    val accentOnContainer: Color,
    val chipBackground: Color,
    val searchBarBackground: Color,
    val dangerRed: Color = Color(0xFFBA1A1A)
) {
    companion object {
        fun create(
            isDark: Boolean,
            accent: AccentTheme,
            isPureBlack: Boolean = false,
            isWarmEyeComfort: Boolean = false
        ): LauncherColorScheme {
            return if (isDark) {
                val bg = if (isPureBlack) Color(0xFF0A0A09) else DarkBackground
                val cardBg = if (isPureBlack) Color(0xFF161514) else DarkSurfaceContainer

                // Dark Mode (Stitch Exact: #E3E2E2 primary text, #C4C7C7 secondary text, #9E968D muted text, #C7C6C6 primary accent)
                LauncherColorScheme(
                    isDark = true,
                    isPureBlack = isPureBlack,
                    isWarmEyeComfort = isWarmEyeComfort,
                    background = bg,
                    surface = bg,
                    surfaceVariant = DarkSurfaceContainerHigh,
                    surfaceElevated = DarkSurfaceContainerHighest,
                    cardBackground = cardBg,
                    cardBorder = if (isPureBlack) Color(0xFF262626) else DarkOutlineVariant,
                    textPrimary = Color(0xFFE3E2E2), // Stitch Dark Mode Primary Text
                    textSecondary = Color(0xFFC4C7C7), // Stitch Dark Mode Secondary Text
                    textMuted = Color(0xFF9E968D), // Stitch Dark Mode Muted Text (ink-muted)
                    divider = DarkOutlineVariant,
                    iconTint = Color(0xFFC7C6C6),
                    accentPrimary = Color(0xFFC7C6C6), // Stitch Dark Mode Primary Accent
                    accentOnPrimary = Color(0xFF303031),
                    accentTextOnSurface = Color(0xFFC7C6C6),
                    accentContainer = DarkPrimaryContainer,
                    accentOnContainer = DarkOnPrimaryContainer,
                    chipBackground = DarkSurfaceContainerHigh,
                    searchBarBackground = cardBg
                )
            } else {
                // Light Mode (Stitch Exact: #1B1C1A on-surface, #4A4640 on-surface-variant, #7B766F outline, #050504 primary, #D97757 secondary terracotta)
                LauncherColorScheme(
                    isDark = false,
                    isPureBlack = false,
                    isWarmEyeComfort = false,
                    background = Color(0xFFFBF9F6), // Stitch surface/background #FBF9F6
                    surface = Color(0xFFFBF9F6),
                    surfaceVariant = Color(0xFFE4E2DF), // Stitch surface-variant #E4E2DF
                    surfaceElevated = Color(0xFFEAE8E5), // Stitch surface-container-high #EAE8E5
                    cardBackground = Color(0xFFEFEEEB), // Stitch surface-container #EFEEEB
                    cardBorder = Color(0xFFCCC6BD), // Stitch outline-variant #CCC6BD
                    textPrimary = Color(0xFF1B1C1A), // Stitch on-surface #1B1C1A
                    textSecondary = Color(0xFF4A4640), // Stitch on-surface-variant #4A4640
                    textMuted = Color(0xFF7B766F), // Stitch outline #7B766F
                    divider = Color(0xFFCCC6BD), // Stitch outline-variant #CCC6BD
                    iconTint = Color(0xFF050504), // Stitch primary #050504
                    accentPrimary = Color(0xFF050504), // Stitch primary #050504 (Warm Charcoal Black)
                    accentOnPrimary = Color(0xFFFFFFFF), // Stitch on-primary #FFFFFF
                    accentTextOnSurface = Color(0xFFD97757), // Stitch secondary Terracotta #D97757
                    accentContainer = Color(0xFF1F1E1D), // Stitch primary-container #1F1E1D
                    accentOnContainer = Color(0xFF888584),
                    chipBackground = Color(0xFFE4E2DF), // Stitch surface-variant #E4E2DF
                    searchBarBackground = Color(0xFFEFEEEB) // Stitch surface-container #EFEEEB
                )
            }
        }
    }
}

val LocalLauncherColors = staticCompositionLocalOf {
    LauncherColorScheme.create(
        isDark = false,
        accent = AccentTheme.ForestSage
    )
}

object LauncherTheme {
    val colors: LauncherColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalLauncherColors.current
}
