package com.crustdev.controva.presentation.controller.layouts

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crustdev.controva.core.components.GlowButton
import com.crustdev.controva.core.components.PedalControl
import com.crustdev.controva.core.components.SteeringWheel
import com.crustdev.controva.core.theme.CrimsonRed
import com.crustdev.controva.core.theme.DarkBorder
import com.crustdev.controva.core.theme.DarkSurface
import com.crustdev.controva.core.theme.DarkSurfaceVariant
import com.crustdev.controva.core.theme.EmeraldGreen
import com.crustdev.controva.core.theme.LocalAccentColor
import com.crustdev.controva.core.theme.PillShape
import com.crustdev.controva.core.theme.TextPrimary
import com.crustdev.controva.core.theme.TextSecondary
import com.crustdev.controva.core.theme.softGlow
import com.crustdev.controva.data.model.ControllerEvent

@Composable
fun SteeringWheelLayout(
    gyroNormalizedTurn: Float,
    isGyroModeInitial: Boolean = false,
    onRecenterGyro: () -> Unit,
    onSendEvent: (ControllerEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var isGyroMode by remember { mutableStateOf(isGyroModeInitial) }
    var currentGearIndex by remember { mutableIntStateOf(1) } // 0=R, 1=N, 2=1, 3=2, 4=3, 5=4, 6=5, 7=6
    val gearNames = listOf("R", "N", "1", "2", "3", "4", "5", "6")
    val accentColor = LocalAccentColor.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        // Top Dashboard Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Steering Mode Selector (Touch Drag vs Gyro Tilt)
            Row(
                modifier = Modifier
                    .clip(PillShape)
                    .background(DarkSurface)
                    .border(1.dp, DarkBorder, PillShape)
                    .padding(3.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(PillShape)
                        .background(if (!isGyroMode) accentColor.copy(alpha = 0.2f) else DarkSurface)
                        .border(1.dp, if (!isGyroMode) accentColor else DarkBorder, PillShape)
                        .clickable { isGyroMode = false }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "TOUCH DRAG",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!isGyroMode) accentColor else TextSecondary
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(PillShape)
                        .background(if (isGyroMode) accentColor.copy(alpha = 0.2f) else DarkSurface)
                        .border(1.dp, if (isGyroMode) accentColor else DarkBorder, PillShape)
                        .clickable { isGyroMode = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "GYRO TILT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isGyroMode) accentColor else TextSecondary
                    )
                }
            }

            // Gear Indicator Hub
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(PillShape)
                    .background(DarkSurface)
                    .border(1.dp, DarkBorder, PillShape)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "GEAR",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(26.dp)
                        .clip(PillShape)
                        .background(DarkSurfaceVariant)
                        .border(1.dp, accentColor, PillShape)
                ) {
                    Text(
                        text = gearNames[currentGearIndex],
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }

            // Recenter Calibration & Handbrake
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (isGyroMode) {
                    GlowButton(
                        label = "RECENTER",
                        size = 46.dp,
                        shape = RoundedCornerShape(10.dp),
                        isCircle = false,
                        onPressChanged = { if (it) onRecenterGyro() }
                    )
                }

                GlowButton(
                    label = "HANDBRAKE",
                    size = 46.dp,
                    shape = RoundedCornerShape(10.dp),
                    isCircle = false,
                    symbolColor = CrimsonRed,
                    onPressChanged = { onSendEvent(ControllerEvent.Button("HANDBRAKE", it)) }
                )
            }
        }

        // Center Steering Wheel
        Box(
            modifier = Modifier.align(Alignment.Center)
        ) {
            SteeringWheel(
                size = 230.dp,
                isGyroMode = isGyroMode,
                gyroTurnNormalized = gyroNormalizedTurn,
                onSteer = { angle, normalized ->
                    onSendEvent(ControllerEvent.Wheel(angle, normalized))
                }
            )
        }

        // Left Controls: Paddle Shifter (-) & Brake Pedal
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Paddle Shift Down (-)
            GlowButton(
                label = "-",
                size = 58.dp,
                shape = RoundedCornerShape(12.dp),
                isCircle = false,
                onPressChanged = { pressed ->
                    if (pressed && currentGearIndex > 0) {
                        currentGearIndex--
                        onSendEvent(ControllerEvent.Button("SHIFT_DOWN", true))
                    } else if (!pressed) {
                        onSendEvent(ControllerEvent.Button("SHIFT_DOWN", false))
                    }
                }
            )

            // Brake Pedal
            PedalControl(
                label = "BRAKE",
                accentColor = CrimsonRed,
                height = 145.dp,
                width = 68.dp,
                onPressureChanged = { pressure ->
                    onSendEvent(ControllerEvent.Trigger("BRAKE", pressure))
                }
            )
        }

        // Right Controls: Gas Pedal & Paddle Shifter (+)
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-16).dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Gas / Throttle Pedal
            PedalControl(
                label = "THROTTLE",
                accentColor = EmeraldGreen,
                height = 145.dp,
                width = 68.dp,
                onPressureChanged = { pressure ->
                    onSendEvent(ControllerEvent.Trigger("THROTTLE", pressure))
                }
            )

            // Paddle Shift Up (+)
            GlowButton(
                label = "+",
                size = 58.dp,
                shape = RoundedCornerShape(12.dp),
                isCircle = false,
                onPressChanged = { pressed ->
                    if (pressed && currentGearIndex < gearNames.size - 1) {
                        currentGearIndex++
                        onSendEvent(ControllerEvent.Button("SHIFT_UP", true))
                    } else if (!pressed) {
                        onSendEvent(ControllerEvent.Button("SHIFT_UP", false))
                    }
                }
            )
        }
    }
}
