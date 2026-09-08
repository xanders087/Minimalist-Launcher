package com.example.ui.gestures

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.ui.graphics.vector.ImageVector

enum class HomeGestureType(
    val title: String,
    val subtitle: String,
    val identifier: String
) {
    DOUBLE_TAP(
        title = "Doble toque",
        subtitle = "Toca dos veces sobre el fondo de pantalla",
        identifier = "double_tap"
    ),
    SWIPE_DOWN(
        title = "Deslizar hacia abajo",
        subtitle = "Desliza un dedo hacia abajo en cualquier zona",
        identifier = "swipe_down"
    ),
    SWIPE_UP(
        title = "Deslizar hacia arriba",
        subtitle = "Desliza un dedo hacia arriba en la pantalla",
        identifier = "swipe_up"
    ),
    LONG_PRESS(
        title = "Pulsación prolongada",
        subtitle = "Mantén presionado el fondo de pantalla",
        identifier = "long_press"
    ),
    TWO_FINGER_SWIPE_DOWN(
        title = "Deslizar 2 dedos hacia abajo",
        subtitle = "Desliza con dos dedos simultáneamente hacia abajo",
        identifier = "two_finger_swipe_down"
    )
}

enum class GestureAction(
    val title: String,
    val description: String,
    val icon: ImageVector
) {
    LOCK_SCREEN(
        title = "Bloquear pantalla",
        description = "Apaga la pantalla o activa el modo standby inmediato",
        icon = Icons.Default.Lock
    ),
    OPEN_NOTIFICATIONS(
        title = "Abrir notificaciones",
        description = "Despliega el panel de notificaciones del sistema",
        icon = Icons.Default.Notifications
    ),
    OPEN_QUICK_SETTINGS(
        title = "Ajustes rápidos",
        description = "Despliega el panel de control y accesos rápidos",
        icon = Icons.Default.Widgets
    ),
    OPEN_APP_DRAWER(
        title = "Cajón de aplicaciones",
        description = "Abre la lista completa de aplicaciones",
        icon = Icons.Default.TouchApp
    ),
    OPEN_SETTINGS(
        title = "Ajustes del Launcher",
        description = "Abre el diálogo de ajustes y personalización",
        icon = Icons.Default.Settings
    ),
    OPEN_WALLPAPERS(
        title = "Fondos de pantalla",
        description = "Abre el gestor de fondos estáticos y diarios",
        icon = Icons.Default.Wallpaper
    ),
    TOGGLE_FOCUS_MODE(
        title = "Alternar Modo Enfoque",
        description = "Activa o desactiva el modo Cero Distracciones",
        icon = Icons.Default.VisibilityOff
    ),
    OPEN_SEARCH(
        title = "Búsqueda rápida",
        description = "Abre el buscador directo de aplicaciones",
        icon = Icons.Default.Search
    ),
    CYCLE_THEME(
        title = "Alternar tema claro / oscuro",
        description = "Cambia inmediatamente entre modos de color",
        icon = Icons.Default.BrightnessMedium
    ),
    NONE(
        title = "Ninguna acción",
        description = "Gesto desactivado",
        icon = Icons.Default.Block
    )
}

enum class GestureSensitivity(
    val title: String,
    val distanceThresholdDp: Float
) {
    HIGH("Alta (30 dp)", 30f),
    MEDIUM("Media (50 dp)", 50f),
    LOW("Baja (80 dp)", 80f)
}

data class GesturesConfig(
    val doubleTapAction: GestureAction = GestureAction.LOCK_SCREEN,
    val swipeDownAction: GestureAction = GestureAction.OPEN_NOTIFICATIONS,
    val swipeUpAction: GestureAction = GestureAction.OPEN_APP_DRAWER,
    val longPressAction: GestureAction = GestureAction.OPEN_SETTINGS,
    val twoFingerSwipeDownAction: GestureAction = GestureAction.OPEN_QUICK_SETTINGS,
    val hapticFeedbackEnabled: Boolean = true,
    val sensitivity: GestureSensitivity = GestureSensitivity.MEDIUM,
    val gesturesEnabled: Boolean = true
) {
    fun getActionFor(type: HomeGestureType): GestureAction = when (type) {
        HomeGestureType.DOUBLE_TAP -> doubleTapAction
        HomeGestureType.SWIPE_DOWN -> swipeDownAction
        HomeGestureType.SWIPE_UP -> swipeUpAction
        HomeGestureType.LONG_PRESS -> longPressAction
        HomeGestureType.TWO_FINGER_SWIPE_DOWN -> twoFingerSwipeDownAction
    }

    fun withActionFor(type: HomeGestureType, action: GestureAction): GesturesConfig = when (type) {
        HomeGestureType.DOUBLE_TAP -> copy(doubleTapAction = action)
        HomeGestureType.SWIPE_DOWN -> copy(swipeDownAction = action)
        HomeGestureType.SWIPE_UP -> copy(swipeUpAction = action)
        HomeGestureType.LONG_PRESS -> copy(longPressAction = action)
        HomeGestureType.TWO_FINGER_SWIPE_DOWN -> copy(twoFingerSwipeDownAction = action)
    }
}
