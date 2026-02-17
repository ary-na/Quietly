package com.any.quietly.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.any.quietly.data.NotificationData
import com.any.quietly.domain.NotificationLogger
import com.any.quietly.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class NotificationListenerService : NotificationListenerService() {

    private val notificationLogger: NotificationLogger by inject()
    private val repository: NotificationRepository by inject()

    // Background scope for DB work
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        Log.d("NotificationListener", "Service created")
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
            val activeWindow = repository.getActiveQuietWindowWithApps(notificationData.postTime)
                ?: return@launch
            val selectedPackages = activeWindow.apps.map { it.packageName }.toSet()
            if (selectedPackages.isNotEmpty() && notificationData.packageName in selectedPackages) {
                if (sbn.isClearable) {
                    cancelNotification(sbn.key)
                    Log.d(
                        "NotificationListener",
                        "Notification cancelled: ${sbn.packageName}"
                    )
                } else {
                    Log.d(
                        "NotificationListener",
                        "Notification NOT clearable (system protected)"
                    )
                }
            }
        }
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
