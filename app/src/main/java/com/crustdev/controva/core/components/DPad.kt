package com.crustdev.controva.core.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.crustdev.controva.core.theme.DarkBorder
import com.crustdev.controva.core.theme.DarkBorderSubtle
import com.crustdev.controva.core.theme.DarkSurface
import com.crustdev.controva.core.theme.DarkSurfaceElevated
import com.crustdev.controva.core.theme.DarkSurfaceVariant
import com.crustdev.controva.core.theme.LocalAccentColor
import com.crustdev.controva.core.theme.TextPrimary
import com.crustdev.controva.core.theme.TextTertiary
import com.crustdev.controva.core.theme.softGlow

enum class DPadDirection {
    UP, DOWN, LEFT, RIGHT
}

@Composable
fun DPad(
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
    onDirectionChanged: (DPadDirection, Boolean) -> Unit
) {
    val buttonArmSize = size * 0.36f
    val centerHubSize = size * 0.28f

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        // Center Hub
        Box(
            modifier = Modifier
                .size(centerHubSize)
                .clip(CircleShape)
                .background(DarkSurfaceElevated)
                .border(1.dp, DarkBorderSubtle, CircleShape)
        )

        // UP Arm
        DPadArm(
            direction = DPadDirection.UP,
            icon = Icons.Default.ArrowDropUp,
            armSize = buttonArmSize,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 2.dp),
            onPressChanged = { pressed -> onDirectionChanged(DPadDirection.UP, pressed) }
        )

        // DOWN Arm
        DPadArm(
            direction = DPadDirection.DOWN,
            icon = Icons.Default.ArrowDropDown,
            armSize = buttonArmSize,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-2).dp),
            onPressChanged = { pressed -> onDirectionChanged(DPadDirection.DOWN, pressed) }
        )

        // LEFT Arm
        DPadArm(
            direction = DPadDirection.LEFT,
            icon = Icons.Default.ArrowLeft,
            armSize = buttonArmSize,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 2.dp),
            onPressChanged = { pressed -> onDirectionChanged(DPadDirection.LEFT, pressed) }
        )

        // RIGHT Arm
        DPadArm(
            direction = DPadDirection.RIGHT,
            icon = Icons.Default.ArrowRight,
            armSize = buttonArmSize,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-2).dp),
            onPressChanged = { pressed -> onDirectionChanged(DPadDirection.RIGHT, pressed) }
        )
    }
}

@Composable
private fun DPadArm(
    direction: DPadDirection,
    icon: ImageVector,
    armSize: Dp,
    modifier: Modifier = Modifier,
    onPressChanged: (Boolean) -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val accentColor = LocalAccentColor.current
    val view = LocalView.current

    val glowAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.5f else 0.0f,
        label = "dpad_glow"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        label = "dpad_scale"
    )

    val shape = when (direction) {
        DPadDirection.UP -> RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomEnd = 4.dp, bottomStart = 4.dp)
        DPadDirection.DOWN -> RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomEnd = 10.dp, bottomStart = 10.dp)
        DPadDirection.LEFT -> RoundedCornerShape(topStart = 10.dp, topEnd = 4.dp, bottomEnd = 4.dp, bottomStart = 10.dp)
        DPadDirection.RIGHT -> RoundedCornerShape(topStart = 4.dp, topEnd = 10.dp, bottomEnd = 10.dp, bottomStart = 4.dp)
    }


    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(armSize)
            .scale(scale)
            .softGlow(
                color = accentColor,
                alpha = glowAlpha,
                radius = 14.dp,
                borderRadius = 8.dp
            )
            .clip(shape)
            .background(if (isPressed) DarkSurfaceVariant else DarkSurface)
            .border(
                width = if (isPressed) 1.5.dp else 1.dp,
                color = if (isPressed) accentColor else DarkBorder,
                shape = shape
            )
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    onPressChanged(true)

                    val up = waitForUpOrCancellation()
                    isPressed = false
                    onPressChanged(false)
                }
            }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = direction.name,
            tint = if (isPressed) accentColor else TextPrimary,
            modifier = Modifier.size(24.dp)
        )
    }
}
