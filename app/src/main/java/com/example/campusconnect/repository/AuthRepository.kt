package com.example.campusconnect.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository(private val firebaseAuth: FirebaseAuth) {
    
    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Login failed"))
        } catch (e: Exception) {
            Result.failure(Exception(mapFirebaseError(e)))
        }
    }
    
    suspend fun signup(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Signup failed"))
        } catch (e: Exception) {
            Result.failure(Exception(mapFirebaseError(e)))
        }
    }
    
    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapFirebaseError(e)))
        }
    }
    
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
    
    fun logout() {
        firebaseAuth.signOut()
    }
    
    private fun mapFirebaseError(exception: Exception): String {
        return when {
            exception.message?.contains("password is invalid") == true -> 
                "Invalid email or password"
            exception.message?.contains("no user record") == true -> 
                "No account found with this email"
            exception.message?.contains("email address is already") == true -> 
                "An account with this email already exists"
            exception.message?.contains("network error") == true -> 
                "Network error. Please check your connection"
            exception.message?.contains("badly formatted") == true -> 
                "Invalid email format"
            else -> "Authentication failed. Please try again"
        }
    }
}
