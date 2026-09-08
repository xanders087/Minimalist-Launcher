package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AppItem
import com.example.data.repository.AppRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LauncherScreenDebounceTest {

    @Test
    fun `test onSearchQueryChanged updates searchQuery StateFlow`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = LauncherViewModel(app)

        assertEquals("", viewModel.searchQuery.value)

        viewModel.onSearchQueryChanged("Settings")
        assertEquals("Settings", viewModel.searchQuery.value)

        viewModel.onSearchQueryChanged("")
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `test debouncedFilteredApps stream initialization`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = LauncherViewModel(app)

        assertNotNull(viewModel.debouncedFilteredApps)
        assertNotNull(viewModel.debouncedFilteredApps.value)
    }
}
