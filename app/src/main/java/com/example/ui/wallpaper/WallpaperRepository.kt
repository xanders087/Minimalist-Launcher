package com.example.ui.wallpaper

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

object WallpaperRepository {

    val curatedWallpapers: List<CuratedWallpaper> = listOf(
        // NATURE
        CuratedWallpaper(
            id = "nat_1",
            url = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.NATURE,
            title = "Lago en el Valle",
            author = "Bailey Zindel"
        ),
        CuratedWallpaper(
            id = "nat_2",
            url = "https://images.unsplash.com/photo-1448375240586-882707db888b?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.NATURE,
            title = "Niebla en el Bosque",
            author = "Sebastian Unrau"
        ),
        CuratedWallpaper(
            id = "nat_3",
            url = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.NATURE,
            title = "Cumbres Alpinas",
            author = "Kalen Emsley"
        ),
        CuratedWallpaper(
            id = "nat_4",
            url = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.NATURE,
            title = "Océano Pacífico",
            author = "Sean Oulashin"
        ),
        CuratedWallpaper(
            id = "nat_5",
            url = "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.NATURE,
            title = "Dunas Doradas",
            author = "Jeremy Bishop"
        ),
        CuratedWallpaper(
            id = "nat_6",
            url = "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.NATURE,
            title = "Valle Verde al Amanecer",
            author = "Luca Bravo"
        ),

        // ART & DESIGN
        CuratedWallpaper(
            id = "art_1",
            url = "https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.ART,
            title = "Curvas Arquitectónicas",
            author = "Simone Hutsch"
        ),
        CuratedWallpaper(
            id = "art_2",
            url = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.ART,
            title = "Escalera Helicoidal",
            author = "Samuel Zeller"
        ),
        CuratedWallpaper(
            id = "art_3",
            url = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.ART,
            title = "Pigmentos Óleo",
            author = "Steve Johnson"
        ),
        CuratedWallpaper(
            id = "art_4",
            url = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.ART,
            title = "Fachada de Cristal",
            author = "Joel Filipe"
        ),
        CuratedWallpaper(
            id = "art_5",
            url = "https://images.unsplash.com/photo-1541701494587-cb58502866ab?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.ART,
            title = "Acrílico Abstracto",
            author = "Geordanna Cordero"
        ),

        // SPACE & COSMOS
        CuratedWallpaper(
            id = "spc_1",
            url = "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.SPACE,
            title = "Vía Láctea Infinita",
            author = "Adrian Pelletier"
        ),
        CuratedWallpaper(
            id = "spc_2",
            url = "https://images.unsplash.com/photo-1531366936337-7c912a4589a7?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.SPACE,
            title = "Aurora Boreal Nórdica",
            author = "Jonatan Pie"
        ),
        CuratedWallpaper(
            id = "spc_3",
            url = "https://images.unsplash.com/photo-1519681393784-d120267933ba?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.SPACE,
            title = "Cielo Estrellado",
            author = "Benjamin Voros"
        ),
        CuratedWallpaper(
            id = "spc_4",
            url = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.SPACE,
            title = "Atmósfera y Cosmos",
            author = "NASA Hubble"
        ),
        CuratedWallpaper(
            id = "spc_5",
            url = "https://images.unsplash.com/photo-1532693322450-2cb5c511067d?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.SPACE,
            title = "Creciente Lunar",
            author = "Mark Tegethoff"
        ),

        // MINIMAL & TEXTURES
        CuratedWallpaper(
            id = "min_1",
            url = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.MINIMAL,
            title = "Ondas Fluídas Oscuras",
            author = "Milad Fakurian"
        ),
        CuratedWallpaper(
            id = "min_2",
            url = "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.MINIMAL,
            title = "Gradiente Líquido",
            author = "Jr Korpa"
        ),
        CuratedWallpaper(
            id = "min_3",
            url = "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.MINIMAL,
            title = "Textura de Seda",
            author = "Pawel Czerwinski"
        ),
        CuratedWallpaper(
            id = "min_4",
            url = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.MINIMAL,
            title = "Luz y Sombras Geométricas",
            author = "R Architecture"
        ),

        // URBAN & CITIES
        CuratedWallpaper(
            id = "urb_1",
            url = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.URBAN,
            title = "Luces Nocturnas de Tokio",
            author = "Aleksandar Pasaric"
        ),
        CuratedWallpaper(
            id = "urb_2",
            url = "https://images.unsplash.com/photo-1477959858617-67f30bc75b82?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.URBAN,
            title = "Horizonte Urbano al Atardecer",
            author = "Sawyer Bengtson"
        ),
        CuratedWallpaper(
            id = "urb_3",
            url = "https://images.unsplash.com/photo-1449034446853-66c86144b0ad?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.URBAN,
            title = "Puente en la Niebla",
            author = "Joseph Barrientos"
        ),
        CuratedWallpaper(
            id = "urb_4",
            url = "https://images.unsplash.com/photo-1514565131-fce0801e5785?auto=format&fit=crop&w=1080&q=80",
            category = WallpaperCategory.URBAN,
            title = "Lluvia y Reflejos de Neón",
            author = "Aleksandar Pasaric"
        )
    )

    fun getTodayDateKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    /**
     * Copies a photo selected from the Android Photo Picker URI into app-private storage.
     * Returns the local file absolute path.
     */
    fun saveGalleryUriToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val wallpapersDir = File(context.filesDir, "custom_wallpapers")
            if (!wallpapersDir.exists()) {
                wallpapersDir.mkdirs()
            }
            val fileName = "wp_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg"
            val destinationFile = File(wallpapersDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }
            destinationFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Deletes a local custom photo from internal storage.
     */
    fun deleteCustomPhoto(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            if (file.exists()) file.delete() else false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Selects a wallpaper for today based on the user's daily source and preferences.
     */
    fun resolveDailyWallpaper(
        dailySource: DailySource,
        galleryPhotos: List<String>,
        selectedCategories: Set<WallpaperCategory>,
        seedOffset: Int = 0
    ): Pair<String, Pair<String, String>> {
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR) + seedOffset
        val year = calendar.get(Calendar.YEAR)

        return when (dailySource) {
            DailySource.GALLERY -> {
                if (galleryPhotos.isNotEmpty()) {
                    val index = Math.floorMod(dayOfYear, galleryPhotos.size)
                    val path = galleryPhotos[index]
                    val photoNumber = index + 1
                    Pair(path, Pair("Foto de mi Galería ($photoNumber/${galleryPhotos.size})", "Cambio diario automático"))
                } else {
                    // Fallback to curated if gallery has no photos yet
                    resolveCuratedDaily(selectedCategories, dayOfYear, year)
                }
            }
            DailySource.PREFERENCES -> {
                resolveCuratedDaily(selectedCategories, dayOfYear, year)
            }
        }
    }

    private fun resolveCuratedDaily(
        selectedCategories: Set<WallpaperCategory>,
        dayOfYear: Int,
        year: Int
    ): Pair<String, Pair<String, String>> {
        val availableWallpapers = if (selectedCategories.isNotEmpty()) {
            curatedWallpapers.filter { it.category in selectedCategories }
        } else {
            curatedWallpapers
        }.ifEmpty { curatedWallpapers }

        val hash = (dayOfYear * 31 + year)
        val index = Math.floorMod(hash, availableWallpapers.size)
        val selected = availableWallpapers[index]

        return Pair(
            selected.url,
            Pair(selected.title, "${selected.category.title} • Foto por ${selected.author}")
        )
    }

    /**
     * Picks the next wallpaper (for manual shuffle / preview button).
     */
    fun pickNextWallpaper(
        dailySource: DailySource,
        galleryPhotos: List<String>,
        selectedCategories: Set<WallpaperCategory>,
        currentPath: String?
    ): Pair<String, Pair<String, String>> {
        return when (dailySource) {
            DailySource.GALLERY -> {
                if (galleryPhotos.isNotEmpty()) {
                    val currentIndex = galleryPhotos.indexOf(currentPath)
                    val nextIndex = (currentIndex + 1).coerceAtLeast(0) % galleryPhotos.size
                    val nextPath = galleryPhotos[nextIndex]
                    Pair(nextPath, Pair("Foto de mi Galería (${nextIndex + 1}/${galleryPhotos.size})", "Seleccionada manualmente"))
                } else {
                    val candidate = curatedWallpapers.random()
                    Pair(candidate.url, Pair(candidate.title, "${candidate.category.title} • ${candidate.author}"))
                }
            }
            DailySource.PREFERENCES -> {
                val availableWallpapers = if (selectedCategories.isNotEmpty()) {
                    curatedWallpapers.filter { it.category in selectedCategories }
                } else {
                    curatedWallpapers
                }.ifEmpty { curatedWallpapers }

                val candidates = availableWallpapers.filter { it.url != currentPath }
                val selected = if (candidates.isNotEmpty()) candidates.random() else availableWallpapers.random()
                Pair(selected.url, Pair(selected.title, "${selected.category.title} • ${selected.author}"))
            }
        }
    }

    /**
     * Calculates the average perceived luminance of a Bitmap (0.0 = black, 1.0 = white).
     * Uses fast downsampled grid sampling (< 1ms).
     */
    fun calculateBitmapLuminance(bitmap: android.graphics.Bitmap): Float {
        return try {
            val width = bitmap.width
            val height = bitmap.height
            val stepX = (width / 16).coerceAtLeast(1)
            val stepY = (height / 16).coerceAtLeast(1)
            var totalLum = 0.0
            var count = 0

            var y = 0
            while (y < height) {
                var x = 0
                while (x < width) {
                    val pixel = bitmap.getPixel(x, y)
                    val r = android.graphics.Color.red(pixel) / 255.0
                    val g = android.graphics.Color.green(pixel) / 255.0
                    val b = android.graphics.Color.blue(pixel) / 255.0
                    val lum = 0.299 * r + 0.587 * g + 0.114 * b
                    totalLum += lum
                    count++
                    x += stepX
                }
                y += stepY
            }
            if (count > 0) (totalLum / count).toFloat().coerceIn(0f, 1f) else 0.35f
        } catch (e: Exception) {
            0.35f
        }
    }

    /**
     * Calculates perceived luminance from a local image file.
     */
    fun calculateFileLuminance(filePath: String): Float {
        return try {
            val options = android.graphics.BitmapFactory.Options().apply {
                inSampleSize = 8
            }
            val bitmap = android.graphics.BitmapFactory.decodeFile(filePath, options) ?: return 0.35f
            val lum = calculateBitmapLuminance(bitmap)
            bitmap.recycle()
            lum
        } catch (e: Exception) {
            0.35f
        }
    }

    /**
     * Retrieves the calibrated base luminance or estimates it for the given path.
     */
    fun getEstimatedLuminance(path: String?): Float {
        if (path == null) return 0.35f
        if (path.startsWith("/")) {
            return calculateFileLuminance(path)
        }
        val curated = curatedWallpapers.find { it.url == path }
        return curated?.baseLuminance ?: 0.35f
    }
}
