package com.crustdev.controva.core.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crustdev.controva.core.theme.DarkBorder
import com.crustdev.controva.core.theme.DarkSurface
import com.crustdev.controva.core.theme.DarkSurfaceVariant
import com.crustdev.controva.core.theme.TextPrimary
import com.crustdev.controva.core.theme.softGlow

@Composable
fun PedalControl(
    label: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    width: Dp = 68.dp,
    height: Dp = 150.dp,
    onPressureChanged: (Float) -> Unit
) {
    var pressure by remember { mutableFloatStateOf(0f) }
    var isPressed by remember { mutableStateOf(false) }
    val view = LocalView.current

    val glowAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.5f else 0.05f,
        label = "pedal_glow"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        label = "pedal_scale"
    )

    val shape = RoundedCornerShape(14.dp)

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
            .width(width)
            .height(height)
            .scale(scale)
            .softGlow(
                color = accentColor,
                alpha = glowAlpha,
                radius = 18.dp,
                borderRadius = 14.dp
            )
            .border(
                width = if (isPressed) 1.5.dp else 1.dp,
                color = if (isPressed) accentColor else DarkBorder,
                shape = shape
            )
            .clip(shape)
            .background(DarkSurface)
            .pointerInput(Unit) {
                val totalH = height.toPx()

                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)

                    fun updatePressure(yPos: Float) {
                        // Dragging upwards increases pressure
                        val ratio = (1.0f - (yPos / totalH)).coerceIn(0.2f, 1.0f)
                        pressure = ratio
                        onPressureChanged(ratio)
                    }

                    updatePressure(down.position.y)

                    do {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull()
                        if (change != null && change.pressed) {
                            updatePressure(change.position.y)
                            change.consume()
                        }
                    } while (event.changes.any { it.pressed })

                    isPressed = false
                    pressure = 0f
                    onPressureChanged(0f)
                }
            }
    ) {
        // Progressive fill bar
        if (pressure > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(pressure)
                    .background(accentColor.copy(alpha = 0.25f))
            )
        }

        // Tactile tread ribs
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPressed) accentColor else TextPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            repeat(4) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 3.dp)
                        .width(width * 0.5f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isPressed) accentColor.copy(alpha = 0.6f) else DarkBorder)
                )
            }
        }
    }
}
