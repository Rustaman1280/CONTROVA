package com.crustdev.controva

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.crustdev.controva.core.theme.ControvaTheme
import com.crustdev.controva.core.theme.DarkBackground
import com.crustdev.controva.data.model.AppSettings
import com.crustdev.controva.data.network.NetworkManager
import com.crustdev.controva.data.preferences.PreferencesManager
import com.crustdev.controva.data.sensor.GyroSensorManager
import com.crustdev.controva.presentation.navigation.ControvaNavGraph

class MainActivity : ComponentActivity() {

    private lateinit var networkManager: NetworkManager
    private lateinit var gyroSensorManager: GyroSensorManager
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Keep screen on during controller usage
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Immersive sticky fullscreen mode
        hideSystemBars()

        networkManager = NetworkManager()
        gyroSensorManager = GyroSensorManager(this)
        preferencesManager = PreferencesManager(this)

        setContent {
            val settings by preferencesManager.settingsFlow.collectAsState(initial = AppSettings())

            ControvaTheme(accentColor = settings.accentColor) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    val navController = rememberNavController()
                    ControvaNavGraph(
                        navController = navController,
                        networkManager = networkManager,
                        gyroSensorManager = gyroSensorManager,
                        preferencesManager = preferencesManager
                    )
                }
            }
        }
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
            )
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

    override fun onDestroy() {
        super.onDestroy()
        networkManager.disconnect()
        gyroSensorManager.stopListening()
    }
}
