package com.example.campusconnect.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.campusconnect.model.Note

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "subject")
    val subject: String,
    
    @ColumnInfo(name = "semester")
    val semester: String,
    
    @ColumnInfo(name = "description")
    val description: String,
    
    @ColumnInfo(name = "file_name")
    val fileName: String,
    
    @ColumnInfo(name = "file_url")
    val fileUrl: String,
    
    @ColumnInfo(name = "upload_date")
    val uploadDate: Long,
    
    @ColumnInfo(name = "file_size")
    val fileSize: Long,
    
    @ColumnInfo(name = "user_id")
    val userId: String
) {
    fun toNote(): Note = Note(
        id = id,
        title = title,
        subject = subject,
        semester = semester,
        description = description,
        fileName = fileName,
        fileUrl = fileUrl,
        uploadDate = uploadDate,
        fileSize = fileSize,
        userId = userId
    )
    
    companion object {
        fun fromNote(note: Note): NoteEntity = NoteEntity(
            id = note.id,
            title = note.title,
            subject = note.subject,
            semester = note.semester,
            description = note.description,
            fileName = note.fileName,
            fileUrl = note.fileUrl,
            uploadDate = note.uploadDate,
            fileSize = note.fileSize,
            userId = note.userId
        )
    }
}
