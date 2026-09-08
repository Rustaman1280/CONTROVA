package com.crustdev.controva.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crustdev.controva.core.theme.CrimsonRed
import com.crustdev.controva.core.theme.DarkBackground
import com.crustdev.controva.core.theme.DarkBorder
import com.crustdev.controva.core.theme.DarkSurface
import com.crustdev.controva.core.theme.DarkSurfaceElevated
import com.crustdev.controva.core.theme.DarkSurfaceVariant
import com.crustdev.controva.core.theme.ElectricCyan
import com.crustdev.controva.core.theme.ElectricViolet
import com.crustdev.controva.core.theme.EmeraldGreen
import com.crustdev.controva.core.theme.LocalAccentColor
import com.crustdev.controva.core.theme.TextPrimary
import com.crustdev.controva.core.theme.TextSecondary
import com.crustdev.controva.core.theme.TextTertiary
import com.crustdev.controva.core.theme.softGlow
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val accentColor = LocalAccentColor.current
    val scrollState = rememberScrollState()

    val colorOptions = listOf(
        "#00F0FF" to ElectricCyan,
        "#A855F7" to ElectricViolet,
        "#10B981" to EmeraldGreen,
        "#EF4444" to CrimsonRed
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(DarkSurface)
                        .border(1.dp, DarkBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(
                        text = "CONTROLLER SETTINGS",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Tune sensitivity, visual glow, and input preferences",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Accent Glow Color
            SettingsCard(title = "ACCENT GLOW COLOR") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    colorOptions.forEach { (hex, color) ->
                        val isSelected = settings.accentColorHex == hex
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(42.dp)
                                .softGlow(
                                    color = color,
                                    alpha = if (isSelected) 0.6f else 0.0f,
                                    radius = 16.dp,
                                    isCircle = true
                                )
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) TextPrimary else DarkBorder,
                                    shape = CircleShape
                                )
                                .clickable { viewModel.updateAccentColor(hex) }
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = DarkBackground,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section: Analog Sticks
            SettingsCard(title = "ANALOG STICKS") {
                // Sensitivity
                Text(
                    text = "Stick Sensitivity: ${(settings.stickSensitivity * 100).roundToInt()}%",
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Slider(
                    value = settings.stickSensitivity,
                    onValueChange = { viewModel.updateStickSensitivity(it) },
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = accentColor,
                        activeTrackColor = accentColor,
                        inactiveTrackColor = DarkSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Deadzone
                Text(
                    text = "Stick Deadzone: ${(settings.stickDeadzone * 100).roundToInt()}%",
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Slider(
                    value = settings.stickDeadzone,
                    onValueChange = { viewModel.updateStickDeadzone(it) },
                    valueRange = 0.02f..0.25f,
                    colors = SliderDefaults.colors(
                        thumbColor = accentColor,
                        activeTrackColor = accentColor,
                        inactiveTrackColor = DarkSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section: Gyroscope & Motion
            SettingsCard(title = "GYROSCOPE & SENSOR FUSION") {
                Text(
                    text = "Gyro Aim / Steering Sensitivity: ${(settings.gyroSensitivity * 100).roundToInt()}%",
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Slider(
                    value = settings.gyroSensitivity,
                    onValueChange = { viewModel.updateGyroSensitivity(it) },
                    valueRange = 0.5f..3.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = accentColor,
                        activeTrackColor = accentColor,
                        inactiveTrackColor = DarkSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section: Feedback & Network
            SettingsCard(title = "PREFERENCES") {
                // Haptic Feedback
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Haptic Vibration Feedback",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Vibrate on button press and trigger pulls",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Switch(
                        checked = settings.hapticFeedback,
                        onCheckedChange = { viewModel.updateHapticFeedback(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DarkBackground,
                            checkedTrackColor = accentColor
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // UDP Network Protocol
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Use Raw UDP Socket",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Lowest latency protocol without TCP handshakes",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Switch(
                        checked = settings.useUdp,
                        onCheckedChange = { viewModel.updateUseUdp(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DarkBackground,
                            checkedTrackColor = accentColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section: About
            SettingsCard(title = "ABOUT CONTROVA") {
                Text(
                    text = "CONTROVA v1.0.0",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Ultra-low latency virtual wireless gamepad for PC and gaming hosts. Built with Jetpack Compose & Kotlin Coroutines.",
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = TextTertiary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}
