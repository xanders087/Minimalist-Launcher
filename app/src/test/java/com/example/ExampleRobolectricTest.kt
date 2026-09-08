package com.example

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.example.ui.home.createAppLaunchIntent
import com.example.ui.home.launchApp
import com.example.ui.widget.WidgetType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Minimalist Launcher", appName)
  }

  @Test
  fun `verify widget default state and pinning`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = LauncherViewModel(app)
    val state = viewModel.state.value

    // Default active widgets should include Weather and Calendar
    val activeTypes = state.activeWidgets.map { it.type }
    assertTrue(activeTypes.contains(WidgetType.WEATHER))
    assertTrue(activeTypes.contains(WidgetType.CALENDAR))

    // Toggle Focus Goal pinned
    viewModel.toggleWidgetPinned(WidgetType.FOCUS_GOAL)
    val updatedState = viewModel.state.value
    assertTrue(updatedState.activeWidgets.any { it.type == WidgetType.FOCUS_GOAL })

    // Weather unit toggle test
    val initialCelsius = updatedState.weatherData.isCelsius
    viewModel.toggleWeatherUnit()
    assertEquals(!initialCelsius, viewModel.state.value.weatherData.isCelsius)

    // Add calendar event test
    val initialEventsCount = viewModel.state.value.calendarEvents.size
    viewModel.addCalendarEvent("Product Demo", "04:00 PM", "Studio A")
    assertEquals(initialEventsCount + 1, viewModel.state.value.calendarEvents.size)
    assertEquals("Product Demo", viewModel.state.value.calendarEvents.first().title)

    // Focus goal toggle test
    val initialStreak = viewModel.state.value.focusGoal.streakDays
    viewModel.toggleFocusGoal()
    assertTrue(viewModel.state.value.focusGoal.isCompleted)
    assertEquals(initialStreak + 1, viewModel.state.value.focusGoal.streakDays)
  }

  @Test
  fun `verify hiding and unhiding specific apps from settings`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = LauncherViewModel(app)

    // Initial state
    assertFalse(viewModel.state.value.hiddenPackages.contains("com.android.chrome"))

    // Hide app
    viewModel.hideApp("com.android.chrome")
    assertTrue(viewModel.state.value.hiddenPackages.contains("com.android.chrome"))
    assertFalse(viewModel.state.value.visibleApps.any { it.packageName == "com.android.chrome" })

    // Unhide app
    viewModel.unhideApp("com.android.chrome")
    assertFalse(viewModel.state.value.hiddenPackages.contains("com.android.chrome"))
    assertTrue(viewModel.state.value.visibleApps.any { it.packageName == "com.android.chrome" })

    // Hide multiple and unhide all
    viewModel.hideApp("com.android.chrome")
    viewModel.hideApp("com.google.android.dialer")
    assertEquals(2, viewModel.state.value.hiddenPackages.size)

    viewModel.unhideAllApps()
    assertTrue(viewModel.state.value.hiddenPackages.isEmpty())
  }

  @Test
  fun `verify createAppLaunchIntent creates system intent with FLAG_ACTIVITY_NEW_TASK`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val packageName = "com.android.settings"

    val intent = createAppLaunchIntent(app, packageName)
    assertNotNull(intent)
    val hasNewTaskFlag = (intent.flags and Intent.FLAG_ACTIVITY_NEW_TASK) != 0
    assertTrue("Launch intent must have FLAG_ACTIVITY_NEW_TASK flag", hasNewTaskFlag)
  }

  @Test
  fun `verify app click launchApp triggers Android system intent startActivity`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val packageName = "com.android.chrome"

    launchApp(app, packageName)

    val shadowApp = shadowOf(app)
    val startedIntent = shadowApp.nextStartedActivity
    assertNotNull("A system intent should be started via startActivity", startedIntent)
    val hasNewTaskFlag = (startedIntent.flags and Intent.FLAG_ACTIVITY_NEW_TASK) != 0
    assertTrue("Started intent must have FLAG_ACTIVITY_NEW_TASK flag", hasNewTaskFlag)
  }

  @Test
  fun `verify clicking app records launch count in LauncherViewModel`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = LauncherViewModel(app)
    val packageName = "com.google.android.dialer"

    val initialCount = viewModel.getAppLaunchCount(packageName)
    viewModel.recordAppLaunch(packageName)
    val newCount = viewModel.getAppLaunchCount(packageName)

    assertEquals(initialCount + 1, newCount)
  }
}

