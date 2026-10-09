package com.example.ui.presentation

import android.content.Context
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AppInfo
import com.example.LauncherViewModel
import com.example.R
import com.example.data.model.AppItem
import com.example.domain.launcher.LaunchResult
import com.example.ui.theme.*

/**
 * Pantalla de presentación y cajón de búsqueda del Launcher según el marco 'Cajón de Aplicaciones' (Stitch).
 * Totalmente internacionalizado y adaptativo al idioma del sistema del teléfono.
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
    val catProd = stringResource(R.string.cat_productivity)
    val catTools = stringResource(R.string.cat_tools)
    val catSocial = stringResource(R.string.cat_social)
    val catMedia = stringResource(R.string.cat_media)

    var selectedCategory by remember { mutableStateOf(catAll) }
    val categories = remember(catAll, catProd, catTools, catSocial, catMedia) {
        listOf(catAll, catProd, catTools, catSocial, catMedia)
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
                (selectedCategory == catTools && (app.category.uppercase().contains("UTIL") || app.category.uppercase().contains("HERRAMIENTAS") || app.category.uppercase().contains("TOOL")))
            }
        }
    }

    val colors = LauncherTheme.colors
    val scale = state.appLabelTextScale

    // Agrupación alfabética de aplicaciones
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
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header Minimalista
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.app_drawer_title),
                        style = WarmMinimalistTypography.captionCaps.copy(fontFamily = activeFontFamily, fontSize = (11 * scale).sp),
                        color = colors.accentPrimary
                    )
                    Text(
                        text = stringResource(R.string.apps_available, filteredApps.size),
                        style = WarmMinimalistTypography.microHint.copy(fontFamily = activeFontFamily, fontSize = (10 * scale).sp),
                        color = colors.textSecondary
                    )
                }

                if (filteredApps.any { it.isWorkProfile }) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = colors.chipBackground
                    ) {
                        Text(
                            text = "MULTI-PROFILE",
                            color = colors.textPrimary,
                            style = WarmMinimalistTypography.microHint.copy(fontFamily = activeFontFamily),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Campo de búsqueda
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
                    AnimatedVisibility(
                        visible = searchQuery.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Limpiar búsqueda",
                                tint = colors.textSecondary
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
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

            Spacer(modifier = Modifier.height(10.dp))

            // Quick-filter Category Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                items(
                    items = categories,
                    key = { it }
                ) { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) colors.textPrimary else colors.chipBackground,
                        border = if (isSelected) BorderStroke(1.dp, colors.textPrimary) else BorderStroke(1.dp, colors.cardBorder),
                        modifier = Modifier.clickable { selectedCategory = category }
                    ) {
                        Text(
                            text = category,
                            style = WarmMinimalistTypography.captionCaps.copy(fontFamily = activeFontFamily, fontSize = (11 * scale).sp),
                            color = if (isSelected) colors.background else colors.textSecondary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Lista agrupada alfabéticamente
            if (filteredApps.isEmpty() && searchQuery.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = stringResource(R.string.no_apps_found, searchQuery),
                        color = colors.textSecondary,
                        style = WarmMinimalistTypography.bodyRegular.copy(fontFamily = activeFontFamily, fontSize = (14 * scale).sp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("apps_lazy_column"),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    groupedApps.forEach { (letter, appsInLetter) ->
                        item(
                            key = "header_$letter",
                            contentType = "letter_header"
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp, bottom = 4.dp)
                            ) {
                                Text(
                                    text = letter.toString(),
                                    style = WarmMinimalistTypography.titleMd.copy(fontFamily = activeFontFamily, fontSize = (18 * scale).sp),
                                    color = colors.accentPrimary
                                )
                                HorizontalDivider(
                                    color = colors.divider,
                                    thickness = 1.dp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        items(
                            items = appsInLetter,
                            key = { it.id },
                            contentType = { "app_list_item" }
                        ) { appItem ->
                            LauncherAppRow(
                                app = appItem,
                                accentColor = colors.accentPrimary,
                                fontFamily = activeFontFamily,
                                scale = scale,
                                isMonochromatic = state.isZeroDistractions,
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
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LauncherAppRow(
    app: AppItem,
    accentColor: Color,
    fontFamily: FontFamily,
    scale: Float,
    isMonochromatic: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    val colors = LauncherTheme.colors

    Box(modifier = modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
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
                .testTag("app_row_${app.packageName}"),
            color = colors.surfaceElevated,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, colors.cardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncAppIcon(
                    app = app,
                    size = 40.dp,
                    accentColor = if (isMonochromatic) colors.textPrimary else accentColor
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.label,
                        style = WarmMinimalistTypography.drawerRow.copy(fontFamily = fontFamily, fontSize = (16 * scale).sp),
                        color = colors.textPrimary,
                        maxLines = 1
                    )
                    if (app.isWorkProfile) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = colors.chipBackground
                        ) {
                            Text(
                                text = "WORK",
                                color = colors.textPrimary,
                                style = WarmMinimalistTypography.microHint.copy(fontFamily = fontFamily),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = app.category.uppercase(),
                    style = WarmMinimalistTypography.captionCaps.copy(fontFamily = fontFamily, fontSize = (11 * scale).sp),
                    color = colors.textMuted,
                    maxLines = 1
                )

                if (app.launchCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = colors.chipBackground
                    ) {
                        Text(
                            text = "${app.launchCount}",
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = (10 * scale).sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            ),
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
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
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = accentColor) },
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
