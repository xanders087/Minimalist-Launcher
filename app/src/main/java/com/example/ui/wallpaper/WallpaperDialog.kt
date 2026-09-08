package com.example.ui.wallpaper

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.LauncherViewModel
import com.example.ui.theme.AccentTheme
import com.example.ui.theme.LauncherColorScheme
import java.io.File

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WallpaperDialog(
    viewModel: LauncherViewModel,
    state: com.example.LauncherState,
    colors: LauncherColorScheme,
    accent: AccentTheme,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val config = state.wallpaperConfig

    // Photo picker for selecting multiple photos for the daily gallery pool
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 15)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.addGalleryPhotos(uris, context)
        }
    }

    // Photo picker for selecting a single static photo
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = WallpaperRepository.saveGalleryUriToInternalStorage(context, uri)
            if (savedPath != null) {
                viewModel.setStaticWallpaper(
                    path = savedPath,
                    isGallery = true,
                    title = "Foto de tu Galería",
                    author = "Dispositivo"
                )
            }
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("dialog_wallpaper_manager"),
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
                                    imageVector = Icons.Default.Wallpaper,
                                    contentDescription = "Fondo de Pantalla",
                                    tint = accent.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Fondo de Pantalla",
                                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Estático o cambio automático diario",
                                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.testTag("btn_close_wallpaper_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = colors.textMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mode Tabs (Solid, Static, Daily)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceVariant)
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    WallpaperMode.entries.forEach { mode ->
                        val isSelected = config.mode == mode
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) accent.primary else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.setWallpaperMode(mode) }
                                .testTag("tab_mode_${mode.name.lowercase()}")
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (mode) {
                                        WallpaperMode.SOLID -> "Minimalista"
                                        WallpaperMode.STATIC -> "Estático"
                                        WallpaperMode.DAILY -> "Diario"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) accent.onPrimary else colors.textSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Content
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // MODE 1: SOLID / MINIMAL
                    if (config.mode == WallpaperMode.SOLID) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = colors.cardBackground,
                                border = BorderStroke(1.dp, colors.cardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = CircleShape,
                                            color = colors.background,
                                            border = BorderStroke(2.dp, accent.primary),
                                            modifier = Modifier.size(32.dp)
                                        ) {}
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "Fondo Minimalista sin Distracciones",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = colors.textPrimary
                                            )
                                            Text(
                                                text = "Acompaña el modo claro / oscuro y ahorro de batería",
                                                fontSize = 11.sp,
                                                color = colors.textMuted
                                            )
                                        }
                                    }

                                    Text(
                                        text = "El lanzador utiliza una base monocromática de máximo contraste con acento ${accent.name}. Selecciona 'Estático' para una foto fija o 'Diario' para renovación automática cada día.",
                                        fontSize = 12.sp,
                                        color = colors.textSecondary,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }

                    // MODE 2: STATIC WALLPAPER
                    if (config.mode == WallpaperMode.STATIC) {
                        item {
                            // Current Static Preview & Gallery Selector Card
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = colors.cardBackground,
                                border = BorderStroke(1.dp, colors.cardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "FONDO ESTÁTICO ACTUAL",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = colors.textMuted
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Current Thumbnail
                                        Box(
                                            modifier = Modifier
                                                .size(72.dp, 100.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(colors.surfaceVariant)
                                        ) {
                                            if (!config.currentWallpaperPath.isNullOrEmpty()) {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(context)
                                                        .data(config.currentWallpaperPath)
                                                        .crossfade(true)
                                                        .build(),
                                                    contentDescription = "Fondo actual",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(14.dp))

                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = config.currentTitle,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = colors.textPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = config.currentSubtitle,
                                                fontSize = 11.sp,
                                                color = colors.textMuted
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            // Gallery Pick Button
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = accent.container,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable {
                                                        singlePhotoPickerLauncher.launch(
                                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                        )
                                                    }
                                                    .testTag("btn_pick_single_gallery_photo")
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.AddPhotoAlternate,
                                                        contentDescription = null,
                                                        tint = accent.primary,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "Elegir de mi galería",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = accent.primary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Curated Wallpapers Catalog for Static Selection
                        item {
                            Text(
                                text = "O ELIGE DEL CATÁLOGO DESTACADO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = colors.textMuted,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        item {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(bottom = 6.dp)
                            ) {
                                items(WallpaperRepository.curatedWallpapers) { wallpaper ->
                                    val isSelected = config.staticWallpaperPath == wallpaper.url
                                    Column(
                                        modifier = Modifier
                                            .width(110.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                viewModel.setStaticWallpaper(
                                                    path = wallpaper.url,
                                                    isGallery = false,
                                                    title = wallpaper.title,
                                                    author = wallpaper.author
                                                )
                                            }
                                            .testTag("curated_item_${wallpaper.id}")
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(150.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .border(
                                                    width = if (isSelected) 2.5.dp else 1.dp,
                                                    color = if (isSelected) accent.primary else colors.cardBorder,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                        ) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(context)
                                                    .data(wallpaper.url)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = wallpaper.title,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )

                                            if (isSelected) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = accent.primary,
                                                    modifier = Modifier
                                                        .padding(6.dp)
                                                        .size(22.dp)
                                                        .align(Alignment.TopEnd)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = "Seleccionado",
                                                            tint = accent.onPrimary,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                    }
                                                }
                                            }

                                            // Category Tag
                                            Surface(
                                                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 6.dp, bottomStart = 6.dp, bottomEnd = 0.dp),
                                                color = Color.Black.copy(alpha = 0.65f),
                                                modifier = Modifier.align(Alignment.BottomStart)
                                            ) {
                                                Text(
                                                    text = wallpaper.category.title,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = wallpaper.title,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.textPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // MODE 3: DAILY AUTOMATIC CHANGE
                    if (config.mode == WallpaperMode.DAILY) {
                        item {
                            // Sub-source Selector: Gallery vs Preferences
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = colors.cardBackground,
                                border = BorderStroke(1.dp, colors.cardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "ORIGEN DEL CAMBIO DIARIO",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = colors.textMuted
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(colors.surfaceVariant)
                                            .padding(3.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        DailySource.entries.forEach { source ->
                                            val isSelected = config.dailySource == source
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (isSelected) accent.primary else Color.Transparent,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable { viewModel.setDailySource(source) }
                                                    .testTag("tab_daily_source_${source.name.lowercase()}")
                                            ) {
                                                Box(
                                                    modifier = Modifier.padding(vertical = 7.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = if (source == DailySource.GALLERY) Icons.Default.Collections else Icons.Default.AutoAwesome,
                                                            contentDescription = null,
                                                            tint = if (isSelected) accent.onPrimary else colors.textSecondary,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = source.title,
                                                            fontSize = 11.sp,
                                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                            color = if (isSelected) accent.onPrimary else colors.textSecondary
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // DAILY SOURCE A: FROM PERSONAL GALLERY
                        if (config.dailySource == DailySource.GALLERY) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = colors.cardBackground,
                                    border = BorderStroke(1.dp, colors.cardBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "Tus Fotos para Rotación Diaria",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = colors.textPrimary
                                                )
                                                Text(
                                                    text = "${config.galleryPhotos.size} fotos en tu catálogo personal",
                                                    fontSize = 11.sp,
                                                    color = colors.textMuted
                                                )
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = accent.primary,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable {
                                                        multiplePhotoPickerLauncher.launch(
                                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                        )
                                                    }
                                                    .testTag("btn_add_gallery_photos")
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.AddPhotoAlternate,
                                                        contentDescription = null,
                                                        tint = accent.onPrimary,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "Añadir fotos",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = accent.onPrimary
                                                    )
                                                }
                                            }
                                        }

                                        if (config.galleryPhotos.isEmpty()) {
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = colors.surfaceVariant,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .padding(20.dp)
                                                        .fillMaxWidth(),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.PhotoLibrary,
                                                        contentDescription = null,
                                                        tint = accent.primary,
                                                        modifier = Modifier.size(36.dp)
                                                    )
                                                    Text(
                                                        text = "Aún no has seleccionado fotos",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp,
                                                        color = colors.textPrimary
                                                    )
                                                    Text(
                                                        text = "Toca 'Añadir fotos' para seleccionar las imágenes de tu galería que cambiarán cada día.",
                                                        fontSize = 11.sp,
                                                        color = colors.textMuted,
                                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                                    )
                                                }
                                            }
                                        } else {
                                            // Grid of gallery photos
                                            FlowRow(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                                maxItemsInEachRow = 3
                                            ) {
                                                config.galleryPhotos.forEachIndexed { index, path ->
                                                    val isCurrentToday = config.currentWallpaperPath == path
                                                    Box(
                                                        modifier = Modifier
                                                            .width(92.dp)
                                                            .height(130.dp)
                                                            .clip(RoundedCornerShape(10.dp))
                                                            .border(
                                                                width = if (isCurrentToday) 2.5.dp else 1.dp,
                                                                color = if (isCurrentToday) accent.primary else colors.cardBorder,
                                                                shape = RoundedCornerShape(10.dp)
                                                            )
                                                    ) {
                                                        AsyncImage(
                                                            model = ImageRequest.Builder(context)
                                                                .data(File(path))
                                                                .crossfade(true)
                                                                .build(),
                                                            contentDescription = "Foto ${index + 1}",
                                                            contentScale = ContentScale.Crop,
                                                            modifier = Modifier.fillMaxSize()
                                                        )

                                                        // Today Badge
                                                        if (isCurrentToday) {
                                                            Surface(
                                                                shape = RoundedCornerShape(bottomEnd = 8.dp),
                                                                color = accent.primary,
                                                                modifier = Modifier.align(Alignment.TopStart)
                                                            ) {
                                                                Text(
                                                                    text = "HOY",
                                                                    fontSize = 9.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = accent.onPrimary,
                                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                                )
                                                            }
                                                        }

                                                        // Delete Button
                                                        Surface(
                                                            shape = CircleShape,
                                                            color = Color.Black.copy(alpha = 0.65f),
                                                            modifier = Modifier
                                                                .padding(4.dp)
                                                                .size(24.dp)
                                                                .align(Alignment.TopEnd)
                                                                .clickable { viewModel.removeGalleryPhoto(path) }
                                                        ) {
                                                            Box(contentAlignment = Alignment.Center) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Delete,
                                                                    contentDescription = "Eliminar",
                                                                    tint = Color.White,
                                                                    modifier = Modifier.size(13.dp)
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
                        }

                        // DAILY SOURCE B: PERSONAL PREFERENCES (ART, NATURE, ETC.)
                        if (config.dailySource == DailySource.PREFERENCES) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = colors.cardBackground,
                                    border = BorderStroke(1.dp, colors.cardBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(
                                            text = "PREFERENCIAS DE CONTENIDO",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                            color = colors.textMuted
                                        )
                                        Text(
                                            text = "Elige uno o más temas. Cada día recibirás una fotografía artística de alta calidad según tus preferencias.",
                                            fontSize = 11.sp,
                                            color = colors.textSecondary
                                        )

                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            WallpaperCategory.entries.forEach { category ->
                                                val isChecked = category in config.selectedCategories
                                                val catIcon = when (category) {
                                                    WallpaperCategory.NATURE -> Icons.Default.Landscape
                                                    WallpaperCategory.ART -> Icons.Default.Brush
                                                    WallpaperCategory.SPACE -> Icons.Default.RocketLaunch
                                                    WallpaperCategory.MINIMAL -> Icons.Default.AutoAwesome
                                                    WallpaperCategory.URBAN -> Icons.Default.LocationCity
                                                }

                                                Surface(
                                                    shape = RoundedCornerShape(10.dp),
                                                    color = if (isChecked) accent.container.copy(alpha = 0.5f) else colors.surfaceVariant,
                                                    border = BorderStroke(
                                                        width = 1.dp,
                                                        color = if (isChecked) accent.primary else Color.Transparent
                                                    ),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .clickable { viewModel.toggleWallpaperCategory(category) }
                                                        .testTag("toggle_category_${category.id}")
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            modifier = Modifier.weight(1f)
                                                        ) {
                                                            Icon(
                                                                imageVector = catIcon,
                                                                contentDescription = null,
                                                                tint = if (isChecked) accent.primary else colors.textMuted,
                                                                modifier = Modifier.size(18.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(10.dp))
                                                            Column {
                                                                Text(
                                                                    text = category.title,
                                                                    fontWeight = FontWeight.Bold,
                                                                    fontSize = 12.sp,
                                                                    color = colors.textPrimary
                                                                )
                                                                Text(
                                                                    text = category.description,
                                                                    fontSize = 10.sp,
                                                                    color = colors.textMuted,
                                                                    maxLines = 1,
                                                                    overflow = TextOverflow.Ellipsis
                                                                )
                                                            }
                                                        }

                                                        Switch(
                                                            checked = isChecked,
                                                            onCheckedChange = { viewModel.toggleWallpaperCategory(category) },
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
                                        }
                                    }
                                }
                            }
                        }

                        // TODAY'S ACTIVE DAILY WALLPAPER BANNER
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = colors.cardBackground,
                                border = BorderStroke(1.dp, colors.cardBorder),
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
                                        Text(
                                            text = "FOTO ACTIVA DE HOY",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                            color = colors.textMuted
                                        )

                                        // Refresh / Next Image Button
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = accent.container,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { viewModel.shuffleOrNextDailyWallpaper() }
                                                .testTag("btn_shuffle_daily_wallpaper")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Refresh,
                                                    contentDescription = null,
                                                    tint = accent.primary,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Probar otra",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = accent.primary
                                                )
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(72.dp, 96.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(colors.surfaceVariant)
                                        ) {
                                            if (!config.currentWallpaperPath.isNullOrEmpty()) {
                                                val model = if (config.currentWallpaperPath.startsWith("/")) {
                                                    File(config.currentWallpaperPath)
                                                } else {
                                                    config.currentWallpaperPath
                                                }
                                                AsyncImage(
                                                    model = ImageRequest.Builder(context)
                                                        .data(model)
                                                        .crossfade(true)
                                                        .build(),
                                                    contentDescription = "Foto de hoy",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(14.dp))

                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = config.currentTitle,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = colors.textPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = config.currentSubtitle,
                                                fontSize = 11.sp,
                                                color = colors.textMuted,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "Se actualizará automáticamente mañana",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = accent.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // DIMMING / LEGIBILITY SCRIM CONTROL (Only for image wallpapers)
                    if (config.mode != WallpaperMode.SOLID) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = colors.cardBackground,
                                border = BorderStroke(1.dp, colors.cardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Tune,
                                                contentDescription = null,
                                                tint = accent.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Atenuación de Contraste",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = colors.textPrimary
                                            )
                                        }

                                        Text(
                                            text = "${(config.dimmingAlpha * 100).toInt()}%",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = accent.primary
                                        )
                                    }

                                    Text(
                                        text = "Oscurece sutilmente la imagen para que la hora y nombres de aplicaciones se lean con total claridad.",
                                        fontSize = 10.sp,
                                        color = colors.textMuted
                                    )

                                    Slider(
                                        value = config.dimmingAlpha,
                                        onValueChange = { viewModel.setWallpaperDimming(it) },
                                        valueRange = 0.15f..0.75f,
                                        steps = 11,
                                        colors = SliderDefaults.colors(
                                            thumbColor = accent.primary,
                                            activeTrackColor = accent.primary,
                                            inactiveTrackColor = colors.divider
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("slider_wallpaper_dimming")
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Done Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = accent.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onDismissRequest() }
                        .testTag("btn_wallpaper_done")
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aplicar y Continuar",
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
