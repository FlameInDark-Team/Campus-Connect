package com.example.campusconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusconnect.model.AttendanceSubject
import com.example.campusconnect.model.AttendanceUiState
import com.example.campusconnect.repository.AttendanceRepository
import com.example.campusconnect.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.ceil

class AttendanceViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _attendanceUiState = MutableStateFlow<AttendanceUiState>(AttendanceUiState.Loading)
    val attendanceUiState: StateFlow<AttendanceUiState> = _attendanceUiState.asStateFlow()
    
    init {
        loadSubjects()
    }
    
    fun loadSubjects() {
        val userId = authRepository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            attendanceRepository.observeSubjects(userId).collect { subjects ->
                _attendanceUiState.value = when {
                    subjects.isEmpty() -> AttendanceUiState.Empty(
                        "No subjects added yet. Tap + to add your first subject"
                    )
                    else -> AttendanceUiState.Success(subjects)
                }
            }
        }
    }
    
    fun addSubject(subjectName: String) {
        if (subjectName.isBlank()) {
            _attendanceUiState.value = AttendanceUiState.Error("Subject name is required")
            return
        }
        
        val userId = authRepository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            val result = attendanceRepository.addSubject(userId, subjectName)
            if (result.isFailure) {
                _attendanceUiState.value = AttendanceUiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to add subject"
                )
            }
        }
    }
    
    fun updateAttendance(subjectId: String, totalClasses: Int, attendedClasses: Int) {
        if (totalClasses < 0 || attendedClasses < 0) {
            _attendanceUiState.value = AttendanceUiState.Error("Values cannot be negative")
            return
        }
        
        if (attendedClasses > totalClasses) {
            _attendanceUiState.value = AttendanceUiState.Error(
                "Attended classes cannot exceed total classes"
            )
            return
        }
        
        val percentage = calculatePercentage(attendedClasses, totalClasses)
        val userId = authRepository.getCurrentUser()?.uid ?: return
        
        viewModelScope.launch {
            attendanceRepository.updateSubject(
                userId, subjectId, totalClasses, attendedClasses, percentage
            )
        }
    }
    
    fun deleteSubject(subjectId: String) {
        val userId = authRepository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            attendanceRepository.deleteSubject(userId, subjectId)
        }
    }
    
    fun calculatePercentage(attended: Int, total: Int): Double {
        return if (total == 0) {
            0.00
        } else {
            (attended.toDouble() / total.toDouble()) * 100.0
        }
    }
    
    fun calculateClassesNeeded(attended: Int, total: Int): Int {
        val currentPercentage = calculatePercentage(attended, total)
        
        if (currentPercentage >= 75.0) {
            return 0
        }
        
        val needed = ((0.75 * total) - attended) / 0.25
        return ceil(needed).toInt()
    }
    
    fun getSubject(subjectId: String): StateFlow<AttendanceSubject?> {
        val flow = MutableStateFlow<AttendanceSubject?>(null)
        viewModelScope.launch {
            attendanceUiState.collect { state ->
                if (state is AttendanceUiState.Success) {
                    flow.value = state.subjects.find { it.id == subjectId }
                }
            }
        }
        return flow.asStateFlow()
    }
}
