package com.any.quietly.di

import androidx.room.Room
import com.any.quietly.data.AppDatabase
import com.any.quietly.domain.DatabaseNotificationLogger
import com.any.quietly.domain.GeminiNotificationSummarizer
import com.any.quietly.domain.NotificationLogger
import com.any.quietly.repository.NotificationRepository
import com.any.quietly.repository.RoomNotificationRepository
import com.any.quietly.ui.viewmodel.NotificationViewModel
import com.any.quietly.ui.viewmodel.QuietWindowViewModel
import com.any.quietly.util.NotificationHelper
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java, "quietly-db"
        )
            .addMigrations(
                AppDatabase.MIGRATION_1_2,
                AppDatabase.MIGRATION_2_3,
                AppDatabase.MIGRATION_3_4,
                AppDatabase.MIGRATION_4_5,
                AppDatabase.MIGRATION_5_6
            )
            .build()
    }
    single { get<AppDatabase>().notificationDao() }
    single { get<AppDatabase>().quietWindowDao() }
    single<NotificationRepository> { RoomNotificationRepository(get(), get()) }
    single { GeminiNotificationSummarizer() }
    single { NotificationHelper(androidContext()) }
    single<NotificationLogger> { DatabaseNotificationLogger(get()) }

    viewModel { NotificationViewModel(get()) }
    viewModel { QuietWindowViewModel(get()) }
}
