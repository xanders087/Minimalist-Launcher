package com.example.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Paleta de colores oficial del sistema de diseño 'Aetheric Minimalist' (Stitch Design System).
 * Diseñada para máxima eficiencia de batería en pantallas OLED, alto contraste de legibilidad
 * suizo-arquitectónico y micro-acentos cromáticos táctiles.
 */

// =========================================================================
// 1. Canvas Foundation & Background OLED
// =========================================================================
val AethericOledBlack = Color(0xFF000000)          // Auténtico negro puro OLED (0% consumo de batería)
val AethericMatteNight = Color(0xFF0C0C0E)         // Base ground mate noche (Level 0)
val AethericSurfaceDark = Color(0xFF131315)        // Superficie base (Stitch surface / background)

// =========================================================================
// 2. Estratificación Tonal de Superficies (Elevation & Depth)
// =========================================================================
val AethericSurfaceLowest = Color(0xFF0E0E10)      // Contenedor de nivel más bajo
val AethericSurfaceLow = Color(0xFF1B1B1D)         // Contenedor nivel bajo (#101010)
val AethericSurfaceContainer = Color(0xFF201F21)   // Level 1: Cards interactivas (#0D0D0D / #141414)
val AethericSurfaceHigh = Color(0xFF2A2A2C)        // Level 2: Modales y drawer de apps (#181818)
val AethericSurfaceHighest = Color(0xFF353437)     // Level 3: Estados de foco / press (#1E1E1E)
val AethericSurfaceDim = Color(0xFF131315)         // Superficie atenuada
val AethericSurfaceBright = Color(0xFF39393B)      // Superficie iluminada

// Líneas estructurales y bordes hairline (1px razor-thin divider)
val AethericOutline = Color(0xFF262626)            // Borde hairline sutil para delimitar tarjetas
val AethericOutlineVariant = Color(0xFF444749)     // Borde secundario visible
val AethericOutlineMedium = Color(0xFF8E9193)

// =========================================================================
// 3. Primario Verde (Forest Sage)
// =========================================================================
val AethericForestSage = Color(0xFF386B1F)         // Tinte primario: indicadores, streaks, foco, checks
val AethericForestSageContainer = Color(0xFF225408)// Contenedor verde profundo
val AethericForestSageLight = Color(0xFF9CD67D)    // Tinte verde claro para contraste en modo noche
val AethericForestSageFixed = Color(0xFFB7F396)    // Resalte verde fijo
val AethericForestSageOnContainer = Color(0xFF8EC870)
val AethericForestSageOnPrimary = Color(0xFF113800)

// =========================================================================
// 4. Acento Ámbar (Solar Dawn Amber)
// =========================================================================
val AethericSolarAmber = Color(0xFFFFD54F)         // Insignias circadianas, transición amanecer/atardecer
val AethericSolarAmberContainer = Color(0xFFFFE087)
val AethericSolarAmberDim = Color(0xFFEBC23E)
val AethericSolarAmberOnContainer = Color(0xFF7A6100)

// =========================================================================
// 5. Escala de Grises Tipográfica (Text & Foreground)
// =========================================================================
val AethericTextOffWhite = Color(0xFFF4F4F5)       // Alto contraste: Reloj principal, títulos hero y selección
val AethericTextPureWhite = Color(0xFFFFFFFF)      // Blanco puro para glifos e iconos destacados
val AethericTextStone = Color(0xFFA1A1AA)          // Gris piedra: etiquetas secundarias, metadatos, prefijos
val AethericTextPebble = Color(0xFF71717A)         // Gris guijarro: pistas inactivas, iconos de búsqueda, guías
val AethericTextSubtle = Color(0xFF444749)         // Elementos desactivados o marcas menores
val AethericTextInverse = Color(0xFF1A1C1D)

// =========================================================================
// 6. Compatibilidad y Aliases de Material Theme
// =========================================================================
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650A4)
val PurpleGrey40 = Color(0xFF625B71)
val Pink40 = Color(0xFF7D5260)
