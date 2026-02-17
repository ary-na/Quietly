package com.any.quietly.repository

import com.any.quietly.data.NotificationData
import com.any.quietly.data.QuietWindow
import com.any.quietly.data.QuietWindowWithApps
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun saveNotification(notificationData: NotificationData)
    fun getAllNotifications(): Flow<List<NotificationData>>
    suspend fun getAllNotificationsOnce(): List<NotificationData>

    suspend fun deleteNotification(id: Int)

    suspend fun saveQuietWindow(quietWindow: QuietWindow): Long
    suspend fun saveQuietWindowApps(quietWindowId: Int, packageNames: List<String>)
    fun getAllQuietWindows(): Flow<List<QuietWindow>>
    fun getQuietWindow(id: Int): Flow<QuietWindow?>
    suspend fun getActiveQuietWindowWithApps(timestamp: Long): QuietWindowWithApps?
    suspend fun clearNotificationsForQuietWindow(quietWindowId: Int)
    suspend fun deleteQuietWindow(id: Int)
}
