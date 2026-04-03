# How to Run Campus Connect App

## Quick Start (5 Minutes)

### Step 1: Firebase Authentication Setup (FREE - No Credit Card)

1. **Go to Firebase Console**: https://console.firebase.google.com/
2. **Select your project**: `campusconnect`
3. **Enable Authentication**:
   - Click **"Build"** in left sidebar (or expand "Product categories" → "Security")
   - Click **"Authentication"**
   - Click **"Get Started"**
   - Click **"Sign-in method"** tab
   - Find **"Email/Password"** in the list
   - Click on it
   - Toggle **"Enable"** switch ON
   - Click **"Save"**

**That's it for Firebase!** No Storage setup needed, no credit card required! ✅

---

### Step 2: Open Project in Android Studio

1. **Launch Android Studio**
2. **Open Project**: File → Open → Navigate to `E:\Hackathon\Android`
3. **Wait for Gradle Sync** to complete (bottom status bar will show progress)
4. **Check for errors** in the "Build" tab (should be none)

---

### Step 3: Set Up Device/Emulator

#### Option A: Use Android Emulator (Recommended for Testing)

1. Click **"Device Manager"** icon (phone icon on right sidebar)
2. If you don't have an emulator:
   - Click **"Create Device"**
   - Choose **"Pixel 5"** or any phone
   - Choose **"API 30"** or higher (Android 11+)
   - Click **"Finish"**
3. Click the **play button** (▶) next to your emulator to start it
4. Wait for emulator to boot (1-2 minutes first time)

#### Option B: Use Physical Device

1. **Enable Developer Options** on your Android phone:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
   - Go back to Settings → Developer Options
2. **Enable USB Debugging**
3. **Connect phone via USB**
4. **Allow USB Debugging** when prompted on phone
5. Phone should appear in device dropdown in Android Studio

---

### Step 4: Run the App

1. **Select your device** from the device dropdown (top toolbar)
2. **Click the green Run button** (▶) or press **Shift + F10**
3. **Wait for build** to complete (1-3 minutes first time)
4. **App will launch** automatically on your device/emulator

---

### Step 5: Test the App

#### First Launch:
1. **Splash Screen** appears
2. **Login Screen** appears (no account yet)

#### Create Account:
1. Click **"Sign Up"**
2. Enter email: `test@example.com`
3. Enter password: `password123` (min 6 characters)
4. Click **"Sign Up"**
5. ✅ You're logged in!

#### Test Features:

**Dashboard:**
- ✅ Welcome message with your email
- ✅ Quick action cards (Notes, Attendance, etc.)
- ✅ Bottom navigation works

**Notes Module:**
1. Click **"Notes"** from dashboard or bottom nav
2. Click **"+"** button (floating action button)
3. Fill in:
   - Title: "Data Structures Notes"
   - Subject: "Computer Science"
   - Semester: "3rd Semester"
   - Description: "Lecture notes on trees and graphs"
4. Click **"Select PDF"** and choose any PDF file
5. Click **"Upload"**
6. ✅ Note appears in list instantly (saved locally)
7. ✅ Search and filter work
8. ✅ Click note to open PDF
9. ✅ Swipe to delete

**Attendance Tracker:**
1. Click **"Attendance"** from dashboard or bottom nav
2. Click **"+"** button
3. Enter subject name: "Data Structures"
4. Click **"Add Subject"**
5. Click on the subject card
6. Update attendance:
   - Total Classes: 20
   - Attended Classes: 18
7. ✅ Percentage calculated automatically (90%)
8. ✅ Green indicator (≥75%)
9. Try with low attendance:
   - Total: 20
   - Attended: 10
10. ✅ Red indicator (<75%)
11. ✅ Shows classes needed to reach 75%

---

## Troubleshooting

### Build Errors

**Issue**: "Gradle sync failed"  
**Solution**:
```bash
# In Android Studio Terminal:
./gradlew clean
./gradlew build --refresh-dependencies
```

**Issue**: "google-services.json not found"  
**Solution**: Make sure `app/google-services.json` exists and is from your Firebase project

---

### Firebase Errors

**Issue**: "Firebase Auth not initialized"  
**Solution**: 
- Check `google-services.json` is in `app/` folder
- Verify Firebase Authentication is enabled in console
- Rebuild the app

**Issue**: "Authentication failed"  
**Solution**:
- Check internet connection (Auth requires internet)
- Verify Email/Password is enabled in Firebase Console
- Try with a different email

---

### App Crashes

**Issue**: App crashes on launch  
**Solution**:
1. Check logcat for error messages
2. Clean and rebuild: Build → Clean Project → Rebuild Project
3. Uninstall app from device and reinstall

**Issue**: "Room database error"  
**Solution**:
1. Uninstall the app (clears database)
2. Reinstall and run again

---

### Device/Emulator Issues

**Issue**: No devices available  
**Solution**:
- Create an emulator (see Step 3)
- Or connect a physical device with USB debugging

**Issue**: Emulator is slow  
**Solution**:
- Use a device with lower resolution (Pixel 5 instead of Pixel 6 Pro)
- Enable hardware acceleration in BIOS
- Use a physical device instead

---

## Features to Test

### ✅ Authentication
- [x] Sign up with new account
- [x] Log out
- [x] Log in with existing account
- [x] Forgot password (sends email)
- [x] Session persistence (stays logged in after app restart)

### ✅ Notes Module
- [x] Upload PDF with metadata
- [x] View list of notes
- [x] Search notes by title
- [x] Filter by subject
- [x] Filter by semester
- [x] Open PDF in viewer
- [x] Share note
- [x] Delete note
- [x] Works offline (no internet needed after login)

### ✅ Attendance Tracker
- [x] Add subject
- [x] Update attendance
- [x] View percentage
- [x] Color indicators (green/red)
- [x] Warning for <75%
- [x] Calculate classes needed
- [x] Delete subject
- [x] Works offline

### ✅ Navigation
- [x] Bottom navigation works
- [x] Back button works
- [x] Deep navigation (dashboard → notes → upload → back)

---

## Offline Testing

1. **Log in** to the app (requires internet)
2. **Add some notes and attendance data**
3. **Enable airplane mode** on device
4. **Test all features**:
   - ✅ View notes
   - ✅ Add new notes (with local PDF)
   - ✅ Search and filter
   - ✅ Update attendance
   - ✅ Delete items
5. **Everything should work!** (except login/signup)

---

## What Works Offline vs Online

| Feature | Offline | Online |
|---------|---------|--------|
| Login/Signup | ❌ Requires internet | ✅ Works |
| View Notes | ✅ Works | ✅ Works |
| Upload Notes | ✅ Works | ✅ Works |
| Search/Filter | ✅ Works | ✅ Works |
| Attendance | ✅ Works | ✅ Works |
| Dashboard | ✅ Works | ✅ Works |

---

## Storage Information

### Where Files Are Stored:
```
/data/data/com.example.campusconnect/files/notes/{userId}/
```

### Storage Limits:
- Limited by device storage (typically 32GB - 256GB)
- No cloud storage limits
- No costs or quotas

### Privacy:
- Files stored in app's private directory
- Other apps cannot access
- Files deleted when app is uninstalled

---

## Next Steps

### For Development:
1. Customize the theme colors
2. Add more features (announcements, profile, etc.)
3. Implement data export/backup
4. Add more subjects and semesters

### For Production:
1. Test on multiple devices
2. Add error tracking (Firebase Crashlytics)
3. Optimize performance
4. Add analytics (optional)
5. Publish to Google Play Store

---

## Need Help?

### Documentation:
- `README.md` - Project overview
- `LOCAL_STORAGE_MIGRATION.md` - Storage details
- `MIGRATION_GUIDE.md` - Database migration info
- `QUICK_START.md` - Quick setup guide

### Common Issues:
- Check logcat for error messages
- Verify Firebase setup
- Ensure device has storage space
- Try clean rebuild

---

**Your app is ready to run!** 🚀

No credit card, no cloud storage costs, completely free forever!
