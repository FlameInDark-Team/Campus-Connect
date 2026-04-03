package com.example.campusconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusconnect.model.AuthState
import com.example.campusconnect.model.ValidationResult
import com.example.campusconnect.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    fun login(email: String, password: String) {
        val validation = validateEmptyFields(email, password)
        if (!validation.isValid) {
            _authState.value = AuthState.Error(validation.errorMessage ?: "Validation failed")
            return
        }
        
        val emailValidation = validateEmail(email)
        if (!emailValidation.isValid) {
            _authState.value = AuthState.Error(emailValidation.errorMessage ?: "Invalid email")
            return
        }
        
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            _authState.value = if (result.isSuccess) {
                AuthState.Authenticated
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }
    
    fun signup(email: String, password: String) {
        val validation = validateEmptyFields(email, password)
        if (!validation.isValid) {
            _authState.value = AuthState.Error(validation.errorMessage ?: "Validation failed")
            return
        }
        
        val emailValidation = validateEmail(email)
        if (!emailValidation.isValid) {
            _authState.value = AuthState.Error(emailValidation.errorMessage ?: "Invalid email")
            return
        }
        
        val passwordValidation = validatePassword(password)
        if (!passwordValidation.isValid) {
            _authState.value = AuthState.Error(passwordValidation.errorMessage ?: "Invalid password")
            return
        }
        
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authRepository.signup(email, password)
            _authState.value = if (result.isSuccess) {
                AuthState.Authenticated
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Signup failed")
            }
        }
    }
    
    fun sendPasswordReset(email: String) {
        val emailValidation = validateEmail(email)
        if (!emailValidation.isValid) {
            _authState.value = AuthState.Error(emailValidation.errorMessage ?: "Invalid email")
            return
        }
        
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authRepository.sendPasswordReset(email)
            _authState.value = if (result.isSuccess) {
                AuthState.Success("Password reset email sent")
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Failed to send reset email")
            }
        }
    }
    
    fun checkAuthState() {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000) // Minimum splash duration
            val user = authRepository.getCurrentUser()
            _authState.value = if (user != null) {
                AuthState.Authenticated
            } else {
                AuthState.Idle
            }
        }
    }
    
    fun logout() {
        authRepository.logout()
        _authState.value = AuthState.Idle
    }
    
    fun validateEmail(email: String): ValidationResult {
        return if (email.contains("@") && email.contains(".")) {
            ValidationResult(true)
        } else {
            ValidationResult(false, "Please enter a valid email")
        }
    }
    
    fun validatePassword(password: String): ValidationResult {
        return if (password.length >= 6) {
            ValidationResult(true)
        } else {
            ValidationResult(false, "Password must be at least 6 characters")
        }
    }
    
    fun validateEmptyFields(email: String, password: String): ValidationResult {
        return if (email.isBlank() || password.isBlank()) {
            ValidationResult(false, "Please fill in all fields")
        } else {
            ValidationResult(true)
        }
    }
    
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
