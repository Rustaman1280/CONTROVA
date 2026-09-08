package com.crustdev.controva.core.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
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
fun GlowButton(
    label: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    shape: Shape = CircleShape,
    isCircle: Boolean = true,
    symbolColor: Color? = null,
    onPressChanged: (Boolean) -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val accentColor = LocalAccentColor.current
    val view = LocalView.current

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        label = "btn_scale"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.6f else 0.0f,
        label = "btn_glow"
    )

    val backgroundColor = if (isPressed) DarkSurfaceVariant else DarkSurface
    val borderColor = if (isPressed) accentColor else DarkBorder
    val textColor = symbolColor ?: if (isPressed) accentColor else TextPrimary

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .scale(scale)
            .softGlow(
                color = accentColor,
                alpha = glowAlpha,
                radius = 18.dp,
                borderRadius = 14.dp,
                isCircle = isCircle
            )
            .border(
                width = if (isPressed) 1.8.dp else 1.dp,
                color = borderColor,
                shape = shape
            )
            .clip(shape)
            .background(backgroundColor)
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
        Text(
            text = label,
            fontSize = if (label.length > 2) 11.sp else 18.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
