package com.any.quietly.ui.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.any.quietly.ui.model.AppInfo

fun getInstalledApps(context: Context): List<AppInfo> {
    val pm = context.packageManager

    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }

    val activities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        pm.queryIntentActivities(
            intent,
            PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong())
        )
    } else {
        @Suppress("DEPRECATION")
        pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
    }

    return activities
        .asSequence()
        .mapNotNull { resolveInfo ->
            val activityInfo = resolveInfo.activityInfo ?: return@mapNotNull null
            val appInfo = activityInfo.applicationInfo
            AppInfo(
                packageName = appInfo.packageName,
                appName = pm.getApplicationLabel(appInfo).toString(),
                icon = pm.getApplicationIcon(appInfo)
            )
        }
        .distinctBy { it.packageName }
        .sortedBy { it.appName.lowercase() }
        .toList()
}
