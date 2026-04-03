# Design Document: Campus Connect Notes Module (Phase 2)

## Overview

The Notes Module is Phase 2 of Campus Connect, building upon the authentication system and MVVM architecture established in Phase 1. This module enables students to upload PDF notes with metadata (title, subject, semester, description), view notes in a searchable and filterable list, download notes for offline access, share notes with classmates, and delete notes they no longer need.

The module integrates Firebase Storage for PDF file storage and Firestore for note metadata management. It follows the existing MVVM architecture, reuses Phase 1 components (CustomTextField, CustomButton, LoadingIndicator, ErrorMessage), and maintains consistency with the Material 3 design system.

Key architectural decisions:
- **Firebase Storage Integration**: PDF files stored in user-specific paths with organized structure
- **Firestore Real-time Updates**: Notes list updates automatically when notes are added or deleted
- **Search and Filter**: Client-side filtering for responsive search and multi-filter support
- **Component Reuse**: Leverages Phase 1 components for consistency and reduced code duplication
- **Permission Handling**: Runtime permission requests for file access following Android best practices
- **Error Recovery**: Comprehensive error handling with rollback mechanisms for failed operations

## Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        UI Layer                              │
│  (Jetpack Compose Screens + Components)                     │
│                                                              │
│  • NotesScreen (list, search, filter)                       │
│  • UploadFormScreen (file selection, metadata input)        │
│  • NoteCard (reusable card component)                       │
│  • EmptyState (no notes, no results)                        │
│  • Reused: CustomTextField, CustomButton, LoadingIndicator  │
└──────────────────┬──────────────────────────────────────────┘
                   │ observes StateFlow
                   │ calls ViewModel methods
┌──────────────────▼──────────────────────────────────────────┐
│                    ViewModel Layer                           │
│         (Business Logic + State Management)                  │
│                                                              │
│  • NotesViewModel (manages notes state, search, filters)    │
│  • Exposes StateFlow<NotesUiState> to UI                    │
│  • Handles validation, search logic, filter logic           │
└──────────────────┬──────────────────────────────────────────┘
                   │ calls repository methods
                   │ transforms data to UI state
┌──────────────────▼──────────────────────────────────────────┐
│                   Repository Layer                           │
│              (Data Access Abstraction)                       │
│                                                              │
│  • NotesRepository (Firebase Storage + Firestore ops)       │
│  • Handles file upload/download                             │
│  • Manages metadata CRUD operations                         │
│  • Provides real-time listeners                             │
└──────────────────┬──────────────────────────────────────────┘
                   │ uses Firebase SDK
┌──────────────────▼──────────────────────────────────────────┐
│                     Data Layer                               │
│              (Firebase Integration)                          │
│                                                              │
│  • Firebase Storage (PDF file storage)                      │
│  • Firestore (note metadata storage)                        │
│  • Android Storage (downloads)                              │
└─────────────────────────────────────────────────────────────┘
```


### MVVM Pattern Implementation for Notes Module

**Model Layer**:
- Note data class with fields: id, title, subject, semester, description, fileName, fileUrl, uploadDate, fileSize, userId
- NotesUiState sealed class representing UI states (Loading, Success, Error, Empty)
- FilterState data class for managing active filters
- Located in `model` package alongside Phase 1 models

**View Layer**:
- NotesScreen: Main screen displaying notes list with search bar and filter chips
- UploadFormScreen: Form for selecting PDF and entering metadata
- NoteCard: Composable displaying individual note with actions (view, download, share, delete)
- EmptyState: Composable for empty states (no notes, no search results)
- Observes NotesViewModel state using `collectAsState()`
- Calls ViewModel methods for user actions
- Reuses Phase 1 components for consistency

**ViewModel Layer**:
- NotesViewModel extends ViewModel
- Manages NotesUiState using StateFlow
- Provides methods: loadNotes, uploadNote, deleteNote, downloadNote, searchNotes, filterBySubject, filterBySemester, clearFilters
- Validates upload form inputs before repository calls
- Transforms repository results into UI state
- Handles real-time Firestore updates

**Repository Layer**:
- NotesRepository abstracts Firebase operations
- Methods: uploadNote, fetchNotes, deleteNote, downloadNote, observeNotes
- Handles Firebase Storage operations (upload, download, delete)
- Handles Firestore operations (create, read, delete, real-time listeners)
- Maps Firebase exceptions to domain errors
- Implements rollback logic for failed operations

### Navigation Integration

The Notes Module integrates into the existing navigation structure:

**Updated Main Graph**:
- Dashboard (existing)
- NotesScreen (replaces NotesPlaceholderScreen)
- UploadFormScreen (new route)
- Profile (existing)

**Navigation Flow**:
1. User taps Notes in bottom navigation → Navigate to NotesScreen
2. User taps FAB on NotesScreen → Navigate to UploadFormScreen
3. User completes upload → Navigate back to NotesScreen
4. Bottom navigation remains visible on NotesScreen
5. Bottom navigation hidden on UploadFormScreen (full-screen form)

**Route Definitions** (added to Routes.kt):
```kotlin
const val NOTES = "notes"  // Replaces notes placeholder
const val UPLOAD_NOTE = "upload_note"
```


## Components and Interfaces

### Package Structure

```
com.example.campusconnect/
├── ui/
│   ├── main/
│   │   ├── NotesScreen.kt                 # NEW (replaces NotesPlaceholderScreen.kt)
│   │   └── UploadFormScreen.kt            # NEW
│   └── components/
│       ├── NoteCard.kt                    # NEW
│       ├── EmptyState.kt                  # NEW
│       ├── CustomTextField.kt             # EXISTING (reused)
│       ├── CustomButton.kt                # EXISTING (reused)
│       ├── LoadingIndicator.kt            # EXISTING (reused)
│       └── ErrorMessage.kt                # EXISTING (reused)
│
├── navigation/
│   ├── Routes.kt                          # MODIFY (add UPLOAD_NOTE route)
│   └── MainNavGraph.kt                    # MODIFY (replace placeholder, add upload route)
│
├── viewmodel/
│   └── NotesViewModel.kt                  # NEW
│
├── repository/
│   └── NotesRepository.kt                 # NEW
│
└── model/
    ├── Note.kt                            # NEW
    ├── NotesUiState.kt                    # NEW
    └── FilterState.kt                     # NEW
```

### Key Components

#### 1. NotesViewModel

```kotlin
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
    
    // Methods
    fun loadNotes()
    fun refreshNotes()
    fun uploadNote(uri: Uri, title: String, subject: String, semester: String, description: String)
    fun deleteNote(noteId: String)
    fun downloadNote(note: Note)
    fun searchNotes(query: String)
    fun filterBySubject(subjects: List<String>)
    fun filterBySemester(semesters: List<String>)
    fun clearFilters()
    
    // Private helper methods
    private fun validateUploadInput(title: String, subject: String, semester: String): ValidationResult
    private fun applyFilters(notes: List<Note>): List<Note>
    private fun observeNotesRealtime()
}
```

#### 2. NotesRepository

```kotlin
class NotesRepository(
    private val firebaseStorage: FirebaseStorage,
    private val firestore: FirebaseFirestore
) {
    
    suspend fun uploadNote(
        userId: String,
        fileUri: Uri,
        fileName: String,
        metadata: NoteMetadata
    ): Result<Note>
    
    suspend fun fetchNotes(userId: String): Result<List<Note>>
    
    fun observeNotes(userId: String): Flow<List<Note>>
    
    suspend fun deleteNote(userId: String, noteId: String, fileUrl: String): Result<Unit>
    
    suspend fun downloadNote(note: Note, destinationUri: Uri): Result<Unit>
    
    // Private helper methods
    private fun getStoragePath(userId: String, fileName: String): String
    private fun getFirestorePath(userId: String): CollectionReference
    private suspend fun deleteFileFromStorage(fileUrl: String): Result<Unit>
    private suspend fun deleteMetadataFromFirestore(userId: String, noteId: String): Result<Unit>
}
```


#### 3. NotesScreen Composable

```kotlin
@Composable
fun NotesScreen(
    viewModel: NotesViewModel,
    onNavigateToUpload: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    val uiState by viewModel.notesUiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    
    Scaffold(
        topBar = { NotesTopBar(searchQuery, onSearchChange = viewModel::searchNotes) },
        floatingActionButton = { UploadFAB(onClick = onNavigateToUpload) },
        bottomBar = { BottomNavigationBar(selected = "notes", ...) }
    ) { padding ->
        when (uiState) {
            is NotesUiState.Loading -> LoadingIndicator()
            is NotesUiState.Success -> NotesListContent(notes, filterState, viewModel)
            is NotesUiState.Empty -> EmptyState(message, onUploadClick = onNavigateToUpload)
            is NotesUiState.Error -> ErrorState(message, onRetry = viewModel::loadNotes)
        }
    }
}
```

#### 4. UploadFormScreen Composable

```kotlin
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
    
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> selectedFileUri = uri }
    
    Scaffold(
        topBar = { UploadTopBar(onBackClick = onNavigateBack) }
    ) { padding ->
        Column {
            FilePickerButton(onClick = { filePicker.launch("application/pdf") })
            if (selectedFileUri != null) {
                SelectedFileDisplay(fileName)
            }
            CustomTextField(value = title, label = "Title", ...)
            CustomTextField(value = subject, label = "Subject", ...)
            SemesterDropdown(selected = semester, onSelect = { semester = it })
            CustomTextField(value = description, label = "Description", multiline = true, ...)
            CustomButton(
                text = "Upload",
                onClick = {
                    viewModel.uploadNote(selectedFileUri!!, title, subject, semester, description)
                }
            )
        }
    }
}
```

#### 5. NoteCard Component

```kotlin
@Composable
fun NoteCard(
    note: Note,
    onView: () -> Unit,
    onDownload: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        shape = RoundedCornerShape(12.dp)  // Consistent with Phase 1
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = note.title, style = MaterialTheme.typography.titleMedium)
            Text(text = "${note.subject} • ${note.semester}", style = MaterialTheme.typography.bodySmall)
            Text(text = note.uploadDate.format(), style = MaterialTheme.typography.bodySmall)
            
            Row(horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onView) { Icon(Icons.Default.Visibility, "View") }
                IconButton(onClick = onDownload) { Icon(Icons.Default.Download, "Download") }
                IconButton(onClick = onShare) { Icon(Icons.Default.Share, "Share") }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete") }
            }
        }
    }
}
```

#### 6. EmptyState Component

```kotlin
@Composable
fun EmptyState(
    message: String,
    icon: ImageVector = Icons.Default.Description,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            CustomButton(text = actionText, onClick = onActionClick)
        }
    }
}
```


### Firebase Storage Structure

The Notes Module uses a hierarchical path structure in Firebase Storage:

```
users/
  {userId}/
    notes/
      {timestamp}_{fileName}.pdf
      {timestamp}_{fileName}.pdf
      ...
```

**Path Format**: `users/{userId}/notes/{timestamp}_{fileName}`

**Rationale**:
- User-specific folders isolate notes by user
- Timestamp prefix ensures unique file names and chronological ordering
- Original file name preserved for user recognition
- Flat structure within user's notes folder for simple queries

**Example Paths**:
- `users/abc123/notes/1704067200000_calculus_notes.pdf`
- `users/abc123/notes/1704153600000_physics_chapter3.pdf`

### Firestore Collection Structure

The Notes Module uses a subcollection structure in Firestore:

```
users/
  {userId}/
    notes/
      {noteId}/
        - id: string
        - title: string
        - subject: string
        - semester: string
        - description: string
        - fileName: string
        - fileUrl: string
        - uploadDate: timestamp
        - fileSize: number
        - userId: string
```

**Collection Path**: `users/{userId}/notes`

**Document Structure**:
```json
{
  "id": "note123",
  "title": "Calculus Chapter 5 Notes",
  "subject": "Mathematics",
  "semester": "Semester 3",
  "description": "Derivatives and integrals",
  "fileName": "1704067200000_calculus_notes.pdf",
  "fileUrl": "https://firebasestorage.googleapis.com/...",
  "uploadDate": "2024-01-01T00:00:00Z",
  "fileSize": 2048576,
  "userId": "abc123"
}
```

**Rationale**:
- Subcollection structure provides automatic user isolation
- Document ID auto-generated by Firestore for uniqueness
- fileUrl stored for direct access without additional Storage queries
- uploadDate as timestamp for sorting and filtering
- fileSize stored for display and validation

### Firestore Queries

**Fetch All Notes for User**:
```kotlin
firestore.collection("users")
    .document(userId)
    .collection("notes")
    .orderBy("uploadDate", Query.Direction.DESCENDING)
    .get()
```

**Real-time Listener**:
```kotlin
firestore.collection("users")
    .document(userId)
    .collection("notes")
    .orderBy("uploadDate", Query.Direction.DESCENDING)
    .addSnapshotListener { snapshot, error ->
        // Handle updates
    }
```

**Delete Note**:
```kotlin
firestore.collection("users")
    .document(userId)
    .collection("notes")
    .document(noteId)
    .delete()
```

**Note**: Search and filtering are performed client-side after fetching all notes. This approach is chosen because:
1. Firestore queries don't support case-insensitive text search
2. Multi-field filtering (subject + semester) requires composite indexes
3. Client-side filtering provides instant results without network latency
4. Expected dataset size per user is manageable (< 1000 notes)


### File Upload Flow

The file upload process follows these steps:

1. **User Selects File**:
   - User taps "Select PDF" button on UploadFormScreen
   - Android file picker launches with PDF filter
   - User selects PDF file
   - File URI returned to app

2. **User Enters Metadata**:
   - User fills in title, subject, semester, description
   - Form validates inputs on submission

3. **Validation**:
   - ViewModel validates required fields (title, subject, semester)
   - ViewModel checks file type is PDF
   - If validation fails, display field-specific errors

4. **Upload to Firebase Storage**:
   - Repository creates storage reference: `users/{userId}/notes/{timestamp}_{fileName}`
   - Repository uploads file using `putFile(uri)`
   - Progress updates displayed in UI
   - On success, retrieve download URL

5. **Save Metadata to Firestore**:
   - Repository creates note document in `users/{userId}/notes`
   - Document includes all metadata + fileUrl
   - On success, navigate back to NotesScreen

6. **Error Handling**:
   - If Storage upload fails, display error and stop
   - If Firestore save fails, delete uploaded file from Storage (rollback)
   - Display user-friendly error message

**Upload Progress**:
```kotlin
storageRef.putFile(uri)
    .addOnProgressListener { taskSnapshot ->
        val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
        _uploadProgress.value = progress
    }
```

### File Download Flow

The file download process follows these steps:

1. **User Taps Download**:
   - User taps download icon on NoteCard
   - ViewModel calls repository.downloadNote(note)

2. **Permission Check**:
   - Repository checks for WRITE_EXTERNAL_STORAGE permission (Android < 10)
   - If not granted, request permission
   - If denied, display error message

3. **Download File**:
   - Repository creates destination file in Downloads folder
   - Repository downloads file from Storage using fileUrl
   - Progress updates displayed on NoteCard

4. **Save to Device**:
   - File saved to: `Downloads/{fileName}`
   - MediaStore updated for file visibility

5. **Success Feedback**:
   - Display snackbar: "Note downloaded to Downloads folder"
   - NoteCard returns to normal state

**Download Implementation**:
```kotlin
val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), note.fileName)
storageRef.getFile(file)
    .addOnProgressListener { taskSnapshot ->
        val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
        _downloadProgress.value = progress
    }
    .addOnSuccessListener {
        // Update MediaStore
        // Show success message
    }
```

### File Share Flow

The file share process uses Android's share system:

1. **User Taps Share**:
   - User taps share icon on NoteCard
   - ViewModel prepares file for sharing

2. **Create Shareable URI**:
   - Download file to cache directory if not already cached
   - Create content URI using FileProvider

3. **Launch Share Sheet**:
   - Create share intent with PDF MIME type
   - Add file URI with read permission
   - Launch Android share sheet

4. **User Selects App**:
   - User chooses app from share sheet (email, messaging, cloud storage, etc.)
   - Selected app receives file

**Share Implementation**:
```kotlin
val shareIntent = Intent(Intent.ACTION_SEND).apply {
    type = "application/pdf"
    putExtra(Intent.EXTRA_STREAM, fileUri)
    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
}
context.startActivity(Intent.createChooser(shareIntent, "Share Note"))
```


### Search and Filter Implementation

**Search Implementation**:
- Search is performed client-side on the fetched notes list
- Search query filters notes by title (case-insensitive)
- Search updates in real-time as user types

```kotlin
fun searchNotes(query: String) {
    _searchQuery.value = query
    val filteredNotes = allNotes.filter { note ->
        note.title.contains(query, ignoreCase = true)
    }
    applyFilters(filteredNotes)
}
```

**Filter Implementation**:
- Filters are applied client-side after search
- Multiple subjects can be selected simultaneously (OR logic within subject)
- Multiple semesters can be selected simultaneously (OR logic within semester)
- Subject and semester filters combined with AND logic
- Empty filter list means no filtering for that dimension

```kotlin
fun applyFilters(notes: List<Note>): List<Note> {
    var filtered = notes
    
    // Apply search query
    if (searchQuery.value.isNotEmpty()) {
        filtered = filtered.filter { it.title.contains(searchQuery.value, ignoreCase = true) }
    }
    
    // Apply subject filter
    if (filterState.value.selectedSubjects.isNotEmpty()) {
        filtered = filtered.filter { it.subject in filterState.value.selectedSubjects }
    }
    
    // Apply semester filter
    if (filterState.value.selectedSemesters.isNotEmpty()) {
        filtered = filtered.filter { it.semester in filterState.value.selectedSemesters }
    }
    
    return filtered
}
```

**Filter UI**:
- Subject filter: Horizontal scrollable row of filter chips
- Semester filter: Horizontal scrollable row of filter chips
- Selected chips highlighted with primary color
- "Clear All" button to reset all filters

### Permission Handling

**Required Permissions**:

For Android 10+ (API 29+):
- No permissions required for downloads (scoped storage)
- READ_EXTERNAL_STORAGE for file picker (granted automatically)

For Android 9 and below (API 28-):
- READ_EXTERNAL_STORAGE for file picker
- WRITE_EXTERNAL_STORAGE for downloads

**Permission Request Flow**:

1. **File Selection**:
   - Check if READ_EXTERNAL_STORAGE is granted
   - If not granted and API < 29, request permission
   - If denied, show error message with settings link

2. **File Download**:
   - Check if WRITE_EXTERNAL_STORAGE is granted
   - If not granted and API < 29, request permission
   - If denied, show error message with settings link

**Implementation**:
```kotlin
val permissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
) { isGranted ->
    if (isGranted) {
        // Proceed with operation
    } else {
        // Show error message
    }
}

fun checkAndRequestPermission(permission: String, onGranted: () -> Unit) {
    when {
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
            onGranted()
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            // No permission needed on Android 10+
            onGranted()
        }
        else -> {
            permissionLauncher.launch(permission)
        }
    }
}
```

**AndroidManifest.xml Updates**:
```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="28" />
<uses-permission android:name="android.permission.INTERNET" />
```


## Data Models

### Note Model

```kotlin
data class Note(
    val id: String = "",
    val title: String = "",
    val subject: String = "",
    val semester: String = "",
    val description: String = "",
    val fileName: String = "",
    val fileUrl: String = "",
    val uploadDate: Long = 0L,  // Timestamp in milliseconds
    val fileSize: Long = 0L,    // Size in bytes
    val userId: String = ""
) {
    // Convert to Firestore document
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "title" to title,
        "subject" to subject,
        "semester" to semester,
        "description" to description,
        "fileName" to fileName,
        "fileUrl" to fileUrl,
        "uploadDate" to uploadDate,
        "fileSize" to fileSize,
        "userId" to userId
    )
    
    companion object {
        // Convert from Firestore document
        fun fromMap(map: Map<String, Any>): Note = Note(
            id = map["id"] as? String ?: "",
            title = map["title"] as? String ?: "",
            subject = map["subject"] as? String ?: "",
            semester = map["semester"] as? String ?: "",
            description = map["description"] as? String ?: "",
            fileName = map["fileName"] as? String ?: "",
            fileUrl = map["fileUrl"] as? String ?: "",
            uploadDate = map["uploadDate"] as? Long ?: 0L,
            fileSize = map["fileSize"] as? Long ?: 0L,
            userId = map["userId"] as? String ?: ""
        )
    }
    
    // Helper methods
    fun getFormattedDate(): String {
        val date = Date(uploadDate)
        val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return format.format(date)
    }
    
    fun getFormattedSize(): String {
        return when {
            fileSize < 1024 -> "$fileSize B"
            fileSize < 1024 * 1024 -> "${fileSize / 1024} KB"
            else -> "${fileSize / (1024 * 1024)} MB"
        }
    }
}
```

### NotesUiState Model

```kotlin
sealed class NotesUiState {
    object Loading : NotesUiState()
    data class Success(val notes: List<Note>) : NotesUiState()
    data class Empty(val message: String) : NotesUiState()
    data class Error(val message: String) : NotesUiState()
}
```

### FilterState Model

```kotlin
data class FilterState(
    val selectedSubjects: List<String> = emptyList(),
    val selectedSemesters: List<String> = emptyList()
) {
    fun isActive(): Boolean = selectedSubjects.isNotEmpty() || selectedSemesters.isNotEmpty()
    
    fun clear(): FilterState = FilterState()
}
```

### UploadState Model

```kotlin
sealed class UploadState {
    object Idle : UploadState()
    data class Uploading(val progress: Int) : UploadState()
    object Success : UploadState()
    data class Error(val message: String) : UploadState()
}
```

### DownloadState Model

```kotlin
sealed class DownloadState {
    object Idle : DownloadState()
    data class Downloading(val progress: Int, val noteId: String) : DownloadState()
    object Success : DownloadState()
    data class Error(val message: String) : DownloadState()
}
```

### NoteMetadata Model

```kotlin
data class NoteMetadata(
    val title: String,
    val subject: String,
    val semester: String,
    val description: String
)
```

### Semester Constants

```kotlin
object SemesterOptions {
    val semesters = listOf(
        "Semester 1",
        "Semester 2",
        "Semester 3",
        "Semester 4",
        "Semester 5",
        "Semester 6",
        "Semester 7",
        "Semester 8"
    )
}
```


## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property 1: Notes Fetch on Screen Load

*For any* screen load of NotesScreen, the ViewModel should trigger a fetch operation to retrieve notes from Firestore.

**Validates: Requirements 1.2**

### Property 2: Note Card Rendering

*For any* list of notes, the UI should render the correct number of NoteCards, each displaying title, subject, semester, and upload date.

**Validates: Requirements 1.3, 1.4**

### Property 3: Refresh Triggers Refetch

*For any* pull-to-refresh action, the ViewModel should trigger a refetch operation from Firestore and update the displayed notes when complete.

**Validates: Requirements 2.2, 2.4**

### Property 4: Selected File Display

*For any* PDF file selected through the file picker, the Upload Form should display the selected file name.

**Validates: Requirements 3.4**

### Property 5: Upload Form Validation

*For any* upload form submission where required fields (title, subject, semester) are missing or empty, validation should fail and display field-specific error messages.

**Validates: Requirements 3.7, 3.8**

### Property 6: File Upload to Storage

*For any* valid upload request with complete metadata, the repository should upload the PDF file to Firebase Storage using the path structure `users/{userId}/notes/{timestamp}_{fileName}` and generate a download URL.

**Validates: Requirements 4.1, 4.2, 4.4**

### Property 7: Upload Progress Display

*For any* file upload in progress, the Upload Form should display a progress indicator showing the upload percentage.

**Validates: Requirements 4.3**

### Property 8: Successful Upload Navigation

*For any* successful file upload and metadata save, the navigation controller should navigate back to the NotesScreen.

**Validates: Requirements 4.8**

### Property 9: Metadata Save After Upload

*For any* successful file upload to Firebase Storage, the repository should save note metadata to Firestore at path `users/{userId}/notes/{noteId}` with all required fields (title, subject, semester, description, fileName, fileUrl, uploadDate, fileSize).

**Validates: Requirements 5.1, 5.2, 5.3**

### Property 10: Rollback on Metadata Save Failure

*For any* failed metadata save to Firestore after successful file upload, the repository should delete the uploaded file from Firebase Storage to maintain consistency.

**Validates: Requirements 5.4**

### Property 11: Real-time Updates

*For any* note added or deleted in Firestore, the NotesScreen should automatically update the displayed notes list without manual refresh, and filters should be reapplied.

**Validates: Requirements 5.5, 26.2, 26.3, 26.4**

### Property 12: Search Filtering

*For any* search query entered in the search bar, the ViewModel should filter notes by title in a case-insensitive manner, and clearing the search should restore the full list.

**Validates: Requirements 6.2, 6.3, 6.5**

### Property 13: Subject Filtering

*For any* subject filter selection (single or multiple), the ViewModel should display only notes matching the selected subjects.

**Validates: Requirements 7.2, 7.3**

### Property 14: Semester Filtering

*For any* semester filter selection (single or multiple), the ViewModel should display only notes matching the selected semesters.

**Validates: Requirements 8.2, 8.3**

### Property 15: Combined Search and Filter

*For any* combination of active search query and filters (subject and/or semester), the ViewModel should apply both conditions using AND logic and display the count of filtered results.

**Validates: Requirements 9.1, 9.2**

### Property 16: PDF Viewer Intent

*For any* note card tap, the ViewModel should retrieve the file URL and create an Android Intent to open the PDF in an external viewer app.

**Validates: Requirements 10.1, 10.2**

### Property 17: File Download to Downloads Folder

*For any* download button tap, the repository should download the PDF file to the device Downloads folder and display a success snackbar with the file location when complete.

**Validates: Requirements 11.2, 11.4**

### Property 18: Download Progress Display

*For any* file download in progress, the NoteCard should display a download progress indicator.

**Validates: Requirements 11.3, 19.3**

### Property 19: Share Sheet Launch

*For any* share button tap, the Notes Module should open the Android share sheet with the PDF file.

**Validates: Requirements 12.2**

### Property 20: Delete Confirmation

*For any* delete button tap, the NotesScreen should display a confirmation dialog before proceeding with deletion.

**Validates: Requirements 13.2**

### Property 21: Note Deletion

*For any* confirmed deletion, the repository should delete the note metadata from Firestore, then delete the PDF file from Firebase Storage, and display a success snackbar when complete.

**Validates: Requirements 13.4, 13.5, 13.6**

### Property 22: Deletion Failure Handling

*For any* failed deletion operation, the NotesScreen should display an error message and keep the note in the list.

**Validates: Requirements 13.7**

### Property 23: Firebase Exception Mapping

*For any* Firebase exception encountered in the repository, a user-friendly error message should be returned instead of technical details.

**Validates: Requirements 14.5**

### Property 24: Validation Before Repository Calls

*For any* upload operation, the ViewModel should validate form inputs before calling repository methods.

**Validates: Requirements 15.4**

### Property 25: Repository Result Transformation

*For any* repository result (success or error), the ViewModel should transform it into the appropriate UI state.

**Validates: Requirements 15.5**

### Property 26: Real-time Listener Updates

*For any* Firestore change detected by the real-time listener, the ViewModel should update the notes list automatically.

**Validates: Requirements 15.6**

### Property 27: Note Model Round-trip

*For any* Note object, converting it to Firestore document format and back should preserve all field values.

**Validates: Requirements 16.4**

### Property 28: File Selection Permission Check

*For any* file selection attempt, the Notes Module should check for storage read permission and request it if not granted.

**Validates: Requirements 21.1, 21.2**

### Property 29: File Download Permission Check

*For any* file download attempt, the Notes Module should check for storage write permission and request it if not granted.

**Validates: Requirements 21.4, 21.5**

### Property 30: Navigation Between Notes and Upload

*For any* navigation from NotesScreen to UploadFormScreen and back, the navigation controller should maintain proper back stack behavior.

**Validates: Requirements 22.6**

### Property 31: Theme Mode Support

*For any* device theme change (light to dark or dark to light), the NotesScreen should apply the appropriate theme from the Phase 1 theme system.

**Validates: Requirements 23.5**

### Property 32: Operation Failure Snackbar

*For any* failed operation (upload, download, delete), the NotesScreen should display an error snackbar with the error message that auto-dismisses after 4 seconds.

**Validates: Requirements 24.4, 24.5**

### Property 33: File Type Validation

*For any* file selected through the file picker, the Notes Module should check the file extension, and if it's not a PDF, display an error message; if it is a PDF, allow the user to proceed.

**Validates: Requirements 27.1, 27.2, 27.3**

### Property 34: Loading State UI Disabling

*For any* operation in progress (upload, download, delete), the NotesScreen should disable relevant UI elements to prevent duplicate operations, and re-enable them when the operation completes.

**Validates: Requirements 19.4, 19.5**

### Property 35: Skeleton to Content Transition

*For any* initial notes fetch completion, the skeleton loading cards should be replaced with actual NoteCards.

**Validates: Requirements 28.4**


## Error Handling

### Error Handling Strategy

The Notes Module implements a comprehensive error handling approach consistent with Phase 1:

**Repository Layer**:
- Catches Firebase Storage exceptions (upload, download, delete failures)
- Catches Firestore exceptions (metadata CRUD failures)
- Catches Android system exceptions (file access, permissions)
- Maps exceptions to domain-specific error messages
- Returns `Result<T>` type (Success or Failure)
- Implements rollback logic for failed operations

**Exception Mappings**:

Firebase Storage Errors:
- `StorageException.ERROR_QUOTA_EXCEEDED` → "Storage quota exceeded. Please free up space or contact support"
- `StorageException.ERROR_OBJECT_NOT_FOUND` → "File not found. It may have been deleted"
- `StorageException.ERROR_NOT_AUTHENTICATED` → "Authentication error. Please log in again"
- `StorageException.ERROR_RETRY_LIMIT_EXCEEDED` → "Network error. Please check your connection and try again"
- Network errors → "Network error. Please check your connection and try again"

Firestore Errors:
- `FirebaseFirestoreException.PERMISSION_DENIED` → "Permission denied. Please log in again"
- `FirebaseFirestoreException.UNAVAILABLE` → "Service unavailable. Please try again later"
- `FirebaseFirestoreException.NOT_FOUND` → "Note not found. It may have been deleted"
- Network errors → "Network error. Please check your connection and try again"

Android System Errors:
- `SecurityException` (permissions) → "Storage permission required. Please grant permission in settings"
- `IOException` (insufficient storage) → "Insufficient storage space. Please free up space and try again"
- `ActivityNotFoundException` (no PDF viewer) → "No PDF viewer installed. Please install a PDF viewer app"
- File size exceeds limit → "File size exceeds limit. Please select a smaller file"

**ViewModel Layer**:
- Receives Result<T> from repository
- Transforms errors into NotesUiState.Error or UploadState.Error
- Ensures error messages are student-friendly and actionable
- Never exposes stack traces or technical details
- Maintains UI state consistency during errors

**UI Layer**:
- Observes state from ViewModel
- Displays errors using ErrorMessage component (Snackbar style) from Phase 1
- Shows field-specific validation errors below input fields
- Provides visual feedback for all error states
- Auto-dismisses snackbars after 4 seconds (consistent with Phase 1)

### Error Categories

**Validation Errors**:
- Displayed immediately below input fields on Upload Form
- Red text with error icon
- Examples: "Title is required", "Please select a semester", "Please select a PDF file"

**Upload Errors**:
- Displayed as Snackbar on Upload Form
- Auto-dismiss after 4 seconds
- Examples: "Network error. Please check your connection and try again", "Storage quota exceeded"

**Download Errors**:
- Displayed as Snackbar on NotesScreen
- Auto-dismiss after 4 seconds
- Examples: "Network error", "Insufficient storage space", "Storage permission required"

**Delete Errors**:
- Displayed as Snackbar on NotesScreen
- Auto-dismiss after 4 seconds
- Note remains in list if deletion fails
- Example: "Failed to delete note. Please try again"

**Permission Errors**:
- Displayed as Snackbar with action to open settings
- Persistent until dismissed
- Examples: "Storage permission required. Please grant permission in settings"

**System Errors**:
- Displayed as Snackbar
- Generic user-friendly message
- Example: "Something went wrong. Please try again"

### Error Recovery

**Rollback Mechanisms**:
- If metadata save fails after file upload, automatically delete uploaded file from Storage
- Ensures data consistency between Storage and Firestore
- User sees single error message, rollback happens transparently

**Retry Support**:
- All error states allow user to retry the operation
- Input fields remain populated after errors (user doesn't lose data)
- Network errors include implicit retry when user attempts operation again

**State Preservation**:
- Failed uploads don't clear the form (user can fix and retry)
- Failed downloads don't affect note display
- Failed deletes keep note in list with error feedback


## Testing Strategy

### Dual Testing Approach

The Notes Module uses both unit testing and property-based testing for comprehensive coverage, following Phase 1 patterns:

**Unit Tests**:
- Specific examples and edge cases
- Integration points between components
- Error conditions and boundary cases
- UI component rendering and interaction
- Navigation flows
- Permission handling scenarios

**Property-Based Tests**:
- Universal properties across all inputs
- Validation logic with randomized inputs
- Search and filter operations with generated data
- File operations with various file types and sizes
- State transitions and flow correctness
- Comprehensive input coverage through randomization

Both approaches are complementary: unit tests catch concrete bugs and verify specific scenarios, while property tests verify general correctness across a wide range of inputs.

### Property-Based Testing Configuration

**Library**: Kotest Property Testing (consistent with Phase 1)
- Mature property-based testing library for Kotlin/Android
- Integrates well with JUnit and Android testing frameworks
- Provides generators for common types and custom data

**Configuration**:
- Minimum 100 iterations per property test
- Each test tagged with reference to design document property
- Tag format: `@Tag("Feature: campus-connect-notes-module, Property {number}: {property_text}")`

**Example Property Test Structure**:
```kotlin
@Test
@Tag("Feature: campus-connect-notes-module, Property 12: Search Filtering")
fun `search filters notes by title case-insensitively`() = runTest {
    checkAll(100, Arb.list(Arb.note()), Arb.string()) { notes, query ->
        val viewModel = NotesViewModel(mockRepository, mockAuthRepository)
        viewModel.setNotes(notes)
        viewModel.searchNotes(query)
        
        val filtered = viewModel.notesUiState.value.notes
        filtered.all { note ->
            note.title.contains(query, ignoreCase = true)
        } shouldBe true
    }
}
```

### Test Coverage Areas

**NotesViewModel Tests**:
- Load notes from repository
- Search notes by title (property-based)
- Filter notes by subject (property-based)
- Filter notes by semester (property-based)
- Combined search and filters (property-based)
- Upload validation (property-based)
- State transformations
- Real-time update handling

**NotesRepository Tests** (with mocked Firebase):
- Upload file to Storage with correct path
- Save metadata to Firestore with correct structure
- Fetch notes from Firestore
- Delete note (metadata + file)
- Download file to correct location
- Rollback on metadata save failure
- Error mapping correctness
- Real-time listener setup

**UI Component Tests**:
- NotesScreen rendering with various states
- NoteCard rendering with note data
- EmptyState rendering with different messages
- UploadFormScreen validation display
- Search bar interaction
- Filter chips interaction
- Permission request flows

**Navigation Tests**:
- Navigate from Dashboard to NotesScreen
- Navigate from NotesScreen to UploadFormScreen
- Navigate back after successful upload
- Bottom navigation integration

**File Operation Tests**:
- File picker integration
- File type validation (property-based)
- Upload progress tracking
- Download progress tracking
- Share intent creation

**Permission Tests**:
- Permission check before file selection
- Permission check before download
- Permission request flow
- Permission denial handling

**Search and Filter Tests** (Property-Based):
- Search with random queries and note lists
- Filter with random subject selections
- Filter with random semester selections
- Combined search and filters with random inputs
- Empty result handling

**Error Handling Tests**:
- Network error scenarios
- Storage quota error scenarios
- Permission denial scenarios
- Invalid file type scenarios
- Missing PDF viewer scenarios
- Rollback on failure scenarios

### Testing Tools

- **JUnit 5**: Test framework (consistent with Phase 1)
- **Kotest**: Property-based testing (consistent with Phase 1)
- **MockK**: Mocking Firebase dependencies (consistent with Phase 1)
- **Turbine**: Testing StateFlow emissions (consistent with Phase 1)
- **Compose Test**: UI component testing
- **Robolectric**: Android framework testing without emulator

### Test Organization

```
src/test/                                    # Unit tests
├── viewmodel/
│   └── NotesViewModelTest.kt               # ViewModel logic tests
├── repository/
│   └── NotesRepositoryTest.kt              # Repository with mocked Firebase
├── validation/
│   └── NotesValidationPropertyTests.kt     # Property-based validation tests
├── search/
│   └── SearchFilterPropertyTests.kt        # Property-based search/filter tests
└── model/
    └── NoteModelTest.kt                    # Model conversion tests

src/androidTest/                             # Instrumentation tests
├── ui/
│   ├── NotesScreenTest.kt                  # NotesScreen UI tests
│   ├── UploadFormScreenTest.kt             # UploadFormScreen UI tests
│   └── NoteCardTest.kt                     # NoteCard component tests
└── integration/
    ├── NotesFlowIntegrationTest.kt         # End-to-end notes flow
    └── PermissionFlowTest.kt               # Permission handling flow
```

### Test Data Generators (for Property-Based Tests)

```kotlin
// Custom Kotest generators for Notes Module
fun Arb.Companion.note(): Arb<Note> = arbitrary {
    Note(
        id = Arb.string(10..20).bind(),
        title = Arb.string(5..50).bind(),
        subject = Arb.element(listOf("Mathematics", "Physics", "Chemistry", "Computer Science")).bind(),
        semester = Arb.element(SemesterOptions.semesters).bind(),
        description = Arb.string(10..200).bind(),
        fileName = "${Arb.long(1000000000000L..9999999999999L).bind()}_${Arb.string(5..20).bind()}.pdf",
        fileUrl = "https://firebasestorage.googleapis.com/${Arb.string(20..50).bind()}",
        uploadDate = Arb.long(1000000000000L..9999999999999L).bind(),
        fileSize = Arb.long(1024L..10485760L).bind(),
        userId = Arb.string(10..20).bind()
    )
}

fun Arb.Companion.noteMetadata(): Arb<NoteMetadata> = arbitrary {
    NoteMetadata(
        title = Arb.string(5..50).bind(),
        subject = Arb.element(listOf("Mathematics", "Physics", "Chemistry", "Computer Science")).bind(),
        semester = Arb.element(SemesterOptions.semesters).bind(),
        description = Arb.string(10..200).bind()
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
│   │   │   │   │   ├── NotesScreen.kt                 # CREATE (replaces NotesPlaceholderScreen.kt)
│   │   │   │   │   └── UploadFormScreen.kt            # CREATE
│   │   │   │   └── components/
│   │   │   │       ├── NoteCard.kt                    # CREATE
│   │   │   │       ├── EmptyState.kt                  # CREATE
│   │   │   │       ├── CustomTextField.kt             # EXISTING (reused)
│   │   │   │       ├── CustomButton.kt                # EXISTING (reused)
│   │   │   │       ├── LoadingIndicator.kt            # EXISTING (reused)
│   │   │   │       └── ErrorMessage.kt                # EXISTING (reused)
│   │   │   │
│   │   │   ├── navigation/
│   │   │   │   ├── Routes.kt                          # MODIFY (add UPLOAD_NOTE route)
│   │   │   │   └── MainNavGraph.kt                    # MODIFY (replace placeholder, add upload route)
│   │   │   │
│   │   │   ├── viewmodel/
│   │   │   │   ├── NotesViewModel.kt                  # CREATE
│   │   │   │   ├── AuthViewModel.kt                   # EXISTING
│   │   │   │   └── DashboardViewModel.kt              # EXISTING
│   │   │   │
│   │   │   ├── repository/
│   │   │   │   ├── NotesRepository.kt                 # CREATE
│   │   │   │   └── AuthRepository.kt                  # EXISTING
│   │   │   │
│   │   │   ├── model/
│   │   │   │   ├── Note.kt                            # CREATE
│   │   │   │   ├── NotesUiState.kt                    # CREATE
│   │   │   │   ├── FilterState.kt                     # CREATE
│   │   │   │   ├── UploadState.kt                     # CREATE
│   │   │   │   ├── DownloadState.kt                   # CREATE
│   │   │   │   ├── NoteMetadata.kt                    # CREATE
│   │   │   │   ├── User.kt                            # EXISTING
│   │   │   │   ├── AuthState.kt                       # EXISTING
│   │   │   │   ├── ValidationResult.kt                # EXISTING
│   │   │   │   └── QuickAction.kt                     # EXISTING
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
│   │   │   ├── values/
│   │   │   │   └── strings.xml                        # MODIFY (add notes strings)
│   │   │   └── xml/
│   │   │       └── file_paths.xml                     # CREATE (for FileProvider)
│   │   │
│   │   └── AndroidManifest.xml                        # MODIFY (add permissions, FileProvider)
│   │
│   ├── test/                                          # Unit tests
│   │   └── java/com/example/campusconnect/
│   │       ├── viewmodel/
│   │       │   └── NotesViewModelTest.kt              # CREATE
│   │       ├── repository/
│   │       │   └── NotesRepositoryTest.kt             # CREATE
│   │       ├── validation/
│   │       │   └── NotesValidationPropertyTests.kt    # CREATE
│   │       ├── search/
│   │       │   └── SearchFilterPropertyTests.kt       # CREATE
│   │       └── model/
│   │           └── NoteModelTest.kt                   # CREATE
│   │
│   └── androidTest/                                   # Instrumentation tests
│       └── java/com/example/campusconnect/
│           ├── ui/
│           │   ├── NotesScreenTest.kt                 # CREATE
│           │   ├── UploadFormScreenTest.kt            # CREATE
│           │   └── NoteCardTest.kt                    # CREATE
│           └── integration/
│               ├── NotesFlowIntegrationTest.kt        # CREATE
│               └── PermissionFlowTest.kt              # CREATE
│
└── build.gradle.kts (app level)                       # NO CHANGES (all dependencies from Phase 1)
```

### Files to Create (18 new files)

**Models** (6 files):
1. Note.kt
2. NotesUiState.kt
3. FilterState.kt
4. UploadState.kt
5. DownloadState.kt
6. NoteMetadata.kt

**Repository** (1 file):
7. NotesRepository.kt

**ViewModel** (1 file):
8. NotesViewModel.kt

**UI Screens** (2 files):
9. NotesScreen.kt
10. UploadFormScreen.kt

**UI Components** (2 files):
11. NoteCard.kt
12. EmptyState.kt

**Configuration** (1 file):
13. file_paths.xml

**Tests** (5 files):
14. NotesViewModelTest.kt
15. NotesRepositoryTest.kt
16. NotesValidationPropertyTests.kt
17. SearchFilterPropertyTests.kt
18. NoteModelTest.kt

### Files to Modify (4 existing files)

1. **Routes.kt**: Add UPLOAD_NOTE route constant
2. **MainNavGraph.kt**: Replace NotesPlaceholderScreen with NotesScreen, add UploadFormScreen route
3. **AndroidManifest.xml**: Add storage permissions, add FileProvider configuration
4. **strings.xml**: Add notes-related strings

### Files to Delete (1 file)

1. **NotesPlaceholderScreen.kt**: Replaced by NotesScreen.kt


## Implementation Order and Workflow

### Phase 1: Data Models (Build remains functional)

**Step 1: Create Data Models**
- Create Note.kt (data class with Firestore conversion methods)
- Create NotesUiState.kt (sealed class for UI states)
- Create FilterState.kt (data class for filter state)
- Create UploadState.kt (sealed class for upload states)
- Create DownloadState.kt (sealed class for download states)
- Create NoteMetadata.kt (data class for upload metadata)
- Build and verify

### Phase 2: Repository Layer (Build remains functional)

**Step 2: Create NotesRepository**
- Create NotesRepository.kt with method signatures
- Implement uploadNote method (Storage + Firestore with rollback)
- Implement fetchNotes method (Firestore query)
- Implement observeNotes method (Firestore real-time listener)
- Implement deleteNote method (Firestore + Storage)
- Implement downloadNote method (Storage download)
- Implement error mapping
- Build and verify

### Phase 3: ViewModel Layer (Build remains functional)

**Step 3: Create NotesViewModel**
- Create NotesViewModel.kt with StateFlow properties
- Implement loadNotes method
- Implement refreshNotes method
- Implement searchNotes method (client-side filtering)
- Implement filterBySubject method
- Implement filterBySemester method
- Implement clearFilters method
- Implement uploadNote method (with validation)
- Implement deleteNote method
- Implement downloadNote method
- Implement real-time listener integration
- Build and verify

### Phase 4: UI Components (Build remains functional)

**Step 4: Create Reusable Components**
- Create EmptyState.kt (composable for empty states)
- Create NoteCard.kt (composable for note display with actions)
- Build and verify

### Phase 5: UI Screens (Build remains functional)

**Step 5: Create UploadFormScreen**
- Create UploadFormScreen.kt
- Implement file picker integration
- Implement form fields (title, subject, semester, description)
- Implement validation display
- Implement upload progress display
- Reuse CustomTextField, CustomButton, LoadingIndicator from Phase 1
- Build and verify

**Step 6: Create NotesScreen**
- Create NotesScreen.kt
- Implement notes list display with LazyColumn
- Implement search bar
- Implement filter chips (subject and semester)
- Implement pull-to-refresh
- Implement empty states
- Implement loading states
- Implement error states
- Implement FAB for upload navigation
- Integrate bottom navigation from Phase 1
- Build and verify

### Phase 6: Navigation Integration (Build remains functional)

**Step 7: Update Navigation**
- Modify Routes.kt (add UPLOAD_NOTE constant)
- Modify MainNavGraph.kt:
  - Replace NotesPlaceholderScreen with NotesScreen
  - Add UploadFormScreen route
  - Wire up navigation between screens
- Delete NotesPlaceholderScreen.kt
- Build and verify
- Test navigation flow

### Phase 7: Permissions and Configuration (Build remains functional)

**Step 8: Add Permissions and FileProvider**
- Create res/xml/file_paths.xml (FileProvider configuration)
- Modify AndroidManifest.xml:
  - Add READ_EXTERNAL_STORAGE permission
  - Add WRITE_EXTERNAL_STORAGE permission
  - Add FileProvider configuration
- Modify strings.xml (add notes-related strings)
- Build and verify

### Phase 8: Testing (Build remains functional)

**Step 9: Unit Tests**
- Create NoteModelTest.kt (test Firestore conversion)
- Create NotesRepositoryTest.kt (test with mocked Firebase)
- Create NotesViewModelTest.kt (test state management)
- Create NotesValidationPropertyTests.kt (property-based validation tests)
- Create SearchFilterPropertyTests.kt (property-based search/filter tests)
- Run tests and verify

**Step 10: UI Tests**
- Create NotesScreenTest.kt (test UI rendering and interaction)
- Create UploadFormScreenTest.kt (test form validation and submission)
- Create NoteCardTest.kt (test card rendering and actions)
- Run tests and verify

**Step 11: Integration Tests**
- Create NotesFlowIntegrationTest.kt (test end-to-end flow)
- Create PermissionFlowTest.kt (test permission handling)
- Run tests and verify

### Phase 9: Polish and Refinement

**Step 12: Final Testing**
- Test upload flow with various file sizes
- Test download to different Android versions
- Test search and filter combinations
- Test real-time updates
- Test error scenarios (network errors, permission denials, etc.)
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

**Naming Conventions** (consistent with Phase 1):
- Composables: PascalCase (e.g., NotesScreen, NoteCard)
- Functions: camelCase (e.g., uploadNote, searchNotes)
- Constants: UPPER_SNAKE_CASE (e.g., MAX_FILE_SIZE)
- Packages: lowercase (e.g., ui, viewmodel, repository)

**Code Organization**:
- One screen per file
- Group related composables in the same file
- Keep composables small and focused
- Extract reusable logic to separate functions
- Use preview annotations for UI development

**State Management** (consistent with Phase 1):
- ViewModels own the state
- UI observes state using collectAsState()
- State updates trigger recomposition
- Use remember for UI-only state
- Use StateFlow for shared state

**Error Handling** (consistent with Phase 1):
- Always handle Firebase exceptions
- Provide user-friendly error messages
- Log errors for debugging
- Never expose technical details to users
- Implement rollback for failed operations

**Testing**:
- Write tests alongside implementation
- Test ViewModels with mocked repositories
- Test repositories with mocked Firebase
- Test UI components in isolation
- Use property-based tests for search/filter logic
- Test permission flows on different Android versions

### Dependency Notes

**No New Dependencies Required**:
All required dependencies were added in Phase 1:
- Firebase Storage (already included)
- Firestore (already included)
- Compose Navigation (already included)
- Kotest for property-based testing (already included)
- MockK for mocking (already included)

**FileProvider Configuration**:
FileProvider is part of AndroidX Core, already included in Phase 1 dependencies.

### Integration with Phase 1

**Reused Components**:
- CustomTextField: Used in UploadFormScreen for title, subject, description inputs
- CustomButton: Used in UploadFormScreen for upload button and EmptyState for action button
- LoadingIndicator: Used in NotesScreen for initial loading state
- ErrorMessage: Used throughout for error display (snackbars)
- CampusConnectTheme: Applied to all new screens
- Bottom Navigation: Integrated into NotesScreen

**Reused Patterns**:
- MVVM architecture: NotesViewModel follows same pattern as AuthViewModel
- StateFlow for state management: Same pattern as Phase 1
- Result type for repository operations: Same pattern as AuthRepository
- Navigation structure: Integrated into existing MainNavGraph
- Error handling: Same snackbar-based approach as Phase 1
- Material 3 styling: Consistent rounded corners, colors, typography

**Modified Components**:
- MainNavGraph: Updated to include NotesScreen and UploadFormScreen
- Routes: Extended with new route constants
- Bottom Navigation: Notes item now navigates to functional NotesScreen instead of placeholder

---

This design document provides a complete blueprint for implementing the Campus Connect Notes Module (Phase 2). The architecture seamlessly integrates with Phase 1, reuses existing components for consistency, and maintains the established MVVM patterns and Material 3 design system. The implementation order ensures the app remains buildable at every step, supporting incremental development and testing.

