package com.example.ui.widget

enum class WidgetType(val displayName: String, val description: String) {
    WEATHER("Weather", "Current temperature, sky condition, and city forecast"),
    CALENDAR("Calendar & Agenda", "Upcoming schedule, meetings, and next event countdown"),
    FOCUS_GOAL("Daily Focus", "Daily mindful priority with checkmark tracker"),
    BATTERY_SYSTEM("System & Battery", "Live battery level, charging status, and memory")
}

data class PinnedWidgetConfig(
    val type: WidgetType,
    val isPinned: Boolean = true,
    val order: Int = 0
)

data class WeatherData(
    val city: String = "San Francisco",
    val temperatureF: Int = 68,
    val condition: String = "Partly Cloudy",
    val highF: Int = 73,
    val lowF: Int = 54,
    val isCelsius: Boolean = false,
    val humidity: Int = 58,
    val uvIndex: Int = 5
) {
    val displayTemp: String
        get() = if (isCelsius) "${((temperatureF - 32) * 5 / 9)}°C" else "$temperatureF°F"

    val displayHighLow: String
        get() = if (isCelsius) {
            "H:${((highF - 32) * 5 / 9)}° L:${((lowF - 32) * 5 / 9)}°"
        } else {
            "H:$highF° L:$lowF°"
        }
}

data class CalendarEvent(
    val id: String,
    val title: String,
    val time: String,
    val location: String? = null,
    val isToday: Boolean = true
)

data class FocusGoalData(
    val goalText: String = "Complete architecture design & review",
    val isCompleted: Boolean = false,
    val streakDays: Int = 4
)

data class BatterySystemData(
    val batteryPct: Int = 85,
    val isCharging: Boolean = false,
    val storageFreeGb: Int = 42
)
