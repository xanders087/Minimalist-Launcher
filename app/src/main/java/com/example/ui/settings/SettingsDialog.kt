package com.example.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.AppInfo
import com.example.LauncherViewModel
import com.example.ui.gestures.GestureSensitivity
import com.example.ui.home.getCategoryColor
import com.example.ui.theme.LauncherTheme
import com.example.ui.theme.SolarLocation
import com.example.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    viewModel: LauncherViewModel,
    onOpenColorPicker: () -> Unit,
    onOpenWidgetsManager: () -> Unit,
    onOpenWallpaperManager: () -> Unit = {},
    onOpenElementsManager: () -> Unit = {},
    onOpenGesturesManager: () -> Unit = {},
    onDismissRequest: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val colors = LauncherTheme.colors
    val accent = state.accentTheme
    var selectedSection by remember { mutableStateOf(0) } // 0: Dark & Solar Schedule, 1: Hidden Apps, 2: Info & Shortcuts
    var appFilterQuery by remember { mutableStateOf("") }
    var filterTab by remember { mutableStateOf(0) } // 0: All Apps, 1: Hidden Only, 2: Visible Only
    var showLocationSelector by remember { mutableStateOf(false) }

    val allApps = state.apps
    val hiddenSet = state.hiddenPackages
    val solar = state.solarTimes

    val filteredApps = remember(allApps, hiddenSet, appFilterQuery, filterTab) {
        allApps.filter { app ->
            val matchesQuery = app.label.contains(appFilterQuery, ignoreCase = true) ||
                    app.packageName.contains(appFilterQuery, ignoreCase = true) ||
                    app.category.contains(appFilterQuery, ignoreCase = true)
            val matchesTab = when (filterTab) {
                1 -> app.packageName in hiddenSet
                2 -> app.packageName !in hiddenSet
                else -> true
            }
            matchesQuery && matchesTab
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("settings_dialog"),
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
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = accent.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Launcher Settings",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Theme, scheduling & visibility",
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
                    .heightIn(max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 4 Section Tabs: Personalizar, Gestos, Modo Solar, Apps
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf(
                        Triple("Personalizar", Icons.Default.Palette, "tab_settings_personalize"),
                        Triple("Gestos", Icons.Default.Gesture, "tab_settings_gestures"),
                        Triple("Solar", Icons.Default.Nightlight, "tab_settings_theme_schedule"),
                        Triple("Apps (${hiddenSet.size})", Icons.Default.VisibilityOff, "tab_settings_hidden_apps")
                    )
                    tabs.forEachIndexed { index, (label, icon, tag) ->
                        val isSelected = selectedSection == index
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) accent.primary else colors.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedSection = index }
                                .testTag(tag)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 7.dp, horizontal = 2.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isSelected) accent.onPrimary else colors.textSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) accent.onPrimary else colors.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                when (selectedSection) {
                    0 -> {
                        // SECTION 0: Personalizar (Elementos, Fondos, Widgets, Colores, Texto)
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 360.dp)
                                .testTag("settings_personalize_scroll"),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Item 1: Home Screen Elements Manager
                            item {
                                val elemConfig = state.homeScreenElements
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = colors.cardBackground,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onDismissRequest()
                                            onOpenElementsManager()
                                        }
                                        .testTag("setting_item_elements_manager")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Dashboard,
                                                contentDescription = "Elementos de Pantalla",
                                                tint = accent.primary,
                                                modifier = Modifier.size(22.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = "Elementos de Pantalla Principal",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = colors.textPrimary
                                                )
                                                Text(
                                                    text = "${elemConfig.visibleElementsCount} de 9 componentes visibles (reloj, widgets, etc.)",
                                                    fontSize = 11.sp,
                                                    color = colors.textMuted,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                        Text(
                                            text = "Gestionar →",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = accent.primary
                                        )
                                    }
                                }
                            }

                            // Item 2: Wallpaper Manager
                            item {
                                val wp = state.wallpaperConfig
                                val wpSubtitle = when (wp.mode) {
                                    com.example.ui.wallpaper.WallpaperMode.SOLID -> "Fondo minimalista monocromático"
                                    com.example.ui.wallpaper.WallpaperMode.STATIC -> "Fondo estático: ${wp.currentTitle}"
                                    com.example.ui.wallpaper.WallpaperMode.DAILY -> "Fondo diario: ${wp.dailySource.title} • ${wp.currentTitle}"
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = colors.cardBackground,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onDismissRequest()
                                            onOpenWallpaperManager()
                                        }
                                        .testTag("setting_item_wallpaper_manager")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Wallpaper,
                                                contentDescription = "Fondo de Pantalla",
                                                tint = accent.primary,
                                                modifier = Modifier.size(22.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = "Fondo de Pantalla (Wallpaper)",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = colors.textPrimary
                                                )
                                                Text(
                                                    text = wpSubtitle,
                                                    fontSize = 11.sp,
                                                    color = colors.textMuted,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                        Text(
                                            text = "Configurar →",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = accent.primary
                                        )
                                    }
                                }
                            }

                            // Item 3: Widgets Manager
                            item {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = colors.cardBackground,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onDismissRequest()
                                            onOpenWidgetsManager()
                                        }
                                        .testTag("setting_item_widgets_manager")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Widgets,
                                                contentDescription = "Widgets",
                                                tint = accent.primary,
                                                modifier = Modifier.size(22.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = "Widgets de Pantalla Principal",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = colors.textPrimary
                                                )
                                                Text(
                                                    text = "${state.activeWidgets.size} widgets activos en el carrusel",
                                                    fontSize = 11.sp,
                                                    color = colors.textMuted
                                                )
                                            }
                                        }
                                        Text(
                                            text = "Configurar →",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = accent.primary
                                        )
                                    }
                                }
                            }

                            // Item 4: Accent Theme & Colors
                            item {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = colors.cardBackground,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onDismissRequest()
                                            onOpenColorPicker()
                                        }
                                        .testTag("setting_item_color_picker")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Surface(
                                                shape = CircleShape,
                                                color = accent.primary,
                                                modifier = Modifier.size(24.dp)
                                            ) {}
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = "Tema de Color y Acento",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = colors.textPrimary
                                                )
                                                Text(
                                                    text = "Paleta activa: ${accent.name}",
                                                    fontSize = 11.sp,
                                                    color = accent.primary,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                        Text(
                                            text = "Cambiar →",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = accent.primary
                                        )
                                    }
                                }
                            }

                            // Item 5: App Label Text Size Slider Card
                            item {
                                AppLabelTextSizeCard(
                                    currentScale = state.appLabelTextScale,
                                    onScaleChange = { viewModel.setAppLabelTextScale(it) },
                                    onReset = { viewModel.resetAppLabelTextScale() },
                                    accent = accent,
                                    colors = colors
                                )
                            }

                            // Item 6: Status Info
                            item {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = colors.surfaceVariant,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "LAUNCHER OVERVIEW",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                            color = colors.textMuted
                                        )
                                        Text(
                                            text = "• ${allApps.size} Total aplicaciones detectadas\n• ${hiddenSet.size} Ocultas de la vista principal\n• ${allApps.size - hiddenSet.size} Disponibles en cajón\n• Tema: ${state.themeMode.title}\n• Escala de texto: ${(state.appLabelTextScale * 100).toInt()}%",
                                            fontSize = 11.sp,
                                            color = colors.textSecondary,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        // SECTION 1: Gestos Táctiles de Pantalla
                        val gestures = state.gesturesConfig
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 360.dp)
                                .testTag("settings_gestures_scroll"),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Item 1: Master Toggle Switch
                            item {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = colors.cardBackground,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Gesture,
                                                contentDescription = "Gestos de Pantalla",
                                                tint = accent.primary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = "Gestos en Pantalla Principal",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = colors.textPrimary
                                                )
                                                Text(
                                                    text = if (gestures.gesturesEnabled) "Activos: toques y deslizamientos en el fondo" else "Gestos desactivados",
                                                    fontSize = 11.sp,
                                                    color = colors.textMuted
                                                )
                                            }
                                        }
                                        Switch(
                                            checked = gestures.gesturesEnabled,
                                            onCheckedChange = { viewModel.toggleGesturesEnabled() },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = accent.primary,
                                                checkedTrackColor = accent.container
                                            ),
                                            modifier = Modifier.testTag("toggle_gestures_master")
                                        )
                                    }
                                }
                            }

                            // Item 2: Actions Summary Card
                            item {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = colors.cardBackground,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "ACCIONES CONFIGURADAS",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.sp,
                                                color = colors.textMuted
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = accent.container,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable {
                                                        onDismissRequest()
                                                        onOpenGesturesManager()
                                                    }
                                                    .testTag("setting_item_gestures_manager")
                                            ) {
                                                Text(
                                                    text = "Configurar →",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = accent.primary,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }

                                        val itemsList = listOf(
                                            "Doble Toque" to gestures.doubleTapAction.title,
                                            "Deslizar Abajo" to gestures.swipeDownAction.title,
                                            "Deslizar Arriba" to gestures.swipeUpAction.title,
                                            "Pulsación Larga" to gestures.longPressAction.title,
                                            "Deslizar 2 Dedos ↓" to gestures.twoFingerSwipeDownAction.title
                                        )

                                        itemsList.forEach { (gestureName, actionName) ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = gestureName,
                                                    fontSize = 11.sp,
                                                    color = colors.textSecondary
                                                )
                                                Text(
                                                    text = actionName,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if (gestures.gesturesEnabled) accent.primary else colors.textMuted
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Item 3: Gesture Sensitivity Selector
                            item {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = colors.cardBackground,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "SENSIBILIDAD DE DESLIZAMIENTO",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                            color = colors.textMuted
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            GestureSensitivity.entries.forEach { sens ->
                                                val isSelected = gestures.sensitivity == sens
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = if (isSelected) accent.primary else colors.surfaceVariant,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .clickable { viewModel.setGestureSensitivity(sens) }
                                                ) {
                                                    Text(
                                                        text = sens.title,
                                                        fontSize = 10.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (isSelected) accent.onPrimary else colors.textSecondary,
                                                        textAlign = TextAlign.Center,
                                                        modifier = Modifier.padding(vertical = 6.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Item 4: Haptic Feedback Switch
                            item {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = colors.cardBackground,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Vibration,
                                                contentDescription = "Vibración Háptica",
                                                tint = accent.primary,
                                                modifier = Modifier.size(22.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = "Respuesta Háptica",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = colors.textPrimary
                                                )
                                                Text(
                                                    text = "Vibración sutil al disparar una acción",
                                                    fontSize = 11.sp,
                                                    color = colors.textMuted
                                                )
                                            }
                                        }
                                        Switch(
                                            checked = gestures.hapticFeedbackEnabled,
                                            onCheckedChange = { viewModel.toggleGestureHaptics() },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = accent.primary,
                                                checkedTrackColor = accent.container
                                            ),
                                            modifier = Modifier.testTag("toggle_gestures_haptic")
                                        )
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        // SECTION 2: Dark Theme & Automated Sunset/Sunrise Scheduling
                        ThemeSchedulingSection(
                            viewModel = viewModel,
                            state = state,
                            colors = colors,
                            accent = accent,
                            showLocationSelector = showLocationSelector,
                            onToggleLocationSelector = { showLocationSelector = !showLocationSelector }
                        )
                    }
                    else -> {
                        // SECTION 3: Hidden Apps Management
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Search and Filter Row
                            OutlinedTextField(
                                value = appFilterQuery,
                                onValueChange = { appFilterQuery = it },
                                placeholder = { Text("Filter applications...", fontSize = 12.sp, color = colors.textMuted) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = colors.textMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (appFilterQuery.isNotEmpty()) {
                                        IconButton(onClick = { appFilterQuery = "" }, modifier = Modifier.size(24.dp)) {
                                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = colors.textMuted)
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("input_hide_apps_filter"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = accent.primary,
                                    unfocusedBorderColor = colors.divider,
                                    focusedContainerColor = colors.surfaceVariant,
                                    unfocusedContainerColor = colors.surfaceVariant,
                                    focusedTextColor = colors.textPrimary,
                                    unfocusedTextColor = colors.textPrimary
                                ),
                                singleLine = true
                            )

                            // Filter Tabs (All / Hidden Only / Visible Only)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val tabs = listOf("All (${allApps.size})", "Hidden (${hiddenSet.size})", "Visible (${allApps.size - hiddenSet.size})")
                                tabs.forEachIndexed { idx, label ->
                                    val isSelected = filterTab == idx
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) accent.container else colors.surfaceVariant,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { filterTab = idx }
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) accent.onContainer else colors.textMuted,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 5.dp)
                                        )
                                    }
                                }
                            }

                            // App list
                            if (filteredApps.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (filterTab == 1) "No apps are hidden" else "No matching apps found",
                                        fontSize = 12.sp,
                                        color = colors.textMuted
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 240.dp)
                                        .testTag("settings_hide_apps_list"),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    items(filteredApps, key = { it.packageName }) { app ->
                                        val isHidden = app.packageName in hiddenSet

                                        AppHideItemRow(
                                            app = app,
                                            isHidden = isHidden,
                                            accent = accent,
                                            onToggleHide = {
                                                if (isHidden) {
                                                    viewModel.unhideApp(app.packageName)
                                                } else {
                                                    viewModel.hideApp(app.packageName)
                                                }
                                            }
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
                Text("Done", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            if (selectedSection == 3 && hiddenSet.isNotEmpty()) {
                TextButton(
                    onClick = { viewModel.unhideAllApps() }
                ) {
                    Text("Unhide All (${hiddenSet.size})", color = colors.dangerRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    )
}

@Composable
fun ThemeSchedulingSection(
    viewModel: LauncherViewModel,
    state: com.example.LauncherState,
    colors: com.example.ui.theme.LauncherColorScheme,
    accent: com.example.ui.theme.AccentTheme,
    showLocationSelector: Boolean,
    onToggleLocationSelector: () -> Unit
) {
    val solar = state.solarTimes

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 350.dp)
            .testTag("theme_scheduling_scroll"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Solar Status & Eye Strain Hero Card
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (state.isDarkThemeActive) Color(0xFF1E241A) else Color(0xFFEFF5E8),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (state.isDarkThemeActive) accent.primary.copy(alpha = 0.4f) else accent.primary.copy(alpha = 0.25f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (state.isDarkThemeActive) Icons.Default.Nightlight else Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = if (state.isDarkThemeActive) Color(0xFFFFD54F) else Color(0xFFE65100),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (state.isDarkThemeActive) "Dark Mode Active (Night)" else "Light Mode Active (Day)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (state.isDarkThemeActive) Color(0xFFE2E4DE) else Color(0xFF1A1C18)
                            )
                        }

                        // Eye Comfort Pill
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = accent.primary.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RemoveRedEye,
                                    contentDescription = "Eye Strain Protection",
                                    tint = accent.primary,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Eye Strain Shield",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accent.primary
                                )
                            }
                        }
                    }

                    // Solar times cards: Sunrise & Sunset
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Sunrise Card
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = colors.surface.copy(alpha = 0.8f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🌅", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(text = "Sunrise", fontSize = 9.sp, color = colors.textMuted)
                                    Text(
                                        text = solar.sunriseFormatted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                }
                            }
                        }

                        // Sunset Card
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = colors.surface.copy(alpha = 0.8f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🌇", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(text = "Sunset", fontSize = 9.sp, color = colors.textMuted)
                                    Text(
                                        text = solar.sunsetFormatted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                }
                            }
                        }
                    }

                    // Status & Next Transition Info
                    Text(
                        text = "• ${solar.solarPhaseDescription}\n• ${solar.nextTransitionDescription}",
                        fontSize = 10.sp,
                        color = colors.textSecondary,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // Section Title: Automation Mode
        item {
            Text(
                text = "THEME SCHEDULE MODE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = colors.textMuted,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Mode 1: Sunset & Sunrise (Auto)
        item {
            ThemeModeOptionCard(
                title = "Sunset & Sunrise (Automated)",
                subtitle = "Syncs with system solar times. Turns dark at sunset to reduce eye fatigue, light at dawn.",
                icon = Icons.Default.BrightnessAuto,
                isSelected = state.themeMode == ThemeMode.AUTO_SUNSET_SUNRISE,
                accent = accent,
                colors = colors,
                badge = "Recommended",
                onClick = { viewModel.setThemeMode(ThemeMode.AUTO_SUNSET_SUNRISE) }
            )
        }

        // Mode 2: Follow Android System
        item {
            ThemeModeOptionCard(
                title = "Follow Android System",
                subtitle = "Matches device-wide system dark/light mode toggle.",
                icon = Icons.Default.Settings,
                isSelected = state.themeMode == ThemeMode.SYSTEM,
                accent = accent,
                colors = colors,
                onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) }
            )
        }

        // Mode 3: Custom Schedule
        item {
            ThemeModeOptionCard(
                title = "Custom Schedule",
                subtitle = "Automatically turns dark during specific selected hours.",
                icon = Icons.Default.Schedule,
                isSelected = state.themeMode == ThemeMode.CUSTOM_SCHEDULE,
                accent = accent,
                colors = colors,
                onClick = { viewModel.setThemeMode(ThemeMode.CUSTOM_SCHEDULE) }
            )
        }

        // Custom Schedule Time Controls (if active)
        if (state.themeMode == ThemeMode.CUSTOM_SCHEDULE) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colors.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "CUSTOM DARK HOURS",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textMuted
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Start Time
                            Column {
                                Text(text = "Turn Dark at:", fontSize = 10.sp, color = colors.textSecondary)
                                Text(
                                    text = String.format("%02d:%02d", state.customDarkStartHour, state.customDarkStartMinute),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accent.primary
                                )
                            }

                            // Quick adjust buttons for start time
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Button(
                                    onClick = {
                                        val newH = (state.customDarkStartHour - 1 + 24) % 24
                                        viewModel.setCustomSchedule(newH, state.customDarkStartMinute, state.customDarkEndHour, state.customDarkEndMinute)
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = colors.cardBackground)
                                ) {
                                    Text("-1h", fontSize = 10.sp, color = colors.textPrimary)
                                }
                                Button(
                                    onClick = {
                                        val newH = (state.customDarkStartHour + 1) % 24
                                        viewModel.setCustomSchedule(newH, state.customDarkStartMinute, state.customDarkEndHour, state.customDarkEndMinute)
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = colors.cardBackground)
                                ) {
                                    Text("+1h", fontSize = 10.sp, color = colors.textPrimary)
                                }
                            }
                        }

                        Divider(color = colors.divider, thickness = 0.5.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // End Time
                            Column {
                                Text(text = "Turn Light at:", fontSize = 10.sp, color = colors.textSecondary)
                                Text(
                                    text = String.format("%02d:%02d", state.customDarkEndHour, state.customDarkEndMinute),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accent.primary
                                )
                            }

                            // Quick adjust buttons for end time
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Button(
                                    onClick = {
                                        val newH = (state.customDarkEndHour - 1 + 24) % 24
                                        viewModel.setCustomSchedule(state.customDarkStartHour, state.customDarkStartMinute, newH, state.customDarkEndMinute)
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = colors.cardBackground)
                                ) {
                                    Text("-1h", fontSize = 10.sp, color = colors.textPrimary)
                                }
                                Button(
                                    onClick = {
                                        val newH = (state.customDarkEndHour + 1) % 24
                                        viewModel.setCustomSchedule(state.customDarkStartHour, state.customDarkStartMinute, newH, state.customDarkEndMinute)
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = colors.cardBackground)
                                ) {
                                    Text("+1h", fontSize = 10.sp, color = colors.textPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Mode 4 & 5: Static Themes (Always Dark / Always Light)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Always Light
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (state.themeMode == ThemeMode.ALWAYS_LIGHT) accent.container else colors.cardBackground,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (state.themeMode == ThemeMode.ALWAYS_LIGHT) accent.primary else colors.cardBorder
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.setThemeMode(ThemeMode.ALWAYS_LIGHT) }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LightMode,
                            contentDescription = "Always Light",
                            tint = if (state.themeMode == ThemeMode.ALWAYS_LIGHT) accent.primary else colors.textMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Always Light",
                            fontSize = 11.sp,
                            fontWeight = if (state.themeMode == ThemeMode.ALWAYS_LIGHT) FontWeight.Bold else FontWeight.Medium,
                            color = colors.textPrimary
                        )
                    }
                }

                // Always Dark
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (state.themeMode == ThemeMode.ALWAYS_DARK) accent.container else colors.cardBackground,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (state.themeMode == ThemeMode.ALWAYS_DARK) accent.primary else colors.cardBorder
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.setThemeMode(ThemeMode.ALWAYS_DARK) }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = "Always Dark",
                            tint = if (state.themeMode == ThemeMode.ALWAYS_DARK) accent.primary else colors.textMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Always Dark",
                            fontSize = 11.sp,
                            fontWeight = if (state.themeMode == ThemeMode.ALWAYS_DARK) FontWeight.Bold else FontWeight.Medium,
                            color = colors.textPrimary
                        )
                    }
                }
            }
        }

        // Section: Eye Strain & Display Features
        item {
            Text(
                text = "EYE COMFORT & DISPLAY ENHANCEMENTS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = colors.textMuted,
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        // Toggle: AMOLED Pure Black
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = colors.cardBackground,
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AMOLED Pure Black (#000000)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Zero glare at night & maximum OLED battery savings",
                            fontSize = 10.sp,
                            color = colors.textMuted
                        )
                    }
                    Switch(
                        checked = state.isPureBlack,
                        onCheckedChange = { viewModel.setPureBlack(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = accent.primary
                        )
                    )
                }
            }
        }

        // Toggle: Warm Night Comfort (Low Blue Light)
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = colors.cardBackground,
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Warm Eye Comfort (Low Blue Light)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Warm amber tones to relieve retinal strain during night hours",
                            fontSize = 10.sp,
                            color = colors.textMuted
                        )
                    }
                    Switch(
                        checked = state.isWarmEyeComfort,
                        onCheckedChange = { viewModel.setWarmEyeComfort(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = accent.primary
                        )
                    )
                }
            }
        }

        // Solar Location Preset Selector
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = colors.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleLocationSelector() }
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = accent.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(text = "Solar Location", fontSize = 10.sp, color = colors.textMuted)
                            Text(
                                text = state.selectedSolarLocation.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }
                    }
                    Text(
                        text = if (showLocationSelector) "Close ▲" else "Change ▼",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = accent.primary
                    )
                }
            }
        }

        if (showLocationSelector) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SolarLocation.DefaultLocations.forEach { loc ->
                        val isSelected = loc.name == state.selectedSolarLocation.name
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) accent.container else colors.cardBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setSolarLocation(loc)
                                    onToggleLocationSelector()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = loc.name,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) accent.onContainer else colors.textPrimary
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = accent.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeModeOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    accent: com.example.ui.theme.AccentTheme,
    colors: com.example.ui.theme.LauncherColorScheme,
    badge: String? = null,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) accent.container.copy(alpha = if (colors.isDark) 0.35f else 0.8f) else colors.cardBackground,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) accent.primary else colors.cardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(if (isSelected) accent.primary else colors.surfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) accent.onPrimary else colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = colors.textPrimary
                        )
                        if (badge != null) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = accent.primary
                            ) {
                                Text(
                                    text = badge,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accent.onPrimary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = subtitle,
                        fontSize = 10.sp,
                        color = colors.textMuted,
                        lineHeight = 13.sp
                    )
                }
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = accent.primary,
                    unselectedColor = colors.textMuted
                )
            )
        }
    }
}

@Composable
fun AppHideItemRow(
    app: AppInfo,
    isHidden: Boolean,
    accent: com.example.ui.theme.AccentTheme,
    onToggleHide: () -> Unit
) {
    val colors = LauncherTheme.colors
    val iconBitmap = remember(app.packageName) {
        try {
            app.icon?.let { d ->
                val w = if (d.intrinsicWidth > 0) d.intrinsicWidth else 96
                val h = if (d.intrinsicHeight > 0) d.intrinsicHeight else 96
                d.toBitmap(w, h).asImageBitmap()
            }
        } catch (e: Throwable) {
            null
        }
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isHidden) colors.dangerRed.copy(alpha = 0.1f) else colors.cardBackground,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isHidden) colors.dangerRed.copy(alpha = 0.3f) else colors.cardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onToggleHide() }
            .testTag("app_hide_row_${app.packageName}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (iconBitmap != null) {
                    Image(
                        bitmap = iconBitmap,
                        contentDescription = app.label,
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(getCategoryColor(app.category).copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = app.label.take(1).uppercase(),
                            color = getCategoryColor(app.category),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = app.label,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = app.category,
                        fontSize = 10.sp,
                        color = colors.textMuted
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isHidden) colors.dangerRed.copy(alpha = 0.15f) else colors.surfaceVariant,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = if (isHidden) "Hidden" else "Visible",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isHidden) colors.dangerRed else colors.textSecondary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Switch(
                    checked = !isHidden, // checked means visible on launcher
                    onCheckedChange = { onToggleHide() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = accent.primary,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = colors.dangerRed
                    ),
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

@Composable
fun AppLabelTextSizeCard(
    currentScale: Float,
    onScaleChange: (Float) -> Unit,
    onReset: () -> Unit,
    accent: com.example.ui.theme.AccentTheme,
    colors: com.example.ui.theme.LauncherColorScheme
) {
    val percentage = (currentScale * 100).toInt()
    val scaleLabel = when {
        percentage <= 80 -> "Compact"
        percentage in 95..105 -> "Standard"
        percentage in 106..130 -> "Large"
        else -> "Extra Large"
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = colors.cardBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row: Icon, Title & Scale Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = accent.primary.copy(alpha = 0.15f),
                        modifier = Modifier.size(30.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = "Text Size",
                                tint = accent.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "App Label Text Size",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Independent from system settings",
                            fontSize = 10.sp,
                            color = colors.textMuted
                        )
                    }
                }

                // Value Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accent.container
                ) {
                    Text(
                        text = "$percentage% • $scaleLabel",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = accent.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Slider Component
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Slider(
                    value = currentScale,
                    onValueChange = { onScaleChange(it) },
                    valueRange = 0.70f..1.60f,
                    steps = 17,
                    colors = SliderDefaults.colors(
                        thumbColor = accent.primary,
                        activeTrackColor = accent.primary,
                        inactiveTrackColor = colors.divider
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("slider_app_label_text_size")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "A- (70%)", fontSize = 10.sp, color = colors.textMuted)
                    Text(
                        text = "100%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (percentage in 98..102) accent.primary else colors.textMuted
                    )
                    Text(text = "A+ (160%)", fontSize = 10.sp, color = colors.textMuted)
                }
            }

            // Live Typography Preview Box
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = colors.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "LIVE PREVIEW",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = colors.textMuted
                    )

                    // Home Screen Display Preview
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(colors.surface.copy(alpha = 0.6f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1.",
                            color = accent.primary.copy(alpha = 0.7f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(20.dp)
                        )
                        Text(
                            text = "Messages",
                            color = colors.textPrimary,
                            fontSize = (30 * currentScale).coerceIn(16f, 44f).sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.8).sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // App Drawer Item Preview
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(colors.surface.copy(alpha = 0.6f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = accent.primary.copy(alpha = 0.2f),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "C",
                                    color = accent.primary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Camera",
                            color = colors.textPrimary,
                            fontSize = (16 * currentScale).coerceIn(10f, 26f).sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.3).sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Quick Preset Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    0.80f to "80%",
                    1.00f to "100%",
                    1.25f to "125%",
                    1.50f to "150%"
                ).forEach { (scaleVal, label) ->
                    val isSelected = kotlin.math.abs(currentScale - scaleVal) < 0.03f
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) accent.primary else colors.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onScaleChange(scaleVal) }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) accent.onPrimary else colors.textPrimary
                            )
                        }
                    }
                }

                if (kotlin.math.abs(currentScale - 1.0f) >= 0.03f) {
                    IconButton(
                        onClick = onReset,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Reset to 100%",
                            tint = colors.textMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
