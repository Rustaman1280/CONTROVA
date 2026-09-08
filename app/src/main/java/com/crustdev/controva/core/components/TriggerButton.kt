package com.crustdev.controva.core.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crustdev.controva.core.theme.DarkBorder
import com.crustdev.controva.core.theme.DarkSurface
import com.crustdev.controva.core.theme.DarkSurfaceVariant
import com.crustdev.controva.core.theme.LocalAccentColor
import com.crustdev.controva.core.theme.TextPrimary
import com.crustdev.controva.core.theme.softGlow

@Composable
fun TriggerButton(
    label: String,
    modifier: Modifier = Modifier,
    width: Dp = 76.dp,
    height: Dp = 38.dp,
    isAnalog: Boolean = false,
    onPressChanged: (Boolean) -> Unit = {},
    onPressureChanged: (Float) -> Unit = {}
) {
    var isPressed by remember { mutableStateOf(false) }
    var pressure by remember { mutableFloatStateOf(0f) }
    val accentColor = LocalAccentColor.current
    val view = LocalView.current

    val glowAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.55f else 0.0f,
        label = "trigger_glow"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        label = "trigger_scale"
    )

    val shape = RoundedCornerShape(10.dp)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(width)
            .height(height)
            .scale(scale)
            .softGlow(
                color = accentColor,
                alpha = glowAlpha,
                radius = 16.dp,
                borderRadius = 10.dp
            )
            .border(
                width = if (isPressed) 1.5.dp else 1.dp,
                color = if (isPressed) accentColor else DarkBorder,
                shape = shape
            )
            .clip(shape)
            .background(if (isPressed) DarkSurfaceVariant else DarkSurface)
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)

                    if (isAnalog) {
                        // Vertical drag tracks analog trigger pull depth (0.0 to 1.0)
                        val totalHeightPx = height.toPx()
                        fun calculatePressure(yPos: Float) {
                            val norm = (yPos / totalHeightPx).coerceIn(0.2f, 1.0f)
                            pressure = norm
                            onPressureChanged(norm)
                        }
                        calculatePressure(down.position.y)
                        onPressChanged(true)

                        do {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull()
                            if (change != null && change.pressed) {
                                calculatePressure(change.position.y)
                                change.consume()
                            }
                        } while (event.changes.any { it.pressed })

                        isPressed = false
                        pressure = 0f
                        onPressureChanged(0f)
                        onPressChanged(false)
                    } else {
                        onPressChanged(true)
                        val up = waitForUpOrCancellation()
                        isPressed = false
                        onPressChanged(false)
                    }

                }
            }
    ) {
        // Analog fill level bar if applicable
        if (isAnalog && pressure > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(pressure)
                        .align(Alignment.BottomCenter)
                        .background(accentColor.copy(alpha = 0.2f))
                )
            }
        }

        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isPressed) accentColor else TextPrimary
        )
    }
}
