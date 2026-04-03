# Requirements Document

## Introduction

This document specifies the requirements for Phase 3 of Campus Connect: the Attendance Tracker Module. This feature enables students to track attendance for their subjects, calculate attendance percentages, receive warnings when attendance falls below 75%, and understand how many classes they need to attend to reach the threshold. The module integrates with Firestore for data storage and follows the existing MVVM architecture, reuses components from Phase 1 and Phase 2, and maintains consistency with the Material 3 design system.

## Glossary

- **Attendance_Module**: The complete attendance tracking feature including subject management, attendance recording, and warning system
- **Attendance_Screen**: The main screen displaying the list of subjects with attendance data
- **Subject**: An academic course with associated attendance data (subject name, total classes, attended classes, percentage)
- **Subject_Card**: A UI component displaying subject attendance information in a Material 3 card
- **Attendance_Detail_Screen**: The screen for viewing and updating attendance for a specific subject
- **Add_Subject_Form**: The screen for adding a new subject to track
- **Attendance_Percentage**: The calculated ratio of attended classes to total classes expressed as a percentage
- **Warning_Threshold**: The 75% attendance threshold below which warnings are displayed
- **Warning_Badge**: A visual indicator on subject cards showing attendance is below threshold
- **Classes_Needed_Calculator**: The logic that calculates how many additional classes must be attended to reach 75%
- **Attendance_Repository**: The data access layer handling Firestore operations for attendance data
- **Attendance_ViewModel**: The business logic layer managing attendance state and operations
- **Firestore**: The NoSQL database for storing attendance data
- **Empty_State**: The UI displayed when no subjects exist
- **Loading_State**: The UI displayed during asynchronous operations
- **Error_State**: The UI displayed when operations fail

## Requirements

### Requirement 1: Attendance Screen Display

**User Story:** As a student, I want to see a list of my subjects with attendance data, so that I can monitor my attendance at a glance.

#### Acceptance Criteria

1. THE Attendance_Screen SHALL display a list of subjects as Subject_Cards in a scrollable list
2. THE Subject_Card SHALL display subject name, total classes, attended classes, and attendance percentage
3. THE Subject_Card SHALL display the attendance percentage with 2 decimal places
4. WHEN the attendance percentage is greater than or equal to 75%, THE Subject_Card SHALL display a green visual indicator
5. WHEN the attendance percentage is less than 75%, THE Subject_Card SHALL display a red visual indicator
6. WHEN the attendance percentage is less than 75%, THE Subject_Card SHALL display a warning badge or icon
7. THE Subject_Card SHALL use Material 3 card styling with rounded corners consistent with Phase 1 and Phase 2 design
8. WHEN the Attendance_Screen loads, THE Attendance_ViewModel SHALL fetch all subjects from Firestore
9. WHILE subjects are loading, THE Attendance_Screen SHALL display a loading indicator
10. WHEN no subjects exist, THE Attendance_Screen SHALL display an empty state

### Requirement 2: Add Subject

**User Story:** As a student, I want to add new subjects to track, so that I can monitor attendance for all my courses.

#### Acceptance Criteria

1. THE Attendance_Screen SHALL display a floating action button for adding new subjects
2. WHEN the add subject button is tapped, THE Navigation_Controller SHALL navigate to the Add_Subject_Form
3. THE Add_Subject_Form SHALL provide a text field for entering the subject name
4. THE Add_Subject_Form SHALL validate that the subject name is not empty
5. THE Add_Subject_Form SHALL validate that the subject name does not duplicate an existing subject
6. WHEN validation fails for empty subject name, THE Add_Subject_Form SHALL display an error message "Subject name is required"
7. WHEN validation fails for duplicate subject name, THE Add_Subject_Form SHALL display an error message "A subject with this name already exists"
8. WHEN the user submits a valid subject name, THE Attendance_Repository SHALL save the subject to Firestore
9. WHEN the subject is saved successfully, THE Navigation_Controller SHALL navigate back to the Attendance_Screen
10. WHEN the save operation fails, THE Add_Subject_Form SHALL display an error message

### Requirement 3: Enter Attendance Data

**User Story:** As a student, I want to enter attendance data for my subjects, so that I can track how many classes I've attended.

#### Acceptance Criteria

1. WHEN a Subject_Card is tapped, THE Navigation_Controller SHALL navigate to the Attendance_Detail_Screen for that subject
2. THE Attendance_Detail_Screen SHALL display the current subject name
3. THE Attendance_Detail_Screen SHALL display the current total classes count
4. THE Attendance_Detail_Screen SHALL display the current attended classes count
5. THE Attendance_Detail_Screen SHALL display the current attendance percentage
6. THE Attendance_Detail_Screen SHALL provide an input field for total classes
7. THE Attendance_Detail_Screen SHALL provide an input field for attended classes
8. THE Attendance_Detail_Screen SHALL validate that total classes is a non-negative integer
9. THE Attendance_Detail_Screen SHALL validate that attended classes is a non-negative integer
10. THE Attendance_Detail_Screen SHALL validate that attended classes does not exceed total classes
11. WHEN validation fails for attended exceeding total, THE Attendance_Detail_Screen SHALL display an error message "Attended classes cannot exceed total classes"

### Requirement 4: Update Attendance Data

**User Story:** As a student, I want to update attendance data for my subjects, so that I can keep my records current.

#### Acceptance Criteria

1. THE Attendance_Detail_Screen SHALL provide an update button to save changes
2. WHEN the user taps the update button with valid data, THE Attendance_Repository SHALL update the subject in Firestore
3. WHEN the update succeeds, THE Attendance_Screen SHALL display a success snackbar "Attendance updated successfully"
4. WHEN the update succeeds, THE Navigation_Controller SHALL navigate back to the Attendance_Screen
5. WHEN the update fails, THE Attendance_Detail_Screen SHALL display an error message
6. WHILE the update is in progress, THE Attendance_Detail_Screen SHALL display a loading indicator and disable the update button

### Requirement 5: Attendance Percentage Calculation

**User Story:** As a student, I want my attendance percentage calculated automatically, so that I don't have to do the math myself.

#### Acceptance Criteria

1. THE Attendance_ViewModel SHALL calculate attendance percentage using the formula: (attended / total) × 100
2. WHEN total classes is zero, THE Attendance_ViewModel SHALL set attendance percentage to 0.00
3. THE Attendance_ViewModel SHALL format the attendance percentage with exactly 2 decimal places
4. WHEN the user types in the attended or total classes fields, THE Attendance_Detail_Screen SHALL calculate and display the percentage in real-time
5. THE Attendance_ViewModel SHALL update the percentage whenever attended or total classes values change

### Requirement 6: Warning System for Low Attendance

**User Story:** As a student, I want to see warnings when my attendance is below 75%, so that I know which subjects need attention.

#### Acceptance Criteria

1. WHEN the attendance percentage is less than 75%, THE Subject_Card SHALL display a warning badge
2. WHEN the attendance percentage is less than 75%, THE Attendance_Detail_Screen SHALL display a warning message
3. THE Warning_Message SHALL use red or warning color styling consistent with Material 3 design
4. THE Warning_Badge SHALL be visually distinct and easily noticeable on the Subject_Card
5. WHEN the attendance percentage is greater than or equal to 75%, THE Subject_Card SHALL not display a warning badge
6. WHEN the attendance percentage is greater than or equal to 75%, THE Attendance_Detail_Screen SHALL not display a warning message

### Requirement 7: Classes Needed Calculation

**User Story:** As a student, I want to know how many classes I need to attend to reach 75%, so that I can plan my attendance.

#### Acceptance Criteria

1. WHEN the attendance percentage is less than 75%, THE Classes_Needed_Calculator SHALL calculate the number of classes needed to reach 75%
2. THE Classes_Needed_Calculator SHALL use the formula: ceil((0.75 × total - attended) / 0.25)
3. WHEN the attendance percentage is less than 75%, THE Attendance_Detail_Screen SHALL display a message "Attend X more classes to reach 75%"
4. WHEN the attendance percentage is greater than or equal to 75%, THE Attendance_Detail_Screen SHALL not display the classes needed message
5. THE Classes_Needed_Message SHALL update in real-time as the user changes attended or total classes values

### Requirement 8: Firestore Integration

**User Story:** As a developer, I want attendance data stored in Firestore, so that data persists across sessions and devices.

#### Acceptance Criteria

1. THE Attendance_Repository SHALL store attendance data in Firestore using the collection structure: users/{userId}/attendance/{subjectId}
2. THE Attendance_Document SHALL include fields: subjectName, totalClasses, attendedClasses, percentage, lastUpdated
3. WHEN a subject is added, THE Attendance_Repository SHALL create a new document in Firestore
4. WHEN attendance is updated, THE Attendance_Repository SHALL update the existing document in Firestore
5. THE Attendance_Repository SHALL update the lastUpdated field with the current timestamp on every update
6. THE Attendance_Repository SHALL use Firestore real-time listeners to update the UI when data changes
7. WHEN Firestore operations fail, THE Attendance_Repository SHALL return appropriate error messages

### Requirement 9: Delete Subject

**User Story:** As a student, I want to delete subjects I no longer need to track, so that I can keep my list organized.

#### Acceptance Criteria

1. THE Subject_Card SHALL display a delete button or icon
2. WHEN the delete button is tapped, THE Attendance_Screen SHALL display a confirmation dialog
3. THE Confirmation_Dialog SHALL ask "Are you sure you want to delete this subject?" with Cancel and Delete options
4. WHEN the user confirms deletion, THE Attendance_Repository SHALL delete the subject document from Firestore
5. WHEN the deletion succeeds, THE Attendance_Screen SHALL display a success snackbar "Subject deleted successfully"
6. WHEN the deletion fails, THE Attendance_Screen SHALL display an error message and keep the subject in the list
7. WHILE the deletion is in progress, THE Attendance_Screen SHALL display a loading indicator

### Requirement 10: Empty State

**User Story:** As a student, I want helpful guidance when I have no subjects, so that I know how to get started.

#### Acceptance Criteria

1. WHEN no subjects exist, THE Attendance_Screen SHALL display an empty state using the EmptyState component
2. THE Empty_State SHALL display the message "No subjects added yet. Tap + to add your first subject"
3. THE Empty_State SHALL display an icon or illustration consistent with Phase 2 empty states
4. THE Empty_State SHALL provide an action button to add a subject
5. WHEN the action button is tapped, THE Navigation_Controller SHALL navigate to the Add_Subject_Form

### Requirement 11: Architecture Consistency

**User Story:** As a developer, I want the Attendance Module to follow the existing MVVM architecture, so that the codebase remains consistent and maintainable.

#### Acceptance Criteria

1. THE Attendance_Module SHALL follow the MVVM pattern established in Phase 1 and Phase 2
2. THE Attendance_Module SHALL create an AttendanceRepository in the repository package
3. THE Attendance_Module SHALL create an AttendanceViewModel in the viewmodel package
4. THE Attendance_Module SHALL create an AttendanceSubject data model in the model package
5. THE Attendance_ViewModel SHALL expose StateFlow for attendance state management
6. THE Attendance_ViewModel SHALL provide methods: loadSubjects, addSubject, updateAttendance, deleteSubject, calculatePercentage, calculateClassesNeeded
7. THE Attendance_Repository SHALL provide methods: fetchSubjects, addSubject, updateSubject, deleteSubject, observeSubjects
8. THE Attendance_Module SHALL use the existing package structure from Phase 1 and Phase 2

### Requirement 12: Component Reuse

**User Story:** As a developer, I want to reuse existing components, so that the UI is consistent and development is efficient.

#### Acceptance Criteria

1. THE Attendance_Module SHALL reuse CustomTextField from Phase 1 for input fields
2. THE Attendance_Module SHALL reuse CustomButton from Phase 1 for action buttons
3. THE Attendance_Module SHALL reuse LoadingIndicator from Phase 1 for loading states
4. THE Attendance_Module SHALL reuse ErrorMessage from Phase 1 for error display
5. THE Attendance_Module SHALL reuse EmptyState from Phase 2 for empty state display
6. THE Attendance_Module SHALL create a new Subject_Card component in the components package
7. THE Subject_Card SHALL follow Material 3 design patterns consistent with NoteCard from Phase 2

### Requirement 13: Navigation Integration

**User Story:** As a student, I want to access the Attendance Tracker from the Dashboard, so that I can navigate to it easily.

#### Acceptance Criteria

1. THE Dashboard SHALL display an Attendance quick action card
2. WHEN the Attendance quick action card is tapped, THE Navigation_Controller SHALL navigate to the Attendance_Screen
3. THE Attendance_Screen SHALL be added as a new route in the navigation system
4. THE Add_Subject_Form SHALL be added as a new route in the navigation system
5. THE Attendance_Detail_Screen SHALL be added as a new route in the navigation system
6. THE Bottom_Navigation SHALL remain visible on the Attendance_Screen
7. THE Navigation_Controller SHALL support navigating from Attendance_Screen to Add_Subject_Form and back
8. THE Navigation_Controller SHALL support navigating from Attendance_Screen to Attendance_Detail_Screen and back

### Requirement 14: Theme Consistency

**User Story:** As a student, I want the Attendance Module to match the existing design, so that the app feels cohesive.

#### Acceptance Criteria

1. THE Attendance_Module SHALL use the CampusConnectTheme from Phase 1
2. THE Attendance_Module SHALL use colors, typography, and spacing from the existing theme system
3. THE Subject_Card SHALL use rounded corners consistent with Phase 1 and Phase 2 cards
4. THE Attendance_Module SHALL use green color for attendance percentage greater than or equal to 75%
5. THE Attendance_Module SHALL use red or warning color for attendance percentage less than 75%
6. THE Attendance_Module SHALL support both light and dark mode using the existing theme system
7. THE Warning_Badge SHALL use Material 3 badge components with appropriate styling

### Requirement 15: Error Handling

**User Story:** As a student, I want clear error messages when operations fail, so that I understand what went wrong.

#### Acceptance Criteria

1. WHEN a Firestore operation fails due to network error, THE Attendance_Screen SHALL display "Network error. Please check your connection and try again"
2. WHEN a subject name is empty, THE Add_Subject_Form SHALL display "Subject name is required"
3. WHEN a subject name is duplicate, THE Add_Subject_Form SHALL display "A subject with this name already exists"
4. WHEN attended classes exceeds total classes, THE Attendance_Detail_Screen SHALL display "Attended classes cannot exceed total classes"
5. WHEN a Firestore operation fails, THE Attendance_Screen SHALL display a user-friendly error message using the ErrorMessage component
6. THE Error_Messages SHALL not expose technical implementation details or stack traces

### Requirement 16: Loading States

**User Story:** As a student, I want to see loading indicators during operations, so that I know the app is working.

#### Acceptance Criteria

1. WHILE subjects are being fetched, THE Attendance_Screen SHALL display a loading indicator using the LoadingIndicator component
2. WHILE a subject is being added, THE Add_Subject_Form SHALL display a loading indicator and disable the submit button
3. WHILE attendance is being updated, THE Attendance_Detail_Screen SHALL display a loading indicator and disable the update button
4. WHILE a subject is being deleted, THE Attendance_Screen SHALL display a loading indicator
5. WHEN an operation completes, THE Attendance_Module SHALL hide loading indicators and re-enable UI elements

### Requirement 17: Real-time Updates

**User Story:** As a student, I want my attendance list to update automatically when I add or update subjects, so that I always see the current state.

#### Acceptance Criteria

1. THE Attendance_Repository SHALL use Firestore real-time listeners for the attendance collection
2. WHEN a subject is added to Firestore, THE Attendance_Screen SHALL automatically display the new subject without manual refresh
3. WHEN a subject is updated in Firestore, THE Attendance_Screen SHALL automatically update the subject card without manual refresh
4. WHEN a subject is deleted from Firestore, THE Attendance_Screen SHALL automatically remove the subject from the list without manual refresh
5. THE Real-time_Updates SHALL work seamlessly with the existing UI state management

### Requirement 18: Input Validation

**User Story:** As a student, I want immediate feedback on invalid inputs, so that I can correct mistakes before submission.

#### Acceptance Criteria

1. WHEN the subject name field is empty on submission, THE Add_Subject_Form SHALL display a validation error
2. WHEN the total classes field contains non-numeric characters, THE Attendance_Detail_Screen SHALL prevent input or display a validation error
3. WHEN the attended classes field contains non-numeric characters, THE Attendance_Detail_Screen SHALL prevent input or display a validation error
4. WHEN attended classes exceeds total classes, THE Attendance_Detail_Screen SHALL display a validation error immediately
5. THE Validation_System SHALL execute validation before making Firestore API calls

### Requirement 19: Snackbar Feedback

**User Story:** As a student, I want snackbar feedback for operations, so that I know when actions succeed or fail.

#### Acceptance Criteria

1. WHEN a subject is added successfully, THE Attendance_Screen SHALL display a success snackbar "Subject added successfully"
2. WHEN attendance is updated successfully, THE Attendance_Screen SHALL display a success snackbar "Attendance updated successfully"
3. WHEN a subject is deleted successfully, THE Attendance_Screen SHALL display a success snackbar "Subject deleted successfully"
4. WHEN an operation fails, THE Attendance_Screen SHALL display an error snackbar with the error message
5. THE Snackbar SHALL auto-dismiss after 4 seconds consistent with Phase 1 and Phase 2

### Requirement 20: Build Compatibility

**User Story:** As a developer, I want the Attendance Module to integrate without breaking the existing app, so that Phase 1 and Phase 2 features continue to work.

#### Acceptance Criteria

1. THE Attendance_Module SHALL work within the existing Android Studio project structure
2. THE Attendance_Module SHALL not break the existing Gradle setup
3. THE Attendance_Module SHALL not use deprecated Android APIs
4. THE Attendance_Module SHALL follow the existing package structure from Phase 1 and Phase 2
5. THE Attendance_Module SHALL maintain the existing MVVM architecture patterns
6. THE Attendance_Module SHALL keep the app buildable at every implementation step

### Requirement 21: AttendanceSubject Data Model

**User Story:** As a developer, I want an AttendanceSubject data model, so that attendance data is structured and type-safe.

#### Acceptance Criteria

1. THE AttendanceSubject_Model SHALL be created in the model package following Phase 1 and Phase 2 patterns
2. THE AttendanceSubject_Model SHALL include fields: id, subjectName, totalClasses, attendedClasses, percentage, lastUpdated, userId
3. THE AttendanceSubject_Model SHALL be a Kotlin data class
4. THE AttendanceSubject_Model SHALL support conversion to and from Firestore document format
5. THE AttendanceSubject_Model SHALL provide helper methods for calculating percentage and classes needed

### Requirement 22: Skeleton Loading

**User Story:** As a student, I want skeleton loading for subject cards, so that the loading experience feels smooth and modern.

#### Acceptance Criteria

1. WHILE subjects are being fetched for the first time, THE Attendance_Screen SHALL display skeleton loading cards
2. THE Skeleton_Cards SHALL match the size and layout of actual Subject_Cards
3. THE Skeleton_Cards SHALL use a shimmer animation effect consistent with Phase 2
4. WHEN subjects finish loading, THE Skeleton_Cards SHALL be replaced with actual Subject_Cards with a smooth transition

### Requirement 23: Delete Button Placement

**User Story:** As a student, I want easy access to delete functionality, so that I can remove subjects quickly.

#### Acceptance Criteria

1. THE Subject_Card SHALL display a delete icon button
2. THE Delete_Button SHALL be positioned consistently on all Subject_Cards
3. THE Delete_Button SHALL use a delete or trash icon from Material Icons
4. THE Delete_Button SHALL provide visual feedback when tapped
5. WHERE the delete button is on the Attendance_Detail_Screen, THE Delete_Button SHALL be positioned in the top app bar or at the bottom of the screen

### Requirement 24: Confirmation Dialog Styling

**User Story:** As a student, I want confirmation dialogs to match the app design, so that the experience is consistent.

#### Acceptance Criteria

1. THE Confirmation_Dialog SHALL use Material 3 AlertDialog component
2. THE Confirmation_Dialog SHALL use colors and typography from the CampusConnectTheme
3. THE Confirmation_Dialog SHALL display Cancel and Delete buttons with appropriate styling
4. THE Delete_Button SHALL use destructive color styling (red or error color)
5. THE Cancel_Button SHALL use neutral color styling

### Requirement 25: Subject Name Display

**User Story:** As a student, I want subject names displayed prominently, so that I can quickly identify subjects.

#### Acceptance Criteria

1. THE Subject_Card SHALL display the subject name using Material 3 titleMedium typography
2. THE Subject_Name SHALL be the most prominent text element on the Subject_Card
3. THE Subject_Name SHALL be truncated with ellipsis if it exceeds the card width
4. THE Attendance_Detail_Screen SHALL display the subject name in the top app bar
5. THE Subject_Name SHALL use consistent text styling across all screens

### Requirement 26: Attendance Statistics Display

**User Story:** As a student, I want to see attendance statistics clearly, so that I can understand my attendance status at a glance.

#### Acceptance Criteria

1. THE Subject_Card SHALL display total classes count with a label "Total: X"
2. THE Subject_Card SHALL display attended classes count with a label "Attended: X"
3. THE Subject_Card SHALL display attendance percentage with a label "X.XX%"
4. THE Attendance_Statistics SHALL use consistent text styling and spacing
5. THE Attendance_Percentage SHALL be the most visually prominent statistic on the card

### Requirement 27: Color-Coded Visual Indicators

**User Story:** As a student, I want color-coded indicators, so that I can quickly identify subjects with low attendance.

#### Acceptance Criteria

1. WHEN the attendance percentage is greater than or equal to 75%, THE Subject_Card SHALL use green color for the percentage text or background indicator
2. WHEN the attendance percentage is less than 75%, THE Subject_Card SHALL use red color for the percentage text or background indicator
3. THE Color_Indicators SHALL be consistent across all Subject_Cards
4. THE Color_Indicators SHALL be accessible and distinguishable in both light and dark mode
5. THE Color_Indicators SHALL follow Material 3 color system guidelines

### Requirement 28: Real-time Percentage Update

**User Story:** As a student, I want the percentage to update as I type, so that I can see the impact of my changes immediately.

#### Acceptance Criteria

1. WHEN the user types in the total classes field, THE Attendance_Detail_Screen SHALL recalculate and display the percentage in real-time
2. WHEN the user types in the attended classes field, THE Attendance_Detail_Screen SHALL recalculate and display the percentage in real-time
3. THE Real-time_Calculation SHALL not trigger Firestore updates until the user taps the update button
4. THE Real-time_Calculation SHALL update the warning message and classes needed message dynamically
5. THE Real-time_Calculation SHALL provide smooth visual feedback without lag

### Requirement 29: Dashboard Quick Action Integration

**User Story:** As a student, I want an Attendance quick action card on the Dashboard, so that I can access attendance tracking easily.

#### Acceptance Criteria

1. THE Dashboard SHALL display an Attendance quick action card using the QuickActionCard component from Phase 1
2. THE Attendance_Quick_Action_Card SHALL display an appropriate icon (e.g., calendar, checklist, or attendance icon)
3. THE Attendance_Quick_Action_Card SHALL display the title "Attendance"
4. THE Attendance_Quick_Action_Card SHALL display a description "Track your class attendance"
5. WHEN the Attendance_Quick_Action_Card is tapped, THE Navigation_Controller SHALL navigate to the Attendance_Screen
6. THE Attendance_Quick_Action_Card SHALL be positioned consistently with other quick action cards on the Dashboard

### Requirement 30: Zero Classes Handling

**User Story:** As a student, I want the app to handle subjects with zero classes gracefully, so that I can add subjects before classes start.

#### Acceptance Criteria

1. WHEN total classes is zero, THE Attendance_ViewModel SHALL set attendance percentage to 0.00
2. WHEN total classes is zero, THE Subject_Card SHALL display "0.00%" as the percentage
3. WHEN total classes is zero, THE Subject_Card SHALL not display a warning badge
4. WHEN total classes is zero, THE Attendance_Detail_Screen SHALL not display the classes needed message
5. THE Attendance_Module SHALL allow subjects with zero total classes and zero attended classes
