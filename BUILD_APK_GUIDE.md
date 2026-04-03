# 📦 How to Build and Publish APK on GitHub

## Step 1: Build the APK in Android Studio

### Option A: Build Debug APK (Recommended for Testing)

1. Open your project in **Android Studio**
2. Click **Build** menu at the top
3. Select **Build Bundle(s) / APK(s)**
4. Click **Build APK(s)**
5. Wait for the build to complete (you'll see a notification)
6. Click **locate** in the notification to find your APK

**APK Location**: `app/build/outputs/apk/debug/app-debug.apk`

### Option B: Build Release APK (For Production)

1. Open your project in **Android Studio**
2. Click **Build** menu at the top
3. Select **Build Bundle(s) / APK(s)**
4. Click **Build APK(s)**
5. Wait for the build to complete
6. The release APK will be at: `app/build/outputs/apk/release/app-release-unsigned.apk`

**Note**: Release APKs should be signed for production. For now, use debug APK for testing.

---

## Step 2: Create GitHub Release

1. Go to your repository: https://github.com/FlameInDark-Team/Campus-Connect

2. Click **Releases** (on the right side of the page)

3. Click **Create a new release** or **Draft a new release**

4. Fill in the release details:

   **Tag version**: `v1.0.0`
   
   **Release title**: `Campus Connect v1.0.0 - Initial Release`
   
   **Description**: Copy and paste this:
   ```markdown
   ## 🎉 First Release of Campus Connect!
   
   ### ✨ Features
   - 🔐 **User Authentication** - Secure login and signup with Firebase
   - 📝 **Notes Module** - Upload, view, search, and share PDF notes
   - 📊 **Attendance Tracker** - Track attendance with smart percentage calculations
   - 🎨 **Material Design 3** - Beautiful, modern UI
   - 💾 **100% Offline** - Works without internet (except first login)
   - 🔒 **Privacy-Focused** - All data stored locally on your device
   
   ### 📥 Installation
   1. Download the APK file below
   2. Enable "Install from Unknown Sources" on your Android device
   3. Install and enjoy!
   
   ### 📱 Requirements
   - Android 7.0 (Nougat) or higher (API 24+)
   - ~20 MB storage space
   - Internet connection for first login only
   
   ### 🐛 Known Issues
   - None reported yet!
   
   ### 🙏 Acknowledgments
   Built with ❤️ using Kotlin, Jetpack Compose, and Firebase.
   
   ---
   
   **Full Changelog**: https://github.com/FlameInDark-Team/Campus-Connect/commits/v1.0.0
   ```

5. **Upload APK**:
   - Drag and drop `app-debug.apk` into the release assets area
   - Or click "Attach binaries" and select the APK file

6. **Publish**:
   - If ready, click **Publish release**
   - Or click **Save draft** to publish later

---

## Step 3: Verify the Release

1. Go to: https://github.com/FlameInDark-Team/Campus-Connect/releases

2. You should see your release with the APK file

3. Test the download link:
   ```
   https://github.com/FlameInDark-Team/Campus-Connect/releases/download/v1.0.0/app-debug.apk
   ```

4. Share this link with users to download your app!

---

## 🎯 Quick Commands

### Build APK from Command Line (After fixing Gradle wrapper)

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Clean and rebuild
./gradlew clean assembleDebug
```

### Find the APK

```bash
# Debug APK
ls app/build/outputs/apk/debug/

# Release APK
ls app/build/outputs/apk/release/
```

---

## 🔧 Troubleshooting

### Issue: "Could not find or load main class org.gradle.wrapper.GradleWrapperMain"

**Solution**: See [FIX_GRADLE_WRAPPER.md](FIX_GRADLE_WRAPPER.md)

### Issue: Build fails with "Execution failed for task ':app:processDebugResources'"

**Solution**: 
1. Click **Build** → **Clean Project**
2. Click **Build** → **Rebuild Project**

### Issue: "google-services.json not found"

**Solution**: Make sure you have `google-services.json` in the `app/` directory

---

## 📝 Release Checklist

Before publishing a release:

- [ ] Build completes successfully
- [ ] App runs on emulator/device
- [ ] All features work as expected
- [ ] No critical bugs
- [ ] README is up to date
- [ ] Version number is updated in `build.gradle.kts`
- [ ] Release notes are written
- [ ] APK is tested on real device

---

## 🚀 Next Steps

After publishing your first release:

1. **Share** the download link with users
2. **Monitor** GitHub Issues for bug reports
3. **Plan** the next version with new features
4. **Update** the README with screenshots
5. **Promote** your app on social media

---

## 📞 Need Help?

- Check [FIX_GRADLE_WRAPPER.md](FIX_GRADLE_WRAPPER.md) for Gradle issues
- Open an issue on GitHub
- Review Android Studio build logs

---

**Happy Publishing! 🎉**
