package com.example.ui.settings

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.LauncherViewModel
import com.example.R
import com.example.ui.gestures.GestureAction
import com.example.ui.gestures.HomeGestureType
import com.example.ui.theme.*

/**
 * Pantalla completa de Preferencias del Launcher (Launcher Preferences / Prefs)
 * totalmente reactiva, internacionalizada y adaptada al idioma y tema del teléfono.
 */
@Composable
fun LauncherSettingsScreen(
    viewModel: LauncherViewModel,
    onBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val colors = LauncherTheme.colors

    var selectedGrid by remember { mutableStateOf("4 × 5") }
    var breathingRoom by remember { mutableFloatStateOf(2f) }

    val activeFontFamily = when (state.typographyChoice) {
        AethericFontFamilyChoice.MONO -> FontFamily.Monospace
        AethericFontFamilyChoice.SERIF -> FontFamily.Serif
        else -> FontFamily.SansSerif
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
    ) {
        // Interactive View Header
        Surface(
            color = colors.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(colors.chipBackground, CircleShape)
                            .clip(CircleShape)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back",
                            tint = colors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = stringResource(R.string.prefs_device_title),
                            style = WarmMinimalistTypography.labelSm.copy(fontFamily = activeFontFamily),
                            color = colors.accentPrimary
                        )
                        Text(
                            text = stringResource(R.string.prefs_screen_title),
                            style = WarmMinimalistTypography.headlineSm.copy(fontFamily = activeFontFamily),
                            color = colors.textPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Settings",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Scrollable Preferences List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Mindful Surface Banner Card
            item {
                Surface(
                    color = colors.cardBackground,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, colors.cardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(colors.surfaceVariant, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Spa,
                                contentDescription = null,
                                tint = colors.accentPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.mindful_surface_title),
                                    style = WarmMinimalistTypography.labelMd.copy(fontFamily = activeFontFamily),
                                    color = colors.textPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    color = colors.surfaceElevated,
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = if (colors.isDark) "pOLED ACTIVE" else "CANVAS ACTIVE",
                                        style = WarmMinimalistTypography.labelSm.copy(fontFamily = activeFontFamily),
                                        color = colors.accentPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = stringResource(R.string.mindful_surface_desc),
                                style = WarmMinimalistTypography.bodySm.copy(fontFamily = activeFontFamily),
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }

            // Group 1: DESKTOP & LAYOUT
            item {
                PreferenceGroup(
                    title = stringResource(R.string.group_desktop_layout),
                    tag = stringResource(R.string.group_ergonomics),
                    fontFamily = activeFontFamily
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(stringResource(R.string.setting_workspace_grid), style = WarmMinimalistTypography.bodyMd.copy(fontFamily = activeFontFamily), color = colors.textPrimary, fontWeight = FontWeight.Medium)
                                Text(stringResource(R.string.setting_workspace_grid_desc), style = WarmMinimalistTypography.bodySm.copy(fontFamily = activeFontFamily), color = colors.textSecondary)
                            }
                            Row(
                                modifier = Modifier
                                    .background(colors.surfaceElevated, CircleShape)
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("4 × 5", "4 × 6").forEach { option ->
                                    val isSel = selectedGrid == option
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isSel) colors.accentPrimary else Color.Transparent,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .clickable { selectedGrid = option }
                                    ) {
                                        Text(
                                            text = option,
                                            style = WarmMinimalistTypography.labelMd.copy(fontFamily = activeFontFamily),
                                            color = if (isSel) colors.accentOnPrimary else colors.textSecondary,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = colors.divider.copy(alpha = 0.3f))

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            val currentPercent = (state.appLabelTextScale * 100f).toInt()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(stringResource(R.string.setting_icon_size), style = WarmMinimalistTypography.bodyMd.copy(fontFamily = activeFontFamily), color = colors.textPrimary)
                                Text("$currentPercent%", style = WarmMinimalistTypography.labelMd.copy(fontFamily = activeFontFamily), color = colors.accentPrimary, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = state.appLabelTextScale * 100f,
                                onValueChange = { viewModel.setAppLabelTextScale(it / 100f) },
                                valueRange = 80f..120f,
                                colors = SliderDefaults.colors(
                                    thumbColor = colors.accentPrimary,
                                    activeTrackColor = colors.accentPrimary,
                                    inactiveTrackColor = colors.surfaceVariant
                                )
                            )
                        }

                        HorizontalDivider(color = colors.divider.copy(alpha = 0.3f))

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(stringResource(R.string.setting_widget_breathing_room), style = WarmMinimalistTypography.bodyMd.copy(fontFamily = activeFontFamily), color = colors.textPrimary)
                                val roomLabel = when (breathingRoom.toInt()) {
                                    1 -> "Compact (8dp)"
                                    3 -> "Spaced (24dp)"
                                    else -> "Relaxed (16dp)"
                                }
                                Text(roomLabel, style = WarmMinimalistTypography.labelMd.copy(fontFamily = activeFontFamily), color = colors.textSecondary)
                            }
                            Slider(
                                value = breathingRoom,
                                onValueChange = { breathingRoom = it },
                                valueRange = 1f..3f,
                                steps = 1,
                                colors = SliderDefaults.colors(
                                    thumbColor = colors.accentPrimary,
                                    activeTrackColor = colors.accentPrimary,
                                    inactiveTrackColor = colors.surfaceVariant
                                )
                            )
                        }

                        HorizontalDivider(color = colors.divider.copy(alpha = 0.3f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(stringResource(R.string.setting_show_status_bar), style = WarmMinimalistTypography.bodyMd.copy(fontFamily = activeFontFamily), color = colors.textPrimary)
                                Text(stringResource(R.string.setting_show_status_bar_desc), style = WarmMinimalistTypography.bodySm.copy(fontFamily = activeFontFamily), color = colors.textSecondary)
                            }
                            Switch(
                                checked = state.homeScreenElements.showHeaderActions,
                                onCheckedChange = { viewModel.setHomeScreenElementVisibility("header_actions", it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = colors.accentOnPrimary,
                                    checkedTrackColor = colors.accentPrimary
                                )
                            )
                        }
                    }
                }
            }

            // Group 2: APPEARANCE
            item {
                PreferenceGroup(
                    title = stringResource(R.string.group_appearance),
                    tag = stringResource(R.string.group_poled_tiers),
                    fontFamily = activeFontFamily
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Theme Mode Selector (Follow System, Always Light, Always Dark)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Theme Mode", style = WarmMinimalistTypography.bodyMd.copy(fontFamily = activeFontFamily), color = colors.textPrimary)
                            val modes = listOf(
                                Triple("Follow System", "Matches device system theme", ThemeMode.SYSTEM),
                                Triple("Always Light", "Crisp light theme", ThemeMode.ALWAYS_LIGHT),
                                Triple("Always Dark", "Soothing dark theme", ThemeMode.ALWAYS_DARK)
                            )
                            modes.forEach { (title, desc, mode) ->
                                val isSelected = state.themeMode == mode
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) colors.surfaceVariant else colors.chipBackground,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { viewModel.setThemeMode(mode) }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                                contentDescription = null,
                                                tint = if (isSelected) colors.accentPrimary else colors.textMuted,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Column {
                                                Text(
                                                    text = title,
                                                    style = WarmMinimalistTypography.bodyMd.copy(fontFamily = activeFontFamily),
                                                    color = colors.textPrimary,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    text = desc,
                                                    style = WarmMinimalistTypography.bodySm.copy(fontFamily = activeFontFamily),
                                                    color = colors.textSecondary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = colors.divider.copy(alpha = 0.3f))

                        // Surface Palette Selector
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(stringResource(R.string.setting_surface_palette), style = WarmMinimalistTypography.bodyMd.copy(fontFamily = activeFontFamily), color = colors.textPrimary)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val palettes = listOf(
                                    Triple(stringResource(R.string.palette_warm_ivory), Color(0xFFF4F1EA), AethericThemeVariant.GRIS),
                                    Triple(stringResource(R.string.palette_slate_stone), Color(0xFF3E4244), AethericThemeVariant.GRAFITO),
                                    Triple(stringResource(R.string.palette_deep_charcoal), Color(0xFF161514), AethericThemeVariant.OLED_PURO)
                                )
                                palettes.forEach { (name, color, variant) ->
                                    val isSelected = state.themeVariant == variant
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) colors.surfaceElevated else colors.surfaceVariant,
                                        border = if (isSelected) BorderStroke(1.dp, colors.accentPrimary) else null,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                viewModel.setThemeVariant(variant)
                                                viewModel.setPureBlack(variant == AethericThemeVariant.OLED_PURO)
                                            }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .background(color, CircleShape)
                                                    .border(1.dp, colors.divider, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = colors.accentPrimary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = name,
                                                style = WarmMinimalistTypography.labelSm.copy(fontFamily = activeFontFamily),
                                                color = if (isSelected) colors.textPrimary else colors.textSecondary,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = colors.divider.copy(alpha = 0.3f))

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            val currentBlurPx = (state.wallpaperConfig.dimmingAlpha * 40f).toInt()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(stringResource(R.string.setting_frosted_glass), style = WarmMinimalistTypography.bodyMd.copy(fontFamily = activeFontFamily), color = colors.textPrimary)
                                Text("${currentBlurPx}px", style = WarmMinimalistTypography.labelMd.copy(fontFamily = activeFontFamily), color = colors.accentPrimary, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = state.wallpaperConfig.dimmingAlpha * 40f,
                                onValueChange = { viewModel.setWallpaperDimming((it / 40f).coerceIn(0.15f, 0.75f)) },
                                valueRange = 0f..32f,
                                colors = SliderDefaults.colors(
                                    thumbColor = colors.accentPrimary,
                                    activeTrackColor = colors.accentPrimary,
                                    inactiveTrackColor = colors.surfaceVariant
                                )
                            )
                        }

                        HorizontalDivider(color = colors.divider.copy(alpha = 0.3f))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(stringResource(R.string.setting_typographic_voice), style = WarmMinimalistTypography.bodyMd.copy(fontFamily = activeFontFamily), color = colors.textPrimary)
                            val voices = listOf(
                                Triple("Plus Jakarta Sans", "Adaptive", AethericFontFamilyChoice.SANS),
                                Triple("Inter Editorial", "Classic", AethericFontFamilyChoice.SERIF),
                                Triple("Geist Technical", "Monospace", AethericFontFamilyChoice.MONO)
                            )
                            voices.forEach { (name, type, fontChoice) ->
                                val isSelected = state.typographyChoice == fontChoice
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) colors.surfaceVariant else colors.chipBackground,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { viewModel.setTypographyChoice(fontChoice) }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                                contentDescription = null,
                                                tint = if (isSelected) colors.accentPrimary else colors.textMuted,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = name,
                                                style = WarmMinimalistTypography.bodyMd.copy(fontFamily = activeFontFamily),
                                                color = colors.textPrimary
                                            )
                                        }
                                        Text(
                                            text = type.uppercase(),
                                            style = WarmMinimalistTypography.labelSm.copy(fontFamily = activeFontFamily),
                                            color = colors.textMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Group 3: TACTILE GESTURES
            item {
                PreferenceGroup(
                    title = stringResource(R.string.group_gestures),
                    tag = stringResource(R.string.group_edge_sense),
                    fontFamily = activeFontFamily
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        GestureRow(
                            icon = Icons.Outlined.SwipeUp,
                            title = stringResource(R.string.gesture_swipe_up),
                            desc = stringResource(R.string.gesture_swipe_up_desc),
                            actionBadge = state.gesturesConfig.swipeUpAction.title,
                            fontFamily = activeFontFamily,
                            onClick = { cycleGestureAction(viewModel, HomeGestureType.SWIPE_UP, state.gesturesConfig.swipeUpAction) }
                        )
                        HorizontalDivider(color = colors.divider.copy(alpha = 0.3f))
                        GestureRow(
                            icon = Icons.Outlined.TouchApp,
                            title = stringResource(R.string.gesture_double_tap),
                            desc = stringResource(R.string.gesture_double_tap_desc),
                            actionBadge = state.gesturesConfig.doubleTapAction.title,
                            fontFamily = activeFontFamily,
                            onClick = { cycleGestureAction(viewModel, HomeGestureType.DOUBLE_TAP, state.gesturesConfig.doubleTapAction) }
                        )
                        HorizontalDivider(color = colors.divider.copy(alpha = 0.3f))
                        GestureRow(
                            icon = Icons.Outlined.SwipeDown,
                            title = stringResource(R.string.gesture_swipe_down),
                            desc = stringResource(R.string.gesture_swipe_down_desc),
                            actionBadge = state.gesturesConfig.swipeDownAction.title,
                            fontFamily = activeFontFamily,
                            onClick = { cycleGestureAction(viewModel, HomeGestureType.SWIPE_DOWN, state.gesturesConfig.swipeDownAction) }
                        )
                    }
                }
            }

            // Group 4: ICONOGRAPHY & CLARITY
            item {
                PreferenceGroup(
                    title = stringResource(R.string.group_iconography),
                    tag = stringResource(R.string.group_visual_order),
                    fontFamily = activeFontFamily
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        ToggleRow(
                            title = stringResource(R.string.setting_monochromatic),
                            desc = stringResource(R.string.setting_monochromatic_desc),
                            checked = state.isZeroDistractions,
                            fontFamily = activeFontFamily,
                            onCheckedChange = { viewModel.toggleZeroDistractions() }
                        )
                        HorizontalDivider(color = colors.divider.copy(alpha = 0.3f))
                        ToggleRow(
                            title = stringResource(R.string.setting_hide_labels),
                            desc = stringResource(R.string.setting_hide_labels_desc),
                            checked = !state.homeScreenElements.showMostUsedHeader,
                            fontFamily = activeFontFamily,
                            onCheckedChange = { viewModel.setHomeScreenElementVisibility("most_used_header", !it) }
                        )
                        HorizontalDivider(color = colors.divider.copy(alpha = 0.3f))
                        ToggleRow(
                            title = stringResource(R.string.setting_suppress_dots),
                            desc = stringResource(R.string.setting_suppress_dots_desc),
                            checked = state.wellbeingConfig.isStrictFocusEnabled,
                            fontFamily = activeFontFamily,
                            onCheckedChange = { viewModel.toggleStrictFocus() }
                        )
                    }
                }
            }

            // Group 5: MINDFUL MODE
            item {
                PreferenceGroup(
                    title = stringResource(R.string.group_mindful_mode),
                    tag = stringResource(R.string.group_wellbeing),
                    fontFamily = activeFontFamily
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        ToggleRow(
                            title = stringResource(R.string.setting_usage_prompts),
                            desc = stringResource(R.string.setting_usage_prompts_desc),
                            checked = state.wellbeingConfig.isMindfulPauseEnabled,
                            fontFamily = activeFontFamily,
                            onCheckedChange = { viewModel.toggleMindfulPause() }
                        )
                        HorizontalDivider(color = colors.divider.copy(alpha = 0.3f))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = colors.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.toggleAutoGrayscale() }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(colors.surfaceElevated, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Outlined.Bedtime, contentDescription = null, tint = colors.accentPrimary, modifier = Modifier.size(18.dp))
                                    }
                                    Column {
                                        Text(stringResource(R.string.setting_evening_grayscale), style = WarmMinimalistTypography.labelMd.copy(fontFamily = activeFontFamily), color = colors.textPrimary, fontWeight = FontWeight.Bold)
                                        Text(stringResource(R.string.setting_evening_grayscale_desc), style = WarmMinimalistTypography.bodySm.copy(fontFamily = activeFontFamily), color = colors.textSecondary)
                                    }
                                }
                                Surface(
                                    shape = CircleShape,
                                    color = if (state.wellbeingConfig.isAutoGrayscaleEnabled) colors.accentPrimary else colors.chipBackground
                                ) {
                                    Text(
                                        text = if (state.wellbeingConfig.isAutoGrayscaleEnabled) "22:00 – 07:00" else "OFF",
                                        style = WarmMinimalistTypography.labelSm.copy(fontFamily = activeFontFamily),
                                        color = if (state.wellbeingConfig.isAutoGrayscaleEnabled) colors.accentOnPrimary else colors.textSecondary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Reset Action Button & Footer
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.resetAppLabelTextScale()
                            viewModel.resetHomeScreenElements()
                            viewModel.resetGesturesToDefaults()
                            viewModel.setPureBlack(false)
                            viewModel.setThemeVariant(AethericThemeVariant.GRIS)
                            viewModel.setTypographyChoice(AethericFontFamilyChoice.SANS)
                            viewModel.setThemeMode(ThemeMode.SYSTEM)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.surfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, tint = colors.accentPrimary, modifier = Modifier.size(18.dp))
                            Text(stringResource(R.string.btn_reset_defaults), style = WarmMinimalistTypography.labelLg.copy(fontFamily = activeFontFamily), color = colors.textPrimary)
                        }
                    }
                    Text(
                        text = stringResource(R.string.build_version_info),
                        style = WarmMinimalistTypography.bodySm.copy(fontFamily = activeFontFamily),
                        color = colors.textMuted
                    )
                }
            }
        }
    }
}

private fun cycleGestureAction(viewModel: LauncherViewModel, type: HomeGestureType, current: GestureAction) {
    val actions = GestureAction.entries
    val nextIdx = (actions.indexOf(current) + 1) % actions.size
    viewModel.setGestureAction(type, actions[nextIdx])
}

@Composable
private fun PreferenceGroup(
    title: String,
    tag: String,
    fontFamily: FontFamily,
    content: @Composable () -> Unit
) {
    val colors = LauncherTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily), color = colors.textSecondary, fontWeight = FontWeight.Bold)
            Text(tag.uppercase(), style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily), color = colors.textMuted)
        }
        Surface(
            color = colors.cardBackground,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, colors.cardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun GestureRow(
    icon: ImageVector,
    title: String,
    desc: String,
    actionBadge: String,
    fontFamily: FontFamily,
    onClick: () -> Unit = {}
) {
    val colors = LauncherTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(colors.surfaceVariant, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = colors.textPrimary, modifier = Modifier.size(18.dp))
            }
            Column {
                Text(title, style = WarmMinimalistTypography.bodyMd.copy(fontFamily = fontFamily), color = colors.textPrimary, fontWeight = FontWeight.Medium)
                Text(desc, style = WarmMinimalistTypography.bodySm.copy(fontFamily = fontFamily), color = colors.textSecondary)
            }
        }
        Surface(
            shape = CircleShape,
            color = colors.surfaceVariant
        ) {
            Text(
                text = actionBadge,
                style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily),
                color = colors.accentPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    desc: String,
    checked: Boolean,
    fontFamily: FontFamily,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = LauncherTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, style = WarmMinimalistTypography.bodyMd.copy(fontFamily = fontFamily), color = colors.textPrimary, fontWeight = FontWeight.Medium)
            Text(desc, style = WarmMinimalistTypography.bodySm.copy(fontFamily = fontFamily), color = colors.textSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.accentOnPrimary,
                checkedTrackColor = colors.accentPrimary
            )
        )
    }
}
