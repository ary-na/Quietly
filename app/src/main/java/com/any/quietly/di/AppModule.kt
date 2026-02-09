package com.any.quietly.di

import android.content.Context
import androidx.room.Room
import com.any.quietly.data.AppDatabase
import com.any.quietly.data.NotificationDao
import com.any.quietly.domain.DatabaseNotificationLogger
import com.any.quietly.domain.NotificationFilter
import com.any.quietly.domain.NotificationLogger
import com.any.quietly.domain.SimpleNotificationFilter
import com.any.quietly.repository.NotificationRepository
import com.any.quietly.repository.RoomNotificationRepository
import com.any.quietly.ui.viewmodel.NotificationViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

// Define a Koin module
val appModule: Module = module {

    // Database
    single { provideDatabase(androidContext()) }
    single { provideDao(get()) }

    // Repository
    single<NotificationRepository> { RoomNotificationRepository(get()) }

    // Domain Logic
    single<NotificationFilter> { SimpleNotificationFilter() }
    single<NotificationLogger> { DatabaseNotificationLogger(get()) }

    // ViewModel
    viewModel<NotificationViewModel> { NotificationViewModel(get()) }
}

// Function to provide the Room database instance
fun provideDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "app_database"
    )
        .fallbackToDestructiveMigration()
        .build()
}

// Function to provide the DAO instance
fun provideDao(database: AppDatabase): NotificationDao {
    return database.notificationDao()
}
