package com.crustdev.controva.presentation.controller.layouts

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.crustdev.controva.core.components.AnalogStick
import com.crustdev.controva.core.components.DPad
import com.crustdev.controva.core.components.DPadDirection
import com.crustdev.controva.core.components.GlowButton
import com.crustdev.controva.core.components.TriggerButton
import com.crustdev.controva.core.theme.ButtonShape
import com.crustdev.controva.core.theme.PsCircle
import com.crustdev.controva.core.theme.PsCross
import com.crustdev.controva.core.theme.PsSquare
import com.crustdev.controva.core.theme.PsTriangle
import com.crustdev.controva.data.model.ControllerEvent

@Composable
fun PlayStationLayout(
    onSendEvent: (ControllerEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Top Shoulders / Triggers Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Shoulder (L1, L2)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TriggerButton(
                    label = "L2",
                    isAnalog = true,
                    onPressChanged = { onSendEvent(ControllerEvent.Button("L2", it)) },
                    onPressureChanged = { onSendEvent(ControllerEvent.Trigger("L2", it)) }
                )
                TriggerButton(
                    label = "L1",
                    isAnalog = false,
                    onPressChanged = { onSendEvent(ControllerEvent.Button("L1", it)) }
                )
            }

            // Center System Buttons (Share / Options)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                GlowButton(
                    label = "SHARE",
                    size = 50.dp,
                    shape = ButtonShape,
                    isCircle = false,
                    onPressChanged = { onSendEvent(ControllerEvent.Button("SHARE", it)) }
                )
                GlowButton(
                    label = "OPTIONS",
                    size = 50.dp,
                    shape = ButtonShape,
                    isCircle = false,
                    onPressChanged = { onSendEvent(ControllerEvent.Button("OPTIONS", it)) }
                )
            }

            // Right Shoulder (R1, R2)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TriggerButton(
                    label = "R1",
                    isAnalog = false,
                    onPressChanged = { onSendEvent(ControllerEvent.Button("R1", it)) }
                )
                TriggerButton(
                    label = "R2",
                    isAnalog = true,
                    onPressChanged = { onSendEvent(ControllerEvent.Button("R2", it)) },
                    onPressureChanged = { onSendEvent(ControllerEvent.Trigger("R2", it)) }
                )
            }
        }

        // Left Controls: D-Pad
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(y = (-10).dp)
        ) {
            DPad(
                size = 145.dp,
                onDirectionChanged = { dir, pressed ->
                    val btnId = when (dir) {
                        DPadDirection.UP -> "DPAD_UP"
                        DPadDirection.DOWN -> "DPAD_DOWN"
                        DPadDirection.LEFT -> "DPAD_LEFT"
                        DPadDirection.RIGHT -> "DPAD_RIGHT"
                    }
                    onSendEvent(ControllerEvent.Button(btnId, pressed))
                }
            )
        }

        // Center-Bottom Dual Sticks (Symmetrical Layout)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-4).dp),
            horizontalArrangement = Arrangement.spacedBy(42.dp)
        ) {
            AnalogStick(
                label = "L3",
                size = 135.dp,
                onMove = { x, y -> onSendEvent(ControllerEvent.Stick("left", x, y)) }
            )
            AnalogStick(
                label = "R3",
                size = 135.dp,
                onMove = { x, y -> onSendEvent(ControllerEvent.Stick("right", x, y)) }
            )
        }

        // Right Controls: PlayStation Symbols (△, ○, ✕, □)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(y = (-10).dp)
                .size(150.dp),
            contentAlignment = Alignment.Center
        ) {
            // Triangle (Top)
            GlowButton(
                label = "△",
                symbolColor = PsTriangle,
                size = 48.dp,
                modifier = Modifier.align(Alignment.TopCenter),
                onPressChanged = { onSendEvent(ControllerEvent.Button("TRIANGLE", it)) }
            )
            // Circle (Right)
            GlowButton(
                label = "○",
                symbolColor = PsCircle,
                size = 48.dp,
                modifier = Modifier.align(Alignment.CenterEnd),
                onPressChanged = { onSendEvent(ControllerEvent.Button("CIRCLE", it)) }
            )
            // Cross (Bottom)
            GlowButton(
                label = "✕",
                symbolColor = PsCross,
                size = 48.dp,
                modifier = Modifier.align(Alignment.BottomCenter),
                onPressChanged = { onSendEvent(ControllerEvent.Button("CROSS", it)) }
            )
            // Square (Left)
            GlowButton(
                label = "□",
                symbolColor = PsSquare,
                size = 48.dp,
                modifier = Modifier.align(Alignment.CenterStart),
                onPressChanged = { onSendEvent(ControllerEvent.Button("SQUARE", it)) }
            )
        }
    }
}
