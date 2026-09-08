package com.crustdev.controva.presentation.navigation

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.crustdev.controva.data.model.ControllerType
import com.crustdev.controva.data.network.NetworkManager
import com.crustdev.controva.data.preferences.PreferencesManager
import com.crustdev.controva.data.sensor.GyroSensorManager
import com.crustdev.controva.presentation.connect.ConnectScreen
import com.crustdev.controva.presentation.connect.ConnectViewModel
import com.crustdev.controva.presentation.controller.ControllerScreen
import com.crustdev.controva.presentation.controller.ControllerViewModel
import com.crustdev.controva.presentation.layoutpicker.LayoutPickerScreen
import com.crustdev.controva.presentation.settings.SettingsScreen
import com.crustdev.controva.presentation.settings.SettingsViewModel
import com.crustdev.controva.presentation.splash.SplashScreen

@Composable
fun ControvaNavGraph(
    navController: NavHostController,
    networkManager: NetworkManager,
    gyroSensorManager: GyroSensorManager,
    preferencesManager: PreferencesManager
) {
    val context = LocalContext.current
    val activity = context as? Activity

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Connect.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Connect Screen
        composable(Screen.Connect.route) {
            // Restore natural orientation for connect screen
            DisposableEffect(Unit) {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                onDispose {}
            }

            val connectViewModel = remember {
                ConnectViewModel(networkManager, preferencesManager)
            }

            ConnectScreen(
                viewModel = connectViewModel,
                onNavigateToLayoutPicker = {
                    navController.navigate(Screen.LayoutPicker.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        // Layout Picker Screen
        composable(Screen.LayoutPicker.route) {
            DisposableEffect(Unit) {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                onDispose {}
            }

            LayoutPickerScreen(
                onSelectLayout = { layout ->
                    navController.navigate(Screen.Controller.createRoute(layout.name))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Controller Screen (Immersive Landscape Mode)
        composable(
            route = Screen.Controller.route,
            arguments = listOf(
                navArgument("layoutName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val layoutName = backStackEntry.arguments?.getString("layoutName") ?: ControllerType.PLAYSTATION.name
            val layoutType = try {
                ControllerType.valueOf(layoutName)
            } catch (_: Exception) {
                ControllerType.PLAYSTATION
            }

            // Lock to sensor landscape for gaming controller experience
            DisposableEffect(Unit) {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                onDispose {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
            }

            val controllerViewModel = remember(layoutType) {
                ControllerViewModel(networkManager, gyroSensorManager, preferencesManager, layoutType)
            }

            ControllerScreen(
                viewModel = controllerViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        // Settings Screen
        composable(Screen.Settings.route) {
            DisposableEffect(Unit) {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                onDispose {}
            }

            val settingsViewModel = remember {
                SettingsViewModel(preferencesManager)
            }

            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
