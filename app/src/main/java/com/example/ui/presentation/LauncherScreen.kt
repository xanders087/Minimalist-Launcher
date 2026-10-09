package com.example.ui.presentation

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AppInfo
import com.example.LauncherState
import com.example.LauncherViewModel
import com.example.R
import com.example.data.model.AppItem
import com.example.domain.launcher.LaunchResult
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pantalla de presentación y cajón de búsqueda del Launcher según la especificación exacta de Stitch.
 * Réplica exacta en cuadrícula de 4 columnas para Light Mode y Dark Mode.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LauncherScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier,
    onAppSelected: ((AppItem) -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current

    val state by viewModel.state.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val rawFilteredApps by viewModel.debouncedFilteredApps.collectAsState()

    val catAll = stringResource(R.string.cat_all)
    val catEssentials = stringResource(R.string.cat_essentials)
    val catWork = stringResource(R.string.cat_productivity)
    val catFocus = stringResource(R.string.cat_tools)
    val catRecent = stringResource(R.string.cat_social)

    var selectedCategory by remember { mutableStateOf(catAll) }
    val categories = remember(catAll, catEssentials, catWork, catFocus, catRecent) {
        listOf(catAll, catEssentials, catWork, catFocus, catRecent)
    }

    val activeFontFamily = when (state.typographyChoice) {
        AethericFontFamilyChoice.MONO -> FontFamily.Monospace
        AethericFontFamilyChoice.SERIF -> FontFamily.Serif
        else -> FontFamily.SansSerif
    }

    val filteredApps = remember(rawFilteredApps, selectedCategory, catAll) {
        if (selectedCategory == catAll) {
            rawFilteredApps
        } else {
            rawFilteredApps.filter { app ->
                app.category.uppercase().contains(selectedCategory) ||
                (selectedCategory == catWork && app.category.uppercase().contains("TRABAJO")) ||
                (selectedCategory == catFocus && app.category.uppercase().contains("UTIL"))
            }
        }
    }

    val colors = LauncherTheme.colors
    val scale = state.appLabelTextScale

    // Agrupación alfabética
    val groupedApps = remember(filteredApps) {
        filteredApps
            .groupBy { app ->
                val firstChar = app.label.firstOrNull()?.uppercaseChar() ?: '#'
                if (firstChar in 'A'..'Z') firstChar else '#'
            }
            .toSortedMap { a, b ->
                if (a == '#') 1 else if (b == '#') -1 else a.compareTo(b)
            }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
            .testTag("launcher_screen_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header Minimalista con Búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input_field"),
                placeholder = {
                    Text(
                        text = stringResource(R.string.search_apps_placeholder),
                        color = colors.textMuted,
                        style = WarmMinimalistTypography.bodyRegular.copy(fontFamily = activeFontFamily, fontSize = (14 * scale).sp)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = colors.textSecondary
                    )
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Limpiar búsqueda",
                                    tint = colors.textSecondary
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Mic,
                                contentDescription = "Voice Search",
                                tint = colors.textSecondary,
                                modifier = Modifier.padding(end = 12.dp).size(20.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accentPrimary,
                    unfocusedBorderColor = colors.cardBorder,
                    focusedContainerColor = colors.searchBarBackground,
                    unfocusedContainerColor = colors.searchBarBackground,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips Horizontal Tray
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                items(
                    items = categories,
                    key = { it }
                ) { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) colors.accentPrimary else colors.surfaceVariant,
                        border = if (isSelected) BorderStroke(1.dp, colors.accentPrimary) else BorderStroke(1.dp, colors.cardBorder),
                        modifier = Modifier.clickable { selectedCategory = category }
                    ) {
                        Text(
                            text = category,
                            style = WarmMinimalistTypography.labelSm.copy(fontFamily = activeFontFamily, fontSize = (11 * scale).sp),
                            color = if (isSelected) colors.accentOnPrimary else colors.textSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Main Content Area
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("apps_lazy_column"),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Showing Frequent Apps and Mindful Card when search is empty
                if (searchQuery.isBlank() && selectedCategory == catAll) {
                    // Frequent / Predictive Apps Section
                    item(key = "section_frequent") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(modifier = Modifier.size(6.dp).background(colors.accentPrimary, CircleShape))
                                    Text(
                                        text = stringResource(R.string.frequent),
                                        style = WarmMinimalistTypography.labelSm.copy(fontFamily = activeFontFamily),
                                        color = colors.textSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = stringResource(R.string.predictive),
                                    style = WarmMinimalistTypography.labelSm.copy(fontFamily = activeFontFamily, fontSize = 9.sp),
                                    color = colors.textMuted
                                )
                            }

                            // 4-Column Grid for Frequent Apps
                            val frequentList = state.mostUsedApps.take(4)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                frequentList.forEach { appInfo ->
                                    val appItem = AppItem(
                                        id = appInfo.packageName,
                                        label = appInfo.label,
                                        packageName = appInfo.packageName,
                                        category = appInfo.category,
                                        launchCount = appInfo.launchCount,
                                        isWorkProfile = appInfo.isWorkProfile
                                    )
                                    Box(modifier = Modifier.weight(1f)) {
                                        FrequentAppGridTile(
                                            app = appItem,
                                            state = state,
                                            fontFamily = activeFontFamily,
                                            scale = scale,
                                            onClick = {
                                                val result = viewModel.launchAppWithResult(appInfo)
                                                if (result !is LaunchResult.Success) {
                                                    Toast.makeText(context, "Error al abrir ${appInfo.label}", Toast.LENGTH_SHORT).show()
                                                }
                                                onAppSelected?.invoke(appItem)
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            }
                                        )
                                    }
                                }
                                // Fill remaining spaces if less than 4
                                repeat(4 - frequentList.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    // Mindful Status Card
                    item(key = "section_mindful_card") {
                        Surface(
                            color = colors.cardBackground,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, colors.cardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(colors.surfaceVariant, RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Spa,
                                        contentDescription = null,
                                        tint = colors.accentPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.focus_mode_ready),
                                            style = WarmMinimalistTypography.labelMd.copy(fontFamily = activeFontFamily),
                                            color = colors.textPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Surface(
                                            color = colors.surfaceVariant,
                                            shape = CircleShape
                                        ) {
                                            Text(
                                                text = stringResource(R.string.eco_badge),
                                                style = WarmMinimalistTypography.labelSm.copy(fontFamily = activeFontFamily, fontSize = 9.sp),
                                                color = colors.accentPrimary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = stringResource(R.string.zero_unread_notifs),
                                        style = WarmMinimalistTypography.bodySm.copy(fontFamily = activeFontFamily),
                                        color = colors.textSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = colors.accentPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Alphabetical Catalog (4-Column Grid per letter)
                if (filteredApps.isEmpty() && searchQuery.isNotBlank()) {
                    item(key = "no_matches") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.no_apps_found, searchQuery),
                                color = colors.textSecondary,
                                style = WarmMinimalistTypography.bodyRegular.copy(fontFamily = activeFontFamily, fontSize = (14 * scale).sp)
                            )
                        }
                    }
                } else {
                    groupedApps.forEach { (letter, appsInLetter) ->
                        // Letter Header Item
                        item(
                            key = "header_$letter",
                            contentType = "letter_header"
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 4.dp)
                            ) {
                                Text(
                                    text = letter.toString(),
                                    style = WarmMinimalistTypography.titleMd.copy(fontFamily = activeFontFamily, fontSize = (16 * scale).sp),
                                    color = colors.accentPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                HorizontalDivider(
                                    color = colors.cardBorder,
                                    thickness = 1.dp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // App items rendered in 4-column rows
                        val rows = appsInLetter.chunked(4)
                        items(
                            items = rows,
                            key = { row -> "row_${letter}_${row.first().id}" }
                        ) { appRow ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                appRow.forEach { appItem ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        AppGridTile(
                                            app = appItem,
                                            state = state,
                                            fontFamily = activeFontFamily,
                                            scale = scale,
                                            onClick = {
                                                val result = viewModel.launchAppWithResult(AppInfo.fromAppItem(appItem))
                                                if (result !is LaunchResult.Success) {
                                                    Toast.makeText(context, "Error al abrir ${appItem.label}", Toast.LENGTH_SHORT).show()
                                                }
                                                onAppSelected?.invoke(appItem)
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            }
                                        )
                                    }
                                }
                                // Fill empty spaces if row has less than 4 items
                                repeat(4 - appRow.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tile para la cuadrícula de 4 columnas de aplicaciones del cajón de Stitch.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppGridTile(
    app: AppItem,
    state: LauncherState,
    fontFamily: FontFamily,
    scale: Float,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    val colors = LauncherTheme.colors
    val isMono = state.isZeroDistractions

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .combinedClickable(
                    role = Role.Button,
                    onClickLabel = "Abrir ${app.label}",
                    onLongClickLabel = "Opciones para ${app.label}",
                    onClick = onClick,
                    onLongClick = {
                        onLongClick()
                        isMenuExpanded = true
                    }
                )
                .padding(vertical = 4.dp)
        ) {
            // Squircle Container for Icon
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colors.cardBackground,
                border = BorderStroke(1.dp, colors.cardBorder),
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    AsyncAppIcon(
                        app = app,
                        size = 28.dp,
                        accentColor = if (isMono) colors.textPrimary else colors.accentPrimary
                    )
                    if (app.isWorkProfile) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(colors.accentPrimary, CircleShape)
                                .align(Alignment.TopEnd)
                                .offset(x = (-4).dp, y = 4.dp)
                        )
                    }
                }
            }

            // App Label underneath
            if (state.homeScreenElements.showMostUsedHeader) {
                Text(
                    text = app.label,
                    style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily, fontSize = (11 * scale).sp),
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = colors.surfaceElevated,
            border = BorderStroke(1.dp, colors.cardBorder)
        ) {
            DropdownMenuItem(
                text = { Text("Info", style = WarmMinimalistTypography.bodyRegular.copy(fontFamily = fontFamily, fontSize = (13 * scale).sp), color = colors.textPrimary) },
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = colors.accentPrimary) },
                onClick = { isMenuExpanded = false }
            )
            DropdownMenuItem(
                text = { Text("Hide", style = WarmMinimalistTypography.bodyRegular.copy(fontFamily = fontFamily, fontSize = (13 * scale).sp), color = colors.textSecondary) },
                leadingIcon = { Icon(Icons.Default.VisibilityOff, contentDescription = null, tint = colors.textSecondary) },
                onClick = { isMenuExpanded = false }
            )
        }
    }
}

/**
 * Tile especial para las aplicaciones frecuentes/predecibles (incluyendo Calendar tipo OCT 24).
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FrequentAppGridTile(
    app: AppItem,
    state: LauncherState,
    fontFamily: FontFamily,
    scale: Float,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val isCalendar = app.packageName.contains("calendar", ignoreCase = true) || app.label.contains("calendar", ignoreCase = true) || app.label.contains("calendario", ignoreCase = true)
    val colors = LauncherTheme.colors

    if (isCalendar) {
        val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
        val dayFormat = SimpleDateFormat("dd", Locale.getDefault())
        val currentDate = Date()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { onClick() }
                .padding(vertical = 4.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colors.cardBackground,
                border = BorderStroke(1.dp, colors.cardBorder),
                modifier = Modifier.size(56.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = monthFormat.format(currentDate).uppercase(),
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.accentPrimary
                        )
                    )
                    Text(
                        text = dayFormat.format(currentDate),
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Light,
                            color = colors.textPrimary
                        )
                    )
                }
            }
            if (state.homeScreenElements.showMostUsedHeader) {
                Text(
                    text = app.label,
                    style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily, fontSize = (11 * scale).sp),
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    } else {
        AppGridTile(
            app = app,
            state = state,
            fontFamily = fontFamily,
            scale = scale,
            onClick = onClick,
            onLongClick = onLongClick
        )
    }
}
