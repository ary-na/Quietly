package com.any.quietly.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [NotificationEntity::class, QuietWindowEntity::class, QuietWindowAppEntity::class],
    version = 6,
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

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                var hasNotificationCount = false
                database.query("PRAGMA table_info(quiet_windows)").use { cursor ->
                    val nameIndex = cursor.getColumnIndex("name")
                    while (cursor.moveToNext()) {
                        if (nameIndex >= 0 && cursor.getString(nameIndex) == "notificationCount") {
                            hasNotificationCount = true
                            break
                        }
                    }
                }

                database.execSQL("PRAGMA foreign_keys=OFF")
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS quiet_windows_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        notificationCount INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                if (hasNotificationCount) {
                    database.execSQL(
                        """
                        INSERT INTO quiet_windows_new (id, name, notificationCount)
                        SELECT id, name, notificationCount
                        FROM quiet_windows
                        """.trimIndent()
                    )
                } else {
                    database.execSQL(
                        """
                        INSERT INTO quiet_windows_new (id, name, notificationCount)
                        SELECT id, name, 10
                        FROM quiet_windows
                        """.trimIndent()
                    )
                }

                database.execSQL("DROP TABLE quiet_windows")
                database.execSQL("ALTER TABLE quiet_windows_new RENAME TO quiet_windows")
                database.execSQL("PRAGMA foreign_keys=ON")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                var hasNotificationCount = false
                database.query("PRAGMA table_info(quiet_windows)").use { cursor ->
                    val nameIndex = cursor.getColumnIndex("name")
                    while (cursor.moveToNext()) {
                        if (nameIndex >= 0 && cursor.getString(nameIndex) == "notificationCount") {
                            hasNotificationCount = true
                            break
                        }
                    }
                }

                database.execSQL("PRAGMA foreign_keys=OFF")
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS quiet_windows_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        notificationCount INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                if (hasNotificationCount) {
                    database.execSQL(
                        """
                        INSERT INTO quiet_windows_new (id, name, notificationCount)
                        SELECT id, name, notificationCount
                        FROM quiet_windows
                        """.trimIndent()
                    )
                } else {
                    database.execSQL(
                        """
                        INSERT INTO quiet_windows_new (id, name, notificationCount)
                        SELECT id, name, 10
                        FROM quiet_windows
                        """.trimIndent()
                    )
                }

                database.execSQL("DROP TABLE quiet_windows")
                database.execSQL("ALTER TABLE quiet_windows_new RENAME TO quiet_windows")
                database.execSQL("PRAGMA foreign_keys=ON")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                var hasEnabledColumn = false
                database.query("PRAGMA table_info(quiet_windows)").use { cursor ->
                    val nameIndex = cursor.getColumnIndex("name")
                    while (cursor.moveToNext()) {
                        if (nameIndex >= 0 && cursor.getString(nameIndex) == "is_enabled") {
                            hasEnabledColumn = true
                            break
                        }
                    }
                }
                if (!hasEnabledColumn) {
                    database.execSQL(
                        "ALTER TABLE quiet_windows ADD COLUMN is_enabled INTEGER NOT NULL DEFAULT 1"
                    )
                }
            }
        }
    }
}
