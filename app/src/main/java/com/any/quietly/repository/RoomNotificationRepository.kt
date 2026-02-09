package com.any.quietly.repository

import android.util.Log
import com.any.quietly.data.NotificationDao
import com.any.quietly.data.NotificationData
import com.any.quietly.data.NotificationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomNotificationRepository(
    private val notificationDao: NotificationDao
) : NotificationRepository {

    override suspend fun saveNotification(notificationData: NotificationData) {
        try {
            val entity = NotificationEntity(
                notificationId = notificationData.notificationId,
                packageName = notificationData.packageName,
                title = notificationData.title,
                text = notificationData.text,
                postTime = notificationData.postTime,
                tag = notificationData.tag
            )
            notificationDao.insert(entity)
            Log.d(
                "RoomNotificationRepo",
                "Notification saved: ${notificationData.title}"
            )
        } catch (e: Exception) {
            Log.e("RoomNotificationRepo", "Error saving notification", e)
            throw e
        }
    }

    override fun getAllNotifications(): Flow<List<NotificationData>> {
        return notificationDao.getAllNotifications().map { entities ->
            entities.map { entity ->
                NotificationData(
                    id = entity.id,
                    notificationId = entity.notificationId,
                    packageName = entity.packageName,
                    title = entity.title,
                    text = entity.text,
                    postTime = entity.postTime,
                    tag = entity.tag
                )
            }
        }
    }

    override suspend fun getAllNotificationsOnce(): List<NotificationData> {
        return notificationDao.getAllNotificationsOnce().map { entity ->
            NotificationData(
                id = entity.id,
                notificationId = entity.notificationId,
                packageName = entity.packageName,
                title = entity.title,
                text = entity.text,
                postTime = entity.postTime,
                tag = entity.tag
            )
        }
    }

    override suspend fun deleteNotification(id: Int) {
        notificationDao.deleteNotification(id)
    }
}
