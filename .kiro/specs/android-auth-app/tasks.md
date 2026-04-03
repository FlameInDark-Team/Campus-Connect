# Implementation Plan: Campus Connect

## Overview

This implementation plan breaks down the Campus Connect Android app into incremental, testable tasks following the MVVM architecture. The app will be built using Kotlin, Jetpack Compose, and Firebase Authentication. Each task builds on previous work while maintaining build compatibility throughout. The implementation follows a bottom-up approach: foundation → data layer → business logic → UI components → navigation → screens → integration.

## Tasks

- [ ] 1. Set up project configuration and Firebase integration
  - [ ] 1.1 Configure Gradle build files and Firebase
    - Modify project-level build.gradle.kts to add Google services plugin
    - Modify app-level build.gradle.kts to add all dependencies (Compose, Firebase, Navigation, Testing)
    - Add google-services.json from Firebase Console to app directory
    - Sync Gradle and verify build succeeds
    - _Requirements: 12.1, 12.5, 12.6, 12.7, 16.1, 16.2_

  - [ ] 1.2 Create Firebase initialization and Application class
    - Create data/FirebaseManager.kt with Firebase initialization logic
    - Create CampusConnectApplication.kt extending Application class
    - Initialize Firebase in Application.onCreate()
    - Modify AndroidManifest.xml to set application class and add INTERNET permission
    - _Requirements: 12.1, 16.1_

  - [ ]* 1.3 Write unit tests for Firebase initialization
    - Test Firebase initialization succeeds
    - Test Application class setup
    - _Requirements: 12.1_

- [ ] 2. Create theme system and data models
  - [ ] 2.1 Implement Material 3 theme system
    - Create theme/Color.kt with light and dark color schemes
    - Create theme/Type.kt with typography definitions
    - Create theme/Spacing.kt with spacing constants
    - Create theme/Theme.kt with CampusConnectTheme composable supporting light/dark mode
    - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5, 8.6, 8.7, 8.8_

  - [ ] 2.2 Create core data models
    - Create model/User.kt data class
    - Create model/AuthState.kt sealed class (Idle, Loading, Authenticated, Error, Success)
    - Create model/ValidationResult.kt data class
    - Create model/QuickAction.kt data class
    - _Requirements: 10.1_

  - [ ]* 2.3 Write unit tests for data models
    - Test data class instantiation and equality
    - Test AuthState sealed class hierarchy
    - _Requirements: 10.1_

- [ ] 3. Implement repository layer
  - [ ] 3.1 Create AuthRepository with Firebase operations
    - Create repository/AuthRepository.kt class
    - Implement login(email, password) returning Result<FirebaseUser>
    - Implement signup(email, password) returning Result<FirebaseUser>
    - Implement sendPasswordReset(email) returning Result<Unit>
    - Implement getCurrentUser() returning FirebaseUser?
    - Implement logout() clearing Firebase session
    - Map Firebase exceptions to user-friendly error messages
    - _Requirements: 1.1, 1.2, 1.6, 2.1, 2.3, 3.1, 3.3, 4.5, 10.4, 13.1, 13.5_

  - [ ]* 3.2 Write unit tests for AuthRepository
    - Mock FirebaseAuth using MockK
    - Test successful login operation
    - Test failed login with invalid credentials
    - Test successful signup operation
    - Test signup with existing email
    - Test password reset email sending
    - Test error mapping from Firebase exceptions
    - _Requirements: 1.1, 1.2, 2.1, 3.1_

- [ ] 4. Implement ViewModel layer with validation
  - [ ] 4.1 Create AuthViewModel with state management
    - Create viewmodel/AuthViewModel.kt extending ViewModel
    - Add StateFlow<AuthState> for UI state
    - Implement login(email, password) method
    - Implement signup(email, password) method
    - Implement sendPasswordReset(email) method
    - Implement checkAuthState() for session checking
    - Implement logout() method
    - Add validateEmail(email) returning ValidationResult
    - Add validatePassword(password) returning ValidationResult (min 6 chars)
    - Add validateEmptyFields(email, password) returning ValidationResult
    - _Requirements: 1.3, 1.4, 1.5, 1.6, 1.7, 2.1, 2.2, 2.3, 2.4, 2.7, 3.1, 3.2, 3.4, 3.6, 4.1, 4.2, 4.3, 4.5, 10.3, 10.5, 15.1, 15.2, 15.3, 15.4, 15.5_

  - [ ]* 4.2 Write property test for email validation
    - **Property 1: Email Format Validation**
    - **Validates: Requirements 1.3, 3.4, 15.1**
    - Use Kotest to generate random strings
    - Verify strings without @ or domain are rejected
    - Run 100 iterations minimum

  - [ ]* 4.3 Write property test for password validation
    - **Property 2: Password Length Validation**
    - **Validates: Requirements 1.4, 15.2**
    - Use Kotest to generate random strings of various lengths
    - Verify passwords < 6 characters are rejected
    - Run 100 iterations minimum

  - [ ]* 4.4 Write property test for empty field validation
    - **Property 3: Empty Field Validation**
    - **Validates: Requirements 2.4, 15.3**
    - Use Kotest to generate whitespace variations
    - Verify empty or whitespace-only fields are rejected
    - Run 100 iterations minimum

  - [ ]* 4.5 Write unit tests for AuthViewModel
    - Mock AuthRepository using MockK
    - Test login flow with valid credentials
    - Test login flow with invalid credentials
    - Test signup flow with valid data
    - Test signup flow with existing email
    - Test password reset flow
    - Test loading state during operations
    - Test error state handling
    - Use Turbine to test StateFlow emissions
    - _Requirements: 1.5, 1.6, 1.7, 2.2, 2.3, 2.7, 3.2, 14.1, 14.2, 14.3_

  - [ ] 4.6 Create DashboardViewModel
    - Create viewmodel/DashboardViewModel.kt extending ViewModel
    - Add State or StateFlow for user info
    - Add method to load current user from AuthRepository
    - Add list of QuickAction items (Notes, Attendance, Announcements, Profile)
    - _Requirements: 6.2, 6.3, 10.3, 10.5_

  - [ ]* 4.7 Write unit tests for DashboardViewModel
    - Test user info loading
    - Test quick action list initialization
    - _Requirements: 6.2, 6.3_

- [ ] 5. Create reusable UI components
  - [ ] 5.1 Implement CustomTextField component
    - Create ui/components/CustomTextField.kt
    - Support email and password input types
    - Add validation error display below field
    - Add leading icon support
    - Add trailing visibility toggle for password fields
    - Apply Material 3 styling with rounded corners
    - _Requirements: 11.1, 11.5, 11.6, 13.3, 15.1, 15.2, 15.5_

  - [ ] 5.2 Implement CustomButton component
    - Create ui/components/CustomButton.kt
    - Support primary and secondary variants
    - Add loading state with circular progress indicator
    - Add disabled state handling
    - Apply Material 3 styling with rounded corners
    - _Requirements: 11.2, 11.5, 11.6, 14.1, 14.2_

  - [ ] 5.3 Implement LoadingIndicator component
    - Create ui/components/LoadingIndicator.kt
    - Create circular progress indicator overlay
    - Block user interaction during loading
    - Add smooth fade in/out animations
    - _Requirements: 11.3, 11.5, 11.6, 14.1_

  - [ ] 5.4 Implement ErrorMessage component
    - Create ui/components/ErrorMessage.kt
    - Support Snackbar-style error display
    - Add auto-dismiss after 4 seconds
    - Apply consistent error styling
    - _Requirements: 11.4, 11.5, 11.6, 13.1, 13.4_

  - [ ] 5.5 Implement QuickActionCard component
    - Create ui/components/QuickActionCard.kt
    - Display icon, title, and description
    - Apply Material 3 card styling with rounded corners
    - Add ripple effect on tap
    - _Requirements: 6.3, 6.4, 11.5, 11.6_

  - [ ]* 5.6 Write UI tests for reusable components
    - Test CustomTextField rendering and interaction
    - Test CustomButton states (normal, loading, disabled)
    - Test LoadingIndicator visibility
    - Test ErrorMessage display and dismiss
    - Test QuickActionCard tap interaction
    - _Requirements: 11.1, 11.2, 11.3, 11.4_

- [ ] 6. Set up navigation architecture
  - [ ] 6.1 Create navigation routes and structure
    - Create navigation/Routes.kt with route constants (SPLASH, LOGIN, SIGNUP, FORGOT_PASSWORD, DASHBOARD, NOTES, PROFILE)
    - Create navigation/NavGraph.kt with empty NavHost
    - Create navigation/AuthNavGraph.kt stub
    - Create navigation/MainNavGraph.kt stub
    - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.6_

  - [ ]* 6.2 Write navigation tests
    - Test route definitions
    - Test navigation graph setup
    - _Requirements: 9.1, 9.4_

- [ ] 7. Implement authentication screens
  - [ ] 7.1 Create SplashScreen with session check
    - Create ui/auth/SplashScreen.kt
    - Display centered app logo or "Campus Connect" text
    - Call AuthViewModel.checkAuthState() on launch
    - Observe authState and navigate to Dashboard if authenticated, Login if not
    - Ensure minimum 1 second display duration
    - Show LoadingIndicator during session check
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 5.1, 5.2, 5.3, 5.4_

  - [ ]* 7.2 Write property test for session check navigation
    - **Property 10: Session Check On Launch**
    - **Property 11: Authenticated Session Navigation**
    - **Property 12: Unauthenticated Session Navigation**
    - **Property 13: Splash Screen Visibility During Session Check**
    - **Property 15: Minimum Splash Duration**
    - **Validates: Requirements 4.1, 4.2, 4.3, 4.4, 5.4**

  - [ ] 7.3 Create LoginScreen with validation
    - Create ui/auth/LoginScreen.kt
    - Add CustomTextField for email with validation error display
    - Add CustomTextField for password with visibility toggle
    - Add CustomButton for login with loading state
    - Add "Forgot Password?" link navigating to ForgotPasswordScreen
    - Add "Don't have an account? Sign up" link navigating to SignupScreen
    - Observe AuthViewModel.authState
    - Show LoadingIndicator when state is Loading
    - Show ErrorMessage when state is Error
    - Navigate to Dashboard when state is Authenticated
    - Validate inputs before calling AuthViewModel.login()
    - _Requirements: 1.3, 1.4, 1.5, 1.6, 1.7, 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 13.1, 13.3, 14.1, 14.2, 14.3, 15.1, 15.2, 15.3, 15.4, 15.5_

  - [ ]* 7.4 Write property test for login validation
    - **Property 4: Successful Authentication Navigation**
    - **Property 5: Authentication Error Display**
    - **Property 6: Loading State During Operations**
    - **Property 8: Valid Login Authentication**
    - **Property 27: Validation Before API Calls**
    - **Validates: Requirements 1.5, 1.6, 1.7, 2.1, 2.7, 15.4**

  - [ ] 7.5 Create SignupScreen with validation
    - Create ui/auth/SignupScreen.kt
    - Add CustomTextField for email with validation error display
    - Add CustomTextField for password with visibility toggle and length validation
    - Add CustomButton for signup with loading state
    - Add "Already have an account? Login" link navigating to LoginScreen
    - Observe AuthViewModel.authState
    - Show LoadingIndicator when state is Loading
    - Show ErrorMessage when state is Error
    - Navigate to Dashboard when state is Authenticated
    - Validate inputs before calling AuthViewModel.signup()
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 13.1, 13.3, 14.1, 14.2, 14.3, 15.1, 15.2, 15.3, 15.4, 15.5_

  - [ ]* 7.6 Write property test for signup validation
    - **Property 7: Valid Signup Account Creation**
    - **Validates: Requirements 1.1, 1.2**

  - [ ] 7.7 Create ForgotPasswordScreen
    - Create ui/auth/ForgotPasswordScreen.kt
    - Add CustomTextField for email with validation error display
    - Add CustomButton for "Send Reset Email" with loading state
    - Add "Back to Login" link navigating to LoginScreen
    - Observe AuthViewModel.authState
    - Show LoadingIndicator when state is Loading
    - Show ErrorMessage when state is Error
    - Show success Snackbar when state is Success
    - Validate email before calling AuthViewModel.sendPasswordReset()
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 13.1, 13.3, 14.1, 15.1, 15.4_

  - [ ]* 7.8 Write property test for password reset
    - **Property 9: Password Reset Email**
    - **Validates: Requirements 3.1, 3.2**

  - [ ] 7.9 Wire up AuthNavGraph
    - Update navigation/AuthNavGraph.kt with all auth routes
    - Add composable routes for SplashScreen, LoginScreen, SignupScreen, ForgotPasswordScreen
    - Configure navigation actions between screens
    - _Requirements: 9.2_

  - [ ]* 7.10 Write integration tests for auth flow
    - Test complete login flow
    - Test complete signup flow
    - Test password reset flow
    - Test navigation between auth screens
    - _Requirements: 1.5, 2.2, 3.2, 9.2_

- [ ] 8. Implement main app screens
  - [ ] 8.1 Create DashboardScreen with quick actions
    - Create ui/main/DashboardScreen.kt
    - Display welcome message with user email or display name from DashboardViewModel
    - Create LazyVerticalGrid with 2 columns
    - Display 4 QuickActionCard components (Notes, Attendance, Announcements, Profile)
    - Add bottom navigation bar with Home, Notes, Profile items
    - Highlight current navigation item (Home)
    - Show toast/snackbar when quick action card is tapped indicating Phase 2 placeholder
    - Prevent back navigation to auth screens
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5, 6.6, 7.1, 7.2, 7.3, 9.5_

  - [ ]* 8.2 Write property tests for Dashboard
    - **Property 14: Logout Session Clearing**
    - **Property 16: Dashboard User Display**
    - **Property 17: Quick Action Card Feedback**
    - **Property 18: Navigation Item Selection Highlighting**
    - **Property 23: Dashboard Back Navigation Prevention**
    - **Validates: Requirements 4.5, 6.2, 6.6, 7.2, 9.5**

  - [ ] 8.3 Create NotesPlaceholderScreen
    - Create ui/main/NotesPlaceholderScreen.kt
    - Display "Notes - Coming in Phase 2" message
    - Include bottom navigation bar with Notes item highlighted
    - _Requirements: 7.3, 7.4_

  - [ ] 8.4 Create ProfilePlaceholderScreen
    - Create ui/main/ProfilePlaceholderScreen.kt
    - Display "Profile - Coming in Phase 2" message
    - Include bottom navigation bar with Profile item highlighted
    - _Requirements: 7.3, 7.4_

  - [ ] 8.5 Wire up MainNavGraph with bottom navigation
    - Update navigation/MainNavGraph.kt with Scaffold and BottomNavigation
    - Add composable routes for DashboardScreen, NotesPlaceholderScreen, ProfilePlaceholderScreen
    - Configure bottom navigation item selection and navigation
    - Ensure bottom navigation persists across main screens
    - _Requirements: 7.1, 7.2, 7.5, 9.3_

  - [ ]* 8.6 Write property tests for navigation
    - **Property 19: Bottom Navigation Persistence**
    - **Validates: Requirements 7.5**

  - [ ]* 8.7 Write UI tests for main screens
    - Test Dashboard rendering with user info
    - Test quick action card interactions
    - Test bottom navigation item selection
    - Test navigation between main screens
    - _Requirements: 6.1, 6.2, 6.6, 7.2, 7.5_

- [ ] 9. Integrate MainActivity and complete navigation
  - [ ] 9.1 Create MainActivity with NavHost
    - Create MainActivity.kt extending ComponentActivity
    - Set content with CampusConnectTheme
    - Add NavHost with NavGraph
    - Set startDestination to SPLASH route
    - _Requirements: 9.1, 9.6, 16.1_

  - [ ] 9.2 Complete NavGraph integration
    - Update navigation/NavGraph.kt to include AuthNavGraph and MainNavGraph
    - Configure navigation between auth and main graphs
    - Ensure authenticated users navigate to Dashboard with cleared back stack
    - _Requirements: 1.5, 2.2, 4.2, 6.1, 9.1, 9.2, 9.3, 9.4, 9.5_

  - [ ]* 9.3 Write property tests for navigation flow
    - **Property 4: Successful Authentication Navigation**
    - **Validates: Requirements 1.5, 2.2, 6.1**

  - [ ]* 9.4 Write integration tests for complete app flow
    - Test app launch with no session (navigates to Login)
    - Test app launch with valid session (navigates to Dashboard)
    - Test complete signup → Dashboard flow
    - Test complete login → Dashboard flow
    - Test logout → Login flow
    - _Requirements: 4.1, 4.2, 4.3, 4.5_

- [ ] 10. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 11. Add theme consistency and polish
  - [ ] 11.1 Verify theme application across all screens
    - Test light mode theme on all screens
    - Test dark mode theme on all screens
    - Verify Material 3 styling consistency
    - Verify rounded corners on cards and buttons
    - Verify proper spacing and text hierarchy
    - _Requirements: 8.1, 8.4, 8.5, 8.6, 8.7, 8.8_

  - [ ]* 11.2 Write property tests for theme
    - **Property 20: Theme Consistency**
    - **Property 21: Dark Mode Theme Application**
    - **Property 22: Light Mode Theme Application**
    - **Validates: Requirements 8.4, 8.7, 8.8**

  - [ ] 11.3 Add string resources
    - Update res/values/strings.xml with all app strings
    - Replace hardcoded strings in UI with string resources
    - _Requirements: 16.1_

  - [ ] 11.4 Verify error handling and loading states
    - Test network error scenarios
    - Test Firebase error scenarios
    - Test validation error display
    - Test loading indicator behavior
    - Verify error messages are user-friendly
    - _Requirements: 13.1, 13.2, 13.3, 13.4, 13.5, 14.1, 14.2, 14.3_

  - [ ]* 11.5 Write property tests for error handling
    - **Property 5: Authentication Error Display**
    - **Property 25: Validation Error Field Placement**
    - **Validates: Requirements 13.1, 13.3, 13.5**

  - [ ]* 11.6 Write property tests for loading states
    - **Property 6: Loading State During Operations**
    - **Property 26: Loading State Clearing**
    - **Validates: Requirements 14.1, 14.2, 14.3**

  - [ ]* 11.7 Write property test for real-time validation
    - **Property 28: Real-Time Validation Feedback**
    - **Validates: Requirements 15.5**

- [ ] 12. Final checkpoint and verification
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 13. Final build verification and testing
  - [ ] 13.1 Verify build compatibility
    - Clean and rebuild project
    - Verify no compilation errors
    - Verify no deprecated API usage
    - Test on multiple Android API levels (24+)
    - _Requirements: 16.1, 16.2, 16.3, 16.4_

  - [ ] 13.2 Test on different devices and configurations
    - Test on different screen sizes (phone, tablet)
    - Test light and dark mode switching
    - Test with different system fonts and accessibility settings
    - Test network error scenarios (airplane mode)
    - _Requirements: 8.7, 8.8, 13.2_

  - [ ] 13.3 Verify all acceptance criteria
    - Review all requirements and verify implementation
    - Test all user flows end-to-end
    - Verify all error scenarios are handled
    - Verify all validation rules work correctly
    - _Requirements: All_

  - [ ]* 13.4 Run complete test suite
    - Run all unit tests
    - Run all property-based tests
    - Run all UI tests
    - Run all integration tests
    - Verify 100% test pass rate
    - _Requirements: All_

## Notes

- Tasks marked with `*` are optional testing tasks and can be skipped for faster MVP delivery
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation and provide opportunities to address issues
- Property tests validate universal correctness properties with randomized inputs (minimum 100 iterations)
- Unit tests validate specific examples and edge cases
- The implementation follows a bottom-up approach ensuring build compatibility at every step
- Firebase configuration (google-services.json) must be obtained from Firebase Console before starting
- All code follows Kotlin and Jetpack Compose best practices
- MVVM architecture ensures clear separation of concerns and testability
