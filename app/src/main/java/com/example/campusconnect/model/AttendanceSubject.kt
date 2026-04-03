package com.example.campusconnect.model

import java.text.SimpleDateFormat
import java.util.*

data class AttendanceSubject(
    val id: String = "",
    val subjectName: String = "",
    val totalClasses: Int = 0,
    val attendedClasses: Int = 0,
    val percentage: Double = 0.00,
    val lastUpdated: Long = 0L,
    val userId: String = ""
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "subjectName" to subjectName,
        "totalClasses" to totalClasses,
        "attendedClasses" to attendedClasses,
        "percentage" to percentage,
        "lastUpdated" to lastUpdated,
        "userId" to userId
    )
    
    companion object {
        fun fromMap(map: Map<String, Any>): AttendanceSubject = AttendanceSubject(
            id = map["id"] as? String ?: "",
            subjectName = map["subjectName"] as? String ?: "",
            totalClasses = (map["totalClasses"] as? Long)?.toInt() ?: 0,
            attendedClasses = (map["attendedClasses"] as? Long)?.toInt() ?: 0,
            percentage = map["percentage"] as? Double ?: 0.00,
            lastUpdated = map["lastUpdated"] as? Long ?: 0L,
            userId = map["userId"] as? String ?: ""
        )
    }
    
    fun getFormattedPercentage(): String {
        return String.format("%.2f", percentage)
    }
    
    fun isLowAttendance(): Boolean {
        return percentage < 75.0
    }
    
    fun getFormattedLastUpdated(): String {
        val date = Date(lastUpdated)
        val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return format.format(date)
    }
}

sealed class AttendanceUiState {
    object Loading : AttendanceUiState()
    data class Success(val subjects: List<AttendanceSubject>) : AttendanceUiState()
    data class Empty(val message: String) : AttendanceUiState()
    data class Error(val message: String) : AttendanceUiState()
}
