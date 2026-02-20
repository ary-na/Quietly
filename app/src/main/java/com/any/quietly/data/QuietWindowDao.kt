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
    
    @Query("SELECT * FROM quiet_windows WHERE id = :id")
    fun getQuietWindow(id: Int): Flow<QuietWindowEntity?>

    @Query("DELETE FROM quiet_windows WHERE id = :id")
    suspend fun deleteQuietWindow(id: Int)

    @Query("UPDATE quiet_windows SET is_enabled = :enabled WHERE id = :id")
    suspend fun setQuietWindowEnabled(id: Int, enabled: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuietWindowApps(apps: List<QuietWindowAppEntity>)

    @Transaction
    @Query("SELECT * FROM quiet_windows")
    suspend fun getQuietWindowsWithApps(): List<QuietWindowWithApps>

    @Query("SELECT * FROM quiet_window_apps WHERE quiet_window_id = :quietWindowId")
    suspend fun getAppsForQuietWindow(quietWindowId: Int): List<QuietWindowAppEntity>
}
