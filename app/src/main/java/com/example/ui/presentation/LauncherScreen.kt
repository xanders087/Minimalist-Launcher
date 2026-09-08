package com.example.ui.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AppInfo
import com.example.LauncherViewModel
import com.example.data.model.AppItem
import com.example.domain.launcher.LaunchResult

/**
 * Pantalla de presentación y cajón de búsqueda del Launcher,
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

    // Escucha eventos unidireccionales (One-off UI events)
    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9F2))
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
                        text = "APLICACIONES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = accent.primary
                    )
                    Text(
                        text = "${filteredApps.size} disponibles",
                        fontSize = 12.sp,
                        color = Color(0xFF74796D)
                    )
                }

                // Badge de perfil de trabajo o foco
                if (filteredApps.any { it.isWorkProfile }) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = accent.container
                    ) {
                        Text(
                            text = "MULTI-PERFIL ACTIVO",
                            color = accent.primary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
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
                        color = Color(0xFF74796D),
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = accent.primary
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
                                tint = Color(0xFF74796D)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accent.primary,
                    unfocusedBorderColor = Color(0xFFE1E4D5),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Lista optimizada a 120 FPS con LazyColumn, keys únicas y contentTypes estables
            if (filteredApps.isEmpty() && searchQuery.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = "No se encontraron coincidencias para \"$searchQuery\"",
                        color = Color(0xFF74796D),
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("apps_lazy_column"),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(
                        items = filteredApps,
                        key = { it.id }, // Clave única y estable que combina paquete y usuario
                        contentType = { "app_list_item" } // Cache de reciclaje de nodos en Compose
                    ) { appItem ->
                        LauncherAppRow(
                            app = appItem,
                            accentColor = accent.primary,
                            onClick = {
                                val result = viewModel.launchAppWithResult(AppInfo.fromAppItem(appItem))
                                if (result !is LaunchResult.Success) {
                                    Toast.makeText(context, "Error al abrir ${appItem.label}", Toast.LENGTH_SHORT).show()
                                }
                                onAppSelected?.invoke(appItem)
                            },
                            onLongClick = {
                                // 4. Microinteracción: Feedback háptico sutil al mantener presionado
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Renglón de aplicación optimizado para evitar recomposiciones innecesarias.
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
                .clip(RoundedCornerShape(14.dp))
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
            color = Color.White,
            shape = RoundedCornerShape(14.dp),
            shadowElevation = 0.5.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 3. Carga asíncrona de icono sin jank en el hilo de Compose
                AsyncAppIcon(
                    app = app,
                    size = 40.dp,
                    accentColor = accentColor
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.label,
                        color = Color(0xFF1A1C18),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = app.category,
                            color = Color(0xFF74796D),
                            fontSize = 11.sp
                        )
                        if (app.isWorkProfile) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = accentColor.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "PERFIL DE TRABAJO",
                                    color = accentColor,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }

                if (app.launchCount > 0) {
                    Text(
                        text = "${app.launchCount}",
                        color = accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Dropdown de opciones activado tras el feedback háptico
        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = false },
            shape = RoundedCornerShape(14.dp),
            containerColor = Color.White
        ) {
            DropdownMenuItem(
                text = { Text("Información", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = accentColor) },
                onClick = { isMenuExpanded = false }
            )
            DropdownMenuItem(
                text = { Text("Ocultar", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.VisibilityOff, contentDescription = null, tint = Color(0xFF74796D)) },
                onClick = { isMenuExpanded = false }
            )
        }
    }
}
