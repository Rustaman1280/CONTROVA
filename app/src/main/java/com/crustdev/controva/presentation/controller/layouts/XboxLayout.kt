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
import com.crustdev.controva.core.theme.XboxA
import com.crustdev.controva.core.theme.XboxB
import com.crustdev.controva.core.theme.XboxX
import com.crustdev.controva.core.theme.XboxY
import com.crustdev.controva.data.model.ControllerEvent

@Composable
fun XboxLayout(
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
            // Left Bumpers (LT, LB)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TriggerButton(
                    label = "LT",
                    isAnalog = true,
                    onPressChanged = { onSendEvent(ControllerEvent.Button("LT", it)) },
                    onPressureChanged = { onSendEvent(ControllerEvent.Trigger("LT", it)) }
                )
                TriggerButton(
                    label = "LB",
                    isAnalog = false,
                    onPressChanged = { onSendEvent(ControllerEvent.Button("LB", it)) }
                )
            }

            // Center View / Menu
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                GlowButton(
                    label = "VIEW",
                    size = 50.dp,
                    shape = ButtonShape,
                    isCircle = false,
                    onPressChanged = { onSendEvent(ControllerEvent.Button("VIEW", it)) }
                )
                GlowButton(
                    label = "MENU",
                    size = 50.dp,
                    shape = ButtonShape,
                    isCircle = false,
                    onPressChanged = { onSendEvent(ControllerEvent.Button("MENU", it)) }
                )
            }

            // Right Bumpers (RB, RT)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TriggerButton(
                    label = "RB",
                    isAnalog = false,
                    onPressChanged = { onSendEvent(ControllerEvent.Button("RB", it)) }
                )
                TriggerButton(
                    label = "RT",
                    isAnalog = true,
                    onPressChanged = { onSendEvent(ControllerEvent.Button("RT", it)) },
                    onPressureChanged = { onSendEvent(ControllerEvent.Trigger("RT", it)) }
                )
            }
        }

        // Left Side: Asymmetrical Stick (Top-Left) and D-Pad (Bottom-Left)
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 10.dp, y = (-24).dp)
        ) {
            AnalogStick(
                label = "LS",
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

        // Right Side: ABXY Diamond (Top-Right) and Right Stick (Bottom-Right)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-10).dp, y = (-24).dp)
                .size(150.dp),
            contentAlignment = Alignment.Center
        ) {
            // Y (Top)
            GlowButton(
                label = "Y",
                symbolColor = XboxY,
                size = 48.dp,
                modifier = Modifier.align(Alignment.TopCenter),
                onPressChanged = { onSendEvent(ControllerEvent.Button("Y", it)) }
            )
            // B (Right)
            GlowButton(
                label = "B",
                symbolColor = XboxB,
                size = 48.dp,
                modifier = Modifier.align(Alignment.CenterEnd),
                onPressChanged = { onSendEvent(ControllerEvent.Button("B", it)) }
            )
            // A (Bottom)
            GlowButton(
                label = "A",
                symbolColor = XboxA,
                size = 48.dp,
                modifier = Modifier.align(Alignment.BottomCenter),
                onPressChanged = { onSendEvent(ControllerEvent.Button("A", it)) }
            )
            // X (Left)
            GlowButton(
                label = "X",
                symbolColor = XboxX,
                size = 48.dp,
                modifier = Modifier.align(Alignment.CenterStart),
                onPressChanged = { onSendEvent(ControllerEvent.Button("X", it)) }
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-120).dp, y = (-12).dp)
        ) {
            AnalogStick(
                label = "RS",
                size = 135.dp,
                onMove = { x, y -> onSendEvent(ControllerEvent.Stick("right", x, y)) }
            )
        }
    }
}
