<div align="center">

# 🎓 Campus Connect

### *Your Ultimate College Companion*

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com/)
[![Material Design 3](https://img.shields.io/badge/Design-Material%203-757575?style=for-the-badge&logo=material-design&logoColor=white)](https://m3.material.io/)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)

*A modern, feature-rich Android application built with cutting-edge technologies to streamline college life.*

[Download](#-download) • [Features](#-features) • [Tech Stack](#-tech-stack) • [Architecture](#-architecture) • [Setup](#-quick-start) • [Screenshots](#-screenshots) • [Contributing](#-contributing)

---

</div>

## 📥 Download

<div align="center">

### 🚀 Latest Release: v1.0.0

[![Download APK](https://img.shields.io/badge/Download-APK-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://github.com/FlameInDark-Team/Campus-Connect/releases/download/v1.0.0/app-debug.apk)
[![GitHub Release](https://img.shields.io/github/v/release/FlameInDark-Team/Campus-Connect?style=for-the-badge)](https://github.com/FlameInDark-Team/Campus-Connect/releases)
[![Downloads](https://img.shields.io/github/downloads/FlameInDark-Team/Campus-Connect/total?style=for-the-badge)](https://github.com/FlameInDark-Team/Campus-Connect/releases)

**[📱 Download Latest APK](https://github.com/FlameInDark-Team/Campus-Connect/releases/latest/download/app-debug.apk)** | **[📋 View All Releases](https://github.com/FlameInDark-Team/Campus-Connect/releases)**

</div>

### Installation Instructions

1. **Download** the APK file from the link above
2. **Enable** "Install from Unknown Sources" on your Android device:
   - Go to **Settings** → **Security** → Enable **Unknown Sources**
   - Or **Settings** → **Apps** → **Special Access** → **Install Unknown Apps** → Select your browser → Allow
3. **Open** the downloaded APK file
4. **Tap Install** and wait for installation to complete
5. **Launch** Campus Connect and enjoy! 🎉

### System Requirements

- **Android Version**: 7.0 (Nougat) or higher (API 24+)
- **Storage**: ~20 MB for app + space for your notes
- **RAM**: 2 GB minimum (4 GB recommended)
- **Internet**: Required only for authentication (first login)

---

## 📱 Overview

**Campus Connect** is a comprehensive Android application designed specifically for college students. It combines essential academic tools into a single, elegant interface - from managing notes and tracking attendance to staying organized throughout the semester.

Built with **100% Kotlin** and **Jetpack Compose**, this app showcases modern Android development practices including **MVVM architecture**, **Room Database**, **Firebase Authentication**, and **Material Design 3**.

### 🎯 Why Campus Connect?

- ✅ **Offline-First Architecture** - Works seamlessly without internet
- ✅ **Privacy-Focused** - Your data stays on your device
- ✅ **Zero Cost** - No subscriptions, no hidden fees
- ✅ **Modern UI** - Beautiful Material Design 3 interface
- ✅ **Production-Ready** - Clean, scalable, maintainable code

---

## ✨ Features

### 🔐 Authentication & Security
- **Firebase Authentication** with email/password
- Secure session management
- Password recovery functionality
- Persistent login state

### 📚 Notes Management
- **Upload PDF notes** with rich metadata
- **Local storage** - no cloud dependency
- **Smart search** by title
- **Advanced filtering** by subject and semester
- **Share notes** with other apps
- **Download & view** PDFs instantly
- **Offline access** to all your notes

### 📊 Attendance Tracker
- **Track multiple subjects** simultaneously
- **Auto-calculate** attendance percentage
- **Visual indicators** (green ≥75%, red <75%)
- **Smart warnings** for low attendance
- **Calculate classes needed** to reach 75%
- **Real-time updates** with Flow
- **Persistent storage** with Room Database

### 🎨 User Experience
- **Material Design 3** theming
- **Smooth animations** and transitions
- **Bottom navigation** for easy access
- **Responsive layouts** for all screen sizes
- **Dark mode** support (system-based)
- **Intuitive UI/UX** design

---

## 🛠 Tech Stack

### Core Technologies

| Technology | Purpose | Version |
|------------|---------|---------|
| **Kotlin** | Programming Language | 1.9.20 |
| **Jetpack Compose** | Modern UI Toolkit | BOM 2023.10.01 |
| **Material Design 3** | Design System | Latest |
| **Coroutines** | Asynchronous Programming | 1.7.3 |
| **Flow** | Reactive Streams | Latest |

### Architecture & Libraries

| Library | Purpose | Version |
|---------|---------|---------|
| **Room Database** | Local SQLite Storage | 2.6.1 |
| **Firebase Auth** | User Authentication | 32.6.0 |
| **Navigation Compose** | Screen Navigation | 2.7.5 |
| **ViewModel** | State Management | 2.6.2 |
| **Lifecycle** | Lifecycle Management | 2.6.2 |
| **Kapt** | Annotation Processing | Latest |

### Testing

| Framework | Purpose | Version |
|-----------|---------|---------|
| **JUnit** | Unit Testing | 4.13.2 |
| **MockK** | Mocking Framework | 1.13.8 |
| **Kotest** | Property-Based Testing | 5.8.0 |
| **Turbine** | Flow Testing | 1.0.0 |
| **Espresso** | UI Testing | 3.5.1 |

---

## 🏗 Architecture

### MVVM Pattern

```
┌─────────────────────────────────────────────────────────┐
│                         View Layer                       │
│  (Jetpack Compose UI - Screens & Components)            │
└────────────────────┬────────────────────────────────────┘
                     │
                     │ observes StateFlow
                     ▼
┌─────────────────────────────────────────────────────────┐
│                      ViewModel Layer                     │
│  (Business Logic, State Management, UI Events)          │
└────────────────────┬────────────────────────────────────┘
                     │
                     │ calls suspend functions
                     ▼
┌─────────────────────────────────────────────────────────┐
│                     Repository Layer                     │
│  (Data Access Abstraction, Error Handling)              │
└────────────────────┬────────────────────────────────────┘
                     │
                     │ queries/inserts
                     ▼
┌─────────────────────────────────────────────────────────┐
│                      Data Layer                          │
│  Room Database (Local) + Firebase Auth (Remote)         │
└─────────────────────────────────────────────────────────┘
```

### Project Structure

```
app/src/main/java/com/example/campusconnect/
├── 📁 data/                    # Firebase integration
│   └── FirebaseManager.kt
├── 📁 database/                # Room Database
│   ├── AppDatabase.kt
│   ├── dao/                    # Data Access Objects
│   │   ├── NotesDao.kt
│   │   └── AttendanceDao.kt
│   └── entity/                 # Database Entities
│       ├── NoteEntity.kt
│       └── AttendanceSubjectEntity.kt
├── 📁 model/                   # Data Models
│   ├── Note.kt
│   ├── AttendanceSubject.kt
│   ├── User.kt
│   └── AuthState.kt
├── 📁 repository/              # Repository Pattern
│   ├── AuthRepository.kt
│   ├── NotesRepository.kt
│   └── AttendanceRepository.kt
├── 📁 viewmodel/               # ViewModels
│   ├── AuthViewModel.kt
│   ├── NotesViewModel.kt
│   ├── AttendanceViewModel.kt
│   └── DashboardViewModel.kt
├── 📁 ui/                      # UI Layer
│   ├── auth/                   # Authentication Screens
│   │   ├── LoginScreen.kt
│   │   ├── SignupScreen.kt
│   │   ├── ForgotPasswordScreen.kt
│   │   └── SplashScreen.kt
│   ├── main/                   # Main App Screens
│   │   ├── DashboardScreen.kt
│   │   ├── NotesScreen.kt
│   │   ├── AttendanceScreen.kt
│   │   ├── ProfileScreen.kt
│   │   └── UploadFormScreen.kt
│   └── components/             # Reusable Components
│       ├── CustomButton.kt
│       ├── CustomTextField.kt
│       ├── NoteCard.kt
│       └── SubjectCard.kt
├── 📁 navigation/              # Navigation
│   ├── NavGraph.kt
│   └── Routes.kt
└── 📁 theme/                   # Material Design 3 Theme
    ├── Color.kt
    ├── Type.kt
    ├── Theme.kt
    └── Spacing.kt
```

---

## 🚀 Quick Start

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or later
- **JDK** 8 or higher
- **Android SDK** API 24+ (Android 7.0+)
- **Firebase Account** (free tier)

### Installation

#### 1. Clone the Repository

```bash
git clone https://github.com/FlameInDark-Team/Campus-Connect.git
cd Campus-Connect
```

#### 2. Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or use existing one
3. Add an Android app:
   - **Package name**: `com.example.campusconnect`
   - Download `google-services.json`
4. Place `google-services.json` in `app/` directory
5. Enable **Authentication** → **Email/Password** sign-in method

**Note**: Firebase Storage is NOT required. This app uses local storage for files.

#### 3. Build & Run

```bash
# Open in Android Studio
# OR use command line:

# Sync Gradle
./gradlew build

# Install on connected device/emulator
./gradlew installDebug

# Run the app
./gradlew run
```

#### 4. First Launch

1. App opens to **Splash Screen**
2. Navigate to **Login Screen**
3. Click **"Sign Up"** to create an account
4. Enter email and password (min 6 characters)
5. Start using Campus Connect! 🎉

---

## 📸 Screenshots

<div align="center">

### Authentication Flow
| Splash Screen | Login | Sign Up |
|:-------------:|:-----:|:-------:|
| ![Splash](docs/screenshots/splash.png) | ![Login](docs/screenshots/login.png) | ![Signup](docs/screenshots/signup.png) |

### Main Features
| Dashboard | Notes | Attendance |
|:---------:|:-----:|:----------:|
| ![Dashboard](docs/screenshots/dashboard.png) | ![Notes](docs/screenshots/notes.png) | ![Attendance](docs/screenshots/attendance.png) |

### Details & Actions
| Note Upload | Attendance Detail | Profile |
|:-----------:|:-----------------:|:-------:|
| ![Upload](docs/screenshots/upload.png) | ![Detail](docs/screenshots/detail.png) | ![Profile](docs/screenshots/profile.png) |

</div>

---

## 🎨 Design System

### Material Design 3

Campus Connect implements Google's latest Material Design 3 (Material You) guidelines:

- **Dynamic Color** - Adapts to system theme
- **Typography Scale** - Consistent text hierarchy
- **Elevation System** - Subtle depth and shadows
- **Motion** - Smooth, purposeful animations
- **Accessibility** - WCAG 2.1 compliant

### Color Palette

```kotlin
// Primary Colors
Primary = Color(0xFF6750A4)
OnPrimary = Color(0xFFFFFFFF)
PrimaryContainer = Color(0xFFEADDFF)

// Secondary Colors
Secondary = Color(0xFF625B71)
OnSecondary = Color(0xFFFFFFFF)
SecondaryContainer = Color(0xFFE8DEF8)

// Tertiary Colors
Tertiary = Color(0xFF7D5260)
OnTertiary = Color(0xFFFFFFFF)
TertiaryContainer = Color(0xFFFFD8E4)
```

---

## 🔧 Configuration

### Gradle Configuration

```kotlin
// app/build.gradle.kts
android {
    compileSdk = 34
    defaultConfig {
        minSdk = 24
        targetSdk = 34
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
}
```

### Performance Optimizations

```properties
# gradle.properties
org.gradle.jvmargs=-Xmx2048m
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true
kotlin.incremental=true
kapt.incremental.apt=true
kapt.use.worker.api=true
```

---

## 📊 Database Schema

### Room Database

#### Notes Table
```sql
CREATE TABLE notes (
    id TEXT PRIMARY KEY NOT NULL,
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

#### Attendance Subjects Table
```sql
CREATE TABLE attendance_subjects (
    id TEXT PRIMARY KEY NOT NULL,
    subject_name TEXT NOT NULL,
    total_classes INTEGER NOT NULL,
    attended_classes INTEGER NOT NULL,
    percentage REAL NOT NULL,
    last_updated INTEGER NOT NULL,
    user_id TEXT NOT NULL
);
```

---

## 🧪 Testing

### Run Tests

```bash
# Unit Tests
./gradlew test

# Instrumented Tests
./gradlew connectedAndroidTest

# Test Coverage Report
./gradlew jacocoTestReport
```

### Test Structure

```
app/src/test/                   # Unit Tests
├── repository/
│   ├── AuthRepositoryTest.kt
│   ├── NotesRepositoryTest.kt
│   └── AttendanceRepositoryTest.kt
├── viewmodel/
│   ├── AuthViewModelTest.kt
│   ├── NotesViewModelTest.kt
│   └── AttendanceViewModelTest.kt
└── database/
    ├── NotesDaoTest.kt
    └── AttendanceDaoTest.kt

app/src/androidTest/            # Instrumented Tests
├── ui/
│   ├── LoginScreenTest.kt
│   ├── NotesScreenTest.kt
│   └── AttendanceScreenTest.kt
└── database/
    └── DatabaseMigrationTest.kt
```

---

## 🔐 Security & Privacy

### Data Protection

- ✅ **Local Storage** - All user data stored on device
- ✅ **No Cloud Database** - Notes and attendance never leave your device
- ✅ **Firebase Auth Only** - Minimal cloud dependency
- ✅ **Encrypted Storage** - Room Database with encryption support
- ✅ **No Analytics** - Zero tracking or data collection

### Best Practices

- Password validation (minimum 6 characters)
- Email format validation
- Secure session management
- Automatic logout on token expiry
- Input sanitization

---

## 📚 Documentation

Comprehensive documentation is available in the `/docs` directory:

- [**Setup Guide**](QUICK_START.md) - Detailed setup instructions
- [**Architecture Guide**](docs/ARCHITECTURE.md) - Deep dive into app architecture
- [**API Documentation**](docs/API.md) - Repository and ViewModel APIs
- [**Migration Guide**](MIGRATION_GUIDE.md) - Firestore to Room migration
- [**Contributing Guide**](CONTRIBUTING.md) - How to contribute
- [**Changelog**](CHANGELOG.md) - Version history

---

## 🤝 Contributing

We welcome contributions! Here's how you can help:

### Ways to Contribute

- 🐛 **Report Bugs** - Open an issue with detailed reproduction steps
- 💡 **Suggest Features** - Share your ideas for improvements
- 📝 **Improve Documentation** - Help make docs clearer
- 🔧 **Submit Pull Requests** - Fix bugs or add features

### Development Workflow

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/AmazingFeature`)
3. **Commit** your changes (`git commit -m 'Add some AmazingFeature'`)
4. **Push** to the branch (`git push origin feature/AmazingFeature`)
5. **Open** a Pull Request

### Code Style

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add comments for complex logic
- Write unit tests for new features
- Ensure all tests pass before submitting PR

---

## 🗺 Roadmap

### Version 1.1 (Planned)
- [ ] Dark mode toggle
- [ ] Export attendance reports (PDF/CSV)
- [ ] Timetable management
- [ ] Push notifications for low attendance
- [ ] Widget support

### Version 1.2 (Future)
- [ ] Cloud sync (optional)
- [ ] Collaborative notes sharing
- [ ] Assignment tracker
- [ ] GPA calculator
- [ ] Exam countdown timer

### Version 2.0 (Vision)
- [ ] Multi-language support
- [ ] Wear OS companion app
- [ ] AI-powered study recommendations
- [ ] Integration with university systems
- [ ] Social features (study groups)

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2026 FlameInDark Team

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

## 👥 Authors

**FlameInDark Team**

- GitHub: [@FlameInDark-Team](https://github.com/FlameInDark-Team)
- Email: contact@flameindar.team

---

## 🙏 Acknowledgments

- **Google** - For Android, Kotlin, Jetpack Compose, and Firebase
- **Material Design** - For the beautiful design system
- **Android Community** - For endless support and resources
- **Contributors** - For making this project better

---

## 📞 Support

Need help? We're here for you!

- 📧 **Email**: support@campusconnect.app
- 💬 **Discord**: [Join our community](https://discord.gg/campusconnect)
- 🐛 **Issues**: [GitHub Issues](https://github.com/FlameInDark-Team/Campus-Connect/issues)
- 📖 **Docs**: [Documentation](https://docs.campusconnect.app)

---

## ⭐ Show Your Support

If you find this project useful, please consider:

- ⭐ **Starring** the repository
- 🍴 **Forking** for your own use
- 📢 **Sharing** with fellow developers
- 💬 **Providing feedback** via issues

---

<div align="center">

### 🎓 Built with ❤️ for Students, by Developers

**Campus Connect** - *Making College Life Easier, One Feature at a Time*

[![GitHub stars](https://img.shields.io/github/stars/FlameInDark-Team/Campus-Connect?style=social)](https://github.com/FlameInDark-Team/Campus-Connect/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/FlameInDark-Team/Campus-Connect?style=social)](https://github.com/FlameInDark-Team/Campus-Connect/network/members)
[![GitHub watchers](https://img.shields.io/github/watchers/FlameInDark-Team/Campus-Connect?style=social)](https://github.com/FlameInDark-Team/Campus-Connect/watchers)

---

**[⬆ Back to Top](#-campus-connect)**

</div>
