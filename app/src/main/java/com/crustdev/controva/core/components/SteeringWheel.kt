package com.crustdev.controva.core.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crustdev.controva.core.theme.DarkBackground
import com.crustdev.controva.core.theme.DarkBorder
import com.crustdev.controva.core.theme.DarkBorderSubtle
import com.crustdev.controva.core.theme.DarkSurface
import com.crustdev.controva.core.theme.DarkSurfaceElevated
import com.crustdev.controva.core.theme.DarkSurfaceVariant
import com.crustdev.controva.core.theme.LocalAccentColor
import com.crustdev.controva.core.theme.TextPrimary
import com.crustdev.controva.core.theme.TextTertiary
import com.crustdev.controva.core.theme.softGlow
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.roundToInt

@Composable
fun SteeringWheel(
    modifier: Modifier = Modifier,
    size: Dp = 230.dp,
    isGyroMode: Boolean = false,
    gyroTurnNormalized: Float = 0f, // -1.0 .. 1.0 from sensor
    onSteer: (angleDeg: Float, normalized: Float) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val accentColor = LocalAccentColor.current

    val animAngle = remember { Animatable(0f) }
    var currentTouchAngle by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    // If Gyro Mode is active, track gyro tilt
    LaunchedEffect(isGyroMode, gyroTurnNormalized) {
        if (isGyroMode) {
            val gyroAngle = gyroTurnNormalized * 90f // -90 .. 90 deg tilt
            animAngle.snapTo(gyroAngle)
            onSteer(gyroAngle, gyroTurnNormalized)
        }
    }

    val displayAngle = if (isGyroMode) animAngle.value else currentTouchAngle

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .softGlow(
                color = accentColor,
                alpha = if (isDragging || isGyroMode) 0.35f else 0.08f,
                radius = 24.dp,
                isCircle = true
            )
            .pointerInput(isGyroMode) {
                if (isGyroMode) return@pointerInput

                val center = Offset(size.toPx() / 2f, size.toPx() / 2f)

                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    isDragging = true

                    var lastAngle = atan2(down.position.y - center.y, down.position.x - center.x)

                    do {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull()
                        if (change != null && change.pressed) {
                            val newAngle = atan2(change.position.y - center.y, change.position.x - center.x)
                            var delta = (newAngle - lastAngle) * (180f / PI.toFloat())

                            // Normalize delta to [-180..180]
                            if (delta > 180f) delta -= 360f
                            if (delta < -180f) delta += 360f

                            val targetAngle = (currentTouchAngle + delta).coerceIn(-180f, 180f)
                            currentTouchAngle = targetAngle
                            lastAngle = newAngle

                            val normalized = (targetAngle / 180f).coerceIn(-1.0f, 1.0f)
                            onSteer(targetAngle, normalized)
                            coroutineScope.launch { animAngle.snapTo(targetAngle) }
                            change.consume()
                        }
                    } while (event.changes.any { it.pressed })

                    // Released: spring back to center
                    isDragging = false
                    currentTouchAngle = 0f
                    onSteer(0f, 0f)
                    coroutineScope.launch {
                        animAngle.animateTo(0f, spring(dampingRatio = 0.65f, stiffness = 600f))
                    }
                }
            }
    ) {
        // Rotating Wheel Canvas
        Canvas(
            modifier = Modifier
                .size(size)
                .rotate(animAngle.value)
        ) {
            val c = size.toPx() / 2f
            val centerOffset = Offset(c, c)
            val outerRadius = c - 8.dp.toPx()
            val innerRadius = outerRadius - 26.dp.toPx()
            val rimStroke = 18.dp.toPx()

            // Outer Rim
            drawCircle(
                color = DarkSurfaceVariant,
                radius = outerRadius,
                center = centerOffset,
                style = Stroke(width = rimStroke)
            )

            // Inner Rim Outline
            drawCircle(
                color = DarkBorder,
                radius = innerRadius,
                center = centerOffset,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Center 12 o'clock accent stripe notch
            drawArc(
                color = accentColor,
                startAngle = -95f,
                sweepAngle = 10f,
                useCenter = false,
                topLeft = Offset(centerOffset.x - outerRadius, centerOffset.y - outerRadius),
                size = Size(outerRadius * 2, outerRadius * 2),
                style = Stroke(width = rimStroke, cap = StrokeCap.Round)
            )

            // Horizontal spokes (left, right, bottom)
            val spokeStroke = 12.dp.toPx()
            // Left spoke
            drawLine(
                color = DarkSurfaceElevated,
                start = Offset(c - 28.dp.toPx(), c),
                end = Offset(c - innerRadius + 2.dp.toPx(), c),
                strokeWidth = spokeStroke,
                cap = StrokeCap.Round
            )
            // Right spoke
            drawLine(
                color = DarkSurfaceElevated,
                start = Offset(c + 28.dp.toPx(), c),
                end = Offset(c + innerRadius - 2.dp.toPx(), c),
                strokeWidth = spokeStroke,
                cap = StrokeCap.Round
            )
            // Bottom vertical spoke
            drawLine(
                color = DarkSurfaceElevated,
                start = Offset(c, c + 28.dp.toPx()),
                end = Offset(c, c + innerRadius - 2.dp.toPx()),
                strokeWidth = spokeStroke,
                cap = StrokeCap.Round
            )

            // Center Hub circle
            drawCircle(
                color = DarkSurface,
                radius = 32.dp.toPx(),
                center = centerOffset
            )
            drawCircle(
                color = if (isDragging || isGyroMode) accentColor else DarkBorder,
                radius = 32.dp.toPx(),
                center = centerOffset,
                style = Stroke(width = 1.5.dp.toPx())
            )
        }

        // Center Angle & Mode Indicator
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(DarkBackground.copy(alpha = 0.85f))
        ) {
            Text(
                text = "${displayAngle.roundToInt()}°",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (displayAngle != 0f) accentColor else TextTertiary
            )
        }
    }
}
