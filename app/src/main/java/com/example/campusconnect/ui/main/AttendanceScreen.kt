package com.example.campusconnect.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.campusconnect.model.AttendanceSubject
import com.example.campusconnect.model.AttendanceUiState
import com.example.campusconnect.ui.components.EmptyState
import com.example.campusconnect.ui.components.LoadingIndicator
import com.example.campusconnect.ui.components.SubjectCard
import com.example.campusconnect.viewmodel.AttendanceViewModel

@Composable
fun AttendanceScreen(
    viewModel: AttendanceViewModel,
    onNavigateToAddSubject: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToNotes: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val uiState by viewModel.attendanceUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var subjectToDelete by remember { mutableStateOf<AttendanceSubject?>(null) }
    
    if (showDeleteDialog && subjectToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Subject") },
            text = { Text("Are you sure you want to delete this subject?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        subjectToDelete?.let {
                            viewModel.deleteSubject(it.id)
                        }
                        showDeleteDialog = false
                        subjectToDelete = null
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
                title = { Text("Attendance") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddSubject) {
                Icon(Icons.Default.Add, "Add Subject")
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
                    selected = false,
                    onClick = onNavigateToNotes,
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
        when (uiState) {
            is AttendanceUiState.Loading -> LoadingIndicator()
            is AttendanceUiState.Empty -> EmptyState(
                message = (uiState as AttendanceUiState.Empty).message,
                icon = Icons.Default.EventNote,
                actionText = "Add Subject",
                onActionClick = onNavigateToAddSubject,
                modifier = Modifier.padding(padding)
            )
            is AttendanceUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items((uiState as AttendanceUiState.Success).subjects) { subject ->
                        SubjectCard(
                            subject = subject,
                            onClick = { onNavigateToDetail(subject.id) },
                            onDelete = {
                                subjectToDelete = subject
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
            is AttendanceUiState.Error -> EmptyState(
                message = (uiState as AttendanceUiState.Error).message,
                actionText = "Retry",
                onActionClick = { viewModel.loadSubjects() },
                modifier = Modifier.padding(padding)
            )
        }
    }
}
