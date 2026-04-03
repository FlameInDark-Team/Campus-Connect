# Migration Guide: Firestore to Room Database

This document explains the migration from Firebase Firestore to Room Database (local SQLite storage).

## What Changed?

### Before (Firestore)
- Cloud-based NoSQL database
- Required internet connection
- Required Firebase project setup
- Data stored in Firebase servers
- Real-time sync across devices

### After (Room Database)
- Local SQLite database
- Works completely offline
- No cloud database setup needed
- Data stored on device
- Faster performance (local queries)

## Benefits of Room Database

✅ **No Setup Required**: No need to configure Firestore or set up security rules  
✅ **Works Offline**: All data is stored locally on the device  
✅ **Faster Performance**: Local database queries are instant  
✅ **Free Forever**: No cloud database costs or quotas  
✅ **Privacy**: User data stays on their device  
✅ **Simple**: Easier to understand and maintain  

## What Stayed the Same?

✅ **Firebase Auth**: User authentication still uses Firebase  
✅ **Firebase Storage**: PDF files still stored in Firebase Storage  
✅ **All UI Screens**: No changes to user interface  
✅ **ViewModels**: Same business logic and state management  
✅ **Navigation**: Same navigation structure  

## Technical Changes

### 1. Dependencies (build.gradle.kts)
```kotlin
// REMOVED
implementation("com.google.firebase:firebase-firestore-ktx")

// ADDED
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
kapt("androidx.room:room-compiler:2.6.1")
```

### 2. New Database Structure

#### Entities
- `NoteEntity`: Room entity for notes metadata
- `AttendanceSubjectEntity`: Room entity for attendance data

#### DAOs (Data Access Objects)
- `NotesDao`: CRUD operations for notes
- `AttendanceDao`: CRUD operations for attendance

#### Database
- `AppDatabase`: Room database singleton with version 1

### 3. Repository Changes

#### NotesRepository
- **Before**: Used Firestore collections and documents
- **After**: Uses Room DAO methods
- **Interface**: Same methods, same return types (Result, Flow)

#### AttendanceRepository
- **Before**: Used Firestore collections and documents
- **After**: Uses Room DAO methods
- **Interface**: Same methods, same return types (Result, Flow)

### 4. Data Flow

```
Before: ViewModel → Repository → Firestore → Cloud
After:  ViewModel → Repository → DAO → Room → SQLite (Local)
```

## File Structure

### New Files Created
```
app/src/main/java/com/example/campusconnect/database/
├── AppDatabase.kt                      # Room database instance
├── entity/
│   ├── NoteEntity.kt                  # Notes table entity
│   └── AttendanceSubjectEntity.kt     # Attendance table entity
└── dao/
    ├── NotesDao.kt                    # Notes data access
    └── AttendanceDao.kt               # Attendance data access
```

### Modified Files
```
app/build.gradle.kts                   # Updated dependencies
app/src/main/java/com/example/campusconnect/
├── data/FirebaseManager.kt            # Removed Firestore, added database
├── repository/NotesRepository.kt      # Uses Room DAO instead of Firestore
├── repository/AttendanceRepository.kt # Uses Room DAO instead of Firestore
└── navigation/NavGraph.kt             # Updated repository initialization
```

## Database Schema

### Notes Table
```sql
CREATE TABLE notes (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    subject TEXT NOT NULL,
    semester TEXT NOT NULL,
    description TEXT NOT NULL,
    file_name TEXT NOT NULL,
    file_url TEXT NOT NULL,
    upload_date INTEGER NOT NULL,
    file_size INTEGER NOT NULL,
    user_id TEXT NOT NULL
);
```

### Attendance Subjects Table
```sql
CREATE TABLE attendance_subjects (
    id TEXT PRIMARY KEY,
    subject_name TEXT NOT NULL,
    total_classes INTEGER NOT NULL,
    attended_classes INTEGER NOT NULL,
    percentage REAL NOT NULL,
    last_updated INTEGER NOT NULL,
    user_id TEXT NOT NULL
);
```

## Key Implementation Details

### 1. Database Initialization
```kotlin
// In FirebaseManager.kt
private lateinit var database: AppDatabase

fun initialize(app: android.app.Application) {
    FirebaseApp.initializeApp(app)
    database = AppDatabase.getInstance(app)
}
```

### 2. Repository Pattern Maintained
```kotlin
// Same interface for ViewModels
suspend fun fetchNotes(userId: String): Result<List<Note>>
fun observeNotes(userId: String): Flow<List<Note>>
suspend fun deleteNote(userId: String, noteId: String, fileUrl: String): Result<Unit>
```

### 3. Reactive Updates with Flow
```kotlin
// Room DAO returns Flow
@Query("SELECT * FROM notes WHERE user_id = :userId ORDER BY upload_date DESC")
fun observeNotes(userId: String): Flow<List<NoteEntity>>

// Repository maps to domain model
fun observeNotes(userId: String): Flow<List<Note>> {
    return notesDao.observeNotes(userId).map { entities ->
        entities.map { it.toNote() }
    }
}
```

### 4. ID Generation
```kotlin
// Before: Firestore auto-generated IDs
val id = firestore.collection("users").document(userId)
    .collection("notes").document().id

// After: UUID for unique IDs
val id = UUID.randomUUID().toString()
```

## Testing the Migration

### 1. Clean Build
```bash
./gradlew clean
./gradlew build
```

### 2. Verify Database Creation
- Run the app
- Add a note or subject
- Check Android Studio's Database Inspector
- Verify data is stored in Room database

### 3. Test Offline Functionality
- Enable airplane mode
- App should work normally (except PDF upload)
- All data operations should succeed

## Troubleshooting

### Build Errors
**Issue**: Kapt errors or Room compiler issues  
**Solution**: 
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

### Database Not Created
**Issue**: Database instance is null  
**Solution**: Ensure `FirebaseManager.initialize()` is called in `Application.onCreate()`

### Data Not Persisting
**Issue**: Data disappears after app restart  
**Solution**: Check that you're using the singleton database instance from `AppDatabase.getInstance()`

## Migration Checklist

- [x] Remove Firestore dependency
- [x] Add Room dependencies
- [x] Create Room entities
- [x] Create DAOs
- [x] Create AppDatabase
- [x] Update NotesRepository
- [x] Update AttendanceRepository
- [x] Update FirebaseManager
- [x] Update NavGraph
- [x] Update README
- [x] Test all features

## Future Considerations

### Optional Cloud Sync
If you want to add cloud sync later:
1. Keep Room as the single source of truth
2. Add a sync service that uploads to Firestore
3. Implement conflict resolution
4. Use WorkManager for background sync

### Data Export/Import
Room makes it easy to:
- Export database to JSON
- Backup database file
- Import data from other sources
- Migrate between devices

## Questions?

For issues or questions about this migration:
1. Check the code comments in repository files
2. Review Room documentation: https://developer.android.com/training/data-storage/room
3. Check the app's error logs for specific issues

---

**Migration completed successfully!** 🎉

The app now uses Room Database for local storage while maintaining all functionality.
