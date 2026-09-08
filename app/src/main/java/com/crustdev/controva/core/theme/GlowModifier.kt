package com.crustdev.controva.core.theme

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Applies a refined, soft ambient glow around a composable.
 * Uses a blurred mask behind the component with subtle opacity to prevent harsh neon banding.
 */
fun Modifier.softGlow(
    color: Color = ElectricCyan,
    alpha: Float = 0.35f,
    radius: Dp = 16.dp,
    borderRadius: Dp = 16.dp,
    isCircle: Boolean = false
): Modifier = if (alpha <= 0.01f) this else this.drawBehind {
    val spreadPx = radius.toPx()
    val cornerPx = borderRadius.toPx()

    if (spreadPx <= 0) return@drawBehind

    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            asFrameworkPaint().apply {
                this.color = color.copy(alpha = alpha).toArgb()
                this.isAntiAlias = true
                this.maskFilter = BlurMaskFilter(spreadPx, BlurMaskFilter.Blur.NORMAL)
            }
        }

        if (isCircle) {
            val centerRadius = (size.minDimension / 2f)
            canvas.drawCircle(
                center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f),
                radius = centerRadius,
                paint = paint
            )
        } else {
            canvas.drawRoundRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height,
                radiusX = cornerPx,
                radiusY = cornerPx,
                paint = paint
            )
        }
    }
}
