package com.crustdev.controva.presentation.layoutpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crustdev.controva.core.theme.ButtonShape
import com.crustdev.controva.core.theme.DarkBackground
import com.crustdev.controva.core.theme.DarkBorder
import com.crustdev.controva.core.theme.DarkSurface
import com.crustdev.controva.core.theme.DarkSurfaceElevated
import com.crustdev.controva.core.theme.LocalAccentColor
import com.crustdev.controva.core.theme.TextPrimary
import com.crustdev.controva.core.theme.TextSecondary
import com.crustdev.controva.core.theme.softGlow
import com.crustdev.controva.data.model.ControllerType

@Composable
fun LayoutPickerScreen(
    initialLayout: ControllerType = ControllerType.PLAYSTATION,
    onSelectLayout: (ControllerType) -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedLayout by remember { mutableStateOf(initialLayout) }
    val accentColor = LocalAccentColor.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(DarkSurface)
                        .border(1.dp, DarkBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(
                        text = "SELECT CONTROLLER",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Choose your preferred layout for gaming",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Grid of Layouts
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(ControllerType.values()) { layoutType ->
                    val isSelected = selectedLayout == layoutType

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .softGlow(
                                color = accentColor,
                                alpha = if (isSelected) 0.35f else 0.0f,
                                radius = 20.dp,
                                borderRadius = 18.dp
                            )
                            .clip(RoundedCornerShape(18.dp))
                            .background(DarkSurface)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) accentColor else DarkBorder,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable { selectedLayout = layoutType }
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = layoutType.displayName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) accentColor else TextPrimary
                                )

                                if (isSelected) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(accentColor)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = DarkBackground,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = layoutType.subtitle,
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = TextSecondary,
                                modifier = Modifier.weight(1f)
                            )

                            // Visual Layout Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DarkSurfaceElevated)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = when (layoutType) {
                                        ControllerType.PLAYSTATION -> "△ ○ ✕ □ · DUAL STICK"
                                        ControllerType.XBOX -> "A B X Y · OFFSET STICK"
                                        ControllerType.NINTENDO -> "X Y A B · SWITCH"
                                        ControllerType.STEERING_WHEEL -> "WHEEL · PEDALS · GYRO"
                                    },
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) accentColor else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Launch Button
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
                    .softGlow(color = accentColor, alpha = 0.4f, radius = 20.dp, borderRadius = 14.dp)
                    .clip(ButtonShape)
                    .background(accentColor)
                    .clickable { onSelectLayout(selectedLayout) }
                    .padding(vertical = 15.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Launch",
                    tint = DarkBackground,
                    modifier = Modifier.padding(end = 6.dp)
                )
                Text(
                    text = "START PLAYING (${selectedLayout.displayName.uppercase()})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkBackground
                )
            }
        }
    }
}
