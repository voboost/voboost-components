package ru.voboost.components.demo.compose

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

/**
 * Demo Compose Activity showcasing voboost-components in a Jetpack Compose project.
 *
 * This demo demonstrates:
 * - Proper Jetpack Compose integration with voboost-components library
 * - Using Compose wrappers for Java Custom Views via AndroidView
 * - State management with ViewModel for configuration persistence
 * - Automotive-oriented layout (1920x720 resolution)
 * - Multi-language support (English/Russian) with reactive updates
 * - Theme switching (Light/Dark) with car type variants (Free/Dreamer)
 * - 7-tab structure with dynamic content
 * - Reactive state management using Compose patterns
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable full-screen immersive mode for automotive display
        setupFullScreenMode()

        // Set Compose content
        setContent {
            VoboostDemoApp()
        }
    }

    /**
     * Sets up full-screen immersive mode to hide system navigation bar.
     * Uses modern WindowInsetsController API for Android 11+ (API 30+)
     * with fallback to deprecated flags for older versions.
     */
    private fun setupFullScreenMode() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Modern API for Android 11+ (API 30+)
                window.setDecorFitsSystemWindows(false)
                window.insetsController?.let { controller ->
                    controller.hide(WindowInsets.Type.systemBars())
                    controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                // Legacy API for Android 9-10 (API 28-29)
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
            }

            // Keep screen on for automotive use
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } catch (e: Exception) {
            // Log exceptions for debugging in development
            if (BuildConfig.DEBUG) {
                Log.w("MainActivity", "Window setup failed", e)
            }
        }
    }
}
