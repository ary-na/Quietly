package com.any.quietly.domain

import com.any.quietly.data.NotificationData

interface NotificationLogger {
    suspend fun logNotification(notificationData: NotificationData)
    suspend fun getLoggedNotifications(): List<NotificationData>
}
