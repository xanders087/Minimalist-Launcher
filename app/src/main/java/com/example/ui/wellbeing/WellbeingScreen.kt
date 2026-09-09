package com.example.ui.wellbeing

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.LauncherViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun WellbeingScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier,
    onBackToHome: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val wb = state.wellbeingConfig
    val scrollState = rememberScrollState()

    // Breathing interactive state for Micro-pausa guiada
    var isBreathing by remember { mutableStateOf(false) }
    var breathPhase by remember { mutableStateOf("Inhala suavemente...") }
    var breathButtonLabel by remember { mutableStateOf("Probar") }
    var breathTargetScale by remember { mutableFloatStateOf(1.0f) }

    val breathScale by animateFloatAsState(
        targetValue = breathTargetScale,
        animationSpec = tween(durationMillis = if (breathTargetScale > 1.0f) 3000 else 2000),
        label = "breathScale"
    )

    LaunchedEffect(isBreathing) {
        if (isBreathing) {
            breathButtonLabel = "5s..."
            breathPhase = "Inhala profundamente... (3s)"
            breathTargetScale = 1.4f
            delay(3000)

            breathPhase = "Exhala con calma... (2s)"
            breathTargetScale = 1.0f
            delay(2000)

            breathPhase = "Mente despejada."
            breathButtonLabel = "Probar"
            isBreathing = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .testTag("wellbeing_screen_content"),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Top Navigation Bar / Intent Tag
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                IconButton(
                    onClick = onBackToHome,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AethericSurfaceLow)
                        .testTag("btn_wellbeing_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Home",
                        tint = AethericTextOffWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Intent Tag & Streak Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(AethericForestSageLight, CircleShape)
                        )
                        Text(
                            text = "RITMO RESTAURATIVO",
                            style = AethericTypography.captionCaps,
                            color = AethericForestSageLight
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(9999.dp),
                        color = AethericSurfaceHigh
                    ) {
                        Text(
                            text = "DÍA 12 RACHA",
                            style = TextStyle(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AethericTextStone,
                                letterSpacing = 1.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Text(
                text = "Tiempo de Pantalla & Consciencia",
                style = AethericTypography.headlineHeroMobile.copy(
                    fontSize = 26.sp,
                    color = AethericTextOffWhite
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // 1. Hero Screen Time Metric Block
        Surface(
            color = AethericSurfaceLow,
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, AethericOutline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HOY ACUMULADO",
                        style = AethericTypography.captionCaps,
                        color = AethericTextMutedZinc
                    )
                    Surface(
                        shape = RoundedCornerShape(9999.dp),
                        color = AethericForestSageContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = null,
                                tint = AethericForestSageLight,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "-42% vs semanal",
                                style = TextStyle(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AethericForestSageLight
                                )
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "1h 18m",
                        style = AethericTypography.clockDisplayMobile.copy(
                            fontSize = 60.sp,
                            letterSpacing = (-2.5).sp,
                            color = AethericTextOffWhite
                        )
                    )
                    Text(
                        text = "/ meta 2h 30m",
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = AethericTextStone
                        ),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }

                // Segmented Minimalist Gauge Bar
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(9999.dp))
                            .background(AethericSurfaceHighest)
                    ) {
                        // WhatsApp (41%)
                        Box(
                            modifier = Modifier
                                .weight(0.41f)
                                .fillMaxHeight()
                                .background(AethericTextOffWhite)
                        )
                        // Lector Kindle (26%)
                        Box(
                            modifier = Modifier
                                .weight(0.26f)
                                .fillMaxHeight()
                                .background(AethericForestSageLight)
                        )
                        // Teléfono (23%)
                        Box(
                            modifier = Modifier
                                .weight(0.23f)
                                .fillMaxHeight()
                                .background(AethericTextStone)
                        )
                        // Otras (10%)
                        Box(
                            modifier = Modifier
                                .weight(0.10f)
                                .fillMaxHeight()
                                .background(AethericSurfaceHighest)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "0m",
                            style = TextStyle(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 10.sp,
                                color = AethericTextMutedZinc
                            )
                        )
                        Text(
                            text = "Límite sugerido: 150m",
                            style = TextStyle(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 10.sp,
                                color = AethericTextMutedZinc
                            )
                        )
                    }
                }
            }
        }

        // 2. App Breakdown Detailed Card
        Surface(
            color = AethericSurfaceLow,
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, AethericOutline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DESGLOSE POR APLICACIÓN",
                        style = AethericTypography.captionCaps,
                        color = AethericTextMutedZinc
                    )
                    Text(
                        text = "4 Apps activas",
                        style = TextStyle(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AethericTextMutedZinc
                        )
                    )
                }

                // Row 1: WhatsApp
                AppUsageRow(
                    tag = "WA",
                    tagColor = AethericTextOffWhite,
                    name = "WhatsApp",
                    subtitle = "Límite: 45 min · 13m restantes",
                    duration = "32 min",
                    durationColor = AethericTextOffWhite,
                    progressPercent = 0.71f,
                    barColor = AethericTextOffWhite
                )

                // Row 2: Lector Kindle
                AppUsageRow(
                    tag = "KD",
                    tagColor = AethericForestSageLight,
                    name = "Lector Kindle",
                    subtitle = "Lectura profunda",
                    subtitleColor = AethericForestSageLight,
                    duration = "20 min",
                    durationColor = AethericForestSageLight,
                    progressPercent = 1.0f,
                    barColor = AethericForestSageLight
                )

                // Row 3: Teléfono
                AppUsageRow(
                    tag = "TL",
                    tagColor = AethericTextStone,
                    name = "Teléfono",
                    subtitle = "Voz / Esenciales",
                    duration = "18 min",
                    durationColor = AethericTextStone,
                    progressPercent = 0.40f,
                    barColor = AethericTextStone
                )

                // Row 4: Otras apps
                AppUsageRow(
                    tag = "++",
                    tagColor = AethericTextMutedZinc,
                    name = "Otras apps",
                    subtitle = "Ajustes, Reloj",
                    duration = "8 min",
                    durationColor = AethericTextMutedZinc,
                    progressPercent = 0.18f,
                    barColor = AethericSurfaceHighest
                )
            }
        }

        // 3. Insight Metric: Screen Unlocks
        Surface(
            color = AethericSurfaceLow,
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, AethericOutline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "DESBLOQUEOS DE PANTALLA",
                        style = AethericTypography.captionCaps,
                        color = AethericTextMutedZinc
                    )
                    Row(
                        verticalAlignment = Alignment.Baseline,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${wb.unlocksTodayCount}",
                            style = AethericTypography.statMono.copy(
                                fontSize = 28.sp,
                                color = AethericTextOffWhite
                            )
                        )
                        Text(
                            text = "veces hoy",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = AethericTextStone
                            )
                        )
                    }
                    Text(
                        text = "Excelente · Promedio normal: 65",
                        style = TextStyle(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AethericForestSageLight
                        )
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = AethericSurfaceHigh,
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = "Screen Unlocks",
                            tint = AethericForestSageLight,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }

        // 4. Herramientas de Fricción Positiva
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HERRAMIENTAS DE FRICCIÓN POSITIVA",
                    style = AethericTypography.captionCaps,
                    color = AethericTextMutedZinc
                )
                val activeCount = listOf(
                    wb.isStrictFocusEnabled,
                    wb.isMindfulPauseEnabled,
                    wb.isAutoGrayscaleEnabled
                ).count { it }
                Text(
                    text = "$activeCount Activas",
                    style = TextStyle(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AethericForestSageLight
                    )
                )
            }

            // Tool 1: Modo Enfoque Estricto
            Surface(
                color = AethericSurfaceLow,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, AethericOutline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Modo Enfoque Estricto",
                                style = AethericTypography.drawerRow.copy(
                                    fontSize = 16.sp,
                                    color = AethericTextOffWhite
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = AethericSurfaceHighest
                            ) {
                                Text(
                                    text = "Hasta ${wb.strictFocusUntil}",
                                    style = TextStyle(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AethericForestSageLight
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Silencia llamadas no esenciales y bloquea redes sociales por completo en tu lanzador.",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontSize = 13.sp,
                                color = AethericTextStone,
                                lineHeight = 18.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    MinimalistSwitch(
                        checked = wb.isStrictFocusEnabled,
                        onCheckedChange = { viewModel.toggleStrictFocus() }
                    )
                }
            }

            // Tool 2: Pausa de Consciencia
            Surface(
                color = AethericSurfaceLow,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, AethericOutline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Pausa de Consciencia",
                                style = AethericTypography.drawerRow.copy(
                                    fontSize = 16.sp,
                                    color = AethericTextOffWhite
                                )
                            )
                            Text(
                                text = "Retardo intencional de 5 segundos con ejercicio de respiración antes de abrir apps distractoras.",
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontSize = 13.sp,
                                    color = AethericTextStone,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        MinimalistSwitch(
                            checked = wb.isMindfulPauseEnabled,
                            onCheckedChange = { viewModel.toggleMindfulPause() }
                        )
                    }

                    // Breathing Micro-Widget Trigger
                    Surface(
                        color = AethericSurfaceHigh,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .scale(breathScale)
                                        .background(
                                            if (isBreathing) AethericForestSageContainer else AethericSurfaceHighest,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SelfImprovement,
                                        contentDescription = "Breathing",
                                        tint = AethericForestSageLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "MICRO-PAUSA GUIADA",
                                        style = AethericTypography.captionCaps,
                                        color = AethericTextOffWhite
                                    )
                                    Text(
                                        text = breathPhase,
                                        style = TextStyle(
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontSize = 10.sp,
                                            color = AethericTextStone
                                        )
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    if (!isBreathing) {
                                        isBreathing = true
                                    }
                                },
                                shape = RoundedCornerShape(9999.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AethericSurfaceHighest,
                                    contentColor = AethericTextOffWhite
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(
                                    text = breathButtonLabel.uppercase(),
                                    style = AethericTypography.captionCaps,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }

            // Tool 3: Escala de Grises Automática
            Surface(
                color = AethericSurfaceLow,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, AethericOutline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Escala de Grises Automática",
                                style = AethericTypography.drawerRow.copy(
                                    fontSize = 16.sp,
                                    color = AethericTextOffWhite
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = AethericSurfaceHighest
                            ) {
                                Text(
                                    text = "OLED",
                                    style = TextStyle(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AethericTextStone
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Neutraliza los estímulos dopaminérgicos de la pantalla eliminando saturaciones innecesarias.",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontSize = 13.sp,
                                color = AethericTextStone,
                                lineHeight = 18.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    MinimalistSwitch(
                        checked = wb.isAutoGrayscaleEnabled,
                        onCheckedChange = { viewModel.toggleAutoGrayscale() }
                    )
                }
            }
        }

        // 5. Calming Reflection Footer Quote
        Surface(
            color = Color(0xFF0E0E10),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF1B1B1D)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = AethericForestSageLight,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "“La atención no es lo que miras, sino lo que eliges no mirar.”",
                    style = TextStyle(
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic,
                        color = AethericTextStone,
                        lineHeight = 18.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun AppUsageRow(
    tag: String,
    tagColor: Color,
    name: String,
    subtitle: String,
    subtitleColor: Color = AethericTextStone,
    duration: String,
    durationColor: Color,
    progressPercent: Float,
    barColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AethericSurfaceHighest),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tag,
                    style = TextStyle(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = tagColor
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = name,
                    style = AethericTypography.drawerRow.copy(
                        fontSize = 15.sp,
                        color = AethericTextOffWhite
                    )
                )
                Text(
                    text = subtitle,
                    style = TextStyle(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 10.sp,
                        color = subtitleColor
                    )
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = duration,
                style = TextStyle(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = durationColor
                )
            )
            Box(
                modifier = Modifier
                    .width(64.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(9999.dp))
                    .background(AethericSurfaceHighest)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressPercent)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(9999.dp))
                        .background(barColor)
                )
            }
        }
    }
}

@Composable
private fun MinimalistSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .width(48.dp)
            .height(28.dp)
            .clip(RoundedCornerShape(9999.dp))
            .background(if (checked) AethericTextOffWhite else AethericSurfaceHighest)
            .clickable { onCheckedChange(!checked) }
            .padding(3.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .background(if (checked) AethericOledBlack else AethericTextStone, CircleShape)
        )
    }
}
