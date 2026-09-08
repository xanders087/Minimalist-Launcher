package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

data class AccentTheme(
    val id: String = "forest_sage",
    val name: String = "Forest Sage",
    val primaryColorLong: Long = 0xFF386B1F,
    val containerColorLong: Long = 0xFFD8E7CC,
    val onContainerColorLong: Long = 0xFF153304
) {
    val primary: Color get() = Color(primaryColorLong)
    val container: Color get() = Color(containerColorLong)
    val onContainer: Color get() = Color(onContainerColorLong)

    /**
     * Dynamically computed WCAG-compliant text color (dark or light)
     * guaranteeing high contrast (>= 4.5:1 AA, up to 21:1 AAA) on top of [primary].
     */
    val onPrimary: Color get() = WcagContrastUtil.getAccessibleTextColor(primary)

    companion object {
        val ForestSage = AccentTheme(
            id = "forest_sage",
            name = "Forest Sage",
            primaryColorLong = 0xFF386B1F,
            containerColorLong = 0xFFD8E7CC,
            onContainerColorLong = 0xFF153304
        )

        val DeepIndigo = AccentTheme(
            id = "deep_indigo",
            name = "Deep Indigo",
            primaryColorLong = 0xFF4338CA,
            containerColorLong = 0xFFE0E7FF,
            onContainerColorLong = 0xFF1E1B4B
        )

        val NordicTeal = AccentTheme(
            id = "nordic_teal",
            name = "Nordic Teal",
            primaryColorLong = 0xFF0E7490,
            containerColorLong = 0xFFCCFBF1,
            onContainerColorLong = 0xFF134E4A
        )

        val WarmTerracotta = AccentTheme(
            id = "warm_terracotta",
            name = "Terracotta",
            primaryColorLong = 0xFFC2410C,
            containerColorLong = 0xFFFFEDD5,
            onContainerColorLong = 0xFF7C2D12
        )

        val CrimsonRose = AccentTheme(
            id = "crimson_rose",
            name = "Crimson Rose",
            primaryColorLong = 0xFFBE123C,
            containerColorLong = 0xFFFFE4E6,
            onContainerColorLong = 0xFF881337
        )

        val PlumAmethyst = AccentTheme(
            id = "plum_amethyst",
            name = "Plum Amethyst",
            primaryColorLong = 0xFF7E22CE,
            containerColorLong = 0xFFF3E8FF,
            onContainerColorLong = 0xFF581C87
        )

        val MinimalCharcoal = AccentTheme(
            id = "minimal_charcoal",
            name = "Charcoal",
            primaryColorLong = 0xFF27272A,
            containerColorLong = 0xFFE4E4E7,
            onContainerColorLong = 0xFF09090B
        )

        val OliveGold = AccentTheme(
            id = "olive_gold",
            name = "Olive Gold",
            primaryColorLong = 0xFF65A30D,
            containerColorLong = 0xFFECFCCB,
            onContainerColorLong = 0xFF365314
        )

        val Presets = listOf(
            ForestSage,
            DeepIndigo,
            NordicTeal,
            WarmTerracotta,
            CrimsonRose,
            PlumAmethyst,
            MinimalCharcoal,
            OliveGold
        )

        fun fromColor(primaryColor: Color, name: String = "Custom"): AccentTheme {
            val argb = primaryColor.toArgb().toLong() and 0xFFFFFFFFL
            val (container, onContainer) = WcagContrastUtil.createAccessibleContainerPair(primaryColor, isDark = false)
            val containerLong = container.toArgb().toLong() and 0xFFFFFFFFL
            val onContainerLong = onContainer.toArgb().toLong() and 0xFFFFFFFFL
            
            return AccentTheme(
                id = "custom_${argb}",
                name = name,
                primaryColorLong = argb,
                containerColorLong = containerLong,
                onContainerColorLong = onContainerLong
            )
        }
    }
}
