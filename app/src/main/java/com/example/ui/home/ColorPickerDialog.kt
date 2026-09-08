package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.LauncherViewModel
import com.example.ui.theme.AccentTheme
import com.example.ui.theme.LauncherTheme
import com.example.ui.theme.WcagContrastUtil

@Composable
fun ColorPickerDialog(
    viewModel: LauncherViewModel,
    onDismissRequest: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val currentTheme = state.accentTheme
    val colors = LauncherTheme.colors

    var selectedHue by remember {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(currentTheme.primary.toArgb(), hsv)
        mutableFloatStateOf(hsv[0])
    }

    var selectedSaturation by remember {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(currentTheme.primary.toArgb(), hsv)
        mutableFloatStateOf(if (hsv[1] > 0.05f) hsv[1] else 0.75f)
    }

    var selectedValue by remember {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(currentTheme.primary.toArgb(), hsv)
        mutableFloatStateOf(if (hsv[2] > 0.05f) hsv[2] else 0.55f)
    }

    var isCustomMode by remember {
        mutableStateOf(AccentTheme.Presets.none { it.id == currentTheme.id })
    }

    val dynamicColor = remember(selectedHue, selectedSaturation, selectedValue) {
        val argb = android.graphics.Color.HSVToColor(floatArrayOf(selectedHue, selectedSaturation, selectedValue))
        Color(argb)
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("color_picker_dialog"),
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
                            .size(32.dp)
                            .background(currentTheme.container, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Accent Color",
                            tint = currentTheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Accent Color",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Minimalist UI Personalization",
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
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Live UI Preview Card with WCAG Contrast Rating
                val contrastEval = remember(currentTheme.primary) {
                    WcagContrastUtil.evaluateContrast(
                        foreground = currentTheme.onPrimary,
                        background = currentTheme.primary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = colors.cardBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "WCAG READABILITY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                    color = colors.textMuted
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (contrastEval.isReadable) Color(0xFF1B5E20).copy(alpha = 0.15f) else Color(0xFFB71C1C).copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = if (contrastEval.isReadable) Color(0xFF2E7D32) else colors.dangerRed,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "${contrastEval.level.shortLabel} • ${contrastEval.formattedRatio}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (contrastEval.isReadable) Color(0xFF2E7D32) else colors.dangerRed
                                        )
                                    }
                                }
                            }

                            val hexString = String.format("#%06X", (0xFFFFFF and currentTheme.primary.toArgb()))
                            Text(
                                text = hexString,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.accentTextOnSurface
                            )
                        }

                        // Demo UI elements showing dynamic readable label colors
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Pill 1: Solid Accent with dynamic WCAG on-accent text
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = currentTheme.primary
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Label on Accent",
                                        color = currentTheme.onPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Pill 2: Container with dynamic WCAG on-container text
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = colors.accentContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Container • Safe",
                                        color = colors.accentOnContainer,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Text automatically switches to ${if (currentTheme.onPrimary == WcagContrastUtil.HighContrastDark) "dark" else "light"} to guarantee WCAG compliance.",
                            fontSize = 10.sp,
                            color = colors.textMuted
                        )
                    }
                }

                // Section 1: Curated Minimalist Presets
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "CURATED PALETTES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = colors.textSecondary
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(AccentTheme.Presets) { preset ->
                            val isSelected = !isCustomMode && currentTheme.id == preset.id

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        isCustomMode = false
                                        viewModel.setAccentTheme(preset)
                                        val hsv = FloatArray(3)
                                        android.graphics.Color.colorToHSV(preset.primary.toArgb(), hsv)
                                        selectedHue = hsv[0]
                                        selectedSaturation = if (hsv[1] > 0.05f) hsv[1] else 0.75f
                                        selectedValue = if (hsv[2] > 0.05f) hsv[2] else 0.55f
                                    }
                                    .padding(4.dp)
                                    .testTag("preset_${preset.id}")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(preset.primary, CircleShape)
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) colors.textPrimary else colors.cardBorder,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = preset.onPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = preset.name,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) colors.textPrimary else colors.textMuted
                                )
                            }
                        }
                    }
                }

                // Section 2: Dynamic Custom Spectrum Slider
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DYNAMIC COLOR SLIDER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = colors.textSecondary
                        )
                        if (isCustomMode) {
                            Text(
                                text = "CUSTOM",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.accentTextOnSurface
                            )
                        }
                    }

                    // Hue Spectrum Track
                    val hueColors = listOf(
                        Color.Red,
                        Color.Yellow,
                        Color.Green,
                        Color.Cyan,
                        Color.Blue,
                        Color.Magenta,
                        Color.Red
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.horizontalGradient(hueColors))
                            .pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                                    selectedHue = fraction * 360f
                                    isCustomMode = true
                                    val argb = android.graphics.Color.HSVToColor(floatArrayOf(selectedHue, selectedSaturation, selectedValue))
                                    viewModel.setCustomAccentColor(Color(argb))
                                }
                            }
                            .pointerInput(Unit) {
                                detectDragGestures { change, _ ->
                                    val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                                    selectedHue = fraction * 360f
                                    isCustomMode = true
                                    val argb = android.graphics.Color.HSVToColor(floatArrayOf(selectedHue, selectedSaturation, selectedValue))
                                    viewModel.setCustomAccentColor(Color(argb))
                                }
                            }
                    ) {
                        // Slider thumb indicator
                        val thumbOffsetRatio = (selectedHue / 360f).coerceIn(0f, 1f)
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val thumbX = size.width * thumbOffsetRatio
                            drawCircle(
                                color = Color.White,
                                radius = 10.dp.toPx(),
                                center = Offset(thumbX, size.height / 2)
                            )
                            drawCircle(
                                color = dynamicColor,
                                radius = 7.dp.toPx(),
                                center = Offset(thumbX, size.height / 2)
                            )
                        }
                    }

                    // Tone / Saturation Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val tones = listOf(
                            Triple("Muted", 0.45f, 0.45f),
                            Triple("Balanced", 0.70f, 0.55f),
                            Triple("Vibrant", 0.90f, 0.70f),
                            Triple("Deep", 0.85f, 0.35f)
                        )

                        tones.forEach { (label, sat, value) ->
                            val isToneSelected = isCustomMode && 
                                kotlin.math.abs(selectedSaturation - sat) < 0.1f && 
                                kotlin.math.abs(selectedValue - value) < 0.1f

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isToneSelected) colors.textPrimary else colors.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedSaturation = sat
                                        selectedValue = value
                                        isCustomMode = true
                                        val argb = android.graphics.Color.HSVToColor(floatArrayOf(selectedHue, sat, value))
                                        viewModel.setCustomAccentColor(Color(argb))
                                    }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isToneSelected) colors.surface else colors.textSecondary
                                    )
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
                colors = ButtonDefaults.buttonColors(containerColor = currentTheme.primary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Done", fontWeight = FontWeight.Bold, color = currentTheme.onPrimary)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    isCustomMode = false
                    viewModel.setAccentTheme(AccentTheme.ForestSage)
                    selectedHue = 100f
                    selectedSaturation = 0.7f
                    selectedValue = 0.42f
                }
            ) {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = "Reset",
                    modifier = Modifier.size(16.dp),
                    tint = colors.textMuted
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Reset", color = colors.textMuted, fontSize = 12.sp)
            }
        }
    )
}
