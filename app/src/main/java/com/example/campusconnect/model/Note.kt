package com.example.campusconnect.model

import java.text.SimpleDateFormat
import java.util.*

data class Note(
    val id: String = "",
    val title: String = "",
    val subject: String = "",
    val semester: String = "",
    val description: String = "",
    val fileName: String = "",
    val fileUrl: String = "",
    val uploadDate: Long = 0L,
    val fileSize: Long = 0L,
    val userId: String = ""
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "title" to title,
        "subject" to subject,
        "semester" to semester,
        "description" to description,
        "fileName" to fileName,
        "fileUrl" to fileUrl,
        "uploadDate" to uploadDate,
        "fileSize" to fileSize,
        "userId" to userId
    )
    
    companion object {
        fun fromMap(map: Map<String, Any>): Note = Note(
            id = map["id"] as? String ?: "",
            title = map["title"] as? String ?: "",
            subject = map["subject"] as? String ?: "",
            semester = map["semester"] as? String ?: "",
            description = map["description"] as? String ?: "",
            fileName = map["fileName"] as? String ?: "",
            fileUrl = map["fileUrl"] as? String ?: "",
            uploadDate = map["uploadDate"] as? Long ?: 0L,
            fileSize = map["fileSize"] as? Long ?: 0L,
            userId = map["userId"] as? String ?: ""
        )
    }
    
    fun getFormattedDate(): String {
        val date = Date(uploadDate)
        val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return format.format(date)
    }
    
    fun getFormattedSize(): String {
        return when {
            fileSize < 1024 -> "$fileSize B"
            fileSize < 1024 * 1024 -> "${fileSize / 1024} KB"
            else -> "${fileSize / (1024 * 1024)} MB"
        }
    }
}

sealed class NotesUiState {
    object Loading : NotesUiState()
    data class Success(val notes: List<Note>) : NotesUiState()
    data class Empty(val message: String) : NotesUiState()
    data class Error(val message: String) : NotesUiState()
}

data class FilterState(
    val selectedSubjects: List<String> = emptyList(),
    val selectedSemesters: List<String> = emptyList()
) {
    fun isActive(): Boolean = selectedSubjects.isNotEmpty() || selectedSemesters.isNotEmpty()
    fun clear(): FilterState = FilterState()
}

sealed class UploadState {
    object Idle : UploadState()
    data class Uploading(val progress: Int) : UploadState()
    object Success : UploadState()
    data class Error(val message: String) : UploadState()
}
