package com.any.quietly.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notification: NotificationEntity): Long

    @Query("SELECT * FROM notifications ORDER BY postTime DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>> // Using Flow for real-time updates

    @Query("SELECT * FROM notifications ORDER BY postTime DESC")
    suspend fun getAllNotificationsOnce(): List<NotificationEntity> // For one-time retrieval
    
    @Query("SELECT * FROM notifications WHERE quiet_window_id = :quietWindowId ORDER BY postTime DESC")
    suspend fun getNotificationsForQuietWindow(quietWindowId: Int): List<NotificationEntity>

    @Query("SELECT COUNT(*) FROM notifications WHERE quiet_window_id = :quietWindowId")
    suspend fun getNotificationCountForQuietWindow(quietWindowId: Int): Int

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: Int) // For deletion

    @Query("DELETE FROM notifications WHERE quiet_window_id = :quietWindowId")
    suspend fun deleteNotificationsForQuietWindow(quietWindowId: Int)
}
