package com.example.ui.wallpaper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector

enum class WallpaperMode(val title: String, val subtitle: String) {
    SOLID("Color Minimalista", "Fondo plano que acompaña el modo oscuro/claro"),
    STATIC("Imagen Estática", "Una sola foto fija de tu galería o del catálogo"),
    DAILY("Cambio Diario Automático", "Renovación diaria según tu galería o preferencias")
}

enum class DailySource(val title: String, val subtitle: String) {
    GALLERY("Mi Galería", "Rotar fotos seleccionadas de tu dispositivo"),
    PREFERENCES("Mis Preferencias", "Imágenes aleatorias según tus temas favoritos")
}

enum class WallpaperCategory(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String
) {
    NATURE("nature", "Naturaleza", "Montañas, bosques, océanos y lagos", "Landscape"),
    ART("art", "Arte y Diseño", "Arquitectura moderna, pinturas y escultura", "Brush"),
    SPACE("space", "Espacio y Cosmos", "Galaxias, estrellas y auroras boreales", "RocketLaunch"),
    MINIMAL("minimal", "Minimalismo y Texturas", "Gradientes suaves, geometría y sombras", "AutoAwesome"),
    URBAN("urban", "Ciudades y Noche", "Rascacielos, horizontes y luces de neón", "LocationCity");

    companion object {
        fun fromId(id: String): WallpaperCategory? = entries.find { it.id == id }
    }
}

data class CuratedWallpaper(
    val id: String,
    val url: String,
    val category: WallpaperCategory,
    val title: String,
    val author: String,
    val baseLuminance: Float = 0.35f
)

data class WallpaperConfig(
    val mode: WallpaperMode = WallpaperMode.SOLID,
    val dailySource: DailySource = DailySource.PREFERENCES,
    val selectedCategories: Set<WallpaperCategory> = setOf(
        WallpaperCategory.NATURE,
        WallpaperCategory.ART,
        WallpaperCategory.SPACE
    ),
    val currentWallpaperPath: String? = null,
    val staticWallpaperPath: String? = null,
    val isStaticFromGallery: Boolean = false,
    val galleryPhotos: List<String> = emptyList(), // Local file paths in app files dir
    val dimmingAlpha: Float = 0.40f, // Scrim opacity for reading comfort (0.15f - 0.75f)
    val lastDailyDateKey: String = "", // e.g., "2026-09-06"
    val currentTitle: String = "Color plano",
    val currentSubtitle: String = "Minimalista",
    // Automatic Text Style & Contrast Adaptation
    val autoAdjustTextColor: Boolean = true,
    val imageLuminance: Float = 0.30f,
    val effectiveLuminance: Float = 0.18f,
    val isDarkBackground: Boolean = true,
    val textShadowEnabled: Boolean = true
) {
    /**
     * Resolves the primary text color:
     * When a wallpaper is active, automatically adapts to crisp light or deep dark
     * depending on the wallpaper's measured brightness and scrim level.
     */
    fun resolvePrimaryTextColor(fallback: Color): Color {
        if (mode == WallpaperMode.SOLID || !autoAdjustTextColor) return fallback
        return if (isDarkBackground) Color(0xFFFFFFFF) else Color(0xFF101210)
    }

    /**
     * Resolves secondary/subtitle text color.
     */
    fun resolveSecondaryTextColor(fallback: Color): Color {
        if (mode == WallpaperMode.SOLID || !autoAdjustTextColor) return fallback
        return if (isDarkBackground) Color(0xFFE2E4DE) else Color(0xFF323630)
    }

    /**
     * Resolves muted/caption text color.
     */
    fun resolveMutedTextColor(fallback: Color): Color {
        if (mode == WallpaperMode.SOLID || !autoAdjustTextColor) return fallback
        return if (isDarkBackground) Color(0xFFB5BBB0) else Color(0xFF555B50)
    }

    /**
     * Resolves surface/card background for translucent legibility pills.
     */
    fun resolveCardBackground(fallback: Color): Color {
        if (mode == WallpaperMode.SOLID || !autoAdjustTextColor) return fallback
        return if (isDarkBackground) Color(0xFF000000).copy(alpha = 0.40f) else Color(0xFFFFFFFF).copy(alpha = 0.70f)
    }

    /**
     * Resolves subtle border for legibility.
     */
    fun resolveCardBorder(fallback: Color): Color {
        if (mode == WallpaperMode.SOLID || !autoAdjustTextColor) return fallback
        return if (isDarkBackground) Color(0xFFFFFFFF).copy(alpha = 0.18f) else Color(0xFF000000).copy(alpha = 0.12f)
    }

    /**
     * Drop shadow ensuring 100% legibility against complex photographic textures.
     */
    fun resolveTextShadow(): Shadow? {
        if (mode == WallpaperMode.SOLID || !textShadowEnabled) return null
        return if (isDarkBackground) {
            Shadow(
                color = Color(0xCC000000),
                offset = Offset(1.5f, 1.5f),
                blurRadius = 6f
            )
        } else {
            Shadow(
                color = Color(0x80FFFFFF),
                offset = Offset(1f, 1f),
                blurRadius = 5f
            )
        }
    }
}
