# Implementation Plan: Campus Connect Attendance Tracker Module (Phase 3)

## Overview

This implementation plan breaks down the Attendance Tracker Module into incremental, testable tasks following the MVVM architecture established in Phase 1 and Phase 2. The module enables students to track attendance for their subjects, calculate attendance percentages, receive warnings when attendance falls below 75%, and understand how many classes they need to attend to reach the threshold.

The implementation follows a bottom-up approach: data models → repository → ViewModel → UI components → screens → navigation → testing. Each step maintains build compatibility and integrates seamlessly with existing Phase 1 and Phase 2 code.

## Tasks

- [ ] 1. Create data models for attendance tracking
  - [ ] 1.1 Create AttendanceSubject data model
    - Create AttendanceSubject.kt in model package
    - Define data class with fields: id, subjectName, totalClasses, attendedClasses, percentage, lastUpdated, userId
    - Implement toMap() method for Firestore document conversion
    - Implement fromMap() companion method for Firestore document parsing
    - Implement helper methods: getFormattedPercentage(), isLowAttendance(), getFormattedLastUpdated()
    - _Requirements: 21.1, 21.2, 21.3, 21.4, 21.5_
  
  - [ ]* 1.2 Write property test for AttendanceSubject model
    - **Property 31: AttendanceSubject Model Round-trip**
    - **Validates: Requirements 21.4**
  
  - [ ] 1.3 Create AttendanceUiState sealed class
    - Create AttendanceUiState.kt in model package
    - Define sealed class with states: Loading, Success(subjects), Empty(message), Error(message)
    - _Requirements: 11.5_

- [ ] 2. Implement AttendanceRepository for Firestore operations
  - [ ] 2.1 Create AttendanceRepository with method signatures
    - Create AttendanceRepository.kt in repository package
    - Define constructor with FirebaseFirestore parameter
    - Define method signatures: fetchSubjects, observeSubjects, addSubject, updateSubject, deleteSubject
    - Define private helper methods: getFirestorePath, checkDuplicateSubject
    - _Requirements: 11.7, 8.1_
  
  - [ ] 2.2 Implement fetchSubjects method
    - Implement Firestore query to fetch all subjects for user
    - Use collection path: users/{userId}/attendance
    - Order by subjectName ascending
    - Map Firestore documents to AttendanceSubject objects
    - Return Result<List<AttendanceSubject>>
    - _Requirements: 1.8, 8.1_
  
  - [ ] 2.3 Implement observeSubjects method for real-time updates
    - Implement Firestore real-time listener using addSnapshotListener
    - Return Flow<List<AttendanceSubject>>
    - Handle listener errors and emit empty list on error
    - _Requirements: 8.6, 17.1_
  
  - [ ] 2.4 Implement addSubject method with duplicate checking
    - Implement checkDuplicateSubject helper to query existing subjects by name
    - If duplicate exists, return failure with error message
    - Create new document in Firestore with auto-generated ID
    - Set initial values: totalClasses=0, attendedClasses=0, percentage=0.00
    - Set lastUpdated to current timestamp
    - Return Result<AttendanceSubject>
    - _Requirements: 2.5, 2.6, 2.7, 2.8, 8.3_
  
  - [ ] 2.5 Implement updateSubject method
    - Update existing document in Firestore with new totalClasses, attendedClasses, percentage
    - Update lastUpdated field to current timestamp
    - Return Result<Unit>
    - _Requirements: 4.2, 8.4, 8.5_
  
  - [ ] 2.6 Implement deleteSubject method
    - Delete document from Firestore by subject ID
    - Return Result<Unit>
    - _Requirements: 9.4_
  
  - [ ] 2.7 Implement error mapping for Firestore exceptions
    - Map FirebaseFirestoreException types to user-friendly messages
    - Handle PERMISSION_DENIED, UNAVAILABLE, NOT_FOUND, ALREADY_EXISTS, network errors
    - Ensure no technical details or stack traces are exposed
    - _Requirements: 8.7, 15.5, 15.6_
  
  - [ ]* 2.8 Write unit tests for AttendanceRepository
    - Test fetchSubjects with mocked Firestore
    - Test addSubject with duplicate checking
    - Test updateSubject
    - Test deleteSubject
    - Test error mapping
    - _Requirements: 11.7_

- [ ] 3. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 4. Implement AttendanceViewModel for business logic and state management
  - [ ] 4.1 Create AttendanceViewModel with StateFlow properties
    - Create AttendanceViewModel.kt in viewmo
del package
    - Define constructor with AttendanceRepository and AuthRepository parameters
    - Define private _attendanceUiState MutableStateFlow
    - Expose public attendanceUiState StateFlow
    - _Requirements: 11.3, 11.5_
  
  - [ ] 4.2 Implement loadSubjects method
    - Get current user ID from AuthRepository
    - Call AttendanceRepository.observeSubjects to set up real-time listener
    - Collect Flow and update _attendanceUiState
    - Handle empty list case (emit AttendanceUiState.Empty)
    - Handle errors (emit AttendanceUiState.Error)
    - _Requirements: 1.8, 11.6, 17.1_
  
  - [ ] 4.3 Implement addSubject method with validation
    - Validate subject name is not empty using validateSubjectName helper
    - If validation fails, emit AttendanceUiState.Error with validation message
    - Call AttendanceRepository.addSubject
    - Handle success and error cases
    - _Requirements: 2.4, 2.5, 2.6, 2.7, 11.6_
  
  - [ ] 4.4 Implement updateAttendance method with validation
    - Validate totalClasses and attendedClasses using validateAttendanceData helper
    - Check attended does not exceed total
    - If validation fails, emit AttendanceUiState.Error with validation message
    - Calculate new percentage using calculatePercentage
    - Call AttendanceRepository.updateSubject with new values
    - Handle success and error cases
    - _Requirements: 3.8, 3.9, 3.10, 3.11, 4.2, 11.6_
  
  - [ ] 4.5 Implement deleteSubject method
    - Call AttendanceRepository.deleteSubject
    - Handle success and error cases
    - _Requirements: 9.4, 11.6_
  
  - [ ] 4.6 Implement calculatePercentage method
    - Use formula: (attended / total) × 100
    - Handle zero total case (return 0.00)
    - Format result to 2 decimal places
    - _Requirements: 5.1, 5.2, 5.3, 11.6_
  
  - [ ]* 4.7 Write property test for calculatePercentage
    - **Property 12: Percentage Calculation Formula**
    - **Validates: Requirements 5.1**
  
  - [ ]* 4.8 Write property test for zero total classes handling
    - **Property 13: Zero Total Classes Handling**
    - **Validates: Requirements 5.2, 30.1**
  
  - [ ] 4.9 Implement calculateClassesNeeded method
    - Use formula: ceil((0.75 × total - attended) / 0.25)
    - Return 0 if percentage already >= 75%
    - _Requirements: 7.1, 7.2, 11.6_
  
  - [ ]* 4.10 Write property test for calculateClassesNeeded
    - **Property 17: Classes Needed Calculation Formula**
    - **Validates: Requirements 7.1, 7.2**
  
  - [ ] 4.11 Implement validation helper methods
    - Implement validateSubjectName (check not empty, not whitespace only)
    - Implement validateAttendanceData (check non-negative, attended <= total)
    - Return ValidationResult with isValid and errorMessage
    - _Requirements: 2.4, 3.8, 3.9, 3.10, 18.1, 18.4_
  
  - [ ]* 4.12 Write property tests for validation methods
    - **Property 5: Subject Name Validation**
    - **Property 9: Non-Negative Integer Validation**
    - **Property 10: Attended Not Exceeding Total Validation**
    - **Validates: Requirements 2.4, 3.8, 3.9, 3.10**
  
  - [ ]* 4.13 Write unit tests for AttendanceViewModel
    - Test loadSubjects with mocked repository
    - Test addSubject with validation
    - Test updateAttendance with validation
    - Test deleteSubject
    - Test state transformations
    - _Requirements: 11.3, 11.5, 11.6_

- [ ] 5. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 6. Create SubjectCard UI component
  - [ ] 6.1 Create SubjectCard composable
    - Create SubjectCard.kt in components package
    - Define composable with parameters: subject, onClick, onDelete
    - Implement Material 3 Card with rounded corners (12.dp)
    - Implement color-coded background based on percentage (green >= 75%, red < 75%)
    - Display subject name using titleMedium typography
    - Display total classes with label "Total: X"
    - Display attended classes with label "Attended: X"
    - Display percentage with label "X.XX%" using titleLarge typography
    - Implement warning badge for percentage < 75%
    - Implement delete IconButton
    - _Requirements: 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 12.6, 12.7, 23.1, 23.2, 23.3, 23.4, 25.1, 25.2, 25.3, 26.1, 26.2, 26.3, 26.4, 26.5, 27.1, 27.2, 27.3, 27.4, 27.5_
  
  - [ ]* 6.2 Write property test for SubjectCard color coding
    - **Property 3: Green Indicator for Healthy Attendance**
    - **Property 4: Red Indicator for Low Attendance**
    - **Validates: Requirements 1.4, 1.5, 1.6, 6.5, 6.1**
  
  - [ ]* 6.3 Write UI test for SubjectCard rendering
    - Test card displays subject name, total, attended, percentage
    - Test warning badge appears when percentage < 75%
    - Test warning badge does not appear when percentage >= 75%
    - Test delete button is visible and clickable
    - _Requirements: 1.2, 1.6, 6.5, 23.1_

- [ ] 7. Create AddSubjectForm screen
  - [ ] 7.1 Create AddSubjectForm composable
    - Create AddSubjectForm.kt in ui/main package
    - Define composable with parameters: viewModel, onNavigateBack
    - Implement Scaffold with top app bar (back button)
    - Reuse CustomTextField for subject name input
    - Implement validation error display below text field
    - Reuse CustomButton for submit action
    - Reuse LoadingIndicator for loading state
    - Observe ViewModel state and handle success (navigate back), error (display message)
    - _Requirements: 2.2, 2.3, 2.4, 2.6, 2.7, 2.10, 12.1, 16.2_
  
  - [ ]* 7.2 Write UI test for AddSubjectForm
    - Test form displays text field and submit button
    - Test validation error displays for empty subject name
    - Test loading indicator displays during submission
    - Test navigation back on success
    - _Requirements: 2.3, 2.4, 2.6, 16.2_

- [ ] 8. Create AttendanceDetailScreen
  - [ ] 8.1 Create AttendanceDetailScreen composable
    - Create AttendanceDetailScreen.kt in ui/main package
    - Define composable with parameters: subjectId, viewModel, onNavigateBack
    - Implement Scaffold with top app bar (subject name, back button, delete button)
    - Display current attendance percentage prominently
    - Implement WarningCard composable for percentage < 75% with classes needed message
    - Reuse CustomTextField for total classes input (numeric keyboard)
    - Reuse CustomTextField for attended classes input (numeric keyboard)
    - Implement real-time percentage calculation using remember and derivedStateOf
    - Implement real-time classes needed calculation
    - Display calculated percentage and classes needed message dynamically
    - Reuse CustomButton for update action
    - Reuse LoadingIndicator for loading state
    - Implement validation error display
    - Observe ViewModel state and handle success (navigate back), error (display message)
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.11, 4.1, 4.6, 5.4, 5.5, 6.2, 6.6, 7.3, 7.4, 7.5, 16.3, 23.5, 28.1, 28.2, 28.3, 28.4, 28.5_
  
  - [ ] 8.2 Create WarningCard composable
    - Define composable with parameters: message, classesNeeded
    - Implement Material 3 Card with errorContainer background
    - Display warning icon and message
    - Display classes needed message if classesNeeded > 0
    - Use error color styling consistent with Material 3
    - _Requirements: 6.2, 6.3, 6.4, 7.3_
  
  - [ ]* 8.3 Write property test for real-time percentage update
    - **Property 14: Real-time Percentage Update**
    - **Validates: Requirements 5.4, 5.5, 28.1, 28.2**
  
  - [ ]* 8.4 Write property test for real-time classes needed update
    - **Property 19: Real-time Classes Needed Update**
    - **Validates: Requirements 7.5, 28.4**
  
  - [ ]* 8.5 Write UI test for AttendanceDetailScreen
    - Test screen displays current stats
    - Test input fields accept numeric input
    - Test percentage updates in real-time as user types
    - Test warning card appears when percentage < 75%
    - Test classes needed message displays correctly
    - Test update button triggers ViewModel method
    - _Requirements: 3.2, 3.3, 3.4, 3.5, 5.4, 6.2, 7.3, 28.1, 28.2_

- [ ] 9. Create AttendanceScreen
  - [ ] 9.1 Create AttendanceScreen composable
    - Create AttendanceScreen.kt in ui/main package
    - Define composable with parameters: viewModel, navigation callbacks
    - Implement Scaffold with top app bar, FAB, bottom navigation
    - Observe attendanceUiState from ViewModel
    - Implement Loading state: display LoadingIndicator (reused from Phase 1)
    - Implement Success state: display LazyColumn with SubjectCards
    - Implement Empty state: display EmptyState component (reused from Phase 2) with message and action button
    - Implement Error state: display error message with retry button
    - Implement FAB for add subject navigation
    - Integrate bottom navigation bar from Phase 1
    - Handle SubjectCard onClick (navigate to detail screen)
    - Handle SubjectCard onDelete (show confirmation dialog, call ViewModel.deleteSubject)
    - _Requirements: 1.1, 1.8, 1.9, 1.10, 2.1, 9.1, 9.2, 9.3, 9.5, 9.7, 10.1, 10.2, 10.3, 10.4, 12.1, 12.2, 12.3, 12.4, 12.5, 13.6, 16.1, 16.4_
  
  - [ ] 9.2 Create confirmation dialog for subject deletion
    - Implement Material 3 AlertDialog
    - Display message "Are you sure you want to delete this subject?"
    - Provide Cancel and Delete buttons
    - Use destructive color styling for Delete button
    - _Requirements: 9.2, 9.3, 24.1, 24.2, 24.3, 24.4, 24.5_
  
  - [ ]* 9.3 Write property test for subject card rendering
    - **Property 1: Subject Card Rendering**
    - **Validates: Requirements 1.1, 1.2**
  
  - [ ]* 9.4 Write UI test for AttendanceScreen
    - Test loading state displays LoadingIndicator
    - Test empty state displays EmptyState component
    - Test success state displays list of SubjectCards
    - Test error state displays error message
    - Test FAB navigates to AddSubjectForm
    - Test SubjectCard click navigates to AttendanceDetailScreen
    - Test delete button shows confirmation dialog
    - _Requirements: 1.1, 1.8, 1.9, 1.10, 2.1, 9.2, 10.1_

- [ ] 10. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 11. Integrate attendance module into navigation
  - [ ] 11.1 Update Routes.kt with attendance routes
    - Add ATTENDANCE constant: "attendance"
    - Add ADD_SUBJECT constant: "add_subject"
    - Add ATTENDANCE_DETAIL constant: "attendance_detail/{subjectId}"
    - Add helper function to create attendance detail route with subject ID parameter
    - _Requirements: 13.3, 13.4, 13.5_
  
  - [ ] 11.2 Update MainNavGraph.kt with attendance routes
    - Add composable for AttendanceScreen route
    - Add composable for AddSubjectForm route
    - Add composable for AttendanceDetailScreen route with subject ID parameter extraction
    - Wire up navigation callbacks for all screens
    - Ensure bottom navigation remains visible on AttendanceScreen
    - Ensure bottom navigation is hidden on AddSubjectForm and AttendanceDetailScreen
    - _Requirements: 13.3, 13.4, 13.5, 13.6, 13.7, 13.8_
  
  - [ ]* 11.3 Write navigation tests
    - Test navigation from Dashboard to AttendanceScreen
    - Test navigation from AttendanceScreen to AddSubjectForm
    - Test navigation from AttendanceScreen to AttendanceDetailScreen
    - Test navigation back after successful operations
    - Test bottom navigation visibility
    - _Requirements: 13.1, 13.2, 13.7, 13.8_

- [ ] 12. Update Dashboard with Attendance quick action card
  - [ ] 12.1 Modify DashboardScreen.kt
    - Add Attendance quick action card using QuickActionCard component from Phase 1
    - Use appropriate icon (e.g., Icons.Default.EventNote or Icons.Default.Checklist)
    - Set title to "Attendance"
    - Set description to "Track your class attendance"
    - Wire up onClick to navigate to AttendanceScreen
    - Position card consistently with other quick action cards
    - _Requirements: 13.1, 13.2, 29.1, 29.2, 29.3, 29.4, 29.5, 29.6_
  
  - [ ]* 12.2 Write UI test for Dashboard attendance card
    - Test Attendance card is displayed
    - Test Attendance card click navigates to AttendanceScreen
    - _Requirements: 13.1, 13.2, 29.1_

- [ ] 13. Add attendance-related strings to resources
  - [ ] 13.1 Update strings.xml
    - Add string resources for all attendance-related UI text
    - Include: screen titles, button labels, error messages, validation messages, empty state message, confirmation dialog text, snackbar messages
    - Ensure consistency with Phase 1 and Phase 2 string naming conventions
    - _Requirements: 14.1, 14.2, 15.1, 15.2, 15.3, 15.4_

- [ ] 14. Implement snackbar feedback for operations
  - [ ] 14.1 Add snackbar display logic to AttendanceScreen
    - Display success snackbar "Subject added successfully" after add operation
    - Display success snackbar "Attendance updated successfully" after update operation
    - Display success snackbar "Subject deleted successfully" after delete operation
    - Display error snackbar with error message on operation failure
    - Auto-dismiss snackbars after 4 seconds (consistent with Phase 1 and Phase 2)
    - _Requirements: 4.3, 9.5, 19.1, 19.2, 19.3, 19.4, 19.5_

- [ ] 15. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 16. Write comprehensive property-based tests
  - [ ]* 16.1 Create AttendanceValidationPropertyTests.kt
    - Write property test for subject name validation with random strings
    - Write property test for duplicate subject validation with random subject lists
    - Write property test for non-negative integer validation with random integers
    - Write property test for attended not exceeding total validation with random pairs
    - Write property test for numeric input validation with random strings
    - Tag each test with feature and property reference
    - Use minimum 100 iterations per test
    - _Requirements: 2.4, 2.5, 3.8, 3.9, 3.10, 18.1, 18.2, 18.3, 18.4_
  
  - [ ]* 16.2 Create AttendanceCalculationPropertyTests.kt
    - Write property test for percentage calculation with random valid inputs
    - Write property test for classes needed calculation with random inputs below 75%
    - Write property test for zero total classes edge case
    - Write property test for real-time recalculation on input change
    - Tag each test with feature and property reference
    - Use minimum 100 iterations per test
    - _Requirements: 5.1, 5.2, 7.1, 7.2, 28.1, 28.2, 28.4_
  
  - [ ]* 16.3 Create custom Kotest generators
    - Implement Arb.attendanceSubject() generator
    - Implement Arb.lowAttendanceSubject() generator (percentage < 75%)
    - Implement Arb.healthyAttendanceSubject() generator (percentage >= 75%)
    - Use generators in property tests for comprehensive coverage

- [ ] 17. Write integration tests
  - [ ]* 17.1 Create AttendanceFlowIntegrationTest.kt
    - Test end-to-end flow: add subject → update attendance → delete subject
    - Test navigation flow through all screens
    - Test real-time updates across screens
    - Test error handling in complete flow
    - _Requirements: 13.7, 13.8, 17.2, 17.3, 17.4_
  
  - [ ]* 17.2 Create RealtimeUpdateTest.kt
    - Test subject addition triggers UI update without manual refresh
    - Test subject update triggers UI update without manual refresh
    - Test subject deletion triggers UI update without manual refresh
    - Test real-time listener integration with Firestore
    - _Requirements: 17.1, 17.2, 17.3, 17.4, 17.5_

- [ ] 18. Final testing and polish
  - [ ] 18.1 Test add subject flow with various names
    - Test with valid subject names
    - Test with empty subject name (validation error)
    - Test with duplicate subject name (validation error)
    - Test with whitespace-only subject name (validation error)
    - Verify error messages display correctly
    - _Requirements: 2.4, 2.5, 2.6, 2.7_
  
  - [ ] 18.2 Test update attendance with various values
    - Test with valid attendance data
    - Test with attended exceeding total (validation error)
    - Test with negative values (validation error)
    - Test with zero total classes (percentage = 0.00)
    - Test real-time percentage and classes needed updates
    - Verify warning card appears/disappears correctly
    - _Requirements: 3.8, 3.9, 3.10, 3.11, 5.2, 5.4, 6.2, 6.6, 7.3, 7.4, 28.1, 28.2, 28.4_
  
  - [ ] 18.3 Test delete subject with confirmation
    - Test confirmation dialog appears
    - Test cancel button keeps subject
    - Test delete button removes subject
    - Test success snackbar displays
    - Test real-time UI update after deletion
    - _Requirements: 9.2, 9.3, 9.4, 9.5, 17.4_
  
  - [ ] 18.4 Test real-time updates
    - Test subject list updates when subject added
    - Test subject list updates when subject updated
    - Test subject list updates when subject deleted
    - Verify no manual refresh needed
    - _Requirements: 17.1, 17.2, 17.3, 17.4, 17.5_
  
  - [ ] 18.5 Test percentage and classes needed calculations
    - Test percentage calculation with various inputs
    - Test classes needed calculation for low attendance
    - Test zero total classes handling
    - Test real-time calculation updates
    - Verify formulas are correct
    - _Requirements: 5.1, 5.2, 5.3, 7.1, 7.2, 7.3, 28.1, 28.2, 28.4_
  
  - [ ] 18.6 Test color coding and warning badges
    - Test green indicator for percentage >= 75%
    - Test red indicator for percentage < 75%
    - Test warning badge appears for low attendance
    - Test warning badge does not appear for healthy attendance
    - Test color indicators work in light and dark mode
    - _Requirements: 1.4, 1.5, 1.6, 6.1, 6.5, 14.4, 14.5, 27.1, 27.2, 27.3, 27.4, 27.5_
  
  - [ ] 18.7 Test error scenarios
    - Test network error handling (display error message)
    - Test Firestore unavailable error
    - Test permission denied error
    - Test validation errors display correctly
    - Verify no technical details or stack traces exposed
    - _Requirements: 8.7, 15.1, 15.2, 15.3, 15.4, 15.5, 15.6_
  
  - [ ] 18.8 Test on different screen sizes
    - Test on phone (small screen)
    - Test on tablet (large screen)
    - Verify layouts adapt correctly
    - Verify text remains readable
    - _Requirements: 14.1, 14.2, 14.3_
  
  - [ ] 18.9 Test light and dark mode
    - Test all screens in light mode
    - Test all screens in dark mode
    - Verify color indicators work in both modes
    - Verify text contrast is sufficient
    - Verify theme consistency with Phase 1 and Phase 2
    - _Requirements: 14.1, 14.2, 14.6, 27.4_
  
  - [ ] 18.10 Verify all acceptance criteria
    - Review requirements document
    - Verify each acceptance criterion is met
    - Document any deviations or issues
    - _Requirements: All_
  
  - [ ] 18.11 Final build and deployment preparation
    - Run full test suite (unit tests, property tests, UI tests, integration tests)
    - Verify no compilation errors or warnings
    - Verify app builds successfully
    - Test on physical device
    - Verify Phase 1 and Phase 2 features still work correctly
    - Prepare for deployment
    - _Requirements: 20.1, 20.2, 20.3, 20.4, 20.5, 20.6_

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation
- Property tests validate universal correctness properties
- Unit tests validate specific examples and edge cases
- The implementation follows a bottom-up approach: models → repository → ViewModel → components → screens → navigation → testing
- Build compatibility is maintained at every step
- All existing Phase 1 and Phase 2 components are reused where possible
- The module integrates seamlessly with existing navigation and theme systems
