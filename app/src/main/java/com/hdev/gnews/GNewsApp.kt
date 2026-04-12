package com.hdev.gnews

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GNewsApp : Application(){
    override fun onCreate() {
        super.onCreate()
    }

}