package com.example.ui.presentation

import android.graphics.Bitmap
import android.util.LruCache
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.data.model.AppItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Cache en memoria LruCache para iconos decodificados.
 * Evita recargas y bloqueos en el hilo principal de Compose a 120 FPS.
 */
object AppIconMemoryCache {
    private val maxMemoryKb = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSizeKb = (maxMemoryKb / 8).coerceAtLeast(1024)

    private val cache = object : LruCache<String, ImageBitmap>(cacheSizeKb) {
        override fun sizeOf(key: String, value: ImageBitmap): Int {
            return (value.width * value.height * 4) / 1024
        }
    }

    fun get(key: String): ImageBitmap? = cache.get(key)

    fun put(key: String, bitmap: ImageBitmap) {
        cache.put(key, bitmap)
    }
}

/**
 * Renderizado ultra-fluido de icono con carga asíncrona en [Dispatchers.IO]
 * y fallback tipográfico (lettermark) sin saltos visuales (jank-free).
 */
@Composable
fun AsyncAppIcon(
    app: AppItem,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    accentColor: Color = Color(0xFF386B1F)
) {
    var cachedBitmap by remember(app.id) {
        mutableStateOf(AppIconMemoryCache.get(app.id))
    }

    LaunchedEffect(app.id) {
        if (cachedBitmap == null) {
            withContext(Dispatchers.IO) {
                val drawable = app.icon
                if (drawable != null) {
                    try {
                        val w = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 96
                        val h = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 96
                        val bmp = drawable.toBitmap(w, h, Bitmap.Config.ARGB_8888).asImageBitmap()
                        AppIconMemoryCache.put(app.id, bmp)
                        cachedBitmap = bmp
                    } catch (e: Throwable) {
                        // Fallback silencioso
                    }
                }
            }
        }
    }

    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(
            targetState = cachedBitmap,
            animationSpec = tween(durationMillis = 150),
            label = "icon_crossfade_${app.id}"
        ) { bitmap ->
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = app.label,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape)
                )
            } else {
                // Placeholder tipográfico minimalista
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape)
                        .background(Color(0xFFE8ECE1))
                        .border(0.5.dp, Color(0x22000000), shape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.label.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = (size.value * 0.42f).sp,
                        color = accentColor
                    )
                }
            }
        }

        // Insignia visual sutil para perfil de trabajo (Work Profile)
        if (app.isWorkProfile) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .clip(CircleShape)
                    .background(accentColor)
                    .border(1.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Work,
                    contentDescription = "Work Profile",
                    tint = Color.White,
                    modifier = Modifier.size(9.dp)
                )
            }
        }
    }
}
