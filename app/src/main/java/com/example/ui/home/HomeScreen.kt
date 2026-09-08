package com.example.ui.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.AppInfo
import com.example.LauncherViewModel
import com.example.ui.home.HomeScreenElementsConfig
import com.example.ui.home.HomeScreenElementsDialog
import com.example.ui.settings.SettingsDialog
import com.example.ui.theme.LauncherTheme
import com.example.ui.theme.ThemeMode
import com.example.ui.widget.HomeWidgetsSection
import com.example.ui.widget.WidgetManagerDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.io.File
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.layout.ContentScale
import com.example.ui.wallpaper.WallpaperDialog
import com.example.ui.wallpaper.WallpaperMode
import com.example.ui.wallpaper.WallpaperRepository
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Gesture
import com.example.ui.gestures.GesturesConfig
import com.example.ui.gestures.HomeGestureType
import com.example.ui.gestures.GestureAction
import com.example.ui.gestures.GestureActionHandler
import com.example.ui.gestures.GesturesDialog
import com.example.ui.gestures.StandbyLockOverlay
import com.example.ui.gestures.homeScreenGestures

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(viewModel: LauncherViewModel) {
    val state by viewModel.state.collectAsState()
    val colors = LauncherTheme.colors
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var isSheetOpen by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showHiddenAppsDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showColorPickerDialog by remember { mutableStateOf(false) }
    var showWidgetManagerDialog by remember { mutableStateOf(false) }
    var showWallpaperDialog by remember { mutableStateOf(false) }
    var showElementsDialog by remember { mutableStateOf(false) }
    var showGesturesDialog by remember { mutableStateOf(false) }
    var isStandbyLockActive by remember { mutableStateOf(false) }

    val accent = state.accentTheme
    val solar = state.solarTimes
    val elemConfig = state.homeScreenElements
    val wpConfig = state.wallpaperConfig
    val gesturesConfig = state.gesturesConfig

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Launcher back-gesture / back-button handler:
    // Never exits the launcher. Instead, gracefully collapses open layers in order:
    // 1. Standby lock overlay
    // 2. Active open dialogs (settings, wallpapers, widgets, elements, gestures, color picker, hidden apps)
    // 3. Search query in app drawer
    // 4. App drawer bottom sheet
    val hasActiveOverlay = isStandbyLockActive ||
            showSettingsDialog ||
            showWallpaperDialog ||
            showWidgetManagerDialog ||
            showElementsDialog ||
            showGesturesDialog ||
            showColorPickerDialog ||
            showHiddenAppsDialog ||
            searchQuery.isNotEmpty() ||
            isSheetOpen

    BackHandler(enabled = hasActiveOverlay) {
        when {
            isStandbyLockActive -> {
                isStandbyLockActive = false
            }
            showSettingsDialog -> {
                showSettingsDialog = false
            }
            showWallpaperDialog -> {
                showWallpaperDialog = false
            }
            showWidgetManagerDialog -> {
                showWidgetManagerDialog = false
            }
            showElementsDialog -> {
                showElementsDialog = false
            }
            showGesturesDialog -> {
                showGesturesDialog = false
            }
            showColorPickerDialog -> {
                showColorPickerDialog = false
            }
            showHiddenAppsDialog -> {
                showHiddenAppsDialog = false
            }
            searchQuery.isNotEmpty() -> {
                searchQuery = ""
                focusManager.clearFocus()
                keyboardController?.hide()
            }
            isSheetOpen -> {
                focusManager.clearFocus()
                keyboardController?.hide()
                coroutineScope.launch {
                    sheetState.hide()
                    isSheetOpen = false
                }
            }
        }
    }

    // Dynamic adaptive styling according to wallpaper luminance for maximum legibility
    val primaryTextColor = wpConfig.resolvePrimaryTextColor(colors.textPrimary)
    val secondaryTextColor = wpConfig.resolveSecondaryTextColor(colors.textSecondary)
    val mutedTextColor = wpConfig.resolveMutedTextColor(colors.textMuted)
    val textShadow = wpConfig.resolveTextShadow()
    val cardBg = wpConfig.resolveCardBackground(colors.cardBackground)
    val cardBorder = wpConfig.resolveCardBorder(colors.cardBorder)
    val surfaceVariantBg = wpConfig.resolveCardBackground(colors.surfaceVariant)

    val gestureCallbacks = remember(state) {
        GestureActionHandler.Callbacks(
            onOpenDrawer = {
                if (!isSheetOpen) {
                    coroutineScope.launch {
                        isSheetOpen = true
                    }
                }
            },
            onOpenSettings = {
                showSettingsDialog = true
            },
            onOpenWallpapers = {
                showWallpaperDialog = true
            },
            onToggleFocusMode = {
                viewModel.toggleZeroDistractions()
            },
            onOpenSearch = {
                if (!isSheetOpen) {
                    coroutineScope.launch {
                        isSheetOpen = true
                    }
                }
                searchQuery = ""
            },
            onCycleTheme = {
                val nextMode = when (state.themeMode) {
                    ThemeMode.AUTO_SUNSET_SUNRISE -> ThemeMode.ALWAYS_DARK
                    ThemeMode.ALWAYS_DARK -> ThemeMode.ALWAYS_LIGHT
                    ThemeMode.ALWAYS_LIGHT -> ThemeMode.AUTO_SUNSET_SUNRISE
                    else -> ThemeMode.ALWAYS_DARK
                }
                viewModel.setThemeMode(nextMode)
            },
            onActivateStandbyLock = {
                isStandbyLockActive = true
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .testTag("home_screen_root")
            .homeScreenGestures(
                enabled = gesturesConfig.gesturesEnabled && !isSheetOpen && !isStandbyLockActive,
                thresholdDp = gesturesConfig.sensitivity.distanceThresholdDp.dp,
                onDoubleTap = {
                    GestureActionHandler.execute(
                        action = gesturesConfig.doubleTapAction,
                        context = context,
                        callbacks = gestureCallbacks,
                        hapticEnabled = gesturesConfig.hapticFeedbackEnabled
                    )
                },
                onSwipeDown = {
                    GestureActionHandler.execute(
                        action = gesturesConfig.swipeDownAction,
                        context = context,
                        callbacks = gestureCallbacks,
                        hapticEnabled = gesturesConfig.hapticFeedbackEnabled
                    )
                },
                onSwipeUp = {
                    GestureActionHandler.execute(
                        action = gesturesConfig.swipeUpAction,
                        context = context,
                        callbacks = gestureCallbacks,
                        hapticEnabled = gesturesConfig.hapticFeedbackEnabled
                    )
                },
                onLongPress = {
                    GestureActionHandler.execute(
                        action = gesturesConfig.longPressAction,
                        context = context,
                        callbacks = gestureCallbacks,
                        hapticEnabled = gesturesConfig.hapticFeedbackEnabled
                    )
                },
                onTwoFingerSwipeDown = {
                    GestureActionHandler.execute(
                        action = gesturesConfig.twoFingerSwipeDownAction,
                        context = context,
                        callbacks = gestureCallbacks,
                        hapticEnabled = gesturesConfig.hapticFeedbackEnabled
                    )
                }
            )
    ) {
        // Wallpaper Layer (if active)
        val wpConfig = state.wallpaperConfig
        if (wpConfig.mode != WallpaperMode.SOLID && !wpConfig.currentWallpaperPath.isNullOrEmpty()) {
            val imageModel = remember(wpConfig.currentWallpaperPath) {
                if (wpConfig.currentWallpaperPath.startsWith("/")) {
                    File(wpConfig.currentWallpaperPath)
                } else {
                    wpConfig.currentWallpaperPath
                }
            }
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageModel)
                    .crossfade(300)
                    .build(),
                onSuccess = { successResult ->
                    val drawable = successResult.result.drawable
                    if (drawable is android.graphics.drawable.BitmapDrawable) {
                        val lum = WallpaperRepository.calculateBitmapLuminance(drawable.bitmap)
                        viewModel.updateWallpaperLuminance(lum)
                    }
                },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Scrim overlay for legibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = wpConfig.dimmingAlpha))
            )
        }

        // Main Screen Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Clock, Date, Status, Solar Indicator, Widgets, & Header Controls
            Column(
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        if (elemConfig.showClock) {
                            ClockWidget(textColor = primaryTextColor, textShadow = textShadow)
                        }
                        if (elemConfig.showDate) {
                            DateWidget(textColor = secondaryTextColor, textShadow = textShadow)
                        }
                        
                        // Solar & Eye Strain Quick Badge
                        if (elemConfig.showSolarBadge && state.themeMode == ThemeMode.AUTO_SUNSET_SUNRISE) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (state.isDarkThemeActive) accent.primary.copy(alpha = 0.2f) else colors.surfaceVariant,
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showSettingsDialog = true }
                                    .testTag("badge_solar_status")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (state.isDarkThemeActive) Icons.Default.Nightlight else Icons.Default.WbSunny,
                                        contentDescription = null,
                                        tint = if (state.isDarkThemeActive) Color(0xFFFFD54F) else accent.primary,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (state.isDarkThemeActive) "Sunset Mode (${solar.sunsetFormatted})" else "Daylight Mode (${solar.sunriseFormatted})",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (state.isDarkThemeActive) Color(0xFFFFE082) else accent.primary
                                    )
                                }
                            }
                        }
                    }
                    
                    // Single Discreet Launcher Settings Access
                    if (elemConfig.showHeaderActions) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = accent.container.copy(alpha = 0.85f),
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable { showSettingsDialog = true }
                                    .testTag("btn_settings_menu")
                            ) {
                                Box(
                                    modifier = Modifier.padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Launcher Settings",
                                        tint = accent.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // AI / Focus status indicator
                            if (state.isAiCategorizing) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(accent.container)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(10.dp),
                                        strokeWidth = 2.dp,
                                        color = accent.primary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "GEMINI AI",
                                        color = accent.primary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }
                            } else if (state.isZeroDistractions) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { viewModel.toggleZeroDistractions() }
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(accent.primary, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "FOCUS",
                                        color = colors.textMuted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.5.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Pinned Home Screen Widgets (Weather, Calendar, Focus, Battery)
                if (elemConfig.showWidgets && state.activeWidgets.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    HomeWidgetsSection(
                        viewModel = viewModel,
                        onOpenWidgetManager = { showWidgetManagerDialog = true }
                    )
                }
            }

            // Middle Section: 'Most Used' Section based on launch frequency
            if (elemConfig.showMostUsedApps) {
                val mostUsedApps = state.mostUsedApps

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {
                    if (elemConfig.showMostUsedHeader) {
                        // Section Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = "Most Used",
                                    tint = accent.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "MOST USED",
                                    color = accent.primary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                )
                            }
                            Text(
                                text = "LONG-PRESS FOR OPTIONS",
                                color = mutedTextColor,
                                style = TextStyle(shadow = textShadow),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    if (mostUsedApps.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            mostUsedApps.forEachIndexed { index, app ->
                                HomeAppItem(
                                    app = app,
                                    index = index + 1,
                                    viewModel = viewModel,
                                    context = context,
                                    accentColor = accent.primary,
                                    accentContainer = accent.container,
                                    colors = colors,
                                    textScale = state.appLabelTextScale,
                                    textColor = primaryTextColor,
                                    textShadow = textShadow
                                )
                            }
                        }
                    } else if (state.isLoading) {
                        CircularProgressIndicator(
                            color = accent.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // Bottom Section: Search apps / Intent Pill & Swipe indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (elemConfig.showSearchBar) {
                    Surface(
                        color = surfaceVariantBg,
                        shape = RoundedCornerShape(32.dp),
                        border = BorderStroke(1.dp, cardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isSheetOpen = true },
                        shadowElevation = if (wpConfig.mode != WallpaperMode.SOLID) 2.dp else 0.5.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = secondaryTextColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Search apps & categories...",
                                    color = mutedTextColor,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(accent.container)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI",
                                    tint = accent.primary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "AI",
                                    color = accent.primary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                
                if (elemConfig.showSwipeUpHint) {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "SWIPE UP FOR ALL APPS",
                        color = mutedTextColor.copy(alpha = 0.75f),
                        style = TextStyle(shadow = textShadow),
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }

        // Apps Bottom Sheet with Categories and Most Used View
        if (isSheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { 
                    isSheetOpen = false 
                    searchQuery = ""
                },
                sheetState = sheetState,
                containerColor = colors.surface,
                scrimColor = Color.Black.copy(alpha = 0.65f),
                dragHandle = { BottomSheetDefaults.DragHandle(color = colors.divider) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp)
                ) {
                    // Search Bar inside Sheet
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("app_search_field"),
                        placeholder = { Text("Search by name or category...", color = colors.textMuted, fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = colors.textSecondary)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = colors.textSecondary)
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
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

                    // Action & Status Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CATEGORIES",
                            color = colors.textMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Settings Button in drawer
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = accent.container,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { showSettingsDialog = true }
                                    .testTag("btn_drawer_settings")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Settings",
                                        tint = accent.primary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Settings",
                                        color = accent.primary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Quick Palette Button in drawer
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = accent.container,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { showColorPickerDialog = true }
                                    .testTag("btn_drawer_palette")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = "Theme",
                                        tint = accent.primary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Color",
                                        color = accent.primary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            if (state.hiddenPackages.isNotEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = colors.surfaceVariant,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { showHiddenAppsDialog = true }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VisibilityOff,
                                            contentDescription = "Hidden",
                                            tint = colors.textSecondary,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Hidden (${state.hiddenPackages.size})",
                                            color = colors.textPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            // Re-run Gemini AI grouping button
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (state.isAiCategorizing) accent.container else colors.surfaceVariant,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable(enabled = !state.isAiCategorizing) {
                                        viewModel.analyzeInstalledAppsWithGemini()
                                    }
                                    .testTag("btn_ai_group")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (state.isAiCategorizing) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(12.dp),
                                            strokeWidth = 2.dp,
                                            color = accent.primary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Grouping...",
                                            color = accent.primary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = "AI",
                                            tint = accent.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Group with Gemini",
                                            color = colors.textPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Category Chips Bar
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.categories) { category ->
                            val isActive = category == state.activeCategory && searchQuery.isEmpty()
                            val count = when (category) {
                                "All" -> state.visibleApps.size
                                "⭐ Most Used" -> state.visibleApps.count { it.launchCount > 0 }
                                else -> state.visibleApps.count { it.category == category }
                            }
                            
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isActive) accent.primary else colors.cardBackground,
                                border = if (isActive) null else androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                                modifier = Modifier
                                    .clickable { 
                                        searchQuery = ""
                                        viewModel.setActiveCategory(category) 
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = category,
                                        color = if (isActive) colors.accentOnPrimary else colors.textPrimary,
                                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .background(
                                                color = if (isActive) colors.accentOnPrimary.copy(alpha = 0.25f) else colors.surfaceVariant,
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$count",
                                            color = if (isActive) colors.accentOnPrimary else colors.textMuted,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    HorizontalDivider(
                        color = colors.divider,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    // App List Filtered by Search or Category
                    val filteredApps = remember(state.visibleApps, state.activeCategory, searchQuery) {
                        if (searchQuery.isNotBlank()) {
                            state.visibleApps.filter {
                                it.label.contains(searchQuery, ignoreCase = true) ||
                                it.category.contains(searchQuery, ignoreCase = true) ||
                                it.packageName.contains(searchQuery, ignoreCase = true)
                            }
                        } else if (state.activeCategory == "⭐ Most Used") {
                            state.visibleApps.filter { it.launchCount > 0 }.sortedByDescending { it.launchCount }
                        } else if (state.activeCategory == "All") {
                            state.visibleApps
                        } else {
                            state.visibleApps.filter { it.category == state.activeCategory }
                        }
                    }

                    if (filteredApps.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No apps found in this category",
                                color = colors.textMuted,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("app_list"),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredApps, key = { it.packageName }) { app ->
                                AppListItem(
                                    app = app,
                                    viewModel = viewModel,
                                    context = context,
                                    accentColor = accent.primary,
                                    colors = colors,
                                    textScale = state.appLabelTextScale,
                                    onLaunch = {
                                        coroutineScope.launch {
                                            sheetState.hide()
                                            isSheetOpen = false
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Settings Dialog
        if (showSettingsDialog) {
            SettingsDialog(
                viewModel = viewModel,
                onOpenColorPicker = { showColorPickerDialog = true },
                onOpenWidgetsManager = { showWidgetManagerDialog = true },
                onOpenWallpaperManager = { showWallpaperDialog = true },
                onOpenElementsManager = { showElementsDialog = true },
                onOpenGesturesManager = { showGesturesDialog = true },
                onDismissRequest = { showSettingsDialog = false }
            )
        }

        // Gestures Dialog
        if (showGesturesDialog) {
            GesturesDialog(
                viewModel = viewModel,
                state = state,
                colors = colors,
                accent = accent,
                onDismissRequest = { showGesturesDialog = false }
            )
        }

        // Home Screen Elements Management Dialog
        if (showElementsDialog) {
            HomeScreenElementsDialog(
                viewModel = viewModel,
                state = state,
                colors = colors,
                accent = accent,
                onDismissRequest = { showElementsDialog = false }
            )
        }

        // Wallpaper Dialog
        if (showWallpaperDialog) {
            WallpaperDialog(
                viewModel = viewModel,
                state = state,
                colors = colors,
                accent = accent,
                onDismissRequest = { showWallpaperDialog = false }
            )
        }

        // Color Picker Dialog
        if (showColorPickerDialog) {
            ColorPickerDialog(
                viewModel = viewModel,
                onDismissRequest = { showColorPickerDialog = false }
            )
        }

        // Widget Manager Dialog
        if (showWidgetManagerDialog) {
            WidgetManagerDialog(
                viewModel = viewModel,
                onDismissRequest = { showWidgetManagerDialog = false }
            )
        }

        // Hidden Apps Management Dialog
        if (showHiddenAppsDialog) {
            val hiddenList = state.apps.filter { it.packageName in state.hiddenPackages }
            AlertDialog(
                onDismissRequest = { showHiddenAppsDialog = false },
                title = {
                    Text(
                        text = "Hidden Applications",
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                },
                text = {
                    if (hiddenList.isEmpty()) {
                        Text("No applications are currently hidden.", color = colors.textMuted)
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().heightIn(max = 280.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(hiddenList) { hiddenApp ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(colors.surfaceVariant)
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = hiddenApp.label,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.textPrimary
                                    )
                                    TextButton(
                                        onClick = { viewModel.unhideApp(hiddenApp.packageName) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Visibility,
                                            contentDescription = "Unhide",
                                            modifier = Modifier.size(16.dp),
                                            tint = accent.primary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Unhide", color = accent.primary, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showHiddenAppsDialog = false }) {
                        Text("Done", color = accent.primary, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    if (hiddenList.isNotEmpty()) {
                        TextButton(onClick = {
                            viewModel.unhideAllApps()
                            showHiddenAppsDialog = false
                        }) {
                            Text("Unhide All", color = colors.dangerRed)
                        }
                    }
                },
                containerColor = colors.surface,
                shape = RoundedCornerShape(20.dp)
            )
        }

        // Standby Lock Screen Overlay (AMOLED Screen-Off / Standby fallback)
        StandbyLockOverlay(
            isActive = isStandbyLockActive,
            onUnlock = { isStandbyLockActive = false },
            onOpenAccessibilitySettings = {
                GestureActionHandler.openAccessibilitySettings(context)
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeAppItem(
    app: AppInfo,
    index: Int,
    viewModel: LauncherViewModel,
    context: Context,
    accentColor: Color,
    accentContainer: Color,
    colors: com.example.ui.theme.LauncherColorScheme,
    textScale: Float = 1.0f,
    textColor: Color = colors.textPrimary,
    textShadow: Shadow? = null,
    onAppClick: ((AppInfo) -> Unit)? = null
) {
    var isMenuOpen by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clip(RoundedCornerShape(8.dp))
                .combinedClickable(
                    role = Role.Button,
                    onClickLabel = "Launch ${app.label}",
                    onLongClickLabel = "App options for ${app.label}",
                    onClick = {
                        viewModel.recordAppLaunch(app)
                        launchApp(context, app, viewModel)
                        onAppClick?.invoke(app)
                    },
                    onLongClick = {
                        isMenuOpen = true
                    }
                )
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .testTag("most_used_app_${app.packageName}"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Text(
                    text = "$index.",
                    color = accentColor.copy(alpha = 0.6f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(24.dp)
                )
                Text(
                    text = app.label,
                    color = textColor,
                    style = TextStyle(shadow = textShadow),
                    fontSize = (30 * textScale).sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.8).sp,
                    maxLines = 1
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (app.launchCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = accentContainer.copy(alpha = 0.7f),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Launches",
                                tint = accentColor,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${app.launchCount}",
                                color = accentColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = app.category.uppercase(),
                    color = colors.textMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp
                )
            }
        }

        // Popup Menu for Quick Action
        AppPopupMenu(
            expanded = isMenuOpen,
            onDismissRequest = { isMenuOpen = false },
            app = app,
            viewModel = viewModel,
            context = context,
            accentColor = accentColor,
            colors = colors
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppListItem(
    app: AppInfo,
    viewModel: LauncherViewModel,
    context: Context,
    accentColor: Color,
    colors: com.example.ui.theme.LauncherColorScheme,
    textScale: Float = 1.0f,
    onLaunch: () -> Unit = {},
    onAppClick: ((AppInfo) -> Unit)? = null
) {
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

    var isSingleCategorizing by remember { mutableStateOf(false) }
    var isMenuOpen by remember { mutableStateOf(false) }

    Box {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .clip(RoundedCornerShape(14.dp))
                .combinedClickable(
                    role = Role.Button,
                    onClickLabel = "Launch ${app.label}",
                    onLongClickLabel = "App options for ${app.label}",
                    onClick = {
                        viewModel.recordAppLaunch(app)
                        launchApp(context, app, viewModel)
                        onLaunch()
                        onAppClick?.invoke(app)
                    },
                    onLongClick = {
                        isMenuOpen = true
                    }
                )
                .testTag("app_item_${app.packageName}"),
            color = colors.cardBackground,
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
            shape = RoundedCornerShape(14.dp),
            shadowElevation = 0.5.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (iconBitmap != null) {
                    Image(
                        bitmap = iconBitmap,
                        contentDescription = app.label,
                        modifier = Modifier.size(42.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(getCategoryColor(app.category).copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = app.label.take(1).uppercase(),
                            color = getCategoryColor(app.category),
                            fontWeight = FontWeight.Bold,
                            fontSize = (16 * textScale).sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.label,
                        color = colors.textPrimary,
                        fontSize = (16 * textScale).sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    // Category & Launch Count
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(getCategoryColor(app.category), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = app.category,
                                color = colors.textMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (app.launchCount > 0) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = colors.surfaceVariant
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = "Launches",
                                        tint = accentColor,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${app.launchCount} launches",
                                        color = colors.textSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Single AI Categorize Button
                IconButton(
                    onClick = {
                        isSingleCategorizing = true
                        viewModel.categorizeAppWithGemini(app)
                    },
                    enabled = !isSingleCategorizing,
                    modifier = Modifier.testTag("ai_categorize_${app.packageName}")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Categorize",
                        tint = if (isSingleCategorizing) colors.textMuted else accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Popup Menu for Quick Action
        AppPopupMenu(
            expanded = isMenuOpen,
            onDismissRequest = { isMenuOpen = false },
            app = app,
            viewModel = viewModel,
            context = context,
            accentColor = accentColor,
            colors = colors,
            onLaunch = onLaunch
        )
    }
}

@Composable
fun AppPopupMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    app: AppInfo,
    viewModel: LauncherViewModel,
    context: Context,
    accentColor: Color,
    colors: com.example.ui.theme.LauncherColorScheme,
    onLaunch: (() -> Unit)? = null
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .background(colors.surface, RoundedCornerShape(16.dp))
            .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
            .widthIn(min = 180.dp)
    ) {
        // App Title in Header
        Text(
            text = app.label,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = colors.textPrimary,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
        
        HorizontalDivider(color = colors.divider)

        // Action 0: Open Application
        DropdownMenuItem(
            text = { 
                Text(
                    "Open", 
                    color = colors.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                ) 
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Launch,
                    contentDescription = "Open Application",
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            },
            onClick = {
                onDismissRequest()
                viewModel.recordAppLaunch(app.packageName)
                launchApp(context, app.packageName)
                onLaunch?.invoke()
            },
            modifier = Modifier.testTag("action_open_${app.packageName}")
        )

        // Action 1: App Info
        DropdownMenuItem(
            text = { 
                Text(
                    "App Info", 
                    color = colors.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                ) 
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "App Info",
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            },
            onClick = {
                onDismissRequest()
                openAppInfo(context, app.packageName)
            },
            modifier = Modifier.testTag("action_app_info_${app.packageName}")
        )

        // Action 2: Hide App
        DropdownMenuItem(
            text = { 
                Text(
                    "Hide", 
                    color = colors.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                ) 
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.VisibilityOff,
                    contentDescription = "Hide App",
                    tint = colors.textSecondary,
                    modifier = Modifier.size(18.dp)
                )
            },
            onClick = {
                onDismissRequest()
                viewModel.hideApp(app.packageName)
                Toast.makeText(context, "${app.label} hidden", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.testTag("action_hide_${app.packageName}")
        )

        // Action 3: Uninstall
        DropdownMenuItem(
            text = { 
                Text(
                    "Uninstall", 
                    color = colors.dangerRed,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                ) 
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Uninstall",
                    tint = colors.dangerRed,
                    modifier = Modifier.size(18.dp)
                )
            },
            onClick = {
                onDismissRequest()
                requestUninstall(context, app.packageName)
            },
            modifier = Modifier.testTag("action_uninstall_${app.packageName}")
        )
    }
}

@Composable
fun ClockWidget(
    textColor: Color = Color(0xFF1A1C18),
    textShadow: Shadow? = null
) {
    var time by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        while (true) {
            time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            delay(1000)
        }
    }
    
    Text(
        text = time,
        color = textColor,
        style = TextStyle(shadow = textShadow),
        fontSize = 80.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-4).sp,
        lineHeight = 80.sp,
        modifier = Modifier.testTag("clock_widget")
    )
}

@Composable
fun DateWidget(
    textColor: Color = Color(0xFF43493E),
    textShadow: Shadow? = null
) {
    var dateStr by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        while (true) {
            val sdf = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
            dateStr = sdf.format(Date())
            delay(60000)
        }
    }
    
    Text(
        text = dateStr,
        color = textColor,
        style = TextStyle(shadow = textShadow),
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .padding(start = 4.dp, top = 4.dp)
            .testTag("date_widget")
    )
}

fun openAppInfo(context: Context, packageName: String) {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Throwable) {
        Toast.makeText(context, "Could not open App Info", Toast.LENGTH_SHORT).show()
    }
}

fun requestUninstall(context: Context, packageName: String) {
    try {
        val intent = Intent(Intent.ACTION_DELETE).apply {
            data = Uri.fromParts("package", packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Throwable) {
        Toast.makeText(context, "Could not launch uninstaller", Toast.LENGTH_SHORT).show()
    }
}

fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "communication" -> Color(0xFF2563EB)
        "productivity" -> Color(0xFF059669)
        "entertainment" -> Color(0xFFDC2626)
        "finance" -> Color(0xFFD97706)
        "utilities" -> Color(0xFF4F46E5)
        "health & fitness" -> Color(0xFF0891B2)
        "navigation" -> Color(0xFFEA580C)
        "system" -> Color(0xFF4B5563)
        else -> Color(0xFF386B1F)
    }
}

/**
 * Builds the Android system intent used to launch the specified application package.
 * Prioritizes standard PackageManager launch intent, then explicit MAIN/LAUNCHER intent,
 * and falls back to system category/action intents for standard system apps.
 */
fun createAppLaunchIntent(context: Context, packageName: String): Intent {
    // 1. Standard launch intent provided by PackageManager
    context.packageManager.getLaunchIntentForPackage(packageName)?.let { intent ->
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        return intent
    }

    // 2. Explicit MAIN / LAUNCHER intent targeting the package
    val directLauncherIntent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
        setPackage(packageName)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
    }
    if (context.packageManager.resolveActivity(directLauncherIntent, 0) != null) {
        return directLauncherIntent
    }

    // 3. Fallback to standard Android system category/action intents for well-known app categories
    val systemIntent: Intent? = when (packageName) {
        "com.android.settings" -> Intent(Settings.ACTION_SETTINGS)
        "com.google.android.dialer", "com.android.dialer" -> Intent(Intent.ACTION_DIAL)
        "com.google.android.apps.messaging", "com.android.mms" -> Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_APP_MESSAGING)
        }
        "com.android.chrome", "com.google.android.browser" -> Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
        "com.google.android.GoogleCamera", "com.android.camera" -> Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        "com.google.android.calculator", "com.android.calculator2" -> Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_APP_CALCULATOR)
        }
        "com.google.android.calendar" -> Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_APP_CALENDAR)
        }
        "com.google.android.deskclock", "com.android.deskclock" -> Intent(AlarmClock.ACTION_SHOW_ALARMS)
        "com.google.android.apps.maps" -> Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="))
        "com.spotify.music" -> Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com"))
        "com.google.android.youtube" -> Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com"))
        "com.google.android.apps.photos" -> Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_APP_GALLERY)
        }
        else -> null
    }

    if (systemIntent != null) {
        systemIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        return systemIntent
    }

    return directLauncherIntent
}

/**
 * Triggers the launch of the specified application, leveraging multi-profile support
 * via LauncherApps / LauncherViewModel when available.
 */
fun launchApp(context: Context, app: AppInfo, viewModel: LauncherViewModel? = null) {
    if (viewModel != null && viewModel.launchApp(app)) {
        return
    }
    launchApp(context, app.packageName)
}

/**
 * Triggers the Android system intent to launch the selected application.
 */
fun launchApp(context: Context, packageName: String) {
    try {
        val launchIntent = createAppLaunchIntent(context, packageName)
        context.startActivity(launchIntent)
    } catch (e: Throwable) {
        try {
            // Direct launch fallback
            val fallback = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                setPackage(packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(fallback)
        } catch (e2: Throwable) {
            Toast.makeText(context, "Could not open $packageName", Toast.LENGTH_SHORT).show()
        }
    }
}
