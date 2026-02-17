package com.any.quietly.repository

import android.util.Log
import com.any.quietly.data.NotificationDao
import com.any.quietly.data.NotificationData
import com.any.quietly.data.NotificationEntity
import com.any.quietly.data.QuietWindow
import com.any.quietly.data.QuietWindowAppEntity
import com.any.quietly.data.QuietWindowDao
import com.any.quietly.data.QuietWindowEntity
import com.any.quietly.data.QuietWindowWithApps
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomNotificationRepository(
    private val notificationDao: NotificationDao,
    private val quietWindowDao: QuietWindowDao
) : NotificationRepository {

    override suspend fun saveNotification(notificationData: NotificationData) {
        try {
            val activeQuietWindowId = quietWindowDao
                .getActiveQuietWindow(notificationData.postTime)
                ?.id
            val entity = NotificationEntity(
                notificationId = notificationData.notificationId,
                packageName = notificationData.packageName,
                title = notificationData.title,
                text = notificationData.text,
                postTime = notificationData.postTime,
                tag = notificationData.tag,
                quietWindowId = activeQuietWindowId
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

    override suspend fun saveQuietWindow(quietWindow: QuietWindow): Long {
        val entity = QuietWindowEntity(
            name = quietWindow.name,
            startTime = quietWindow.startTime,
            endTime = quietWindow.endTime
        )
        return quietWindowDao.insertQuietWindow(entity)
    }

    override suspend fun saveQuietWindowApps(quietWindowId: Int, packageNames: List<String>) {
        if (packageNames.isEmpty()) return
        val entities = packageNames.map { packageName ->
            QuietWindowAppEntity(
                quietWindowId = quietWindowId,
                packageName = packageName
            )
        }
        quietWindowDao.insertQuietWindowApps(entities)
    }

    override fun getAllQuietWindows(): Flow<List<QuietWindow>> {
        return quietWindowDao.getAllQuietWindowsWithNotifications().map { entities ->
            entities.map {
                QuietWindow(
                    id = it.quietWindow.id,
                    name = it.quietWindow.name,
                    startTime = it.quietWindow.startTime,
                    endTime = it.quietWindow.endTime,
                    notifications = it.notifications.map { notificationEntity ->
                        NotificationData(
                            id = notificationEntity.id,
                            notificationId = notificationEntity.notificationId,
                            packageName = notificationEntity.packageName,
                            title = notificationEntity.title,
                            text = notificationEntity.text,
                            postTime = notificationEntity.postTime,
                            tag = notificationEntity.tag
                        )
                    }
                )
            }
        }
    }

    override fun getQuietWindow(id: Int): Flow<QuietWindow?> {
        return quietWindowDao.getQuietWindowWithNotifications(id).map {
            it?.let {
                QuietWindow(
                    id = it.quietWindow.id,
                    name = it.quietWindow.name,
                    startTime = it.quietWindow.startTime,
                    endTime = it.quietWindow.endTime,
                    notifications = it.notifications.map { notificationEntity ->
                        NotificationData(
                            id = notificationEntity.id,
                            notificationId = notificationEntity.notificationId,
                            packageName = notificationEntity.packageName,
                            title = notificationEntity.title,
                            text = notificationEntity.text,
                            postTime = notificationEntity.postTime,
                            tag = notificationEntity.tag
                        )
                    }
                )
            }
        }
    }

    override suspend fun getActiveQuietWindowWithApps(timestamp: Long): QuietWindowWithApps? {
        return quietWindowDao.getActiveQuietWindowWithApps(timestamp)
    }

    override suspend fun clearNotificationsForQuietWindow(quietWindowId: Int) {
        notificationDao.deleteNotificationsForQuietWindow(quietWindowId)
    }

    override suspend fun deleteQuietWindow(id: Int) {
        quietWindowDao.deleteQuietWindow(id)
    }
}
