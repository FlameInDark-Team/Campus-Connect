# Design Document: Campus Connect

## Overview

Campus Connect is a modern Android application built with Kotlin, Jetpack Compose, and Firebase, following MVVM architecture patterns. Phase 1 delivers a complete authentication system with email/password login, signup, password recovery, session management, and a home dashboard with placeholder quick action cards for future features.

The application uses a single-activity architecture with Jetpack Compose Navigation, Material 3 design system, and Firebase Authentication. The codebase is organized into modular packages (ui, navigation, data, repository, viewmodel, model, components, theme) to ensure scalability, maintainability, and beginner-friendliness.

Key architectural decisions:
- **MVVM Pattern**: Clear separation between UI (Composables), business logic (ViewModels), and data access (Repositories)
- **Single Activity**: All screens are Composables managed by Jetpack Compose Navigation
- **StateFlow for State Management**: ViewModels expose UI state using StateFlow for reactive updates
- **Firebase Backend**: Authentication, Firestore (ready for Phase 2), and Storage (ready for Phase 2)
- **Material 3 Design**: Modern, consistent UI with dynamic theming support
- **Modular Package Structure**: Organized for easy navigation and feature additions

## Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        UI Layer                              │
│  (Jetpack Compose Screens + Reusable Components)            │
│                                                              │
│  • AuthScreens (Login, Signup, ForgotPassword, Splash)      │
│  • MainScreens (Dashboard, NotesPlaceholder, Profile)       │
│  • Components (CustomTextField, CustomButton, etc.)         │
└──────────────────┬──────────────────────────────────────────┘
                   │ observes StateFlow
                   │ calls ViewModel methods
┌──────────────────▼──────────────────────────────────────────┐
│                    ViewModel Layer                           │
│         (Business Logic + State Management)                  │
│                                                              │
│  • AuthViewModel (manages auth state, validation)           │
│  • DashboardViewModel (manages dashboard state)             │
│  • Exposes StateFlow<UiState> to UI                         │
└──────────────────┬──────────────────────────────────────────┘
                   │ calls repository methods
                   │ transforms data to UI state
┌──────────────────▼──────────────────────────────────────────┐
│                   Repository Layer                           │
│              (Data Access Abstraction)                       │
│                                                              │
│  • AuthRepository (Firebase Auth operations)                │
│  • Provides clean API for ViewModels                        │
└──────────────────┬──────────────────────────────────────────┘
                   │ uses Firebase SDK
┌──────────────────▼──────────────────────────────────────────┐
│                     Data Layer                               │
│              (Firebase Integration)                          │
│                                                              │
│  • FirebaseAuth (authentication)                            │
│  • Firestore (ready for Phase 2)                            │
│  • Storage (ready for Phase 2)                              │
└─────────────────────────────────────────────────────────────┘
```

### MVVM Pattern Implementation

**Model Layer**:
- Data classes representing domain entities (User, AuthState, etc.)
- Located in `model` package
- Pure Kotlin data classes with no Android dependencies

**View Layer**:
- Jetpack Compose Composables
- Located in `ui` package (organized by feature)
- Observes ViewModel state using `collectAsState()`
- Calls ViewModel methods for user actions
- No business logic - only UI rendering and event handling

**ViewModel Layer**:
- Extends `ViewModel` from Android Architecture Components
- Located in `viewmodel` package
- Manages UI state using `StateFlow` or `MutableState`
- Contains business logic and validation
- Calls Repository methods for data operations
- Transforms repository results into UI state

**Repository Layer**:
- Located in `repository` package
- Abstracts Firebase operations
- Provides clean, testable API for ViewModels
- Handles error mapping from Firebase exceptions to domain errors

### Navigation Architecture

The app uses Jetpack Compose Navigation with two navigation graphs:

**Auth Graph** (unauthenticated users):
- Splash Screen (entry point)
- Login Screen
- Signup Screen
- Forgot Password Screen

**Main Graph** (authenticated users):
- Dashboard (with bottom navigation)
- Notes Placeholder Screen
- Profile Placeholder Screen

Navigation flow:
1. App launches → Splash Screen
2. Splash checks Firebase auth state
3. If authenticated → Navigate to Dashboard (Main Graph)
4. If not authenticated → Navigate to Login (Auth Graph)
5. After successful login/signup → Navigate to Dashboard
6. Back navigation from Dashboard is disabled (prevents returning to auth screens)

## Components and Interfaces

### Package Structure

```
com.example.campusconnect/
├── MainActivity.kt                    # Single activity hosting NavHost
├── CampusConnectApplication.kt        # Application class for Firebase init
│
├── ui/                                # UI Layer (Composables)
│   ├── auth/                          # Authentication screens
│   │   ├── LoginScreen.kt
│   │   ├── SignupScreen.kt
│   │   ├── ForgotPasswordScreen.kt
│   │   └── SplashScreen.kt
│   ├── main/                          # Main app screens
│   │   ├── DashboardScreen.kt
│   │   ├── NotesPlaceholderScreen.kt
│   │   └── ProfilePlaceholderScreen.kt
│   └── components/                    # Reusable UI components
│       ├── CustomTextField.kt         # Email/password input fields
│       ├── CustomButton.kt            # Primary/secondary buttons
│       ├── LoadingIndicator.kt        # Circular progress indicator
│       ├── ErrorMessage.kt            # Snackbar/toast error display
│       └── QuickActionCard.kt         # Dashboard feature cards
│
├── navigation/                        # Navigation setup
│   ├── NavGraph.kt                    # Main navigation host
│   ├── Routes.kt                      # Centralized route definitions
│   ├── AuthNavGraph.kt                # Auth flow navigation
│   └── MainNavGraph.kt                # Main app navigation
│
├── viewmodel/                         # ViewModels
│   ├── AuthViewModel.kt               # Auth state + validation
│   └── DashboardViewModel.kt          # Dashboard state
│
├── repository/                        # Data access layer
│   └── AuthRepository.kt              # Firebase Auth operations
│
├── model/                             # Data models
│   ├── User.kt                        # User data class
│   ├── AuthState.kt                   # Auth UI state sealed class
│   └── ValidationResult.kt            # Validation result data class
│
├── data/                              # Firebase integration
│   └── FirebaseManager.kt             # Firebase initialization
│
└── theme/                             # Material 3 theming
    ├── Color.kt                       # Color palette
    ├── Type.kt                        # Typography definitions
    ├── Theme.kt                       # Theme composable
    └── Spacing.kt                     # Spacing constants
```

### Key Components

#### 1. MainActivity
```kotlin
// Single activity hosting the navigation graph
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CampusConnectTheme {
                NavGraph()
            }
        }
    }
}
```

#### 2. Navigation Routes
```kotlin
// Centralized route definitions
object Routes {
    // Auth routes
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val FORGOT_PASSWORD = "forgot_password"
    
    // Main routes
    const val DASHBOARD = "dashboard"
    const val NOTES = "notes"
    const val PROFILE = "profile"
}
```

#### 3. AuthViewModel
```kotlin
// Manages authentication state and operations
class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    fun login(email: String, password: String)
    fun signup(email: String, password: String)
    fun sendPasswordReset(email: String)
    fun checkAuthState()
    fun logout()
    
    // Validation methods
    private fun validateEmail(email: String): ValidationResult
    private fun validatePassword(password: String): ValidationResult
}
```

#### 4. AuthRepository
```kotlin
// Abstracts Firebase Auth operations
class AuthRepository(private val firebaseAuth: FirebaseAuth) {
    suspend fun login(email: String, password: String): Result<FirebaseUser>
    suspend fun signup(email: String, password: String): Result<FirebaseUser>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    fun getCurrentUser(): FirebaseUser?
    fun logout()
}
```

#### 5. Reusable Components

**CustomTextField**:
- Email and password input fields
- Built-in validation error display
- Material 3 styling with rounded corners
- Support for leading icons and trailing visibility toggle (for passwords)

**CustomButton**:
- Primary and secondary button variants
- Loading state support (shows progress indicator)
- Disabled state handling
- Material 3 styling with proper elevation

**LoadingIndicator**:
- Circular progress indicator overlay
- Blocks user interaction during operations
- Smooth fade in/out animations

**ErrorMessage**:
- Snackbar-style error display
- Toast-style quick feedback
- Consistent error styling across the app

**QuickActionCard**:
- Dashboard feature cards
- Icon, title, and description
- Material 3 card styling with rounded corners
- Ripple effect on tap

### State Management

#### AuthState (Sealed Class)
```kotlin
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    data class Error(val message: String) : AuthState()
    data class Success(val message: String) : AuthState()
}
```

#### UI State Flow
1. User interacts with UI (e.g., clicks login button)
2. Composable calls ViewModel method
3. ViewModel updates StateFlow to Loading
4. ViewModel calls Repository method
5. Repository performs Firebase operation
6. Repository returns Result<T>
7. ViewModel transforms result to UI state
8. ViewModel updates StateFlow (Success or Error)
9. Composable observes StateFlow change
10. UI updates reactively (show success, error, or navigate)

### Component Hierarchy

```
MainActivity
└── CampusConnectTheme
    └── NavGraph (NavHost)
        ├── AuthNavGraph
        │   ├── SplashScreen
        │   │   └── LoadingIndicator
        │   ├── LoginScreen
        │   │   ├── CustomTextField (email)
        │   │   ├── CustomTextField (password)
        │   │   ├── CustomButton (login)
        │   │   ├── LoadingIndicator (when loading)
        │   │   └── ErrorMessage (when error)
        │   ├── SignupScreen
        │   │   ├── CustomTextField (email)
        │   │   ├── CustomTextField (password)
        │   │   ├── CustomButton (signup)
        │   │   ├── LoadingIndicator (when loading)
        │   │   └── ErrorMessage (when error)
        │   └── ForgotPasswordScreen
        │       ├── CustomTextField (email)
        │       ├── CustomButton (send reset)
        │       ├── LoadingIndicator (when loading)
        │       └── ErrorMessage (when error)
        │
        └── MainNavGraph
            └── Scaffold (with BottomNavigation)
                ├── DashboardScreen
                │   ├── Welcome message
                │   └── LazyVerticalGrid
                │       └── QuickActionCard (x4)
                ├── NotesPlaceholderScreen
                └── ProfilePlaceholderScreen
```

## Data Models

### User Model
```kotlin
data class User(
    val uid: String,
    val email: String,
    val displayName: String? = null
)
```

### AuthState Model
```kotlin
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    data class Error(val message: String) : AuthState()
    data class Success(val message: String) : AuthState()
}
```

### ValidationResult Model
```kotlin
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)
```

### QuickAction Model
```kotlin
data class QuickAction(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector
)
```

## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property 1: Email Format Validation

*For any* string provided as an email input, if the string does not match valid email format (contains @ symbol and domain), the validation system should reject it and display an error message before any Firebase API call is made.

**Validates: Requirements 1.3, 3.4, 15.1**

### Property 2: Password Length Validation

*For any* string provided as a password input, if the string length is less than 6 characters, the validation system should reject it and display an error message.

**Validates: Requirements 1.4, 15.2**

### Property 3: Empty Field Validation

*For any* required input field (email or password), if the field is empty or contains only whitespace, the validation system should display an error message on submission attempt.

**Validates: Requirements 2.4, 15.3**

### Property 4: Successful Authentication Navigation

*For any* successful authentication operation (login or signup), the navigation controller should navigate to the Dashboard screen and clear the back stack to prevent returning to auth screens.

**Validates: Requirements 1.5, 2.2, 6.1**

### Property 5: Authentication Error Display

*For any* failed Firebase authentication operation, the system should display a user-friendly error message to the user without exposing technical implementation details or stack traces.

**Validates: Requirements 1.6, 13.1, 13.5**

### Property 6: Loading State During Operations

*For any* asynchronous authentication operation (login, signup, password reset), the UI should display a loading indicator and disable all input fields and buttons until the operation completes.

**Validates: Requirements 1.7, 2.7, 3.6, 14.1, 14.2**

### Property 7: Valid Signup Account Creation

*For any* valid email and password combination (email matches format, password >= 6 characters), calling the signup function should create a new Firebase user account or return an appropriate error if the account already exists.

**Validates: Requirements 1.1, 1.2**

### Property 8: Valid Login Authentication

*For any* registered user credentials (valid email and password), calling the login function should authenticate the user with Firebase and transition to authenticated state.

**Validates: Requirements 2.1**

### Property 9: Password Reset Email

*For any* valid email address provided to the forgot password function, the system should send a password reset email via Firebase and display a confirmation message.

**Validates: Requirements 3.1, 3.2**

### Property 10: Session Check On Launch

*For any* app launch, the authentication system should check for an existing Firebase session before navigating to any screen.

**Validates: Requirements 4.1**

### Property 11: Authenticated Session Navigation

*For any* app launch where a valid Firebase session exists, the navigation controller should navigate directly to the Dashboard screen.

**Validates: Requirements 4.2**

### Property 12: Unauthenticated Session Navigation

*For any* app launch where no valid Firebase session exists, the navigation controller should navigate to the Login screen.

**Validates: Requirements 4.3**

### Property 13: Splash Screen Visibility During Session Check

*For any* session check operation, the splash screen should remain visible until the check completes and navigation decision is made.

**Validates: Requirements 4.4, 5.2**

### Property 14: Logout Session Clearing

*For any* logout operation, the system should clear the Firebase session and navigate to the Login screen.

**Validates: Requirements 4.5**

### Property 15: Minimum Splash Duration

*For any* app launch, the splash screen should display for a minimum of 1 second to ensure smooth visual transition, even if session check completes faster.

**Validates: Requirements 5.4**

### Property 16: Dashboard User Display

*For any* authenticated user on the Dashboard, the screen should display a welcome message containing the user's email or display name.

**Validates: Requirements 6.2**

### Property 17: Quick Action Card Feedback

*For any* quick action card tapped on the Dashboard, the UI should provide visual feedback indicating the feature is a placeholder for Phase 2.

**Validates: Requirements 6.6**

### Property 18: Navigation Item Selection Highlighting

*For any* bottom navigation item selected, the UI should highlight that item using Material 3 styling to indicate the current screen.

**Validates: Requirements 7.2**

### Property 19: Bottom Navigation Persistence

*For any* navigation between main screens (Dashboard, Notes, Profile), the bottom navigation bar should remain visible and functional.

**Validates: Requirements 7.5**

### Property 20: Theme Consistency

*For any* screen in the application, the Material 3 theme should be applied consistently using the CampusConnectTheme composable.

**Validates: Requirements 8.4**

### Property 21: Dark Mode Theme Application

*For any* device in dark mode, the theme system should apply the dark color scheme to all UI components.

**Validates: Requirements 8.7**

### Property 22: Light Mode Theme Application

*For any* device in light mode, the theme system should apply the light color scheme to all UI components.

**Validates: Requirements 8.8**

### Property 23: Dashboard Back Navigation Prevention

*For any* back press event on the Dashboard screen, the navigation controller should not navigate back to authentication screens.

**Validates: Requirements 9.5**

### Property 24: Firebase Initialization On Startup

*For any* app launch, Firebase should be initialized in the Application class before any Firebase operations are attempted.

**Validates: Requirements 12.1**

### Property 25: Validation Error Field Placement

*For any* validation error, the error message should appear below or near the specific input field that failed validation, not as a generic message.

**Validates: Requirements 13.3**

### Property 26: Loading State Clearing

*For any* completed operation (success or failure), the loading indicator should be hidden and input fields should be re-enabled.

**Validates: Requirements 14.3**

### Property 27: Validation Before API Calls

*For any* authentication operation, input validation should execute and pass before any Firebase API call is made.

**Validates: Requirements 15.4**

### Property 28: Real-Time Validation Feedback

*For any* input field with validation rules, validation should trigger either as the user types or on field blur, providing immediate feedback.

**Validates: Requirements 15.5**

## Error Handling

### Error Handling Strategy

The application implements a multi-layered error handling approach:

**Repository Layer**:
- Catches Firebase exceptions
- Maps Firebase error codes to domain-specific error messages
- Returns `Result<T>` type (Success or Failure)
- Example mappings:
  - `FirebaseAuthInvalidCredentialsException` → "Invalid email or password"
  - `FirebaseAuthUserCollisionException` → "An account with this email already exists"
  - `FirebaseAuthInvalidUserException` → "No account found with this email"
  - `FirebaseNetworkException` → "Network error. Please check your connection"

**ViewModel Layer**:
- Receives Result<T> from repository
- Transforms errors into UI-friendly AuthState.Error
- Ensures error messages are student-friendly and actionable
- Never exposes stack traces or technical details

**UI Layer**:
- Observes AuthState from ViewModel
- Displays errors using ErrorMessage component (Snackbar style)
- Shows field-specific validation errors below input fields
- Provides visual feedback for all error states

### Error Categories

**Validation Errors**:
- Displayed immediately below input fields
- Red text with error icon
- Examples: "Please enter a valid email", "Password must be at least 6 characters"

**Authentication Errors**:
- Displayed as Snackbar at bottom of screen
- Auto-dismiss after 4 seconds
- Examples: "Invalid email or password", "Account already exists"

**Network Errors**:
- Displayed as Snackbar with retry option
- Persistent until dismissed or retry succeeds
- Example: "Network error. Please check your connection and try again"

**System Errors**:
- Displayed as Snackbar
- Generic user-friendly message
- Example: "Something went wrong. Please try again"

### Error Recovery

- All error states allow user to retry the operation
- Input fields remain populated after errors (user doesn't lose data)
- Clear error messages guide users on how to fix issues
- Network errors include retry button for convenience

## Testing Strategy

### Dual Testing Approach

Campus Connect uses both unit testing and property-based testing to ensure comprehensive coverage:

**Unit Tests**:
- Specific examples and edge cases
- Integration points between components
- Error conditions and boundary cases
- UI component rendering and interaction
- Navigation flows

**Property-Based Tests**:
- Universal properties across all inputs
- Validation logic with randomized inputs
- State transitions and flow correctness
- Authentication operations with generated data
- Comprehensive input coverage through randomization

Both approaches are complementary: unit tests catch concrete bugs and verify specific scenarios, while property tests verify general correctness across a wide range of inputs.

### Property-Based Testing Configuration

**Library**: Kotest Property Testing (for Kotlin/Android)
- Mature property-based testing library for Kotlin
- Integrates well with JUnit and Android testing frameworks
- Provides generators for common types and custom data

**Configuration**:
- Minimum 100 iterations per property test
- Each test tagged with reference to design document property
- Tag format: `@Tag("Feature: android-auth-app, Property {number}: {property_text}")`

**Example Property Test Structure**:
```kotlin
@Test
@Tag("Feature: android-auth-app, Property 1: Email Format Validation")
fun `email validation rejects invalid formats`() = runTest {
    checkAll(100, Arb.string()) { email ->
        if (!email.contains("@") || !email.contains(".")) {
            val result = authViewModel.validateEmail(email)
            result.isValid shouldBe false
            result.errorMessage shouldNotBe null
        }
    }
}
```

### Test Coverage Areas

**Authentication Flow Tests**:
- Login with valid/invalid credentials
- Signup with valid/invalid data
- Password reset with valid/invalid emails
- Session persistence across app restarts
- Logout functionality

**Validation Tests** (Property-Based):
- Email format validation with random strings
- Password length validation with random lengths
- Empty field validation with whitespace variations
- Real-time validation triggering

**Navigation Tests**:
- Auth graph navigation flows
- Main graph navigation flows
- Back navigation prevention from Dashboard
- Deep linking (Phase 2 preparation)

**UI State Tests**:
- Loading state during operations
- Error state display
- Success state transitions
- Theme application (light/dark mode)

**Repository Tests**:
- Firebase operation mocking
- Error mapping correctness
- Result type handling

### Testing Tools

- **JUnit 5**: Test framework
- **Kotest**: Property-based testing
- **MockK**: Mocking Firebase dependencies
- **Turbine**: Testing StateFlow emissions
- **Compose Test**: UI component testing
- **Espresso**: Integration testing (if needed)

### Test Organization

```
src/test/                              # Unit tests
├── viewmodel/
│   ├── AuthViewModelTest.kt
│   └── DashboardViewModelTest.kt
├── repository/
│   └── AuthRepositoryTest.kt
├── validation/
│   └── ValidationTests.kt            # Property-based validation tests
└── navigation/
    └── NavigationTests.kt

src/androidTest/                       # Instrumentation tests
├── ui/
│   ├── LoginScreenTest.kt
│   ├── SignupScreenTest.kt
│   └── DashboardScreenTest.kt
└── integration/
    └── AuthFlowIntegrationTest.kt
```

## File Structure and Implementation

### Complete File Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/campusconnect/
│   │   │   ├── MainActivity.kt                    # CREATE
│   │   │   ├── CampusConnectApplication.kt        # CREATE
│   │   │   │
│   │   │   ├── ui/
│   │   │   │   ├── auth/
│   │   │   │   │   ├── LoginScreen.kt             # CREATE
│   │   │   │   │   ├── SignupScreen.kt            # CREATE
│   │   │   │   │   ├── ForgotPasswordScreen.kt    # CREATE
│   │   │   │   │   └── SplashScreen.kt            # CREATE
│   │   │   │   ├── main/
│   │   │   │   │   ├── DashboardScreen.kt         # CREATE
│   │   │   │   │   ├── NotesPlaceholderScreen.kt  # CREATE
│   │   │   │   │   └── ProfilePlaceholderScreen.kt # CREATE
│   │   │   │   └── components/
│   │   │   │       ├── CustomTextField.kt         # CREATE
│   │   │   │       ├── CustomButton.kt            # CREATE
│   │   │   │       ├── LoadingIndicator.kt        # CREATE
│   │   │   │       ├── ErrorMessage.kt            # CREATE
│   │   │   │       └── QuickActionCard.kt         # CREATE
│   │   │   │
│   │   │   ├── navigation/
│   │   │   │   ├── NavGraph.kt                    # CREATE
│   │   │   │   ├── Routes.kt                      # CREATE
│   │   │   │   ├── AuthNavGraph.kt                # CREATE
│   │   │   │   └── MainNavGraph.kt                # CREATE
│   │   │   │
│   │   │   ├── viewmodel/
│   │   │   │   ├── AuthViewModel.kt               # CREATE
│   │   │   │   └── DashboardViewModel.kt          # CREATE
│   │   │   │
│   │   │   ├── repository/
│   │   │   │   └── AuthRepository.kt              # CREATE
│   │   │   │
│   │   │   ├── model/
│   │   │   │   ├── User.kt                        # CREATE
│   │   │   │   ├── AuthState.kt                   # CREATE
│   │   │   │   ├── ValidationResult.kt            # CREATE
│   │   │   │   └── QuickAction.kt                 # CREATE
│   │   │   │
│   │   │   ├── data/
│   │   │   │   └── FirebaseManager.kt             # CREATE
│   │   │   │
│   │   │   └── theme/
│   │   │       ├── Color.kt                       # CREATE
│   │   │       ├── Type.kt                        # CREATE
│   │   │       ├── Theme.kt                       # CREATE
│   │   │       └── Spacing.kt                     # CREATE
│   │   │
│   │   ├── res/
│   │   │   ├── values/
│   │   │   │   ├── strings.xml                    # MODIFY (add strings)
│   │   │   │   └── themes.xml                     # MODIFY (Material 3)
│   │   │   └── drawable/
│   │   │       └── ic_launcher_foreground.xml     # MODIFY (app icon)
│   │   │
│   │   └── AndroidManifest.xml                    # MODIFY (permissions, app class)
│   │
│   ├── test/                                      # Unit tests
│   │   └── java/com/example/campusconnect/
│   │       ├── viewmodel/
│   │       │   ├── AuthViewModelTest.kt           # CREATE
│   │       │   └── DashboardViewModelTest.kt      # CREATE
│   │       ├── repository/
│   │       │   └── AuthRepositoryTest.kt          # CREATE
│   │       └── validation/
│   │           └── ValidationPropertyTests.kt     # CREATE
│   │
│   └── androidTest/                               # Instrumentation tests
│       └── java/com/example/campusconnect/
│           └── ui/
│               ├── LoginScreenTest.kt             # CREATE
│               └── AuthFlowIntegrationTest.kt     # CREATE
│
├── build.gradle.kts (app level)                   # MODIFY (dependencies)
├── build.gradle.kts (project level)               # MODIFY (plugins)
├── google-services.json                           # ADD (from Firebase Console)
└── gradle.properties                              # MODIFY (if needed)
```

### Files to Create (28 new files)

**Core Application** (2 files):
1. MainActivity.kt
2. CampusConnectApplication.kt

**UI Screens** (7 files):
3. LoginScreen.kt
4. SignupScreen.kt
5. ForgotPasswordScreen.kt
6. SplashScreen.kt
7. DashboardScreen.kt
8. NotesPlaceholderScreen.kt
9. ProfilePlaceholderScreen.kt

**UI Components** (5 files):
10. CustomTextField.kt
11. CustomButton.kt
12. LoadingIndicator.kt
13. ErrorMessage.kt
14. QuickActionCard.kt

**Navigation** (4 files):
15. NavGraph.kt
16. Routes.kt
17. AuthNavGraph.kt
18. MainNavGraph.kt

**ViewModels** (2 files):
19. AuthViewModel.kt
20. DashboardViewModel.kt

**Repository** (1 file):
21. AuthRepository.kt

**Models** (4 files):
22. User.kt
23. AuthState.kt
24. ValidationResult.kt
25. QuickAction.kt

**Data** (1 file):
26. FirebaseManager.kt

**Theme** (4 files):
27. Color.kt
28. Type.kt
29. Theme.kt
30. Spacing.kt

### Files to Modify (4 existing files)

1. **build.gradle.kts (app level)**: Add Firebase and Compose dependencies
2. **build.gradle.kts (project level)**: Add Google services plugin
3. **AndroidManifest.xml**: Add internet permission, set application class
4. **strings.xml**: Add app strings

### Files to Add (1 file)

1. **google-services.json**: Firebase configuration (from Firebase Console)

## Gradle Dependencies

### Project-Level build.gradle.kts

```kotlin
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
}
```

### App-Level build.gradle.kts

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.campusconnect"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.campusconnect"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")

    // Compose BOM (Bill of Materials)
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    
    // Compose Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")
    
    // ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    
    // Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    
    // Testing - Unit Tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.kotest:kotest-property:5.8.0")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("app.cash.turbine:turbine:1.0.0")
    
    // Testing - Android Tests
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.navigation:navigation-testing:2.7.5")
    
    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
```

### Dependency Rationale

**Compose BOM**: Ensures all Compose dependencies use compatible versions
**Navigation Compose**: Single-activity navigation with type-safe routes
**Firebase BOM**: Manages Firebase dependency versions automatically
**Kotest**: Property-based testing library for Kotlin
**MockK**: Kotlin-friendly mocking library
**Turbine**: Testing StateFlow and Flow emissions
**Coroutines**: Async operations for Firebase calls

## Implementation Order and Workflow

### Phase 1: Foundation Setup (Build remains functional)

**Step 1: Project Configuration**
- Modify project-level build.gradle.kts (add Google services plugin)
- Modify app-level build.gradle.kts (add all dependencies)
- Add google-services.json from Firebase Console
- Sync Gradle and verify build succeeds

**Step 2: Theme System**
- Create theme package
- Create Color.kt (define color palette)
- Create Type.kt (define typography)
- Create Spacing.kt (define spacing constants)
- Create Theme.kt (Material 3 theme composable)
- Build and verify

**Step 3: Data Models**
- Create model package
- Create User.kt
- Create AuthState.kt
- Create ValidationResult.kt
- Create QuickAction.kt
- Build and verify

### Phase 2: Data Layer (Build remains functional)

**Step 4: Firebase Integration**
- Create data package
- Create FirebaseManager.kt (Firebase initialization)
- Create CampusConnectApplication.kt (Application class)
- Modify AndroidManifest.xml (set application class, add internet permission)
- Build and verify

**Step 5: Repository Layer**
- Create repository package
- Create AuthRepository.kt (Firebase Auth operations)
- Build and verify

### Phase 3: Business Logic (Build remains functional)

**Step 6: ViewModels**
- Create viewmodel package
- Create AuthViewModel.kt (auth state, validation, operations)
- Create DashboardViewModel.kt (dashboard state)
- Build and verify

### Phase 4: UI Components (Build remains functional)

**Step 7: Reusable Components**
- Create ui/components package
- Create CustomTextField.kt
- Create CustomButton.kt
- Create LoadingIndicator.kt
- Create ErrorMessage.kt
- Create QuickActionCard.kt
- Build and verify

### Phase 5: Navigation (Build remains functional)

**Step 8: Navigation Setup**
- Create navigation package
- Create Routes.kt (route constants)
- Create NavGraph.kt (empty NavHost with splash route)
- Create AuthNavGraph.kt (auth routes stub)
- Create MainNavGraph.kt (main routes stub)
- Build and verify

### Phase 6: Auth Screens (Build remains functional)

**Step 9: Auth UI**
- Create ui/auth package
- Create SplashScreen.kt (with session check logic)
- Create LoginScreen.kt (with validation and auth)
- Create SignupScreen.kt (with validation and auth)
- Create ForgotPasswordScreen.kt (with password reset)
- Wire up AuthNavGraph.kt
- Build and verify

### Phase 7: Main Screens (Build remains functional)

**Step 10: Main UI**
- Create ui/main package
- Create DashboardScreen.kt (with quick action cards and bottom nav)
- Create NotesPlaceholderScreen.kt
- Create ProfilePlaceholderScreen.kt
- Wire up MainNavGraph.kt
- Build and verify

### Phase 8: MainActivity Integration (Build remains functional)

**Step 11: Main Activity**
- Create MainActivity.kt (single activity with NavHost)
- Wire up complete navigation graph
- Build and verify
- Test complete auth flow

### Phase 9: Testing (Build remains functional)

**Step 12: Unit Tests**
- Create test package structure
- Create AuthViewModelTest.kt
- Create AuthRepositoryTest.kt
- Create ValidationPropertyTests.kt (property-based tests)
- Run tests and verify

**Step 13: UI Tests**
- Create androidTest package structure
- Create LoginScreenTest.kt
- Create AuthFlowIntegrationTest.kt
- Run tests and verify

### Phase 10: Polish and Refinement

**Step 14: Final Touches**
- Add all strings to strings.xml
- Test on different screen sizes
- Test light/dark mode switching
- Test error scenarios
- Verify all acceptance criteria
- Final build and deployment preparation

### Build Verification Strategy

After each step:
1. Sync Gradle
2. Build project (Build → Make Project)
3. Verify no compilation errors
4. Run app on emulator/device if UI changes were made
5. Commit changes to version control

This incremental approach ensures the project remains buildable at every step, allowing for testing and validation throughout development.

### Development Best Practices

**Naming Conventions**:
- Composables: PascalCase (e.g., LoginScreen, CustomButton)
- Functions: camelCase (e.g., validateEmail, sendPasswordReset)
- Constants: UPPER_SNAKE_CASE (e.g., MIN_PASSWORD_LENGTH)
- Packages: lowercase (e.g., ui, viewmodel, repository)

**Code Organization**:
- One screen per file
- Group related composables in the same file
- Keep composables small and focused
- Extract reusable logic to separate functions
- Use preview annotations for UI development

**State Management**:
- ViewModels own the state
- UI observes state using collectAsState()
- State updates trigger recomposition
- Use remember for UI-only state
- Use StateFlow for shared state

**Error Handling**:
- Always handle Firebase exceptions
- Provide user-friendly error messages
- Log errors for debugging (use Timber in production)
- Never expose technical details to users

**Testing**:
- Write tests alongside implementation
- Test ViewModels with mocked repositories
- Test repositories with mocked Firebase
- Test UI components in isolation
- Use property-based tests for validation logic

---

This design document provides a complete blueprint for implementing Campus Connect Phase 1. The architecture is scalable, maintainable, and beginner-friendly, with clear separation of concerns and a modular structure that supports future feature additions in Phase 2.
