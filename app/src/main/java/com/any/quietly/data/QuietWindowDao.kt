package com.any.quietly.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface QuietWindowDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuietWindow(quietWindow: QuietWindowEntity): Long

    @Transaction
    @Query("SELECT * FROM quiet_windows WHERE id = :id")
    fun getQuietWindowWithNotifications(id: Int): Flow<QuietWindowWithNotifications?>

    @Transaction
    @Query("SELECT * FROM quiet_windows")
    fun getAllQuietWindowsWithNotifications(): Flow<List<QuietWindowWithNotifications>>

    @Query("SELECT * FROM quiet_windows")
    fun getAllQuietWindows(): Flow<List<QuietWindowEntity>>

    @Query("DELETE FROM quiet_windows WHERE id = :id")
    suspend fun deleteQuietWindow(id: Int)

    @Query("SELECT * FROM quiet_windows WHERE :timestamp BETWEEN startTime AND endTime ORDER BY startTime DESC LIMIT 1")
    suspend fun getActiveQuietWindow(timestamp: Long): QuietWindowEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuietWindowApps(apps: List<QuietWindowAppEntity>)

    @Transaction
    @Query("SELECT * FROM quiet_windows WHERE :timestamp BETWEEN startTime AND endTime ORDER BY startTime DESC LIMIT 1")
    suspend fun getActiveQuietWindowWithApps(timestamp: Long): QuietWindowWithApps?

    @Query("SELECT * FROM quiet_window_apps WHERE quiet_window_id = :quietWindowId")
    suspend fun getAppsForQuietWindow(quietWindowId: Int): List<QuietWindowAppEntity>
}
