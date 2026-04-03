package com.example.campusconnect

import android.app.Application
import com.example.campusconnect.data.FirebaseManager

class CampusConnectApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseManager.initialize(this)
    }
}
