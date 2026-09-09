package com.example.ui.theme

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 * Supported Theme Modes for the Launcher
 */
enum class ThemeMode(val title: String, val description: String) {
    AUTO_SUNSET_SUNRISE(
        "Sunset & Sunrise (Auto)",
        "Syncs with local solar times. Dark mode activates at dusk to relieve eye strain and turns light at dawn."
    ),
    SYSTEM(
        "Follow System",
        "Matches Android device system-wide dark/light mode setting."
    ),
    ALWAYS_LIGHT(
        "Always Light",
        "Crisp, high-contrast light theme throughout the day and night."
    ),
    ALWAYS_DARK(
        "Always Dark",
        "Soothing dark theme for maximum contrast reduction and OLED battery savings."
    ),
    CUSTOM_SCHEDULE(
        "Custom Schedule",
        "Automatically turns dark during your chosen hours."
    )
}

/**
 * Variantes tonales de tema para el sistema de diseño Aetheric Minimalist.
 */
enum class AethericThemeVariant(
    val title: String,
    val description: String,
    val hexColor: String,
    val backgroundColor: androidx.compose.ui.graphics.Color,
    val surfaceColor: androidx.compose.ui.graphics.Color
) {
    OLED_PURO("OLED Puro", "Negro absoluto (#000000) • 0% consumo en pantalla", "#000000", androidx.compose.ui.graphics.Color(0xFF000000), androidx.compose.ui.graphics.Color(0xFF101010)),
    GRAFITO("Grafito", "Gris carbón oscuro (#1B1B1D)", "#1B1B1D", androidx.compose.ui.graphics.Color(0xFF1B1B1D), androidx.compose.ui.graphics.Color(0xFF242426)),
    GRIS("Gris", "Gris arquitectónico (#2A2A2C)", "#2A2A2C", androidx.compose.ui.graphics.Color(0xFF2A2A2C), androidx.compose.ui.graphics.Color(0xFF353437))
}

/**
 * Familias tipográficas seleccionables para personalización minimalista.
 */
enum class AethericFontFamilyChoice(
    val title: String,
    val description: String,
    val subtitle: String
) {
    SANS("Sans", "Inter / Neutralidad geométrica suiza", "Inter"),
    MONO("Mono", "JetBrains Mono / Precisión técnica", "Monospace"),
    SERIF("Serif", "Serif clásico / Editorial reflexivo", "Serif")
}

/**
 * Solar location coordinates and information
 */
data class SolarLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        val DefaultLocations = listOf(
            SolarLocation("Auto (Local Timezone)", 37.7749, -122.4194),
            SolarLocation("San Francisco", 37.7749, -122.4194),
            SolarLocation("New York", 40.7128, -74.0060),
            SolarLocation("London", 51.5074, -0.1278),
            SolarLocation("Tokyo", 35.6762, 139.6503),
            SolarLocation("Paris", 48.8566, 2.3522),
            SolarLocation("Sydney", -33.8688, 151.2093),
            SolarLocation("Berlin", 52.5200, 13.4050),
            SolarLocation("Chicago", 41.8781, -87.6298),
            SolarLocation("Seattle", 47.6062, -122.3321)
        )
    }
}

/**
 * Solar calculation result for a given day
 */
data class SolarTimes(
    val sunriseHour: Int,
    val sunriseMinute: Int,
    val sunsetHour: Int,
    val sunsetMinute: Int,
    val sunriseFormatted: String,
    val sunsetFormatted: String,
    val isNightNow: Boolean,
    val nextTransitionDescription: String,
    val solarPhaseDescription: String
)

/**
 * Astronomical Solar Calculation Engine to calculate accurate Sunrise & Sunset
 * based on NOAA solar algorithm formulas.
 */
object SolarScheduleCalculator {

    /**
     * Calculates solar sunrise and sunset for the given date, coordinates, and timezone.
     */
    fun calculateSolarTimes(
        calendar: Calendar = Calendar.getInstance(),
        latitude: Double = 37.7749,
        longitude: Double = -122.4194
    ): SolarTimes {
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val currentTotalMinutes = currentHour * 60 + currentMinute

        // Standard Solar calculation approximation (NOAA formula)
        val zenith = 90.833 // Official zenith for sunrise/sunset in degrees
        val latRad = Math.toRadians(latitude)

        // Fractional year in radians
        val gamma = (2.0 * Math.PI / 365.0) * (dayOfYear - 1 + (currentHour - 12.0) / 24.0)

        // Equation of time in minutes
        val eqtime = 229.18 * (0.000075 + 0.001868 * cos(gamma) - 0.032077 * sin(gamma) -
                0.014615 * cos(2 * gamma) - 0.040849 * sin(2 * gamma))

        // Solar declination angle in radians
        val decl = 0.006918 - 0.399912 * cos(gamma) + 0.070257 * sin(gamma) -
                0.006758 * cos(2 * gamma) + 0.000907 * sin(2 * gamma) -
                0.002697 * cos(3 * gamma) + 0.00148 * sin(3 * gamma)

        // Hour angle in degrees
        val cosHourAngle = (cos(Math.toRadians(zenith)) / (cos(latRad) * cos(decl))) - (tan(latRad) * tan(decl))
        
        // Clamp to [-1, 1] for extreme latitudes
        val clampedCosHA = cosHourAngle.coerceIn(-1.0, 1.0)
        val hourAngleDeg = Math.toDegrees(acos(clampedCosHA))

        val tzOffsetHours = calendar.timeZone.getOffset(calendar.timeInMillis) / 3600000.0

        // Sunrise and sunset in minutes from midnight UTC/local
        val solarNoonMinutes = 720.0 - (4.0 * longitude) - eqtime + (tzOffsetHours * 60.0)
        val sunriseMinutesRaw = (solarNoonMinutes - (hourAngleDeg * 4.0)).toInt()
        val sunsetMinutesRaw = (solarNoonMinutes + (hourAngleDeg * 4.0)).toInt()

        // Normalize minutes to 0..1439 (24h)
        val sunriseMinutes = ((sunriseMinutesRaw % 1440) + 1440) % 1440
        val sunsetMinutes = ((sunsetMinutesRaw % 1440) + 1440) % 1440

        val sunriseH = sunriseMinutes / 60
        val sunriseM = sunriseMinutes % 60

        val sunsetH = sunsetMinutes / 60
        val sunsetM = sunsetMinutes % 60

        // Night is before sunrise OR after sunset
        val isNight = currentTotalMinutes < sunriseMinutes || currentTotalMinutes >= sunsetMinutes

        val sunriseFormatted = formatTime(sunriseH, sunriseM)
        val sunsetFormatted = formatTime(sunsetH, sunsetM)

        val nextTransitionDescription = if (isNight) {
            if (currentTotalMinutes < sunriseMinutes) {
                val diff = sunriseMinutes - currentTotalMinutes
                val h = diff / 60
                val m = diff % 60
                if (h > 0) "Sunrise in ${h}h ${m}m (Light mode at dawn)" else "Sunrise in ${m}m"
            } else {
                val diff = (1440 - currentTotalMinutes) + sunriseMinutes
                val h = diff / 60
                val m = diff % 60
                "Sunrise in ${h}h ${m}m (Light mode at dawn)"
            }
        } else {
            val diff = sunsetMinutes - currentTotalMinutes
            val h = diff / 60
            val m = diff % 60
            if (h > 0) "Sunset in ${h}h ${m}m (Dark mode at dusk)" else "Sunset in ${m}m (Dark mode at dusk)"
        }

        val solarPhaseDescription = when {
            isNight -> "Nighttime • Eye Strain Protection Active"
            currentTotalMinutes in (sunriseMinutes..sunriseMinutes + 60) -> "Golden Morning • Daylight Active"
            currentTotalMinutes in (sunsetMinutes - 60..sunsetMinutes) -> "Dusk / Sunset Approaching"
            else -> "Daytime • Crisp Daylight Active"
        }

        return SolarTimes(
            sunriseHour = sunriseH,
            sunriseMinute = sunriseM,
            sunsetHour = sunsetH,
            sunsetMinute = sunsetM,
            sunriseFormatted = sunriseFormatted,
            sunsetFormatted = sunsetFormatted,
            isNightNow = isNight,
            nextTransitionDescription = nextTransitionDescription,
            solarPhaseDescription = solarPhaseDescription
        )
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(cal.time)
    }

    /**
     * Checks whether custom schedule is currently active
     */
    fun isCustomScheduleActive(
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int,
        calendar: Calendar = Calendar.getInstance()
    ): Boolean {
        val currentMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
        val startMinutes = startHour * 60 + startMinute
        val endMinutes = endHour * 60 + endMinute

        return if (startMinutes < endMinutes) {
            // Same day window (e.g. 13:00 to 17:00)
            currentMinutes in startMinutes until endMinutes
        } else {
            // Overnight window (e.g. 20:00 to 07:00)
            currentMinutes >= startMinutes || currentMinutes < endMinutes
        }
    }
}
