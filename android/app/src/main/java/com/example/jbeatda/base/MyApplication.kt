package com.example.jbeatda.base

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.jbeatda.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // 다크모드 비활성화
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object {
        const val BASE_URL = "https://jbeatda.up.railway.app/api/"
    }
}