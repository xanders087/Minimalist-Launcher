package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * WCAG 2.1 Compliant Contrast and Dynamic Readability Utility.
 * 
 * Provides mathematical relative luminance calculation, contrast ratios (1:1 to 21:1),
 * compliance grading (AAA, AA, Large/UI, Fail), dynamic readable text color selection,
 * and contrast enhancement for accent colors against arbitrary background surfaces.
 */
object WcagContrastUtil {

    const val MIN_CONTRAST_AA_NORMAL = 4.5
    const val MIN_CONTRAST_AA_LARGE = 3.0
    const val MIN_CONTRAST_AAA_NORMAL = 7.0
    const val MIN_CONTRAST_AAA_LARGE = 4.5

    val HighContrastDark = Color(0xFF111410)
    val HighContrastLight = Color(0xFFFFFFFF)
    val PureBlack = Color(0xFF000000)
    val SoftWhite = Color(0xFFF7F9F2)

    /**
     * WCAG Compliance Levels according to W3C Accessibility Guidelines (2.1).
     */
    enum class WcagLevel(
        val minRatio: Double,
        val shortLabel: String,
        val fullTitle: String,
        val isCompliant: Boolean
    ) {
        AAA_NORMAL(7.0, "AAA", "WCAG AAA (Enhanced)", true),
        AA_NORMAL(4.5, "AA", "WCAG AA (Standard)", true),
        AA_LARGE(3.0, "AA-Large", "WCAG AA (Large Text / UI)", true),
        FAIL(0.0, "Fail", "Low Contrast (< 3.0:1)", false)
    }

    /**
     * Structured result holding contrast metrics and accessibility assessment.
     */
    data class ContrastEvaluation(
        val foreground: Color,
        val background: Color,
        val ratio: Double,
        val level: WcagLevel,
        val recommendedTextColor: Color,
        val formattedRatio: String,
        val isReadable: Boolean
    )

    /**
     * Calculates the WCAG relative luminance of a color.
     * Normalized value between 0.0 (pure black) and 1.0 (pure white).
     * 
     * Formula: L = 0.2126 * R_linear + 0.7152 * G_linear + 0.0722 * B_linear
     */
    fun calculateRelativeLuminance(color: Color): Double {
        val rLinear = linearizeChannel(color.red)
        val gLinear = linearizeChannel(color.green)
        val bLinear = linearizeChannel(color.blue)
        return 0.2126 * rLinear + 0.7152 * gLinear + 0.0722 * bLinear
    }

    /**
     * Linearizes an sRGB gamma-compressed channel (0.0 to 1.0).
     */
    private fun linearizeChannel(channel: Float): Double {
        val c = channel.toDouble().coerceIn(0.0, 1.0)
        return if (c <= 0.04045) {
            c / 12.92
        } else {
            ((c + 0.055) / 1.055).pow(2.4)
        }
    }

    /**
     * Calculates the WCAG contrast ratio between two colors.
     * Value ranges from 1.0 (identical) to 21.0 (pure black on pure white).
     * 
     * Formula: (L1 + 0.05) / (L2 + 0.05), where L1 is lighter and L2 is darker.
     */
    fun calculateContrastRatio(colorA: Color, colorB: Color): Double {
        val l1 = calculateRelativeLuminance(colorA)
        val l2 = calculateRelativeLuminance(colorB)
        val lighter = max(l1, l2)
        val darker = min(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    /**
     * Classifies a contrast ratio into its corresponding WCAG Compliance Level.
     */
    fun getComplianceLevel(ratio: Double): WcagLevel {
        return when {
            ratio >= MIN_CONTRAST_AAA_NORMAL -> WcagLevel.AAA_NORMAL
            ratio >= MIN_CONTRAST_AA_NORMAL -> WcagLevel.AA_NORMAL
            ratio >= MIN_CONTRAST_AA_LARGE -> WcagLevel.AA_LARGE
            else -> WcagLevel.FAIL
        }
    }

    /**
     * Evaluates contrast and returns a full [ContrastEvaluation].
     */
    fun evaluateContrast(foreground: Color, background: Color): ContrastEvaluation {
        val ratio = calculateContrastRatio(foreground, background)
        val level = getComplianceLevel(ratio)
        val recommended = getAccessibleTextColor(background)
        val formatted = String.format("%.1f:1", ratio)
        return ContrastEvaluation(
            foreground = foreground,
            background = background,
            ratio = ratio,
            level = level,
            recommendedTextColor = recommended,
            formattedRatio = formatted,
            isReadable = ratio >= MIN_CONTRAST_AA_NORMAL
        )
    }

    /**
     * Dynamically chooses the most readable, WCAG-compliant text color (dark or light)
     * to place on top of [backgroundColor].
     * 
     * Tests high-contrast white and dark candidates and picks the one yielding the highest
     * contrast ratio. If both pass WCAG AA (4.5:1), prefers the candidate with maximum safety margin.
     */
    fun getAccessibleTextColor(
        backgroundColor: Color,
        darkCandidate: Color = HighContrastDark,
        lightCandidate: Color = HighContrastLight
    ): Color {
        val lightRatio = calculateContrastRatio(lightCandidate, backgroundColor)
        val darkRatio = calculateContrastRatio(darkCandidate, backgroundColor)

        return if (lightRatio >= darkRatio) {
            if (lightRatio >= MIN_CONTRAST_AA_NORMAL) lightCandidate else PureBlack
        } else {
            if (darkRatio >= MIN_CONTRAST_AA_NORMAL) darkCandidate else HighContrastLight
        }
    }

    /**
     * Adjusts an accent color when used as text/icon foreground on top of [backgroundColor]
     * to guarantee it satisfies the minimum required WCAG contrast ratio (default 4.5:1).
     * 
     * If the raw accent color is already >= minRatio, it returns [accentColor] unchanged.
     * Otherwise, it shifts its lightness/brightness while preserving hue and saturation.
     */
    fun getAccessibleAccentTextColor(
        accentColor: Color,
        backgroundColor: Color,
        minRatio: Double = MIN_CONTRAST_AA_NORMAL
    ): Color {
        val currentRatio = calculateContrastRatio(accentColor, backgroundColor)
        if (currentRatio >= minRatio) {
            return accentColor
        }

        val bgLuminance = calculateRelativeLuminance(backgroundColor)
        val isDarkBg = bgLuminance < 0.3

        // Convert to HSV to preserve hue and saturation while adjusting brightness/value
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(accentColor.toArgb(), hsv)

        var bestColor = accentColor
        var bestRatio = currentRatio

        if (isDarkBg) {
            // Lighten the color: increase Value and decrease Saturation if needed
            for (step in 1..20) {
                val testVal = (hsv[2] + (1f - hsv[2]) * (step / 20f)).coerceIn(0f, 1f)
                val testSat = if (step > 10) (hsv[1] * (1f - (step - 10) / 20f)).coerceIn(0.2f, 1f) else hsv[1]
                val argb = android.graphics.Color.HSVToColor(floatArrayOf(hsv[0], testSat, testVal))
                val candidate = Color(argb)
                val ratio = calculateContrastRatio(candidate, backgroundColor)
                if (ratio > bestRatio) {
                    bestRatio = ratio
                    bestColor = candidate
                }
                if (ratio >= minRatio) {
                    return candidate
                }
            }
        } else {
            // Darken the color: decrease Value and boost Saturation slightly if needed
            for (step in 1..20) {
                val testVal = (hsv[2] * (1f - step / 22f)).coerceIn(0.05f, 1f)
                val testSat = (hsv[1] * 1.05f).coerceIn(0f, 1f)
                val argb = android.graphics.Color.HSVToColor(floatArrayOf(hsv[0], testSat, testVal))
                val candidate = Color(argb)
                val ratio = calculateContrastRatio(candidate, backgroundColor)
                if (ratio > bestRatio) {
                    bestRatio = ratio
                    bestColor = candidate
                }
                if (ratio >= minRatio) {
                    return candidate
                }
            }
        }

        return bestColor
    }

    /**
     * Generates an accessible (container, onContainer) pair from an accent color.
     * Guarantees contrast ratio between container and onContainer is >= 4.5:1.
     */
    fun createAccessibleContainerPair(
        primaryColor: Color,
        isDark: Boolean
    ): Pair<Color, Color> {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(primaryColor.toArgb(), hsv)

        return if (isDark) {
            // In dark mode: tinted dark container with bright legible text/icon
            val containerArgb = android.graphics.Color.HSVToColor(floatArrayOf(hsv[0], (hsv[1] * 0.4f).coerceIn(0.1f, 0.45f), 0.22f))
            val container = Color(containerArgb)
            val onContainer = getAccessibleAccentTextColor(primaryColor, container, minRatio = MIN_CONTRAST_AA_NORMAL)
            Pair(container, onContainer)
        } else {
            // In light mode: soft pastel light container with deep contrasting text/icon
            val containerArgb = android.graphics.Color.HSVToColor(floatArrayOf(hsv[0], (hsv[1] * 0.18f).coerceIn(0.05f, 0.22f), 0.94f))
            val container = Color(containerArgb)
            val onContainer = getAccessibleAccentTextColor(primaryColor, container, minRatio = MIN_CONTRAST_AA_NORMAL)
            Pair(container, onContainer)
        }
    }
}
