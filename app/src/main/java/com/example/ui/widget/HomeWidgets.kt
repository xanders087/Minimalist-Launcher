package com.example.ui.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.LauncherViewModel
import com.example.ui.theme.AccentTheme
import com.example.ui.theme.LauncherColorScheme
import com.example.ui.theme.LauncherTheme

@Composable
fun HomeWidgetsSection(
    viewModel: LauncherViewModel,
    onOpenWidgetManager: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val activeWidgets = state.activeWidgets
    val accent = state.accentTheme
    val colors = LauncherTheme.colors
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("home_widgets_section")
    ) {
        if (activeWidgets.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 0.dp)
            ) {
                items(activeWidgets, key = { it.type.name }) { widgetConfig ->
                    when (widgetConfig.type) {
                        WidgetType.WEATHER -> {
                            WeatherWidgetCard(
                                weatherData = state.weatherData,
                                accent = accent,
                                colors = colors,
                                onUnitToggle = { viewModel.toggleWeatherUnit() },
                                onCardClick = { launchWeatherApp(context) },
                                onLongClick = onOpenWidgetManager
                            )
                        }
                        WidgetType.CALENDAR -> {
                            CalendarWidgetCard(
                                events = state.calendarEvents,
                                accent = accent,
                                colors = colors,
                                onCardClick = { launchCalendarApp(context) },
                                onLongClick = onOpenWidgetManager
                            )
                        }
                        WidgetType.FOCUS_GOAL -> {
                            FocusGoalWidgetCard(
                                focusGoal = state.focusGoal,
                                accent = accent,
                                colors = colors,
                                onToggleCompleted = { viewModel.toggleFocusGoal() },
                                onLongClick = onOpenWidgetManager
                            )
                        }
                        WidgetType.BATTERY_SYSTEM -> {
                            BatterySystemWidgetCard(
                                batteryData = state.batteryData,
                                accent = accent,
                                colors = colors,
                                onCardClick = { viewModel.loadBatteryAndStorage() },
                                onLongClick = onOpenWidgetManager
                            )
                        }
                    }
                }
            }
        } else {
            // Empty state helper to quickly pin widgets
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colors.cardBackground.copy(alpha = 0.8f),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onOpenWidgetManager() }
                    .testTag("btn_empty_widgets_add")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Widgets,
                            contentDescription = "Pin Widgets",
                            tint = accent.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pin Weather, Calendar or System widgets",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.textSecondary
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = accent.container
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = accent.primary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Add",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = accent.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeatherWidgetCard(
    weatherData: WeatherData,
    accent: AccentTheme,
    colors: LauncherColorScheme,
    onUnitToggle: () -> Unit,
    onCardClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val weatherIcon = when {
        weatherData.condition.contains("Rain", ignoreCase = true) -> Icons.Default.Cloud
        weatherData.condition.contains("Cloud", ignoreCase = true) -> Icons.Default.Cloud
        weatherData.condition.contains("Night", ignoreCase = true) -> Icons.Default.NightsStay
        else -> Icons.Default.WbSunny
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = colors.cardBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
        shadowElevation = 0.5.dp,
        modifier = Modifier
            .width(210.dp)
            .clip(RoundedCornerShape(20.dp))
            .combinedClickable(
                onClick = onCardClick,
                onLongClick = onLongClick
            )
            .testTag("weather_widget_card")
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Header Row: City & Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = accent.primary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = weatherData.city,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(accent.container, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = weatherIcon,
                        contentDescription = weatherData.condition,
                        tint = accent.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Temperature & Condition
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = weatherData.displayTemp,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp,
                    color = colors.textPrimary
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colors.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onUnitToggle() }
                ) {
                    Text(
                        text = if (weatherData.isCelsius) "°C" else "°F",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = accent.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // High/Low & Condition description
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = weatherData.condition,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textSecondary
                )
                Text(
                    text = weatherData.displayHighLow,
                    fontSize = 10.sp,
                    color = colors.textMuted
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarWidgetCard(
    events: List<CalendarEvent>,
    accent: AccentTheme,
    colors: LauncherColorScheme,
    onCardClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val nextEvent = events.firstOrNull()

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = colors.cardBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
        shadowElevation = 0.5.dp,
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(20.dp))
            .combinedClickable(
                onClick = onCardClick,
                onLongClick = onLongClick
            )
            .testTag("calendar_widget_card")
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Calendar",
                        tint = accent.primary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "NEXT EVENT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = colors.textMuted
                    )
                }

                if (nextEvent != null) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = accent.container
                    ) {
                        Text(
                            text = nextEvent.time,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = accent.primary,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            if (nextEvent != null) {
                Text(
                    text = nextEvent.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (nextEvent.location != null) "📍 ${nextEvent.location}" else "Scheduled Agenda",
                    fontSize = 11.sp,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Text(
                    text = "No upcoming events",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textMuted
                )
                Text(
                    text = "Enjoy your free time!",
                    fontSize = 11.sp,
                    color = colors.textSecondary
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FocusGoalWidgetCard(
    focusGoal: FocusGoalData,
    accent: AccentTheme,
    colors: LauncherColorScheme,
    onToggleCompleted: () -> Unit,
    onLongClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = colors.cardBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
        shadowElevation = 0.5.dp,
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(20.dp))
            .combinedClickable(
                onClick = onToggleCompleted,
                onLongClick = onLongClick
            )
            .testTag("focus_goal_widget_card")
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Whatshot,
                        contentDescription = "Focus Goal",
                        tint = accent.primary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "TODAY'S INTENTION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = colors.textMuted
                    )
                }

                Icon(
                    imageVector = if (focusGoal.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (focusGoal.isCompleted) "Completed" else "Incomplete",
                    tint = if (focusGoal.isCompleted) accent.primary else colors.textMuted,
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = focusGoal.goalText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (focusGoal.isCompleted) colors.textMuted else colors.textPrimary,
                textDecoration = if (focusGoal.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = if (focusGoal.isCompleted) "✓ Completed (${focusGoal.streakDays}d streak)" else "Tap card when done",
                fontSize = 10.sp,
                color = if (focusGoal.isCompleted) accent.primary else colors.textMuted,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BatterySystemWidgetCard(
    batteryData: BatterySystemData,
    accent: AccentTheme,
    colors: LauncherColorScheme,
    onCardClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = colors.cardBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
        shadowElevation = 0.5.dp,
        modifier = Modifier
            .width(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .combinedClickable(
                onClick = onCardClick,
                onLongClick = onLongClick
            )
            .testTag("battery_widget_card")
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (batteryData.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                        contentDescription = "Battery",
                        tint = accent.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "BATTERY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = colors.textMuted
                    )
                }

                Text(
                    text = "${batteryData.batteryPct}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            }

            // Progress bar
            LinearProgressIndicator(
                progress = { (batteryData.batteryPct / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = accent.primary,
                trackColor = colors.divider
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (batteryData.isCharging) "Charging" else "Discharging",
                    fontSize = 10.sp,
                    color = accent.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = "Storage",
                        tint = colors.textMuted,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${batteryData.storageFreeGb} GB Free",
                        fontSize = 10.sp,
                        color = colors.textMuted
                    )
                }
            }
        }
    }
}

fun launchCalendarApp(context: Context) {
    try {
        val calendarIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("content://com.android.calendar/time")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(calendarIntent)
    } catch (e: Throwable) {
        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_CALENDAR)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e2: Throwable) {
            Toast.makeText(context, "Calendar", Toast.LENGTH_SHORT).show()
        }
    }
}

fun launchWeatherApp(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=weather")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Throwable) {
        Toast.makeText(context, "Weather details", Toast.LENGTH_SHORT).show()
    }
}
