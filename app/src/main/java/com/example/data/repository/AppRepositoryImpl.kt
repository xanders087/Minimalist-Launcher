package com.example.data.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.os.UserHandle
import android.os.UserManager
import com.example.data.model.AppItem
import com.example.domain.launcher.LaunchAppUseCase
import com.example.domain.launcher.LaunchResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Implementación de producción de [AppRepository] que aprovecha [LauncherApps] y [UserManager]
 * para proveer soporte Multi-Usuario, Work Profile (Android Enterprise), Dual Apps y Carpetas Seguras,
 * además de sincronización reactiva en tiempo real mediante [LauncherApps.Callback].
 */
class AppRepositoryImpl(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AppRepository {

    private val applicationContext = context.applicationContext

    private val launcherApps: LauncherApps? =
        applicationContext.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps

    private val userManager: UserManager? =
        applicationContext.getSystemService(Context.USER_SERVICE) as? UserManager

    private val prefs: SharedPreferences =
        applicationContext.getSharedPreferences("app_launch_stats", Context.MODE_PRIVATE)

    private val repositoryScope = CoroutineScope(ioDispatcher + SupervisorJob())
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _appsStream = MutableStateFlow<List<AppItem>>(emptyList())
    override val appsStream: StateFlow<List<AppItem>> = _appsStream.asStateFlow()

    @Volatile
    private var isCallbackRegistered = false

    /**
     * Callback reactivo de LauncherApps. Detecta adición, eliminación y cambios
     * de paquetes por perfil de usuario en tiempo real.
     */
    private val launcherCallback = object : LauncherApps.Callback() {
        override fun onPackageAdded(packageName: String, user: UserHandle) {
            repositoryScope.launch {
                handlePackageAddedOrChanged(packageName, user)
            }
        }

        override fun onPackageRemoved(packageName: String, user: UserHandle) {
            repositoryScope.launch {
                handlePackageRemoved(packageName, user)
            }
        }

        override fun onPackageChanged(packageName: String, user: UserHandle) {
            repositoryScope.launch {
                handlePackageAddedOrChanged(packageName, user)
            }
        }

        override fun onPackagesAvailable(
            packageNames: Array<out String>,
            user: UserHandle,
            replacing: Boolean
        ) {
            repositoryScope.launch {
                for (pkg in packageNames) {
                    handlePackageAddedOrChanged(pkg, user)
                }
            }
        }

        override fun onPackagesUnavailable(
            packageNames: Array<out String>,
            user: UserHandle,
            replacing: Boolean
        ) {
            repositoryScope.launch {
                for (pkg in packageNames) {
                    handlePackageRemoved(pkg, user)
                }
            }
        }
    }

    override suspend fun loadApps(): Unit = withContext(ioDispatcher) {
        val loadedApps = queryAllProfilesApps()
        val finalApps = if (loadedApps.isNotEmpty()) {
            loadedApps
        } else {
            // Fallback resiliente para entornos de prueba / emuladores
            loadFallbackFromPackageManager()
        }
        _appsStream.value = finalApps.sortedBy { it.label.lowercase() }
    }

    /**
     * Consulta todas las actividades lanzables para cada perfil de usuario configurado
     * en el sistema operativo mediante [UserManager.getUserProfiles].
     */
    private fun queryAllProfilesApps(): List<AppItem> {
        val launcher = launcherApps ?: return emptyList()
        val myUser = Process.myUserHandle()
        val currentPackageName = applicationContext.packageName

        val profiles: List<UserHandle> = try {
            userManager?.userProfiles ?: listOf(myUser)
        } catch (e: Throwable) {
            listOf(myUser)
        }

        val appList = mutableListOf<AppItem>()

        for (profile in profiles) {
            val isWork = (profile != myUser)
            val activities: List<LauncherActivityInfo> = try {
                launcher.getActivityList(null, profile)
            } catch (e: Throwable) {
                emptyList()
            }

            for (activity in activities) {
                val pkgName = activity.applicationInfo.packageName
                if (pkgName == currentPackageName) continue

                val item = mapActivityInfoToAppItem(activity, profile, isWork)
                appList.add(item)
            }
        }

        return appList
    }

    /**
     * Fallback por PackageManager cuando LauncherApps no retorna actividades (ej. pruebas unitarias).
     */
    private fun loadFallbackFromPackageManager(): List<AppItem> {
        return try {
            val pm = applicationContext.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = pm.queryIntentActivities(intent, 0)
            val currentPkg = applicationContext.packageName
            val myUser = Process.myUserHandle()

            resolveInfos
                .filter { it.activityInfo.packageName != currentPkg }
                .map { resolveInfo ->
                    val pkg = resolveInfo.activityInfo.packageName
                    val label = resolveInfo.loadLabel(pm).toString()
                    val icon = try { resolveInfo.loadIcon(pm) } catch (e: Throwable) { null }
                    val component = ComponentName(pkg, resolveInfo.activityInfo.name)
                    val count = getLaunchCountForComponent(component, myUser, pkg)

                    AppItem(
                        packageName = pkg,
                        label = label,
                        userHandle = myUser,
                        componentName = component,
                        icon = icon,
                        isWorkProfile = false,
                        category = getCategoryFallback(label, pkg),
                        launchCount = count
                    )
                }
        } catch (e: Throwable) {
            emptyList()
        }
    }

    /**
     * Mapea un [LauncherActivityInfo] a nuestro modelo de dominio [AppItem]
     * inyectando insignias oficiales del perfil de trabajo y contadores de frecuencia.
     */
    private fun mapActivityInfoToAppItem(
        activity: LauncherActivityInfo,
        user: UserHandle,
        isWorkProfile: Boolean
    ): AppItem {
        val pkg = activity.applicationInfo.packageName
        val label = activity.label.toString()
        val component = activity.componentName
        // getBadgedIcon aplica automáticamente la insignia corporativa (maletín) si corresponde
        val icon = try {
            activity.getBadgedIcon(0)
        } catch (e: Throwable) {
            null
        }
        val count = getLaunchCountForComponent(component, user, pkg)

        return AppItem(
            packageName = pkg,
            label = label,
            userHandle = user,
            componentName = component,
            icon = icon,
            isWorkProfile = isWorkProfile,
            category = getCategoryFallback(label, pkg),
            launchCount = count
        )
    }

    /**
     * Actualización incremental cuando se agrega o actualiza un paquete:
     * Consulta ÚNICAMENTE el paquete y perfil afectados, actualizando el StateFlow
     * sin reescanear el resto del sistema.
     */
    private fun handlePackageAddedOrChanged(packageName: String, user: UserHandle) {
        val launcher = launcherApps ?: return
        val myUser = Process.myUserHandle()
        val isWork = (user != myUser)

        val activities: List<LauncherActivityInfo> = try {
            launcher.getActivityList(packageName, user)
        } catch (e: Throwable) {
            emptyList()
        }

        val updatedItems = activities.map { mapActivityInfoToAppItem(it, user, isWork) }

        _appsStream.update { currentList ->
            // Filtra los elementos previos de este paquete y usuario
            val remaining = currentList.filterNot { it.packageName == packageName && it.userHandle == user }
            (remaining + updatedItems).sortedBy { it.label.lowercase() }
        }
    }

    /**
     * Actualización incremental al desinstalar un paquete:
     * Elimina reactivamente solo las entradas correspondientes a ese paquete y perfil.
     */
    private fun handlePackageRemoved(packageName: String, user: UserHandle) {
        _appsStream.update { currentList ->
            currentList.filterNot { it.packageName == packageName && it.userHandle == user }
        }
    }

    @Synchronized
    override fun startObserving() {
        if (isCallbackRegistered) return
        try {
            launcherApps?.registerCallback(launcherCallback, mainHandler)
            isCallbackRegistered = true
        } catch (e: Throwable) {
            // Manejo de seguridad en entornos donde el servicio no esté activo
        }
    }

    @Synchronized
    override fun stopObserving() {
        if (!isCallbackRegistered) return
        try {
            launcherApps?.unregisterCallback(launcherCallback)
            isCallbackRegistered = false
        } catch (e: Throwable) {
            // Manejo defensivo
        }
    }

    private val launchAppUseCase = LaunchAppUseCase(applicationContext)

    override fun launchAppWithResult(appItem: AppItem, sourceBounds: Rect?, opts: Bundle?): LaunchResult {
        recordAppLaunch(appItem)
        return launchAppUseCase(appItem, sourceBounds, opts)
    }

    override fun launchApp(appItem: AppItem, sourceBounds: Rect?, opts: Bundle?): Boolean {
        return launchAppWithResult(appItem, sourceBounds, opts) is LaunchResult.Success
    }

    override fun recordAppLaunch(appItem: AppItem) {
        val key = buildLaunchStatsKey(appItem.componentName, appItem.userHandle)
        val currentCount = prefs.getInt(key, prefs.getInt("launch_${appItem.packageName}", 0))
        val newCount = currentCount + 1

        prefs.edit()
            .putInt(key, newCount)
            .putInt("launch_${appItem.packageName}", newCount)
            .apply()

        _appsStream.update { list ->
            list.map { item ->
                if (item.id == appItem.id) {
                    item.copy(launchCount = newCount)
                } else {
                    item
                }
            }
        }
    }

    override fun getAppLaunchCount(appItem: AppItem): Int {
        val key = buildLaunchStatsKey(appItem.componentName, appItem.userHandle)
        return prefs.getInt(key, prefs.getInt("launch_${appItem.packageName}", 0))
    }

    private fun getLaunchCountForComponent(component: ComponentName, user: UserHandle, pkg: String): Int {
        val key = buildLaunchStatsKey(component, user)
        return prefs.getInt(key, prefs.getInt("launch_$pkg", 0))
    }

    private fun buildLaunchStatsKey(component: ComponentName, user: UserHandle): String {
        return "launch_${component.flattenToString()}#${user.hashCode()}"
    }

    private fun getCategoryFallback(appName: String, packageName: String): String {
        val lower = "$appName $packageName".lowercase()
        val keywords = mapOf(
            "Communication" to listOf("dialer", "phone", "teléfono", "chat", "mail", "whatsapp", "telegram", "message", "mensaje", "slack", "discord", "meet", "zoom", "teams"),
            "Productivity" to listOf("chrome", "browser", "doc", "sheet", "note", "notion", "todo", "drive", "work", "calc", "calculadora", "calendar", "calendario", "office", "pdf"),
            "Entertainment" to listOf("youtube", "netflix", "spotify", "music", "música", "game", "juego", "photo", "foto", "twitch", "tiktok", "instagram", "prime", "disney", "video"),
            "Finance" to listOf("bank", "banco", "pay", "pago", "wallet", "crypto", "binance", "paypal", "revolut", "finance", "finanzas"),
            "Navigation" to listOf("map", "maps", "gps", "uber", "taxi", "transit", "waze", "lyft", "citymapper"),
            "Health & Fitness" to listOf("fit", "fitness", "health", "salud", "gym", "yoga", "medita", "strava", "run"),
            "System" to listOf("setting", "ajuste", "system", "launcher", "android", "package", "store", "play")
        )

        for ((cat, words) in keywords) {
            if (words.any { lower.contains(it) }) {
                return cat
            }
        }
        return "Utilities"
    }
}
