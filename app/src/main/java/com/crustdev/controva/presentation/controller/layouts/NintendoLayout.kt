package com.crustdev.controva.presentation.controller.layouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.crustdev.controva.core.theme.TextPrimary
import com.crustdev.controva.data.model.ControllerEvent

@Composable
fun NintendoLayout(
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
            // Left Bumpers (ZL, L)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TriggerButton(
                    label = "ZL",
                    isAnalog = true,
                    onPressChanged = { onSendEvent(ControllerEvent.Button("ZL", it)) },
                    onPressureChanged = { onSendEvent(ControllerEvent.Trigger("ZL", it)) }
                )
                TriggerButton(
                    label = "L",
                    isAnalog = false,
                    onPressChanged = { onSendEvent(ControllerEvent.Button("L", it)) }
                )
            }

            // Center - / + Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                GlowButton(
                    label = "-",
                    size = 46.dp,
                    shape = ButtonShape,
                    isCircle = false,
                    onPressChanged = { onSendEvent(ControllerEvent.Button("MINUS", it)) }
                )
                GlowButton(
                    label = "HOME",
                    size = 46.dp,
                    shape = ButtonShape,
                    isCircle = false,
                    onPressChanged = { onSendEvent(ControllerEvent.Button("HOME", it)) }
                )
                GlowButton(
                    label = "+",
                    size = 46.dp,
                    shape = ButtonShape,
                    isCircle = false,
                    onPressChanged = { onSendEvent(ControllerEvent.Button("PLUS", it)) }
                )
            }

            // Right Bumpers (R, ZR)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TriggerButton(
                    label = "R",
                    isAnalog = false,
                    onPressChanged = { onSendEvent(ControllerEvent.Button("R", it)) }
                )
                TriggerButton(
                    label = "ZR",
                    isAnalog = true,
                    onPressChanged = { onSendEvent(ControllerEvent.Button("ZR", it)) },
                    onPressureChanged = { onSendEvent(ControllerEvent.Trigger("ZR", it)) }
                )
            }
        }

        // Left Side: Left Stick (Top-Left) and D-Pad (Bottom-Left)
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 10.dp, y = (-24).dp)
        ) {
            AnalogStick(
                label = "L",
                size = 135.dp,
                onMove = { x, y -> onSendEvent(ControllerEvent.Stick("left", x, y)) }
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 120.dp, y = (-12).dp)
        ) {
            DPad(
                size = 125.dp,
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

        // Right Side: Nintendo Diamond ABXY (X top, A right, B bottom, Y left)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-10).dp, y = (-24).dp)
                .size(150.dp),
            contentAlignment = Alignment.Center
        ) {
            // X (Top)
            GlowButton(
                label = "X",
                symbolColor = TextPrimary,
                size = 48.dp,
                modifier = Modifier.align(Alignment.TopCenter),
                onPressChanged = { onSendEvent(ControllerEvent.Button("X", it)) }
            )
            // A (Right)
            GlowButton(
                label = "A",
                symbolColor = TextPrimary,
                size = 48.dp,
                modifier = Modifier.align(Alignment.CenterEnd),
                onPressChanged = { onSendEvent(ControllerEvent.Button("A", it)) }
            )
            // B (Bottom)
            GlowButton(
                label = "B",
                symbolColor = TextPrimary,
                size = 48.dp,
                modifier = Modifier.align(Alignment.BottomCenter),
                onPressChanged = { onSendEvent(ControllerEvent.Button("B", it)) }
            )
            // Y (Left)
            GlowButton(
                label = "Y",
                symbolColor = TextPrimary,
                size = 48.dp,
                modifier = Modifier.align(Alignment.CenterStart),
                onPressChanged = { onSendEvent(ControllerEvent.Button("Y", it)) }
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-120).dp, y = (-12).dp)
        ) {
            AnalogStick(
                label = "R",
                size = 135.dp,
                onMove = { x, y -> onSendEvent(ControllerEvent.Stick("right", x, y)) }
            )
        }
    }
}
