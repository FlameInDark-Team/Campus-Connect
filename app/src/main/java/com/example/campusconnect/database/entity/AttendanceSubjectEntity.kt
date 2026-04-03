package com.example.campusconnect.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.campusconnect.model.AttendanceSubject

@Entity(tableName = "attendance_subjects")
data class AttendanceSubjectEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "subject_name")
    val subjectName: String,
    
    @ColumnInfo(name = "total_classes")
    val totalClasses: Int,
    
    @ColumnInfo(name = "attended_classes")
    val attendedClasses: Int,
    
    @ColumnInfo(name = "percentage")
    val percentage: Double,
    
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long,
    
    @ColumnInfo(name = "user_id")
    val userId: String
) {
    fun toAttendanceSubject(): AttendanceSubject = AttendanceSubject(
        id = id,
        subjectName = subjectName,
        totalClasses = totalClasses,
        attendedClasses = attendedClasses,
        percentage = percentage,
        lastUpdated = lastUpdated,
        userId = userId
    )
    
    companion object {
        fun fromAttendanceSubject(subject: AttendanceSubject): AttendanceSubjectEntity = 
            AttendanceSubjectEntity(
                id = subject.id,
                subjectName = subject.subjectName,
                totalClasses = subject.totalClasses,
                attendedClasses = subject.attendedClasses,
                percentage = subject.percentage,
                lastUpdated = subject.lastUpdated,
                userId = subject.userId
            )
    }
}
