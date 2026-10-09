package com.example.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Paleta de colores oficial del sistema de diseño 'Warm Minimalist' (Stitch Design System).
 * Incluye todos los tokens para Light Mode y Dark Mode.
 */

// =========================================================================
// LIGHT MODE (Warm Minimalist)
// =========================================================================
val LightSurface = Color(0xFFFBF9F6)
val LightSurfaceDim = Color(0xFFDBDAD7)
val LightSurfaceBright = Color(0xFFFBF9F6)
val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
val LightSurfaceContainerLow = Color(0xFFF5F3F0)
val LightSurfaceContainer = Color(0xFFEFEEEB)
val LightSurfaceContainerHigh = Color(0xFFEAE8E5)
val LightSurfaceContainerHighest = Color(0xFFE4E2DF)

val LightOnSurface = Color(0xFF1B1C1A)
val LightOnSurfaceVariant = Color(0xFF4A4640)
val LightInverseSurface = Color(0xFF30312F)
val LightInverseOnSurface = Color(0xFFF2F0ED)

val LightOutline = Color(0xFF7B766F)
val LightOutlineVariant = Color(0xFFCCC6BD)
val LightSurfaceTint = Color(0xFF605E5C)

val LightPrimary = Color(0xFF050504)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFF1F1E1D)
val LightOnPrimaryContainer = Color(0xFF888584)
val LightInversePrimary = Color(0xFFCAC6C4)

val LightSecondary = Color(0xFF99462A)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFFE9572)
val LightOnSecondaryContainer = Color(0xFF762C12)

val LightTertiary = Color(0xFF070402)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFF231D17)
val LightOnTertiaryContainer = Color(0xFF8E847C)

val LightError = Color(0xFFBA1A1A)
val LightOnError = Color(0xFFFFFFFF)
val LightErrorContainer = Color(0xFFFFDAD6)
val LightOnErrorContainer = Color(0xFF93000A)

val LightBackground = Color(0xFFFBF9F6)
val LightOnBackground = Color(0xFF1B1C1A)


// =========================================================================
// DARK MODE (Warm Minimalist)
// =========================================================================
val DarkSurface = Color(0xFF121414)
val DarkSurfaceDim = Color(0xFF121414)
val DarkSurfaceBright = Color(0xFF393939)
val DarkSurfaceContainerLowest = Color(0xFF0D0E0F)
val DarkSurfaceContainerLow = Color(0xFF1B1C1C)
val DarkSurfaceContainer = Color(0xFF1F2020)
val DarkSurfaceContainerHigh = Color(0xFF292A2A)
val DarkSurfaceContainerHighest = Color(0xFF343535)

val DarkOnSurface = Color(0xFFE3E2E2)
val DarkOnSurfaceVariant = Color(0xFFC4C7C7)
val DarkInverseSurface = Color(0xFFE3E2E2)
val DarkInverseOnSurface = Color(0xFF303031)

val DarkOutline = Color(0xFF8E9192)
val DarkOutlineVariant = Color(0xFF444748)
val DarkSurfaceTint = Color(0xFFC7C6C6)

val DarkPrimary = Color(0xFFC7C6C6)
val DarkOnPrimary = Color(0xFF303031)
val DarkPrimaryContainer = Color(0xFF919090)
val DarkOnPrimaryContainer = Color(0xFF292A2A)
val DarkInversePrimary = Color(0xFF5E5E5E)

val DarkSecondary = Color(0xFFC7C6C6)
val DarkOnSecondary = Color(0xFF303031)
val DarkSecondaryContainer = Color(0xFF464747)
val DarkOnSecondaryContainer = Color(0xFFB6B5B5)

val DarkTertiary = Color(0xFFC7C6C6)
val DarkOnTertiary = Color(0xFF303031)
val DarkTertiaryContainer = Color(0xFF919090)
val DarkOnTertiaryContainer = Color(0xFF292A2A)

val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)
val DarkErrorContainer = Color(0xFF93000A)
val DarkOnErrorContainer = Color(0xFFFFDAD6)

val DarkBackground = Color(0xFF121414)
val DarkOnBackground = Color(0xFFE3E2E2)

// =========================================================================
// Legacy / General Colors
// =========================================================================
val Transparent = Color(0x00000000)
val Black = Color(0xFF000000)
val White = Color(0xFFFFFFFF)

// Fallbacks to avoid breaking other screens
val AethericOledBlack = DarkBackground
val AethericMatteNight = DarkBackground
val AethericSurfaceDark = DarkSurface
val AethericSurfaceLowest = DarkSurfaceContainerLowest
val AethericSurfaceLow = DarkSurfaceContainerLow
val AethericSurfaceContainer = DarkSurfaceContainer
val AethericSurfaceHigh = DarkSurfaceContainerHigh
val AethericSurfaceHighest = DarkSurfaceContainerHighest
val AethericSurfaceDim = DarkSurfaceDim
val AethericSurfaceBright = DarkSurfaceBright
val AethericOutline = DarkOutline
val AethericOutlineVariant = DarkOutlineVariant
val AethericOutlineMedium = DarkOutlineVariant
val AethericForestSage = DarkPrimary
val AethericForestSageContainer = DarkPrimaryContainer
val AethericForestSageLight = DarkPrimary
val AethericForestSageFixed = DarkPrimary
val AethericForestSageOnContainer = DarkOnPrimaryContainer
val AethericForestSageOnPrimary = DarkOnPrimary
val AethericSolarAmber = DarkPrimary
val AethericSolarAmberContainer = DarkPrimaryContainer
val AethericSolarAmberDim = DarkPrimaryContainer
val AethericSolarAmberOnContainer = DarkOnPrimaryContainer
val AethericTextOffWhite = DarkOnSurface
val AethericTextPureWhite = DarkOnSurface
val AethericTextStone = DarkOnSurfaceVariant
val AethericTextMutedZinc = DarkOutline
val AethericTextPebble = DarkOutlineVariant
val AethericTextSubtle = DarkOutlineVariant
val AethericTextInverse = DarkInverseOnSurface

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650A4)
val PurpleGrey40 = Color(0xFF625B71)
val Pink40 = Color(0xFF7D5260)
