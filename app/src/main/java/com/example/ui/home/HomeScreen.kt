package com.example.ui.home

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.LauncherState
import com.example.LauncherViewModel
import com.example.R
import com.example.ui.glance.GlanceScreen
import com.example.ui.presentation.LauncherScreen
import com.example.ui.settings.LauncherSettingsScreen
import com.example.ui.settings.SettingsDialog
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: LauncherViewModel) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    
    var currentTab by remember { mutableStateOf("Surface") }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Resolve Font Family from Typography choice
    val activeFontFamily = when (state.typographyChoice) {
        AethericFontFamilyChoice.MONO -> FontFamily.Monospace
        AethericFontFamilyChoice.SERIF -> FontFamily.Serif
        else -> FontFamily.SansSerif
    }

    // Handle back button
    BackHandler(enabled = currentTab != "Surface" || showSettingsDialog) {
        if (showSettingsDialog) showSettingsDialog = false
        else if (currentTab != "Surface") currentTab = "Surface"
    }

    if (showSettingsDialog) {
        SettingsDialog(
            viewModel = viewModel,
            onDismissRequest = { showSettingsDialog = false },
            onOpenColorPicker = {},
            onOpenWidgetsManager = {},
            onOpenWallpaperManager = {},
            onOpenElementsManager = {},
            onOpenGesturesManager = {}
        )
    }

    Scaffold(
        containerColor = LauncherTheme.colors.background,
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Persistent Bottom Navigation
            Surface(
                color = LauncherTheme.colors.surfaceElevated.copy(alpha = 0.9f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    HorizontalDivider(color = LauncherTheme.colors.divider.copy(alpha = 0.3f), thickness = 1.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .height(56.dp)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NavItem(
                            icon = Icons.Outlined.BubbleChart,
                            label = stringResource(R.string.tab_surface),
                            isSelected = currentTab == "Surface",
                            fontFamily = activeFontFamily,
                            onClick = { currentTab = "Surface" }
                        )
                        NavItem(
                            icon = Icons.Outlined.GridView,
                            label = stringResource(R.string.tab_drawer),
                            isSelected = currentTab == "Drawer",
                            fontFamily = activeFontFamily,
                            onClick = { currentTab = "Drawer" }
                        )
                        NavItem(
                            icon = Icons.Outlined.Article,
                            label = stringResource(R.string.tab_glance),
                            isSelected = currentTab == "Glance",
                            fontFamily = activeFontFamily,
                            onClick = { currentTab = "Glance" }
                        )
                        NavItem(
                            icon = Icons.Outlined.Tune,
                            label = stringResource(R.string.tab_prefs),
                            isSelected = currentTab == "Prefs",
                            fontFamily = activeFontFamily,
                            onClick = { currentTab = "Prefs" }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = currentTab,
            animationSpec = tween(durationMillis = 300),
            label = "Tab Transition",
            modifier = Modifier.padding(innerPadding)
        ) { tab ->
            when (tab) {
                "Drawer" -> LauncherScreen(
                    viewModel = viewModel,
                    onAppSelected = { currentTab = "Surface" }
                )
                "Glance" -> GlanceScreen(
                    viewModel = viewModel,
                    fontFamily = activeFontFamily,
                    scale = state.appLabelTextScale
                )
                "Prefs" -> LauncherSettingsScreen(
                    viewModel = viewModel,
                    onBack = { currentTab = "Surface" }
                )
                else -> {
                    // "Surface" (Home) Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(top = 24.dp, bottom = 48.dp)
                    ) {
                        item {
                            AmbientBreathingGlowCard(context, state, activeFontFamily)
                        }
                        item {
                            IntentionBar(activeFontFamily)
                        }
                        if (state.homeScreenElements.showWidgets) {
                            item {
                                BentoContextTiles(activeFontFamily)
                            }
                        }
                        item {
                            PrimaryLauncherBubbles(state, activeFontFamily)
                        }
                        item {
                            DailyMicroQuote(activeFontFamily)
                        }
                        if (state.homeScreenElements.showSwipeUpHint) {
                            item {
                                SwipeUpCue(activeFontFamily, onClick = { currentTab = "Drawer" })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    fontFamily: FontFamily,
    onClick: () -> Unit
) {
    val color = if (isSelected) LauncherTheme.colors.accentPrimary else LauncherTheme.colors.textSecondary
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily), color = color)
    }
}

@Composable
fun AmbientBreathingGlowCard(context: Context, state: LauncherState, fontFamily: FontFamily) {
    val colors = LauncherTheme.colors
    val scale = state.appLabelTextScale
    Surface(
        color = colors.cardBackground.copy(alpha = 0.9f),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, colors.cardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Meta Bar (controlled by showHeaderActions)
            if (state.homeScreenElements.showHeaderActions) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        color = colors.surfaceVariant,
                        shape = CircleShape,
                        border = BorderStroke(1.dp, colors.divider)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(modifier = Modifier.size(6.dp).background(colors.accentPrimary, CircleShape))
                            Text(
                                stringResource(R.string.mindful_space),
                                style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily, fontSize = (10 * scale).sp),
                                color = colors.accentPrimary
                            )
                        }
                    }

                    Surface(
                        color = colors.surfaceVariant,
                        shape = CircleShape,
                        border = BorderStroke(1.dp, colors.divider)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Outlined.Spa, contentDescription = null, tint = colors.accentPrimary, modifier = Modifier.size(14.dp))
                            Text(
                                stringResource(R.string.zen_active),
                                style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily, fontSize = (10 * scale).sp),
                                color = colors.textPrimary
                            )
                        }
                    }
                }
            }

            // Time & Date Stack
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    if (state.homeScreenElements.showClock) {
                        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                            val amPmFormat = SimpleDateFormat("a", Locale.getDefault())
                            var currentTime by remember { mutableStateOf(Date()) }
                            LaunchedEffect(Unit) {
                                while (true) {
                                    currentTime = Date()
                                    delay(1000)
                                }
                            }
                            Text(
                                text = timeFormat.format(currentTime),
                                style = WarmMinimalistTypography.displayClockMobile.copy(fontFamily = fontFamily, fontSize = (48 * scale).sp),
                                color = colors.textPrimary,
                                modifier = Modifier.clickable {
                                    try { context.startActivity(Intent(AlarmClock.ACTION_SHOW_ALARMS)) } catch (e: Exception) {}
                                }
                            )
                            Text(
                                text = amPmFormat.format(currentTime),
                                style = WarmMinimalistTypography.labelMd.copy(fontFamily = fontFamily, fontSize = (11 * scale).sp),
                                color = colors.textSecondary
                            )
                        }
                    }

                    // Weather Pill
                    Surface(
                        color = colors.surfaceVariant,
                        shape = CircleShape,
                        border = BorderStroke(1.dp, colors.divider)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Outlined.WbCloudy, contentDescription = null, tint = colors.accentPrimary, modifier = Modifier.size(18.dp))
                            Column {
                                Text(state.weatherData.displayTemp, style = WarmMinimalistTypography.labelLg.copy(fontFamily = fontFamily, fontSize = (13 * scale).sp), color = colors.textPrimary)
                                Text(state.weatherData.condition, style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily, fontSize = (10 * scale).sp), color = colors.textSecondary)
                            }
                        }
                    }
                }

                if (state.homeScreenElements.showDate) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val dateFormat = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
                        Text(
                            text = dateFormat.format(Date()),
                            style = WarmMinimalistTypography.headlineSm.copy(fontFamily = fontFamily, fontSize = (18 * scale).sp),
                            color = colors.textSecondary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Outlined.Bolt, contentDescription = null, tint = colors.accentPrimary, modifier = Modifier.size(16.dp))
                            Text("${state.batteryData.batteryPct}%", style = WarmMinimalistTypography.labelMd.copy(fontFamily = fontFamily, fontSize = (11 * scale).sp), color = colors.textPrimary)
                        }
                    }
                }
            }

            // Claude Micro-Note
            HorizontalDivider(color = colors.divider.copy(alpha = 0.5f), thickness = 1.dp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(Icons.Outlined.Lightbulb, contentDescription = null, tint = colors.accentPrimary, modifier = Modifier.size(16.dp))
                Text("Next: Design Sync in 35 mins", style = WarmMinimalistTypography.bodySm.copy(fontFamily = fontFamily, fontSize = (12 * scale).sp), color = colors.textSecondary)
            }
        }
    }
}

@Composable
fun IntentionBar(fontFamily: FontFamily) {
    val colors = LauncherTheme.colors
    var intention by remember { mutableStateOf("") }
    Surface(
        color = colors.cardBackground.copy(alpha = 0.9f),
        shape = CircleShape,
        border = BorderStroke(1.dp, colors.cardBorder),
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = colors.accentPrimary, modifier = Modifier.size(20.dp))
                TextField(
                    value = intention,
                    onValueChange = { intention = it },
                    placeholder = { Text(stringResource(R.string.intention_placeholder), style = WarmMinimalistTypography.bodyMd.copy(fontFamily = fontFamily), color = colors.textSecondary) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = WarmMinimalistTypography.bodyMd.copy(fontFamily = fontFamily, color = colors.textPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Box(
                modifier = Modifier.size(32.dp).background(colors.accentPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowUpward, contentDescription = "Send", tint = colors.accentOnPrimary, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun BentoContextTiles(fontFamily: FontFamily) {
    val colors = LauncherTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Screen Time Tile
        Surface(
            color = colors.cardBackground.copy(alpha = 0.9f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, colors.cardBorder),
            modifier = Modifier.weight(1f).height(120.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.screen_peace), style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily), color = colors.textSecondary)
                    Icon(Icons.Outlined.HourglassBottom, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(16.dp))
                }
                Column {
                    Text(stringResource(R.string.screen_peace_stat), style = WarmMinimalistTypography.headlineMd.copy(fontFamily = fontFamily), color = colors.textPrimary)
                    Text(stringResource(R.string.screen_peace_vs), style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily), color = colors.textSecondary)
                }
                LinearProgressIndicator(
                    progress = 0.28f,
                    color = colors.accentPrimary,
                    trackColor = colors.surfaceVariant,
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
                )
            }
        }

        // Soundscape Tile
        Surface(
            color = colors.cardBackground.copy(alpha = 0.9f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, colors.cardBorder),
            modifier = Modifier.weight(1f).height(120.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.soundscape), style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily), color = colors.textSecondary)
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = colors.accentPrimary, modifier = Modifier.size(18.dp))
                }
                Column {
                    Text(stringResource(R.string.soundscape_title), style = WarmMinimalistTypography.headlineSm.copy(fontFamily = fontFamily), color = colors.textPrimary)
                    Text(stringResource(R.string.soundscape_artist), style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily), color = colors.textSecondary)
                }
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.height(12.dp)) {
                    listOf(8.dp, 12.dp, 6.dp, 10.dp, 4.dp).forEach { h ->
                        Box(modifier = Modifier.width(4.dp).height(h).background(colors.accentPrimary, CircleShape))
                    }
                }
            }
        }
    }
}

@Composable
fun PrimaryLauncherBubbles(state: LauncherState, fontFamily: FontFamily) {
    val colors = LauncherTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.essentials_title), style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily), color = colors.textSecondary)
            Text(stringResource(R.string.essentials_count), style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily), color = colors.textSecondary)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AppBubble(icon = Icons.Outlined.Call, label = stringResource(R.string.app_phone), showLabel = state.homeScreenElements.showMostUsedHeader, state = state, fontFamily = fontFamily)
            AppBubble(icon = Icons.Outlined.ChatBubbleOutline, label = stringResource(R.string.app_chat), hasNotification = true, showLabel = state.homeScreenElements.showMostUsedHeader, state = state, fontFamily = fontFamily)
            AppBubble(icon = Icons.Outlined.Public, label = stringResource(R.string.app_browser), showLabel = state.homeScreenElements.showMostUsedHeader, state = state, fontFamily = fontFamily)
            AppBubble(icon = Icons.Outlined.EditNote, label = stringResource(R.string.app_reflect), showLabel = state.homeScreenElements.showMostUsedHeader, state = state, fontFamily = fontFamily)
            AppBubble(icon = Icons.Outlined.PhotoCamera, label = stringResource(R.string.app_lens), showLabel = state.homeScreenElements.showMostUsedHeader, state = state, fontFamily = fontFamily)
        }
    }
}

@Composable
fun AppBubble(
    icon: ImageVector,
    label: String,
    hasNotification: Boolean = false,
    showLabel: Boolean = true,
    state: LauncherState,
    fontFamily: FontFamily
) {
    val colors = LauncherTheme.colors
    val scale = state.appLabelTextScale
    val isMono = state.isZeroDistractions

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(56.dp)
                .background(colors.surfaceVariant, CircleShape)
                .border(1.dp, colors.divider.copy(alpha = 0.4f), CircleShape)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isMono) colors.textPrimary else colors.accentPrimary,
                modifier = Modifier.size(24.dp)
            )
            if (hasNotification && state.wellbeingConfig.isStrictFocusEnabled.not()) {
                Box(modifier = Modifier.size(8.dp).background(colors.accentPrimary, CircleShape).align(Alignment.TopEnd).offset(x = (-8).dp, y = 8.dp))
            }
        }
        if (showLabel) {
            Text(
                text = label,
                style = WarmMinimalistTypography.labelMd.copy(fontFamily = fontFamily, fontSize = (11 * scale).sp),
                color = colors.textPrimary
            )
        }
    }
}

@Composable
fun DailyMicroQuote(fontFamily: FontFamily) {
    val colors = LauncherTheme.colors
    Surface(
        color = colors.cardBackground.copy(alpha = 0.9f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, colors.cardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(colors.surfaceVariant, CircleShape).border(1.dp, colors.divider, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.FilterVintage, contentDescription = null, tint = colors.accentPrimary, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(stringResource(R.string.quote_text), style = WarmMinimalistTypography.bodySm.copy(fontFamily = fontFamily), color = colors.textPrimary)
                Text(stringResource(R.string.quote_author), style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily), color = colors.textSecondary)
            }
        }
    }
}

@Composable
fun SwipeUpCue(fontFamily: FontFamily, onClick: () -> Unit) {
    val colors = LauncherTheme.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 16.dp)
    ) {
        Icon(Icons.Outlined.KeyboardArrowUp, contentDescription = "Swipe up", tint = colors.textSecondary, modifier = Modifier.size(18.dp))
        Text(stringResource(R.string.swipe_up_hint), style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily), color = colors.textSecondary)
    }
}
