package com.any.quietly.service

import android.service.notification.NotificationListenerService as AndroidNotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.any.quietly.data.NotificationData
import com.any.quietly.domain.NotificationLogger
import com.any.quietly.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class NotificationListenerService : AndroidNotificationListenerService() {

    private val notificationLogger: NotificationLogger by inject()
    private val repository: NotificationRepository by inject()

    // Background scope for DB work
    private val serviceScope = CoroutineScope(Dispatchers.IO)

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

        // ALWAYS log first (even blocked ones)
        serviceScope.launch {
            try {
                notificationLogger.logNotification(notificationData)
                Log.d("NotificationListener", "Notification logged to DB")
            } catch (e: Exception) {
                Log.e("NotificationListener", "Failed to log notification", e)
            }
        }

        // Decide whether to block based on active quiet window + selected apps
        serviceScope.launch {
            val captureTime = System.currentTimeMillis()
            val activeWindow = repository.getActiveQuietWindowWithApps(captureTime)
                ?: return@launch
            val selectedPackages = activeWindow.apps.map { it.packageName }.toSet()
            if (notificationData.packageName in selectedPackages) {
                cancelFromShade(sbn)
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
