package com.example.campusconnect.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.campusconnect.ui.components.CustomButton
import com.example.campusconnect.ui.components.CustomTextField
import com.example.campusconnect.viewmodel.AttendanceViewModel

@Composable
fun AttendanceDetailScreen(
    subjectId: String,
    viewModel: AttendanceViewModel,
    onNavigateBack: () -> Unit
) {
    val subject by viewModel.getSubject(subjectId).collectAsState()
    var totalClasses by remember { mutableStateOf("") }
    var attendedClasses by remember { mutableStateOf("") }
    
    LaunchedEffect(subject) {
        subject?.let {
            totalClasses = it.totalClasses.toString()
            attendedClasses = it.attendedClasses.toString()
        }
    }
    
    val percentage = remember(totalClasses, attendedClasses) {
        val total = totalClasses.toIntOrNull() ?: 0
        val attended = attendedClasses.toIntOrNull() ?: 0
        viewModel.calculatePercentage(attended, total)
    }
    
    val classesNeeded = remember(totalClasses, attendedClasses) {
        val total = totalClasses.toIntOrNull() ?: 0
        val attended = attendedClasses.toIntOrNull() ?: 0
        if (percentage < 75.0) {
            viewModel.calculateClassesNeeded(attended, total)
        } else null
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(subject?.subjectName ?: "Attendance Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Current Attendance: ${String.format("%.2f", percentage)}%",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            
            if (percentage < 75.0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Your attendance is below 75%",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        
                        if (classesNeeded != null && classesNeeded > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Attend $classesNeeded more classes to reach 75%",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            CustomTextField(
                value = totalClasses,
                onValueChange = { totalClasses = it },
                label = "Total Classes",
                keyboardType = KeyboardType.Number
            )
            
            CustomTextField(
                value = attendedClasses,
                onValueChange = { attendedClasses = it },
                label = "Attended Classes",
                keyboardType = KeyboardType.Number
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            CustomButton(
                text = "Update Attendance",
                onClick = {
                    val total = totalClasses.toIntOrNull() ?: 0
                    val attended = attendedClasses.toIntOrNull() ?: 0
                    viewModel.updateAttendance(subjectId, total, attended)
                    onNavigateBack()
                },
                enabled = totalClasses.isNotBlank() && attendedClasses.isNotBlank()
            )
        }
    }
}
