package com.example.campusconnect.navigation

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
    const val UPLOAD_NOTE = "upload_note"
    const val ATTENDANCE = "attendance"
    const val ADD_SUBJECT = "add_subject"
    const val ATTENDANCE_DETAIL = "attendance_detail/{subjectId}"
    
    fun attendanceDetail(subjectId: String) = "attendance_detail/$subjectId"
}
