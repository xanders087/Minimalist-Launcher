package com.example

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.os.Process
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AppItem
import com.example.domain.launcher.LaunchAppUseCase
import com.example.domain.launcher.LaunchResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LaunchAppUseCaseTest {

    @Test
    fun `test launch valid package injects critical flags NEW_TASK and RESET_TASK_IF_NEEDED`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val useCase = LaunchAppUseCase(app)

        val result = useCase("com.android.settings")
        assertTrue("LaunchResult should be Success", result is LaunchResult.Success)

        val shadowApp = shadowOf(app)
        val startedIntent = shadowApp.nextStartedActivity
        assertNotNull("Intent should be started", startedIntent)

        val hasNewTask = (startedIntent.flags and Intent.FLAG_ACTIVITY_NEW_TASK) != 0
        val hasResetTask = (startedIntent.flags and Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED) != 0
        assertTrue("Must have FLAG_ACTIVITY_NEW_TASK", hasNewTask)
        assertTrue("Must have FLAG_ACTIVITY_RESET_TASK_IF_NEEDED", hasResetTask)
    }

    @Test
    fun `test launch unknown package triggers Level 3 system action fallback or AppNotFound`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val useCase = LaunchAppUseCase(app)

        // Dialer fallback test
        val dialerResult = useCase("com.unknown.dialer.app")
        assertTrue("Dialer fallback should succeed via Level 3 system action", dialerResult is LaunchResult.Success)

        // Completely unknown app without fallback returns AppNotFound
        val unknownResult = useCase("com.completely.nonexistent.dummy.xyz")
        assertTrue("Completely unknown package should return AppNotFound", unknownResult is LaunchResult.AppNotFound)
    }

    @Test
    fun `test launch AppItem with component and userHandle`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val useCase = LaunchAppUseCase(app)

        val appItem = AppItem(
            packageName = "com.android.settings",
            label = "Settings",
            userHandle = Process.myUserHandle(),
            componentName = ComponentName("com.android.settings", "com.android.settings.Settings")
        )

        val result = useCase(appItem)
        assertTrue(result is LaunchResult.Success)
    }

    @Test
    fun `test empty package name returns AppNotFound`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val useCase = LaunchAppUseCase(app)

        val result = useCase("")
        assertTrue(result is LaunchResult.AppNotFound)
    }
}
