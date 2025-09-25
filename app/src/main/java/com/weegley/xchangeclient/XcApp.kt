package com.weegley.xchangeclient

import android.app.Application

class XcApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppGraph.init(this)
    }
}
