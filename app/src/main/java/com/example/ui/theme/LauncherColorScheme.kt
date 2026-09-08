package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Adaptive Color Scheme for Minimalist Launcher supporting Light, Dark,
 * AMOLED Pure Black, and Warm Night Eye Comfort with WCAG 2.1 AA/AAA compliance.
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
            val onPrimary = WcagContrastUtil.getAccessibleTextColor(accent.primary)

            return if (isDark) {
                if (isPureBlack) {
                    val bg = Color(0xFF000000)
                    val surf = Color(0xFF101010)
                    val (container, onContainer) = WcagContrastUtil.createAccessibleContainerPair(accent.primary, isDark = true)
                    val accentText = WcagContrastUtil.getAccessibleAccentTextColor(accent.primary, bg, minRatio = 4.5)

                    // AMOLED Pure Black
                    LauncherColorScheme(
                        isDark = true,
                        isPureBlack = true,
                        isWarmEyeComfort = isWarmEyeComfort,
                        background = bg,
                        surface = surf,
                        surfaceVariant = Color(0xFF181818),
                        surfaceElevated = Color(0xFF202020),
                        cardBackground = Color(0xFF0D0D0D),
                        cardBorder = Color(0xFF262626),
                        textPrimary = if (isWarmEyeComfort) Color(0xFFFFECC8) else Color(0xFFF1F1F1),
                        textSecondary = if (isWarmEyeComfort) Color(0xFFD4C1A5) else Color(0xFFAAAAAA),
                        textMuted = Color(0xFF707070),
                        divider = Color(0xFF262626),
                        iconTint = if (isWarmEyeComfort) Color(0xFFFFECC8) else Color(0xFFE5E5E5),
                        accentPrimary = accent.primary,
                        accentOnPrimary = onPrimary,
                        accentTextOnSurface = accentText,
                        accentContainer = container,
                        accentOnContainer = onContainer,
                        chipBackground = Color(0xFF1E1E1E),
                        searchBarBackground = Color(0xFF141414)
                    )
                } else if (isWarmEyeComfort) {
                    val bg = Color(0xFF151410)
                    val surf = Color(0xFF211E18)
                    val (container, onContainer) = WcagContrastUtil.createAccessibleContainerPair(accent.primary, isDark = true)
                    val accentText = WcagContrastUtil.getAccessibleAccentTextColor(accent.primary, bg, minRatio = 4.5)

                    // Warm Night Comfort (Amber / Low Blue Light)
                    LauncherColorScheme(
                        isDark = true,
                        isPureBlack = false,
                        isWarmEyeComfort = true,
                        background = bg,
                        surface = surf,
                        surfaceVariant = Color(0xFF2C2820),
                        surfaceElevated = Color(0xFF373228),
                        cardBackground = Color(0xFF1F1C16),
                        cardBorder = Color(0xFF3B352A),
                        textPrimary = Color(0xFFFFE8C2),
                        textSecondary = Color(0xFFDCC49E),
                        textMuted = Color(0xFFA19074),
                        divider = Color(0xFF383226),
                        iconTint = Color(0xFFFFE3B0),
                        accentPrimary = accent.primary,
                        accentOnPrimary = onPrimary,
                        accentTextOnSurface = accentText,
                        accentContainer = container,
                        accentOnContainer = onContainer,
                        chipBackground = Color(0xFF2C2820),
                        searchBarBackground = Color(0xFF211E18)
                    )
                } else {
                    val bg = Color(0xFF131512)
                    val surf = Color(0xFF1E211D)
                    val (container, onContainer) = WcagContrastUtil.createAccessibleContainerPair(accent.primary, isDark = true)
                    val accentText = WcagContrastUtil.getAccessibleAccentTextColor(accent.primary, bg, minRatio = 4.5)

                    // Standard Deep Charcoal Dark
                    LauncherColorScheme(
                        isDark = true,
                        isPureBlack = false,
                        isWarmEyeComfort = false,
                        background = bg,
                        surface = surf,
                        surfaceVariant = Color(0xFF282C26),
                        surfaceElevated = Color(0xFF323730),
                        cardBackground = Color(0xFF1B1D19),
                        cardBorder = Color(0xFF30362E),
                        textPrimary = Color(0xFFE2E4DE),
                        textSecondary = Color(0xFFA5A9A0),
                        textMuted = Color(0xFF757A70),
                        divider = Color(0xFF2E332B),
                        iconTint = Color(0xFFE2E4DE),
                        accentPrimary = accent.primary,
                        accentOnPrimary = onPrimary,
                        accentTextOnSurface = accentText,
                        accentContainer = container,
                        accentOnContainer = onContainer,
                        chipBackground = Color(0xFF262A24),
                        searchBarBackground = Color(0xFF1E211D)
                    )
                }
            } else {
                val bg = Color(0xFFF7F9F2)
                val surf = Color(0xFFFFFFFF)
                val (container, onContainer) = WcagContrastUtil.createAccessibleContainerPair(accent.primary, isDark = false)
                val accentText = WcagContrastUtil.getAccessibleAccentTextColor(accent.primary, bg, minRatio = 4.5)

                // High Contrast Light Theme
                LauncherColorScheme(
                    isDark = false,
                    isPureBlack = false,
                    isWarmEyeComfort = false,
                    background = bg,
                    surface = surf,
                    surfaceVariant = Color(0xFFF0F2EB),
                    surfaceElevated = Color(0xFFFFFFFF),
                    cardBackground = Color(0xFFFFFFFF),
                    cardBorder = Color(0xFFE1E4D5),
                    textPrimary = Color(0xFF1A1C18),
                    textSecondary = Color(0xFF43493E),
                    textMuted = Color(0xFF74796D),
                    divider = Color(0xFFE1E4D5),
                    iconTint = Color(0xFF1A1C18),
                    accentPrimary = accent.primary,
                    accentOnPrimary = onPrimary,
                    accentTextOnSurface = accentText,
                    accentContainer = container,
                    accentOnContainer = onContainer,
                    chipBackground = Color(0xFFF0F2EB),
                    searchBarBackground = Color(0xFFF0F2EB)
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
