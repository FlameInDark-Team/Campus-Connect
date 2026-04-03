package com.example.campusconnect.ui.main

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.campusconnect.model.UploadState
import com.example.campusconnect.ui.components.CustomButton
import com.example.campusconnect.ui.components.CustomTextField
import com.example.campusconnect.viewmodel.NotesViewModel

@Composable
fun UploadFormScreen(
    viewModel: NotesViewModel,
    onNavigateBack: () -> Unit
) {
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    
    val uploadState by viewModel.uploadState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
    }
    
    LaunchedEffect(uploadState) {
        when (uploadState) {
            is UploadState.Success -> {
                snackbarHostState.showSnackbar("Note uploaded successfully")
                viewModel.resetUploadState()
                onNavigateBack()
            }
            is UploadState.Error -> {
                snackbarHostState.showSnackbar((uploadState as UploadState.Error).message)
                viewModel.resetUploadState()
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Note") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { filePicker.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (selectedFileUri != null) "PDF Selected" else "Select PDF")
            }
            
            if (selectedFileUri != null) {
                Text(
                    text = "File: ${selectedFileUri?.lastPathSegment}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            CustomTextField(
                value = title,
                onValueChange = { title = it },
                label = "Title"
            )
            
            CustomTextField(
                value = subject,
                onValueChange = { subject = it },
                label = "Subject"
            )
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = semester,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Semester") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    (1..8).forEach { sem ->
                        DropdownMenuItem(
                            text = { Text("Semester $sem") },
                            onClick = {
                                semester = "Semester $sem"
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            CustomTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description",
                singleLine = false
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            CustomButton(
                text = "Upload",
                onClick = {
                    selectedFileUri?.let { uri ->
                        viewModel.uploadNote(uri, title, subject, semester, description)
                    }
                },
                enabled = selectedFileUri != null && title.isNotBlank() && 
                         subject.isNotBlank() && semester.isNotBlank(),
                isLoading = uploadState is UploadState.Uploading
            )
        }
    }
}
