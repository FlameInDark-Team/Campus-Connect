package com.example.campusconnect.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.lifecycle.ViewModel
import com.example.campusconnect.model.QuickAction
import com.example.campusconnect.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DashboardViewModel(private val authRepository: AuthRepository) : ViewModel() {
    
    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()
    
    val quickActions = listOf(
        QuickAction(
            id = "notes",
            title = "Notes",
            description = "Upload and view study notes",
            icon = Icons.Default.Description
        ),
        QuickAction(
            id = "attendance",
            title = "Attendance",
            description = "Track your class attendance",
            icon = Icons.Default.EventNote
        ),
        QuickAction(
            id = "announcements",
            title = "Announcements",
            description = "View campus announcements",
            icon = Icons.Default.Notifications
        ),
        QuickAction(
            id = "profile",
            title = "Profile",
            description = "Manage your profile",
            icon = Icons.Default.Person
        )
    )
    
    fun loadUserInfo() {
        val user = authRepository.getCurrentUser()
        _userEmail.value = user?.email ?: "Student"
    }
}
