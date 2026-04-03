package com.example.campusconnect.data

import android.content.Context
import com.example.campusconnect.database.AppDatabase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

object FirebaseManager {
    private lateinit var database: AppDatabase
    
    fun initialize(app: android.app.Application) {
        FirebaseApp.initializeApp(app)
        database = AppDatabase.getInstance(app)
    }
    
    val auth: FirebaseAuth
        get() = FirebaseAuth.getInstance()
    
    fun getDatabase(context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }
}
