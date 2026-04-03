package com.example.campusconnect.model

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    data class Error(val message: String) : AuthState()
    data class Success(val message: String) : AuthState()
}
