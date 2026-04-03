# Quick Start Guide - Campus Connect with Room Database

## What You Need

### Required
- ✅ Android Studio Hedgehog or later
- ✅ JDK 8 or later
- ✅ Android SDK (API 24+)
- ✅ Firebase project (for Auth & Storage only)

### NOT Required
- ❌ Firestore Database setup
- ❌ Firestore security rules
- ❌ Cloud database configuration

## Setup in 5 Minutes

### Step 1: Clone and Open
```bash
git clone <repository-url>
cd campus-connect
```
Open the project in Android Studio.

### Step 2: Firebase Setup (Auth & Storage Only)

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project (or use existing)
3. Add an Android app:
   - Package name: `com.example.campusconnect`
   - Download `google-services.json`
4. Replace `app/google-services.json` with your file
5. Enable **Authentication** → Email/Password
6. Enable **Storage** (for PDF files)

**That's it!** No Firestore setup needed! 🎉

### Step 3: Build and Run
```bash
./gradlew clean
./gradlew build
./gradlew installDebug
```

Or use Android Studio:
- Click "Sync Project with Gradle Files"
- Click "Run" (Shift+F10)

## How It Works

### Data Storage
```
User Data (Notes & Attendance) → Room Database → SQLite (Local)
PDF Files → Firebase Storage → Cloud
User Auth → Firebase Auth → Cloud
```

### Why This Architecture?
- **Room Database**: Fast, offline, free, private
- **Firebase Storage**: Reliable, free tier is generous, handles large files
- **Firebase Auth**: Secure, easy to use, handles sessions

## First Run

### 1. Sign Up
- Open the app
- Click "Sign Up"
- Enter email and password
- You're in! 🎉

### 2. Add a Note
- Go to Notes screen
- Click the + button
- Fill in details and select a PDF
- Upload!

### 3. Track Attendance
- Go to Attendance screen
- Click + to add a subject
- Update attendance as you attend classes
- See your percentage in real-time

## Verify Database

### Using Database Inspector (Android Studio)
1. Run the app on an emulator or device
2. Add some data (notes, attendance)
3. In Android Studio: View → Tool Windows → App Inspection
4. Select "Database Inspector" tab
5. Select your app process
6. Expand `campus_connect_database`
7. View `notes` and `attendance_subjects` tables

### Using ADB
```bash
adb shell
run-as com.example.campusconnect
cd databases
ls -la
# You should see: campus_connect_database
```

## Test Offline Mode

1. Run the app
2. Add some notes and attendance data
3. Enable airplane mode on device
4. App should work normally! ✅
5. All data operations succeed
6. Only PDF upload requires internet

## Common Issues

### Issue: Build fails with kapt errors
**Solution**:
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

### Issue: Database not found
**Solution**: Make sure you're running the latest build. Clean and rebuild:
```bash
./gradlew clean
./gradlew installDebug
```

### Issue: Firebase errors
**Solution**: 
- Verify `google-services.json` is in `app/` directory
- Check Firebase Auth is enabled
- Check Firebase Storage is enabled

### Issue: Data not persisting
**Solution**: 
- Check you're using the singleton database instance
- Verify `FirebaseManager.initialize()` is called in Application class
- Check logcat for any database errors

## Project Structure

```
app/src/main/java/com/example/campusconnect/
├── database/              ← NEW! Room Database
│   ├── AppDatabase.kt
│   ├── entity/
│   │   ├── NoteEntity.kt
│   │   └── AttendanceSubjectEntity.kt
│   └── dao/
│       ├── NotesDao.kt
│       └── AttendanceDao.kt
├── repository/            ← Updated to use Room
│   ├── NotesRepository.kt
│   └── AttendanceRepository.kt
├── viewmodel/             ← No changes
├── ui/                    ← No changes
└── navigation/            ← Minor update
```

## Key Features

### Notes Module
- ✅ Upload PDF notes with metadata
- ✅ Search by title
- ✅ Filter by subject and semester
- ✅ Download and share notes
- ✅ Delete notes
- ✅ **Works offline** (except upload)

### Attendance Tracker
- ✅ Add subjects
- ✅ Track attendance
- ✅ Auto-calculate percentage
- ✅ Visual indicators (green/red)
- ✅ Calculate classes needed for 75%
- ✅ **Works completely offline**

## Development Tips

### 1. Database Migrations
When you change the schema:
```kotlin
@Database(
    entities = [NoteEntity::class, AttendanceSubjectEntity::class],
    version = 2,  // Increment version
    exportSchema = false
)
```

### 2. Debugging Queries
Enable SQL logging in `AppDatabase.kt`:
```kotlin
Room.databaseBuilder(context, AppDatabase::class.java, "campus_connect_database")
    .setQueryCallback({ sqlQuery, bindArgs ->
        Log.d("RoomQuery", "SQL: $sqlQuery, Args: $bindArgs")
    }, Executors.newSingleThreadExecutor())
    .build()
```

### 3. Testing
```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## What's Different from Firestore?

| Feature | Firestore | Room Database |
|---------|-----------|---------------|
| Storage | Cloud | Local (SQLite) |
| Offline | Cached | Always offline |
| Setup | Complex | Simple |
| Cost | Paid (after free tier) | Free |
| Speed | Network latency | Instant |
| Sync | Real-time across devices | Local only |
| Privacy | Data in cloud | Data on device |

## Need Cloud Sync?

Room is the single source of truth. To add cloud sync later:

1. Keep Room as primary storage
2. Add a sync service (WorkManager)
3. Upload changes to Firestore/Backend
4. Download changes from cloud
5. Implement conflict resolution

This gives you:
- Offline-first architecture
- Fast local access
- Optional cloud backup
- Multi-device sync

## Resources

- [Room Documentation](https://developer.android.com/training/data-storage/room)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Firebase Auth](https://firebase.google.com/docs/auth)
- [Firebase Storage](https://firebase.google.com/docs/storage)

## Next Steps

1. ✅ Build and run the app
2. ✅ Test all features
3. ✅ Verify offline functionality
4. ✅ Check database persistence
5. ✅ Customize for your needs
6. ✅ Deploy to users

## Questions?

- Check `MIGRATION_GUIDE.md` for detailed technical info
- Check `CHANGES_SUMMARY.md` for all changes made
- Review code comments in repository files
- Check Room documentation

---

**Happy coding!** 🚀

Your app now has a fast, offline-first database with no cloud setup required!
