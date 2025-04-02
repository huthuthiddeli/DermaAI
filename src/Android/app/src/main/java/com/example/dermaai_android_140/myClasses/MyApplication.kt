package com.example.dermaai_android_140.myClasses

import android.app.Application
import com.google.firebase.FirebaseApp
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

        // Start Firebase
        FirebaseApp.initializeApp(this)
    }
}
