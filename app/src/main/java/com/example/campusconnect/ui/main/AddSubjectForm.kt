package com.example.campusconnect.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.campusconnect.ui.components.CustomButton
import com.example.campusconnect.ui.components.CustomTextField
import com.example.campusconnect.viewmodel.AttendanceViewModel

@Composable
fun AddSubjectForm(
    viewModel: AttendanceViewModel,
    onNavigateBack: () -> Unit
) {
    var subjectName by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Subject") },
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
            CustomTextField(
                value = subjectName,
                onValueChange = { subjectName = it },
                label = "Subject Name"
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            CustomButton(
                text = "Add Subject",
                onClick = {
                    viewModel.addSubject(subjectName)
                    onNavigateBack()
                },
                enabled = subjectName.isNotBlank()
            )
        }
    }
}
