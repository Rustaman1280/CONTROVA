package com.crustdev.controva.presentation.controller

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crustdev.controva.core.components.ConnectionStatusBadge
import com.crustdev.controva.core.theme.DarkBackground
import com.crustdev.controva.core.theme.DarkBorder
import com.crustdev.controva.core.theme.DarkSurface
import com.crustdev.controva.core.theme.LocalAccentColor
import com.crustdev.controva.core.theme.PillShape
import com.crustdev.controva.core.theme.TextPrimary
import com.crustdev.controva.core.theme.TextSecondary
import com.crustdev.controva.core.theme.softGlow
import com.crustdev.controva.data.model.ControllerType
import com.crustdev.controva.presentation.controller.layouts.NintendoLayout
import com.crustdev.controva.presentation.controller.layouts.PlayStationLayout
import com.crustdev.controva.presentation.controller.layouts.SteeringWheelLayout
import com.crustdev.controva.presentation.controller.layouts.XboxLayout

@Composable
fun ControllerScreen(
    viewModel: ControllerViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val currentLayout by viewModel.layout.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    val gyroData by viewModel.gyroData.collectAsState()
    val isGyroEnabled by viewModel.isGyroEnabled.collectAsState()
    val accentColor = LocalAccentColor.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Main Controller Layout with Animated Transition
        AnimatedContent(
            targetState = currentLayout,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "layout_transition",
            modifier = Modifier.fillMaxSize()
        ) { layout ->
            when (layout) {
                ControllerType.PLAYSTATION -> PlayStationLayout(
                    onSendEvent = { viewModel.sendEvent(it) }
                )
                ControllerType.XBOX -> XboxLayout(
                    onSendEvent = { viewModel.sendEvent(it) }
                )
                ControllerType.NINTENDO -> NintendoLayout(
                    onSendEvent = { viewModel.sendEvent(it) }
                )
                ControllerType.STEERING_WHEEL -> SteeringWheelLayout(
                    gyroNormalizedTurn = gyroData.normalizedWheelTurn,
                    isGyroModeInitial = isGyroEnabled,
                    onRecenterGyro = { viewModel.recenterGyro() },
                    onSendEvent = { viewModel.sendEvent(it) }
                )
            }
        }

        // Top Minimalist Control Overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Exit Button
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(DarkSurface.copy(alpha = 0.8f))
                    .border(1.dp, DarkBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Exit",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Center Connection Pill
            ConnectionStatusBadge(
                connectionState = connectionState,
                gyroEnabled = isGyroEnabled
            )

            // Right Quick Actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Layout Switcher Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(PillShape)
                        .background(DarkSurface.copy(alpha = 0.8f))
                        .border(1.dp, DarkBorder, PillShape)
                        .clickable {
                            // Cycle through layouts
                            val all = ControllerType.values()
                            val next = all[(currentLayout.ordinal + 1) % all.size]
                            viewModel.setLayout(next)
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Switch Layout",
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = currentLayout.displayName.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // Gyro Toggle
                IconButton(
                    onClick = { viewModel.toggleGyro() },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (isGyroEnabled) accentColor.copy(alpha = 0.2f) else DarkSurface.copy(alpha = 0.8f))
                        .border(1.dp, if (isGyroEnabled) accentColor else DarkBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = "Gyro",
                        tint = if (isGyroEnabled) accentColor else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Recenter Gyro
                IconButton(
                    onClick = { viewModel.recenterGyro() },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(DarkSurface.copy(alpha = 0.8f))
                        .border(1.dp, DarkBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Recenter",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Settings Shortcut
                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(DarkSurface.copy(alpha = 0.8f))
                        .border(1.dp, DarkBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
