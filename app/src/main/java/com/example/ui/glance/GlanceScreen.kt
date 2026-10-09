package com.example.ui.glance

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.LauncherViewModel
import com.example.R
import com.example.ui.theme.*

/**
 * Pantalla completa de Glance Feed diseñada exactamente según las pantallas de Stitch
 * (0257656d... Light Mode y 8ffc9474... Dark Mode).
 */
@Composable
fun GlanceScreen(
    viewModel: LauncherViewModel,
    fontFamily: FontFamily,
    scale: Float = 1.0f
) {
    val state by viewModel.state.collectAsState()
    val colors = LauncherTheme.colors
    var thoughtInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Ambient Intro Section
        item(key = "ambient_intro") {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = colors.surfaceVariant,
                        shape = CircleShape
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(modifier = Modifier.size(6.dp).background(colors.accentPrimary, CircleShape))
                            Text(
                                stringResource(R.string.ambient_feed_title),
                                style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily, fontSize = (10 * scale).sp),
                                color = colors.textSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Surface(
                        color = colors.surfaceElevated,
                        shape = CircleShape
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = colors.accentPrimary, modifier = Modifier.size(13.dp))
                            Text(
                                stringResource(R.string.claude_synthesis),
                                style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily, fontSize = (10 * scale).sp),
                                color = colors.textPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.greeting_alex),
                    style = WarmMinimalistTypography.headlineLg.copy(fontFamily = fontFamily, fontSize = (26 * scale).sp),
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = stringResource(R.string.greeting_sub),
                    style = WarmMinimalistTypography.bodyMd.copy(fontFamily = fontFamily, fontSize = (14 * scale).sp),
                    color = colors.textSecondary
                )
            }
        }

        // Thought Resonance Bento Card
        item(key = "thought_resonance_card") {
            Surface(
                color = colors.cardBackground,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, colors.cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Outlined.Psychology, contentDescription = null, tint = colors.accentPrimary, modifier = Modifier.size(18.dp))
                            Text(
                                stringResource(R.string.thought_resonance),
                                style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily, fontSize = (10 * scale).sp),
                                color = colors.textSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Outlined.CloudDone, contentDescription = null, tint = colors.textMuted, modifier = Modifier.size(14.dp))
                            Text(
                                stringResource(R.string.offline_sync),
                                style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily, fontSize = (10 * scale).sp),
                                color = colors.textMuted
                            )
                        }
                    }

                    // Input Bar
                    Surface(
                        color = colors.surfaceVariant,
                        shape = CircleShape,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Outlined.EditNote, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(20.dp))
                            TextField(
                                value = thoughtInput,
                                onValueChange = { thoughtInput = it },
                                placeholder = { Text(stringResource(R.string.capture_thought_placeholder), style = WarmMinimalistTypography.bodyMd.copy(fontFamily = fontFamily, fontSize = (13 * scale).sp), color = colors.textMuted) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                textStyle = WarmMinimalistTypography.bodyMd.copy(fontFamily = fontFamily, color = colors.textPrimary, fontSize = (13 * scale).sp),
                                modifier = Modifier.weight(1f)
                            )
                            Box(
                                modifier = Modifier.size(28.dp).background(colors.cardBackground, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Mic, contentDescription = "Mic", tint = colors.textSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    // Suggested Resonance Pills
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = colors.surfaceVariant,
                            modifier = Modifier.clickable { thoughtInput = "Summarize notes" }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Outlined.AutoFixHigh, contentDescription = null, tint = colors.accentPrimary, modifier = Modifier.size(13.dp))
                                Text(
                                    stringResource(R.string.pill_summarize_notes),
                                    style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily, fontSize = (11 * scale).sp),
                                    color = colors.textSecondary
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = colors.surfaceVariant,
                            modifier = Modifier.clickable { thoughtInput = "Draft outline" }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Outlined.Article, contentDescription = null, tint = colors.accentPrimary, modifier = Modifier.size(13.dp))
                                Text(
                                    stringResource(R.string.pill_draft_outline),
                                    style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily, fontSize = (11 * scale).sp),
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Next Appointment Bento Card
        item(key = "next_appointment_card") {
            Surface(
                color = colors.cardBackground,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, colors.cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = colors.accentPrimary, modifier = Modifier.size(18.dp))
                            Text(
                                stringResource(R.string.next_appointment),
                                style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily, fontSize = (10 * scale).sp),
                                color = colors.textSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            color = colors.accentContainer,
                            shape = CircleShape
                        ) {
                            Text(
                                stringResource(R.string.in_28_mins),
                                style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily, fontSize = (10 * scale).sp),
                                color = colors.accentPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = stringResource(R.string.event_title),
                            style = WarmMinimalistTypography.headlineSm.copy(fontFamily = fontFamily, fontSize = (16 * scale).sp),
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Outlined.Schedule, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(14.dp))
                            Text(
                                text = stringResource(R.string.event_time),
                                style = WarmMinimalistTypography.bodySm.copy(fontFamily = fontFamily, fontSize = (12 * scale).sp),
                                color = colors.textSecondary
                            )
                        }
                    }

                    HorizontalDivider(color = colors.divider.copy(alpha = 0.3f))

                    // Attendees & Actions Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatars
                        Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                            listOf("MR", "KL", "TS", "+3").forEach { tag ->
                                Surface(
                                    shape = CircleShape,
                                    color = if (tag == "TS") colors.accentContainer else colors.surfaceVariant,
                                    border = BorderStroke(1.dp, colors.cardBackground),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = tag,
                                            style = TextStyle(fontFamily = fontFamily, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                                        )
                                    }
                                }
                            }
                        }

                        // Action Buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = CircleShape,
                                color = colors.surfaceVariant,
                                modifier = Modifier.clickable { }
                            ) {
                                Text(
                                    text = stringResource(R.string.btn_decline),
                                    style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily, fontSize = (11 * scale).sp),
                                    color = colors.textSecondary,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = colors.accentPrimary,
                                modifier = Modifier.clickable { }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Outlined.Videocam, contentDescription = null, tint = colors.accentOnPrimary, modifier = Modifier.size(15.dp))
                                    Text(
                                        text = stringResource(R.string.btn_join_call),
                                        style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily, fontSize = (11 * scale).sp),
                                        color = colors.accentOnPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Atmospheric Glance Card
        item(key = "atmospheric_glance_card") {
            Surface(
                color = colors.cardBackground,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, colors.cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Outlined.WbTwilight, contentDescription = null, tint = colors.accentPrimary, modifier = Modifier.size(18.dp))
                            Text(
                                stringResource(R.string.atmospheric_glance),
                                style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily, fontSize = (10 * scale).sp),
                                color = colors.textSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            stringResource(R.string.location_sample),
                            style = WarmMinimalistTypography.bodySm.copy(fontFamily = fontFamily, fontSize = (12 * scale).sp),
                            color = colors.textSecondary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = state.weatherData.displayTemp.ifEmpty { stringResource(R.string.temp_sample) },
                                    style = WarmMinimalistTypography.displayClockMobile.copy(fontFamily = fontFamily, fontSize = (48 * scale).sp),
                                    color = colors.textPrimary,
                                    fontWeight = FontWeight.Light
                                )
                                Text(
                                    text = stringResource(R.string.feels_sample),
                                    style = WarmMinimalistTypography.bodySm.copy(fontFamily = fontFamily, fontSize = (11 * scale).sp),
                                    color = colors.textSecondary,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                            Text(
                                text = state.weatherData.condition.ifEmpty { stringResource(R.string.weather_condition_sample) },
                                style = WarmMinimalistTypography.bodySm.copy(fontFamily = fontFamily, fontSize = (12 * scale).sp),
                                color = colors.textPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = colors.surfaceVariant
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(modifier = Modifier.size(6.dp).background(colors.accentPrimary, CircleShape))
                                    Text(
                                        stringResource(R.string.aqi_sample),
                                        style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily, fontSize = 9.sp),
                                        color = colors.textPrimary
                                    )
                                }
                            }

                            Surface(
                                shape = CircleShape,
                                color = colors.surfaceVariant
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Outlined.WbSunny, contentDescription = null, tint = colors.accentPrimary, modifier = Modifier.size(12.dp))
                                    Text(
                                        "UV 2 • Low",
                                        style = WarmMinimalistTypography.labelSm.copy(fontFamily = fontFamily, fontSize = 9.sp),
                                        color = colors.textPrimary
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
