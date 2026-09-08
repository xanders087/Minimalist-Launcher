package com.example.data.repository

import android.graphics.Rect
import android.os.Bundle
import com.example.data.model.AppItem
import kotlinx.coroutines.flow.StateFlow

/**
 * Contrato del repositorio para la gestión, sincronización reactiva y lanzamiento
 * de aplicaciones en el entorno de Launcher Android, con soporte completo Multi-Usuario.
 */
interface AppRepository {

    /**
     * Flujo reactivo (StateFlow) que emite la lista actual de aplicaciones instaladas.
     * Se actualiza de manera incremental cuando ocurren eventos de instalación, desinstalación o cambio.
     */
    val appsStream: StateFlow<List<AppItem>>

    /**
     * Carga inicial síncrona o asíncrona del catálogo de aplicaciones para todos
     * los perfiles del dispositivo (Personal, Trabajo, Seguro, etc.).
     */
    suspend fun loadApps()

    /**
     * Registra el callback reactivo [android.content.pm.LauncherApps.Callback]
     * con el sistema operativo para escuchar cambios en tiempo real.
     */
    fun startObserving()

    /**
     * Desregistra el callback de LauncherApps para prevenir fugas de memoria (memory leaks),
     * típicamente llamado en onCleared() del ViewModel o al destruir el servicio.
     */
    fun stopObserving()

    /**
     * Lanza la actividad principal de la aplicación especificada en el perfil de usuario correcto.
     *
     * @param appItem Aplicación a lanzar.
     * @param sourceBounds Límites visuales rectangulares para animaciones nativas del sistema.
     * @param opts Opciones adicionales de lanzamiento (ActivityOptions).
     * @return true si la app se lanzó exitosamente, false en caso contrario.
     */
    fun launchApp(appItem: AppItem, sourceBounds: Rect? = null, opts: Bundle? = null): Boolean

    /**
     * Lanza la aplicación y retorna un [LaunchResult] tipado con el estado exacto (éxito, no encontrada, permiso denegado, etc.).
     */
    fun launchAppWithResult(appItem: AppItem, sourceBounds: Rect? = null, opts: Bundle? = null): com.example.domain.launcher.LaunchResult

    /**
     * Incrementa y persiste el contador de lanzamientos para optimizar la eficiencia del Launcher.
     */
    fun recordAppLaunch(appItem: AppItem)

    /**
     * Obtiene el contador actual de lanzamientos de una app para un perfil específico.
     */
    fun getAppLaunchCount(appItem: AppItem): Int
}
