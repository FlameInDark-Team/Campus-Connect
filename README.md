# Campus Connect - Android App

A comprehensive Android application for college students built with Kotlin, Jetpack Compose, and Firebase.

## Features

### Phase 1: Authentication & Foundation
- ✅ Firebase Authentication (Email/Password)
- ✅ Login, Signup, and Password Recovery
- ✅ Session Management
- ✅ Material 3 Design System
- ✅ MVVM Architecture
- ✅ Bottom Navigation
- ✅ Dashboard with Quick Actions

### Phase 2: Notes Module
- ✅ PDF Upload with Metadata
- ✅ Local File Storage (No Cloud Required)
- ✅ Search and Filter Notes
- ✅ Real-time Updates
- ✅ Download and Share Notes
- ✅ Delete Notes with Confirmation
- ✅ 100% Offline Functionality

### Phase 3: Attendance Tracker
- ✅ Subject Management
- ✅ Attendance Percentage Calculation
- ✅ Warning System (< 75%)
- ✅ Classes Needed Calculator
- ✅ Color-coded Visual Indicators
- ✅ Real-time Updates

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM
- **Design System**: Material 3
- **Database**: Room (SQLite) - Local storage
- **File Storage**: Local file system (app private storage)
- **Authentication**: Firebase Auth (Email/Password)
- **Navigation**: Jetpack Compose Navigation
- **State Management**: StateFlow

## Project Structure

```
app/src/main/java/com/example/campusconnect/
├── data/                    # Firebase integration
├── database/                # Room Database
│   ├── entity/             # Room entities
│   ├── dao/                # Data Access Objects
│   └── AppDatabase.kt      # Database instance
├── model/                   # Data models
├── repository/              # Data access layer
├── viewmodel/               # Business logic
├── ui/
│   ├── auth/               # Authentication screens
│   ├── main/               # Main app screens
│   └── components/         # Reusable UI components
├── navigation/             # Navigation setup
└── theme/                  # Material 3 theme
```

## Setup Instructions

### Prerequisites
- Android Studio Hedgehog or later
- JDK 8 or later
- Android SDK (API 24+)

### Firebase Setup (Authentication Only)
1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Add an Android app to your Firebase project
3. Download `google-services.json`
4. Replace the placeholder `app/google-services.json` with your actual file
5. Enable Authentication (Email/Password) in Firebase Console
6. **No credit card required** - Authentication is completely free!

### Build and Run
1. Clone the repository
2. Open the project in Android Studio
3. Replace `google-services.json` with your Firebase configuration
4. Sync Gradle files
5. Run the app on an emulator or physical device

## Key Features Implementation

### Database Architecture (Room)
- **Offline-First**: All data stored locally in SQLite database
- **No Cloud Database Needed**: No Firestore setup or costs
- **Fast Performance**: Local queries are instant
- **Reactive Updates**: Flow-based observers for automatic UI updates
- **Type Safety**: Compile-time verification of SQL queries
- **Easy Migration**: Version management for schema changes

### Authentication
- Email/password authentication with Firebase
- Input validation (email format, password length)
- Session persistence
- Password recovery via email

### Notes Module
- Upload PDF files with metadata (title, subject, semester, description)
- Files stored locally in app's private storage (no cloud required)
- Search notes by title
- Filter by subject and semester
- Metadata stored in Room Database
- Download notes to device
- Share notes with other apps
- Delete notes with confirmation dialog
- 100% offline functionality

### Attendance Tracker
- Add subjects to track
- Update attendance (total classes, attended classes)
- Automatic percentage calculation
- Visual indicators (green ≥75%, red <75%)
- Warning badges for low attendance
- Calculate classes needed to reach 75%
- Local storage with Room Database
- Real-time UI updates with Flow

## Architecture Highlights

### MVVM Pattern
- **Model**: Data classes and sealed classes for state
- **View**: Jetpack Compose UI components
- **ViewModel**: Business logic and state management with StateFlow

### Repository Pattern
- Clean separation between data sources and business logic
- Room Database operations abstracted in repositories
- Error mapping for user-friendly messages

### Component Reusability
- CustomTextField, CustomButton, LoadingIndicator
- EmptyState, ErrorMessage, QuickActionCard
- NoteCard, SubjectCard
- Consistent Material 3 styling across all components

## Design Decisions

1. **Single Activity Architecture**: All screens are Composables managed by Navigation
2. **StateFlow for State Management**: Reactive UI updates
3. **Room Database**: Local SQLite storage for offline-first functionality
4. **Local File Storage**: PDF files stored in app's private directory (no cloud needed)
5. **Flow for Reactive Updates**: Database changes automatically update UI
6. **Client-side Filtering**: Search and filter performed locally for instant results
7. **Color-coded Indicators**: Visual feedback for attendance status
8. **Rollback Mechanisms**: Delete local files if metadata save fails
9. **Firebase Auth Only**: User authentication without cloud storage costs
10. **No Credit Card Required**: Completely free to use

## Testing

The project includes comprehensive testing support:
- Unit tests for ViewModels and Repositories
- Property-based tests for validation logic
- UI tests for Compose components
- Integration tests for complete flows

## Future Enhancements

- Announcements module
- Profile management
- Data backup and restore
- Push notifications
- Dark mode improvements
- Export attendance reports
- Collaborative note sharing
- Cloud sync (optional)

## License

This project is for educational purposes.

## Contributors

Built following MVVM architecture patterns and Material 3 design guidelines.
