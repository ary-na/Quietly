package com.any.quietly.ui.util

import android.content.Context
import android.content.Intent
import com.any.quietly.ui.model.AppInfo

fun getInstalledApps(context: Context): List<AppInfo> {
    val pm = context.packageManager
    val quietlyPackageName = context.packageName

    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }

    return pm.queryIntentActivities(intent, 0)
        .map {
            val appInfo = it.activityInfo.applicationInfo
            AppInfo(
                packageName = appInfo.packageName,
                appName = pm.getApplicationLabel(appInfo).toString(),
                icon = pm.getApplicationIcon(appInfo)
            )
        }
        .filter { it.packageName != quietlyPackageName }
        .sortedBy { it.appName.lowercase() }
}

