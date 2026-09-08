package com.example.data.model

import android.content.ComponentName
import android.graphics.drawable.Drawable
import android.os.Process
import android.os.UserHandle

/**
 * Representa una aplicación instalada en el dispositivo con soporte para
 * Multi-Usuario, Work Profile (Android Enterprise), Dual Apps y Carpetas Seguras.
 *
 * @property packageName Nombre del paquete de la aplicación (ej. "com.android.chrome").
 * @property label Etiqueta legible por el usuario (ej. "Chrome").
 * @property userHandle Identificador del usuario/perfil de Android propietario de la app.
 * @property componentName Componente de la actividad principal que se lanza.
 * @property id Identificador único compuesto por packageName y userHandle (evita colisiones multi-perfil).
 * @property icon Icono con la insignia (badge) de perfil aplicada automáticamente si es necesario.
 * @property isWorkProfile Indica si la app pertenece a un perfil administrado de trabajo o secundario.
 * @property category Categoría asignada (clasificada por IA Gemini o heurística).
 * @property launchCount Frecuencia de lanzamiento registrada en el launcher.
 */
data class AppItem(
    val packageName: String,
    val label: String,
    val userHandle: UserHandle = Process.myUserHandle(),
    val componentName: ComponentName = ComponentName(packageName, ""),
    val id: String = "${packageName}_${userHandle.hashCode()}",
    val icon: Drawable? = null,
    val isWorkProfile: Boolean = false,
    var category: String = "Utilities",
    val launchCount: Int = 0
)
