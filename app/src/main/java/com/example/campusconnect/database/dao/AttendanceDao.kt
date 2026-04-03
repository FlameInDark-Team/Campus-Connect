package com.example.campusconnect.database.dao

import androidx.room.*
import com.example.campusconnect.database.entity.AttendanceSubjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    
    @Query("SELECT * FROM attendance_subjects WHERE user_id = :userId ORDER BY subject_name ASC")
    fun observeSubjects(userId: String): Flow<List<AttendanceSubjectEntity>>
    
    @Query("SELECT * FROM attendance_subjects WHERE user_id = :userId ORDER BY subject_name ASC")
    suspend fun getSubjects(userId: String): List<AttendanceSubjectEntity>
    
    @Query("SELECT * FROM attendance_subjects WHERE id = :subjectId")
    suspend fun getSubjectById(subjectId: String): AttendanceSubjectEntity?
    
    @Query("SELECT * FROM attendance_subjects WHERE user_id = :userId AND subject_name = :subjectName LIMIT 1")
    suspend fun getSubjectByName(userId: String, subjectName: String): AttendanceSubjectEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: AttendanceSubjectEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<AttendanceSubjectEntity>)
    
    @Update
    suspend fun updateSubject(subject: AttendanceSubjectEntity)
    
    @Delete
    suspend fun deleteSubject(subject: AttendanceSubjectEntity)
    
    @Query("DELETE FROM attendance_subjects WHERE id = :subjectId")
    suspend fun deleteSubjectById(subjectId: String)
    
    @Query("DELETE FROM attendance_subjects WHERE user_id = :userId")
    suspend fun deleteAllSubjectsForUser(userId: String)
}
