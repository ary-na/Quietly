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

    override suspend fun saveNotification(notificationData: NotificationData, quietWindowId: Int?) {
        try {
            val entity = NotificationEntity(
                notificationId = notificationData.notificationId,
                packageName = notificationData.packageName,
                title = notificationData.title,
                text = notificationData.text,
                postTime = notificationData.postTime,
                tag = notificationData.tag,
                quietWindowId = quietWindowId
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
    
    override suspend fun getNotificationsForQuietWindow(quietWindowId: Int): List<NotificationData> {
        return notificationDao.getNotificationsForQuietWindow(quietWindowId).map { entity ->
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
            notificationCount = quietWindow.notificationCount,
            isEnabled = quietWindow.isEnabled
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
        return quietWindowDao.getAllQuietWindows().map { entities ->
            entities.map {
                QuietWindow(
                    id = it.id,
                    name = it.name,
                    notificationCount = it.notificationCount,
                    isEnabled = it.isEnabled
                )
            }
        }
    }

    override fun getQuietWindow(id: Int): Flow<QuietWindow?> {
        return quietWindowDao.getQuietWindow(id).map {
            it?.let {
                QuietWindow(
                    id = it.id,
                    name = it.name,
                    notificationCount = it.notificationCount,
                    isEnabled = it.isEnabled
                )
            }
        }
    }

    override suspend fun getQuietWindowsWithApps(): List<QuietWindowWithApps> {
        return quietWindowDao.getQuietWindowsWithApps()
    }
    
    override suspend fun getNotificationCountForQuietWindow(quietWindowId: Int): Int {
        return notificationDao.getNotificationCountForQuietWindow(quietWindowId)
    }

    override suspend fun clearNotificationsForQuietWindow(quietWindowId: Int) {
        notificationDao.deleteNotificationsForQuietWindow(quietWindowId)
    }

    override suspend fun setQuietWindowEnabled(id: Int, enabled: Boolean) {
        quietWindowDao.setQuietWindowEnabled(id, enabled)
    }

    override suspend fun deleteQuietWindow(id: Int) {
        quietWindowDao.deleteQuietWindow(id)
    }
}
