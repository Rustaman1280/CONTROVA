package com.crustdev.controva.core.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crustdev.controva.core.theme.DarkBackground
import com.crustdev.controva.core.theme.DarkBorder
import com.crustdev.controva.core.theme.DarkBorderSubtle
import com.crustdev.controva.core.theme.DarkSurface
import com.crustdev.controva.core.theme.DarkSurfaceElevated
import com.crustdev.controva.core.theme.DarkSurfaceVariant
import com.crustdev.controva.core.theme.LocalAccentColor
import com.crustdev.controva.core.theme.TextTertiary
import com.crustdev.controva.core.theme.softGlow
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun AnalogStick(
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
    thumbSize: Dp = 56.dp,
    label: String = "L",
    deadzone: Float = 0.08f,
    sensitivity: Float = 1.0f,
    onMove: (x: Float, y: Float) -> Unit,
    onClick: (() -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val accentColor = LocalAccentColor.current

    val animOffsetX = remember { Animatable(0f) }
    val animOffsetY = remember { Animatable(0f) }

    var isInteracting by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(DarkSurface.copy(alpha = 0.7f))
            .border(1.dp, DarkBorder, CircleShape)
            .pointerInput(Unit) {
                val baseRadius = (size.toPx() - thumbSize.toPx()) / 2f

                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    isInteracting = true

                    val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
                    var currentPos = down.position - center

                    fun updateStick(pos: Offset) {
                        val distance = sqrt(pos.x * pos.x + pos.y * pos.y)
                        val angle = atan2(pos.y, pos.x)

                        val clampedDistance = distance.coerceAtMost(baseRadius)
                        val clampedX = cos(angle) * clampedDistance
                        val clampedY = sin(angle) * clampedDistance

                        coroutineScope.launch {
                            animOffsetX.snapTo(clampedX)
                            animOffsetY.snapTo(clampedY)
                        }

                        // Normalized values -1.0 .. 1.0
                        val rawNormX = (clampedX / baseRadius) * sensitivity
                        val rawNormY = -(clampedY / baseRadius) * sensitivity // Invert Y so up is positive

                        val normDistance = sqrt(rawNormX * rawNormX + rawNormY * rawNormY)
                        if (normDistance < deadzone) {
                            onMove(0f, 0f)
                        } else {
                            onMove(
                                rawNormX.coerceIn(-1.0f, 1.0f),
                                rawNormY.coerceIn(-1.0f, 1.0f)
                            )
                        }
                    }

                    updateStick(currentPos)

                    // Track drag movements
                    do {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull()
                        if (change != null && change.pressed) {
                            currentPos = change.position - center
                            updateStick(currentPos)
                            change.consume()
                        }
                    } while (event.changes.any { it.pressed })

                    // Released: spring back to center
                    isInteracting = false
                    onMove(0f, 0f)
                    coroutineScope.launch {
                        animOffsetX.animateTo(0f, spring(dampingRatio = 0.6f, stiffness = 800f))
                    }
                    coroutineScope.launch {
                        animOffsetY.animateTo(0f, spring(dampingRatio = 0.6f, stiffness = 800f))
                    }
                }
            }
    ) {
        // Subtle crosshair guides inside base
        Canvas(modifier = Modifier.size(size)) {
            val stroke = Stroke(width = 1f)
            val c = size.toPx() / 2f
            val r = (size.toPx() - thumbSize.toPx()) / 2f

            // Outer boundary guide
            drawCircle(
                color = DarkBorderSubtle,
                radius = r,
                center = Offset(c, c),
                style = stroke
            )
            // Center cross lines
            drawLine(DarkBorderSubtle, Offset(c - 12.dp.toPx(), c), Offset(c + 12.dp.toPx(), c), strokeWidth = 1.5f)
            drawLine(DarkBorderSubtle, Offset(c, c - 12.dp.toPx()), Offset(c, c + 12.dp.toPx()), strokeWidth = 1.5f)
        }

        // Draggable Thumb Knob
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .offset {
                    IntOffset(
                        animOffsetX.value.roundToInt(),
                        animOffsetY.value.roundToInt()
                    )
                }
                .size(thumbSize)
                .softGlow(
                    color = accentColor,
                    alpha = if (isInteracting) 0.5f else 0.0f,
                    radius = 16.dp,
                    isCircle = true
                )
                .clip(CircleShape)
                .background(if (isInteracting) DarkSurfaceVariant else DarkSurfaceElevated)
                .border(
                    width = if (isInteracting) 1.5.dp else 1.dp,
                    color = if (isInteracting) accentColor else DarkBorder,
                    shape = CircleShape
                )
        ) {
            // Knob tactile grip ring
            Canvas(modifier = Modifier.size(thumbSize * 0.7f)) {
                drawCircle(
                    color = if (isInteracting) accentColor.copy(alpha = 0.3f) else DarkBorderSubtle,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            Text(
                text = label,
                fontSize = 13.sp,
                color = if (isInteracting) accentColor else TextTertiary
            )
        }
    }
}
