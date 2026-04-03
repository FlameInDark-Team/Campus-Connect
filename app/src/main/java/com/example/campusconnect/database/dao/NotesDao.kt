package com.example.campusconnect.database.dao

import androidx.room.*
import com.example.campusconnect.database.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    
    @Query("SELECT * FROM notes WHERE user_id = :userId ORDER BY upload_date DESC")
    fun observeNotes(userId: String): Flow<List<NoteEntity>>
    
    @Query("SELECT * FROM notes WHERE user_id = :userId ORDER BY upload_date DESC")
    suspend fun getNotes(userId: String): List<NoteEntity>
    
    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: String): NoteEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)
    
    @Update
    suspend fun updateNote(note: NoteEntity)
    
    @Delete
    suspend fun deleteNote(note: NoteEntity)
    
    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: String)
    
    @Query("DELETE FROM notes WHERE user_id = :userId")
    suspend fun deleteAllNotesForUser(userId: String)
    
    @Query("SELECT DISTINCT subject FROM notes WHERE user_id = :userId ORDER BY subject ASC")
    suspend fun getDistinctSubjects(userId: String): List<String>
    
    @Query("SELECT DISTINCT semester FROM notes WHERE user_id = :userId ORDER BY semester ASC")
    suspend fun getDistinctSemesters(userId: String): List<String>
}
