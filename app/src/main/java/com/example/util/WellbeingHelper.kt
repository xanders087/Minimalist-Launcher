package com.example.util

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Process
import android.provider.Settings
import java.util.Calendar

data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val totalTimeInForegroundMs: Long
)

data class TodayUsageSummary(
    val totalScreenTimeMs: Long,
    val topApps: List<AppUsageInfo>,
    val hasPermission: Boolean
)

object WellbeingHelper {

    fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager ?: return false
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun openUsageAccessSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getTodayUsageSummary(context: Context): TodayUsageSummary {
        if (!hasUsageStatsPermission(context)) {
            return TodayUsageSummary(0L, emptyList(), hasPermission = false)
        }

        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            ?: return TodayUsageSummary(0L, emptyList(), hasPermission = true)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val usageStatsList: List<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        ) ?: emptyList()

        val packageManager = context.packageManager
        
        val aggregatedUsage = mutableMapOf<String, Long>()
        for (stat in usageStatsList) {
            if (stat.totalTimeInForeground > 0) {
                val current = aggregatedUsage.getOrDefault(stat.packageName, 0L)
                aggregatedUsage[stat.packageName] = Math.max(current, stat.totalTimeInForeground)
            }
        }

        var totalMs = 0L
        val appUsageList = mutableListOf<AppUsageInfo>()

        for ((pkgName, timeMs) in aggregatedUsage) {
            if (pkgName == context.packageName) continue

            totalMs += timeMs

            val appName = try {
                val appInfo = packageManager.getApplicationInfo(pkgName, 0)
                packageManager.getApplicationLabel(appInfo).toString()
            } catch (e: Exception) {
                pkgName
            }

            appUsageList.add(AppUsageInfo(pkgName, appName, timeMs))
        }

        val top3Apps = appUsageList.sortedByDescending { it.totalTimeInForegroundMs }.take(3)

        return TodayUsageSummary(
            totalScreenTimeMs = totalMs,
            topApps = top3Apps,
            hasPermission = true
        )
    }
}
