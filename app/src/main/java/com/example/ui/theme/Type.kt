package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Escala tipográfica oficial del sistema de diseño 'Aetheric Minimalist' (Stitch Design System).
 *
 * Combina la neutralidad geométrica suiza de [FontFamily.SansSerif] (Inter)
 * con la precisión de instrumentación monospace de [FontFamily.Monospace] (JetBrains Mono).
 */

val InterFontFamily = FontFamily.SansSerif
val JetBrainsMonoFontFamily = FontFamily.Monospace

object AethericTypography {
    /**
     * Reloj de pantalla completa: 80px, bold (700), tracking negativo (-0.05em).
     */
    val clockDisplay = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 80.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 80.sp,
        letterSpacing = (-4.0).sp // -0.05em
    )

    /**
     * Reloj en pantallas móviles compactas: 64px, bold (700), tracking negativo (-0.04em).
     */
    val clockDisplayMobile = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 64.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 64.sp,
        letterSpacing = (-2.56).sp // -0.04em
    )

    /**
     * Títulos principales y nombres de apps en el Home: 30px, bold (700), tracking negativo (-0.025em).
     */
    val headlineHero = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 36.sp,
        letterSpacing = (-0.75).sp // -0.025em
    )

    /**
     * Títulos hero en pantallas móviles reducidas: 26px, bold (700), tracking -0.025em.
     */
    val headlineHeroMobile = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 32.sp,
        letterSpacing = (-0.65).sp
    )

    /**
     * Métricas estadísticas monospaciadas (batería, temperatura, racha de foco): 28px, bold (700).
     */
    val statMono = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 32.sp,
        letterSpacing = (-0.84).sp // -0.03em
    )

    /**
     * Títulos de sección medianos: 18px, medium (500), tracking neutro (0em).
     */
    val titleMd = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    )

    /**
     * Índices numéricos ("01.", "02.") en lista de apps: 18px, bold (700), monospaciado.
     */
    val indexMono = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 24.sp,
        letterSpacing = (-0.36).sp // -0.02em
    )

    /**
     * Fila en el cajón de aplicaciones: 16px, bold (700), tracking -0.02em.
     */
    val drawerRow = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 22.sp,
        letterSpacing = (-0.32).sp
    )

    /**
     * Cuerpo regular de texto y búsqueda: 15px, medium (500).
     */
    val bodyRegular = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    )

    /**
     * Captions en mayúsculas y botones de acción rápida: 11px, bold (700), tracking expandido (+0.14em).
     */
    val captionCaps = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 16.sp,
        letterSpacing = 1.54.sp // +0.14em
    )

    /**
     * Micro-etiquetas, estados astronómicos e indicadores: 10px, bold (700), tracking expandido (+0.2em).
     */
    val microHint = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 14.sp,
        letterSpacing = 2.0.sp // +0.2em
    )
}

/**
 * Configuración Material 3 mapeada a las escalas del sistema de diseño Aetheric Minimalist.
 */
val Typography = Typography(
    displayLarge = AethericTypography.clockDisplay,
    displayMedium = AethericTypography.clockDisplayMobile,
    headlineLarge = AethericTypography.headlineHero,
    headlineMedium = AethericTypography.headlineHeroMobile,
    headlineSmall = AethericTypography.statMono,
    titleLarge = AethericTypography.titleMd,
    titleMedium = AethericTypography.indexMono,
    titleSmall = AethericTypography.drawerRow,
    bodyLarge = AethericTypography.bodyRegular,
    bodyMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = AethericTypography.drawerRow,
    labelMedium = AethericTypography.captionCaps,
    labelSmall = AethericTypography.microHint
)
