package com.example.ui.gestures

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.LauncherState
import com.example.LauncherViewModel
import com.example.ui.theme.AccentTheme
import com.example.ui.theme.LauncherColorScheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GesturesDialog(
    viewModel: LauncherViewModel,
    state: LauncherState,
    colors: LauncherColorScheme,
    accent: AccentTheme,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val config = state.gesturesConfig
    var selectedGestureForAction by remember { mutableStateOf<HomeGestureType?>(null) }
    val isAccessibilityActive = remember(state) {
        LauncherAccessibilityService.isAccessibilityServiceEnabled(context)
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(28.dp),
            color = colors.background,
            border = BorderStroke(1.dp, colors.cardBorder),
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
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
                            color = accent.container,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Gesture,
                                    contentDescription = null,
                                    tint = accent.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Gestos de Pantalla",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "Personaliza toques y deslizamientos",
                                fontSize = 12.sp,
                                color = colors.textMuted
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.testTag("btn_close_gestures")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = colors.textSecondary
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 14.dp),
                    color = colors.cardBorder
                )

                // Scrollable Gestures Content
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Master Switch
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (config.gesturesEnabled) accent.container.copy(alpha = 0.35f) else colors.surfaceVariant
                        ),
                        border = BorderStroke(1.dp, if (config.gesturesEnabled) accent.primary.copy(alpha = 0.3f) else colors.cardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Reconocimiento de Gestos",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = if (config.gesturesEnabled) "Activo en la pantalla principal" else "Gestos desactivados",
                                    fontSize = 12.sp,
                                    color = colors.textMuted
                                )
                            }
                            Switch(
                                checked = config.gesturesEnabled,
                                onCheckedChange = { viewModel.toggleGesturesEnabled() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = accent.primary,
                                    checkedTrackColor = accent.container
                                ),
                                modifier = Modifier.testTag("switch_gestures_master")
                            )
                        }
                    }

                    // Gesture List Items
                    Text(
                        text = "GESTOS CONFIGURADOS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = accent.primary,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )

                    HomeGestureType.values().forEach { gestureType ->
                        val currentAction = config.getActionFor(gestureType)
                        GestureConfigItem(
                            gestureType = gestureType,
                            action = currentAction,
                            colors = colors,
                            accent = accent,
                            onClick = { selectedGestureForAction = gestureType }
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 6.dp),
                        color = colors.cardBorder
                    )

                    // Additional Options: Haptics & Sensitivity
                    Text(
                        text = "PREFERENCIAS Y RESPUESTA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = accent.primary,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    // Haptic Feedback Switch
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
                        border = BorderStroke(1.dp, colors.cardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Vibration,
                                    contentDescription = null,
                                    tint = accent.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Respuesta Háptica",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = "Vibración suave al reconocer el gesto",
                                        fontSize = 11.sp,
                                        color = colors.textMuted
                                    )
                                }
                            }
                            Switch(
                                checked = config.hapticFeedbackEnabled,
                                onCheckedChange = { viewModel.toggleGestureHaptics() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = accent.primary,
                                    checkedTrackColor = accent.container
                                ),
                                modifier = Modifier.testTag("switch_gesture_haptics")
                            )
                        }
                    }

                    // Swipe Sensitivity Selector
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
                        border = BorderStroke(1.dp, colors.cardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "Sensibilidad de Deslizamiento",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "Distancia requerida para registrar el deslizamiento",
                                fontSize = 11.sp,
                                color = colors.textMuted
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                GestureSensitivity.values().forEach { sens ->
                                    val isSelected = config.sensitivity == sens
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.setGestureSensitivity(sens) },
                                        label = {
                                            Text(
                                                text = sens.title.substringBefore(" "),
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = accent.primary,
                                            selectedLabelColor = accent.onPrimary,
                                            containerColor = colors.cardBackground,
                                            labelColor = colors.textPrimary
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // Accessibility Service Status for Native Screen Lock
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAccessibilityActive) Color(0xFF142918) else Color(0xFF261D12)
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isAccessibilityActive) Color(0xFF2E7D32) else Color(0xFFE65100).copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isAccessibilityActive) Icons.Default.CheckCircle else Icons.Default.WarningAmber,
                                    contentDescription = null,
                                    tint = if (isAccessibilityActive) Color(0xFF81C784) else Color(0xFFFFB74D),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAccessibilityActive) "Bloqueo Directo Habilitado" else "Servicio de Accesibilidad",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAccessibilityActive) Color(0xFFC8E6C9) else Color(0xFFFFE082)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isAccessibilityActive) {
                                    "El doble toque apaga la pantalla del dispositivo de forma nativa sin restringir la huella dactilar ni requerir PIN."
                                } else {
                                    "Para que el doble toque apague la pantalla del sistema de forma directa, activa el Servicio de Accesibilidad de Minimalist Launcher. De lo contrario, se activará la pantalla standby AMOLED."
                                },
                                fontSize = 11.sp,
                                color = if (isAccessibilityActive) Color(0xFFA5D6A7) else Color(0xFFFFCC80)
                            )
                            if (!isAccessibilityActive) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { GestureActionHandler.openAccessibilitySettings(context) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF6C00)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("btn_grant_accessibility")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Activar en Ajustes del Sistema",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = colors.cardBorder
                )

                // Footer Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { viewModel.resetGesturesToDefaults() },
                        modifier = Modifier.testTag("btn_reset_gestures")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Valores por defecto",
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )
                    }

                    Button(
                        onClick = onDismissRequest,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accent.primary,
                            contentColor = accent.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_save_gestures")
                    ) {
                        Text(
                            text = "Listo",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    // Action Picker Sub-Dialog
    selectedGestureForAction?.let { gestureType ->
        ActionPickerDialog(
            gestureType = gestureType,
            currentAction = config.getActionFor(gestureType),
            colors = colors,
            accent = accent,
            onActionSelected = { newAction ->
                viewModel.setGestureAction(gestureType, newAction)
                selectedGestureForAction = null
            },
            onDismissRequest = { selectedGestureForAction = null }
        )
    }
}

@Composable
private fun GestureConfigItem(
    gestureType: HomeGestureType,
    action: GestureAction,
    colors: LauncherColorScheme,
    accent: AccentTheme,
    onClick: () -> Unit
) {
    Surface(
        color = colors.cardBackground,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, colors.cardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("gesture_item_${gestureType.identifier}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Gesture Icon Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = accent.container,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        val icon = when (gestureType) {
                            HomeGestureType.DOUBLE_TAP -> Icons.Default.TouchApp
                            HomeGestureType.SWIPE_DOWN -> Icons.Default.KeyboardArrowDown
                            HomeGestureType.SWIPE_UP -> Icons.Default.KeyboardArrowUp
                            HomeGestureType.LONG_PRESS -> Icons.Default.TouchApp
                            HomeGestureType.TWO_FINGER_SWIPE_DOWN -> Icons.Default.KeyboardArrowDown
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accent.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = gestureType.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = gestureType.subtitle,
                        fontSize = 11.sp,
                        color = colors.textMuted
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action Badge Chip
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (action != GestureAction.NONE) accent.primary.copy(alpha = 0.15f) else colors.surfaceVariant,
                border = BorderStroke(
                    1.dp,
                    if (action != GestureAction.NONE) accent.primary.copy(alpha = 0.35f) else colors.cardBorder
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = null,
                        tint = if (action != GestureAction.NONE) accent.primary else colors.textMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = action.title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (action != GestureAction.NONE) accent.primary else colors.textMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionPickerDialog(
    gestureType: HomeGestureType,
    currentAction: GestureAction,
    colors: LauncherColorScheme,
    accent: AccentTheme,
    onActionSelected: (GestureAction) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = colors.background,
            border = BorderStroke(1.dp, colors.cardBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Acción para ${gestureType.title}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Elige la acción a ejecutar",
                            fontSize = 12.sp,
                            color = colors.textMuted
                        )
                    }
                    IconButton(onClick = onDismissRequest) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = colors.textSecondary
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = colors.cardBorder
                )

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GestureAction.values().forEach { action ->
                        val isSelected = action == currentAction
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) accent.container.copy(alpha = 0.5f) else colors.cardBackground,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) accent.primary else colors.cardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onActionSelected(action) }
                                .testTag("action_choice_${action.name}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = action.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) accent.primary else colors.textSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = action.title,
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) accent.primary else colors.textPrimary
                                        )
                                        Text(
                                            text = action.description,
                                            fontSize = 11.sp,
                                            color = colors.textMuted
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Seleccionado",
                                        tint = accent.primary,
                                        modifier = Modifier.size(18.dp)
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
