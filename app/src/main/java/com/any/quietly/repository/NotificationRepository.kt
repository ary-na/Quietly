package com.any.quietly.repository

import com.any.quietly.data.NotificationData
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun saveNotification(notificationData: NotificationData)
    fun getAllNotifications(): Flow<List<NotificationData>>
    suspend fun getAllNotificationsOnce(): List<NotificationData>

    suspend fun deleteNotification(id: Int)
}
