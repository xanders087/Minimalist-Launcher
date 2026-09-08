package com.example

import com.example.ui.gestures.GestureAction
import com.example.ui.gestures.GestureActionHandler
import com.example.ui.gestures.GestureSensitivity
import com.example.ui.gestures.GesturesConfig
import com.example.ui.gestures.HomeGestureType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GestureModelsTest {

    @Test
    fun defaultGesturesConfig_hasExpectedMappings() {
        val config = GesturesConfig()
        assertTrue(config.gesturesEnabled)
        assertTrue(config.hapticFeedbackEnabled)
        assertEquals(GestureAction.LOCK_SCREEN, config.doubleTapAction)
        assertEquals(GestureAction.OPEN_NOTIFICATIONS, config.swipeDownAction)
        assertEquals(GestureAction.OPEN_APP_DRAWER, config.swipeUpAction)
        assertEquals(GestureAction.OPEN_SETTINGS, config.longPressAction)
        assertEquals(GestureAction.OPEN_QUICK_SETTINGS, config.twoFingerSwipeDownAction)
        assertEquals(GestureSensitivity.MEDIUM, config.sensitivity)
    }

    @Test
    fun withActionFor_updatesSpecificGesture() {
        val config = GesturesConfig()

        val updatedDoubleTap = config.withActionFor(HomeGestureType.DOUBLE_TAP, GestureAction.CYCLE_THEME)
        assertEquals(GestureAction.CYCLE_THEME, updatedDoubleTap.doubleTapAction)
        assertEquals(GestureAction.OPEN_NOTIFICATIONS, updatedDoubleTap.swipeDownAction)

        val updatedSwipeDown = config.withActionFor(HomeGestureType.SWIPE_DOWN, GestureAction.LOCK_SCREEN)
        assertEquals(GestureAction.LOCK_SCREEN, updatedSwipeDown.swipeDownAction)

        val updatedTwoFinger = config.withActionFor(HomeGestureType.TWO_FINGER_SWIPE_DOWN, GestureAction.TOGGLE_FOCUS_MODE)
        assertEquals(GestureAction.TOGGLE_FOCUS_MODE, updatedTwoFinger.twoFingerSwipeDownAction)
    }

    @Test
    fun gestureSensitivity_thresholdsAreOrdered() {
        assertTrue(GestureSensitivity.HIGH.distanceThresholdDp < GestureSensitivity.MEDIUM.distanceThresholdDp)
        assertTrue(GestureSensitivity.MEDIUM.distanceThresholdDp < GestureSensitivity.LOW.distanceThresholdDp)
    }

    @Test
    fun gestureCallbacks_invokeCorrespondingActions() {
        var drawerOpened = false
        var settingsOpened = false
        var standbyActivated = false

        val callbacks = GestureActionHandler.Callbacks(
            onOpenDrawer = { drawerOpened = true },
            onOpenSettings = { settingsOpened = true },
            onActivateStandbyLock = { standbyActivated = true }
        )

        callbacks.onOpenDrawer()
        assertTrue(drawerOpened)

        callbacks.onOpenSettings()
        assertTrue(settingsOpened)

        callbacks.onActivateStandbyLock()
        assertTrue(standbyActivated)
    }
}
