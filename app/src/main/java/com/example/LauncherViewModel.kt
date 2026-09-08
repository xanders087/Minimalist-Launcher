package com.example

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui.theme.AccentTheme
import com.example.ui.theme.SolarLocation
import com.example.ui.theme.SolarScheduleCalculator
import com.example.ui.theme.SolarTimes
import com.example.ui.theme.ThemeMode
import com.example.ui.widget.BatterySystemData
import com.example.ui.widget.CalendarEvent
import com.example.ui.widget.FocusGoalData
import com.example.ui.widget.PinnedWidgetConfig
import com.example.ui.widget.WeatherData
import com.example.ui.widget.WidgetType
import com.example.ui.home.HomeScreenElementsConfig
import com.example.ui.home.HomeScreenPreset
import com.example.ui.gestures.GesturesConfig
import com.example.ui.gestures.HomeGestureType
import com.example.ui.gestures.GestureAction
import com.example.ui.gestures.GestureSensitivity
import com.example.ui.wallpaper.CuratedWallpaper
import com.example.ui.wallpaper.DailySource
import com.example.ui.wallpaper.WallpaperCategory
import com.example.ui.wallpaper.WallpaperConfig
import com.example.ui.wallpaper.WallpaperMode
import com.example.ui.wallpaper.WallpaperRepository
import android.net.Uri
import com.example.data.model.AppItem
import com.example.data.repository.AppRepository
import com.example.data.repository.AppRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

data class LauncherState(
    val apps: List<AppInfo> = emptyList(),
    val hiddenPackages: Set<String> = emptySet(),
    val categories: List<String> = emptyList(),
    val activeCategory: String = "All",
    val isZeroDistractions: Boolean = true,
    val isLoading: Boolean = true,
    val isAiCategorizing: Boolean = false,
    val aiCategorizationMessage: String? = null,
    val accentTheme: AccentTheme = AccentTheme.ForestSage,
    val themeMode: ThemeMode = ThemeMode.AUTO_SUNSET_SUNRISE,
    val isDarkThemeActive: Boolean = false,
    val isPureBlack: Boolean = false,
    val isWarmEyeComfort: Boolean = false,
    val selectedSolarLocation: SolarLocation = SolarLocation.DefaultLocations.first(),
    val solarTimes: SolarTimes = SolarScheduleCalculator.calculateSolarTimes(),
    val customDarkStartHour: Int = 20,
    val customDarkStartMinute: Int = 0,
    val customDarkEndHour: Int = 7,
    val customDarkEndMinute: Int = 0,
    val pinnedWidgets: List<PinnedWidgetConfig> = listOf(
        PinnedWidgetConfig(WidgetType.WEATHER, isPinned = true, order = 0),
        PinnedWidgetConfig(WidgetType.CALENDAR, isPinned = true, order = 1),
        PinnedWidgetConfig(WidgetType.FOCUS_GOAL, isPinned = false, order = 2),
        PinnedWidgetConfig(WidgetType.BATTERY_SYSTEM, isPinned = false, order = 3)
    ),
    val weatherData: WeatherData = WeatherData(),
    val calendarEvents: List<CalendarEvent> = listOf(
        CalendarEvent(id = "1", title = "Product & Design Sync", time = "10:30 AM", location = "Design Studio"),
        CalendarEvent(id = "2", title = "Team Standup", time = "02:00 PM", location = "Google Meet"),
        CalendarEvent(id = "3", title = "Gym & Workout", time = "05:30 PM", location = "Fitness Center")
    ),
    val focusGoal: FocusGoalData = FocusGoalData(),
    val batteryData: BatterySystemData = BatterySystemData(),
    val appLabelTextScale: Float = 1.0f,
    val wallpaperConfig: WallpaperConfig = WallpaperConfig(),
    val homeScreenElements: HomeScreenElementsConfig = HomeScreenElementsConfig(),
    val gesturesConfig: GesturesConfig = GesturesConfig()
) {
    val visibleApps: List<AppInfo>
        get() = apps.filter { it.packageName !in hiddenPackages }

    val mostUsedApps: List<AppInfo>
        get() = visibleApps.filter { it.launchCount > 0 }.sortedByDescending { it.launchCount }.take(5)
            .ifEmpty { visibleApps.take(5) }

    val activeWidgets: List<PinnedWidgetConfig>
        get() = pinnedWidgets.filter { it.isPinned }.sortedBy { it.order }
}

class LauncherViewModel(
    application: Application,
    val appRepository: AppRepository = AppRepositoryImpl(application)
) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("app_launch_stats", Context.MODE_PRIVATE)

    private val _uiEvents = Channel<String>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(kotlinx.coroutines.FlowPreview::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val debouncedFilteredApps: StateFlow<List<AppItem>> = _searchQuery
        .debounce(150L)
        .distinctUntilChanged()
        .combine(appRepository.appsStream) { query, apps ->
            query to apps
        }
        .mapLatest { (query, apps) ->
            if (query.isBlank()) {
                apps
            } else {
                val trimmed = query.trim().lowercase()
                apps.filter { app ->
                    app.label.lowercase().contains(trimmed) ||
                    app.packageName.lowercase().contains(trimmed) ||
                    app.category.lowercase().contains(trimmed)
                }
            }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private val _state = MutableStateFlow(LauncherState())
    val state: StateFlow<LauncherState> = _state.asStateFlow()

    init {
        loadAccentTheme()
        loadThemeModePreferences()
        loadAppLabelScalePreferences()
        loadHomeScreenElementsPreferences()
        loadWallpaperPreferences()
        loadWidgetPreferences()
        loadBatteryAndStorage()
        initInitialLaunchStats()
        loadHiddenPackages()
        loadGesturesPreferences()
        appRepository.startObserving()
        observeAppRepository()
        loadApps()
        startPeriodicThemeEvaluation()
    }

    private fun loadThemeModePreferences() {
        val modeName = prefs.getString("theme_mode", ThemeMode.AUTO_SUNSET_SUNRISE.name) ?: ThemeMode.AUTO_SUNSET_SUNRISE.name
        val themeMode = try {
            ThemeMode.valueOf(modeName)
        } catch (e: Exception) {
            ThemeMode.AUTO_SUNSET_SUNRISE
        }

        val isPureBlack = prefs.getBoolean("theme_pure_black", false)
        val isWarmComfort = prefs.getBoolean("theme_warm_comfort", false)
        val locationName = prefs.getString("solar_location_name", SolarLocation.DefaultLocations.first().name)
        val solarLocation = SolarLocation.DefaultLocations.firstOrNull { it.name == locationName } ?: SolarLocation.DefaultLocations.first()

        val customStartH = prefs.getInt("custom_dark_start_h", 20)
        val customStartM = prefs.getInt("custom_dark_start_m", 0)
        val customEndH = prefs.getInt("custom_dark_end_h", 7)
        val customEndM = prefs.getInt("custom_dark_end_m", 0)

        val solarTimes = SolarScheduleCalculator.calculateSolarTimes(
            latitude = solarLocation.latitude,
            longitude = solarLocation.longitude
        )

        val isDark = evaluateIsDarkTheme(
            mode = themeMode,
            solarTimes = solarTimes,
            customStartH = customStartH,
            customStartM = customStartM,
            customEndH = customEndH,
            customEndM = customEndM
        )

        _state.update {
            it.copy(
                themeMode = themeMode,
                isDarkThemeActive = isDark,
                isPureBlack = isPureBlack,
                isWarmEyeComfort = isWarmComfort,
                selectedSolarLocation = solarLocation,
                solarTimes = solarTimes,
                customDarkStartHour = customStartH,
                customDarkStartMinute = customStartM,
                customDarkEndHour = customEndH,
                customDarkEndMinute = customEndM
            )
        }
    }

    private fun evaluateIsDarkTheme(
        mode: ThemeMode,
        solarTimes: SolarTimes,
        customStartH: Int,
        customStartM: Int,
        customEndH: Int,
        customEndM: Int
    ): Boolean {
        return when (mode) {
            ThemeMode.AUTO_SUNSET_SUNRISE -> solarTimes.isNightNow
            ThemeMode.SYSTEM -> {
                val nightModeFlags = getApplication<Application>().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                nightModeFlags == Configuration.UI_MODE_NIGHT_YES
            }
            ThemeMode.ALWAYS_DARK -> true
            ThemeMode.ALWAYS_LIGHT -> false
            ThemeMode.CUSTOM_SCHEDULE -> {
                SolarScheduleCalculator.isCustomScheduleActive(
                    startHour = customStartH,
                    startMinute = customStartM,
                    endHour = customEndH,
                    endMinute = customEndM
                )
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _state.update { current ->
            val isDark = evaluateIsDarkTheme(
                mode = mode,
                solarTimes = current.solarTimes,
                customStartH = current.customDarkStartHour,
                customStartM = current.customDarkStartMinute,
                customEndH = current.customDarkEndHour,
                customEndM = current.customDarkEndMinute
            )
            current.copy(themeMode = mode, isDarkThemeActive = isDark)
        }
    }

    fun toggleQuickTheme() {
        val current = _state.value
        val newMode = when (current.themeMode) {
            ThemeMode.AUTO_SUNSET_SUNRISE -> ThemeMode.ALWAYS_DARK
            ThemeMode.ALWAYS_DARK -> ThemeMode.ALWAYS_LIGHT
            ThemeMode.ALWAYS_LIGHT -> ThemeMode.AUTO_SUNSET_SUNRISE
            else -> ThemeMode.AUTO_SUNSET_SUNRISE
        }
        setThemeMode(newMode)
    }

    fun setPureBlack(enabled: Boolean) {
        prefs.edit().putBoolean("theme_pure_black", enabled).apply()
        _state.update { it.copy(isPureBlack = enabled) }
    }

    fun setWarmEyeComfort(enabled: Boolean) {
        prefs.edit().putBoolean("theme_warm_comfort", enabled).apply()
        _state.update { it.copy(isWarmEyeComfort = enabled) }
    }

    fun setSolarLocation(location: SolarLocation) {
        prefs.edit().putString("solar_location_name", location.name).apply()
        val solarTimes = SolarScheduleCalculator.calculateSolarTimes(
            latitude = location.latitude,
            longitude = location.longitude
        )
        _state.update { current ->
            val isDark = evaluateIsDarkTheme(
                mode = current.themeMode,
                solarTimes = solarTimes,
                customStartH = current.customDarkStartHour,
                customStartM = current.customDarkStartMinute,
                customEndH = current.customDarkEndHour,
                customEndM = current.customDarkEndMinute
            )
            current.copy(
                selectedSolarLocation = location,
                solarTimes = solarTimes,
                isDarkThemeActive = isDark
            )
        }
    }

    fun setCustomSchedule(startH: Int, startM: Int, endH: Int, endM: Int) {
        prefs.edit()
            .putInt("custom_dark_start_h", startH)
            .putInt("custom_dark_start_m", startM)
            .putInt("custom_dark_end_h", endH)
            .putInt("custom_dark_end_m", endM)
            .apply()

        _state.update { current ->
            val isDark = evaluateIsDarkTheme(
                mode = current.themeMode,
                solarTimes = current.solarTimes,
                customStartH = startH,
                customStartM = startM,
                customEndH = endH,
                customEndM = endM
            )
            current.copy(
                customDarkStartHour = startH,
                customDarkStartMinute = startM,
                customDarkEndHour = endH,
                customDarkEndMinute = endM,
                isDarkThemeActive = isDark
            )
        }
    }

    private fun startPeriodicThemeEvaluation() {
        viewModelScope.launch {
            while (isActive) {
                delay(30_000) // Recheck every 30 seconds
                val current = _state.value
                val updatedSolar = SolarScheduleCalculator.calculateSolarTimes(
                    latitude = current.selectedSolarLocation.latitude,
                    longitude = current.selectedSolarLocation.longitude
                )
                val isDark = evaluateIsDarkTheme(
                    mode = current.themeMode,
                    solarTimes = updatedSolar,
                    customStartH = current.customDarkStartHour,
                    customStartM = current.customDarkStartMinute,
                    customEndH = current.customDarkEndHour,
                    customEndM = current.customDarkEndMinute
                )
                _state.update {
                    it.copy(
                        solarTimes = updatedSolar,
                        isDarkThemeActive = isDark
                    )
                }
            }
        }
    }


    private fun loadAccentTheme() {
        val themeId = prefs.getString("accent_id", AccentTheme.ForestSage.id) ?: AccentTheme.ForestSage.id
        val preset = AccentTheme.Presets.firstOrNull { it.id == themeId }
        if (preset != null) {
            _state.update { it.copy(accentTheme = preset) }
        } else {
            val primaryLong = prefs.getLong("accent_primary", AccentTheme.ForestSage.primaryColorLong)
            val containerLong = prefs.getLong("accent_container", AccentTheme.ForestSage.containerColorLong)
            val onContainerLong = prefs.getLong("accent_on_container", AccentTheme.ForestSage.onContainerColorLong)
            val name = prefs.getString("accent_name", "Custom") ?: "Custom"
            val loadedTheme = AccentTheme(
                id = themeId,
                name = name,
                primaryColorLong = primaryLong,
                containerColorLong = containerLong,
                onContainerColorLong = onContainerLong
            )
            _state.update { it.copy(accentTheme = loadedTheme) }
        }
    }

    fun setAccentTheme(theme: AccentTheme) {
        prefs.edit()
            .putString("accent_id", theme.id)
            .putString("accent_name", theme.name)
            .putLong("accent_primary", theme.primaryColorLong)
            .putLong("accent_container", theme.containerColorLong)
            .putLong("accent_on_container", theme.onContainerColorLong)
            .apply()
        _state.update { it.copy(accentTheme = theme) }
    }

    fun setCustomAccentColor(color: Color) {
        val theme = AccentTheme.fromColor(color)
        setAccentTheme(theme)
    }

    private fun loadWidgetPreferences() {
        val weatherPinned = prefs.getBoolean("widget_weather_pinned", true)
        val calendarPinned = prefs.getBoolean("widget_calendar_pinned", true)
        val focusPinned = prefs.getBoolean("widget_focus_pinned", false)
        val batteryPinned = prefs.getBoolean("widget_battery_pinned", false)

        val updatedConfigs = listOf(
            PinnedWidgetConfig(WidgetType.WEATHER, isPinned = weatherPinned, order = 0),
            PinnedWidgetConfig(WidgetType.CALENDAR, isPinned = calendarPinned, order = 1),
            PinnedWidgetConfig(WidgetType.FOCUS_GOAL, isPinned = focusPinned, order = 2),
            PinnedWidgetConfig(WidgetType.BATTERY_SYSTEM, isPinned = batteryPinned, order = 3)
        )

        val isCelsius = prefs.getBoolean("weather_is_celsius", false)
        val city = prefs.getString("weather_city", "San Francisco") ?: "San Francisco"
        val condition = prefs.getString("weather_condition", "Partly Cloudy") ?: "Partly Cloudy"
        val tempF = prefs.getInt("weather_temp_f", 68)
        val highF = prefs.getInt("weather_high_f", 73)
        val lowF = prefs.getInt("weather_low_f", 54)

        val focusText = prefs.getString("focus_goal_text", "Complete architecture design & review") ?: "Complete architecture design & review"
        val focusCompleted = prefs.getBoolean("focus_goal_completed", false)
        val streakDays = prefs.getInt("focus_goal_streak", 4)

        _state.update {
            it.copy(
                pinnedWidgets = updatedConfigs,
                weatherData = WeatherData(
                    city = city,
                    temperatureF = tempF,
                    condition = condition,
                    highF = highF,
                    lowF = lowF,
                    isCelsius = isCelsius
                ),
                focusGoal = FocusGoalData(
                    goalText = focusText,
                    isCompleted = focusCompleted,
                    streakDays = streakDays
                )
            )
        }
    }

    fun toggleWidgetPinned(type: WidgetType) {
        _state.update { currentState ->
            val updated = currentState.pinnedWidgets.map {
                if (it.type == type) it.copy(isPinned = !it.isPinned) else it
            }
            val key = when (type) {
                WidgetType.WEATHER -> "widget_weather_pinned"
                WidgetType.CALENDAR -> "widget_calendar_pinned"
                WidgetType.FOCUS_GOAL -> "widget_focus_pinned"
                WidgetType.BATTERY_SYSTEM -> "widget_battery_pinned"
            }
            val nowPinned = updated.firstOrNull { it.type == type }?.isPinned ?: true
            prefs.edit().putBoolean(key, nowPinned).apply()
            currentState.copy(pinnedWidgets = updated)
        }
    }

    fun setWidgetPinned(type: WidgetType, isPinned: Boolean) {
        _state.update { currentState ->
            val updated = currentState.pinnedWidgets.map {
                if (it.type == type) it.copy(isPinned = isPinned) else it
            }
            val key = when (type) {
                WidgetType.WEATHER -> "widget_weather_pinned"
                WidgetType.CALENDAR -> "widget_calendar_pinned"
                WidgetType.FOCUS_GOAL -> "widget_focus_pinned"
                WidgetType.BATTERY_SYSTEM -> "widget_battery_pinned"
            }
            prefs.edit().putBoolean(key, isPinned).apply()
            currentState.copy(pinnedWidgets = updated)
        }
    }

    fun toggleWeatherUnit() {
        val currentIsCelsius = _state.value.weatherData.isCelsius
        val nextIsCelsius = !currentIsCelsius
        prefs.edit().putBoolean("weather_is_celsius", nextIsCelsius).apply()
        _state.update { it.copy(weatherData = it.weatherData.copy(isCelsius = nextIsCelsius)) }
    }

    fun updateWeather(city: String, tempF: Int, condition: String, highF: Int, lowF: Int) {
        prefs.edit()
            .putString("weather_city", city)
            .putInt("weather_temp_f", tempF)
            .putString("weather_condition", condition)
            .putInt("weather_high_f", highF)
            .putInt("weather_low_f", lowF)
            .apply()
        _state.update {
            it.copy(
                weatherData = it.weatherData.copy(
                    city = city,
                    temperatureF = tempF,
                    condition = condition,
                    highF = highF,
                    lowF = lowF
                )
            )
        }
    }

    fun addCalendarEvent(title: String, time: String, location: String? = null) {
        val newEvent = CalendarEvent(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            time = time.trim(),
            location = location?.trim()?.ifBlank { null }
        )
        _state.update {
            it.copy(calendarEvents = listOf(newEvent) + it.calendarEvents)
        }
    }

    fun removeCalendarEvent(eventId: String) {
        _state.update {
            it.copy(calendarEvents = it.calendarEvents.filterNot { event -> event.id == eventId })
        }
    }

    fun toggleFocusGoal() {
        val current = _state.value.focusGoal
        val isNowCompleted = !current.isCompleted
        val newStreak = if (isNowCompleted) current.streakDays + 1 else (current.streakDays - 1).coerceAtLeast(0)
        prefs.edit()
            .putBoolean("focus_goal_completed", isNowCompleted)
            .putInt("focus_goal_streak", newStreak)
            .apply()
        _state.update {
            it.copy(focusGoal = current.copy(isCompleted = isNowCompleted, streakDays = newStreak))
        }
    }

    fun setFocusGoalText(text: String) {
        prefs.edit().putString("focus_goal_text", text).apply()
        _state.update {
            it.copy(focusGoal = it.focusGoal.copy(goalText = text))
        }
    }

    fun loadBatteryAndStorage() {
        try {
            val app = getApplication<Application>()
            val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = app.registerReceiver(null, ifilter)
            val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 85
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: 100
            val batteryPct = if (level >= 0 && scale > 0) (level * 100 / scale) else 85
            val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

            val stat = StatFs(Environment.getDataDirectory().path)
            val bytesAvailable = stat.availableBlocksLong * stat.blockSizeLong
            val gigaAvailable = (bytesAvailable / (1024 * 1024 * 1024)).toInt().coerceAtLeast(1)

            _state.update {
                it.copy(
                    batteryData = BatterySystemData(
                        batteryPct = batteryPct,
                        isCharging = isCharging,
                        storageFreeGb = gigaAvailable
                    )
                )
            }
        } catch (e: Throwable) {
            // fallback gracefully
        }
    }

    private fun loadHiddenPackages() {
        val hidden = prefs.getStringSet("hidden_packages", emptySet()) ?: emptySet()
        _state.update { it.copy(hiddenPackages = hidden.toSet()) }
    }

    fun hideApp(packageName: String) {
        val currentHidden = _state.value.hiddenPackages.toMutableSet()
        currentHidden.add(packageName)
        prefs.edit().putStringSet("hidden_packages", currentHidden).apply()
        _state.update { it.copy(hiddenPackages = currentHidden) }
    }

    fun unhideApp(packageName: String) {
        val currentHidden = _state.value.hiddenPackages.toMutableSet()
        currentHidden.remove(packageName)
        prefs.edit().putStringSet("hidden_packages", currentHidden).apply()
        _state.update { it.copy(hiddenPackages = currentHidden) }
    }

    fun unhideAllApps() {
        prefs.edit().remove("hidden_packages").apply()
        _state.update { it.copy(hiddenPackages = emptySet()) }
    }

    private fun initInitialLaunchStats() {
        val isFirstRun = prefs.getBoolean("is_first_run_stats", true)
        if (isFirstRun) {
            val initialStats = mapOf(
                "com.google.android.dialer" to 28,
                "com.android.chrome" to 24,
                "com.google.android.apps.messaging" to 19,
                "com.google.android.GoogleCamera" to 14,
                "com.google.android.apps.maps" to 11,
                "com.spotify.music" to 9,
                "com.google.android.youtube" to 7,
                "com.android.settings" to 5
            )
            val editor = prefs.edit()
            for ((pkg, count) in initialStats) {
                if (!prefs.contains("launch_$pkg")) {
                    editor.putInt("launch_$pkg", count)
                }
            }
            editor.putBoolean("is_first_run_stats", false)
            editor.apply()
        }
    }

    private fun observeAppRepository() {
        viewModelScope.launch {
            appRepository.appsStream.collect { appItems ->
                if (appItems.isNotEmpty()) {
                    val appInfos = appItems.map { AppInfo.fromAppItem(it) }
                    val categories = computeCategories(appInfos)
                    _state.update {
                        it.copy(
                            apps = appInfos,
                            categories = categories,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun loadApps() {
        viewModelScope.launch {
            try {
                appRepository.loadApps()
                val currentApps = _state.value.apps
                if (currentApps.isEmpty()) {
                    val fallbackApps = getDefaultFallbackApps()
                    val categories = computeCategories(fallbackApps)
                    _state.update {
                        it.copy(
                            apps = fallbackApps,
                            categories = categories,
                            activeCategory = "All",
                            isLoading = false
                        )
                    }
                }
                // Automatically analyze and group apps using Gemini AI
                analyzeInstalledAppsWithGemini()
            } catch (e: Throwable) {
                val fallbackApps = getDefaultFallbackApps()
                val categories = computeCategories(fallbackApps)
                _state.update {
                    it.copy(
                        apps = fallbackApps,
                        categories = categories,
                        activeCategory = "All",
                        isLoading = false
                    )
                }
                analyzeInstalledAppsWithGemini()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        appRepository.stopObserving()
    }

    private fun computeCategories(apps: List<AppInfo>): List<String> {
        val baseCategories = listOf("All", "⭐ Most Used")
        val appCategories = apps.map { it.category }.distinct().sorted()
        return (baseCategories + appCategories).distinct()
    }

    private fun getDefaultFallbackApps(): List<AppInfo> {
        val list = listOf(
            AppInfo("Phone", "com.google.android.dialer", null, "Communication"),
            AppInfo("Messages", "com.google.android.apps.messaging", null, "Communication"),
            AppInfo("Chrome", "com.android.chrome", null, "Productivity"),
            AppInfo("YouTube", "com.google.android.youtube", null, "Entertainment"),
            AppInfo("Spotify", "com.spotify.music", null, "Entertainment"),
            AppInfo("Google Drive", "com.google.android.apps.docs", null, "Productivity"),
            AppInfo("Google Pay", "com.google.android.apps.walletnfcrel", null, "Finance"),
            AppInfo("Camera", "com.google.android.GoogleCamera", null, "Utilities"),
            AppInfo("Settings", "com.android.settings", null, "System"),
            AppInfo("Calculator", "com.google.android.calculator", null, "Productivity"),
            AppInfo("Calendar", "com.google.android.calendar", null, "Productivity"),
            AppInfo("Clock", "com.google.android.deskclock", null, "Utilities"),
            AppInfo("Google Maps", "com.google.android.apps.maps", null, "Navigation"),
            AppInfo("Google Fit", "com.google.android.apps.fitness", null, "Health & Fitness"),
            AppInfo("Photos", "com.google.android.apps.photos", null, "Entertainment")
        )
        return list.map {
            val count = prefs.getInt("launch_${it.packageName}", 0)
            it.copy(launchCount = count)
        }.sortedBy { it.label }
    }

    fun recordAppLaunch(packageName: String) {
        val current = prefs.getInt("launch_$packageName", 0)
        val updatedCount = current + 1
        prefs.edit().putInt("launch_$packageName", updatedCount).apply()

        val targetApp = _state.value.apps.firstOrNull { it.packageName == packageName }
        if (targetApp != null) {
            appRepository.recordAppLaunch(targetApp.toAppItem())
        }

        _state.update { currentState ->
            val updatedApps = currentState.apps.map { app ->
                if (app.packageName == packageName) {
                    app.copy(launchCount = updatedCount)
                } else {
                    app
                }
            }
            currentState.copy(apps = updatedApps)
        }
    }

    fun recordAppLaunch(appInfo: AppInfo) {
        appRepository.recordAppLaunch(appInfo.toAppItem())
        recordAppLaunch(appInfo.packageName)
    }

    fun launchAppWithResult(appInfo: AppInfo): com.example.domain.launcher.LaunchResult {
        return appRepository.launchAppWithResult(appInfo.toAppItem())
    }

    fun launchApp(appInfo: AppInfo): Boolean {
        return launchAppWithResult(appInfo) is com.example.domain.launcher.LaunchResult.Success
    }

    fun getAppLaunchCount(packageName: String): Int {
        return prefs.getInt("launch_$packageName", 0)
    }

    private fun getLocalCategoryFallback(appName: String, packageName: String = ""): String {
        val lower = "$appName $packageName".lowercase()
        val keywords = mapOf(
            "Communication" to listOf("dialer", "phone", "teléfono", "chat", "mail", "whatsapp", "telegram", "message", "mensaje", "slack", "discord", "meet", "zoom", "teams"),
            "Productivity" to listOf("chrome", "browser", "doc", "sheet", "note", "notion", "todo", "drive", "work", "calc", "calculadora", "calendar", "calendario", "office", "pdf"),
            "Entertainment" to listOf("youtube", "netflix", "spotify", "music", "música", "game", "juego", "photo", "foto", "twitch", "tiktok", "instagram", "prime", "disney", "video"),
            "Finance" to listOf("bank", "banco", "pay", "pago", "wallet", "crypto", "binance", "paypal", "revolut", "finance", "finanzas"),
            "Navigation" to listOf("map", "maps", "gps", "uber", "taxi", "transit", "waze", "lyft", "citymapper"),
            "Health & Fitness" to listOf("fit", "fitness", "health", "salud", "gym", "yoga", "medita", "strava", "run"),
            "System" to listOf("setting", "ajuste", "system", "launcher", "android", "package", "store", "play")
        )
        
        for ((cat, words) in keywords) {
            if (words.any { lower.contains(it) }) {
                return cat
            }
        }
        return "Utilities"
    }

    fun setActiveCategory(category: String) {
        _state.update { it.copy(activeCategory = category) }
    }

    fun toggleZeroDistractions() {
        _state.update { it.copy(isZeroDistractions = !it.isZeroDistractions) }
    }

    /**
     * Triggers the Gemini API to analyze all installed apps in batch and group them
     * logically into categories like Communication, Productivity, Entertainment, Finance, etc.
     */
    fun analyzeInstalledAppsWithGemini() {
        val currentApps = _state.value.apps
        if (currentApps.isEmpty()) return

        viewModelScope.launch {
            _state.update { 
                it.copy(
                    isAiCategorizing = true,
                    aiCategorizationMessage = "Analyzing installed apps with Gemini AI..."
                ) 
            }

            try {
                val categorizationMap = GeminiClient.categorizeAllApps(currentApps)
                
                if (categorizationMap.isNotEmpty()) {
                    val updatedApps = currentApps.map { app ->
                        val aiCat = categorizationMap[app.packageName]
                        if (!aiCat.isNullOrBlank()) {
                            app.copy(category = aiCat)
                        } else {
                            app
                        }
                    }

                    val newCategories = computeCategories(updatedApps)

                    _state.update {
                        it.copy(
                            apps = updatedApps,
                            categories = newCategories,
                            isAiCategorizing = false,
                            aiCategorizationMessage = "Categorized with Gemini AI"
                        )
                    }
                } else {
                    _state.update { 
                        it.copy(
                            isAiCategorizing = false,
                            aiCategorizationMessage = null
                        ) 
                    }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isAiCategorizing = false,
                        aiCategorizationMessage = null
                    ) 
                }
            }
        }
    }

    private fun loadAppLabelScalePreferences() {
        val scale = prefs.getFloat("app_label_text_scale", 1.0f).coerceIn(0.70f, 1.60f)
        _state.update { it.copy(appLabelTextScale = scale) }
    }

    fun setAppLabelTextScale(scale: Float) {
        val clamped = scale.coerceIn(0.70f, 1.60f)
        prefs.edit().putFloat("app_label_text_scale", clamped).apply()
        _state.update { it.copy(appLabelTextScale = clamped) }
    }

    fun resetAppLabelTextScale() {
        setAppLabelTextScale(1.0f)
    }

    fun categorizeAppWithGemini(appInfo: AppInfo) {
        viewModelScope.launch {
            val newCategory = GeminiClient.categorizeApp(appInfo.label, appInfo.packageName)
            val updatedApps = _state.value.apps.map { 
                if (it.packageName == appInfo.packageName) it.copy(category = newCategory) else it 
            }
            val newCategories = computeCategories(updatedApps)
            
            _state.update {
                it.copy(
                    apps = updatedApps,
                    categories = newCategories
                )
            }
        }
    }

    private fun loadWallpaperPreferences() {
        val modeStr = prefs.getString("wallpaper_mode", WallpaperMode.SOLID.name) ?: WallpaperMode.SOLID.name
        val mode = try { WallpaperMode.valueOf(modeStr) } catch (e: Exception) { WallpaperMode.SOLID }

        val dailySourceStr = prefs.getString("wallpaper_daily_source", DailySource.PREFERENCES.name) ?: DailySource.PREFERENCES.name
        val dailySource = try { DailySource.valueOf(dailySourceStr) } catch (e: Exception) { DailySource.PREFERENCES }

        val catSet = prefs.getStringSet("wallpaper_categories", setOf("nature", "art", "space")) ?: setOf("nature", "art", "space")
        val selectedCategories = catSet.mapNotNull { WallpaperCategory.fromId(it) }.toSet().ifEmpty {
            setOf(WallpaperCategory.NATURE, WallpaperCategory.ART, WallpaperCategory.SPACE)
        }

        val currentPath = prefs.getString("wallpaper_current_path", null)
        val staticPath = prefs.getString("wallpaper_static_path", null)
        val isStaticGallery = prefs.getBoolean("wallpaper_is_static_gallery", false)
        val galleryPhotos = prefs.getStringSet("wallpaper_gallery_photos", emptySet())?.toList() ?: emptyList()
        val dimming = prefs.getFloat("wallpaper_dimming", 0.40f).coerceIn(0.15f, 0.75f)
        val lastDateKey = prefs.getString("wallpaper_last_date_key", "") ?: ""
        val title = prefs.getString("wallpaper_current_title", "Color plano") ?: "Color plano"
        val subtitle = prefs.getString("wallpaper_current_subtitle", "Minimalista") ?: "Minimalista"
        val autoAdjust = prefs.getBoolean("wallpaper_auto_adjust_text", true)
        val imageLum = prefs.getFloat("wallpaper_image_luminance", 0.30f)
        val textShadow = prefs.getBoolean("wallpaper_text_shadow", true)

        var initialConfig = WallpaperConfig(
            mode = mode,
            dailySource = dailySource,
            selectedCategories = selectedCategories,
            currentWallpaperPath = currentPath,
            staticWallpaperPath = staticPath,
            isStaticFromGallery = isStaticGallery,
            galleryPhotos = galleryPhotos,
            dimmingAlpha = dimming,
            lastDailyDateKey = lastDateKey,
            currentTitle = title,
            currentSubtitle = subtitle,
            autoAdjustTextColor = autoAdjust,
            imageLuminance = imageLum,
            textShadowEnabled = textShadow
        )
        initialConfig = adaptWallpaperTextStyling(initialConfig, imageLum)

        _state.update { it.copy(wallpaperConfig = initialConfig) }
        checkDailyWallpaperOnLaunch(initialConfig)
    }

    private fun adaptWallpaperTextStyling(config: WallpaperConfig, measuredLuminance: Float? = null): WallpaperConfig {
        val imageLum = measuredLuminance ?: WallpaperRepository.getEstimatedLuminance(config.currentWallpaperPath)
        val effLum = (imageLum * (1.0f - config.dimmingAlpha)).coerceIn(0f, 1f)
        val isDark = effLum < 0.45f
        return config.copy(
            imageLuminance = imageLum,
            effectiveLuminance = effLum,
            isDarkBackground = isDark
        )
    }

    private fun checkDailyWallpaperOnLaunch(config: WallpaperConfig) {
        if (config.mode == WallpaperMode.DAILY) {
            val todayKey = WallpaperRepository.getTodayDateKey()
            if (config.lastDailyDateKey != todayKey || config.currentWallpaperPath.isNullOrEmpty()) {
                val (resolvedPath, textInfo) = WallpaperRepository.resolveDailyWallpaper(
                    dailySource = config.dailySource,
                    galleryPhotos = config.galleryPhotos,
                    selectedCategories = config.selectedCategories
                )
                val updated = adaptWallpaperTextStyling(
                    config.copy(
                        currentWallpaperPath = resolvedPath,
                        lastDailyDateKey = todayKey,
                        currentTitle = textInfo.first,
                        currentSubtitle = textInfo.second
                    )
                )
                saveWallpaperConfig(updated)
                _state.update { it.copy(wallpaperConfig = updated) }
            }
        } else if (config.mode == WallpaperMode.STATIC) {
            if (config.currentWallpaperPath != config.staticWallpaperPath && !config.staticWallpaperPath.isNullOrEmpty()) {
                val updated = adaptWallpaperTextStyling(config.copy(currentWallpaperPath = config.staticWallpaperPath))
                _state.update { it.copy(wallpaperConfig = updated) }
            }
        }
    }

    private fun saveWallpaperConfig(config: WallpaperConfig) {
        prefs.edit()
            .putString("wallpaper_mode", config.mode.name)
            .putString("wallpaper_daily_source", config.dailySource.name)
            .putStringSet("wallpaper_categories", config.selectedCategories.map { it.id }.toSet())
            .putString("wallpaper_current_path", config.currentWallpaperPath)
            .putString("wallpaper_static_path", config.staticWallpaperPath)
            .putBoolean("wallpaper_is_static_gallery", config.isStaticFromGallery)
            .putStringSet("wallpaper_gallery_photos", config.galleryPhotos.toSet())
            .putFloat("wallpaper_dimming", config.dimmingAlpha)
            .putString("wallpaper_last_date_key", config.lastDailyDateKey)
            .putString("wallpaper_current_title", config.currentTitle)
            .putString("wallpaper_current_subtitle", config.currentSubtitle)
            .putBoolean("wallpaper_auto_adjust_text", config.autoAdjustTextColor)
            .putFloat("wallpaper_image_luminance", config.imageLuminance)
            .putFloat("wallpaper_effective_luminance", config.effectiveLuminance)
            .putBoolean("wallpaper_is_dark_bg", config.isDarkBackground)
            .putBoolean("wallpaper_text_shadow", config.textShadowEnabled)
            .apply()
    }

    fun updateWallpaperLuminance(measuredLuminance: Float) {
        val current = _state.value.wallpaperConfig
        val adapted = adaptWallpaperTextStyling(current, measuredLuminance)
        if (Math.abs(current.imageLuminance - adapted.imageLuminance) > 0.05f ||
            current.isDarkBackground != adapted.isDarkBackground
        ) {
            saveWallpaperConfig(adapted)
            _state.update { it.copy(wallpaperConfig = adapted) }
        }
    }

    fun toggleAutoAdjustTextColor() {
        val current = _state.value.wallpaperConfig
        val updated = current.copy(autoAdjustTextColor = !current.autoAdjustTextColor)
        saveWallpaperConfig(updated)
        _state.update { it.copy(wallpaperConfig = updated) }
    }

    fun toggleTextShadow() {
        val current = _state.value.wallpaperConfig
        val updated = current.copy(textShadowEnabled = !current.textShadowEnabled)
        saveWallpaperConfig(updated)
        _state.update { it.copy(wallpaperConfig = updated) }
    }

    fun setWallpaperMode(mode: WallpaperMode) {
        val current = _state.value.wallpaperConfig
        val todayKey = WallpaperRepository.getTodayDateKey()

        val updated = when (mode) {
            WallpaperMode.SOLID -> {
                current.copy(
                    mode = WallpaperMode.SOLID,
                    currentWallpaperPath = null,
                    currentTitle = "Color plano",
                    currentSubtitle = "Minimalista"
                )
            }
            WallpaperMode.STATIC -> {
                val path = current.staticWallpaperPath ?: WallpaperRepository.curatedWallpapers.first().url
                val title = if (current.isStaticFromGallery) "Foto de Galería" else "Fondo Estático"
                adaptWallpaperTextStyling(
                    current.copy(
                        mode = WallpaperMode.STATIC,
                        staticWallpaperPath = path,
                        currentWallpaperPath = path,
                        currentTitle = title,
                        currentSubtitle = "Imagen fija"
                    )
                )
            }
            WallpaperMode.DAILY -> {
                val (resolvedPath, textInfo) = WallpaperRepository.resolveDailyWallpaper(
                    dailySource = current.dailySource,
                    galleryPhotos = current.galleryPhotos,
                    selectedCategories = current.selectedCategories
                )
                adaptWallpaperTextStyling(
                    current.copy(
                        mode = WallpaperMode.DAILY,
                        currentWallpaperPath = resolvedPath,
                        lastDailyDateKey = todayKey,
                        currentTitle = textInfo.first,
                        currentSubtitle = textInfo.second
                    )
                )
            }
        }

        saveWallpaperConfig(updated)
        _state.update { it.copy(wallpaperConfig = updated) }
    }

    fun setDailySource(dailySource: DailySource) {
        val current = _state.value.wallpaperConfig
        val todayKey = WallpaperRepository.getTodayDateKey()
        val (resolvedPath, textInfo) = WallpaperRepository.resolveDailyWallpaper(
            dailySource = dailySource,
            galleryPhotos = current.galleryPhotos,
            selectedCategories = current.selectedCategories
        )
        val updated = adaptWallpaperTextStyling(
            current.copy(
                dailySource = dailySource,
                currentWallpaperPath = if (current.mode == WallpaperMode.DAILY) resolvedPath else current.currentWallpaperPath,
                lastDailyDateKey = todayKey,
                currentTitle = if (current.mode == WallpaperMode.DAILY) textInfo.first else current.currentTitle,
                currentSubtitle = if (current.mode == WallpaperMode.DAILY) textInfo.second else current.currentSubtitle
            )
        )
        saveWallpaperConfig(updated)
        _state.update { it.copy(wallpaperConfig = updated) }
    }

    fun toggleWallpaperCategory(category: WallpaperCategory) {
        val current = _state.value.wallpaperConfig
        val newCategories = if (category in current.selectedCategories) {
            if (current.selectedCategories.size > 1) current.selectedCategories - category else current.selectedCategories
        } else {
            current.selectedCategories + category
        }
        val (resolvedPath, textInfo) = WallpaperRepository.resolveDailyWallpaper(
            dailySource = DailySource.PREFERENCES,
            galleryPhotos = current.galleryPhotos,
            selectedCategories = newCategories
        )
        val updated = adaptWallpaperTextStyling(
            current.copy(
                selectedCategories = newCategories,
                currentWallpaperPath = if (current.mode == WallpaperMode.DAILY && current.dailySource == DailySource.PREFERENCES) resolvedPath else current.currentWallpaperPath,
                currentTitle = if (current.mode == WallpaperMode.DAILY && current.dailySource == DailySource.PREFERENCES) textInfo.first else current.currentTitle,
                currentSubtitle = if (current.mode == WallpaperMode.DAILY && current.dailySource == DailySource.PREFERENCES) textInfo.second else current.currentSubtitle
            )
        )
        saveWallpaperConfig(updated)
        _state.update { it.copy(wallpaperConfig = updated) }
    }

    fun setStaticWallpaper(path: String, isGallery: Boolean, title: String = "Fondo Seleccionado", author: String = "") {
        val current = _state.value.wallpaperConfig
        val updated = adaptWallpaperTextStyling(
            current.copy(
                mode = WallpaperMode.STATIC,
                staticWallpaperPath = path,
                isStaticFromGallery = isGallery,
                currentWallpaperPath = path,
                currentTitle = title,
                currentSubtitle = if (author.isNotEmpty()) "Por $author" else if (isGallery) "Foto de tu galería" else "Imagen estática"
            )
        )
        saveWallpaperConfig(updated)
        _state.update { it.copy(wallpaperConfig = updated) }
    }

    fun addGalleryPhotos(uris: List<Uri>, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val savedPaths = mutableListOf<String>()
            for (uri in uris) {
                val path = WallpaperRepository.saveGalleryUriToInternalStorage(context, uri)
                if (path != null) {
                    savedPaths.add(path)
                }
            }
            if (savedPaths.isNotEmpty()) {
                val current = _state.value.wallpaperConfig
                val newGallery = current.galleryPhotos + savedPaths
                val updated = if (current.mode == WallpaperMode.DAILY && current.dailySource == DailySource.GALLERY) {
                    val (path, textInfo) = WallpaperRepository.resolveDailyWallpaper(
                        dailySource = DailySource.GALLERY,
                        galleryPhotos = newGallery,
                        selectedCategories = current.selectedCategories
                    )
                    adaptWallpaperTextStyling(
                        current.copy(
                            galleryPhotos = newGallery,
                            currentWallpaperPath = path,
                            currentTitle = textInfo.first,
                            currentSubtitle = textInfo.second
                        )
                    )
                } else {
                    current.copy(galleryPhotos = newGallery)
                }
                saveWallpaperConfig(updated)
                _state.update { it.copy(wallpaperConfig = updated) }
            }
        }
    }

    fun removeGalleryPhoto(path: String) {
        WallpaperRepository.deleteCustomPhoto(path)
        val current = _state.value.wallpaperConfig
        val newGallery = current.galleryPhotos.filter { it != path }
        val updated = if (current.currentWallpaperPath == path) {
            val (newPath, textInfo) = WallpaperRepository.resolveDailyWallpaper(
                dailySource = current.dailySource,
                galleryPhotos = newGallery,
                selectedCategories = current.selectedCategories
            )
            adaptWallpaperTextStyling(
                current.copy(
                    galleryPhotos = newGallery,
                    currentWallpaperPath = if (current.mode == WallpaperMode.SOLID) null else newPath,
                    currentTitle = textInfo.first,
                    currentSubtitle = textInfo.second
                )
            )
        } else {
            current.copy(galleryPhotos = newGallery)
        }
        saveWallpaperConfig(updated)
        _state.update { it.copy(wallpaperConfig = updated) }
    }

    fun setWallpaperDimming(dimming: Float) {
        val clamped = dimming.coerceIn(0.15f, 0.75f)
        val current = _state.value.wallpaperConfig
        val updated = adaptWallpaperTextStyling(current.copy(dimmingAlpha = clamped))
        saveWallpaperConfig(updated)
        _state.update { it.copy(wallpaperConfig = updated) }
    }

    fun shuffleOrNextDailyWallpaper() {
        val current = _state.value.wallpaperConfig
        val (nextPath, textInfo) = WallpaperRepository.pickNextWallpaper(
            dailySource = current.dailySource,
            galleryPhotos = current.galleryPhotos,
            selectedCategories = current.selectedCategories,
            currentPath = current.currentWallpaperPath
        )
        val updated = adaptWallpaperTextStyling(
            current.copy(
                currentWallpaperPath = nextPath,
                currentTitle = textInfo.first,
                currentSubtitle = textInfo.second
            )
        )
        saveWallpaperConfig(updated)
        _state.update { it.copy(wallpaperConfig = updated) }
    }

    // --- HOME SCREEN ELEMENTS MANAGEMENT ---

    private fun loadHomeScreenElementsPreferences() {
        val showClock = prefs.getBoolean("pref_elem_clock", true)
        val showDate = prefs.getBoolean("pref_elem_date", true)
        val showSolarBadge = prefs.getBoolean("pref_elem_solar_badge", true)
        val showHeaderActions = prefs.getBoolean("pref_elem_header_actions", true)
        val showWidgets = prefs.getBoolean("pref_elem_widgets", true)
        val showMostUsedApps = prefs.getBoolean("pref_elem_most_used_apps", true)
        val showMostUsedHeader = prefs.getBoolean("pref_elem_most_used_header", true)
        val showSearchBar = prefs.getBoolean("pref_elem_search_bar", true)
        val showSwipeUpHint = prefs.getBoolean("pref_elem_swipe_hint", true)

        val elementsConfig = HomeScreenElementsConfig(
            showClock = showClock,
            showDate = showDate,
            showSolarBadge = showSolarBadge,
            showHeaderActions = showHeaderActions,
            showWidgets = showWidgets,
            showMostUsedApps = showMostUsedApps,
            showMostUsedHeader = showMostUsedHeader,
            showSearchBar = showSearchBar,
            showSwipeUpHint = showSwipeUpHint
        )
        _state.update { it.copy(homeScreenElements = elementsConfig) }
    }

    private fun saveHomeScreenElements(config: HomeScreenElementsConfig) {
        prefs.edit()
            .putBoolean("pref_elem_clock", config.showClock)
            .putBoolean("pref_elem_date", config.showDate)
            .putBoolean("pref_elem_solar_badge", config.showSolarBadge)
            .putBoolean("pref_elem_header_actions", config.showHeaderActions)
            .putBoolean("pref_elem_widgets", config.showWidgets)
            .putBoolean("pref_elem_most_used_apps", config.showMostUsedApps)
            .putBoolean("pref_elem_most_used_header", config.showMostUsedHeader)
            .putBoolean("pref_elem_search_bar", config.showSearchBar)
            .putBoolean("pref_elem_swipe_hint", config.showSwipeUpHint)
            .apply()
    }

    fun setHomeScreenElementVisibility(elementKey: String, isVisible: Boolean) {
        val current = _state.value.homeScreenElements
        val updated = when (elementKey) {
            "clock" -> current.copy(showClock = isVisible)
            "date" -> current.copy(showDate = isVisible)
            "solar_badge" -> current.copy(showSolarBadge = isVisible)
            "header_actions" -> current.copy(showHeaderActions = isVisible)
            "widgets" -> current.copy(showWidgets = isVisible)
            "most_used_apps" -> current.copy(showMostUsedApps = isVisible)
            "most_used_header" -> current.copy(showMostUsedHeader = isVisible)
            "search_bar" -> current.copy(showSearchBar = isVisible)
            "swipe_hint" -> current.copy(showSwipeUpHint = isVisible)
            else -> current
        }
        saveHomeScreenElements(updated)
        _state.update { it.copy(homeScreenElements = updated) }
    }

    fun applyHomeScreenPreset(preset: HomeScreenPreset) {
        saveHomeScreenElements(preset.config)
        _state.update { it.copy(homeScreenElements = preset.config) }
    }

    fun resetHomeScreenElements() {
        val standard = HomeScreenPreset.FULL.config
        saveHomeScreenElements(standard)
        _state.update { it.copy(homeScreenElements = standard) }
    }

    private fun loadGesturesPreferences() {
        val gesturesEnabled = prefs.getBoolean("pref_gestures_enabled", true)
        val hapticEnabled = prefs.getBoolean("pref_gestures_haptic", true)

        val doubleTapName = prefs.getString("pref_gesture_double_tap", GestureAction.LOCK_SCREEN.name) ?: GestureAction.LOCK_SCREEN.name
        val swipeDownName = prefs.getString("pref_gesture_swipe_down", GestureAction.OPEN_NOTIFICATIONS.name) ?: GestureAction.OPEN_NOTIFICATIONS.name
        val swipeUpName = prefs.getString("pref_gesture_swipe_up", GestureAction.OPEN_APP_DRAWER.name) ?: GestureAction.OPEN_APP_DRAWER.name
        val longPressName = prefs.getString("pref_gesture_long_press", GestureAction.OPEN_SETTINGS.name) ?: GestureAction.OPEN_SETTINGS.name
        val twoFingerDownName = prefs.getString("pref_gesture_two_finger_down", GestureAction.OPEN_QUICK_SETTINGS.name) ?: GestureAction.OPEN_QUICK_SETTINGS.name

        val sensitivityName = prefs.getString("pref_gesture_sensitivity", GestureSensitivity.MEDIUM.name) ?: GestureSensitivity.MEDIUM.name

        val doubleTapAction = try { GestureAction.valueOf(doubleTapName) } catch (e: Exception) { GestureAction.LOCK_SCREEN }
        val swipeDownAction = try { GestureAction.valueOf(swipeDownName) } catch (e: Exception) { GestureAction.OPEN_NOTIFICATIONS }
        val swipeUpAction = try { GestureAction.valueOf(swipeUpName) } catch (e: Exception) { GestureAction.OPEN_APP_DRAWER }
        val longPressAction = try { GestureAction.valueOf(longPressName) } catch (e: Exception) { GestureAction.OPEN_SETTINGS }
        val twoFingerDownAction = try { GestureAction.valueOf(twoFingerDownName) } catch (e: Exception) { GestureAction.OPEN_QUICK_SETTINGS }
        val sensitivity = try { GestureSensitivity.valueOf(sensitivityName) } catch (e: Exception) { GestureSensitivity.MEDIUM }

        val config = GesturesConfig(
            doubleTapAction = doubleTapAction,
            swipeDownAction = swipeDownAction,
            swipeUpAction = swipeUpAction,
            longPressAction = longPressAction,
            twoFingerSwipeDownAction = twoFingerDownAction,
            hapticFeedbackEnabled = hapticEnabled,
            sensitivity = sensitivity,
            gesturesEnabled = gesturesEnabled
        )
        _state.update { it.copy(gesturesConfig = config) }
    }

    private fun saveGesturesConfig(config: GesturesConfig) {
        prefs.edit()
            .putBoolean("pref_gestures_enabled", config.gesturesEnabled)
            .putBoolean("pref_gestures_haptic", config.hapticFeedbackEnabled)
            .putString("pref_gesture_double_tap", config.doubleTapAction.name)
            .putString("pref_gesture_swipe_down", config.swipeDownAction.name)
            .putString("pref_gesture_swipe_up", config.swipeUpAction.name)
            .putString("pref_gesture_long_press", config.longPressAction.name)
            .putString("pref_gesture_two_finger_down", config.twoFingerSwipeDownAction.name)
            .putString("pref_gesture_sensitivity", config.sensitivity.name)
            .apply()
    }

    fun setGestureAction(type: HomeGestureType, action: GestureAction) {
        val updated = _state.value.gesturesConfig.withActionFor(type, action)
        saveGesturesConfig(updated)
        _state.update { it.copy(gesturesConfig = updated) }
    }

    fun setGestureSensitivity(sensitivity: GestureSensitivity) {
        val updated = _state.value.gesturesConfig.copy(sensitivity = sensitivity)
        saveGesturesConfig(updated)
        _state.update { it.copy(gesturesConfig = updated) }
    }

    fun toggleGestureHaptics() {
        val current = _state.value.gesturesConfig
        val updated = current.copy(hapticFeedbackEnabled = !current.hapticFeedbackEnabled)
        saveGesturesConfig(updated)
        _state.update { it.copy(gesturesConfig = updated) }
    }

    fun toggleGesturesEnabled() {
        val current = _state.value.gesturesConfig
        val updated = current.copy(gesturesEnabled = !current.gesturesEnabled)
        saveGesturesConfig(updated)
        _state.update { it.copy(gesturesConfig = updated) }
    }

    fun resetGesturesToDefaults() {
        val defaults = GesturesConfig()
        saveGesturesConfig(defaults)
        _state.update { it.copy(gesturesConfig = defaults) }
    }
}
