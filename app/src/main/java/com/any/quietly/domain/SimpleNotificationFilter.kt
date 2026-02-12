package com.any.quietly.domain

import android.util.Log
import com.any.quietly.data.NotificationData

class SimpleNotificationFilter : NotificationFilter {

    // Define a set of package names to block
    private val blockedPackages = setOf(
        "com.android.systemui",
        "com.google.android.gms",
        "com.whatsapp"
    )

    // Define keywords to block
    private val blockedKeywords = setOf(
        "update available",
        "promotional offer"
    )

    override fun shouldBlock(notificationData: NotificationData): Boolean {

        // Block if the package name is in the blocked list
        if (notificationData.packageName in blockedPackages) {
            // Log debug
            Log.d(
                "NotificationFilter",
                "Blocking by package: ${notificationData.packageName}"
            )
            return true
        }

        // Normalize title and text (safe + case-insensitive)
        val title = notificationData.title?.lowercase() ?: ""
        val text = notificationData.text?.lowercase() ?: ""

        // Block if title or text contains blocked keywords
        for (keyword in blockedKeywords) {
            val normalizedKeyword = keyword.lowercase()
            if (title.contains(normalizedKeyword) || text.contains(normalizedKeyword)) {
                // Log debug
                Log.d(
                    "NotificationFilter",
                    "Blocking by keyword: $keyword"
                )
                return true
            }
        }

        return false
    }
}
