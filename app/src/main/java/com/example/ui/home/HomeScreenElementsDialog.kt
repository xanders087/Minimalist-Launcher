package com.example.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.LauncherViewModel
import com.example.ui.theme.AccentTheme
import com.example.ui.theme.LauncherColorScheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreenElementsDialog(
    viewModel: LauncherViewModel,
    state: com.example.LauncherState,
    colors: LauncherColorScheme,
    accent: AccentTheme,
    onDismissRequest: () -> Unit
) {
    val config = state.homeScreenElements

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("dialog_home_screen_elements"),
            color = colors.surface,
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 6.dp,
            border = BorderStroke(1.dp, colors.cardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = accent.primary.copy(alpha = 0.15f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Dashboard,
                                    contentDescription = "Elementos de Pantalla",
                                    tint = accent.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Elementos Visibles",
                                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "${config.visibleElementsCount} de 9 componentes activos",
                                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.testTag("btn_close_elements_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = colors.textMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Presets Row
                Text(
                    text = "PLANTILLAS RÁPIDAS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = colors.textMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HomeScreenPreset.entries.forEach { preset ->
                        val isCurrent = config == preset.config
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isCurrent) accent.primary else colors.surfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (isCurrent) accent.primary else colors.cardBorder
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.applyHomeScreenPreset(preset) }
                                .testTag("preset_${preset.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isCurrent) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = accent.onPrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = preset.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isCurrent) accent.onPrimary else colors.textPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Element Switches
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Item 1: Clock
                    item {
                        ElementToggleItem(
                            title = "Reloj Digital",
                            subtitle = "Muestra la hora grande en la parte superior",
                            icon = Icons.Default.AccessTime,
                            isChecked = config.showClock,
                            onToggle = { viewModel.setHomeScreenElementVisibility("clock", !config.showClock) },
                            colors = colors,
                            accent = accent,
                            testTag = "toggle_show_clock"
                        )
                    }

                    // Item 2: Date
                    item {
                        ElementToggleItem(
                            title = "Fecha y Día",
                            subtitle = "Muestra la fecha actual debajo del reloj",
                            icon = Icons.Default.CalendarToday,
                            isChecked = config.showDate,
                            onToggle = { viewModel.setHomeScreenElementVisibility("date", !config.showDate) },
                            colors = colors,
                            accent = accent,
                            testTag = "toggle_show_date"
                        )
                    }

                    // Item 3: Solar Mode Badge
                    item {
                        ElementToggleItem(
                            title = "Insignia Modo Solar / Crepúsculo",
                            subtitle = "Indica la hora de amanecer/atardecer y confort visual",
                            icon = Icons.Default.Nightlight,
                            isChecked = config.showSolarBadge,
                            onToggle = { viewModel.setHomeScreenElementVisibility("solar_badge", !config.showSolarBadge) },
                            colors = colors,
                            accent = accent,
                            testTag = "toggle_show_solar_badge"
                        )
                    }

                    // Item 4: Header Actions
                    item {
                        ElementToggleItem(
                            title = "Acceso a Ajustes en Pantalla",
                            subtitle = "Botón discreto de ajustes en la esquina superior derecha",
                            icon = Icons.Default.Tune,
                            isChecked = config.showHeaderActions,
                            onToggle = { viewModel.setHomeScreenElementVisibility("header_actions", !config.showHeaderActions) },
                            colors = colors,
                            accent = accent,
                            testTag = "toggle_show_header_actions"
                        )
                    }

                    // Item 5: Pinned Widgets
                    item {
                        ElementToggleItem(
                            title = "Sección de Widgets",
                            subtitle = "Clima, eventos de calendario, metas y estado de batería",
                            icon = Icons.Default.Widgets,
                            isChecked = config.showWidgets,
                            onToggle = { viewModel.setHomeScreenElementVisibility("widgets", !config.showWidgets) },
                            colors = colors,
                            accent = accent,
                            testTag = "toggle_show_widgets"
                        )
                    }

                    // Item 6: Most Used Apps List
                    item {
                        ElementToggleItem(
                            title = "Lista de Aplicaciones Más Usadas",
                            subtitle = "Acceso rápido a las aplicaciones frecuentes en el centro",
                            icon = Icons.Default.ViewAgenda,
                            isChecked = config.showMostUsedApps,
                            onToggle = { viewModel.setHomeScreenElementVisibility("most_used_apps", !config.showMostUsedApps) },
                            colors = colors,
                            accent = accent,
                            testTag = "toggle_show_most_used_apps"
                        )
                    }

                    // Item 7: Apps Header
                    item {
                        ElementToggleItem(
                            title = "Encabezado 'Más Usadas'",
                            subtitle = "Muestra la etiqueta de sección y recordatorio de pulsación larga",
                            icon = Icons.AutoMirrored.Filled.TrendingUp,
                            isChecked = config.showMostUsedHeader,
                            onToggle = { viewModel.setHomeScreenElementVisibility("most_used_header", !config.showMostUsedHeader) },
                            colors = colors,
                            accent = accent,
                            testTag = "toggle_show_most_used_header"
                        )
                    }

                    // Item 8: Search Bar Pill
                    item {
                        ElementToggleItem(
                            title = "Barra de Búsqueda",
                            subtitle = "Cápsula inferior para buscar apps o abrir el cajón",
                            icon = Icons.Default.Search,
                            isChecked = config.showSearchBar,
                            onToggle = { viewModel.setHomeScreenElementVisibility("search_bar", !config.showSearchBar) },
                            colors = colors,
                            accent = accent,
                            testTag = "toggle_show_search_bar"
                        )
                    }

                    // Item 9: Swipe Up Hint
                    item {
                        ElementToggleItem(
                            title = "Indicador de Deslizamiento",
                            subtitle = "Texto inferior 'Desliza hacia arriba para todas las apps'",
                            icon = Icons.Default.KeyboardArrowUp,
                            isChecked = config.showSwipeUpHint,
                            onToggle = { viewModel.setHomeScreenElementVisibility("swipe_hint", !config.showSwipeUpHint) },
                            colors = colors,
                            accent = accent,
                            testTag = "toggle_show_swipe_hint"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Buttons (Reset defaults & Done)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = colors.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.resetHomeScreenElements() }
                            .testTag("btn_reset_elements")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = null,
                                tint = colors.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Restablecer",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textSecondary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = accent.primary,
                        modifier = Modifier
                            .weight(1.5f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onDismissRequest() }
                            .testTag("btn_elements_done")
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Guardar y Salir",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = accent.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ElementToggleItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isChecked: Boolean,
    onToggle: () -> Unit,
    colors: LauncherColorScheme,
    accent: AccentTheme,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = colors.cardBackground,
        border = BorderStroke(
            1.dp,
            if (isChecked) accent.primary.copy(alpha = 0.35f) else colors.cardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onToggle() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isChecked) accent.primary.copy(alpha = 0.15f) else colors.surfaceVariant,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isChecked) accent.primary else colors.textMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = colors.textPrimary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 10.sp,
                        color = colors.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Switch(
                checked = isChecked,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = accent.primary,
                    checkedTrackColor = accent.container,
                    uncheckedThumbColor = colors.textMuted,
                    uncheckedTrackColor = colors.surfaceVariant
                ),
                modifier = Modifier.size(width = 38.dp, height = 24.dp)
            )
        }
    }
}
