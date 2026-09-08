package com.crustdev.controva.data.model

import androidx.compose.ui.graphics.Color
import com.crustdev.controva.core.theme.CrimsonRed
import com.crustdev.controva.core.theme.ElectricCyan
import com.crustdev.controva.core.theme.ElectricViolet
import com.crustdev.controva.core.theme.EmeraldGreen

data class AppSettings(
    val accentColorHex: String = "#00F0FF",
    val stickSensitivity: Float = 1.0f,
    val stickDeadzone: Float = 0.08f,
    val gyroSensitivity: Float = 1.0f,
    val gyroEnabled: Boolean = true,
    val steeringModeGyro: Boolean = false, // false = touch drag, true = gyro tilt
    val hapticFeedback: Boolean = true,
    val useUdp: Boolean = false,
    val lastHostIp: String = "192.168.1.100",
    val lastPort: Int = 8080,
    val lastLayout: ControllerType = ControllerType.PLAYSTATION
) {
    val accentColor: Color
        get() = when (accentColorHex) {
            "#A855F7" -> ElectricViolet
            "#10B981" -> EmeraldGreen
            "#EF4444" -> CrimsonRed
            else -> ElectricCyan
        }
}
