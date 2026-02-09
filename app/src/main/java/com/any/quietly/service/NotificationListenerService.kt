package com.any.quietly.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.any.quietly.data.NotificationData
import com.any.quietly.domain.NotificationFilter
import com.any.quietly.domain.NotificationLogger
import com.any.quietly.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class NotificationListenerService : NotificationListenerService() {

    // Inject dependencies using Koin
    private val notificationRepository: NotificationRepository by inject()
    private val notificationFilter: NotificationFilter by inject()
    private val notificationLogger: NotificationLogger by inject()

    // Coroutine scope for background operations
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // Koin injection happens automatically when the service is created if DI is set up in Application
        Log.d("NotificationListener", "Service created and dependencies injected via Koin")
    }

    // Callback when a notification is received
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        Log.d("NotificationListener", "onNotificationPosted called with sbn: $sbn")
        if (sbn == null) return

        val notificationData = NotificationData.fromStatusBarNotification(sbn)
        Log.d("NotificationListener", "Notification posted: ${notificationData.title} from ${sbn.packageName}")

        if (notificationFilter.shouldBlock(notificationData)) {
            Log.d("NotificationListener", "Blocking notification from ${sbn.packageName}")
            // Uncomment to actively cancel the notification
            // cancelNotification(sbn.packageName, sbn.tag, sbn.id)
            return
        }

        serviceScope.launch {
            try {
                notificationLogger.logNotification(notificationData)
                Log.d("NotificationListener", "Notification logged: ${notificationData.title}")
            } catch (e: Exception) {
                Log.e("NotificationListener", "Error logging notification", e)
            }
        }
    }

    // Callback when a notification is removed
    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        Log.d("NotificationListener", "Notification removed: ${sbn?.notification?.extras?.getString("android.title")}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("NotificationListener", "Service destroyed")
    }
}
