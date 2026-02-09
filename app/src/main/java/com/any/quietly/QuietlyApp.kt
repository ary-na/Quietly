package com.any.quietly

import android.app.Application
import com.any.quietly.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class QuietlyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@QuietlyApp)
            modules(appModule)
        }
    }
}
