package com.example.ui.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AppInfo
import com.example.LauncherViewModel
import com.example.data.model.AppItem
import com.example.domain.launcher.LaunchResult
import com.example.ui.theme.*

/**
 * Pantalla de presentación y cajón de búsqueda del Launcher según el marco 'Cajón de Aplicaciones' (Stitch).
 * Agrupa alfabéticamente (A, B, C...) con categoría alineada a la derecha en tipografía muted,
 * optimizada para alcanzar 120 FPS constantes mediante Edge-to-Edge nativo,
 * gestión de insets del teclado (IME), búsqueda con debounce y microinteracciones hápticas.
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
    val filteredApps by viewModel.debouncedFilteredApps.collectAsState()

    val accent = state.accentTheme
    val backgroundColor = state.themeVariant.backgroundColor

    // Agrupación alfabética de aplicaciones (A, B, C... y '#' para caracteres especiales/números)
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

    // Escucha eventos unidireccionales (One-off UI events)
    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            // 1. Edge-to-Edge: Consumo de la barra de estado superior
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
            .testTag("launcher_screen_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                // 1. Edge-to-Edge & IME: Animación suave sincronizada con el teclado virtual y barra de navegación
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header Minimalista (Stitch Aetheric Header)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CAJÓN DE APLICACIONES",
                        style = AethericTypography.captionCaps,
                        color = AethericForestSageLight
                    )
                    Text(
                        text = "${filteredApps.size} apps disponibles",
                        style = AethericTypography.microHint,
                        color = AethericTextStone
                    )
                }

                // Badge de perfil de trabajo o foco
                if (filteredApps.any { it.isWorkProfile }) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AethericForestSageContainer
                    ) {
                        Text(
                            text = "MULTI-PERFIL",
                            color = AethericForestSageLight,
                            style = AethericTypography.microHint,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // 2. Campo de búsqueda minimalista con reactividad debounced
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input_field"),
                placeholder = {
                    Text(
                        text = "Buscar por nombre o categoría...",
                        color = AethericTextPebble,
                        style = AethericTypography.bodyRegular.copy(fontSize = 14.sp)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = AethericForestSageLight
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
                                tint = AethericTextStone
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AethericForestSageLight,
                    unfocusedBorderColor = AethericOutline,
                    focusedContainerColor = AethericSurfaceLow,
                    unfocusedContainerColor = AethericSurfaceLow,
                    focusedTextColor = AethericTextOffWhite,
                    unfocusedTextColor = AethericTextOffWhite
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Lista agrupada alfabéticamente (A, B, C...) optimizada a 120 FPS
            if (filteredApps.isEmpty() && searchQuery.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = "No se encontraron coincidencias para \"$searchQuery\"",
                        color = AethericTextStone,
                        style = AethericTypography.bodyRegular.copy(fontSize = 14.sp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("apps_lazy_column"),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    groupedApps.forEach { (letter, appsInLetter) ->
                        // Encabezado de sección alfabética (A, B, C...)
                        item(
                            key = "header_$letter",
                            contentType = "letter_header"
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 14.dp, bottom = 4.dp)
                            ) {
                                Text(
                                    text = letter.toString(),
                                    style = AethericTypography.indexMono,
                                    color = AethericForestSageLight
                                )
                                HorizontalDivider(
                                    color = AethericSurfaceHigh,
                                    thickness = 1.dp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Aplicaciones pertenecientes a la letra
                        items(
                            items = appsInLetter,
                            key = { it.id },
                            contentType = { "app_list_item" }
                        ) { appItem ->
                            LauncherAppRow(
                                app = appItem,
                                accentColor = AethericForestSageLight,
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

/**
 * Renglón de aplicación con categoría alineada a la derecha en tipografía muted
 * según el sistema Aetheric Minimalist.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LauncherAppRow(
    app: AppItem,
    accentColor: Color,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
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
            color = AethericSurfaceLow,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, AethericOutline.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 3. Carga asíncrona de icono sin jank en el hilo de Compose
                AsyncAppIcon(
                    app = app,
                    size = 38.dp,
                    accentColor = accentColor
                )

                Spacer(modifier = Modifier.width(14.dp))

                // Nombre de la app (lado izquierdo)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.label,
                        style = AethericTypography.drawerRow,
                        color = AethericTextOffWhite,
                        maxLines = 1
                    )
                    if (app.isWorkProfile) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = AethericForestSageContainer
                        ) {
                            Text(
                                text = "TRABAJO",
                                color = AethericForestSageLight,
                                style = AethericTypography.microHint,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Categoría o funcionalidad alineada a la derecha en tipografía muted
                Text(
                    text = app.category.uppercase(),
                    style = AethericTypography.captionCaps,
                    color = AethericTextStone,
                    maxLines = 1
                )

                if (app.launchCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = AethericSurfaceHigh
                    ) {
                        Text(
                            text = "${app.launchCount}",
                            style = TextStyle(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AethericForestSageLight
                            ),
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Dropdown de opciones activado tras el feedback háptico
        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = false },
            shape = RoundedCornerShape(12.dp),
            containerColor = AethericSurfaceHigh,
            border = BorderStroke(1.dp, AethericOutline)
        ) {
            DropdownMenuItem(
                text = { Text("Información", style = AethericTypography.bodyRegular.copy(fontSize = 13.sp), color = AethericTextOffWhite) },
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = accentColor) },
                onClick = { isMenuExpanded = false }
            )
            DropdownMenuItem(
                text = { Text("Ocultar", style = AethericTypography.bodyRegular.copy(fontSize = 13.sp), color = AethericTextStone) },
                leadingIcon = { Icon(Icons.Default.VisibilityOff, contentDescription = null, tint = AethericTextStone) },
                onClick = { isMenuExpanded = false }
            )
        }
    }
}
