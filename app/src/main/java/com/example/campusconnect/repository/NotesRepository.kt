package com.example.campusconnect.repository

import android.content.Context
import android.net.Uri
import com.example.campusconnect.database.dao.NotesDao
import com.example.campusconnect.database.entity.NoteEntity
import com.example.campusconnect.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.util.UUID

class NotesRepository(
    private val context: Context,
    private val notesDao: NotesDao
) {
    
    suspend fun uploadNote(
        userId: String,
        fileUri: Uri,
        title: String,
        subject: String,
        semester: String,
        description: String
    ): Result<Note> {
        return try {
            val timestamp = System.currentTimeMillis()
            val originalFileName = fileUri.lastPathSegment ?: "note.pdf"
            val fileName = "${timestamp}_$originalFileName"
            
            // Create notes directory in app's private storage
            val notesDir = File(context.filesDir, "notes/$userId")
            if (!notesDir.exists()) {
                notesDir.mkdirs()
            }
            
            // Copy file to local storage
            val destFile = File(notesDir, fileName)
            context.contentResolver.openInputStream(fileUri)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            
            val fileSize = destFile.length()
            val localFilePath = destFile.absolutePath
            
            // Create note metadata
            val note = Note(
                id = UUID.randomUUID().toString(),
                title = title,
                subject = subject,
                semester = semester,
                description = description,
                fileName = fileName,
                fileUrl = localFilePath, // Store local file path instead of URL
                uploadDate = timestamp,
                fileSize = fileSize,
                userId = userId
            )
            
            // Save metadata to Room Database
            try {
                notesDao.insertNote(NoteEntity.fromNote(note))
                Result.success(note)
            } catch (e: Exception) {
                // Rollback: delete local file
                destFile.delete()
                Result.failure(Exception("Failed to save note metadata"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(mapError(e)))
        }
    }
    
    suspend fun fetchNotes(userId: String): Result<List<Note>> {
        return try {
            val noteEntities = notesDao.getNotes(userId)
            val notes = noteEntities.map { it.toNote() }
            Result.success(notes)
        } catch (e: Exception) {
            Result.failure(Exception(mapError(e)))
        }
    }
    
    fun observeNotes(userId: String): Flow<List<Note>> {
        return notesDao.observeNotes(userId).map { entities ->
            entities.map { it.toNote() }
        }
    }
    
    suspend fun deleteNote(userId: String, noteId: String, fileUrl: String): Result<Unit> {
        return try {
            // Delete metadata from Room Database
            notesDao.deleteNoteById(noteId)
            
            // Delete local file
            val file = File(fileUrl)
            if (file.exists()) {
                file.delete()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapError(e)))
        }
    }
    
    private fun mapError(exception: Exception): String {
        return when {
            exception.message?.contains("permission") == true -> 
                "Permission denied. Please check your permissions"
            exception.message?.contains("not found") == true -> 
                "File not found"
            exception.message?.contains("space") == true -> 
                "Not enough storage space"
            else -> "Operation failed. Please try again"
        }
    }
}
