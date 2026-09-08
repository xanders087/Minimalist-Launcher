package com.example.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.LauncherViewModel
import com.example.ui.theme.LauncherTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetManagerDialog(
    viewModel: LauncherViewModel,
    onDismissRequest: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val accent = state.accentTheme
    val colors = LauncherTheme.colors
    var selectedTab by remember { mutableStateOf(0) } // 0: Pin / Unpin, 1: Weather Setup, 2: Calendar Events, 3: Daily Focus

    var newEventTitle by remember { mutableStateOf("") }
    var newEventTime by remember { mutableStateOf("") }
    var newEventLocation by remember { mutableStateOf("") }
    var showAddEventForm by remember { mutableStateOf(false) }

    var customCityName by remember { mutableStateOf(state.weatherData.city) }
    var focusTextState by remember { mutableStateOf(state.focusGoal.goalText) }

    val cityPresets = listOf(
        Triple("San Francisco", 68, "Partly Cloudy"),
        Triple("New York", 75, "Sunny"),
        Triple("London", 61, "Rain Showers"),
        Triple("Tokyo", 79, "Clear Sky"),
        Triple("Paris", 66, "Mild Breeze"),
        Triple("Seattle", 59, "Light Rain"),
        Triple("Berlin", 64, "Overcast")
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("widget_manager_dialog"),
        containerColor = colors.surface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(accent.container, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Widgets,
                            contentDescription = "Widgets",
                            tint = accent.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Home Widgets",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Pin essential info to home screen",
                            fontSize = 11.sp,
                            color = colors.textMuted
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Tab Navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val tabs = listOf("Widgets", "Weather", "Calendar", "Focus")
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) accent.primary else colors.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = index }
                                .testTag("tab_widget_mgr_$index")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) colors.accentOnPrimary else colors.textSecondary
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = colors.divider)

                // Content depending on selected tab
                when (selectedTab) {
                    0 -> {
                        // Pin / Unpin Widgets List
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(WidgetType.values()) { widgetType ->
                                val isPinned = state.pinnedWidgets.firstOrNull { it.type == widgetType }?.isPinned == true

                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isPinned) accent.container.copy(alpha = if (colors.isDark) 0.35f else 0.5f) else colors.cardBackground,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isPinned) accent.primary.copy(alpha = 0.4f) else colors.cardBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.toggleWidgetPinned(widgetType) }
                                        .testTag("toggle_widget_${widgetType.name}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = widgetType.displayName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = colors.textPrimary
                                            )
                                            Text(
                                                text = widgetType.description,
                                                fontSize = 11.sp,
                                                color = colors.textMuted,
                                                lineHeight = 14.sp
                                            )
                                        }

                                        Switch(
                                            checked = isPinned,
                                            onCheckedChange = { viewModel.setWidgetPinned(widgetType, it) },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = accent.primary,
                                                uncheckedThumbColor = colors.textMuted,
                                                uncheckedTrackColor = colors.divider
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Weather Configuration
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                // Unit Switch
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(colors.surfaceVariant)
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Thermostat,
                                            contentDescription = null,
                                            tint = accent.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Temperature Unit",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = colors.textPrimary
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (!state.weatherData.isCelsius) accent.primary else colors.divider,
                                            modifier = Modifier.clickable {
                                                if (state.weatherData.isCelsius) viewModel.toggleWeatherUnit()
                                            }
                                        ) {
                                            Text(
                                                text = "°F",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (!state.weatherData.isCelsius) colors.accentOnPrimary else colors.textSecondary,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (state.weatherData.isCelsius) accent.primary else colors.divider,
                                            modifier = Modifier.clickable {
                                                if (!state.weatherData.isCelsius) viewModel.toggleWeatherUnit()
                                            }
                                        ) {
                                            Text(
                                                text = "°C",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (state.weatherData.isCelsius) colors.accentOnPrimary else colors.textSecondary,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                Text(
                                    text = "SELECT CITY PRESET",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = colors.textMuted
                                )
                            }

                            items(cityPresets) { (cityName, tempF, condition) ->
                                val isCurrentCity = state.weatherData.city == cityName

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isCurrentCity) accent.container else colors.cardBackground,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isCurrentCity) accent.primary else colors.cardBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            customCityName = cityName
                                            viewModel.updateWeather(
                                                city = cityName,
                                                tempF = tempF,
                                                condition = condition,
                                                highF = tempF + 5,
                                                lowF = tempF - 10
                                            )
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = cityName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = colors.textPrimary
                                            )
                                            Text(
                                                text = condition,
                                                fontSize = 11.sp,
                                                color = colors.textMuted
                                            )
                                        }

                                        Text(
                                            text = if (state.weatherData.isCelsius) "${((tempF - 32) * 5 / 9)}°C" else "$tempF°F",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (isCurrentCity) accent.primary else colors.textPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Calendar Agenda Management
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "UPCOMING AGENDA",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = colors.textMuted
                                    )

                                    TextButton(
                                        onClick = { showAddEventForm = !showAddEventForm },
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add Event",
                                            tint = accent.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = if (showAddEventForm) "Cancel" else "New Event",
                                            color = accent.primary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            if (showAddEventForm) {
                                item {
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = colors.surfaceVariant,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, accent.primary.copy(alpha = 0.5f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = newEventTitle,
                                                onValueChange = { newEventTitle = it },
                                                label = { Text("Event Title", color = colors.textMuted) },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = accent.primary,
                                                    unfocusedBorderColor = colors.divider,
                                                    focusedTextColor = colors.textPrimary,
                                                    unfocusedTextColor = colors.textPrimary
                                                ),
                                                singleLine = true
                                            )
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                OutlinedTextField(
                                                    value = newEventTime,
                                                    onValueChange = { newEventTime = it },
                                                    label = { Text("Time (e.g. 03:00 PM)", color = colors.textMuted) },
                                                    modifier = Modifier.weight(1f),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor = accent.primary,
                                                        unfocusedBorderColor = colors.divider,
                                                        focusedTextColor = colors.textPrimary,
                                                        unfocusedTextColor = colors.textPrimary
                                                    ),
                                                    singleLine = true
                                                )
                                                OutlinedTextField(
                                                    value = newEventLocation,
                                                    onValueChange = { newEventLocation = it },
                                                    label = { Text("Location", color = colors.textMuted) },
                                                    modifier = Modifier.weight(1f),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor = accent.primary,
                                                        unfocusedBorderColor = colors.divider,
                                                        focusedTextColor = colors.textPrimary,
                                                        unfocusedTextColor = colors.textPrimary
                                                    ),
                                                    singleLine = true
                                                )
                                            }
                                            Button(
                                                onClick = {
                                                    if (newEventTitle.isNotBlank()) {
                                                        val t = if (newEventTime.isNotBlank()) newEventTime else "Today"
                                                        viewModel.addCalendarEvent(newEventTitle, t, newEventLocation)
                                                        newEventTitle = ""
                                                        newEventTime = ""
                                                        newEventLocation = ""
                                                        showAddEventForm = false
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = accent.primary),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text("Add to Widget Agenda", color = Color.White, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }

                            if (state.calendarEvents.isEmpty()) {
                                item {
                                    Text(
                                        text = "No upcoming events scheduled.",
                                        fontSize = 12.sp,
                                        color = colors.textMuted,
                                        modifier = Modifier.padding(vertical = 12.dp)
                                    )
                                }
                            } else {
                                items(state.calendarEvents, key = { it.id }) { event ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = colors.cardBackground,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = event.title,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = colors.textPrimary
                                                )
                                                Text(
                                                    text = "${event.time} • ${event.location ?: "Meeting"}",
                                                    fontSize = 11.sp,
                                                    color = colors.textMuted
                                                )
                                            }

                                            IconButton(
                                                onClick = { viewModel.removeCalendarEvent(event.id) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete",
                                                    tint = colors.dangerRed,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    3 -> {
                        // Daily Focus Setup
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "DAILY INTENTION / GOAL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = colors.textMuted
                            )

                            OutlinedTextField(
                                value = focusTextState,
                                onValueChange = {
                                    focusTextState = it
                                    viewModel.setFocusGoalText(it)
                                },
                                label = { Text("Today's Focus", color = colors.textMuted) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = accent.primary,
                                    unfocusedBorderColor = colors.divider,
                                    focusedTextColor = colors.textPrimary,
                                    unfocusedTextColor = colors.textPrimary
                                ),
                                minLines = 2
                            )

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = accent.container,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Current Streak: ${state.focusGoal.streakDays} days",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = accent.primary
                                    )
                                    Button(
                                        onClick = { viewModel.toggleFocusGoal() },
                                        colors = ButtonDefaults.buttonColors(containerColor = accent.primary),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (state.focusGoal.isCompleted) "Completed ✓" else "Mark Complete",
                                            fontSize = 11.sp,
                                            color = colors.accentOnPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(containerColor = accent.primary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Done", fontWeight = FontWeight.Bold, color = colors.accentOnPrimary)
            }
        }
    )
}
