package com.example.dermaai_android_140.myClasses

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Start Koin
        startKoin {
            androidContext(this@MyApplication)
            modules(appModule)
        }
    }
}
