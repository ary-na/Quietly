package com.any.quietly.domain

import android.util.Log
import com.any.quietly.data.NotificationData
import com.any.quietly.repository.NotificationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseNotificationLogger(
    private val repository: NotificationRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : NotificationLogger {

    override suspend fun logNotification(notificationData: NotificationData) {
        try {
            withContext(dispatcher) {
                repository.saveNotification(notificationData)
                // Log debug
                Log.d(
                    "DatabaseLogger",
                    "Notification logged successfully: ${notificationData.title}"
                )
            }
        } catch (e: Exception) {
            // Log error
            Log.e(
                "DatabaseLogger",
                "Failed to log notification: ${notificationData.title}",
                e
            )
            throw e
        }
    }

    override suspend fun getLoggedNotifications(): List<NotificationData> {
        return withContext(dispatcher) {
            repository.getAllNotificationsOnce()
        }
    }
}
