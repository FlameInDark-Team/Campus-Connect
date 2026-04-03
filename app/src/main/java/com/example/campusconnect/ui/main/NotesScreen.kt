package com.example.campusconnect.ui.main

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import kotlinx.coroutines.launch
import com.example.campusconnect.model.NotesUiState
import com.example.campusconnect.ui.components.EmptyState
import com.example.campusconnect.ui.components.LoadingIndicator
import com.example.campusconnect.ui.components.NoteCard
import com.example.campusconnect.viewmodel.NotesViewModel

@Composable
fun NotesScreen(
    viewModel: NotesViewModel,
    onNavigateToUpload: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val uiState by viewModel.notesUiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var noteToDelete by remember { mutableStateOf<com.example.campusconnect.model.Note?>(null) }
    
    if (showDeleteDialog && noteToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to delete this note?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        noteToDelete?.let {
                            viewModel.deleteNote(it.id, it.fileUrl)
                        }
                        showDeleteDialog = false
                        noteToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notes") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToUpload) {
                Icon(Icons.Default.Add, "Upload Note")
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToDashboard,
                    icon = { Icon(Icons.Default.Home, "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Description, "Notes") },
                    label = { Text("Notes") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProfile,
                    icon = { Icon(Icons.Default.Person, "Profile") },
                    label = { Text("Profile") }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchNotes(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search notes...") },
                leadingIcon = { Icon(Icons.Default.Search, "Search") },
                singleLine = true
            )
            
            // Content
            when (uiState) {
                is NotesUiState.Loading -> LoadingIndicator()
                is NotesUiState.Empty -> EmptyState(
                    message = (uiState as NotesUiState.Empty).message,
                    actionText = "Upload Note",
                    onActionClick = onNavigateToUpload
                )
                is NotesUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items((uiState as NotesUiState.Success).notes) { note ->
                            NoteCard(
                                note = note,
                                onView = {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(note.fileUrl.toUri(), "application/pdf")
                                        flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                                    }
                                    context.startActivity(intent)
                                },
                                onDownload = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Download feature coming soon")
                                    }
                                },
                                onShare = {
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "application/pdf"
                                        putExtra(Intent.EXTRA_TEXT, note.fileUrl)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Share Note"))
                                },
                                onDelete = {
                                    noteToDelete = note
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
                is NotesUiState.Error -> EmptyState(
                    message = (uiState as NotesUiState.Error).message,
                    actionText = "Retry",
                    onActionClick = { viewModel.loadNotes() }
                )
            }
        }
    }
}
