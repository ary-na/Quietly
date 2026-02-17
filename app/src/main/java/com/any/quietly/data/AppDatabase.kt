package com.any.quietly.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [NotificationEntity::class, QuietWindowEntity::class, QuietWindowAppEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun notificationDao(): NotificationDao
    abstract fun quietWindowDao(): QuietWindowDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS quiet_windows (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        startTime INTEGER NOT NULL,
                        endTime INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS quiet_window_apps (
                        quiet_window_id INTEGER NOT NULL,
                        package_name TEXT NOT NULL,
                        PRIMARY KEY(quiet_window_id, package_name),
                        FOREIGN KEY(quiet_window_id) REFERENCES quiet_windows(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_quiet_window_apps_quiet_window_id ON quiet_window_apps(quiet_window_id)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_quiet_window_apps_package_name ON quiet_window_apps(package_name)"
                )
            }
        }
    }
}
