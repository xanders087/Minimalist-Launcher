package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * Escala tipográfica oficial del sistema de diseño 'Warm Minimalist' (Stitch Design System).
 */

val PlusJakartaSansFontFamily = FontFamily.SansSerif // Fallback till actual font is added

object WarmMinimalistTypography {
    val displayClock = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontSize = 64.sp,
        fontWeight = FontWeight.Light,
        lineHeight = 68.sp,
        letterSpacing = (-0.03).em
    )
    val displayClockMobile = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontSize = 48.sp,
        fontWeight = FontWeight.Light,
        lineHeight = 52.sp,
        letterSpacing = (-0.03).em
    )
    val headlineLg = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontSize = 32.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 38.sp,
        letterSpacing = (-0.02).em
    )
    val headlineLgMobile = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontSize = 26.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 32.sp,
        letterSpacing = (-0.02).em
    )
    val headlineMd = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontSize = 22.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 28.sp,
        letterSpacing = (-0.01).em
    )
    val headlineSm = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 24.sp,
        letterSpacing = 0.em
    )
    val bodyLg = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp,
        letterSpacing = 0.01.em
    )
    val bodyMd = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
        letterSpacing = 0.01.em
    )
    val bodySm = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp,
        letterSpacing = 0.02.em
    )
    val labelLg = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 18.sp,
        letterSpacing = 0.04.em
    )
    val labelMd = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp,
        letterSpacing = 0.05.em
    )
    val labelSm = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 14.sp,
        letterSpacing = 0.08.em
    )

    // Legacy Aliases
    val captionCaps = labelLg
    val microHint = labelSm
    val titleMd = headlineSm
    val drawerRow = bodyLg
    val bodyRegular = bodyMd
}

object AethericTypography {
    val clockDisplay = WarmMinimalistTypography.displayClock
    val clockDisplayMobile = WarmMinimalistTypography.displayClockMobile
    val headlineHero = WarmMinimalistTypography.headlineLg
    val headlineHeroMobile = WarmMinimalistTypography.headlineLgMobile
    val statMono = WarmMinimalistTypography.headlineMd
    val titleMd = WarmMinimalistTypography.headlineSm
    val indexMono = WarmMinimalistTypography.bodyLg
    val drawerRow = WarmMinimalistTypography.bodyMd
    val bodyRegular = WarmMinimalistTypography.bodySm
    val captionCaps = WarmMinimalistTypography.labelLg
    val microHint = WarmMinimalistTypography.labelSm
}

val InterFontFamily = PlusJakartaSansFontFamily
val JetBrainsMonoFontFamily = PlusJakartaSansFontFamily

/**
 * Configuración Material 3 mapeada a las escalas del sistema de diseño Warm Minimalist.
 */
val Typography = Typography(
    displayLarge = WarmMinimalistTypography.displayClock,
    displayMedium = WarmMinimalistTypography.displayClockMobile,
    headlineLarge = WarmMinimalistTypography.headlineLg,
    headlineMedium = WarmMinimalistTypography.headlineLgMobile,
    headlineSmall = WarmMinimalistTypography.headlineSm,
    titleLarge = WarmMinimalistTypography.headlineMd,
    titleMedium = WarmMinimalistTypography.bodyLg,
    titleSmall = WarmMinimalistTypography.bodyMd,
    bodyLarge = WarmMinimalistTypography.bodyLg,
    bodyMedium = WarmMinimalistTypography.bodyMd,
    bodySmall = WarmMinimalistTypography.bodySm,
    labelLarge = WarmMinimalistTypography.labelLg,
    labelMedium = WarmMinimalistTypography.labelMd,
    labelSmall = WarmMinimalistTypography.labelSm
)
