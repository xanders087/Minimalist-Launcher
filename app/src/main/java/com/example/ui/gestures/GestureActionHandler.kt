package com.example.ui.gestures

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.widget.Toast

class GestureActionHandler {

    data class Callbacks(
        val onOpenDrawer: () -> Unit = {},
        val onOpenSettings: () -> Unit = {},
        val onOpenWallpapers: () -> Unit = {},
        val onToggleFocusMode: () -> Unit = {},
        val onOpenSearch: () -> Unit = {},
        val onCycleTheme: () -> Unit = {},
        val onActivateStandbyLock: () -> Unit = {}
    )

    companion object {
        fun execute(
            action: GestureAction,
            context: Context,
            callbacks: Callbacks,
            hapticEnabled: Boolean = true
        ) {
            if (action == GestureAction.NONE) return

            if (hapticEnabled) {
                triggerHaptic(context)
            }

            when (action) {
                GestureAction.LOCK_SCREEN -> {
                    val locked = LauncherAccessibilityService.lockScreen()
                    if (!locked) {
                        // Fallback to AMOLED Standby Lock overlay
                        callbacks.onActivateStandbyLock()
                    }
                }
                GestureAction.OPEN_NOTIFICATIONS -> {
                    val opened = LauncherAccessibilityService.openNotifications()
                    if (!opened) {
                        expandNotificationsReflect(context)
                    }
                }
                GestureAction.OPEN_QUICK_SETTINGS -> {
                    val opened = LauncherAccessibilityService.openQuickSettings()
                    if (!opened) {
                        expandQuickSettingsReflect(context)
                    }
                }
                GestureAction.OPEN_APP_DRAWER -> callbacks.onOpenDrawer()
                GestureAction.OPEN_SETTINGS -> callbacks.onOpenSettings()
                GestureAction.OPEN_WALLPAPERS -> callbacks.onOpenWallpapers()
                GestureAction.TOGGLE_FOCUS_MODE -> callbacks.onToggleFocusMode()
                GestureAction.OPEN_SEARCH -> callbacks.onOpenSearch()
                GestureAction.CYCLE_THEME -> callbacks.onCycleTheme()
                GestureAction.NONE -> Unit
            }
        }

        @SuppressLint("WrongConstant")
        fun expandNotificationsReflect(context: Context): Boolean {
            return try {
                val statusBarService = context.getSystemService("statusbar")
                val statusBarManager = Class.forName("android.app.StatusBarManager")
                val method = statusBarManager.getMethod("expandNotificationsPanel")
                method.invoke(statusBarService)
                true
            } catch (e: Exception) {
                Toast.makeText(context, "Notificaciones (Gesto detectado)", Toast.LENGTH_SHORT).show()
                false
            }
        }

        @SuppressLint("WrongConstant")
        fun expandQuickSettingsReflect(context: Context): Boolean {
            return try {
                val statusBarService = context.getSystemService("statusbar")
                val statusBarManager = Class.forName("android.app.StatusBarManager")
                val method = statusBarManager.getMethod("expandSettingsPanel")
                method.invoke(statusBarService)
                true
            } catch (e: Exception) {
                Toast.makeText(context, "Ajustes rápidos (Gesto detectado)", Toast.LENGTH_SHORT).show()
                false
            }
        }

        fun triggerHaptic(context: Context) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    val vibrator = vibratorManager?.defaultVibrator
                    vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                } else {
                    @Suppress("DEPRECATION")
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator?.vibrate(35)
                    }
                }
            } catch (e: Exception) {
                // Ignore if device lacks vibration
            }
        }

        fun openAccessibilitySettings(context: Context) {
            try {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Abre Ajustes > Accesibilidad para activar el servicio", Toast.LENGTH_LONG).show()
            }
        }
    }
}
