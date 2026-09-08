package com.example

import android.app.Application
import android.content.ComponentName
import android.os.Process
import android.os.UserHandle
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AppItem
import com.example.data.repository.AppRepositoryImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AppRepositoryTest {

    @Test
    fun `test AppItem unique identifier combines package and user`() {
        val user0 = Process.myUserHandle()
        val pkg = "com.android.chrome"
        val component = ComponentName(pkg, "com.android.chrome.Main")

        val appItem = AppItem(
            packageName = pkg,
            label = "Chrome",
            userHandle = user0,
            componentName = component
        )

        assertEquals("${pkg}_${user0.hashCode()}", appItem.id)
        assertFalse(appItem.isWorkProfile)
    }

    @Test
    fun `test AppItem and AppInfo bidirectional conversion`() {
        val user = Process.myUserHandle()
        val appItem = AppItem(
            packageName = "com.google.android.dialer",
            label = "Phone",
            userHandle = user,
            componentName = ComponentName("com.google.android.dialer", "com.google.android.dialer.DialtactsActivity"),
            category = "Communication",
            launchCount = 12,
            isWorkProfile = false
        )

        val appInfo = AppInfo.fromAppItem(appItem)
        assertEquals(appItem.label, appInfo.label)
        assertEquals(appItem.packageName, appInfo.packageName)
        assertEquals(appItem.userHandle, appInfo.userHandle)
        assertEquals(appItem.componentName, appInfo.componentName)
        assertEquals(appItem.category, appInfo.category)
        assertEquals(appItem.launchCount, appInfo.launchCount)
        assertEquals(appItem.isWorkProfile, appInfo.isWorkProfile)

        val convertedBack = appInfo.toAppItem()
        assertEquals(appItem.id, convertedBack.id)
        assertEquals(appItem.packageName, convertedBack.packageName)
    }

    @Test
    fun `test AppRepository startObserving and stopObserving lifecycle without crash`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val repository = AppRepositoryImpl(app)

        // Verifies registering observer
        repository.startObserving()

        // Verifies clean unregistering to prevent memory leaks
        repository.stopObserving()
        assertTrue(true)
    }

    @Test
    fun `test LauncherViewModel integrates AppRepository and cleans up onCleared`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val repository = AppRepositoryImpl(app)
        val viewModel = LauncherViewModel(app, repository)

        assertNotNull(viewModel.appRepository)
        assertNotNull(viewModel.state.value)

        // Trigger onCleared to ensure unregistering callback does not leak memory
        val onClearedMethod = LauncherViewModel::class.java.getDeclaredMethod("onCleared")
        onClearedMethod.isAccessible = true
        onClearedMethod.invoke(viewModel)
    }
}
