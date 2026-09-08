package com.example

import com.example.ui.wallpaper.DailySource
import com.example.ui.wallpaper.WallpaperCategory
import com.example.ui.wallpaper.WallpaperRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WallpaperRepositoryTest {

    @Test
    fun testCuratedWallpapersCategoriesPresent() {
        val categoriesInCatalog = WallpaperRepository.curatedWallpapers.map { it.category }.toSet()
        WallpaperCategory.entries.forEach { category ->
            assertTrue("Expected category ${category.name} in catalog", categoriesInCatalog.contains(category))
        }
    }

    @Test
    fun testResolveDailyWallpaperPreferences() {
        val selectedCategories = setOf(WallpaperCategory.NATURE, WallpaperCategory.SPACE)
        val (url, info) = WallpaperRepository.resolveDailyWallpaper(
            dailySource = DailySource.PREFERENCES,
            galleryPhotos = emptyList(),
            selectedCategories = selectedCategories
        )

        assertNotNull(url)
        assertTrue(url.startsWith("http"))
        assertNotNull(info.first)
        assertNotNull(info.second)
    }

    @Test
    fun testResolveDailyWallpaperGalleryFallbackWhenEmpty() {
        val (url, info) = WallpaperRepository.resolveDailyWallpaper(
            dailySource = DailySource.GALLERY,
            galleryPhotos = emptyList(),
            selectedCategories = setOf(WallpaperCategory.MINIMAL)
        )

        assertNotNull(url)
        assertTrue(url.startsWith("http"))
    }

    @Test
    fun testResolveDailyWallpaperGalleryWithPhotos() {
        val sampleGallery = listOf("/data/user/0/com.example/files/wp1.jpg", "/data/user/0/com.example/files/wp2.jpg")
        val (path, info) = WallpaperRepository.resolveDailyWallpaper(
            dailySource = DailySource.GALLERY,
            galleryPhotos = sampleGallery,
            selectedCategories = emptySet()
        )

        assertTrue(sampleGallery.contains(path))
        assertTrue(info.first.contains("Galería"))
    }

    @Test
    fun testPickNextWallpaperCycles() {
        val selectedCategories = setOf(WallpaperCategory.ART)
        val first = WallpaperRepository.resolveDailyWallpaper(
            dailySource = DailySource.PREFERENCES,
            galleryPhotos = emptyList(),
            selectedCategories = selectedCategories
        )

        val next = WallpaperRepository.pickNextWallpaper(
            dailySource = DailySource.PREFERENCES,
            galleryPhotos = emptyList(),
            selectedCategories = selectedCategories,
            currentPath = first.first
        )

        assertNotNull(next.first)
    }
}
