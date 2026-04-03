# Implementation Plan: Campus Connect Notes Module (Phase 2)

## Overview

This implementation plan breaks down the Notes Module into incremental, testable tasks that build upon the Phase 1 authentication system. Each task maintains build compatibility and follows the MVVM architecture established in Phase 1. The implementation follows the order specified in the design document, ensuring the app remains functional at every step.

## Tasks

- [ ] 1. Create data models for Notes Module
  - [ ] 1.1 Create Note data model with Firestore conversion methods
    - Create `model/Note.kt` with fields: id, title, subject, semester, description, fileName, fileUrl, uploadDate, fileSize, userId
    - Implement `toMap()` method for Firestore document conversion
    - Implement `fromMap()` companion method for Firestore document parsing
    - Implement `getFormattedDate()` helper method for display
    - Implement `getFormattedSize()` helper method for display
    - _Requirements: 16.1, 16.2, 16.3, 16.4_
  
  - [ ] 1.2 Create NotesUiState sealed class
    - Create `model/NotesUiState.kt` with states: Loading, Success(notes), Empty(message), Error(message)
    - _Requirements: 15.2_
  
  - [ ] 1.3 Create FilterState data model
    - Create `model/FilterState.kt` with fields: selectedSubjects, selectedSemesters
    - Implement `isActive()` method to check if any filters are applied
    - Implement `clear()` method to reset filters
    - _Requirements: 7.1, 8.1, 9.1_
  
  - [ ] 1.4 Create UploadState sealed class
    - Create `model/UploadState.kt` with states: Idle, Uploading(progress), Success, Error(message)
    - _Requirements: 4.3, 19.2_

  - [ ] 1.5 Create DownloadState sealed class
    - Create `model/DownloadState.kt` with states: Idle, Downloading(progress, noteId), Success, Error(message)
    - _Requirements: 11.3, 19.3_
  
  - [ ] 1.6 Create NoteMetadata data model
    - Create `model/NoteMetadata.kt` with fields: title, subject, semester, description
    - _Requirements: 5.3_

- [ ] 2. Create NotesRepository for Firebase operations
  - [ ] 2.1 Create NotesRepository class structure
    - Create `repository/NotesRepository.kt` with constructor accepting FirebaseStorage and FirebaseFirestore
    - Define method signatures: uploadNote, fetchNotes, observeNotes, deleteNote, downloadNote
    - _Requirements: 14.1, 14.2_
  
  - [ ] 2.2 Implement uploadNote method with rollback
    - Implement file upload to Firebase Storage at path `users/{userId}/notes/{timestamp}_{fileName}`
    - Implement progress tracking during upload
    - Retrieve download URL after successful upload
    - Save note metadata to Firestore at `users/{userId}/notes/{noteId}`
    - Implement rollback: delete uploaded file if metadata save fails
    - Map Firebase exceptions to user-friendly error messages
    - Return Result<Note>
    - _Requirements: 4.1, 4.2, 4.4, 5.1, 5.2, 5.3, 5.4, 14.3, 14.4, 14.5_
  
  - [ ] 2.3 Implement fetchNotes method
    - Query Firestore collection `users/{userId}/notes` ordered by uploadDate descending
    - Parse Firestore documents to Note objects using Note.fromMap()
    - Map Firebase exceptions to user-friendly error messages
    - Return Result<List<Note>>
    - _Requirements: 1.2, 14.4, 14.5_
  
  - [ ] 2.4 Implement observeNotes method for real-time updates
    - Set up Firestore snapshot listener on `users/{userId}/notes` collection
    - Return Flow<List<Note>> that emits updates when notes are added or deleted
    - Parse Firestore documents to Note objects
    - Handle listener errors gracefully
    - _Requirements: 5.5, 26.1, 26.2, 26.3_

  - [ ] 2.5 Implement deleteNote method
    - Delete note metadata from Firestore at `users/{userId}/notes/{noteId}`
    - Delete PDF file from Firebase Storage using fileUrl
    - Map Firebase exceptions to user-friendly error messages
    - Return Result<Unit>
    - _Requirements: 13.4, 13.5, 14.4, 14.5_
  
  - [ ] 2.6 Implement downloadNote method
    - Download PDF file from Firebase Storage to device Downloads folder
    - Track download progress
    - Update MediaStore for file visibility (Android 10+)
    - Map Firebase exceptions to user-friendly error messages
    - Return Result<Unit>
    - _Requirements: 11.2, 11.3, 14.4, 14.5_
  
  - [ ]* 2.7 Write property test for NotesRepository
    - **Property 6: File Upload to Storage**
    - **Property 9: Metadata Save After Upload**
    - **Property 10: Rollback on Metadata Save Failure**
    - **Validates: Requirements 4.1, 4.2, 4.4, 5.1, 5.2, 5.3, 5.4**

- [ ] 3. Create NotesViewModel for state management
  - [ ] 3.1 Create NotesViewModel class structure
    - Create `viewmodel/NotesViewModel.kt` extending ViewModel
    - Inject NotesRepository and AuthRepository
    - Define StateFlow properties: notesUiState, searchQuery, filterState, uploadState, downloadState
    - _Requirements: 15.1, 15.2_
  
  - [ ] 3.2 Implement loadNotes and refreshNotes methods
    - Implement loadNotes to fetch notes from repository and update notesUiState
    - Implement refreshNotes for pull-to-refresh functionality
    - Transform repository Result to NotesUiState (Loading, Success, Empty, Error)
    - _Requirements: 1.2, 2.2, 2.4, 15.5_
  
  - [ ] 3.3 Implement search functionality
    - Implement searchNotes method with case-insensitive title filtering
    - Update searchQuery StateFlow
    - Apply search filter to notes list
    - Handle empty search results with appropriate Empty state
    - _Requirements: 6.2, 6.3, 6.4, 6.5_

  - [ ] 3.4 Implement filter functionality
    - Implement filterBySubject method supporting multiple subject selection
    - Implement filterBySemester method supporting multiple semester selection
    - Implement clearFilters method to reset all filters
    - Update filterState StateFlow
    - Apply filters using AND logic between subject and semester, OR logic within each dimension
    - Handle empty filter results with appropriate Empty state
    - _Requirements: 7.2, 7.3, 7.4, 7.5, 8.2, 8.3, 8.4, 8.5, 9.1, 9.2, 9.3, 9.4_
  
  - [ ] 3.5 Implement uploadNote method with validation
    - Validate required fields: title, subject, semester
    - Validate file type is PDF
    - Display field-specific validation errors
    - Call repository.uploadNote if validation passes
    - Update uploadState with progress during upload
    - Transform repository Result to UploadState
    - _Requirements: 3.7, 3.8, 15.4, 15.5, 27.1, 27.2, 27.3_
  
  - [ ] 3.6 Implement deleteNote method
    - Call repository.deleteNote
    - Transform repository Result to UI state
    - Update notesUiState on success or error
    - _Requirements: 13.4, 15.5_
  
  - [ ] 3.7 Implement downloadNote method
    - Call repository.downloadNote
    - Update downloadState with progress during download
    - Transform repository Result to DownloadState
    - _Requirements: 11.2, 15.5_
  
  - [ ] 3.8 Implement real-time updates integration
    - Set up observeNotes flow from repository in ViewModel init
    - Update notesUiState when real-time updates are received
    - Reapply search and filters when notes list updates
    - _Requirements: 5.5, 15.6, 26.2, 26.3, 26.4_
  
  - [ ]* 3.9 Write property tests for NotesViewModel
    - **Property 12: Search Filtering**
    - **Property 13: Subject Filtering**
    - **Property 14: Semester Filtering**
    - **Property 15: Combined Search and Filter**
    - **Property 5: Upload Form Validation**
    - **Validates: Requirements 6.2, 6.3, 7.2, 7.3, 8.2, 8.3, 9.1, 9.2, 3.7, 3.8**


- [ ] 4. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 5. Create reusable UI components
  - [ ] 5.1 Create EmptyState component
    - Create `ui/components/EmptyState.kt` composable
    - Accept parameters: message, icon, actionText, onActionClick
    - Display centered icon, message, and optional action button
    - Use Material 3 styling consistent with Phase 1
    - _Requirements: 20.1, 20.2, 20.3, 20.4, 20.5_
  
  - [ ] 5.2 Create NoteCard component
    - Create `ui/components/NoteCard.kt` composable
    - Display note title, subject, semester, upload date, file size
    - Include action buttons: view, download, share, delete
    - Use Material 3 Card with rounded corners consistent with Phase 1
    - Show download progress indicator when downloading
    - _Requirements: 1.4, 1.5, 10.1, 11.1, 12.1, 13.1, 17.5_
  
  - [ ]* 5.3 Write UI tests for components
    - Test EmptyState rendering with different messages
    - Test NoteCard rendering with note data
    - Test NoteCard action button interactions

- [ ] 6. Create UploadFormScreen
  - [ ] 6.1 Create UploadFormScreen composable structure
    - Create `ui/main/UploadFormScreen.kt` composable
    - Set up Scaffold with top bar (back button)
    - Accept NotesViewModel and onNavigateBack parameters
    - _Requirements: 3.2, 22.5_
  
  - [ ] 6.2 Implement file picker integration
    - Integrate ActivityResultContracts.GetContent for PDF selection
    - Configure file picker to filter for PDF files
    - Display selected file name when file is selected
    - Validate file type is PDF
    - Display error if non-PDF file is selected
    - _Requirements: 3.3, 3.4, 27.1, 27.2, 27.3, 27.4_

  - [ ] 6.3 Implement form input fields
    - Add CustomTextField for title (reused from Phase 1)
    - Add CustomTextField for subject (reused from Phase 1)
    - Add dropdown for semester selection (Semester 1-8)
    - Add CustomTextField for description with multiline support (reused from Phase 1)
    - Display field-specific validation errors below each field
    - _Requirements: 3.5, 3.6, 3.7, 3.8, 17.1_
  
  - [ ] 6.4 Implement upload button and progress display
    - Add CustomButton for upload action (reused from Phase 1)
    - Disable upload button when required fields are empty
    - Display upload progress indicator during upload
    - Show upload percentage
    - Disable form inputs during upload
    - _Requirements: 4.3, 17.2, 19.2, 19.4_
  
  - [ ] 6.5 Implement upload success and error handling
    - Navigate back to NotesScreen on successful upload
    - Display error snackbar on upload failure using ErrorMessage component (reused from Phase 1)
    - Display appropriate error messages for network errors, storage quota, file size limits
    - Keep form data populated on error for retry
    - _Requirements: 4.5, 4.6, 4.7, 4.8, 17.4, 18.2, 18.3, 18.4_
  
  - [ ]* 6.6 Write UI tests for UploadFormScreen
    - Test file picker launch
    - Test form validation display
    - Test upload button state
    - Test error message display

- [ ] 7. Create NotesScreen
  - [ ] 7.1 Create NotesScreen composable structure
    - Create `ui/main/NotesScreen.kt` composable (replaces NotesPlaceholderScreen.kt)
    - Set up Scaffold with top bar, FAB, and bottom navigation
    - Accept NotesViewModel and navigation callbacks
    - Integrate bottom navigation from Phase 1
    - _Requirements: 1.1, 3.1, 22.1, 22.3_

  - [ ] 7.2 Implement search bar
    - Add search TextField in top bar
    - Bind to NotesViewModel.searchQuery StateFlow
    - Call NotesViewModel.searchNotes on text change
    - Display search results in real-time
    - _Requirements: 6.1, 6.2, 6.3_
  
  - [ ] 7.3 Implement filter chips
    - Add horizontal scrollable row of subject filter chips
    - Add horizontal scrollable row of semester filter chips
    - Highlight selected chips with primary color
    - Call NotesViewModel.filterBySubject and filterBySemester on chip selection
    - Add "Clear All" button to reset filters
    - Display filtered results count
    - _Requirements: 7.1, 7.2, 7.5, 8.1, 8.2, 8.5, 9.2, 9.4_
  
  - [ ] 7.4 Implement notes list with LazyColumn
    - Display notes using LazyColumn with NoteCard components
    - Implement pull-to-refresh using SwipeRefresh
    - Call NotesViewModel.refreshNotes on pull-to-refresh
    - Display refresh indicator during refresh
    - _Requirements: 1.3, 2.1, 2.2, 2.3_
  
  - [ ] 7.5 Implement loading, empty, and error states
    - Display LoadingIndicator (reused from Phase 1) when notesUiState is Loading
    - Display skeleton loading cards during initial fetch
    - Display EmptyState when no notes exist with message and upload button
    - Display EmptyState when search returns no results
    - Display EmptyState when filters return no results
    - Display error message using ErrorMessage component (reused from Phase 1) when notesUiState is Error
    - _Requirements: 1.6, 1.7, 6.4, 7.4, 8.4, 9.3, 17.3, 17.4, 19.1, 20.1, 20.2, 20.3, 28.1, 28.2, 28.3, 28.4_
  
  - [ ] 7.6 Implement note actions
    - Wire up view action to open PDF in external viewer using Intent
    - Wire up download action to call NotesViewModel.downloadNote
    - Wire up share action to open Android share sheet with PDF file
    - Wire up delete action to show confirmation dialog, then call NotesViewModel.deleteNote
    - Display appropriate error messages for missing PDF viewer, permission errors, etc.
    - _Requirements: 10.1, 10.2, 10.3, 10.4, 11.1, 11.2, 11.4, 11.5, 11.6, 11.7, 12.1, 12.2, 12.3, 13.1, 13.2, 13.3, 13.6, 13.7, 18.7_

  - [ ] 7.7 Implement FAB for upload navigation
    - Add FloatingActionButton with "+" icon
    - Call onNavigateToUpload callback on FAB click
    - _Requirements: 3.1_
  
  - [ ] 7.8 Implement success snackbars
    - Display success snackbar "Note uploaded successfully" after upload
    - Display success snackbar "Note downloaded to Downloads folder" after download
    - Display success snackbar "Note deleted successfully" after deletion
    - Auto-dismiss snackbars after 4 seconds consistent with Phase 1
    - _Requirements: 24.1, 24.2, 24.3, 24.5_
  
  - [ ]* 7.9 Write UI tests for NotesScreen
    - Test notes list rendering
    - Test search bar interaction
    - Test filter chips interaction
    - Test pull-to-refresh
    - Test empty states
    - Test note action buttons

- [ ] 8. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 9. Integrate navigation
  - [ ] 9.1 Update Routes.kt
    - Add UPLOAD_NOTE route constant
    - _Requirements: 22.5_
  
  - [ ] 9.2 Update MainNavGraph.kt
    - Replace NotesPlaceholderScreen composable with NotesScreen
    - Add UploadFormScreen route
    - Wire up navigation: NotesScreen → UploadFormScreen → NotesScreen
    - Pass NotesViewModel to both screens
    - _Requirements: 22.1, 22.2, 22.5, 22.6_
  
  - [ ] 9.3 Delete NotesPlaceholderScreen.kt
    - Remove `ui/main/NotesPlaceholderScreen.kt` file
    - _Requirements: 1.1, 22.1_
  
  - [ ]* 9.4 Write navigation tests
    - Test navigation from Dashboard to NotesScreen
    - Test navigation from NotesScreen to UploadFormScreen
    - Test navigation back after successful upload
    - Test bottom navigation integration


- [ ] 10. Add permissions and FileProvider configuration
  - [ ] 10.1 Create FileProvider configuration
    - Create `res/xml/file_paths.xml` with external-files-path for sharing PDFs
    - _Requirements: 12.2_
  
  - [ ] 10.2 Update AndroidManifest.xml
    - Add READ_EXTERNAL_STORAGE permission with maxSdkVersion="32"
    - Add WRITE_EXTERNAL_STORAGE permission with maxSdkVersion="28"
    - Add FileProvider configuration in application tag
    - _Requirements: 21.1, 21.4_
  
  - [ ] 10.3 Implement permission handling in NotesViewModel
    - Check for READ_EXTERNAL_STORAGE permission before file selection
    - Check for WRITE_EXTERNAL_STORAGE permission before download (Android < 10)
    - Request permissions if not granted
    - Display permission error messages if denied
    - _Requirements: 21.1, 21.2, 21.3, 21.4, 21.5, 21.6_
  
  - [ ] 10.4 Update strings.xml
    - Add notes-related strings: screen titles, button labels, error messages, empty state messages
    - _Requirements: 18.1, 18.2, 18.3, 18.4, 18.5, 18.6, 18.7, 20.1, 20.2, 20.3_
  
  - [ ]* 10.5 Write permission flow tests
    - Test permission request for file selection
    - Test permission request for download
    - Test permission denial handling

- [ ] 11. Final integration and polish
  - [ ] 11.1 Verify theme consistency
    - Verify NotesScreen uses CampusConnectTheme from Phase 1
    - Verify UploadFormScreen uses CampusConnectTheme from Phase 1
    - Verify Material 3 colors, typography, and spacing are applied
    - Verify rounded corners on cards match Phase 1 styling
    - Test light and dark mode support
    - _Requirements: 23.1, 23.2, 23.3, 23.4, 23.5_
  
  - [ ] 11.2 Verify component reuse
    - Verify CustomTextField is used in UploadFormScreen
    - Verify CustomButton is used in UploadFormScreen and EmptyState
    - Verify LoadingIndicator is used in NotesScreen
    - Verify ErrorMessage is used throughout for error display
    - _Requirements: 17.1, 17.2, 17.3, 17.4_

  - [ ] 11.3 Test real-time updates
    - Test that notes list updates automatically when notes are added
    - Test that notes list updates automatically when notes are deleted
    - Test that filters are reapplied after real-time updates
    - _Requirements: 26.1, 26.2, 26.3, 26.4_
  
  - [ ] 11.4 Test error scenarios
    - Test network error handling during upload
    - Test network error handling during download
    - Test storage quota exceeded error
    - Test file size limit error
    - Test permission denial scenarios
    - Test missing PDF viewer scenario
    - Test invalid file URL scenario
    - _Requirements: 18.1, 18.2, 18.3, 18.4, 18.5, 18.6, 18.7_
  
  - [ ] 11.5 Test build compatibility
    - Verify project builds successfully with no compilation errors
    - Verify no deprecated Android APIs are used
    - Verify existing Phase 1 features still work (auth, dashboard, profile)
    - Verify bottom navigation works correctly
    - _Requirements: 25.1, 25.2, 25.3, 25.4, 25.5, 25.6_
  
  - [ ]* 11.6 Write integration tests
    - Test complete upload flow: select file → enter metadata → upload → navigate back
    - Test complete download flow: tap download → permission check → download → success message
    - Test complete delete flow: tap delete → confirm → delete → success message
    - Test search and filter combinations
    - Test real-time updates during active filters

- [ ] 12. Final checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional testing tasks and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation at key milestones
- Property tests validate universal correctness properties from the design document
- Unit tests validate specific examples and edge cases
- The implementation order ensures the app remains buildable at every step
- All Firebase dependencies were added in Phase 1, no new dependencies required
- Component reuse from Phase 1 ensures UI consistency and reduces code duplication
