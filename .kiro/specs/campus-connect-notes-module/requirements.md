# Requirements Document

## Introduction

This document specifies the requirements for Phase 2 of Campus Connect: the Notes Module. This feature enables students to upload, view, search, filter, download, and share PDF notes. The module integrates with Firebase Storage for file storage and Firestore for metadata management. It follows the existing MVVM architecture, reuses Phase 1 components, and maintains consistency with the established Material 3 design system.

## Glossary

- **Notes_Module**: The complete notes feature including upload, view, search, filter, download, and share capabilities
- **Notes_Screen**: The main screen displaying the list of uploaded notes with search and filter capabilities
- **Note**: A PDF document with associated metadata (title, subject, semester, description, upload date, file size)
- **Note_Card**: A UI component displaying note metadata in a Material 3 card
- **Upload_Form**: The screen for uploading new PDF notes with metadata input fields
- **File_Picker**: The Android system file picker for selecting PDF files from device storage
- **Firebase_Storage**: The cloud storage service for storing PDF files
- **Firestore**: The NoSQL database for storing note metadata
- **Notes_Repository**: The data access layer handling Firestore and Storage operations
- **Notes_ViewModel**: The business logic layer managing notes state and operations
- **Search_Bar**: The UI component for filtering notes by title
- **Filter_System**: The mechanism for filtering notes by subject or semester
- **Download_Manager**: The system for downloading PDF files to device storage
- **Share_System**: The Android share functionality for sharing PDFs with other apps
- **Empty_State**: The UI displayed when no notes exist or search returns no results
- **Loading_State**: The UI displayed during asynchronous operations
- **Error_State**: The UI displayed when operations fail

## Requirements

### Requirement 1: Notes Screen Display

**User Story:** As a student, I want to see a list of my uploaded notes, so that I can access my study materials.

#### Acceptance Criteria

1. THE Notes_Screen SHALL replace the NotesPlaceholderScreen from Phase 1
2. WHEN the Notes_Screen loads, THE Notes_ViewModel SHALL fetch all notes from Firestore
3. THE Notes_Screen SHALL display notes as Note_Cards in a scrollable list
4. THE Note_Card SHALL display title, subject, semester, and upload date
5. THE Notes_Screen SHALL use Material 3 card styling with rounded corners consistent with Phase 1 design
6. WHEN notes are loading, THE Notes_Screen SHALL display a loading indicator
7. WHEN no notes exist, THE Notes_Screen SHALL display an empty state with a helpful message and upload button

### Requirement 2: Pull-to-Refresh

**User Story:** As a student, I want to refresh my notes list, so that I can see newly uploaded notes.

#### Acceptance Criteria

1. THE Notes_Screen SHALL support pull-to-refresh gesture
2. WHEN a user pulls down on the notes list, THE Notes_ViewModel SHALL refetch notes from Firestore
3. WHILE refreshing, THE Notes_Screen SHALL display a refresh indicator
4. WHEN the refresh completes, THE Notes_Screen SHALL update the displayed notes

### Requirement 3: Upload PDF Notes

**User Story:** As a student, I want to upload PDF notes with metadata, so that I can organize and share my study materials.

#### Acceptance Criteria

1. THE Notes_Screen SHALL display a floating action button for uploading new notes
2. WHEN the upload button is tapped, THE Navigation_Controller SHALL navigate to the Upload_Form
3. THE Upload_Form SHALL provide a button to open the File_Picker for selecting PDF files
4. WHEN a PDF is selected, THE Upload_Form SHALL display the selected file name
5. THE Upload_Form SHALL provide text fields for title, subject, and description
6. THE Upload_Form SHALL provide a dropdown for semester selection with options Semester 1 through Semester 8
7. THE Upload_Form SHALL validate that title, subject, and semester are provided before upload
8. WHEN validation fails, THE Upload_Form SHALL display field-specific error messages

### Requirement 4: File Upload to Firebase Storage

**User Story:** As a student, I want my PDF files securely stored in the cloud, so that I can access them from any device.

#### Acceptance Criteria

1. WHEN a user submits the Upload_Form with valid data, THE Notes_Repository SHALL upload the PDF file to Firebase_Storage
2. THE Firebase_Storage SHALL organize files using the path structure: users/{userId}/notes/{timestamp}_{fileName}
3. WHILE the upload is in progress, THE Upload_Form SHALL display a progress indicator showing upload percentage
4. WHEN the upload succeeds, THE Notes_Repository SHALL generate a download URL for the uploaded file
5. WHEN the upload fails due to network error, THE Upload_Form SHALL display a network error message
6. WHEN the upload fails due to storage quota exceeded, THE Upload_Form SHALL display a storage quota error message
7. WHEN the upload fails due to file size exceeding limits, THE Upload_Form SHALL display a file size error message
8. WHEN the upload completes successfully, THE Navigation_Controller SHALL navigate back to the Notes_Screen

### Requirement 5: Save Note Metadata to Firestore

**User Story:** As a student, I want my note metadata stored in the database, so that I can search and filter my notes.

#### Acceptance Criteria

1. WHEN a file upload to Firebase_Storage succeeds, THE Notes_Repository SHALL save note metadata to Firestore
2. THE Firestore SHALL use the collection structure: users/{userId}/notes/{noteId}
3. THE Note_Metadata SHALL include fields: title, subject, semester, description, fileName, fileUrl, uploadDate, fileSize
4. WHEN metadata save fails, THE Notes_Repository SHALL delete the uploaded file from Firebase_Storage to maintain consistency
5. THE Notes_Screen SHALL receive real-time updates when notes are added or deleted in Firestore

### Requirement 6: Search Notes

**User Story:** As a student, I want to search my notes by title, so that I can quickly find specific materials.

#### Acceptance Criteria

1. THE Notes_Screen SHALL display a Search_Bar at the top of the screen
2. WHEN a user types in the Search_Bar, THE Notes_ViewModel SHALL filter notes by title in real-time
3. THE Search SHALL be case-insensitive
4. WHEN the search returns no results, THE Notes_Screen SHALL display an empty state with a message indicating no matches
5. WHEN the search field is cleared, THE Notes_Screen SHALL display all notes again

### Requirement 7: Filter Notes by Subject

**User Story:** As a student, I want to filter notes by subject, so that I can focus on specific course materials.

#### Acceptance Criteria

1. THE Notes_Screen SHALL provide a subject filter using chips or a dropdown
2. WHEN a subject filter is selected, THE Notes_ViewModel SHALL display only notes matching that subject
3. THE Filter_System SHALL support selecting multiple subjects simultaneously
4. WHEN no notes match the selected subject, THE Notes_Screen SHALL display an empty state
5. THE Notes_Screen SHALL provide a clear filters option to reset subject filters

### Requirement 8: Filter Notes by Semester

**User Story:** As a student, I want to filter notes by semester, so that I can organize materials by academic period.

#### Acceptance Criteria

1. THE Notes_Screen SHALL provide a semester filter using chips or a dropdown
2. WHEN a semester filter is selected, THE Notes_ViewModel SHALL display only notes matching that semester
3. THE Filter_System SHALL support selecting multiple semesters simultaneously
4. WHEN no notes match the selected semester, THE Notes_Screen SHALL display an empty state
5. THE Notes_Screen SHALL provide a clear filters option to reset semester filters

### Requirement 9: Combined Search and Filters

**User Story:** As a student, I want to use search and filters together, so that I can find specific notes efficiently.

#### Acceptance Criteria

1. WHEN both search and filters are active, THE Notes_ViewModel SHALL apply both conditions using AND logic
2. THE Notes_Screen SHALL display the count of filtered results
3. WHEN the combined search and filters return no results, THE Notes_Screen SHALL display an empty state
4. THE Notes_Screen SHALL provide a clear all option to reset both search and filters

### Requirement 10: View PDF Notes

**User Story:** As a student, I want to view my PDF notes, so that I can read my study materials.

#### Acceptance Criteria

1. WHEN a Note_Card is tapped, THE Notes_ViewModel SHALL retrieve the file URL from the note metadata
2. THE Notes_Module SHALL open the PDF in an external PDF viewer app using Android Intent
3. WHEN no PDF viewer app is installed, THE Notes_Screen SHALL display an error message prompting the user to install a PDF viewer
4. WHEN the file URL is invalid or the file no longer exists, THE Notes_Screen SHALL display an error message

### Requirement 11: Download PDF Notes

**User Story:** As a student, I want to download PDF notes to my device, so that I can access them offline.

#### Acceptance Criteria

1. THE Note_Card SHALL display a download button
2. WHEN the download button is tapped, THE Notes_Repository SHALL download the PDF file to the device Downloads folder
3. WHILE the download is in progress, THE Note_Card SHALL display a download progress indicator
4. WHEN the download succeeds, THE Notes_Screen SHALL display a success snackbar with the file location
5. WHEN the download fails due to network error, THE Notes_Screen SHALL display a network error message
6. WHEN the download fails due to insufficient storage, THE Notes_Screen SHALL display a storage error message
7. WHEN the download fails due to permission denial, THE Notes_Screen SHALL display a permission error message and prompt for storage permission

### Requirement 12: Share PDF Notes

**User Story:** As a student, I want to share PDF notes with classmates, so that we can collaborate on study materials.

#### Acceptance Criteria

1. THE Note_Card SHALL display a share button
2. WHEN the share button is tapped, THE Notes_Module SHALL open the Android share sheet with the PDF file
3. THE Share_System SHALL support sharing via email, messaging apps, cloud storage, and other installed apps
4. WHEN the file cannot be shared, THE Notes_Screen SHALL display an error message

### Requirement 13: Delete Notes

**User Story:** As a student, I want to delete notes I no longer need, so that I can keep my notes list organized.

#### Acceptance Criteria

1. THE Note_Card SHALL display a delete button
2. WHEN the delete button is tapped, THE Notes_Screen SHALL display a confirmation dialog
3. THE Confirmation_Dialog SHALL ask "Are you sure you want to delete this note?" with Cancel and Delete options
4. WHEN the user confirms deletion, THE Notes_Repository SHALL delete the note metadata from Firestore
5. WHEN the metadata deletion succeeds, THE Notes_Repository SHALL delete the PDF file from Firebase_Storage
6. WHEN the deletion succeeds, THE Notes_Screen SHALL display a success snackbar
7. WHEN the deletion fails, THE Notes_Screen SHALL display an error message and keep the note in the list

### Requirement 14: Notes Repository

**User Story:** As a developer, I want a NotesRepository following the existing architecture, so that data access is clean and testable.

#### Acceptance Criteria

1. THE Notes_Repository SHALL be created in the repository package following Phase 1 patterns
2. THE Notes_Repository SHALL provide methods: uploadNote, fetchNotes, deleteNote, downloadNote
3. THE Notes_Repository SHALL handle Firebase_Storage operations for file upload, download, and deletion
4. THE Notes_Repository SHALL handle Firestore operations for metadata CRUD operations
5. THE Notes_Repository SHALL map Firebase exceptions to user-friendly error messages
6. THE Notes_Repository SHALL return Result types for all operations

### Requirement 15: Notes ViewModel

**User Story:** As a developer, I want a NotesViewModel managing notes state, so that the UI remains reactive and testable.

#### Acceptance Criteria

1. THE Notes_ViewModel SHALL be created in the viewmodel package following Phase 1 patterns
2. THE Notes_ViewModel SHALL expose StateFlow for notes list, loading state, error state, and filter state
3. THE Notes_ViewModel SHALL provide methods: loadNotes, uploadNote, deleteNote, downloadNote, searchNotes, filterBySubject, filterBySemester, clearFilters
4. THE Notes_ViewModel SHALL validate upload form inputs before calling repository methods
5. THE Notes_ViewModel SHALL transform repository results into UI state
6. THE Notes_ViewModel SHALL handle real-time Firestore updates and update the notes list automatically

### Requirement 16: Note Data Model

**User Story:** As a developer, I want a Note data model, so that note data is structured and type-safe.

#### Acceptance Criteria

1. THE Note_Model SHALL be created in the model package following Phase 1 patterns
2. THE Note_Model SHALL include fields: id, title, subject, semester, description, fileName, fileUrl, uploadDate, fileSize, userId
3. THE Note_Model SHALL be a Kotlin data class
4. THE Note_Model SHALL support conversion to and from Firestore document format

### Requirement 17: Reusable Components

**User Story:** As a developer, I want to reuse existing components and create notes-specific components, so that the UI is consistent and maintainable.

#### Acceptance Criteria

1. THE Notes_Module SHALL reuse CustomTextField from Phase 1 for upload form inputs
2. THE Notes_Module SHALL reuse CustomButton from Phase 1 for upload and action buttons
3. THE Notes_Module SHALL reuse LoadingIndicator from Phase 1 for loading states
4. THE Notes_Module SHALL reuse ErrorMessage from Phase 1 for error display
5. THE Notes_Module SHALL create a new Note_Card component in the components package
6. THE Notes_Module SHALL create a new EmptyState component for no notes and no search results scenarios
7. THE Reusable_Components SHALL follow Material 3 design patterns consistent with Phase 1

### Requirement 18: Error Handling

**User Story:** As a student, I want clear error messages when operations fail, so that I understand what went wrong and how to fix it.

#### Acceptance Criteria

1. WHEN a file picker is cancelled, THE Upload_Form SHALL not display an error message
2. WHEN a file upload fails due to network error, THE Notes_Screen SHALL display "Network error. Please check your connection and try again"
3. WHEN a file upload fails due to storage quota, THE Notes_Screen SHALL display "Storage quota exceeded. Please free up space or contact support"
4. WHEN a file upload fails due to file size, THE Notes_Screen SHALL display "File size exceeds limit. Please select a smaller file"
5. WHEN a file download fails due to permission error, THE Notes_Screen SHALL display "Storage permission required. Please grant permission in settings"
6. WHEN a file download fails due to insufficient storage, THE Notes_Screen SHALL display "Insufficient storage space. Please free up space and try again"
7. WHEN a PDF viewer is not installed, THE Notes_Screen SHALL display "No PDF viewer installed. Please install a PDF viewer app"
8. THE Error_Messages SHALL use the ErrorMessage component from Phase 1 for consistency

### Requirement 19: Loading States

**User Story:** As a student, I want to see loading indicators during operations, so that I know the app is working.

#### Acceptance Criteria

1. WHILE notes are being fetched, THE Notes_Screen SHALL display a loading indicator using the LoadingIndicator component from Phase 1
2. WHILE a file is being uploaded, THE Upload_Form SHALL display a progress indicator showing upload percentage
3. WHILE a file is being downloaded, THE Note_Card SHALL display a download progress indicator
4. WHILE an operation is in progress, THE Notes_Screen SHALL disable relevant UI elements to prevent duplicate operations
5. WHEN an operation completes, THE Notes_Screen SHALL hide loading indicators and re-enable UI elements

### Requirement 20: Empty States

**User Story:** As a student, I want helpful empty state messages, so that I know what to do when no notes are displayed.

#### Acceptance Criteria

1. WHEN no notes exist, THE Notes_Screen SHALL display "No notes yet. Tap the + button to upload your first note"
2. WHEN search returns no results, THE Notes_Screen SHALL display "No notes match your search. Try different keywords"
3. WHEN filters return no results, THE Notes_Screen SHALL display "No notes match your filters. Try adjusting your filters"
4. THE Empty_State SHALL include an icon or illustration for visual appeal
5. THE Empty_State SHALL follow Material 3 design patterns

### Requirement 21: Permission Handling

**User Story:** As a student, I want the app to request necessary permissions, so that I can upload and download files.

#### Acceptance Criteria

1. WHEN a user attempts to select a file, THE Notes_Module SHALL check for storage read permission
2. WHEN storage read permission is not granted, THE Notes_Module SHALL request the permission
3. WHEN the user denies storage read permission, THE Notes_Screen SHALL display an error message explaining why the permission is needed
4. WHEN a user attempts to download a file, THE Notes_Module SHALL check for storage write permission
5. WHEN storage write permission is not granted, THE Notes_Module SHALL request the permission
6. WHEN the user denies storage write permission, THE Notes_Screen SHALL display an error message explaining why the permission is needed

### Requirement 22: Navigation Integration

**User Story:** As a developer, I want the Notes_Screen integrated into existing navigation, so that users can access notes from the bottom navigation.

#### Acceptance Criteria

1. THE Notes_Screen SHALL replace the NotesPlaceholderScreen in the MainNavGraph
2. WHEN the Notes bottom navigation item is tapped, THE Navigation_Controller SHALL navigate to the Notes_Screen
3. THE Notes_Screen SHALL display the bottom navigation bar from Phase 1
4. THE Bottom_Navigation SHALL highlight the Notes item when the Notes_Screen is active
5. THE Upload_Form SHALL be added as a new route in the MainNavGraph
6. THE Navigation_Controller SHALL support navigating from Notes_Screen to Upload_Form and back

### Requirement 23: Theme Consistency

**User Story:** As a student, I want the Notes_Module to match the Phase 1 design, so that the app feels cohesive.

#### Acceptance Criteria

1. THE Notes_Module SHALL use the CampusConnectTheme from Phase 1
2. THE Notes_Module SHALL use colors, typography, and spacing from the Phase 1 theme system
3. THE Note_Card SHALL use rounded corners consistent with Phase 1 cards
4. THE Upload_Form SHALL use the same input field styling as Phase 1 auth screens
5. THE Notes_Screen SHALL support both light and dark mode using the Phase 1 theme system

### Requirement 24: Snackbar Feedback

**User Story:** As a student, I want snackbar feedback for operations, so that I know when actions succeed or fail.

#### Acceptance Criteria

1. WHEN a note is uploaded successfully, THE Notes_Screen SHALL display a success snackbar "Note uploaded successfully"
2. WHEN a note is deleted successfully, THE Notes_Screen SHALL display a success snackbar "Note deleted successfully"
3. WHEN a note is downloaded successfully, THE Notes_Screen SHALL display a success snackbar "Note downloaded to Downloads folder"
4. WHEN an operation fails, THE Notes_Screen SHALL display an error snackbar with the error message
5. THE Snackbar SHALL auto-dismiss after 4 seconds consistent with Phase 1 error handling

### Requirement 25: Build Compatibility

**User Story:** As a developer, I want the Notes_Module to integrate without breaking the existing app, so that Phase 1 features continue to work.

#### Acceptance Criteria

1. THE Notes_Module SHALL work within the existing Android Studio project structure
2. THE Notes_Module SHALL not break the existing Gradle setup
3. THE Notes_Module SHALL not use deprecated Android APIs
4. THE Notes_Module SHALL follow the existing package structure from Phase 1
5. THE Notes_Module SHALL maintain the existing MVVM architecture patterns
6. THE Notes_Module SHALL keep the app buildable at every implementation step

### Requirement 26: Real-time Updates

**User Story:** As a student, I want my notes list to update automatically when I add or delete notes, so that I always see the current state.

#### Acceptance Criteria

1. THE Notes_Repository SHALL use Firestore real-time listeners for the notes collection
2. WHEN a note is added to Firestore, THE Notes_Screen SHALL automatically display the new note without manual refresh
3. WHEN a note is deleted from Firestore, THE Notes_Screen SHALL automatically remove the note from the list without manual refresh
4. THE Real-time_Updates SHALL work seamlessly with search and filter functionality

### Requirement 27: File Validation

**User Story:** As a student, I want the app to validate file types, so that I only upload PDF files.

#### Acceptance Criteria

1. WHEN a user selects a file from the File_Picker, THE Notes_Module SHALL check the file extension
2. WHEN the selected file is not a PDF, THE Upload_Form SHALL display an error message "Please select a PDF file"
3. WHEN the selected file is a PDF, THE Upload_Form SHALL allow the user to proceed with metadata entry
4. THE File_Picker SHALL be configured to show only PDF files when possible

### Requirement 28: Skeleton Loading

**User Story:** As a student, I want skeleton loading for note cards, so that the loading experience feels smooth and modern.

#### Acceptance Criteria

1. WHILE notes are being fetched for the first time, THE Notes_Screen SHALL display skeleton loading cards
2. THE Skeleton_Cards SHALL match the size and layout of actual Note_Cards
3. THE Skeleton_Cards SHALL use a shimmer animation effect
4. WHEN notes finish loading, THE Skeleton_Cards SHALL be replaced with actual Note_Cards with a smooth transition
