package com.example.campusconnect.repository

import com.example.campusconnect.database.dao.AttendanceDao
import com.example.campusconnect.database.entity.AttendanceSubjectEntity
import com.example.campusconnect.model.AttendanceSubject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class AttendanceRepository(private val attendanceDao: AttendanceDao) {
    
    suspend fun fetchSubjects(userId: String): Result<List<AttendanceSubject>> {
        return try {
            val subjectEntities = attendanceDao.getSubjects(userId)
            val subjects = subjectEntities.map { it.toAttendanceSubject() }
            Result.success(subjects)
        } catch (e: Exception) {
            Result.failure(Exception(mapError(e)))
        }
    }
    
    fun observeSubjects(userId: String): Flow<List<AttendanceSubject>> {
        return attendanceDao.observeSubjects(userId).map { entities ->
            entities.map { it.toAttendanceSubject() }
        }
    }
    
    suspend fun addSubject(userId: String, subjectName: String): Result<AttendanceSubject> {
        return try {
            // Check for duplicates
            val existing = attendanceDao.getSubjectByName(userId, subjectName)
            
            if (existing != null) {
                return Result.failure(Exception("A subject with this name already exists"))
            }
            
            val subject = AttendanceSubject(
                id = UUID.randomUUID().toString(),
                subjectName = subjectName,
                totalClasses = 0,
                attendedClasses = 0,
                percentage = 0.00,
                lastUpdated = System.currentTimeMillis(),
                userId = userId
            )
            
            attendanceDao.insertSubject(AttendanceSubjectEntity.fromAttendanceSubject(subject))
            Result.success(subject)
        } catch (e: Exception) {
            Result.failure(Exception(mapError(e)))
        }
    }
    
    suspend fun updateSubject(
        userId: String,
        subjectId: String,
        totalClasses: Int,
        attendedClasses: Int,
        percentage: Double
    ): Result<Unit> {
        return try {
            val existingEntity = attendanceDao.getSubjectById(subjectId)
                ?: return Result.failure(Exception("Subject not found"))
            
            val updatedEntity = existingEntity.copy(
                totalClasses = totalClasses,
                attendedClasses = attendedClasses,
                percentage = percentage,
                lastUpdated = System.currentTimeMillis()
            )
            
            attendanceDao.updateSubject(updatedEntity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapError(e)))
        }
    }
    
    suspend fun deleteSubject(userId: String, subjectId: String): Result<Unit> {
        return try {
            attendanceDao.deleteSubjectById(subjectId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapError(e)))
        }
    }
    
    private fun mapError(exception: Exception): String {
        return when {
            exception.message?.contains("network") == true -> 
                "Network error. Please check your connection"
            exception.message?.contains("permission") == true -> 
                "Permission denied"
            else -> "Operation failed. Please try again"
        }
    }
}
