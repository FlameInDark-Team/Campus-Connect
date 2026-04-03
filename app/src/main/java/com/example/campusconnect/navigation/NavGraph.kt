package com.example.campusconnect.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.campusconnect.data.FirebaseManager
import com.example.campusconnect.repository.AttendanceRepository
import com.example.campusconnect.repository.AuthRepository
import com.example.campusconnect.repository.NotesRepository
import com.example.campusconnect.ui.auth.*
import com.example.campusconnect.ui.main.*
import com.example.campusconnect.viewmodel.AttendanceViewModel
import com.example.campusconnect.viewmodel.AuthViewModel
import com.example.campusconnect.viewmodel.DashboardViewModel
import com.example.campusconnect.viewmodel.NotesViewModel

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = FirebaseManager.getDatabase(context)
    
    val authRepository = AuthRepository(FirebaseManager.auth)
    val notesRepository = NotesRepository(context, database.notesDao())
    val attendanceRepository = AttendanceRepository(database.attendanceDao())
    
    val authViewModel = AuthViewModel(authRepository)
    val dashboardViewModel = DashboardViewModel(authRepository)
    val notesViewModel = NotesViewModel(notesRepository, authRepository)
    val attendanceViewModel = AttendanceViewModel(attendanceRepository, authRepository)
    
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        // Auth Graph
        composable(Routes.SPLASH) {
            SplashScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToSignup = {
                    navController.navigate(Routes.SIGNUP)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                },
                onNavigateToDashboard = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Routes.SIGNUP) {
            SignupScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToDashboard = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.SIGNUP) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        
        // Main Graph
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                viewModel = dashboardViewModel,
                onNavigateToNotes = {
                    navController.navigate(Routes.NOTES)
                },
                onNavigateToAttendance = {
                    navController.navigate(Routes.ATTENDANCE)
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                }
            )
        }
        
        // Notes Module
        composable(Routes.NOTES) {
            NotesScreen(
                viewModel = notesViewModel,
                onNavigateToUpload = {
                    navController.navigate(Routes.UPLOAD_NOTE)
                },
                onNavigateToDashboard = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                }
            )
        }
        
        composable(Routes.UPLOAD_NOTE) {
            UploadFormScreen(
                viewModel = notesViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Attendance Module
        composable(Routes.ATTENDANCE) {
            AttendanceScreen(
                viewModel = attendanceViewModel,
                onNavigateToAddSubject = {
                    navController.navigate(Routes.ADD_SUBJECT)
                },
                onNavigateToDetail = { subjectId ->
                    navController.navigate(Routes.attendanceDetail(subjectId))
                },
                onNavigateToDashboard = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                },
                onNavigateToNotes = {
                    navController.navigate(Routes.NOTES)
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                }
            )
        }
        
        composable(Routes.ADD_SUBJECT) {
            AddSubjectForm(
                viewModel = attendanceViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Routes.ATTENDANCE_DETAIL,
            arguments = listOf(navArgument("subjectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
            AttendanceDetailScreen(
                subjectId = subjectId,
                viewModel = attendanceViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Profile
        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigateToDashboard = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                },
                onNavigateToNotes = {
                    navController.navigate(Routes.NOTES)
                }
            )
        }
    }
}
