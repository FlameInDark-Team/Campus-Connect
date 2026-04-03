package com.example.campusconnect.model

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)
