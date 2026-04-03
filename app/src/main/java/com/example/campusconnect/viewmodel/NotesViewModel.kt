package com.example.campusconnect.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusconnect.model.FilterState
import com.example.campusconnect.model.Note
import com.example.campusconnect.model.NotesUiState
import com.example.campusconnect.model.UploadState
import com.example.campusconnect.repository.AuthRepository
import com.example.campusconnect.repository.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotesViewModel(
    private val notesRepository: NotesRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _notesUiState = MutableStateFlow<NotesUiState>(NotesUiState.Loading)
    val notesUiState: StateFlow<NotesUiState> = _notesUiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()
    
    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()
    
    private var allNotes: List<Note> = emptyList()
    
    init {
        observeNotesRealtime()
    }
    
    private fun observeNotesRealtime() {
        val userId = authRepository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            notesRepository.observeNotes(userId).collect { notes ->
                allNotes = notes
                applyFilters()
            }
        }
    }
    
    fun loadNotes() {
        val userId = authRepository.getCurrentUser()?.uid ?: return
        _notesUiState.value = NotesUiState.Loading
        viewModelScope.launch {
            val result = notesRepository.fetchNotes(userId)
            if (result.isSuccess) {
                allNotes = result.getOrNull() ?: emptyList()
                applyFilters()
            } else {
                _notesUiState.value = NotesUiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to load notes"
                )
            }
        }
    }
    
    fun uploadNote(
        fileUri: Uri,
        title: String,
        subject: String,
        semester: String,
        description: String
    ) {
        if (title.isBlank() || subject.isBlank() || semester.isBlank()) {
            _uploadState.value = UploadState.Error("Please fill in all required fields")
            return
        }
        
        val userId = authRepository.getCurrentUser()?.uid ?: return
        _uploadState.value = UploadState.Uploading(0)
        
        viewModelScope.launch {
            val result = notesRepository.uploadNote(
                userId, fileUri, title, subject, semester, description
            )
            _uploadState.value = if (result.isSuccess) {
                UploadState.Success
            } else {
                UploadState.Error(result.exceptionOrNull()?.message ?: "Upload failed")
            }
        }
    }
    
    fun deleteNote(noteId: String, fileUrl: String) {
        val userId = authRepository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            notesRepository.deleteNote(userId, noteId, fileUrl)
        }
    }
    
    fun searchNotes(query: String) {
        _searchQuery.value = query
        applyFilters()
    }
    
    fun filterBySubject(subjects: List<String>) {
        _filterState.value = _filterState.value.copy(selectedSubjects = subjects)
        applyFilters()
    }
    
    fun filterBySemester(semesters: List<String>) {
        _filterState.value = _filterState.value.copy(selectedSemesters = semesters)
        applyFilters()
    }
    
    fun clearFilters() {
        _filterState.value = FilterState()
        _searchQuery.value = ""
        applyFilters()
    }
    
    private fun applyFilters() {
        var filtered = allNotes
        
        // Apply search
        if (_searchQuery.value.isNotEmpty()) {
            filtered = filtered.filter {
                it.title.contains(_searchQuery.value, ignoreCase = true)
            }
        }
        
        // Apply subject filter
        if (_filterState.value.selectedSubjects.isNotEmpty()) {
            filtered = filtered.filter {
                it.subject in _filterState.value.selectedSubjects
            }
        }
        
        // Apply semester filter
        if (_filterState.value.selectedSemesters.isNotEmpty()) {
            filtered = filtered.filter {
                it.semester in _filterState.value.selectedSemesters
            }
        }
        
        _notesUiState.value = when {
            filtered.isEmpty() && allNotes.isEmpty() -> 
                NotesUiState.Empty("No notes yet. Tap the + button to upload your first note")
            filtered.isEmpty() -> 
                NotesUiState.Empty("No notes match your filters")
            else -> NotesUiState.Success(filtered)
        }
    }
    
    fun resetUploadState() {
        _uploadState.value = UploadState.Idle
    }
}
