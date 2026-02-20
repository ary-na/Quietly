package com.any.quietly.service

import android.service.notification.NotificationListenerService as AndroidNotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.any.quietly.data.NotificationData
import com.any.quietly.repository.NotificationRepository
import com.any.quietly.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

class NotificationListenerService : AndroidNotificationListenerService() {

    private val repository: NotificationRepository by inject()
    private val notificationHelper: NotificationHelper by inject()

    // Background scope for DB work
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private val summaryInProgressWindowIds = ConcurrentHashMap.newKeySet<Int>()

    override fun onCreate() {
        super.onCreate()
        Log.d("NotificationListener", "Service created")
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d("NotificationListener", "Listener connected")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d("NotificationListener", "Listener disconnected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        if (sbn == null) return

        val notificationData = NotificationData.fromStatusBarNotification(sbn)

        Log.d(
            "NotificationListener",
            "Received notification from ${sbn.packageName}, clearable=${sbn.isClearable}"
        )

        serviceScope.launch {
            val quietWindows = repository.getQuietWindowsWithApps()

            for (quietWindow in quietWindows) {
                if (!quietWindow.quietWindow.isEnabled) continue
                val selectedPackages = quietWindow.apps.map { it.packageName }.toSet()
                if (notificationData.packageName in selectedPackages) {
                    repository.saveNotification(notificationData, quietWindow.quietWindow.id)
                    cancelFromShade(sbn)

                    val notificationCount = repository.getNotificationCountForQuietWindow(quietWindow.quietWindow.id)
                    if (notificationCount >= quietWindow.quietWindow.notificationCount) {
                        val windowId = quietWindow.quietWindow.id
                        if (!summaryInProgressWindowIds.add(windowId)) continue

                        // Disable immediately to avoid duplicate summaries from concurrent posts.
                        repository.setQuietWindowEnabled(windowId, false)
                        val notificationsToSummarize = repository.getNotificationsForQuietWindow(quietWindow.quietWindow.id)
                        try {
                            val summary = buildHumanReadableSummary(notificationsToSummarize)
                            notificationHelper.showSummaryNotification(quietWindow.quietWindow.name, summary)
                            repository.clearNotificationsForQuietWindow(windowId)
                            Log.d(
                                "NotificationListener",
                                "Quiet window paused after threshold: id=$windowId"
                            )
                        } finally {
                            summaryInProgressWindowIds.remove(windowId)
                        }
                    }
                }
            }
        }
    }

    private fun cancelFromShade(sbn: StatusBarNotification) {
        val key = sbn.key

        val byBatchKey = runCatching {
            cancelNotifications(arrayOf(key))
        }.isSuccess

        val byKey = runCatching {
            cancelNotification(key)
        }.isSuccess

        @Suppress("DEPRECATION")
        val byLegacyTriplet = runCatching {
            cancelNotification(sbn.packageName, sbn.tag, sbn.id)
        }.isSuccess

        // Some OEM builds are picky with the posted instance; also cancel matching active entries.
        val activeMatches = activeNotifications
            ?.filter { active ->
                active.packageName == sbn.packageName &&
                        (active.key == key || (active.id == sbn.id && active.tag == sbn.tag))
            }
            .orEmpty()

        var activeCancelledCount = 0
        activeMatches.forEach { active ->
            if (runCatching { cancelNotification(active.key) }.isSuccess) {
                activeCancelledCount++
            }
        }

        Log.d(
            "NotificationListener",
            "Cancel attempt pkg=${sbn.packageName} id=${sbn.id} key=$key " +
                    "batch=$byBatchKey byKey=$byKey legacy=$byLegacyTriplet " +
                    "activeCancelled=$activeCancelledCount postedClearable=${sbn.isClearable}"
        )
    }

    private fun buildHumanReadableSummary(notifications: List<NotificationData>): String {
        if (notifications.isEmpty()) return "You had no notifications."

        val groupedByApp = notifications.groupBy { it.packageName }
        val appSentences = groupedByApp.entries.map { (pkg, list) ->
            val appName = prettifyAppName(pkg)
            val categories = list.groupingBy { detectCategory(it) }.eachCount()
            val dominantCategory = categories.maxByOrNull { it.value }?.key ?: "notifications"
            val count = list.size
            "$count from $appName ($dominantCategory)"
        }
        val lead = "You received ${notifications.size} notifications: ${appSentences.joinToString(", ")}."

        val stats = groupedByApp.entries.joinToString("; ") { (pkg, list) ->
            val appName = prettifyAppName(pkg)
            val categories = list.groupingBy { detectCategory(it) }.eachCount()
            val categoryStats = categories.entries.joinToString(", ") { "${it.value} ${it.key}" }
            "$appName - $categoryStats"
        }
        return "$lead Stats: $stats."
    }

    private fun detectCategory(notification: NotificationData): String {
        val text = "${notification.title.orEmpty()} ${notification.text.orEmpty()}".lowercase(Locale.getDefault())
        return when {
            "friend request" in text -> "friend request"
            "connection request" in text -> "connection request"
            "update" in text -> "update"
            "message" in text -> "message"
            "comment" in text -> "comment"
            "like" in text -> "like"
            else -> "notification"
        }
    }

    private fun prettifyAppName(packageName: String): String {
        val raw = packageName.substringAfterLast('.').replace('_', ' ').replace('-', ' ')
        return raw.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)

        Log.d(
            "NotificationListener",
            "Notification removed: ${sbn?.packageName}"
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("NotificationListener", "Service destroyed")
    }
}
