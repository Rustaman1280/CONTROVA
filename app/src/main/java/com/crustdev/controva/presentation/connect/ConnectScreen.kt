package com.crustdev.controva.presentation.connect

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crustdev.controva.core.components.ConnectionStatusBadge
import com.crustdev.controva.core.components.GlowButton
import com.crustdev.controva.core.theme.ButtonShape
import com.crustdev.controva.core.theme.DarkBackground
import com.crustdev.controva.core.theme.DarkBorder
import com.crustdev.controva.core.theme.DarkSurface
import com.crustdev.controva.core.theme.DarkSurfaceElevated
import com.crustdev.controva.core.theme.DarkSurfaceVariant
import com.crustdev.controva.core.theme.LocalAccentColor
import com.crustdev.controva.core.theme.PillShape
import com.crustdev.controva.core.theme.TextPrimary
import com.crustdev.controva.core.theme.TextSecondary
import com.crustdev.controva.core.theme.TextTertiary
import com.crustdev.controva.core.theme.softGlow
import com.crustdev.controva.data.model.ConnectionState

@Composable
fun ConnectScreen(
    viewModel: ConnectViewModel,
    onNavigateToLayoutPicker: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val hostIp by viewModel.hostIp.collectAsState()
    val port by viewModel.port.collectAsState()
    val accentColor = LocalAccentColor.current

    var useUdp by remember { mutableStateOf(false) }

    val isConnected = connectionState is ConnectionState.Connected
    val isConnecting = connectionState is ConnectionState.Connecting

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp)
    ) {
        // Top Bar: Settings Shortcut
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ConnectionStatusBadge(connectionState = connectionState)

            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(DarkSurface)
                    .border(1.dp, DarkBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = TextSecondary
                )
            }
        }

        // Center Connect Card
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(DarkSurface)
                .border(1.dp, DarkBorder, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Wifi,
                contentDescription = "WiFi",
                tint = accentColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Connect to Companion Host",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Ensure your PC and phone are on the same WiFi network",
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            // IP Input
            OutlinedTextField(
                value = hostIp,
                onValueChange = { viewModel.onHostIpChanged(it) },
                label = { Text("Host IP Address", color = TextTertiary) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = accentColor
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Port Input
            OutlinedTextField(
                value = port,
                onValueChange = { viewModel.onPortChanged(it) },
                label = { Text("Port (default 8080)", color = TextTertiary) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = accentColor
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Protocol Toggle (WebSocket vs UDP)
            Row(
                modifier = Modifier
                    .clip(PillShape)
                    .background(DarkSurfaceVariant)
                    .border(1.dp, DarkBorder, PillShape)
                    .padding(3.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(PillShape)
                        .background(if (!useUdp) accentColor.copy(alpha = 0.2f) else DarkSurfaceVariant)
                        .border(1.dp, if (!useUdp) accentColor else DarkBorder, PillShape)
                        .clickable { useUdp = false }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "WebSocket (Reliable)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!useUdp) accentColor else TextSecondary
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(PillShape)
                        .background(if (useUdp) accentColor.copy(alpha = 0.2f) else DarkSurfaceVariant)
                        .border(1.dp, if (useUdp) accentColor else DarkBorder, PillShape)
                        .clickable { useUdp = true }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "UDP (Fastest)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (useUdp) accentColor else TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Connect / Disconnect Button
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isConnected) {
                    GlowButton(
                        label = "DISCONNECT",
                        size = 50.dp,
                        shape = ButtonShape,
                        isCircle = false,
                        modifier = Modifier.weight(1f),
                        onPressChanged = { if (it) viewModel.disconnect() }
                    )
                } else {
                    GlowButton(
                        label = if (isConnecting) "CONNECTING..." else "CONNECT",
                        size = 50.dp,
                        shape = ButtonShape,
                        isCircle = false,
                        modifier = Modifier.weight(1f),
                        onPressChanged = { if (it) viewModel.connect() }
                    )
                }
            }
        }

        // Bottom Action: Go to Layouts
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(ButtonShape)
                    .background(DarkSurfaceElevated)
                    .border(1.dp, if (isConnected) accentColor else DarkBorder, ButtonShape)
                    .clickable { onNavigateToLayoutPicker() }
                    .padding(vertical = 14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Gamepad,
                    contentDescription = "Gamepad",
                    tint = if (isConnected) accentColor else TextPrimary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = if (isConnected) "CHOOSE LAYOUT & PLAY" else "CHOOSE LAYOUT (OFFLINE PREVIEW)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isConnected) accentColor else TextPrimary
                )
            }
        }
    }
}
private val CircleShape = androidx.compose.foundation.shape.CircleShape
