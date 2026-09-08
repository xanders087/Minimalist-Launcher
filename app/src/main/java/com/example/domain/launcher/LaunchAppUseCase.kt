package com.example.domain.launcher

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.os.UserHandle
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import com.example.data.model.AppItem

/**
 * Caso de uso / Motor de lanzamiento seguro (Intent Dispatcher) con arquitectura de fallbacks
 * por niveles, inyección obligatoria de flags de sistema y blindaje granular contra excepciones.
 */
class LaunchAppUseCase(
    private val context: Context
) {
    private val applicationContext = context.applicationContext

    private val packageManager: PackageManager = applicationContext.packageManager

    private val launcherApps: LauncherApps? =
        applicationContext.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps

    /**
     * Sobrecarga de conveniencia para lanzar directamente un [AppItem].
     */
    operator fun invoke(
        appItem: AppItem,
        sourceBounds: Rect? = null,
        opts: Bundle? = null
    ): LaunchResult {
        return executeLaunch(
            packageName = appItem.packageName,
            componentName = appItem.componentName,
            userHandle = appItem.userHandle,
            sourceBounds = sourceBounds,
            opts = opts
        )
    }

    /**
     * Despacha el intento de apertura de la aplicación ejecutando la estrategia
     * de resolución por 3 niveles de fallback.
     */
    operator fun invoke(
        packageName: String,
        userHandle: UserHandle = Process.myUserHandle(),
        componentName: ComponentName? = null,
        sourceBounds: Rect? = null,
        opts: Bundle? = null
    ): LaunchResult {
        return executeLaunch(
            packageName = packageName,
            componentName = componentName,
            userHandle = userHandle,
            sourceBounds = sourceBounds,
            opts = opts
        )
    }

    private fun executeLaunch(
        packageName: String,
        componentName: ComponentName?,
        userHandle: UserHandle,
        sourceBounds: Rect?,
        opts: Bundle?
    ): LaunchResult {
        // Validación básica
        if (packageName.isBlank()) {
            return LaunchResult.AppNotFound(packageName, "Nombre de paquete inválido o vacío")
        }

        // =========================================================================
        // NIVEL 1: Lanzamiento estándar vía LauncherApps (Multi-Perfil) o PackageManager
        // =========================================================================
        val level1Result = tryLevel1StandardLaunch(packageName, componentName, userHandle, sourceBounds, opts)
        if (level1Result is LaunchResult.Success) {
            return level1Result
        }
        // Si falló por violación de permisos/seguridad explícita, no intentamos fallbacks peligrosos
        if (level1Result is LaunchResult.PermissionDenied) {
            return level1Result
        }

        // =========================================================================
        // NIVEL 2: Intención explícita MAIN/LAUNCHER resolviendo ActivityInfo
        // =========================================================================
        val level2Result = tryLevel2ExplicitLauncher(packageName, sourceBounds, opts)
        if (level2Result is LaunchResult.Success) {
            return level2Result
        }
        if (level2Result is LaunchResult.PermissionDenied) {
            return level2Result
        }

        // =========================================================================
        // NIVEL 3: Fallback a acciones esenciales del sistema (Dialer, Cámara, etc.)
        // =========================================================================
        val level3Result = tryLevel3SystemActionsFallback(packageName, sourceBounds, opts)
        if (level3Result is LaunchResult.Success) {
            return level3Result
        }

        // Si todos los niveles fallaron, consolidamos el error más preciso
        return level1Result
    }

    /**
     * Nivel 1: Utiliza [LauncherApps.startMainActivity] si se conoce el componente y perfil,
     * o [PackageManager.getLaunchIntentForPackage] con inyección de flags críticos.
     */
    private fun tryLevel1StandardLaunch(
        packageName: String,
        componentName: ComponentName?,
        userHandle: UserHandle,
        sourceBounds: Rect?,
        opts: Bundle?
    ): LaunchResult {
        try {
            // Si tenemos un componente válido y LauncherApps disponible, respetamos el UserHandle
            if (componentName != null && componentName.className.isNotBlank() && launcherApps != null) {
                launcherApps.startMainActivity(componentName, userHandle, sourceBounds, opts)
                return LaunchResult.Success
            }

            // Fallback estándar por PackageManager
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                applyCriticalLauncherFlags(launchIntent, sourceBounds)
                applicationContext.startActivity(launchIntent, opts)
                return LaunchResult.Success
            }

            return LaunchResult.AppNotFound(packageName, "No se encontró launch intent en PackageManager")
        } catch (e: ActivityNotFoundException) {
            return LaunchResult.AppNotFound(packageName, e.message)
        } catch (e: SecurityException) {
            return LaunchResult.PermissionDenied(packageName, e)
        } catch (e: NullPointerException) {
            return LaunchResult.GenericError(e)
        } catch (e: Throwable) {
            return LaunchResult.GenericError(e)
        }
    }

    /**
     * Nivel 2: Construcción manual de un intent MAIN/LAUNCHER filtrado por paquete,
     * resolviendo dinámicamente la ActivityInfo registrada en el sistema.
     */
    private fun tryLevel2ExplicitLauncher(
        packageName: String,
        sourceBounds: Rect?,
        opts: Bundle?
    ): LaunchResult {
        try {
            val queryIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                setPackage(packageName)
            }

            val resolvedActivities = packageManager.queryIntentActivities(queryIntent, 0)
            if (resolvedActivities.isNotEmpty()) {
                val resolvedInfo = resolvedActivities.first().activityInfo
                val explicitIntent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    component = ComponentName(packageName, resolvedInfo.name)
                    applyCriticalLauncherFlags(this, sourceBounds)
                }

                applicationContext.startActivity(explicitIntent, opts)
                return LaunchResult.Success
            }

            return LaunchResult.AppNotFound(packageName, "No se encontró actividad MAIN/LAUNCHER explícita")
        } catch (e: ActivityNotFoundException) {
            return LaunchResult.AppNotFound(packageName, e.message)
        } catch (e: SecurityException) {
            return LaunchResult.PermissionDenied(packageName, e)
        } catch (e: NullPointerException) {
            return LaunchResult.GenericError(e)
        } catch (e: Throwable) {
            return LaunchResult.GenericError(e)
        }
    }

    /**
     * Nivel 3: Fallback a acciones de sistema esenciales (Intent.ACTION_DIAL,
     * ACTION_IMAGE_CAPTURE, ACTION_SETTINGS, CATEGORY_APP_BROWSER, etc.)
     * si la aplicación específica fue desinstalada o su componente principal fue renombrado.
     */
    private fun tryLevel3SystemActionsFallback(
        packageName: String,
        sourceBounds: Rect?,
        opts: Bundle?
    ): LaunchResult {
        val fallbackIntent = resolveSystemActionForPackage(packageName) ?: return LaunchResult.AppNotFound(packageName)

        applyCriticalLauncherFlags(fallbackIntent, sourceBounds)

        return try {
            if (fallbackIntent.resolveActivity(packageManager) != null) {
                applicationContext.startActivity(fallbackIntent, opts)
                LaunchResult.Success
            } else {
                LaunchResult.AppNotFound(packageName, "No hay manejador de sistema para la acción alternativa")
            }
        } catch (e: ActivityNotFoundException) {
            LaunchResult.AppNotFound(packageName, e.message)
        } catch (e: SecurityException) {
            LaunchResult.PermissionDenied(packageName, e)
        } catch (e: Throwable) {
            LaunchResult.GenericError(e)
        }
    }

    /**
     * Inyección OBLIGATORIA de flags críticos de sistema para lanzadores:
     * - FLAG_ACTIVITY_NEW_TASK: Necesario al iniciar actividades desde un contexto de Launcher.
     * - FLAG_ACTIVITY_RESET_TASK_IF_NEEDED: Garantiza que la tarea existente se reinicie al estado raíz.
     */
    private fun applyCriticalLauncherFlags(intent: Intent, sourceBounds: Rect?) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        if (sourceBounds != null) {
            intent.sourceBounds = sourceBounds
        }
    }

    /**
     * Deduce la acción de sistema idónea en base al paquete o firma de la aplicación.
     */
    private fun resolveSystemActionForPackage(packageName: String): Intent? {
        val lower = packageName.lowercase()
        return when {
            // Teléfono / Dialer
            lower.contains("dialer") || lower.contains("phone") || lower.contains("telecom") -> {
                Intent(Intent.ACTION_DIAL)
            }
            // Navegador web
            lower.contains("chrome") || lower.contains("browser") || lower.contains("firefox") || lower.contains("opera") -> {
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
            }
            // Configuración / Ajustes
            lower.contains("settings") -> {
                Intent(Settings.ACTION_SETTINGS)
            }
            // Cámara
            lower.contains("camera") -> {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            }
            // Reloj / Alarma
            lower.contains("deskclock") || lower.contains("clock") -> {
                Intent(AlarmClock.ACTION_SHOW_ALARMS)
            }
            // Calendario
            lower.contains("calendar") -> {
                Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_APP_CALENDAR) }
            }
            // Mensajería SMS / Chat
            lower.contains("messaging") || lower.contains("mms") || lower.contains("sms") -> {
                Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_APP_MESSAGING) }
            }
            // Galería / Fotos
            lower.contains("photos") || lower.contains("gallery") -> {
                Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_APP_GALLERY) }
            }
            else -> null
        }
    }
}
