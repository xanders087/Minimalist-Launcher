package com.example

import android.content.ComponentName
import android.graphics.drawable.Drawable
import android.os.Process
import android.os.UserHandle
import com.example.data.model.AppItem

/**
 * Modelo de presentación para la interfaz de usuario del Launcher,
 * compatible y convertible hacia el modelo de dominio [AppItem].
 */
data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable? = null,
    var category: String = "Utilities",
    val launchCount: Int = 0,
    val userHandle: UserHandle = Process.myUserHandle(),
    val componentName: ComponentName = ComponentName(packageName, ""),
    val isWorkProfile: Boolean = false
) {
    val id: String = "${packageName}_${userHandle.hashCode()}"

    fun toAppItem(): AppItem = AppItem(
        packageName = packageName,
        label = label,
        userHandle = userHandle,
        componentName = componentName,
        id = id,
        icon = icon,
        isWorkProfile = isWorkProfile,
        category = category,
        launchCount = launchCount
    )

    companion object {
        fun fromAppItem(item: AppItem): AppInfo = AppInfo(
            label = item.label,
            packageName = item.packageName,
            icon = item.icon,
            category = item.category,
            launchCount = item.launchCount,
            userHandle = item.userHandle,
            componentName = item.componentName,
            isWorkProfile = item.isWorkProfile
        )
    }
}
