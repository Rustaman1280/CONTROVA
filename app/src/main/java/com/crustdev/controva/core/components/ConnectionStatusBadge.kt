package com.crustdev.controva.core.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crustdev.controva.core.theme.DarkBorder
import com.crustdev.controva.core.theme.DarkSurface
import com.crustdev.controva.core.theme.PillShape
import com.crustdev.controva.core.theme.StatusConnected
import com.crustdev.controva.core.theme.StatusConnecting
import com.crustdev.controva.core.theme.StatusDisconnected
import com.crustdev.controva.core.theme.TextPrimary
import com.crustdev.controva.core.theme.TextSecondary
import com.crustdev.controva.core.theme.softGlow
import com.crustdev.controva.data.model.ConnectionState

@Composable
fun ConnectionStatusBadge(
    connectionState: ConnectionState,
    gyroEnabled: Boolean = false,
    modifier: Modifier = Modifier
) {
    val statusColor by animateColorAsState(
        targetValue = when (connectionState) {
            is ConnectionState.Connected -> StatusConnected
            is ConnectionState.Connecting -> StatusConnecting
            is ConnectionState.Disconnected, is ConnectionState.Error -> StatusDisconnected
        },
        label = "status_color"
    )

    val isConnecting = connectionState is ConnectionState.Connecting

    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val labelText = when (connectionState) {
        is ConnectionState.Connected -> "${connectionState.host} · ${connectionState.pingMs}ms"
        is ConnectionState.Connecting -> "Connecting to ${connectionState.host}..."
        is ConnectionState.Disconnected -> "Disconnected"
        is ConnectionState.Error -> "Connection error"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(PillShape)
            .background(DarkSurface.copy(alpha = 0.85f))
            .border(1.dp, DarkBorder, PillShape)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // Glowing Status Dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .scale(if (isConnecting) pulseScale else 1.0f)
                .softGlow(
                    color = statusColor,
                    alpha = 0.6f,
                    radius = 8.dp,
                    isCircle = true
                )
                .clip(CircleShape)
                .background(statusColor)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = labelText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
        )

        if (gyroEnabled) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(PillShape)
                    .background(DarkBorder.copy(alpha = 0.5f))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "GYRO",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
    }
}
