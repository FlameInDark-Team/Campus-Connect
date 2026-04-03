# Design Document: Campus Connect Attendance Tracker Module (Phase 3)

## Overview

The Attendance Tracker Module is Phase 3 of Campus Connect, building upon the authentication system (Phase 1) and Notes Module (Phase 2). This module enables students to track attendance for their subjects, calculate attendance percentages, receive warnings when attendance falls below 75%, and understand how many classes they need to attend to reach the threshold.

The module integrates Firestore for data storage and follows the existing MVVM architecture established in Phase 1 and Phase 2. It reuses components from previous phases (CustomTextField, CustomButton, LoadingIndicator, ErrorMessage, EmptyState), maintains consistency with the Material 3 design system, and follows the same patterns for state management, navigation, and error handling.

Key architectural decisions:
- **Firestore Real-time Updates**: Attendance list updates automatically when subjects are added, updated, or deleted
- **Client-side Calculations**: Attendance percentage and classes needed calculated in ViewModel for instant feedback
- **Component Reuse**: Leverages Phase 1 and Phase 2 components for consistency and reduced code duplication
- **Color-Coded Indicators**: Visual feedback using green (≥75%) and red (<75%) for quick status identification
- **Warning System**: Prominent warnings and actionable guidance when attendance is below threshold
- **Real-time Percentage Updates**: Percentage recalculates as user types for immediate feedback

## Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        UI Layer                              │
│  (Jetpack Compose Screens + Components)                     │
│                                                              │
│  • AttendanceScreen (list, empty state)                     │
│  • AddSubjectForm (subject name input)                      │
│  • AttendanceDetailScreen (view/update attendance)          │
│  • SubjectCard (reusable card component)                    │
│  • Reused: CustomTextField, CustomButton, LoadingIndicator, │
│    ErrorMessage, EmptyState                                 │
└──────────────────┬──────────────────────────────────────────┘
                   │ observes StateFlow
                   │ calls ViewModel methods
┌──────────────────▼──────────────────────────────────────────┐
│                    ViewModel Layer                           │
│         (Business Logic + State Management)                  │
│                                                              │
│  • AttendanceViewModel (manages attendance state, calcs)    │
│  • Exposes StateFlow<AttendanceUiState> to UI               │
│  • Handles validation, percentage calc, classes needed calc │
└──────────────────┬──────────────────────────────────────────┘
                   │ calls repository methods
                   │ transforms data to UI state
┌──────────────────▼──────────────────────────────────────────┐
│                   Repository Layer                           │
│              (Data Access Abstraction)                       │
│                                                              │
│  • AttendanceRepository (Firestore operations)              │
│  • Manages subject CRUD operations                          │
│  • Provides real-time listeners                             │
└──────────────────┬──────────────────────────────────────────┘
                   │ uses Firebase SDK
┌──────────────────▼──────────────────────────────────────────┐
│                     Data Layer                               │
│              (Firebase Integration)                          │
│                                                              │
│  • Firestore (attendance data storage)                      │
└─────────────────────────────────────────────────────────────┘
```

### MVVM Pattern Implementation for Attendance Module

**Model Layer**:
- AttendanceSubject data class with fields: id, subjectName, totalClasses, attendedClasses, percentage, lastUpdated, userId
- AttendanceUiState sealed class representing UI states (Loading, Success, Error, Empty)
- Located in `model` package alongside Phase 1 and Phase 2 models

**View Layer**:
- AttendanceScreen: Main screen displaying subjects list with color-coded cards
- AddSubjectForm: Form for entering new subject name
- AttendanceDetailScreen: Screen for viewing and updating attendance data
- SubjectCard: Composable displaying subject with attendance stats and warning badge
- Observes AttendanceViewModel state using `collectAsState()`
- Calls ViewModel methods for user actions
- Reuses Phase 1 and Phase 2 components for consistency

**ViewModel Layer**:
- AttendanceViewModel extends ViewModel
- Manages AttendanceUiState using StateFlow
- Provides methods: loadSubjects, addSubject, updateAttendance, deleteSubject, calculatePercentage, calculateClassesNeeded
- Validates inputs before repository calls
- Transforms repository results into UI state
- Handles real-time Firestore updates
- Performs client-side calculations for percentage and classes needed

**Repository Layer**:
- AttendanceRepository abstracts Firestore operations
- Methods: fetchSubjects, addSubject, updateSubject, deleteSubject, observeSubjects
- Handles Firestore operations (create, read, update, delete, real-time listeners)
- Maps Firebase exceptions to domain errors
- Provides clean API for ViewModel

### Navigation Integration

The Attendance Module integrates into the existing navigation structure:

**Updated Main Graph**:
- Dashboard (existing, with new Attendance quick action card)
- NotesScreen (existing from Phase 2)
- AttendanceScreen (new route)
- AddSubjectForm (new route)
- AttendanceDetailScreen (new route with subject ID parameter)
- Profile (existing)

**Navigation Flow**:
1. User taps Attendance quick action card on Dashboard → Navigate to AttendanceScreen
2. User taps FAB on AttendanceScreen → Navigate to AddSubjectForm
3. User completes add subject → Navigate back to AttendanceScreen
4. User taps SubjectCard on AttendanceScreen → Navigate to AttendanceDetailScreen
5. User updates attendance → Navigate back to AttendanceScreen
6. Bottom navigation remains visible on AttendanceScreen
7. Bottom navigation hidden on AddSubjectForm and AttendanceDetailScreen (full-screen forms)

**Route Definitions** (added to Routes.kt):
```kotlin
const val ATTENDANCE = "attendance"
const val ADD_SUBJECT = "add_subject"
const val ATTENDANCE_DETAIL = "attendance_detail/{subjectId}"
```

## Components and Interfaces

### Package Structure

```
com.example.campusconnect/
├── ui/
│   ├── main/
│   │   ├── AttendanceScreen.kt            # NEW
│   │   ├── AddSubjectForm.kt              # NEW
│   │   └── AttendanceDetailScreen.kt      # NEW
│   └── components/
│       ├── SubjectCard.kt                 # NEW
│       ├── CustomTextField.kt             # EXISTING (reused)
│       ├── CustomButton.kt                # EXISTING (reused)
│       ├── LoadingIndicator.kt            # EXISTING (reused)
│       ├── ErrorMessage.kt                # EXISTING (reused)
│       └── EmptyState.kt                  # EXISTING (reused from Phase 2)
│
├── navigation/
│   ├── Routes.kt                          # MODIFY (add attendance routes)
│   └── MainNavGraph.kt                    # MODIFY (add attendance routes)
│
├── viewmodel/
│   └── AttendanceViewModel.kt             # NEW
│
├── repository/
│   └── AttendanceRepository.kt            # NEW
│
└── model/
    ├── AttendanceSubject.kt               # NEW
    └── AttendanceUiState.kt               # NEW
```

### Key Components

#### 1. AttendanceViewModel

```kotlin
class AttendanceViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _attendanceUiState = MutableStateFlow<AttendanceUiState>(AttendanceUiState.Loading)
    val attendanceUiState: StateFlow<AttendanceUiState> = _attendanceUiState.asStateFlow()
    
    // Methods
    fun loadSubjects()
    fun addSubject(subjectName: String)
    fun updateAttendance(subjectId: String, totalClasses: Int, attendedClasses: Int)
    fun deleteSubject(subjectId: String)
    fun calculatePercentage(attended: Int, total: Int): Double
    fun calculateClassesNeeded(attended: Int, total: Int): Int
    
    // Private helper methods
    private fun validateSubjectName(name: String): ValidationResult
    private fun validateAttendanceData(attended: Int, total: Int): ValidationResult
    private fun observeSubjectsRealtime()
}
```

#### 2. AttendanceRepository

```kotlin
class AttendanceRepository(
    private val firestore: FirebaseFirestore
) {
    
    suspend fun fetchSubjects(userId: String): Result<List<AttendanceSubject>>
    
    fun observeSubjects(userId: String): Flow<List<AttendanceSubject>>
    
    suspend fun addSubject(userId: String, subjectName: String): Result<AttendanceSubject>
    
    suspend fun updateSubject(
        userId: String,
        subjectId: String,
        totalClasses: Int,
        attendedClasses: Int,
        percentage: Double
    ): Result<Unit>
    
    suspend fun deleteSubject(userId: String, subjectId: String): Result<Unit>
    
    // Private helper methods
    private fun getFirestorePath(userId: String): CollectionReference
    private suspend fun checkDuplicateSubject(userId: String, subjectName: String): Boolean
}
```

#### 3. AttendanceScreen Composable

```kotlin
@Composable
fun AttendanceScreen(
    viewModel: AttendanceViewModel,
    onNavigateToAddSubject: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToNotes: () -> Unit
) {
    val uiState by viewModel.attendanceUiState.collectAsState()
    
    Scaffold(
        topBar = { AttendanceTopBar() },
        floatingActionButton = { AddSubjectFAB(onClick = onNavigateToAddSubject) },
        bottomBar = { BottomNavigationBar(selected = "attendance", ...) }
    ) { padding ->
        when (uiState) {
            is AttendanceUiState.Loading -> LoadingIndicator()
            is AttendanceUiState.Success -> SubjectsListContent(subjects, onNavigateToDetail, viewModel)
            is AttendanceUiState.Empty -> EmptyState(
                message = "No subjects added yet. Tap + to add your first subject",
                actionText = "Add Subject",
                onActionClick = onNavigateToAddSubject
            )
            is AttendanceUiState.Error -> ErrorState(message, onRetry = viewModel::loadSubjects)
        }
    }
}
```

#### 4. AddSubjectForm Composable

```kotlin
@Composable
fun AddSubjectForm(
    viewModel: AttendanceViewModel,
    onNavigateBack: () -> Unit
) {
    var subjectName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val isLoading by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = { AddSubjectTopBar(onBackClick = onNavigateBack) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            CustomTextField(
                value = subjectName,
                onValueChange = { subjectName = it },
                label = "Subject Name",
                errorMessage = errorMessage
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CustomButton(
                text = "Add Subject",
                onClick = {
                    viewModel.addSubject(subjectName)
                },
                enabled = !isLoading
            )
            
            if (isLoading) {
                LoadingIndicator()
            }
        }
    }
}
```

#### 5. AttendanceDetailScreen Composable

```kotlin
@Composable
fun AttendanceDetailScreen(
    subjectId: String,
    viewModel: AttendanceViewModel,
    onNavigateBack: () -> Unit
) {
    val subject by viewModel.getSubject(subjectId).collectAsState()
    var totalClasses by remember { mutableStateOf(subject?.totalClasses?.toString() ?: "0") }
    var attendedClasses by remember { mutableStateOf(subject?.attendedClasses?.toString() ?: "0") }
    
    // Real-time percentage calculation
    val percentage = remember(totalClasses, attendedClasses) {
        val total = totalClasses.toIntOrNull() ?: 0
        val attended = attendedClasses.toIntOrNull() ?: 0
        viewModel.calculatePercentage(attended, total)
    }
    
    // Real-time classes needed calculation
    val classesNeeded = remember(totalClasses, attendedClasses) {
        val total = totalClasses.toIntOrNull() ?: 0
        val attended = attendedClasses.toIntOrNull() ?: 0
        if (percentage < 75.0) {
            viewModel.calculateClassesNeeded(attended, total)
        } else null
    }
    
    Scaffold(
        topBar = { 
            AttendanceDetailTopBar(
                subjectName = subject?.subjectName ?: "",
                onBackClick = onNavigateBack,
                onDeleteClick = { viewModel.deleteSubject(subjectId) }
            ) 
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            // Display current stats
            Text(text = "Current Attendance: ${String.format("%.2f", percentage)}%")
            
            // Warning message if below 75%
            if (percentage < 75.0) {
                WarningCard(
                    message = "Your attendance is below 75%",
                    classesNeeded = classesNeeded
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Input fields
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CustomButton(
                text = "Update Attendance",
                onClick = {
                    val total = totalClasses.toIntOrNull() ?: 0
                    val attended = attendedClasses.toIntOrNull() ?: 0
                    viewModel.updateAttendance(subjectId, total, attended)
                }
            )
        }
    }
}
```

#### 6. SubjectCard Component

```kotlin
@Composable
fun SubjectCard(
    subject: AttendanceSubject,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val backgroundColor = if (subject.percentage >= 75.0) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.errorContainer
    }
    
    val textColor = if (subject.percentage >= 75.0) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onErrorContainer
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),  // Consistent with Phase 1 and Phase 2
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = subject.subjectName,
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor
                    )
                    
                    // Warning badge if below 75%
                    if (subject.percentage < 75.0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge(
                            containerColor = MaterialTheme.colorScheme.error
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Low attendance",
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Total: ${subject.totalClasses}",
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor
                )
                Text(
                    text = "Attended: ${subject.attendedClasses}",
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${String.format("%.2f", subject.percentage)}%",
                    style = MaterialTheme.typography.titleLarge,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete subject",
                    tint = textColor
                )
            }
        }
    }
}
```

### Firestore Collection Structure

The Attendance Module uses a subcollection structure in Firestore:

```
users/
  {userId}/
    attendance/
      {subjectId}/
        - id: string
        - subjectName: string
        - totalClasses: number
        - attendedClasses: number
        - percentage: number
        - lastUpdated: timestamp
        - userId: string
```

**Collection Path**: `users/{userId}/attendance`

**Document Structure**:
```json
{
  "id": "subject123",
  "subjectName": "Data Structures",
  "totalClasses": 40,
  "attendedClasses": 32,
  "percentage": 80.00,
  "lastUpdated": "2024-01-15T10:30:00Z",
  "userId": "abc123"
}
```

**Rationale**:
- Subcollection structure provides automatic user isolation (consistent with Phase 2)
- Document ID auto-generated by Firestore for uniqueness
- percentage stored for display without recalculation on every read
- lastUpdated as timestamp for tracking changes
- userId stored for additional validation

### Firestore Queries

**Fetch All Subjects for User**:
```kotlin
firestore.collection("users")
    .document(userId)
    .collection("attendance")
    .orderBy("subjectName", Query.Direction.ASCENDING)
    .get()
```

**Real-time Listener**:
```kotlin
firestore.collection("users")
    .document(userId)
    .collection("attendance")
    .orderBy("subjectName", Query.Direction.ASCENDING)
    .addSnapshotListener { snapshot, error ->
        // Handle updates
    }
```

**Add Subject**:
```kotlin
firestore.collection("users")
    .document(userId)
    .collection("attendance")
    .add(subjectData)
```

**Update Subject**:
```kotlin
firestore.collection("users")
    .document(userId)
    .collection("attendance")
    .document(subjectId)
    .update(updateData)
```

**Delete Subject**:
```kotlin
firestore.collection("users")
    .document(userId)
    .collection("attendance")
    .document(subjectId)
    .delete()
```

### Attendance Percentage Calculation

The attendance percentage is calculated using the formula:

```kotlin
fun calculatePercentage(attended: Int, total: Int): Double {
    return if (total == 0) {
        0.00
    } else {
        (attended.toDouble() / total.toDouble()) * 100.0
    }
}
```

**Formula**: `(attended / total) × 100`

**Special Cases**:
- When total classes is 0, percentage is 0.00
- Result is formatted to 2 decimal places for display
- Calculation performed in ViewModel for instant feedback
- Stored in Firestore for efficient retrieval

### Classes Needed Calculation

The classes needed to reach 75% is calculated using the formula:

```kotlin
fun calculateClassesNeeded(attended: Int, total: Int): Int {
    val currentPercentage = calculatePercentage(attended, total)
    
    if (currentPercentage >= 75.0) {
        return 0
    }
    
    // Formula: ceil((0.75 × total - attended) / 0.25)
    val needed = ((0.75 * total) - attended) / 0.25
    return ceil(needed).toInt()
}
```

**Formula**: `ceil((0.75 × total - attended) / 0.25)`

**Explanation**:
- This formula calculates how many additional classes must be attended (assuming all future classes are attended)
- The denominator 0.25 represents the net gain per class attended (1.0 for attended - 0.75 for total increase)
- ceil() ensures we round up to the next whole number
- Returns 0 if already at or above 75%

**Example**:
- Total classes: 40
- Attended: 25
- Current percentage: 62.5%
- Classes needed: ceil((0.75 × 40 - 25) / 0.25) = ceil((30 - 25) / 0.25) = ceil(20) = 20

### Color-Coded Visual Indicators

The Attendance Module uses color-coding for quick status identification:

**Green Indicator (≥75%)**:
- Background: `MaterialTheme.colorScheme.primaryContainer`
- Text: `MaterialTheme.colorScheme.onPrimaryContainer`
- No warning badge displayed
- Indicates healthy attendance status

**Red Indicator (<75%)**:
- Background: `MaterialTheme.colorScheme.errorContainer`
- Text: `MaterialTheme.colorScheme.onErrorContainer`
- Warning badge displayed with warning icon
- Indicates attention needed

**Implementation**:
```kotlin
val backgroundColor = if (subject.percentage >= 75.0) {
    MaterialTheme.colorScheme.primaryContainer
} else {
    MaterialTheme.colorScheme.errorContainer
}

val textColor = if (subject.percentage >= 75.0) {
    MaterialTheme.colorScheme.onPrimaryContainer
} else {
    MaterialTheme.colorScheme.onErrorContainer
}
```

**Accessibility**:
- Colors follow Material 3 color system for proper contrast
- Warning badge provides additional visual indicator beyond color
- Text remains readable in both light and dark modes

### Warning System Implementation

The warning system provides clear feedback when attendance is below 75%:

**Warning Badge on SubjectCard**:
```kotlin
if (subject.percentage < 75.0) {
    Badge(
        containerColor = MaterialTheme.colorScheme.error
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Low attendance",
            modifier = Modifier.size(12.dp)
        )
    }
}
```

**Warning Card on AttendanceDetailScreen**:
```kotlin
@Composable
fun WarningCard(message: String, classesNeeded: Int?) {
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
                    text = message,
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
```

**Warning Display Logic**:
- Warning badge appears on SubjectCard when percentage < 75%
- Warning card appears on AttendanceDetailScreen when percentage < 75%
- Classes needed message displays actionable guidance
- Warnings disappear automatically when percentage reaches 75%

## Data Models

### AttendanceSubject Model

```kotlin
data class AttendanceSubject(
    val id: String = "",
    val subjectName: String = "",
    val totalClasses: Int = 0,
    val attendedClasses: Int = 0,
    val percentage: Double = 0.00,
    val lastUpdated: Long = 0L,  // Timestamp in milliseconds
    val userId: String = ""
) {
    // Convert to Firestore document
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "subjectName" to subjectName,
        "totalClasses" to totalClasses,
        "attendedClasses" to attendedClasses,
        "percentage" to percentage,
        "lastUpdated" to lastUpdated,
        "userId" to userId
    )
    
    companion object {
        // Convert from Firestore document
        fun fromMap(map: Map<String, Any>): AttendanceSubject = AttendanceSubject(
            id = map["id"] as? String ?: "",
            subjectName = map["subjectName"] as? String ?: "",
            totalClasses = (map["totalClasses"] as? Long)?.toInt() ?: 0,
            attendedClasses = (map["attendedClasses"] as? Long)?.toInt() ?: 0,
            percentage = map["percentage"] as? Double ?: 0.00,
            lastUpdated = map["lastUpdated"] as? Long ?: 0L,
            userId = map["userId"] as? String ?: ""
        )
    }
    
    // Helper methods
    fun getFormattedPercentage(): String {
        return String.format("%.2f", percentage)
    }
    
    fun isLowAttendance(): Boolean {
        return percentage < 75.0
    }
    
    fun getFormattedLastUpdated(): String {
        val date = Date(lastUpdated)
        val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return format.format(date)
    }
}
```

### AttendanceUiState Model

```kotlin
sealed class AttendanceUiState {
    object Loading : AttendanceUiState()
    data class Success(val subjects: List<AttendanceSubject>) : AttendanceUiState()
    data class Empty(val message: String) : AttendanceUiState()
    data class Error(val message: String) : AttendanceUiState()
}
```

## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*


### Property 1: Subject Card Rendering

*For any* list of subjects, the AttendanceScreen should render the correct number of SubjectCards, each displaying subject name, total classes, attended classes, and attendance percentage.

**Validates: Requirements 1.1, 1.2**

### Property 2: Percentage Formatting

*For any* attendance percentage value, the system should format it with exactly 2 decimal places.

**Validates: Requirements 1.3, 5.3**

### Property 3: Green Indicator for Healthy Attendance

*For any* subject with attendance percentage greater than or equal to 75%, the SubjectCard should display a green visual indicator and no warning badge.

**Validates: Requirements 1.4, 6.5**

### Property 4: Red Indicator for Low Attendance

*For any* subject with attendance percentage less than 75%, the SubjectCard should display a red visual indicator and a warning badge.

**Validates: Requirements 1.5, 1.6, 6.1**

### Property 5: Subject Name Validation

*For any* string provided as a subject name, if the string is empty or contains only whitespace, the validation system should reject it and display an error message.

**Validates: Requirements 2.4**

### Property 6: Duplicate Subject Validation

*For any* subject name that already exists in the user's subject list, the validation system should reject it and display an error message.

**Validates: Requirements 2.5**

### Property 7: Subject Addition to Firestore

*For any* valid subject name, calling the addSubject method should create a new document in Firestore with all required fields.

**Validates: Requirements 2.8, 8.3**

### Property 8: Attendance Detail Display

*For any* subject, the AttendanceDetailScreen should display the subject's current name, total classes, attended classes, and percentage.

**Validates: Requirements 3.2, 3.3, 3.4, 3.5**

### Property 9: Non-Negative Integer Validation

*For any* input to total classes or attended classes fields, if the value is negative, the validation system should reject it.

**Validates: Requirements 3.8, 3.9**

### Property 10: Attended Not Exceeding Total Validation

*For any* pair of attended and total classes values, if attended exceeds total, the validation system should reject it and display an error message.

**Validates: Requirements 3.10, 18.4**

### Property 11: Attendance Update to Firestore

*For any* valid attendance data (total classes and attended classes), calling the updateAttendance method should update the existing document in Firestore with the new values and recalculated percentage.

**Validates: Requirements 4.2, 8.4**

### Property 12: Percentage Calculation Formula

*For any* pair of attended and total classes values where total is not zero, the calculated percentage should equal (attended / total) × 100.

**Validates: Requirements 5.1**

### Property 13: Zero Total Classes Handling

*For any* subject with total classes equal to zero, the calculated percentage should be 0.00.

**Validates: Requirements 5.2, 30.1**

### Property 14: Real-time Percentage Update

*For any* change to attended or total classes values in the AttendanceDetailScreen, the displayed percentage should recalculate and update immediately without requiring a button press.

**Validates: Requirements 5.4, 5.5, 28.1, 28.2**

### Property 15: Warning Display for Low Attendance

*For any* subject with attendance percentage less than 75%, the AttendanceDetailScreen should display a warning message.

**Validates: Requirements 6.2**

### Property 16: No Warning for Healthy Attendance

*For any* subject with attendance percentage greater than or equal to 75%, the AttendanceDetailScreen should not display a warning message or classes needed message.

**Validates: Requirements 6.6, 7.4**

### Property 17: Classes Needed Calculation Formula

*For any* subject with attendance percentage less than 75%, the calculated classes needed should equal ceil((0.75 × total - attended) / 0.25).

**Validates: Requirements 7.1, 7.2**

### Property 18: Classes Needed Message Display

*For any* subject with attendance percentage less than 75%, the AttendanceDetailScreen should display a message showing how many classes need to be attended to reach 75%.

**Validates: Requirements 7.3**

### Property 19: Real-time Classes Needed Update

*For any* change to attended or total classes values that results in percentage below 75%, the classes needed message should recalculate and update immediately.

**Validates: Requirements 7.5, 28.4**

### Property 20: Firestore Document Structure

*For any* subject saved to Firestore, the document should include all required fields: subjectName, totalClasses, attendedClasses, percentage, lastUpdated, userId.

**Validates: Requirements 8.2**

### Property 21: LastUpdated Timestamp Update

*For any* update operation on a subject, the lastUpdated field should be set to the current timestamp.

**Validates: Requirements 8.5**

### Property 22: Error Message Mapping

*For any* Firestore operation failure, the repository should return a user-friendly error message without exposing technical details or stack traces.

**Validates: Requirements 8.7, 15.5, 15.6**

### Property 23: Subject Deletion from Firestore

*For any* subject deletion confirmation, the repository should delete the subject document from Firestore.

**Validates: Requirements 9.4**

### Property 24: Real-time Subject Addition

*For any* subject added to Firestore, the AttendanceScreen should automatically display the new subject without manual refresh.

**Validates: Requirements 17.2**

### Property 25: Real-time Subject Update

*For any* subject updated in Firestore, the AttendanceScreen should automatically update the subject card without manual refresh.

**Validates: Requirements 17.3**

### Property 26: Real-time Subject Deletion

*For any* subject deleted from Firestore, the AttendanceScreen should automatically remove the subject from the list without manual refresh.

**Validates: Requirements 17.4**

### Property 27: Numeric Input Validation

*For any* input to total classes or attended classes fields, if the value contains non-numeric characters, the system should prevent input or display a validation error.

**Validates: Requirements 18.2, 18.3**

### Property 28: Validation Before API Calls

*For any* add or update operation, the validation system should execute and pass before any Firestore API call is made.

**Validates: Requirements 18.5**

### Property 29: Real-time Calculation Without Save

*For any* change to attended or total classes values in the AttendanceDetailScreen, the percentage and warnings should update in real-time without triggering Firestore updates until the update button is tapped.

**Validates: Requirements 28.3**

### Property 30: Zero Classes Allowed

*For any* subject with zero total classes and zero attended classes, the system should allow the subject to be created and stored.

**Validates: Requirements 30.5**

### Property 31: AttendanceSubject Model Round-trip

*For any* AttendanceSubject object, converting it to Firestore document format and back should preserve all field values.

**Validates: Requirements 21.4**


## Error Handling

### Error Handling Strategy

The Attendance Module implements a comprehensive error handling approach consistent with Phase 1 and Phase 2:

**Repository Layer**:
- Catches Firestore exceptions (CRUD operation failures)
- Maps exceptions to domain-specific error messages
- Returns `Result<T>` type (Success or Failure)
- Provides clean API for ViewModel

**Exception Mappings**:

Firestore Errors:
- `FirebaseFirestoreException.PERMISSION_DENIED` → "Permission denied. Please log in again"
- `FirebaseFirestoreException.UNAVAILABLE` → "Service unavailable. Please try again later"
- `FirebaseFirestoreException.NOT_FOUND` → "Subject not found. It may have been deleted"
- `FirebaseFirestoreException.ALREADY_EXISTS` → "A subject with this name already exists"
- Network errors → "Network error. Please check your connection and try again"

Validation Errors:
- Empty subject name → "Subject name is required"
- Duplicate subject name → "A subject with this name already exists"
- Negative total classes → "Total classes must be a non-negative number"
- Negative attended classes → "Attended classes must be a non-negative number"
- Attended exceeds total → "Attended classes cannot exceed total classes"
- Non-numeric input → "Please enter a valid number"

**ViewModel Layer**:
- Receives Result<T> from repository
- Transforms errors into AttendanceUiState.Error
- Ensures error messages are student-friendly and actionable
- Never exposes stack traces or technical details
- Maintains UI state consistency during errors

**UI Layer**:
- Observes state from ViewModel
- Displays errors using ErrorMessage component (Snackbar style) from Phase 1
- Shows field-specific validation errors below input fields
- Provides visual feedback for all error states
- Auto-dismisses snackbars after 4 seconds (consistent with Phase 1 and Phase 2)

### Error Categories

**Validation Errors**:
- Displayed immediately below input fields
- Red text with error icon
- Examples: "Subject name is required", "Attended classes cannot exceed total classes"

**Firestore Errors**:
- Displayed as Snackbar on AttendanceScreen
- Auto-dismiss after 4 seconds
- Examples: "Network error. Please check your connection and try again", "Service unavailable"

**System Errors**:
- Displayed as Snackbar
- Generic user-friendly message
- Example: "Something went wrong. Please try again"

### Error Recovery

**Retry Support**:
- All error states allow user to retry the operation
- Input fields remain populated after errors (user doesn't lose data)
- Network errors include implicit retry when user attempts operation again

**State Preservation**:
- Failed additions don't clear the form (user can fix and retry)
- Failed updates don't affect subject display
- Failed deletes keep subject in list with error feedback


## Testing Strategy

### Dual Testing Approach

The Attendance Module uses both unit testing and property-based testing for comprehensive coverage, following Phase 1 and Phase 2 patterns:

**Unit Tests**:
- Specific examples and edge cases
- Integration points between components
- Error conditions and boundary cases
- UI component rendering and interaction
- Navigation flows
- Confirmation dialogs

**Property-Based Tests**:
- Universal properties across all inputs
- Validation logic with randomized inputs
- Calculation formulas with generated data
- State transitions and flow correctness
- Real-time update behavior
- Comprehensive input coverage through randomization

Both approaches are complementary: unit tests catch concrete bugs and verify specific scenarios, while property tests verify general correctness across a wide range of inputs.

### Property-Based Testing Configuration

**Library**: Kotest Property Testing (consistent with Phase 1 and Phase 2)
- Mature property-based testing library for Kotlin/Android
- Integrates well with JUnit and Android testing frameworks
- Provides generators for common types and custom data

**Configuration**:
- Minimum 100 iterations per property test
- Each test tagged with reference to design document property
- Tag format: `@Tag("Feature: campus-connect-attendance-tracker, Property {number}: {property_text}")`

**Example Property Test Structure**:
```kotlin
@Test
@Tag("Feature: campus-connect-attendance-tracker, Property 12: Percentage Calculation Formula")
fun `percentage calculation follows formula for all valid inputs`() = runTest {
    checkAll(100, Arb.int(1..100), Arb.int(0..100)) { total, attended ->
        whenever(attended <= total) {
            val viewModel = AttendanceViewModel(mockRepository, mockAuthRepository)
            val percentage = viewModel.calculatePercentage(attended, total)
            
            val expected = (attended.toDouble() / total.toDouble()) * 100.0
            percentage shouldBe expected plusOrMinus 0.01
        }
    }
}
```

### Test Coverage Areas

**AttendanceViewModel Tests**:
- Load subjects from repository
- Add subject with validation (property-based)
- Update attendance with validation (property-based)
- Delete subject
- Calculate percentage (property-based)
- Calculate classes needed (property-based)
- State transformations
- Real-time update handling

**AttendanceRepository Tests** (with mocked Firestore):
- Fetch subjects from Firestore
- Add subject to Firestore with correct structure
- Update subject in Firestore
- Delete subject from Firestore
- Real-time listener setup
- Error mapping correctness
- Duplicate subject detection

**UI Component Tests**:
- AttendanceScreen rendering with various states
- SubjectCard rendering with subject data
- SubjectCard color coding based on percentage
- Warning badge display logic
- AddSubjectForm validation display
- AttendanceDetailScreen real-time calculations
- EmptyState rendering

**Navigation Tests**:
- Navigate from Dashboard to AttendanceScreen
- Navigate from AttendanceScreen to AddSubjectForm
- Navigate from AttendanceScreen to AttendanceDetailScreen
- Navigate back after successful operations
- Bottom navigation integration

**Calculation Tests** (Property-Based):
- Percentage calculation with random valid inputs
- Classes needed calculation with random inputs below 75%
- Zero total classes edge case
- Real-time recalculation on input change

**Validation Tests** (Property-Based):
- Empty subject name validation with whitespace variations
- Duplicate subject name validation with random subject lists
- Non-negative integer validation with random integers
- Attended not exceeding total validation with random pairs
- Numeric input validation with random strings

**Error Handling Tests**:
- Network error scenarios
- Firestore unavailable scenarios
- Permission denied scenarios
- Validation error display
- Error message user-friendliness

**Real-time Update Tests**:
- Subject addition triggers UI update
- Subject update triggers UI update
- Subject deletion triggers UI update
- Real-time listener integration

### Testing Tools

- **JUnit 5**: Test framework (consistent with Phase 1 and Phase 2)
- **Kotest**: Property-based testing (consistent with Phase 1 and Phase 2)
- **MockK**: Mocking Firestore dependencies (consistent with Phase 1 and Phase 2)
- **Turbine**: Testing StateFlow emissions (consistent with Phase 1 and Phase 2)
- **Compose Test**: UI component testing
- **Robolectric**: Android framework testing without emulator

### Test Organization

```
src/test/                                          # Unit tests
├── viewmodel/
│   └── AttendanceViewModelTest.kt                # ViewModel logic tests
├── repository/
│   └── AttendanceRepositoryTest.kt               # Repository with mocked Firestore
├── validation/
│   └── AttendanceValidationPropertyTests.kt      # Property-based validation tests
├── calculation/
│   └── AttendanceCalculationPropertyTests.kt     # Property-based calculation tests
└── model/
    └── AttendanceSubjectModelTest.kt             # Model conversion tests

src/androidTest/                                   # Instrumentation tests
├── ui/
│   ├── AttendanceScreenTest.kt                   # AttendanceScreen UI tests
│   ├── AddSubjectFormTest.kt                     # AddSubjectForm UI tests
│   ├── AttendanceDetailScreenTest.kt             # AttendanceDetailScreen UI tests
│   └── SubjectCardTest.kt                        # SubjectCard component tests
└── integration/
    ├── AttendanceFlowIntegrationTest.kt          # End-to-end attendance flow
    └── RealtimeUpdateTest.kt                     # Real-time listener integration
```

### Test Data Generators (for Property-Based Tests)

```kotlin
// Custom Kotest generators for Attendance Module
fun Arb.Companion.attendanceSubject(): Arb<AttendanceSubject> = arbitrary {
    val total = Arb.int(0..100).bind()
    val attended = Arb.int(0..total).bind()
    val percentage = if (total == 0) 0.00 else (attended.toDouble() / total.toDouble()) * 100.0
    
    AttendanceSubject(
        id = Arb.string(10..20).bind(),
        subjectName = Arb.string(5..50).bind(),
        totalClasses = total,
        attendedClasses = attended,
        percentage = percentage,
        lastUpdated = Arb.long(1000000000000L..9999999999999L).bind(),
        userId = Arb.string(10..20).bind()
    )
}

fun Arb.Companion.lowAttendanceSubject(): Arb<AttendanceSubject> = arbitrary {
    val total = Arb.int(10..100).bind()
    val maxAttended = (total * 0.74).toInt()
    val attended = Arb.int(0..maxAttended).bind()
    val percentage = (attended.toDouble() / total.toDouble()) * 100.0
    
    AttendanceSubject(
        id = Arb.string(10..20).bind(),
        subjectName = Arb.string(5..50).bind(),
        totalClasses = total,
        attendedClasses = attended,
        percentage = percentage,
        lastUpdated = Arb.long(1000000000000L..9999999999999L).bind(),
        userId = Arb.string(10..20).bind()
    )
}

fun Arb.Companion.healthyAttendanceSubject(): Arb<AttendanceSubject> = arbitrary {
    val total = Arb.int(10..100).bind()
    val minAttended = ceil(total * 0.75).toInt()
    val attended = Arb.int(minAttended..total).bind()
    val percentage = (attended.toDouble() / total.toDouble()) * 100.0
    
    AttendanceSubject(
        id = Arb.string(10..20).bind(),
        subjectName = Arb.string(5..50).bind(),
        totalClasses = total,
        attendedClasses = attended,
        percentage = percentage,
        lastUpdated = Arb.long(1000000000000L..9999999999999L).bind(),
        userId = Arb.string(10..20).bind()
    )
}
```


## File Structure and Implementation

### Complete File Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/campusconnect/
│   │   │   │
│   │   │   ├── ui/
│   │   │   │   ├── main/
│   │   │   │   │   ├── AttendanceScreen.kt            # CREATE
│   │   │   │   │   ├── AddSubjectForm.kt              # CREATE
│   │   │   │   │   ├── AttendanceDetailScreen.kt      # CREATE
│   │   │   │   │   ├── DashboardScreen.kt             # MODIFY (add attendance card)
│   │   │   │   │   ├── NotesScreen.kt                 # EXISTING
│   │   │   │   │   └── ProfilePlaceholderScreen.kt    # EXISTING
│   │   │   │   └── components/
│   │   │   │       ├── SubjectCard.kt                 # CREATE
│   │   │   │       ├── CustomTextField.kt             # EXISTING (reused)
│   │   │   │       ├── CustomButton.kt                # EXISTING (reused)
│   │   │   │       ├── LoadingIndicator.kt            # EXISTING (reused)
│   │   │   │       ├── ErrorMessage.kt                # EXISTING (reused)
│   │   │   │       ├── EmptyState.kt                  # EXISTING (reused)
│   │   │   │       ├── NoteCard.kt                    # EXISTING
│   │   │   │       └── QuickActionCard.kt             # EXISTING
│   │   │   │
│   │   │   ├── navigation/
│   │   │   │   ├── Routes.kt                          # MODIFY (add attendance routes)
│   │   │   │   └── MainNavGraph.kt                    # MODIFY (add attendance routes)
│   │   │   │
│   │   │   ├── viewmodel/
│   │   │   │   ├── AttendanceViewModel.kt             # CREATE
│   │   │   │   ├── NotesViewModel.kt                  # EXISTING
│   │   │   │   ├── AuthViewModel.kt                   # EXISTING
│   │   │   │   └── DashboardViewModel.kt              # EXISTING
│   │   │   │
│   │   │   ├── repository/
│   │   │   │   ├── AttendanceRepository.kt            # CREATE
│   │   │   │   ├── NotesRepository.kt                 # EXISTING
│   │   │   │   └── AuthRepository.kt                  # EXISTING
│   │   │   │
│   │   │   ├── model/
│   │   │   │   ├── AttendanceSubject.kt               # CREATE
│   │   │   │   ├── AttendanceUiState.kt               # CREATE
│   │   │   │   ├── Note.kt                            # EXISTING
│   │   │   │   ├── NotesUiState.kt                    # EXISTING
│   │   │   │   ├── User.kt                            # EXISTING
│   │   │   │   ├── AuthState.kt                       # EXISTING
│   │   │   │   └── ValidationResult.kt                # EXISTING
│   │   │   │
│   │   │   ├── data/
│   │   │   │   └── FirebaseManager.kt                 # EXISTING
│   │   │   │
│   │   │   └── theme/
│   │   │       ├── Color.kt                           # EXISTING
│   │   │       ├── Type.kt                            # EXISTING
│   │   │       ├── Theme.kt                           # EXISTING
│   │   │       └── Spacing.kt                         # EXISTING
│   │   │
│   │   ├── res/
│   │   │   └── values/
│   │   │       └── strings.xml                        # MODIFY (add attendance strings)
│   │   │
│   │   └── AndroidManifest.xml                        # NO CHANGES
│   │
│   ├── test/                                          # Unit tests
│   │   └── java/com/example/campusconnect/
│   │       ├── viewmodel/
│   │       │   └── AttendanceViewModelTest.kt         # CREATE
│   │       ├── repository/
│   │       │   └── AttendanceRepositoryTest.kt        # CREATE
│   │       ├── validation/
│   │       │   └── AttendanceValidationPropertyTests.kt # CREATE
│   │       ├── calculation/
│   │       │   └── AttendanceCalculationPropertyTests.kt # CREATE
│   │       └── model/
│   │           └── AttendanceSubjectModelTest.kt      # CREATE
│   │
│   └── androidTest/                                   # Instrumentation tests
│       └── java/com/example/campusconnect/
│           ├── ui/
│           │   ├── AttendanceScreenTest.kt            # CREATE
│           │   ├── AddSubjectFormTest.kt              # CREATE
│           │   ├── AttendanceDetailScreenTest.kt      # CREATE
│           │   └── SubjectCardTest.kt                 # CREATE
│           └── integration/
│               ├── AttendanceFlowIntegrationTest.kt   # CREATE
│               └── RealtimeUpdateTest.kt              # CREATE
│
└── build.gradle.kts (app level)                       # NO CHANGES (all dependencies from Phase 1)
```

### Files to Create (15 new files)

**Models** (2 files):
1. AttendanceSubject.kt
2. AttendanceUiState.kt

**Repository** (1 file):
3. AttendanceRepository.kt

**ViewModel** (1 file):
4. AttendanceViewModel.kt

**UI Screens** (3 files):
5. AttendanceScreen.kt
6. AddSubjectForm.kt
7. AttendanceDetailScreen.kt

**UI Components** (1 file):
8. SubjectCard.kt

**Tests** (7 files):
9. AttendanceViewModelTest.kt
10. AttendanceRepositoryTest.kt
11. AttendanceValidationPropertyTests.kt
12. AttendanceCalculationPropertyTests.kt
13. AttendanceSubjectModelTest.kt
14. AttendanceScreenTest.kt (and other UI tests)
15. AttendanceFlowIntegrationTest.kt (and other integration tests)

### Files to Modify (4 existing files)

1. **Routes.kt**: Add ATTENDANCE, ADD_SUBJECT, ATTENDANCE_DETAIL route constants
2. **MainNavGraph.kt**: Add AttendanceScreen, AddSubjectForm, AttendanceDetailScreen routes
3. **DashboardScreen.kt**: Add Attendance quick action card
4. **strings.xml**: Add attendance-related strings

### Files to Delete

None (no placeholder files to replace)


## Implementation Order and Workflow

### Phase 1: Data Models (Build remains functional)

**Step 1: Create Data Models**
- Create AttendanceSubject.kt (data class with Firestore conversion methods)
- Create AttendanceUiState.kt (sealed class for UI states)
- Build and verify

### Phase 2: Repository Layer (Build remains functional)

**Step 2: Create AttendanceRepository**
- Create AttendanceRepository.kt with method signatures
- Implement fetchSubjects method (Firestore query)
- Implement observeSubjects method (Firestore real-time listener)
- Implement addSubject method (Firestore create with duplicate check)
- Implement updateSubject method (Firestore update)
- Implement deleteSubject method (Firestore delete)
- Implement error mapping
- Build and verify

### Phase 3: ViewModel Layer (Build remains functional)

**Step 3: Create AttendanceViewModel**
- Create AttendanceViewModel.kt with StateFlow properties
- Implement loadSubjects method
- Implement addSubject method (with validation)
- Implement updateAttendance method (with validation)
- Implement deleteSubject method
- Implement calculatePercentage method
- Implement calculateClassesNeeded method
- Implement real-time listener integration
- Build and verify

### Phase 4: UI Components (Build remains functional)

**Step 4: Create SubjectCard Component**
- Create SubjectCard.kt (composable for subject display with color coding)
- Implement color-coded background based on percentage
- Implement warning badge display logic
- Implement delete button
- Build and verify

### Phase 5: UI Screens (Build remains functional)

**Step 5: Create AddSubjectForm**
- Create AddSubjectForm.kt
- Implement subject name input field
- Implement validation display
- Implement loading state
- Reuse CustomTextField, CustomButton, LoadingIndicator from Phase 1
- Build and verify

**Step 6: Create AttendanceDetailScreen**
- Create AttendanceDetailScreen.kt
- Implement current stats display
- Implement input fields for total and attended classes
- Implement real-time percentage calculation
- Implement real-time classes needed calculation
- Implement warning card display logic
- Implement update button
- Implement delete button in top bar
- Reuse CustomTextField, CustomButton from Phase 1
- Build and verify

**Step 7: Create AttendanceScreen**
- Create AttendanceScreen.kt
- Implement subjects list display with LazyColumn
- Implement empty state using EmptyState from Phase 2
- Implement loading state using LoadingIndicator from Phase 1
- Implement error state
- Implement FAB for add subject navigation
- Integrate bottom navigation from Phase 1
- Build and verify

### Phase 6: Navigation Integration (Build remains functional)

**Step 8: Update Navigation**
- Modify Routes.kt (add ATTENDANCE, ADD_SUBJECT, ATTENDANCE_DETAIL constants)
- Modify MainNavGraph.kt:
  - Add AttendanceScreen route
  - Add AddSubjectForm route
  - Add AttendanceDetailScreen route with subject ID parameter
  - Wire up navigation between screens
- Build and verify
- Test navigation flow

**Step 9: Update Dashboard**
- Modify DashboardScreen.kt:
  - Add Attendance quick action card using QuickActionCard from Phase 1
  - Wire up navigation to AttendanceScreen
- Build and verify
- Test dashboard navigation

### Phase 7: Strings and Resources (Build remains functional)

**Step 10: Add Strings**
- Modify strings.xml (add attendance-related strings)
- Build and verify

### Phase 8: Testing (Build remains functional)

**Step 11: Unit Tests**
- Create AttendanceSubjectModelTest.kt (test Firestore conversion)
- Create AttendanceRepositoryTest.kt (test with mocked Firestore)
- Create AttendanceViewModelTest.kt (test state management and calculations)
- Create AttendanceValidationPropertyTests.kt (property-based validation tests)
- Create AttendanceCalculationPropertyTests.kt (property-based calculation tests)
- Run tests and verify

**Step 12: UI Tests**
- Create AttendanceScreenTest.kt (test UI rendering and interaction)
- Create AddSubjectFormTest.kt (test form validation and submission)
- Create AttendanceDetailScreenTest.kt (test real-time calculations and updates)
- Create SubjectCardTest.kt (test card rendering and color coding)
- Run tests and verify

**Step 13: Integration Tests**
- Create AttendanceFlowIntegrationTest.kt (test end-to-end flow)
- Create RealtimeUpdateTest.kt (test real-time listener integration)
- Run tests and verify

### Phase 9: Polish and Refinement

**Step 14: Final Testing**
- Test add subject flow with various names
- Test update attendance with various values
- Test delete subject with confirmation
- Test real-time updates
- Test percentage and classes needed calculations
- Test color coding and warning badges
- Test error scenarios (network errors, validation errors, etc.)
- Test on different screen sizes
- Test light/dark mode
- Verify all acceptance criteria
- Final build and deployment preparation

### Build Verification Strategy

After each step:
1. Sync Gradle (no changes needed - all dependencies from Phase 1)
2. Build project (Build → Make Project)
3. Verify no compilation errors
4. Run app on emulator/device if UI changes were made
5. Test new functionality incrementally
6. Commit changes to version control

This incremental approach ensures the project remains buildable at every step, allowing for testing and validation throughout development.

### Development Best Practices

**Naming Conventions** (consistent with Phase 1 and Phase 2):
- Composables: PascalCase (e.g., AttendanceScreen, SubjectCard)
- Functions: camelCase (e.g., calculatePercentage, addSubject)
- Constants: UPPER_SNAKE_CASE (e.g., WARNING_THRESHOLD)
- Packages: lowercase (e.g., ui, viewmodel, repository)

**Code Organization**:
- One screen per file
- Group related composables in the same file
- Keep composables small and focused
- Extract reusable logic to separate functions
- Use preview annotations for UI development

**State Management** (consistent with Phase 1 and Phase 2):
- ViewModels own the state
- UI observes state using collectAsState()
- State updates trigger recomposition
- Use remember for UI-only state
- Use StateFlow for shared state

**Error Handling** (consistent with Phase 1 and Phase 2):
- Always handle Firestore exceptions
- Provide user-friendly error messages
- Log errors for debugging
- Never expose technical details to users

**Testing**:
- Write tests alongside implementation
- Test ViewModels with mocked repositories
- Test repositories with mocked Firestore
- Test UI components in isolation
- Use property-based tests for calculation and validation logic
- Test real-time updates with Firestore emulator

### Dependency Notes

**No New Dependencies Required**:
All required dependencies were added in Phase 1:
- Firestore (already included)
- Compose Navigation (already included)
- Kotest for property-based testing (already included)
- MockK for mocking (already included)

### Integration with Phase 1 and Phase 2

**Reused Components**:
- CustomTextField: Used in AddSubjectForm and AttendanceDetailScreen for input fields
- CustomButton: Used in AddSubjectForm and AttendanceDetailScreen for action buttons
- LoadingIndicator: Used in AttendanceScreen for loading state
- ErrorMessage: Used throughout for error display (snackbars)
- EmptyState: Used in AttendanceScreen for empty state display
- QuickActionCard: Used in DashboardScreen for Attendance card
- CampusConnectTheme: Applied to all new screens
- Bottom Navigation: Integrated into AttendanceScreen

**Reused Patterns**:
- MVVM architecture: AttendanceViewModel follows same pattern as AuthViewModel and NotesViewModel
- StateFlow for state management: Same pattern as Phase 1 and Phase 2
- Result type for repository operations: Same pattern as AuthRepository and NotesRepository
- Navigation structure: Integrated into existing MainNavGraph
- Error handling: Same snackbar-based approach as Phase 1 and Phase 2
- Material 3 styling: Consistent rounded corners, colors, typography
- Firestore subcollection structure: Same pattern as Phase 2 Notes Module
- Real-time listeners: Same pattern as Phase 2 Notes Module

**Modified Components**:
- MainNavGraph: Updated to include AttendanceScreen, AddSubjectForm, AttendanceDetailScreen
- Routes: Extended with new route constants
- DashboardScreen: Added Attendance quick action card
- Bottom Navigation: Attendance item now navigates to functional AttendanceScreen

**New Patterns Introduced**:
- Color-coded cards based on data values (green/red for attendance percentage)
- Warning badges on cards for low values
- Real-time calculation display without save (percentage and classes needed update as user types)
- Actionable guidance messages (classes needed to reach threshold)

---

This design document provides a complete blueprint for implementing the Campus Connect Attendance Tracker Module (Phase 3). The architecture seamlessly integrates with Phase 1 and Phase 2, reuses existing components for consistency, and maintains the established MVVM patterns and Material 3 design system. The implementation order ensures the app remains buildable at every step, supporting incremental development and testing.

