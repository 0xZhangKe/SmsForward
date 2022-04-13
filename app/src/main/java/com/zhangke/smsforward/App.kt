package com.zhangke.smsforward

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}

lateinit var appContext: Application
    private set